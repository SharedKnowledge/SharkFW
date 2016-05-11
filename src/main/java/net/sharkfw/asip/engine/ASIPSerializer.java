package net.sharkfw.asip.engine;

import net.sharkfw.asip.ASIPInformation;
import net.sharkfw.asip.ASIPInterest;
import net.sharkfw.asip.ASIPKnowledge;
import net.sharkfw.asip.ASIPSpace;
import net.sharkfw.knowledgeBase.*;
import net.sharkfw.knowledgeBase.geom.SharkGeometry;
import net.sharkfw.knowledgeBase.geom.inmemory.InMemoSharkGeometry;
import net.sharkfw.knowledgeBase.inmemory.*;
import net.sharkfw.system.L;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * @author msc
 */
public class ASIPSerializer {

    private static final String CLASS = "ASIPSERIALIZER: ";

    public static final String CONTENT = "CONTENT";
    public static final String LOGICALSENDER = "LOGICALSENDER";
    public static final String SIGNED = "SIGNED";
    public static final String INTEREST = "INTEREST";
    public static final String KNOWLEDGE = "KNOWLEDGE";
    public static final String RAW = "RAW";

    public static JSONObject serializeExpose(ASIPMessage header, ASIPSpace interest)
            throws SharkKBException, JSONException {

        JSONObject object = serializeHeader(header);
        JSONObject content = new JSONObject();
        content.put(LOGICALSENDER, ""); // PeerSemanticTag from Content Sender.
        content.put(SIGNED, false); // If signed or not
        content.put(INTEREST, serializeInterest(interest));
        object.put(CONTENT, content);
        return object;
    }

    public static JSONObject serializeInsert(ASIPMessage header, ASIPKnowledge knowledge)
            throws JSONException, SharkKBException {

        JSONObject object = serializeHeader(header);
        JSONObject content = new JSONObject();
        content.put(LOGICALSENDER, ""); // PeerSemanticTag from Content Sender.
        content.put(SIGNED, false); // If signed or not
        content.put(KNOWLEDGE, serializeKnowledge(knowledge));
        object.put(CONTENT, content);

        return object;
    }

    public static JSONObject serializeRaw(ASIPMessage header, byte[] raw) throws SharkKBException {

        JSONObject object = serializeHeader(header);
        JSONObject content = new JSONObject();
        content.put(LOGICALSENDER, ""); // PeerSemanticTag from Content Sender.
        content.put(SIGNED, false); // If signed or not
        content.put(RAW, new String(raw, StandardCharsets.UTF_8));
        object.put(CONTENT, content);
        return object;
    }

