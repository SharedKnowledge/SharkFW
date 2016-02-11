package net.sharkfw.asip;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import net.sharkfw.knowledgeBase.ASIPInterest;
import net.sharkfw.knowledgeBase.InformationSpace;
import net.sharkfw.knowledgeBase.Interest;
import net.sharkfw.knowledgeBase.Knowledge;
import net.sharkfw.knowledgeBase.PeerSTSet;
import net.sharkfw.knowledgeBase.PeerSemanticTag;
import net.sharkfw.knowledgeBase.STSet;
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
import net.sharkfw.knowledgeBase.ASIPSpace;
import net.sharkfw.knowledgeBase.SpatialSemanticTag;
import net.sharkfw.knowledgeBase.inmemory.InMemoInterest;
import net.sharkfw.knowledgeBase.inmemory.InMemoSharkCS;

/**
 *
 * @author micha
 */
public class ASIPSerializer {
    
    public static final String HEADER = "HEADER";
    public static final String INTEREST = "INTEREST";
    public static final String KNOWLEDGE = "KNOWLEDGE";
    
    //Simple String Methods for Serialization
    
    public static String serializeExpose(ASIPMessage header, ASIPSpace interest) throws SharkKBException{
        return ASIPSerializer.serializeExposeJSON(header, interest).toString();
    }
    
    public static String serializeInsert(ASIPMessage header, Knowledge knowledge) throws SharkKBException{
        return ASIPSerializer.serializeInsertJSON(header, knowledge).toString();
    }
    
    public static String serializeHeader(ASIPMessage header) throws JSONException, SharkKBException{
        return ASIPSerializer.serializeHeaderJSON(header).toString();
    }

    public static String serializeInterest(ASIPSpace space) throws SharkKBException, JSONException {
        return ASIPSerializer.serializeInterestJSON(space).toString();
    }
    
    public static String serializeKnowledge(){
        return ASIPSerializer.serializeKnowledgeJSON().toString();
    }
    
    public static String serializeTag(SemanticTag tag) throws JSONException {
        return ASIPSerializer.serializeTagJSON(tag).toString();
    }
    
    public static String serializeSTSet(STSet stset) throws SharkKBException, JSONException {
        return ASIPSerializer.serializeSTSetJSON(stset).toString();
    }
    
    public static String serializeProperties(SystemPropertyHolder target){
        return ASIPSerializer.serializePropertiesJSON(target).toString();
    }
    
    public static String serializeRelations(Enumeration<SemanticTag> tagEnum){
        return ASIPSerializer.serializeRelationsJSON(tagEnum).toString();
    }
        
