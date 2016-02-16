package net.sharkfw.asip.engine;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import net.sharkfw.asip.ASIPInformationSpace;
import net.sharkfw.asip.ASIPInterest;
import net.sharkfw.asip.ASIPKnowledge;
import net.sharkfw.asip.ASIPSpace;
import net.sharkfw.knowledgeBase.Interest;
import net.sharkfw.knowledgeBase.PeerSTSet;
import net.sharkfw.knowledgeBase.PeerSemanticTag;
import net.sharkfw.knowledgeBase.PropertyHolder;
import net.sharkfw.knowledgeBase.SNSemanticTag;
import net.sharkfw.knowledgeBase.STSet;
import net.sharkfw.knowledgeBase.SemanticNet;
import net.sharkfw.knowledgeBase.SemanticTag;
import net.sharkfw.knowledgeBase.SharkCS;
import net.sharkfw.knowledgeBase.SharkKB;
import net.sharkfw.knowledgeBase.SharkKBException;
import net.sharkfw.knowledgeBase.SpatialSTSet;
import net.sharkfw.knowledgeBase.SystemPropertyHolder;
import net.sharkfw.knowledgeBase.TimeSTSet;
import net.sharkfw.knowledgeBase.TimeSemanticTag;
import net.sharkfw.knowledgeBase.inmemory.InMemoSharkKB;
import net.sharkfw.system.Util;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import net.sharkfw.knowledgeBase.SpatialSemanticTag;
import net.sharkfw.knowledgeBase.TXSemanticTag;
import net.sharkfw.knowledgeBase.Taxonomy;
import net.sharkfw.knowledgeBase.inmemory.InMemoInterest;

/**
 *
 * @author msc
 */
public class ASIPSerializer {
    
    public static final String HEADER = "HEADER";
    public static final String INTEREST = "INTEREST";
    public static final String KNOWLEDGE = "KNOWLEDGE";
    
    //Simple String Methods for Serialization
    
    public static String serializeExpose(ASIPMessage header, ASIPSpace interest) throws SharkKBException{
        return ASIPSerializer.serializeExposeJSON(header, interest).toString();
    }
    
    public static String serializeInsert(ASIPMessage header, ASIPKnowledge knowledge) throws SharkKBException{
        return ASIPSerializer.serializeInsertJSON(header, knowledge).toString();
    }
    
    public static String serializeHeader(ASIPMessage header) throws JSONException, SharkKBException{
        return ASIPSerializer.serializeHeaderJSON(header).toString();
    }

    public static String serializeInterest(ASIPSpace space) throws SharkKBException, JSONException {
        return ASIPSerializer.serializeInterestJSON(space).toString();
    }
    
    public static String serializeKnowledge(ASIPKnowledge knowledge){
        return ASIPSerializer.serializeKnowledgeJSON(knowledge).toString();
    }
    
    public static String serializeTag(SemanticTag tag) throws JSONException, SharkKBException {
        return ASIPSerializer.serializeTagJSON(tag).toString();
    }
    
    public static String serializeSTSet(STSet stset) throws SharkKBException, JSONException {
        return ASIPSerializer.serializeSTSetJSON(stset).toString();
    }
    
    public static String serializeProperties(SystemPropertyHolder target) throws SharkKBException{
        return ASIPSerializer.serializePropertiesJSON(target).toString();
    }
    
    public static String serializeRelations(Enumeration<SemanticTag> tagEnum){
        return ASIPSerializer.serializeRelationsJSON(tagEnum).toString();
    }
        
    public static String serializeASIPSpace(ASIPSpace space) throws SharkKBException {
        return ASIPSerializer.serializeASIPSpaceJSON(space).toString();
    }
    
    // JSON Helper Methods
    
    public static JSONObject serializeExposeJSON(ASIPMessage header, ASIPSpace interest)
            throws SharkKBException, JSONException {
        
        JSONObject object = new JSONObject();
        object.put(HEADER, serializeHeaderJSON(header));
        object.put(INTEREST, serializeInterestJSON(interest));
        return object;
    }

    public static JSONObject serializeInsertJSON(ASIPMessage header, ASIPKnowledge knowledge)
            throws JSONException, SharkKBException{
        JSONObject object = new JSONObject();
        object.put(HEADER, serializeHeaderJSON(header));
        object.put(KNOWLEDGE, serializeKnowledgeJSON(knowledge));
        return object;
    }    
    