    public static JSONObject serializeRaw(ASIPMessage header, InputStream raw) throws SharkKBException {

        JSONObject object = serializeHeader(header);
        JSONObject content = new JSONObject();
        content.put(LOGICALSENDER, ""); // PeerSemanticTag from Content Sender.
        content.put(SIGNED, false); // If signed or not
        try {
            String text = null;
            try (Scanner scanner = new Scanner(raw, StandardCharsets.UTF_8.name())) {
                text = scanner.useDelimiter("\\A").next();
            }
            content.put(RAW, text);
        } finally {
            try {
                raw.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        object.put(CONTENT, content);
        return object;
    }


    public static JSONObject serializeHeader(ASIPMessage header) throws JSONException, SharkKBException {
        return new JSONObject()
                .put(ASIPMessage.VERSION, header.getVersion())
                .put(ASIPMessage.FORMAT, header.getFormat())
                .put(ASIPMessage.ENCRYPTED, header.isEncrypted())
                .put(ASIPMessage.ENCRYPTEDSESSIONKEY, header.getEncryptedSessionKey())
                .put(ASIPMessage.SIGNED, header.isSigned())
                .put(ASIPMessage.TTL, header.getTtl())
                .put(ASIPMessage.COMMAND, header.getCommand())
//                .put(ASIPMessage.TOPIC, header.getTopic())
                .put(ASIPMessage.SENDER, (header.getSender() != null) ?
                        serializeTag(header.getSender()): "")
                .put(ASIPMessage.RECEIVERPEER, (header.getReceiverPeer() != null) ?
                        serializeTag(header.getReceiverPeer()) : "")
                .put(ASIPMessage.RECEIVERLOCATION, (header.getReceiverSpatial() != null) ?
                        serializeTag(header.getReceiverSpatial()) : "")
                .put(ASIPMessage.RECEIVERTIME, (header.getReceiverTime() != null) ?
                        serializeTag(header.getReceiverTime()) : "");
    }

    public static JSONObject serializeInterest(ASIPSpace space) throws SharkKBException, JSONException {
        return serializeASIPSpace(space);
    }

    public static JSONObject serializeKnowledge(ASIPKnowledge knowledge) throws SharkKBException {
        if (knowledge == null) return null;


        JSONObject object = new JSONObject();
        SharkVocabulary vocabulary = knowledge.getVocabulary();
        object.put(ASIPKnowledge.VOCABULARY, serializeVocabulary(vocabulary));

        ASIPInfoDataManager manager = new ASIPInfoDataManager(knowledge.informationSpaces());
        Iterator pointInformations = manager.getPointInformations();
        JSONArray pointInfoArray = new JSONArray();
        while (pointInformations.hasNext()) {
            JSONObject pointInfoJSON = new JSONObject();
            ASIPPointInformation pointInformation = (ASIPPointInformation) pointInformations.next();
            ASIPSpace space = pointInformation.getSpace();
            pointInfoJSON.put(ASIPPointInformation.ASIPSPACE, serializeASIPSpace(space));

            JSONArray infoMetaDataArray = new JSONArray();
            Iterator<ASIPInfoMetaData> infoIter = pointInformation.getInfoData();
            while (infoIter.hasNext()) {
                ASIPInfoMetaData infoMetaData = infoIter.next();
                JSONObject infoMetaDataJSON = new JSONObject();
                infoMetaDataJSON.put(ASIPInfoMetaData.NAME, infoMetaData.getName());
                infoMetaDataJSON.put(ASIPInfoMetaData.OFFSET, infoMetaData.getOffset());
                infoMetaDataJSON.put(ASIPInfoMetaData.LENGTH, infoMetaData.getLength());
                infoMetaDataArray.put(infoMetaDataJSON);
            }
            pointInfoJSON.put(ASIPPointInformation.INFOMETADATA, infoMetaDataArray);
            pointInfoArray.put(pointInfoJSON);
        }
        object.put(ASIPInfoDataManager.INFODATA, pointInfoArray);
        object.put(ASIPInfoDataManager.INFOCONTENT, manager.getInfoContent());

        return object;
    }

    public static JSONObject serializeVocabulary(SharkVocabulary vocabulary) throws SharkKBException {
        JSONObject jsonObject = new JSONObject();


        jsonObject.put(SharkVocabulary.TOPICS, serializeSTSet(vocabulary.getTopicSTSet()));
        jsonObject.put(SharkVocabulary.TYPES, serializeSTSet(vocabulary.getTypeSTSet()));
        jsonObject.put(SharkVocabulary.PEERS, serializeSTSet(vocabulary.getPeerSTSet()));
        jsonObject.put(SharkVocabulary.LOCATIONS, serializeSTSet(vocabulary.getSpatialSTSet()));
        jsonObject.put(SharkVocabulary.TIMES, serializeSTSet(vocabulary.getTimeSTSet()));

        return jsonObject;
    }

    public static JSONObject serializeTag(SemanticTag tag) throws JSONException, SharkKBException {

        JSONObject object = new JSONObject();

        object.put(SemanticTag.NAME, tag.getName());

        String[] sis = tag.getSI();
        JSONArray sisArray = new JSONArray();
        for (String si : sis) {
            sisArray.put(si);
        }
        object.put(SemanticTag.SI, sisArray);

        // pst
        if (tag instanceof PeerSemanticTag) {
            PeerSemanticTag pst = (PeerSemanticTag) tag;

            String[] addresses = pst.getAddresses();
            JSONArray addrArray = new JSONArray();
            for (String addr : addresses) {
                addrArray.put(addr);
            }
            object.put(PeerSemanticTag.ADDRESSES, addrArray);
        }

        // tst
        if (tag instanceof TimeSemanticTag) {
            TimeSemanticTag tst = (TimeSemanticTag) tag;
            object.put(TimeSemanticTag.FROM, tst.getFrom());
            object.put(TimeSemanticTag.DURATION, tst.getDuration());
        }

        // sst
        if (tag instanceof SpatialSemanticTag) {
            SpatialSemanticTag sst = (SpatialSemanticTag) tag;
            object.put(SpatialSemanticTag.GEOMETRY, sst.getGeometry());
        }

        object.put(PropertyHolder.PROPERTIES, serializeProperties(tag).toString());

        return object;
    }

    public static JSONObject serializeSTSet(STSet stset) throws SharkKBException, JSONException {

        //TODO Type of STSet?
        if (stset == null) {
            return null;
        }
        JSONObject jsonObject = new JSONObject();
        String type = "";
        if (stset instanceof PeerSTSet)
            type = STSet.PEERSTSET;
        else if (stset instanceof TimeSTSet)
            type = STSet.TIMESTSET;
        else if (stset instanceof SpatialSTSet)
            type = STSet.SPATIALSTSET;
        else if (stset instanceof STSet)
            type = STSet.ANYSTSET;

        jsonObject.put(STSet.TYPE, type);

        JSONArray set = new JSONArray();
        Enumeration<SemanticTag> tags = stset.tags();
        while (tags.hasMoreElements()) {
            set.put(ASIPSerializer.serializeTag(tags.nextElement()));
        }
        jsonObject.put(STSet.STSET, set);

        if (stset instanceof SemanticNet || stset instanceof Taxonomy) {
            if (stset.tags().hasMoreElements())
                jsonObject.put(STSet.RELATIONS, serializeRelations(stset.tags()));
        }
        return jsonObject;
    }

    public static JSONArray serializeProperties(SystemPropertyHolder target) throws SharkKBException {
        if (target == null) {
            return null;
        }

        Enumeration<String> propNamesEnum = target.propertyNames(false);
        if (propNamesEnum == null || !propNamesEnum.hasMoreElements()) {
            return new JSONArray();
        }
        JSONObject jsonObject = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        while (propNamesEnum.hasMoreElements()) {
            String name = propNamesEnum.nextElement();
            String value = target.getProperty(name);

            JSONObject property = new JSONObject();
            property.put(PropertyHolder.NAME, name);
            property.put(PropertyHolder.VALUE, value);
            jsonArray.put(property);
        }

//        jsonObject.put(PropertyHolder.PROPERTIES, jsonArray);

        return jsonArray;
    }

    public static JSONObject serializeRelations(Enumeration<SemanticTag> tagEnum) {


        if (tagEnum == null || !tagEnum.hasMoreElements()) {
            return null;
        }

        SemanticTag tag = tagEnum.nextElement();
        boolean semanticNet;

        if (tag instanceof SNSemanticTag) {
            semanticNet = true;
        } else if (tag instanceof TXSemanticTag) {
            semanticNet = false;
        } else {
            return null;
        }


        JSONArray predicates = new JSONArray();
        JSONArray subSuperTags = new JSONArray();
        JSONObject jsonObject = new JSONObject();

        if (semanticNet) {
            // Semantic Net
            do {
                SNSemanticTag snTag = (SNSemanticTag) tag;
                // get tag for next round
                tag = null;
                if (tagEnum.hasMoreElements())
                    tag = tagEnum.nextElement();

                String[] sSIs = snTag.getSI();
                if (sSIs != null) {
                    String sourceSI = sSIs[0];

                    Enumeration<String> pNameEnum = snTag.predicateNames();
                    if (pNameEnum != null) {
                        while (pNameEnum.hasMoreElements()) {
                            String predicateName = pNameEnum.nextElement();
                            Enumeration<SNSemanticTag> targetEnum =
                                    snTag.targetTags(predicateName);
                            if (targetEnum == null) {
                                continue;
                            }
                            while (targetEnum.hasMoreElements()) {
                                SNSemanticTag target = targetEnum.nextElement();
                                String[] tSIs = target.getSI();
                                if (tSIs == null) {
                                    continue;
                                }

                                String targetSI = tSIs[0];
                                JSONObject predicate = new JSONObject();

                                predicate.put(SemanticNet.NAME, predicateName);
                                predicate.put(SemanticNet.SOURCE, sourceSI);
                                predicate.put(SemanticNet.TARGET, targetSI);

                                predicates.put(predicate);
                            }
                        }
                    }
                }
            } while (tag != null);

            jsonObject.put(SemanticNet.PREDICATES, predicates);

        } else {
            // Taxonomy
            do {
                TXSemanticTag txTag = (TXSemanticTag) tag;
                // get tag for next round
                tag = null;
                if (tagEnum.hasMoreElements()) {
                    tag = tagEnum.nextElement();
                }

                String[] sSIs = txTag.getSI();
                if (sSIs != null) {
                    String sourceSI = sSIs[0];

                    TXSemanticTag superTag = txTag.getSuperTag();
                    if (superTag != null) {
                        String[] tSIs = superTag.getSI();
                        if (tSIs == null) {
                            continue;
                        }

                        String targetSI = tSIs[0];

                        JSONObject subSuperTag = new JSONObject();

                        subSuperTag.put(Taxonomy.SOURCE, sourceSI);
                        subSuperTag.put(Taxonomy.TARGET, targetSI);

                        subSuperTags.put(subSuperTag);
                    }
                }
            } while (tagEnum.hasMoreElements());

            jsonObject.put(Taxonomy.SUBSUPERTAGS, subSuperTags);

        }
        return jsonObject;
    }

    public static JSONObject serializeASIPSpace(ASIPSpace space) throws SharkKBException {
        if (space == null)
            return null;

        JSONObject jsonObject = new JSONObject();

        STSet topics = space.getTopics();
        if (topics != null && !topics.isEmpty()) {
            jsonObject.put(ASIPSpace.TOPICS, serializeSTSet(topics));
        }

        // types
        STSet types = space.getTypes();
        if (types != null && !types.isEmpty()) {
            jsonObject.put(ASIPSpace.TYPES, serializeSTSet(types));
        }

        // sender
        PeerSemanticTag sender = space.getSender();
        if (sender != null) {
            jsonObject.put(ASIPSpace.SENDER, serializeTag(sender));
        }

        // approvers
        PeerSTSet approvers = space.getApprovers();
        if (approvers != null && !approvers.isEmpty()) {
            jsonObject.put(ASIPSpace.APPROVERS, serializeSTSet(approvers));
        }

        // receivers
        PeerSTSet receivers = space.getReceivers();
        if (receivers != null && !receivers.isEmpty()) {
            jsonObject.put(ASIPSpace.RECEIVERS, serializeSTSet(receivers));
        }

        // locations
        SpatialSTSet locations = space.getLocations();
        if (locations != null && !locations.isEmpty()) {
            jsonObject.put(ASIPSpace.LOCATIONS, serializeSTSet(locations));
        }

        // times
        TimeSTSet times = space.getTimes();
        if (times != null && !times.isEmpty()) {
            jsonObject.put(ASIPSpace.TIMES, serializeSTSet(times));
        }

        // direction
        jsonObject.put(ASIPSpace.DIRECTION, space.getDirection());

        return jsonObject;
    }

    public static void deserializeInMessage(ASIPInMessage message, String parsedStream) {
        if (parsedStream.isEmpty()) {
            L.d(CLASS + "Stream is empty.");
            return;
        }

        JSONObject object = null;

        try {
            object = new JSONObject(parsedStream);
        } catch (Exception e) {
            L.d(CLASS + e);
        }


        String version = "";
        String format = "";
        boolean encrypted = false;
        String encryptedSessionKey = "";
        boolean signed = false;
        long ttl = -1;
        int command = -1;
        PeerSemanticTag sender = null;
        PeerSemanticTag receiverPeer = null;
        SpatialSemanticTag receiverLocation = null;
        TimeSemanticTag receiverTime = null;
        STSet receivers = null;
        String senderString = "";
        String receiverString = "";
        String receiverPeerString = "";
        String receiverLocationString = "";
        String receiverTimeString = "";


        if (object.has(ASIPMessage.VERSION))
            version = object.getString(ASIPMessage.VERSION);
        if (object.has(ASIPMessage.FORMAT))
            format = object.getString(ASIPMessage.FORMAT);
        if (object.has(ASIPMessage.ENCRYPTED))
            encrypted = object.getBoolean(ASIPMessage.ENCRYPTED);
        if (object.has(ASIPMessage.ENCRYPTEDSESSIONKEY))
            encryptedSessionKey = object.getString(ASIPMessage.ENCRYPTEDSESSIONKEY);
        if (object.has(ASIPMessage.SIGNED))
            signed = object.getBoolean(ASIPMessage.SIGNED);
        if (object.has(ASIPMessage.TTL))
            ttl = object.getLong(ASIPMessage.TTL);
        if (object.has(ASIPMessage.COMMAND))
            command = object.getInt(ASIPMessage.COMMAND);
        if (object.has(ASIPMessage.SENDER)) {
            senderString = object.get(ASIPMessage.SENDER).toString();
            try {
                sender = ASIPSerializer.deserializePeerTag(senderString);
            } catch (SharkKBException e) {
                e.printStackTrace();
            }
        }
        if (object.has(ASIPMessage.RECEIVERPEER)) {
            receiverPeerString = object.get(ASIPMessage.RECEIVERPEER).toString();
            try {
                receiverPeer = ASIPSerializer.deserializePeerTag(receiverPeerString);
            } catch (SharkKBException e) {
                e.printStackTrace();
            }
        }
        if (object.has(ASIPMessage.RECEIVERLOCATION)) {
            receiverLocationString = object.get(ASIPMessage.RECEIVERLOCATION).toString();
            try {
                receiverLocation = ASIPSerializer.deserializeSpatialTag(receiverLocationString);
            } catch (SharkKBException e) {
                e.printStackTrace();
            }
        }
        if (object.has(ASIPMessage.RECEIVERTIME)) {
            receiverTimeString = object.get(ASIPMessage.RECEIVERTIME).toString();
            try {
                receiverTime = ASIPSerializer.deserializeTimeTag(receiverTimeString);
            } catch (SharkKBException e) {
                e.printStackTrace();
            }
        }
        // TODO obsolete?
        if (object.has(ASIPMessage.RECEIVERS)) {
            receiverString = object.getString(ASIPMessage.RECEIVERS);
//            receivers = ASIPSerializer.deserializeAnySTSet(null, receiverString);
        }

        message.setEncrypted(encrypted);
        message.setEncryptedSessionKey(encryptedSessionKey);
        message.setSigned(signed);
        message.setTtl(ttl);
        message.setCommand(command);
        message.setSender(sender);
        message.setReceiverPeer(receiverPeer);
        message.setReceiverSpatial(receiverLocation);
        message.setReceiverTime(receiverTime);
        message.setReceivers(receivers);

//      TODO
//        try {
//            L.d(CLASS + receivers.size());
//        Iterator<SemanticTag> tags = receivers.stTags();
//            while(receivers.stTags().hasNext()) {
//                SemanticTag tag = receivers.stTags().next();
//                L.d(CLASS + tag.getClass());
//                if(tag instanceof PeerSemanticTag){
//                    message.setReceiverPeer((PeerSemanticTag) tag);
//                } else if (tag instanceof SpatialSemanticTag){
//                    message.setReceiverSpatial((SpatialSemanticTag) tag);
//                } else if(tag instanceof TimeSemanticTag){
//                    message.setReceiverTime((TimeSemanticTag) tag);
//                }
//            }
//        } catch (SharkKBException e) {
//            e.printStackTrace();
//        }

        JSONObject content = object.getJSONObject(ASIPSerializer.CONTENT);

        switch (command) {
            case ASIPMessage.ASIP_EXPOSE:
                try {
                    ASIPInterest interest = deserializeASIPInterest(content.get(ASIPSerializer.INTEREST).toString());
                    message.setInterest(interest);
                } catch (SharkKBException e) {
                    e.printStackTrace();
                }
                break;
            case ASIPMessage.ASIP_INSERT:
                try {
                    ASIPKnowledge knowledge = deserializeASIPKnowledge(content.get(ASIPSerializer.KNOWLEDGE).toString());
                    message.setKnowledge(knowledge);
                } catch (SharkKBException e) {
                    e.printStackTrace();
                }
                break;
            case ASIPMessage.ASIP_RAW:
                byte[] raw = content.getString(ASIPSerializer.RAW).getBytes(StandardCharsets.UTF_8);
                message.setRaw(new ByteArrayInputStream(raw));
                break;
        }
    }

    /**
     * TODO SharkInputStream as param
     * Deserializes knowledge and return a newly created knowledge object..
     *
     * @return
     * @throws SharkKBException
     */
    public static ASIPKnowledge deserializeASIPKnowledge(String string) throws SharkKBException {

        if (string == null) return null;

        JSONObject jsonObject = new JSONObject(string);

        JSONObject vocabularyJSON = jsonObject.getJSONObject(ASIPKnowledge.VOCABULARY);

        SemanticNet topics = InMemoSharkKB.createInMemoSemanticNet();
        deserializeSTSet(topics, vocabularyJSON.get(SharkVocabulary.TOPICS).toString());
        SemanticNet types = InMemoSharkKB.createInMemoSemanticNet();
        deserializeSTSet(types, vocabularyJSON.get(SharkVocabulary.TYPES).toString());
        PeerTaxonomy peers = InMemoSharkKB.createInMemoPeerTaxonomy();
        deserializePeerSTSet(null, vocabularyJSON.get(SharkVocabulary.PEERS).toString());
        SpatialSTSet locations = deserializeSpatialSTSet(null, vocabularyJSON.get(SharkVocabulary.LOCATIONS).toString());
        TimeSTSet times = deserializeTimeSTSet(null, vocabularyJSON.get(SharkVocabulary.TIMES).toString());

        // create knowledge which actuall IS a SharkKB
        InMemoSemanticNet stnet = new InMemoSemanticNet();
        Knowledge knowledge = new InMemoASIPKnowledge();
        SharkKB kb = new InMemoSharkKB(topics, types, peers, locations, times, knowledge);

//        byte[] infoContent = jsonObject.getJSONArray(ASIPInfoDataManager.INFOCONTENT).toString().getBytes();
        String contentString = jsonObject.getString(ASIPInfoDataManager.INFOCONTENT);

        JSONArray infoDataArray = jsonObject.getJSONArray(ASIPInfoDataManager.INFODATA);
        for (int i = 0; i < infoDataArray.length(); i++) {

            JSONObject infoObject = infoDataArray.getJSONObject(i);
            ASIPSpace space = ASIPSerializer.deserializeASIPInterest(infoObject.get(ASIPPointInformation.ASIPSPACE).toString());

            JSONArray infoMetaDataArray = infoObject.getJSONArray(ASIPPointInformation.INFOMETADATA);
            for (int k = 0; k < infoMetaDataArray.length(); k++) {

                JSONObject object = infoMetaDataArray.getJSONObject(k);

                int offset = object.getInt(ASIPInfoMetaData.OFFSET);
                int length = object.getInt(ASIPInfoMetaData.LENGTH);
//                byte[] buff = new byte[length];
                String buff = "";

//                System.arraycopy(contentString.getBytes(), offset, buff, 0, length);

                buff = contentString.substring(offset, length);

                ASIPInformation info = kb.addInformation(buff, space);
                // TODO Info setName poassible?
                if (object.has(ASIPInfoMetaData.NAME)) {
                    info.setName(object.getString(ASIPInfoMetaData.NAME));
                }
            }
        }
        if (infoDataArray.length() <= 0 && contentString.length() > 0) {
            kb.addInformation(contentString, null);
        }
//    }
        return kb;
    }

    public static SemanticTag deserializeTag(STSet targetSet, String tagString) throws SharkKBException {

        // TODO Types of SemanticTags?
        if (targetSet == null)
            targetSet = InMemoSharkKB.createInMemoSTSet();

        if (tagString == null || tagString.isEmpty())
            return null;

        JSONObject jsonObject = new JSONObject(tagString);

        List<String> list = new ArrayList<>();
        JSONArray semanticTagArray = jsonObject.getJSONArray(SemanticTag.SI);
        for (int i = 0; i < semanticTagArray.length(); i++) {
            list.add(semanticTagArray.get(i).toString());
        }

        String name = jsonObject.getString(SemanticTag.NAME);
        String[] sis = new String[list.size()];
        sis = list.toArray(sis);

        SemanticTag tag = targetSet.createSemanticTag(name, sis);
        deserializeProperties(tag, tagString);
        return tag;
    }

    /**
     * static variant - not to be used in protocol parser
     *
     * @param tag
     * @return
     * @throws SharkKBException
     */
    public static SemanticTag deserializeTag(String tag) throws SharkKBException {

        STSet stSet = InMemoSharkKB.createInMemoSTSet();
        return ASIPSerializer.deserializeTag(stSet, tag);
    }

    public static PeerSemanticTag deserializePeerTag(STSet targetSet, String tagString) throws SharkKBException {

        if (targetSet == null)
            targetSet = InMemoSharkKB.createInMemoPeerSTSet();

        if (tagString == null || tagString.isEmpty()) {
            L.d(CLASS + "tag equals null");
            return null;
        }

        JSONObject jsonObject = new JSONObject(tagString);

        List<String> list = new ArrayList<>();
        JSONArray semanticTagArray = jsonObject.getJSONArray(SemanticTag.SI);
        for (int i = 0; i < semanticTagArray.length(); i++) {
            list.add(semanticTagArray.get(i).toString());
        }

        List<String> addressesList = new ArrayList<>();
        JSONArray addressArray = jsonObject.getJSONArray(PeerSemanticTag.ADDRESSES);
        for (int i = 0; i < addressArray.length(); i++) {
            addressesList.add((String) addressArray.get(i));
        }

        String name = jsonObject.getString(SemanticTag.NAME);
        String[] sis = new String[list.size()];
        sis = list.toArray(sis);
        String[] addresses = new String[addressesList.size()];
        addresses = addressesList.toArray(addresses);

        PeerSemanticTag tag = ((PeerSTSet) targetSet).createPeerSemanticTag(name, sis, addresses);

        deserializeProperties(tag, tagString);

        return tag;
    }

    /**
     * static variant - not to be used in protocol parser
     *
     * @param tag
     * @return
     * @throws SharkKBException
     */
    public static PeerSemanticTag deserializePeerTag(String tag) throws SharkKBException {

        PeerSTSet stSet = InMemoSharkKB.createInMemoPeerSTSet();
        return ASIPSerializer.deserializePeerTag(stSet, tag);
    }

    public static SpatialSemanticTag deserializeSpatialTag(STSet targetSet, String tagString) throws SharkKBException {

        if (targetSet == null)
            targetSet = InMemoSharkKB.createInMemoSpatialSTSet();

        if (tagString == null || tagString.isEmpty())
            return null;

        JSONObject jsonObject = new JSONObject(tagString);

        List<String> list = new ArrayList<>();
        JSONArray semanticTagArray = jsonObject.getJSONArray(SemanticTag.SI);
        for (int i = 0; i < semanticTagArray.length(); i++) {
            list.add(semanticTagArray.get(i).toString());
        }

        List<String> geometriesList = new ArrayList<>();
        JSONArray geometriesArray = jsonObject.getJSONArray(SpatialSemanticTag.GEOMETRY);
        for (int i = 0; i < geometriesArray.length(); i++) {
            geometriesList.add(geometriesArray.get(i).toString());
        }

        String name = jsonObject.getString(SemanticTag.NAME);
        String[] sis = new String[geometriesList.size()];
        sis = geometriesList.toArray(sis);
        String[] geometries = new String[geometriesList.size()];
        geometries = geometriesList.toArray(geometries);

        SharkGeometry[] geoms = new SharkGeometry[geometries.length];
        for (int i = 0; i < geometries.length; i++) {
            geoms[i] = InMemoSharkGeometry.createGeomByEWKT(geometries[i]);
        }
        // TODO Geometries just adding the first geom
        SpatialSemanticTag tag = ((SpatialSTSet) targetSet).createSpatialSemanticTag(name, sis, geoms);
        deserializeProperties(tag, tagString);
        return tag;
    }

    /**
     * static variant - not to be used in protocol parser
     *
     * @param tag
     * @return
     * @throws SharkKBException
     */
    public static SpatialSemanticTag deserializeSpatialTag(String tag) throws SharkKBException {
        // there is no specific set - create one
        SpatialSTSet stSet = InMemoSharkKB.createInMemoSpatialSTSet();
        return ASIPSerializer.deserializeSpatialTag(stSet, tag);
    }

    public static TimeSemanticTag deserializeTimeTag(STSet targetSet, String tagString) throws SharkKBException {

        if (targetSet == null)
            targetSet = InMemoSharkKB.createInMemoTimeSTSet();

        if (tagString == null || tagString.isEmpty())
            return null;

        JSONObject jsonObject = new JSONObject(tagString);
        Long from = jsonObject.getLong(TimeSemanticTag.FROM);
        Long duration = jsonObject.getLong(TimeSemanticTag.DURATION);

        TimeSemanticTag tag = ((TimeSTSet) targetSet).createTimeSemanticTag(from, duration);
        return tag;
    }

    /**
     * static variant - not to be used in protocol parser
     *
     * @param tag
     * @return
     * @throws SharkKBException
     */
    public static TimeSemanticTag deserializeTimeTag(String tag) throws SharkKBException {
        // there is no specific set - create one
        TimeSTSet stSet = InMemoSharkKB.createInMemoTimeSTSet();
        return ASIPSerializer.deserializeTimeTag(stSet, tag);
    }

    public static STSet deserializeAnySTSet(STSet stSet, String stSetString) throws SharkKBException {
        // TODO useless???
        JSONObject jsonObject = new JSONObject(stSetString);
        String typeJSON = jsonObject.getString(STSet.TYPE);
        L.d(CLASS + typeJSON);
        STSet set = stSet;
        switch (typeJSON) {
            case STSet.PEERSTSET:
                return ASIPSerializer.deserializePeerSTSet(set, stSetString);
            case STSet.TIMESTSET:
                return ASIPSerializer.deserializeTimeSTSet(set, stSetString);
            case STSet.SPATIALSTSET:
                return ASIPSerializer.deserializeSpatialSTSet(set, stSetString);
            case STSet.ANYSTSET:
                if (set == null) set = InMemoSharkKB.createInMemoSTSet();
                return ASIPSerializer.deserializeSTSet(set, stSetString);
            default:
                break;
        }

        return null;
    }

    public static STSet deserializeSTSet(STSet target, String stSetString) throws SharkKBException {

        if (target == null) {
            target = InMemoSharkKB.createInMemoSTSet();
        }

        JSONObject jsonObject = new JSONObject(stSetString);

        JSONArray semanticTagsArray = jsonObject.getJSONArray(STSet.STSET);
        for (int i = 0; i < semanticTagsArray.length(); i++) {
            JSONObject tag = semanticTagsArray.getJSONObject(i);
            if (tag.has(PeerSemanticTag.ADDRESSES)) {
                deserializePeerTag(target, tag.toString());
            } else if (tag.has(SpatialSemanticTag.GEOMETRY)) {
                deserializeSpatialTag(target, tag.toString());
            } else if (tag.has(TimeSemanticTag.DURATION) || jsonObject.has(TimeSemanticTag.FROM)) {
                deserializeTimeTag(target, tag.toString());
            } else {
                deserializeTag(target, tag.toString());
            }
        }

        if (target instanceof SemanticNet) {
            deserializeRelations((SemanticNet) target, stSetString);
            return target;
        } else if (target instanceof PeerTaxonomy) {
            deserializeProperties((SystemPropertyHolder) target, stSetString);
        }

        return target;
    }

    public static PeerSTSet deserializePeerSTSet(STSet set, String stSetString) throws SharkKBException {
        if (set == null) set = InMemoSharkKB.createInMemoPeerSTSet();
        PeerSTSet peerSTSet = (PeerSTSet) set;
        return (PeerSTSet) ASIPSerializer.deserializeSTSet(peerSTSet, stSetString);
    }

    public static PeerTaxonomy deserializePeerTaxonomy(STSet set, String stSetString) {
        if (set == null) set = InMemoSharkKB.createInMemoPeerTaxonomy();
        PeerTaxonomy peerSTSet = (PeerTaxonomy) set;
        try {
            ASIPSerializer.deserializeSTSet(peerSTSet, stSetString);
        } catch (SharkKBException e) {
            e.printStackTrace();
        }
        return peerSTSet;
    }

    public static TimeSTSet deserializeTimeSTSet(STSet set, String stSetString) throws SharkKBException {
        if (set == null) set = InMemoSharkKB.createInMemoTimeSTSet();
        TimeSTSet timeSTSet = (TimeSTSet) set;
        return (TimeSTSet) ASIPSerializer.deserializeSTSet(timeSTSet, stSetString);
    }

    public static SpatialSTSet deserializeSpatialSTSet(STSet set, String stSetString) throws SharkKBException {
        if (set == null) set = InMemoSharkKB.createInMemoSpatialSTSet();
        SpatialSTSet spatialSTSet = (SpatialSTSet) set;
        return (SpatialSTSet) ASIPSerializer.deserializeSTSet(spatialSTSet, stSetString);
    }

    public static STSet deserializeSTSet(String stSetString) throws SharkKBException {
        // there is no specific set - create one
        STSet stSet = InMemoSharkKB.createInMemoSTSet();
        return ASIPSerializer.deserializeSTSet(stSet, stSetString);
    }

    public static void deserializeProperties(SystemPropertyHolder target, String properties) throws SharkKBException {

        if (target == null) {
            L.d(CLASS + "target == null");
            return;
        }
        if (properties == null) {
            L.d(CLASS + "properties == null");
            return;
        }

        JSONObject jsonObject = new JSONObject(properties);

        if (jsonObject.has(PropertyHolder.PROPERTIES) && !jsonObject.isNull(PropertyHolder.PROPERTIES)) {

            if (!(jsonObject.get(PropertyHolder.PROPERTIES) instanceof JSONArray)) return;

            JSONArray propertiesArray = jsonObject.getJSONArray(PropertyHolder.PROPERTIES);
            for (int i = 0; i < propertiesArray.length(); i++) {

                JSONObject property = propertiesArray.getJSONObject(i);
                String name = property.getString(PropertyHolder.NAME);
                String value = property.getString(PropertyHolder.VALUE);

                target.setProperty(name, value);
            }
        }

    }

    public static void deserializeRelations(Taxonomy target, String relations) {
        if (target == null) return;
        if (relations == null) return;
        JSONObject jsonObject = new JSONObject(relations);

        if (!jsonObject.has(Taxonomy.SUBSUPERTAGS)) return;
        JSONArray propertiesArray = jsonObject.getJSONArray(Taxonomy.SUBSUPERTAGS);
        for (int i = 0; i < propertiesArray.length(); i++) {
            JSONObject relation = propertiesArray.getJSONObject(i);
            String sourceSI = relation.getString(Taxonomy.SOURCE);
            String targetSI = relation.getString(Taxonomy.TARGET);
            try {
                TXSemanticTag sourceTag = (TXSemanticTag) target.getSemanticTag(sourceSI);
                if (sourceTag == null) continue;

                TXSemanticTag targetTag = (TXSemanticTag) target.getSemanticTag(targetSI);
                if (targetTag == null) continue;

                // set super tag
                sourceTag.move(targetTag);
            } catch (SharkKBException skbe) {
                // ignore and go ahead
            }
        }

    }

    public static void deserializeRelations(SemanticNet target, String relations) {
        if (target == null) return;
        if (relations == null) return;
        JSONObject jsonObject = new JSONObject(relations);

        if (!jsonObject.has(SemanticNet.PREDICATES)) return;
        JSONArray propertiesArray = jsonObject.getJSONArray(SemanticNet.PREDICATES);
        for (int i = 0; i < propertiesArray.length(); i++) {
            JSONObject relation = propertiesArray.getJSONObject(i);
            String sourceSI = relation.getString(SemanticNet.SOURCE);
            String targetSI = relation.getString(SemanticNet.TARGET);
            String predicateName = relation.getString(SemanticNet.NAME);
            try {
                SNSemanticTag sourceTag = (SNSemanticTag) target.getSemanticTag(sourceSI);
                if (sourceTag == null) continue;

                SNSemanticTag targetTag = (SNSemanticTag) target.getSemanticTag(targetSI);
                if (targetTag == null) continue;

                // set super tag
                sourceTag.setPredicate(predicateName, targetTag);
            } catch (SharkKBException skbe) {
                // ignore and go ahead
            }
        }


    }

    public static ASIPInterest deserializeASIPInterest(SharkKB kb, String spaceString) throws SharkKBException {

        String topicsString = null;
        String typesString = null;
        String senderString = null;
        String approversString = null;
        String receiverString = null;
        String locationsString = null;
        String timesString = null;
        int direction = -1;

        ASIPInterest interest = InMemoSharkKB.createInMemoASIPInterest();

        JSONObject parsed = new JSONObject(spaceString);

        try {
            if (parsed.has(ASIPSpace.TOPICS)) topicsString = parsed.get(ASIPSpace.TOPICS).toString();
            if (parsed.has(ASIPSpace.TYPES)) typesString = parsed.get(ASIPSpace.TYPES).toString();
            if (parsed.has(ASIPSpace.SENDER)) senderString = parsed.get(ASIPSpace.SENDER).toString();
            if (parsed.has(ASIPSpace.APPROVERS)) approversString = parsed.get(ASIPSpace.APPROVERS).toString();
            if (parsed.has(ASIPSpace.RECEIVERS)) receiverString = parsed.get(ASIPSpace.RECEIVERS).toString();
            if (parsed.has(ASIPSpace.LOCATIONS)) locationsString = parsed.get(ASIPSpace.LOCATIONS).toString();
            if (parsed.has(ASIPSpace.TIMES)) timesString = parsed.get(ASIPSpace.TIMES).toString();
            if (parsed.has(ASIPSpace.DIRECTION)) direction = parsed.getInt(ASIPSpace.DIRECTION);
        } catch (JSONException e) {
            L.d("" + e);
        }


        STSet topics;
        if (topicsString != null) {
            topics = deserializeSTSet(null, topicsString);
            interest.setTopics(topics);
        }
        STSet types;
        if (typesString != null) {
            types = deserializeSTSet(null, typesString);
            interest.setTypes(types);
        }
        PeerSTSet approvers;
        if (approversString != null) {
            approvers = deserializePeerSTSet(null, approversString);
            interest.setApprovers(approvers);
        }
        PeerSTSet receivers;
        if (receiverString != null) {
            receivers = deserializePeerSTSet(null, receiverString);
            interest.setReceivers(receivers);
        }
        SpatialSTSet locations;
        if (locationsString != null) {
            locations = deserializeSpatialSTSet(null, locationsString);
            interest.setLocations(locations);
        }
        TimeSTSet times;
        if (timesString != null) {
            times = deserializeTimeSTSet(null, timesString);
            interest.setTimes(times);
        }
        if (senderString != null) {
            PeerSemanticTag sender = deserializePeerTag(null, senderString);
            interest.setSender(sender);
        }
        if (direction != -1) interest.setDirection(direction);

        return interest;
    }

    public static ASIPInterest deserializeASIPInterest(String sharkCS) throws SharkKBException {
        InMemoSharkKB imkb = new InMemoSharkKB();
        return ASIPSerializer.deserializeASIPInterest(imkb, sharkCS);
    }

    private static SemanticNet cast2SN(STSet stset) throws SharkKBException {
        SemanticNet sn;
        try {
            sn = (SemanticNet) stset;
        } catch (ClassCastException e) {
            InMemoSTSet imset = null;

            try {
                imset = (InMemoSTSet) stset;
            } catch (ClassCastException cce) {
                throw new SharkKBException("sorry, this implementation works with in memo shark kb implementation only");
            }

            InMemoGenericTagStorage tagStorage = imset.getTagStorage();
            sn = new InMemoSemanticNet(tagStorage);
        }

        return sn;
    }
}
