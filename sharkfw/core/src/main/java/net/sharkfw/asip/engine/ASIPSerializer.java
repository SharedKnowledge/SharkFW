package net.sharkfw.asip.engine;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;

import net.sharkfw.asip.*;
import net.sharkfw.knowledgeBase.PeerSTSet;
import net.sharkfw.knowledgeBase.PeerSemanticTag;
import net.sharkfw.knowledgeBase.PeerTaxonomy;
import net.sharkfw.knowledgeBase.PropertyHolder;
import net.sharkfw.knowledgeBase.SNSemanticTag;
import net.sharkfw.knowledgeBase.STSet;
import net.sharkfw.knowledgeBase.SemanticNet;
import net.sharkfw.knowledgeBase.SemanticTag;
import net.sharkfw.knowledgeBase.SharkKB;
import net.sharkfw.knowledgeBase.SharkKBException;
import net.sharkfw.knowledgeBase.SharkVocabulary;
import net.sharkfw.knowledgeBase.SpatialSTSet;
import net.sharkfw.knowledgeBase.SystemPropertyHolder;
import net.sharkfw.knowledgeBase.TimeSTSet;
import net.sharkfw.knowledgeBase.TimeSemanticTag;
import net.sharkfw.knowledgeBase.inmemory.*;
import net.sharkfw.system.L;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import net.sharkfw.knowledgeBase.SpatialSemanticTag;
import net.sharkfw.knowledgeBase.TXSemanticTag;
import net.sharkfw.knowledgeBase.Taxonomy;
import net.sharkfw.knowledgeBase.geom.SharkGeometry;
import net.sharkfw.knowledgeBase.geom.inmemory.InMemoSharkGeometry;