    public static JSONObject serializeHeaderJSON(ASIPMessage header) throws JSONException, SharkKBException {
        return new JSONObject()
            .put(ASIPMessage.ENCRYPTED, header.isEncrypted())
            .put(ASIPMessage.ENCRYPTEDSESSIONKEY, header.getEncyptedSessionKey())
            .put(ASIPMessage.VERSION, header.getVersion())
            .put(ASIPMessage.FORMAT, header.getFormat())
            .put(ASIPMessage.COMMAND, header.getCommand())
            .put(ASIPMessage.SENDER, serializeTagJSON(header.getSender()))
            .put(ASIPMessage.RECEIVERS, serializeSTSetJSON(header.getReceivers()))
            .put(ASIPMessage.SIGNATURE, header.getSignature());
    }
    
    public static JSONObject serializeInterestJSON(ASIPSpace space) throws SharkKBException, JSONException {
        return serializeASIPSpaceJSON(space);
    }
    
    public static JSONObject serializeKnowledgeJSON(ASIPKnowledge knowledge){
        return new JSONObject();
    }
    
    public static JSONObject serializeTagJSON(SemanticTag tag) throws JSONException, SharkKBException {
        
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
        
        object.put(PropertyHolder.PROPERTIES, serializePropertiesJSON(tag));
        
        return object;
    }
    
    public static JSONObject serializeSTSetJSON(STSet stset) throws SharkKBException, JSONException {
        
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
            set.put(ASIPSerializer.serializeTagJSON(tags.nextElement()));
        }
        
        jsonObject.put(STSet.STSET, set);
        
//        System.out.println(jsonObject.toString());
        