    public static String serializeASIPSpace(ASIPSpace space) {
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

    public static JSONObject serializeInsertJSON(ASIPMessage header, Knowledge knowledge){
        return new JSONObject();
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
        JSONObject object = new JSONObject();
        
        STSet topics = space.getTopics();
        STSet types = space.getTypes();
        STSet approvers = space.getApprovers();
        STSet receivers = space.getReceivers();
        SemanticTag sender = space.getSender();
        STSet locations = space.getLocations();
        STSet times = space.getTimes();
        int direction = space.getDirection();
        
        object.put(ASIPInterest.TOPICS, ASIPSerializer.serializeSTSetJSON(topics));
        object.put(ASIPInterest.TYPES, ASIPSerializer.serializeSTSetJSON(types));
        object.put(ASIPInterest.APPROVERS, ASIPSerializer.serializeSTSetJSON(approvers));
        object.put(ASIPInterest.RECEIVERS, ASIPSerializer.serializeSTSetJSON(receivers));
        object.put(ASIPInterest.SENDER, ASIPSerializer.serializeTagJSON(sender));
        object.put(ASIPInterest.LOCATIONS, ASIPSerializer.serializeSTSetJSON(locations));
        object.put(ASIPInterest.TIMES, ASIPSerializer.serializeSTSetJSON(times));
        object.put(ASIPInterest.DIRECTION, direction);
        
        return object;
    }
    
    public static JSONObject serializeKnowledgeJSON(){
        return new JSONObject();
    }
    
    public static JSONObject serializeTagJSON(SemanticTag tag) throws JSONException {
        
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
        
        
        //TODO Properties
        
        // properties
//        String serializedProperties = this.serializePropertiesJSON(tag);
//        if(serializedProperties != null) {
//            object.append("prooperties", serializedProperties);
//        }
        
        return object;
    }
    
    public static JSONArray serializeSTSetJSON(STSet stset) throws SharkKBException, JSONException {
        
        if(stset == null){
            return null;
        }
        
        JSONArray set = new JSONArray();
        
        Enumeration<SemanticTag> tags = stset.tags();
        
        while(tags.hasMoreElements()) {
            set.put(ASIPSerializer.serializeTagJSON(tags.nextElement()));
        }
        
        return set;
    }
    
    public static JSONObject serializePropertiesJSON(SystemPropertyHolder target){
        return new JSONObject();
    }
    
    public static JSONObject serializeRelationsJSON(Enumeration<SemanticTag> tagEnum){
        return new JSONObject();
    }
        
    public static JSONObject serializeASIPSpaceJSON(ASIPSpace space) {
        return new JSONObject();
    }
    
    public static ASIPMessage deserializeHeader(String header){
        return null;
    }
    
    public static ASIPInterest deserializeInterest(String interestString) {
        if(interestString.isEmpty())
            return null;
        
        InMemoInterest interest = (InMemoInterest) InMemoSharkKB.createInMemoInterest();
        
        JSONObject jsonObject = new JSONObject(interestString);
        
        JSONArray topicsJSON = jsonObject.getJSONArray(ASIPInterest.TOPICS);
        JSONArray typesJSON = jsonObject.getJSONArray(ASIPInterest.TYPES);
        JSONArray approversJSON = jsonObject.getJSONArray(ASIPInterest.APPROVERS);
        JSONObject senderJSON = jsonObject.getJSONObject(ASIPInterest.SENDER);
        JSONArray receiversJSON = jsonObject.getJSONArray(ASIPInterest.RECEIVERS);
        JSONArray locationsJSON = jsonObject.getJSONArray(ASIPInterest.LOCATIONS);
        JSONArray timesJSON = jsonObject.getJSONArray(ASIPInterest.TIMES);
        JSONArray directionJSON = jsonObject.getJSONArray(ASIPInterest.DIRECTION);
        
//        interest.setTopics(topicsJSON.g);
        return (ASIPInterest) interest;
    }
    
    /**
     * Deserializes knowledge and return a newly created knowledge object..
     * @param knowledge
     * @return
     * @throws SharkKBException 
     */
    public static Knowledge deserializeKnowledge(String knowledge) throws SharkKBException {
        ASIPSerializer wS = new ASIPSerializer();
        
        InMemoSharkKB imkb = new InMemoSharkKB();
        
        wS.deserializeAndMergeKnowledge(imkb, knowledge);
        
        return imkb.asKnowledge();
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
        
        // could add cs to knowledge base
        InformationSpace infoSpace = target.createInformationSpace(cs);
        
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
    
    public STSet deserializeSTSet(STSet target, String stset){
        
        
        
        return null;
    }
    
    public static SystemPropertyHolder deserializeProperties(String properties){
        return null;
    }
    
    public static Enumeration<SemanticTag> deserializeRelations(String relations){
        return null;
    }
        
    public ASIPSpace deserializeCS(SharkKB kb, String sharkCS) throws SharkKBException {
        
        // TODO
        
        Interest interest = InMemoSharkKB.createInMemoInterest();
        
        JSONObject deserialized = new JSONObject(sharkCS);
        
        JSONArray topicsArray = deserialized.getJSONArray(ASIPInterest.TOPICS);
        
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