/**
 *
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
        content.put(INTEREST, serializeInterest(interest).toString());
        object.put(CONTENT, content);
        return object;
    }

    public static JSONObject serializeInsert(ASIPMessage header, ASIPKnowledge knowledge)
            throws JSONException, SharkKBException{

        JSONObject object = serializeHeader(header);
        JSONObject content = new JSONObject();
        content.put(LOGICALSENDER, ""); // PeerSemanticTag from Content Sender.
        content.put(SIGNED, false); // If signed or not
        content.put(KNOWLEDGE, serializeKnowledge(knowledge).toString());
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

    public static JSONObject serializeHeader(ASIPMessage header) throws JSONException, SharkKBException {
        return new JSONObject()
            .put(ASIPMessage.VERSION, header.getVersion())
            .put(ASIPMessage.FORMAT, header.getFormat())
            .put(ASIPMessage.ENCRYPTED, header.isEncrypted())
            .put(ASIPMessage.ENCRYPTEDSESSIONKEY, header.getEncryptedSessionKey())
            .put(ASIPMessage.SIGNED, header.isSigned())
            .put(ASIPMessage.TTL, header.getTtl())
            .put(ASIPMessage.COMMAND, header.getCommand())
            .put(ASIPMessage.SENDER, (header.getSender()!=null) ?
                    serializeTag(header.getSender()).toString() : "")
//            .put(ASIPMessage.RECEIVERS, serializeSTSet(header.getReceivers()).toString());
            .put(ASIPMessage.RECEIVERPEER, (header.getReceiverPeer()!= null) ?
                    serializeTag(header.getReceiverPeer()).toString() : "")
            .put(ASIPMessage.RECEIVERLOCATION, (header.getReceiverSpatial()!=null) ?
                    serializeTag(header.getReceiverSpatial()).toString() : "")
            .put(ASIPMessage.RECEIVERTIME, (header.getReceiverTime()!=null) ?
                    serializeTag(header.getReceiverTime()).toString() : "");
//            .put(ASIPMessage.SIGNATURE, header.getSignature());
    }

    public static JSONObject serializeInterest(ASIPSpace space) throws SharkKBException, JSONException {
        return serializeASIPSpace(space);
    }
    
    public static JSONObject serializeKnowledge(ASIPKnowledge knowledge) throws SharkKBException{
        if(knowledge==null) return null;
        
        JSONObject object = new JSONObject();
        SharkVocabulary vocabulary = knowledge.getVocabulary();
        
        object.put(ASIPKnowledge.VOCABULARY, serializeVocabulary(vocabulary).toString());
        
        ASIPInfoDataManager manager = new ASIPInfoDataManager(knowledge.informationSpaces());
        Iterator pointInformations = manager.getPointInfromations();
        JSONArray pointInfoArray = new JSONArray();
        while(pointInformations.hasNext()){
            JSONObject pointInfoJSON = new JSONObject();
            ASIPPointInformation pointInformation = (ASIPPointInformation) pointInformations.next();
            ASIPSpace space = pointInformation.getSpace();
            pointInfoJSON.put(ASIPPointInformation.ASIPSPACE, serializeASIPSpace(space).toString());
            
            JSONArray infoMetaDataArray = new JSONArray();
            while(pointInformation.getInfoData().hasNext()){
                ASIPInfoMetaData infoMetaData = pointInformation.getInfoData().next();
                JSONObject infoMetaDataJSON = new JSONObject();
                infoMetaDataJSON.put(ASIPInfoMetaData.NAME, infoMetaData.getName());
                infoMetaDataJSON.put(ASIPInfoMetaData.OFFSET, infoMetaData.getOffset());
                infoMetaDataJSON.put(ASIPInfoMetaData.LENGTH, infoMetaData.getLength());
                infoMetaDataArray.put(infoMetaDataJSON);
            }
            pointInfoJSON.put(ASIPPointInformation.INFOMETADATA, infoMetaDataArray);
        }
        object.put(ASIPInfoDataManager.INFODATA, pointInfoArray);
        object.put(ASIPInfoDataManager.INFOCONTENT, manager.getInfoContent());
        
        return object;
    }
    
    public static JSONObject serializeVocabulary(SharkVocabulary vocabulary) throws SharkKBException{ 
        JSONObject jsonObject = new JSONObject();
        
        jsonObject.put(SharkVocabulary.TOPICS, serializeSTSet(vocabulary.getTopicSTSet()).toString());
        jsonObject.put(SharkVocabulary.TYPES, serializeSTSet(vocabulary.getTypeSTSet()).toString());
        jsonObject.put(SharkVocabulary.PEERS, serializeSTSet(vocabulary.getPeerSTSet()).toString());
        jsonObject.put(SharkVocabulary.LOCATIONS, serializeSTSet(vocabulary.getSpatialSTSet()).toString());
        jsonObject.put(SharkVocabulary.TIMES, serializeSTSet(vocabulary.getTimeSTSet()).toString());
        
        return jsonObject;
    }
    
    public static JSONObject serializeTag(SemanticTag tag) throws JSONException, SharkKBException {
        
        JSONObject object = new JSONObject();
        
        object.put(SemanticTag.NAME, tag.getName());
        
        String[] sis = tag.getSI();
        JSONArray sisArray = new JSONArray();
        for(String si : sis){
            sisArray.put(si);
        }
        object.put(SemanticTag.SI, sisArray);
        
        // pst
        if(tag instanceof PeerSemanticTag) {
            PeerSemanticTag pst = (PeerSemanticTag) tag;
            
            String[] addresses = pst.getAddresses();
            JSONArray addrArray = new JSONArray();
            for(String addr : addresses){
                addrArray.put(addr);
            }
            object.put(PeerSemanticTag.ADDRESSES, addrArray);
        }

        // tst
        if(tag instanceof TimeSemanticTag) {
            TimeSemanticTag tst = (TimeSemanticTag) tag;
            object.put(TimeSemanticTag.FROM, tst.getFrom());
            object.put(TimeSemanticTag.DURATION, tst.getDuration());
        }
        
        // sst
        if(tag instanceof SpatialSemanticTag) {
            SpatialSemanticTag sst = (SpatialSemanticTag) tag;
            object.put(SpatialSemanticTag.GEOMETRY, sst.getGeometry());
        }
        
        object.put(PropertyHolder.PROPERTIES, serializeProperties(tag).toString());
        
        return object;
    }
    
    public static JSONObject serializeSTSet(STSet stset) throws SharkKBException, JSONException {
        
        //TODO Type of STSet?
        if(stset == null){
            return null;
        }
        
        JSONObject jsonObject = new JSONObject();
        String type = "";
        if(stset instanceof PeerSTSet)
            type = STSet.PEERSTSET;
        else if(stset instanceof TimeSTSet)
            type = STSet.TIMESTSET;
        else if(stset instanceof SpatialSTSet)
            type = STSet.SPATIALSTSET;
        else if(stset instanceof STSet)
            type = STSet.ANYSTSET;
        
        jsonObject.put(STSet.TYPE, type);
        
        JSONArray set = new JSONArray();
        Enumeration<SemanticTag> tags = stset.tags();
        while(tags.hasMoreElements()) {
            set.put(ASIPSerializer.serializeTag(tags.nextElement()));
        }
        
        jsonObject.put(STSet.STSET, set);
        
        Enumeration<SemanticTag> tagEnum = stset.tags();
        if(stset instanceof SemanticNet || stset instanceof Taxonomy) {
            jsonObject.put(STSet.RELATIONS, serializeRelations(tagEnum).toString());
        }
        
        return jsonObject;
    }
    
    public static JSONObject serializeProperties(SystemPropertyHolder target) throws SharkKBException{
        if(target == null) {
            return null;
        }
        
        Enumeration<String> propNamesEnum = target.propertyNames(false);
        if(propNamesEnum == null || !propNamesEnum.hasMoreElements()) {
            return new JSONObject();
        }
        JSONObject jsonObject = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        while(propNamesEnum.hasMoreElements()){
            String name = propNamesEnum.nextElement();
            String value = target.getProperty(name);
            
            JSONObject property = new JSONObject();
            property.put(PropertyHolder.NAME, name);
            property.put(PropertyHolder.VALUE, value);
            jsonArray.put(property);
        }
        
        jsonObject.put(PropertyHolder.PROPERTIES, jsonArray);
        
        return jsonObject;
    }
    
    public static JSONObject serializeRelations(Enumeration<SemanticTag> tagEnum){
        
        if(tagEnum == null || !tagEnum.hasMoreElements())
            return null;

        SemanticTag tag = tagEnum.nextElement();
        boolean semanticNet;
        
        if(tag instanceof SNSemanticTag) {
            semanticNet = true;
        } else if(tag instanceof TXSemanticTag) {
            semanticNet = false;
        } else
            return null;
        
        JSONArray predicates = new JSONArray();
        JSONArray subSuperTags = new JSONArray();
        JSONObject jsonObject = new JSONObject();
        
        if(semanticNet) {
            // Semantic Net
            do {
                SNSemanticTag snTag = (SNSemanticTag) tag;
                // get tag for next round
                tag = null;
                if(tagEnum.hasMoreElements())
                    tag = tagEnum.nextElement();
                
                String[] sSIs = snTag.getSI();
                if(sSIs != null) {
                    String sourceSI = sSIs[0];
                    
                    Enumeration<String> pNameEnum = snTag.predicateNames();
                    if(pNameEnum != null) {
                        while(pNameEnum.hasMoreElements()) {
                            String predicateName = pNameEnum.nextElement();                            
                            Enumeration<SNSemanticTag> targetEnum = 
                                    snTag.targetTags(predicateName);
                            if(targetEnum == null) {
                                continue;
                            }
                            while(targetEnum.hasMoreElements()) {
                                SNSemanticTag target = targetEnum.nextElement();
                                String[] tSIs = target.getSI();
                                if(tSIs == null) {
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
            } while(tag!=null);
            
            jsonObject.put(SemanticNet.PREDICATES, predicates);
            
        } else {
            // Taxonomy
            do {
                TXSemanticTag txTag = (TXSemanticTag) tag;
                // get tag for next round
                tag = null;
                if(tagEnum.hasMoreElements()) {
                    tag = tagEnum.nextElement();
                }
                
                String[] sSIs = txTag.getSI();
                if(sSIs != null) {
                    String sourceSI = sSIs[0];
                    
                    TXSemanticTag superTag = txTag.getSuperTag();
                    if(superTag != null) {
                        String[] tSIs = superTag.getSI();
                        if(tSIs == null) {
                            continue;
                        }

                        String targetSI = tSIs[0];
                        
                        JSONObject subSuperTag = new JSONObject();

                        subSuperTag.put(Taxonomy.SOURCE, sourceSI);
                        subSuperTag.put(Taxonomy.TARGET, targetSI);

                        subSuperTags.put(subSuperTag);
                    }
                }
            } while(tagEnum.hasMoreElements());
            
            jsonObject.put(Taxonomy.SUBSUPERTAGS, subSuperTags);
            
        }
        return jsonObject;
    }
        
    public static JSONObject serializeASIPSpace(ASIPSpace space) throws SharkKBException {
        if(space == null)
            return null;
        
        JSONObject jsonObject = new JSONObject();

        STSet topics = space.getTopics();
        if(topics != null && !topics.isEmpty()) {
            jsonObject.put(ASIPSpace.TOPICS, serializeSTSet(topics).toString());
        }

        // types
        STSet types = space.getTypes();
        if(types != null && !types.isEmpty()) {
            jsonObject.put(ASIPSpace.TYPES, serializeSTSet(types).toString());
        }

        // sender
        PeerSemanticTag sender = space.getSender();
        if(sender != null) {
            jsonObject.put(ASIPSpace.SENDER, serializeTag(sender).toString());
        }
        
        // approvers
        PeerSTSet approvers = space.getApprovers();
        if(approvers != null && !approvers.isEmpty()) {
            jsonObject.put(ASIPSpace.APPROVERS, serializeSTSet(approvers).toString());
        }

        // receivers
        PeerSTSet receivers = space.getReceivers();
        if(receivers != null && !receivers.isEmpty()) {
            jsonObject.put(ASIPSpace.RECEIVERS, serializeSTSet(receivers).toString());
        }
        
        // locations
        SpatialSTSet locations = space.getLocations();
        if(locations != null && !locations.isEmpty()) {
            jsonObject.put(ASIPSpace.LOCATIONS, serializeSTSet(locations).toString());
        }

        // times
        TimeSTSet times = space.getTimes();
        if(times != null && !times.isEmpty()) {
            jsonObject.put(ASIPSpace.TIMES, serializeSTSet(times).toString());
        }

        // direction
        jsonObject.put(ASIPSpace.DIRECTION, space.getDirection());
        
        return jsonObject;
    }

    public static void deserializeInMessage(ASIPInMessage message, String parsedStream){
        if(parsedStream.isEmpty()){
            L.d(CLASS + "Stream is empty.");
            return;
        }

        JSONObject object = null;

        try{
            object = new JSONObject(parsedStream);
        } catch(Exception e){
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


        if(object.has(ASIPMessage.VERSION))
            version = object.getString(ASIPMessage.VERSION);
        if(object.has(ASIPMessage.FORMAT))
            format = object.getString(ASIPMessage.FORMAT);
        if(object.has(ASIPMessage.ENCRYPTED))
            encrypted = object.getBoolean(ASIPMessage.ENCRYPTED);
        if(object.has(ASIPMessage.ENCRYPTEDSESSIONKEY))
            encryptedSessionKey = object.getString(ASIPMessage.ENCRYPTEDSESSIONKEY);
        if(object.has(ASIPMessage.SIGNED))
            signed = object.getBoolean(ASIPMessage.SIGNED);
        if(object.has(ASIPMessage.TTL))
            ttl = object.getLong(ASIPMessage.TTL);
        if(object.has(ASIPMessage.COMMAND))
            command = object.getInt(ASIPMessage.COMMAND);
        L.d(CLASS + version + " " +
                format + " " +
                encrypted + " " +
                encryptedSessionKey + " " +
                signed + " " +
                ttl + " " +
                command
        );
        if(object.has(ASIPMessage.SENDER)){
            senderString = object.getString(ASIPMessage.SENDER);
            try {
                sender = ASIPSerializer.deserializePeerTag(senderString);
            } catch (SharkKBException e) {
                e.printStackTrace();
            }
        }
        if(object.has(ASIPMessage.RECEIVERPEER)){
            receiverPeerString = object.getString(ASIPMessage.RECEIVERPEER);
            try {
                receiverPeer = ASIPSerializer.deserializePeerTag(receiverPeerString);
            } catch (SharkKBException e) {
                e.printStackTrace();
            }
        }
        if(object.has(ASIPMessage.RECEIVERLOCATION)){
            receiverLocationString = object.getString(ASIPMessage.RECEIVERLOCATION);
            try {
                receiverLocation = ASIPSerializer.deserializeSpatialTag(receiverLocationString);
            } catch (SharkKBException e) {
                e.printStackTrace();
            }
        }
        if(object.has(ASIPMessage.RECEIVERTIME)){
            receiverTimeString = object.getString(ASIPMessage.RECEIVERTIME);
            try {
                receiverTime = ASIPSerializer.deserializeTimeTag(receiverTimeString);
            } catch (SharkKBException e) {
                e.printStackTrace();
            }
        }
        // TODO obsolete?
        if(object.has(ASIPMessage.RECEIVERS)) {
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

        switch (command){
            case ASIPMessage.ASIP_EXPOSE:
                try {
                    ASIPInterest interest = deserializeASIPInterest(content.getString(ASIPSerializer.INTEREST));
                    message.setInterest(interest);
                } catch (SharkKBException e) {
                    e.printStackTrace();
                }
                break;
            case ASIPMessage.ASIP_INSERT:
                try {
                    ASIPKnowledge knowledge = deserializeASIPKnowledge(content.getString(ASIPSerializer.KNOWLEDGE));
                    message.setKnowledge(knowledge);
                } catch (SharkKBException e) {
                    e.printStackTrace();
                }
                break;
            case ASIPMessage.ASIP_RAW:
                byte[] raw = content.getString(ASIPSerializer.RAW).getBytes(StandardCharsets.UTF_8);
                message.setRaw(raw);
                break;
        }
    }

    /**
     * TODO SharkInputStream as param
     * Deserializes knowledge and return a newly created knowledge object..
     * @return
     * @throws SharkKBException 
     */
    public static ASIPKnowledge deserializeASIPKnowledge(String string) throws SharkKBException {
        
        if(string==null) return null;
        
        JSONObject jsonObject = new JSONObject(string);
        
        JSONObject vocabularyJSON = jsonObject.getJSONObject(ASIPKnowledge.VOCABULARY);
        
        STSet topics = deserializeSTSet(vocabularyJSON.getString(SharkVocabulary.TOPICS));
        STSet types = deserializeSTSet(vocabularyJSON.getString(SharkVocabulary.TYPES));
        PeerSTSet peers = deserializePeerSTSet(null, vocabularyJSON.getString(SharkVocabulary.PEERS));
        SpatialSTSet locations = deserializeSpatialSTSet(null, vocabularyJSON.getString(SharkVocabulary.LOCATIONS));
        TimeSTSet times = deserializeTimeSTSet(null, vocabularyJSON.getString(SharkVocabulary.TIMES));

        // TODO Cast do SN and Taxonomy ?!
        
        // create knowledge which actuall IS a SharkKB
        SharkKB kb = new InMemoSharkKB((SemanticNet) topics, (SemanticNet) types, (PeerTaxonomy) peers, locations, times);

        byte[] infoContent = (byte[]) jsonObject.get(ASIPInfoDataManager.INFOCONTENT);

        Iterator infoPointIterator;
        try{
            infoPointIterator = jsonObject.getJSONArray(ASIPInfoDataManager.INFODATA).iterator();
        } catch (JSONException e){
            return null;
        }
        while(infoPointIterator.hasNext()){
            JSONObject infoObject = (JSONObject) infoPointIterator.next();
            ASIPSpace space = ASIPSerializer.deserializeASIPInterest(infoObject.getString(ASIPPointInformation.ASIPSPACE));

            Iterator infoDataIterator;
            try{
                infoDataIterator = jsonObject.getJSONArray(ASIPPointInformation.INFOMETADATA).iterator();
            } catch (JSONException e){
                return null;
            }
            while(infoDataIterator.hasNext()){
                JSONObject object = (JSONObject) infoDataIterator.next();

                int offset = object.getInt(ASIPInfoMetaData.OFFSET);
                int length = object.getInt(ASIPInfoMetaData.LENGTH);
                byte[] buff = new byte[length];

                System.arraycopy(infoContent, offset, buff, 0, length);

                ASIPInformation info = kb.addInformation(buff, space);
                info.setName(object.getString(ASIPInfoMetaData.NAME));
            }
        }
        return kb;
    }
    
    public static SemanticTag deserializeTag(STSet targetSet, String tagString) throws SharkKBException {

        // TODO Types of SemanticTags?
        if(targetSet == null)
            targetSet = InMemoSharkKB.createInMemoSTSet();
        
        if(tagString == null || tagString.isEmpty())
            return null;
        
        JSONObject jsonObject = new JSONObject(tagString);
        Iterator siIterator;
        try{
            siIterator = jsonObject.getJSONArray(SemanticTag.SI).iterator();
        } catch (JSONException e){
            return null;
        }

        List<String> list = new ArrayList<>();
        while(siIterator.hasNext()){
            list.add((String) siIterator.next());
        }
        
        String name = jsonObject.getString(SemanticTag.NAME);
        String[] sis =  new String[list.size()];
        sis = list.toArray(sis);
        
        SemanticTag tag = targetSet.createSemanticTag(name, sis);
        deserializeProperties(tag, tagString);
        return tag;
    }
    
    /**
     * static variant - not to be used in protocol parser
     * @param tag
     * @return
     * @throws SharkKBException 
     */
    public static SemanticTag deserializeTag(String tag) throws SharkKBException {

        STSet stSet = InMemoSharkKB.createInMemoSTSet();
        return ASIPSerializer.deserializeTag(stSet, tag);
    }
    
    public static PeerSemanticTag deserializePeerTag(STSet targetSet, String tagString) throws SharkKBException {
        
        if(targetSet == null)
            targetSet = InMemoSharkKB.createInMemoPeerSTSet();
        
        if(tagString == null || tagString.isEmpty()){
            L.d(CLASS + "tag equals null");
            return null;
        }

        JSONObject jsonObject = new JSONObject(tagString);
        Iterator siIterator;
        try{
            siIterator = jsonObject.getJSONArray(SemanticTag.SI).iterator();
        } catch (JSONException e){
            return null;
        }
        Iterator addressIterator;
        try{
            addressIterator = jsonObject.getJSONArray(PeerSemanticTag.ADDRESSES).iterator();
        } catch (JSONException e){
            return null;
        }

        List<String> list = new ArrayList<>();
        while(siIterator.hasNext()){
            list.add((String) siIterator.next());
        }
        
        List<String> addressesList = new ArrayList<>();
        while(addressIterator.hasNext()){
            addressesList.add((String) addressIterator.next());
        }
        
        String name = jsonObject.getString(SemanticTag.NAME);
        String[] sis =  new String[list.size()];
        sis = list.toArray(sis);
        String[] addresses = new String[addressesList.size()];
        addresses = addressesList.toArray(addresses);

        PeerSemanticTag tag = ((PeerSTSet) targetSet).createPeerSemanticTag(name, sis, addresses);

        deserializeProperties(tag, tagString);

        return tag;
    }
    
    /**
     * static variant - not to be used in protocol parser
     * @param tag
     * @return
     * @throws SharkKBException 
     */
    public static PeerSemanticTag deserializePeerTag(String tag) throws SharkKBException {

        PeerSTSet stSet = InMemoSharkKB.createInMemoPeerSTSet();
        return ASIPSerializer.deserializePeerTag(stSet, tag);
    }
    
    public static SpatialSemanticTag deserializeSpatialTag(STSet targetSet, String tagString) throws SharkKBException {
        
        if(targetSet == null)
            targetSet = InMemoSharkKB.createInMemoSpatialSTSet();
        
        if(tagString == null || tagString.isEmpty())
            return null;
        
        JSONObject jsonObject = new JSONObject(tagString);
        Iterator siIterator;
        try{
            siIterator = jsonObject.getJSONArray(SemanticTag.SI).iterator();
        } catch (JSONException e){
            return null;
        }
        Iterator geometryIterator;
        try{
            geometryIterator = jsonObject.getJSONArray(SpatialSemanticTag.GEOMETRY).iterator();
        } catch (JSONException e){
            return null;
        }

        List<String> list = new ArrayList<>();
        while(siIterator.hasNext()){
            list.add((String) siIterator.next());
        }
        
        List<String> geometriesList = new ArrayList<>();
        while(geometryIterator.hasNext()){
            geometriesList.add((String) geometryIterator.next());
        }
        
        String name = jsonObject.getString(SemanticTag.NAME);
        String[] sis =  new String[list.size()];
        sis = list.toArray(sis);
        String[] geometries = new String[geometriesList.size()];
        geometries = geometriesList.toArray(geometries);
        
        SharkGeometry[] geoms = new SharkGeometry[geometries.length];
        for (int i = 0; i< geometries.length; i++) {
            geoms[i] = InMemoSharkGeometry.createGeomByEWKT(geometries[i]);
        }
        // TODO Geometries just adding the first geom
        SpatialSemanticTag tag = ((SpatialSTSet) targetSet).createSpatialSemanticTag(name, sis, geoms);
        deserializeProperties(tag, tagString);
        return tag;
    }
    
    /**
     * static variant - not to be used in protocol parser
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
        
        if(targetSet == null)
            targetSet = InMemoSharkKB.createInMemoTimeSTSet();
        
        if(tagString == null || tagString.isEmpty())
            return null;

        JSONObject jsonObject = new JSONObject(tagString);Long from = jsonObject.getLong(TimeSemanticTag.FROM);
        Long duration = jsonObject.getLong(TimeSemanticTag.DURATION);
        
        TimeSemanticTag tag = ((TimeSTSet) targetSet).createTimeSemanticTag(from, duration);
        return tag;
    }
    
    /**
     * static variant - not to be used in protocol parser
     * @param tag
     * @return
     * @throws SharkKBException 
     */
    public static TimeSemanticTag deserializeTimeTag(String tag) throws SharkKBException {
        // there is no specific set - create one
        TimeSTSet stSet = InMemoSharkKB.createInMemoTimeSTSet();
        return ASIPSerializer.deserializeTimeTag(stSet, tag);
    }
    
    public static STSet deserializeAnySTSet(STSet stSet, String stSetString) throws SharkKBException{   
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
                if(set==null) set = InMemoSharkKB.createInMemoSTSet();
                return ASIPSerializer.deserializeSTSet(set, stSetString);
            default:
                break;
        }
        
        return null;
    }

    public static STSet deserializeSTSet(STSet target, String stSetString) throws SharkKBException{

        if(target==null){
            target = InMemoSharkKB.createInMemoSTSet();
        }

        // SemanticNet
        JSONObject jsonObject = new JSONObject(stSetString);
        JSONArray jsonArray = null;
        try{
            jsonArray = jsonObject.getJSONArray(STSet.STSET);
        } catch (JSONException e){
            L.d(""+e);
            return null;
        }
        Iterator stIterator = jsonArray.iterator();
        while(stIterator.hasNext()){
            JSONObject tag = (JSONObject) stIterator.next();
            if(tag.has(PeerSemanticTag.ADDRESSES)){
                deserializePeerTag(target, tag.toString());
            } else if(tag.has(SpatialSemanticTag.GEOMETRY)){
                deserializeSpatialTag(target, tag.toString());
            } else if(tag.has(TimeSemanticTag.DURATION) || jsonObject.has(TimeSemanticTag.FROM)){
                deserializeTimeTag(target, tag.toString());
            } else{
                deserializeTag(target, tag.toString());
            }
        }
        return target;
    }
    
    public static PeerSTSet deserializePeerSTSet(STSet set, String stSetString) throws SharkKBException{
        if(set==null) set = InMemoSharkKB.createInMemoPeerSTSet();
        PeerSTSet peerSTSet = (PeerSTSet) set;
        L.d("ho");
        return (PeerSTSet) ASIPSerializer.deserializeSTSet(peerSTSet, stSetString);
    }
    
    public static TimeSTSet deserializeTimeSTSet(STSet set, String stSetString) throws SharkKBException{
        if(set==null) set = InMemoSharkKB.createInMemoTimeSTSet();
        TimeSTSet timeSTSet = (TimeSTSet) set;
        return (TimeSTSet) ASIPSerializer.deserializeSTSet(timeSTSet, stSetString);
    }
    
    public static SpatialSTSet deserializeSpatialSTSet(STSet set, String stSetString) throws SharkKBException{
        if(set==null) set = InMemoSharkKB.createInMemoSpatialSTSet();
        SpatialSTSet spatialSTSet = (SpatialSTSet) set;
        return (SpatialSTSet) ASIPSerializer.deserializeSTSet(spatialSTSet, stSetString);
    }
    
    public static STSet deserializeSTSet(String stSetString) throws SharkKBException{
        // there is no specific set - create one
        STSet stSet = InMemoSharkKB.createInMemoSTSet();
        return ASIPSerializer.deserializeSTSet(stSet, stSetString);
    }
    
    public static void deserializeProperties(SystemPropertyHolder target, String properties) throws SharkKBException{


        if(target == null){
            L.d(CLASS + "target == null");
            return;
        }
        if(properties == null){
            L.d(CLASS + "properties == null");
            return;
        }

        JSONObject jsonObject = new JSONObject(properties);

        if(jsonObject.has(PropertyHolder.PROPERTIES) && !jsonObject.isNull(PropertyHolder.PROPERTIES)){
            JSONArray jsonArray = null;
            try{
                jsonArray = jsonObject.getJSONArray(PropertyHolder.PROPERTIES);
            } catch( JSONException e){
                return;
            }

            Iterator iterator = jsonArray.iterator();

            while(iterator.hasNext()){
                JSONObject property = (JSONObject) iterator.next();
                String name = property.getString(PropertyHolder.NAME);
                String value = property.getString(PropertyHolder.VALUE);

                target.setProperty(name, value);
            }
        }

    }
    
    public static void deserializeRelations(Taxonomy target, String relations){
        if(target==null) return;
        if(relations==null) return;
        JSONObject jsonObject = new JSONObject(relations);
        JSONArray jsonArray = null;
        try{
            jsonArray = jsonObject.getJSONArray(Taxonomy.SUBSUPERTAGS);
        } catch (JSONException e){
            return;
        }
        Iterator iterator = jsonArray.iterator();
        
        while(iterator.hasNext()){
            JSONObject relation = (JSONObject) iterator.next();
            String sourceSI = relation.getString(Taxonomy.SOURCE);
            String targetSI = relation.getString(Taxonomy.TARGET);
            try {
                TXSemanticTag sourceTag = (TXSemanticTag) target.getSemanticTag(sourceSI);
                if(sourceTag == null) continue;

                TXSemanticTag targetTag = (TXSemanticTag) target.getSemanticTag(targetSI);
                if(targetTag == null) continue;
                
                // set super tag
                sourceTag.move(targetTag);
            }
            catch(SharkKBException skbe) {
                // ignore and go ahead
            }
        }
        
    }
    
    public static void deserializeRelations(SemanticNet target, String relations){
        if(target==null) return;
        if(relations==null) return;
        JSONObject jsonObject = new JSONObject(relations);
        JSONArray jsonArray = null;
        try{
            jsonArray = jsonObject.getJSONArray(SemanticNet.PREDICATES);
        } catch (JSONException e){
            return;
        }
        Iterator iterator = jsonArray.iterator();
        
        
        while(iterator.hasNext()){
            JSONObject relation = (JSONObject) iterator.next();
            String sourceSI = relation.getString(SemanticNet.SOURCE);
            String targetSI = relation.getString(SemanticNet.TARGET);
            String predicateName = relation.getString(SemanticNet.NAME);
            try {
                SNSemanticTag sourceTag = (SNSemanticTag) target.getSemanticTag(sourceSI);
                if(sourceTag == null) continue;

                SNSemanticTag targetTag = (SNSemanticTag) target.getSemanticTag(targetSI);
                if(targetTag == null) continue;
                
                // set super tag
                sourceTag.setPredicate(predicateName, targetTag);
            }
            catch(SharkKBException skbe) {
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

        L.d(parsed.toString(4));

        try {
            if(parsed.has(ASIPSpace.TOPICS)) topicsString = parsed.getString(ASIPSpace.TOPICS);
            if(parsed.has(ASIPSpace.TYPES)) typesString = parsed.getString(ASIPSpace.TYPES);
            if(parsed.has(ASIPSpace.SENDER)) senderString = parsed.getString(ASIPSpace.SENDER);
            if(parsed.has(ASIPSpace.APPROVERS)) approversString = parsed.getString(ASIPSpace.APPROVERS);
            if(parsed.has(ASIPSpace.RECEIVERS)) receiverString = parsed.getString(ASIPSpace.RECEIVERS);
            if(parsed.has(ASIPSpace.LOCATIONS)) locationsString = parsed.getString(ASIPSpace.LOCATIONS);
            if(parsed.has(ASIPSpace.TIMES)) timesString = parsed.getString(ASIPSpace.TIMES);
            if(parsed.has(ASIPSpace.DIRECTION)) direction = parsed.getInt(ASIPSpace.DIRECTION);
        } catch (JSONException e){
            L.d("" +e);
        }


        STSet topics;
        if(topicsString != null) {
            topics = deserializeSTSet(null, topicsString);
            interest.setTopics(topics);
        }
        STSet types;
        if(typesString != null) {
            types = deserializeSTSet(null, typesString);
            interest.setTypes(types);
        }
        PeerSTSet approvers;
        if(approversString != null) {
            approvers = deserializePeerSTSet(null, approversString);
            interest.setApprovers(approvers);
        }
        PeerSTSet receivers;
        if(receiverString!=null){
            receivers = deserializePeerSTSet(null, receiverString);
            interest.setReceivers(receivers);
        }
        SpatialSTSet locations;
        if(locationsString!=null){
            locations = deserializeSpatialSTSet(null, locationsString);
            interest.setLocations(locations);
        }
        TimeSTSet times;
        if(timesString!=null){
            times = deserializeTimeSTSet(null, timesString);
            interest.setTimes(times);
        }
        if(senderString != null){
            PeerSemanticTag sender = deserializePeerTag(null, senderString);
            interest.setSender(sender);
        }
        if(direction != -1) interest.setDirection(direction);

        return interest;
    }
    
    public static ASIPInterest deserializeASIPInterest(String sharkCS) throws SharkKBException {
        InMemoSharkKB imkb = new InMemoSharkKB();
        return ASIPSerializer.deserializeASIPInterest(imkb, sharkCS);
    }
    
    private SemanticNet cast2SN(STSet stset) throws SharkKBException {
        SemanticNet sn;
        try {
            sn = (SemanticNet) stset;
        }
        catch(ClassCastException e) {
            InMemoSTSet imset = null;
            
            try {
                imset = (InMemoSTSet) stset;
            }
            catch(ClassCastException cce) {
                throw new SharkKBException("sorry, this implementation works with in memo shark kb implementation only");
            }
            
            InMemoGenericTagStorage tagStorage = imset.getTagStorage();
            sn = new InMemoSemanticNet(tagStorage);
        }
        
        return sn;
    }
}