        return jsonObject;
    }
    
    public static JSONObject serializePropertiesJSON(SystemPropertyHolder target) throws SharkKBException{
        if(target == null) {
            return null;
        }
        
        Enumeration<String> propNamesEnum = target.propertyNames(false);
        if(propNamesEnum == null || !propNamesEnum.hasMoreElements()) {
            return new JSONObject();
        }
        JSONObject jsonOject = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        while(propNamesEnum.hasMoreElements()){
            String name = propNamesEnum.nextElement();
            String value = target.getProperty(name);
            
            JSONObject property = new JSONObject();
            property.put(PropertyHolder.NAME, name);
            property.put(PropertyHolder.VALUE, value);
            jsonArray.put(property);
        }
        
        jsonOject.put(PropertyHolder.PROPERTIES, jsonArray);
        
        return jsonOject;
    }
    
    public static JSONObject serializeRelationsJSON(Enumeration<SemanticTag> tagEnum){
        
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
        
    public static JSONObject serializeASIPSpaceJSON(ASIPSpace space) throws SharkKBException {
        if(space == null)
            return null;
        
        JSONObject jsonObject = new JSONObject();
         
        STSet topics = space.getTopics();
        if(topics != null && !topics.isEmpty()) {
            jsonObject.put(ASIPSpace.TOPICS, serializeSTSetJSON(topics));
        }
        
        // sender
        PeerSemanticTag sender = space.getSender();
        if(sender != null) {
            jsonObject.put(ASIPSpace.SENDER, serializeTagJSON(sender));
        }
        
        // approvers
        PeerSTSet approvers = space.getApprovers();
        if(approvers != null && !approvers.isEmpty()) {
            jsonObject.put(ASIPSpace.APPROVERS, serializeSTSetJSON(approvers));
        }
        
        // receivers
        PeerSTSet receivers = space.getReceivers();
        if(receivers != null && !receivers.isEmpty()) {
            jsonObject.put(ASIPSpace.RECEIVERS, serializeSTSetJSON(receivers));
        }
        
        // locations
        SpatialSTSet locations = space.getLocations();
        if(locations != null && !locations.isEmpty()) {
            jsonObject.put(ASIPSpace.LOCATIONS, serializeSTSetJSON(locations));
        }
        
        
        // times
        TimeSTSet times = space.getTimes();
        if(times != null && !times.isEmpty()) {
            jsonObject.put(ASIPSpace.TIMES, serializeSTSetJSON(times));
        }

        // direction
        jsonObject.put(ASIPSpace.DIRECTION, space.getDirection());
        
        return jsonObject;
    }
    
    public static ASIPMessage deserializeHeader(String header){
        return null;
    }
    
    public static ASIPSpace deserializeInterest(String interestString) {
        if(interestString.isEmpty())
            return null;
        
        InMemoInterest interest = (InMemoInterest) InMemoSharkKB.createInMemoInterest();
        
        JSONObject jsonObject = new JSONObject(interestString);
        
        JSONArray topicsJSON = jsonObject.getJSONArray(ASIPSpace.TOPICS);
        JSONArray typesJSON = jsonObject.getJSONArray(ASIPSpace.TYPES);
        JSONArray approversJSON = jsonObject.getJSONArray(ASIPSpace.APPROVERS);
        JSONObject senderJSON = jsonObject.getJSONObject(ASIPSpace.SENDER);
        JSONArray receiversJSON = jsonObject.getJSONArray(ASIPSpace.RECEIVERS);
        JSONArray locationsJSON = jsonObject.getJSONArray(ASIPSpace.LOCATIONS);
        JSONArray timesJSON = jsonObject.getJSONArray(ASIPSpace.TIMES);
        JSONArray directionJSON = jsonObject.getJSONArray(ASIPSpace.DIRECTION);
        
        // TODO 
        
//        interest.setTopics(topicsJSON.g);
        return (ASIPSpace) interest;
    }
    
    /**
     * Deserializes knowledge and return a newly created knowledge object..
     * @param knowledge
     * @return
     * @throws SharkKBException 
     */
    public static ASIPKnowledge deserializeKnowledge(String knowledge) throws SharkKBException {
        ASIPSerializer wS = new ASIPSerializer();
        
        InMemoSharkKB imkb = new InMemoSharkKB();
        
        wS.deserializeAndMergeKnowledge(imkb, knowledge);
        
        return null; // TODO
        
        // return imkb.asKnowledge();
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
        
        /* we use deserializeCS in that class to create a context space out
            of a string */
        ASIPSpace cs = this.deserializeCS(target, knowledgeString);
        
        /* infos can be added now - tja und das muss man schlau machen
        wegen der eventuell großen Datenmengen. Man kann ein Infoobjekt
        eibnhängen, das aber nicht sofort alle Daten aus dem stream liest...
        Da InfoSpace nicht implementiert ist, kann man sich noch alles
        wünschen... ;)
        */
        // infoSpace.addInformation(??)
    }
    
    public static SemanticTag deserializeTag(STSet targetSet, String tagString) throws SharkKBException {
        
        JSONObject jsonObject = new JSONObject(tagString);
        Iterator siIterator = jsonObject.getJSONArray(SemanticTag.SI).iterator();
                
        List<String> list = new ArrayList<>();
        while(siIterator.hasNext()){
            list.add((String) siIterator.next());
        }
        
        String name = jsonObject.getString(SemanticTag.NAME);
        String[] sis =  new String[list.size()];
        sis = list.toArray(sis);
        
        // if success - create a tag with targetSet
        return targetSet.createSemanticTag(name, sis);
    }
    
    /**
     * static variant - not to be used in protocol parser
     * @param tag
     * @return
     * @throws SharkKBException 
     */
    public static SemanticTag deserializeTag(String tag) throws SharkKBException {
        // there is no specific set - create one
        STSet stSet = InMemoSharkKB.createInMemoSTSet();
        return ASIPSerializer.deserializeTag(stSet, tag);
    }
    
    public static STSet deserializeAnySTSet(STSet stSet, String stSetString) throws SharkKBException{   
        
        // TODO is usefull to just pass the string of the stset without the type?
        
        JSONObject jsonObject = new JSONObject(stSetString);
        
        String typeJSON = jsonObject.getString(STSet.TYPE);
        String stSetJSON = jsonObject.getJSONArray(STSet.STSET).toString();
        System.out.println(stSetJSON);
        
        STSet set = stSet;
        
        switch (typeJSON) {
            case STSet.PEERSTSET:
                if(set==null) set = InMemoSharkKB.createInMemoPeerSTSet();
                return (PeerSTSet) ASIPSerializer.deserializeSTSet(set, stSetJSON);
            case STSet.TIMESTSET:
                if(set==null) set = InMemoSharkKB.createInMemoTimeSTSet();
                return (TimeSTSet) ASIPSerializer.deserializeSTSet(set, stSetJSON);
            case STSet.SPATIALSTSET:
                if(set==null) set = InMemoSharkKB.createInMemoSpatialSTSet();
                return (SpatialSTSet) ASIPSerializer.deserializeSTSet(set, stSetJSON);
            case STSet.ANYSTSET:
                if(set==null) set = InMemoSharkKB.createInMemoSTSet();
                return ASIPSerializer.deserializeSTSet(set, stSetJSON);
            default:
                break;
        }
        
        return null;
    }
    
    public static STSet deserializeSTSet(STSet target, String stSetString) throws SharkKBException{
        
        // SemanticNet
        
//        JSONObject jsonObject = new JSONObject(stSetString);
        
        JSONArray jsonArray = new JSONArray(stSetString);
        Iterator stIterator = jsonArray.iterator();
        while(stIterator.hasNext()){
            deserializeTag(target, stIterator.next().toString());
        }
        return target;
    }
    
    public static PeerSTSet deserializePeerSTSet(String stSetString) throws SharkKBException{
        STSet stSet = InMemoSharkKB.createInMemoPeerSTSet();
        return (PeerSTSet) ASIPSerializer.deserializeSTSet(stSet, stSetString);
    }
    
    public static TimeSTSet deserializeTimeSTSet(String stSetString) throws SharkKBException{
        STSet stSet = InMemoSharkKB.createInMemoTimeSTSet();
        return (TimeSTSet) ASIPSerializer.deserializeSTSet(stSet, stSetString);
    }
    
    public static SpatialSTSet deserializeSpatialSTSet(String stSetString) throws SharkKBException{
        STSet stSet = InMemoSharkKB.createInMemoSpatialSTSet();
        return (SpatialSTSet) ASIPSerializer.deserializeSTSet(stSet, stSetString);
    }
    
    public static STSet deserializeSTSet(String stSetString) throws SharkKBException{
        // there is no specific set - create one
        STSet stSet = InMemoSharkKB.createInMemoSTSet();
        return ASIPSerializer.deserializeSTSet(stSet, stSetString);
    }
    
    public static SystemPropertyHolder deserializeProperties(String properties){
        return null;
    }
    
    public static Enumeration<SemanticTag> deserializeRelations(String relations){
        return null;
    }
        
    public ASIPSpace deserializeCS(SharkKB kb, String sharkCS) throws SharkKBException {
        
        // TODO
        
        ASIPInterest interest = InMemoSharkKB.createInMemoASIPInterest();
        
        JSONObject deserialized = new JSONObject(sharkCS);
        
        JSONArray topicsArray = deserialized.getJSONArray(ASIPSpace.TOPICS);
        
        // read topics dimension
        String topicsSerialized = topicsArray.toString();

        // create objects: topics dimension already set in empty interest??
        STSet topics = this.deserializeSTSet(interest.getTopics(), topicsSerialized);
        
        interest.setTopics(topics);
        
        // types
        STSet types = null;
        // deserialize.. and set
        interest.setTypes(types);
        
        // sender
        PeerSemanticTag sender = null;
        // deserialize.. and set
        interest.setSender(sender);
        
        // approvers
        PeerSTSet approvers = null;
        // deserialize.. and set
        interest.setApprovers(approvers);
        
        // receivers
        PeerSTSet receivers = null;
        // deserialize.. and set
        interest.setReceivers(receivers);
        
        // times
        TimeSTSet times = null;
        // deserialize.. and set
        interest.setTimes(times);
        
        // locations
        SpatialSTSet locations = null;
        // deserialize.. and set
        interest.setLocations(locations);
        
        // direction
        int direction = 0;
        // deserialize.. and set
        interest.setDirection(direction);
        
        return interest;
    }
    
    public static ASIPSpace deserializeSharkCS(String sharkCS) throws SharkKBException {
        ASIPSerializer wS = new ASIPSerializer();
        
        InMemoSharkKB imkb = new InMemoSharkKB();
        
        return wS.deserializeCS(imkb, sharkCS);
    }
}
