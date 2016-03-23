package net.sharkfw.asip.engine;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
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
import net.sharkfw.knowledgeBase.SharkCS;
import net.sharkfw.knowledgeBase.SharkKB;
import net.sharkfw.knowledgeBase.SharkKBException;
import net.sharkfw.knowledgeBase.SharkVocabulary;
import net.sharkfw.knowledgeBase.SpatialSTSet;
import net.sharkfw.knowledgeBase.SystemPropertyHolder;
import net.sharkfw.knowledgeBase.TimeSTSet;
import net.sharkfw.knowledgeBase.TimeSemanticTag;
import net.sharkfw.knowledgeBase.inmemory.*;
import net.sharkfw.protocols.SharkInputStream;
import net.sharkfw.system.L;
import net.sharkfw.system.Util;
import org.apache.commons.compress.utils.IOUtils;
import org.apache.maven.doxia.logging.Log;
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
        object.put(KNOWLEDGE, serializeKnowledge(knowledge).toString());
        object.put(CONTENT, content);
        return object;
    }

    public static JSONObject serializeRaw(ASIPMessage header, InputStream raw) throws SharkKBException {

        JSONObject object = serializeHeader(header);
        JSONObject content = new JSONObject();
        content.put(LOGICALSENDER, ""); // PeerSemanticTag from Content Sender.
        content.put(SIGNED, false); // If signed or not
        // TODO Possible to put byte[] in json object?
        try {
            object.put(RAW, IOUtils.toByteArray(raw));
        } catch (IOException e) {
            e.printStackTrace();
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
    
    // Deserialize

//    TODO
//    public static ASIPMessage deserializeMessage(String string) throws SharkKBException{
//        ASIPInMessage message = new ASIPInMessage(null, null, null);
//        JSONObject jsonObject = new JSONObject(string);
//
//        message.
//
//        message.setEncrypted(jsonObject.getBoolean(ASIPMessage.ENCRYPTED));
//        message.setEncryptedSessionKey(jsonObject.getString(ASIPMessage.ENCRYPTEDSESSIONKEY));
//        message.setVersion(jsonObject.getString(ASIPMessage.VERSION));
//        message.setFormat(jsonObject.getString(ASIPMessage.FORMAT));
//        message.setCommand(jsonObject.getInt(ASIPMessage.COMMAND));
//
//        String senderString = jsonObject.getString(ASIPMessage.SENDER);
//        message.setSenders(deserializePeerTag(senderString));
//
//        String receiverString = jsonObject.getString(ASIPMessage.RECEIVERS);
//        STSet set = deserializeSTSet(receiverString);
//        message.setReceivers(set);
//
//        message.setSignature(jsonObject.getString(ASIPMessage.SIGNATURE));
//
//        JSONObject content = (JSONObject) jsonObject.get(CONTENT);
//
//        if(content.has(INTEREST)){
//            message.setInterest(deserializeInterest(content.getString(INTEREST)));
//        } else if (content.has(KNOWLEDGE)){
//            message.setKnowledge(deserializeKnowledge(content.getString(KNOWLEDGE)));
//        } else if(content.has(RAW)){
//            // TODO RAW
//        }
//        return message;
//    }

    public static void deserializeInMessage(ASIPInMessage message, String parsedStream){
        if(parsedStream.isEmpty()){
            L.d(CLASS + "Stream is empty.");
            return;
        }

        L.d(CLASS + "Start parsing the stream.");

        JSONObject object = null;

        try{
            object = new JSONObject(parsedStream);
        } catch(Exception e){
            L.d(CLASS + e);
        }

        L.d(CLASS + "JSONObject created");


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
            L.d(CLASS + "SENDER");
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
        L.d(CLASS + "receiver");

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

        L.d(CLASS + "iterating through receivers");
//
//        try {
//            L.d(CLASS + receivers.size());
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

        ASIPSpace interest = null;
        ASIPKnowledge knowledge = null;
        L.d(CLASS + command);
        switch (command){
            case ASIPMessage.ASIP_EXPOSE:
                try {
                    interest = deserializeASIPSpace(object.getString(ASIPSerializer.INTEREST));
                    message.setInterest(interest);
                } catch (SharkKBException e) {
                    e.printStackTrace();
                }
                break;
            case ASIPMessage.ASIP_INSERT:
                try {
                    knowledge = deserializeKnowledge(object.getString(ASIPSerializer.KNOWLEDGE));
                    message.setKnowledge(knowledge);
                } catch (SharkKBException e) {
                    e.printStackTrace();
                }
                break;
            case ASIPMessage.ASIP_RAW:
                L.d(CLASS + "RAW");
                L.d(CLASS + object.has(ASIPSerializer.RAW));
                byte[] raw = object.get(ASIPSerializer.RAW).toString().getBytes();
                L.d(CLASS + "RAW1");
                message.setRaw(raw);
                L.d(CLASS + "RAW2");
                break;
        }
        L.d(CLASS + "finished");
    }


    
    public static ASIPSpace deserializeInterest(String interestString) throws SharkKBException {
        return deserializeASIPSpace(interestString);
    }
    
    /**
     * TODO SharkInputStream as param
     * Deserializes knowledge and return a newly created knowledge object..
     * @return
     * @throws SharkKBException 
     */
    public static ASIPKnowledge deserializeKnowledge(String string) throws SharkKBException {
        
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
            ASIPSpace space = ASIPSerializer.deserializeASIPSpace(infoObject.getString(ASIPPointInformation.ASIPSPACE));

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
    
    /**
     * Deserializes and merges knowledge into an existing knowledge base
     * @param target
     * @param knowledgeString
     * @throws SharkKBException 
     */
    public void deserializeAndMergeKnowledge(SharkKB target, String knowledgeString) throws SharkKBException {
        // deserialize vocabulary and merge into target
        SharkCS vocabulary = null; // shouldn't be null after deserialization
        Util.merge(target, vocabulary);
        
        // deserialize context
        
        /*
        LASP exchanges knowledge which contains semantically annotated 
        information. Each information is attached to a context space (!) not 
        only a single context point as in KEP. That actually is one major
        difference (enhancements) compared to KEP. Thus, we can deserialize
        ContextSpace objects and add information later...
        */
        
        /* we use deserializeASIPSpace in that class to create a context space out
            of a string */
        ASIPSpace cs = deserializeASIPSpace(target, knowledgeString);
        
        /* infos can be added now - tja und das muss man schlau machen
        wegen der eventuell großen Datenmengen. Man kann ein Infoobjekt
        eibnhängen, das aber nicht sofort alle Daten aus dem stream liest...
        Da InfoSpace nicht implementiert ist, kann man sich noch alles
        wünschen... ;)
        */
        // infoSpace.addInformation(??)
    }
    
    public static SemanticTag deserializeTag(STSet targetSet, String tagString) throws SharkKBException {
        
        // TODO What if targetSet equals null
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
        // there is no specific set - create one

        L.d(CLASS + "deserializeTag()");

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

        L.d(CLASS + "props finished");

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
        
        // SemanticNet
        JSONObject jsonObject = new JSONObject(stSetString);
        JSONArray jsonArray = null;
        try{
            jsonArray = jsonObject.getJSONArray(STSet.STSET);
        } catch (JSONException e){
            return null;
        }
        Iterator stIterator = jsonArray.iterator();
        while(stIterator.hasNext()){
            JSONObject tag = (JSONObject) stIterator.next();
            if(jsonObject.has(PeerSemanticTag.ADDRESSES)){
                deserializePeerTag(target, tag.toString());
            } else if(jsonObject.has(SpatialSemanticTag.GEOMETRY)){
                deserializeSpatialTag(target, tag.toString());
            } else if(jsonObject.has(TimeSemanticTag.DURATION) || jsonObject.has(TimeSemanticTag.FROM)){
                deserializeTimeTag(target, tag.toString());
            } else{
                deserializeTag(target, tag.toString());
            }
        }
        return target;
    }
    
    public static PeerSTSet deserializePeerSTSet(STSet stSet, String stSetString) throws SharkKBException{
        STSet set = stSet;
        if(set==null) set = InMemoSharkKB.createInMemoPeerSTSet();
        return (PeerSTSet) ASIPSerializer.deserializeSTSet(set, stSetString);
    }
    
    public static TimeSTSet deserializeTimeSTSet(STSet stSet, String stSetString) throws SharkKBException{
        STSet set = stSet;
        if(set==null) set = InMemoSharkKB.createInMemoTimeSTSet();
        return (TimeSTSet) ASIPSerializer.deserializeSTSet(set, stSetString);
    }
    
    public static SpatialSTSet deserializeSpatialSTSet(STSet stSet, String stSetString) throws SharkKBException{
        STSet set = stSet;
        if(set==null) set = InMemoSharkKB.createInMemoSpatialSTSet();
        return (SpatialSTSet) ASIPSerializer.deserializeSTSet(set, stSetString);
    }
    
    public static STSet deserializeSTSet(String stSetString) throws SharkKBException{
        // there is no specific set - create one
        STSet stSet = InMemoSharkKB.createInMemoSTSet();
        return ASIPSerializer.deserializeSTSet(stSet, stSetString);
    }
    
    public static void deserializeProperties(SystemPropertyHolder target, String properties) throws SharkKBException{

        L.d(CLASS + "deserializeProperties");

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
                L.d(CLASS + e);
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
        // TODO deserializeRelations
        if(target==null) return;
        if(relations==null) return;
        JSONObject jsonObject = new JSONObject(relations);
        JSONArray jsonArray = null;
        try{
            jsonArray = jsonObject.getJSONArray(Taxonomy.SUBSUPERTAGS);
        } catch (JSONException e){
            L.d(CLASS + e);
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
        // TODO deserializeRelations
        if(target==null) return;
        if(relations==null) return;
        JSONObject jsonObject = new JSONObject(relations);
        JSONArray jsonArray = null;
        try{
            jsonArray = jsonObject.getJSONArray(SemanticNet.PREDICATES);
        } catch (JSONException e){
            L.d(CLASS + e);
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
        
    public static ASIPSpace deserializeASIPSpace(SharkKB kb, String spaceString) throws SharkKBException {
        
        ASIPInterest interest = InMemoSharkKB.createInMemoASIPInterest();
        
        JSONObject deserialized = new JSONObject(spaceString);
        JSONArray topicsArray = null;
        try{
            topicsArray = deserialized.getJSONArray(ASIPSpace.TOPICS);
        } catch (JSONException e){ }

        JSONArray typesArray = null;
        try{
            typesArray = deserialized.getJSONArray(ASIPSpace.TYPES);
        } catch (JSONException e){ }

        JSONArray approversArray = null;
        try{
            approversArray = deserialized.getJSONArray(ASIPSpace.APPROVERS);
        } catch (JSONException e){ }

        JSONArray receiversArray = null;
        try{
            receiversArray = deserialized.getJSONArray(ASIPSpace.RECEIVERS);
        } catch (JSONException e){ }

        JSONArray locationsArray = null;
        try{
            locationsArray = deserialized.getJSONArray(ASIPSpace.LOCATIONS);
        } catch (JSONException e){ }

        JSONArray timesArray = null;
        try{
            timesArray = deserialized.getJSONArray(ASIPSpace.TIMES);
        } catch (JSONException e){ }
        JSONObject senderObject = deserialized.getJSONObject(ASIPSpace.SENDER);
        int direction = deserialized.getInt(ASIPSpace.DIRECTION);

        STSet topics;
        if(topicsArray != null) {
            topics = deserializeSTSet(interest.getTopics(), topicsArray.toString());
            interest.setTopics(topics);
        }
        STSet types;
        if(typesArray!=null) {
            types = deserializeSTSet(interest.getTypes(), typesArray.toString());
            interest.setTypes(types);
        }
        PeerSTSet approvers;
        if(approversArray!=null) {
            approvers = deserializePeerSTSet(interest.getApprovers(), approversArray.toString());
            interest.setApprovers(approvers);
        }
        PeerSTSet receivers;
        if(receiversArray!=null){
            receivers = deserializePeerSTSet(interest.getReceivers(), receiversArray.toString());
            interest.setReceivers(receivers);
        }
        SpatialSTSet locations;
        if(locationsArray!=null){
            locations = deserializeSpatialSTSet(interest.getLocations(), locationsArray.toString());
            interest.setLocations(locations);
        }
        TimeSTSet times;
        if(timesArray!=null){
            times = deserializeTimeSTSet(interest.getTimes(), timesArray.toString());
            interest.setTimes(times);
        }
        // TODO Casting okay?
        PeerSemanticTag sender = deserializePeerTag(null, senderObject.toString());

        interest.setSender(sender);
        interest.setDirection(direction);
        
        return interest;
    }
    
    public static ASIPSpace deserializeASIPSpace(String sharkCS) throws SharkKBException {
        InMemoSharkKB imkb = new InMemoSharkKB();
        return ASIPSerializer.deserializeASIPSpace(imkb, sharkCS);
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
