package net.sharkfw.asip;

import java.util.Enumeration;
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

/**
 *
 * @author micha
 */
public class ASIPSerializer {
    
    public static final String HEADER = "HEADER";
    public static final String INTEREST = "INTEREST";
    public static final String KNOWLEDGE = "KNOWLEDGE";
    
    /**
     *
     * @param header
     * @param interest
     * @return
     * @throws SharkKBException
     * @throws JSONException
     */
    public static JSONObject serializeExpose(ASIPMessage header, ASIPSpace interest)
            throws SharkKBException, JSONException {
        
        JSONObject object = new JSONObject();
        object.put(HEADER, serializeHeader(header));
        object.put(INTEREST, serializeInterest(interest));
        return object;
    }
    
    public static JSONObject serializeInsert(ASIPMessage header, Knowledge knowledge){
        return new JSONObject();
    }
    
    public static JSONObject serializeHeader(ASIPMessage header) throws JSONException, SharkKBException {
        return new JSONObject()
            .put(ASIPMessage.ENCRYPTED, header.isEncrypted())
            .put(ASIPMessage.ENCRYPTEDSESSIONKEY, header.getEncyptedSessionKey())
            .put(ASIPMessage.VERSION, header.getVersion())
            .put(ASIPMessage.FORMAT, header.getFormat())
            .put(ASIPMessage.COMMAND, header.getCommand())
            .put(ASIPMessage.SENDER, serializeTag(header.getSender()))
            .put(ASIPMessage.RECEIVERS, serializeSTSet(header.getReceivers()))
            .put(ASIPMessage.SIGNATURE, header.getSignature());
    }
    
    
    public static JSONObject serializeInterest(ASIPSpace space) throws SharkKBException, JSONException {
        JSONObject object = new JSONObject();
        
        STSet topics = space.getTopics();
        STSet types = space.getTypes();
        STSet approvers = space.getApprovers();
        STSet receivers = space.getReceivers();
        SemanticTag sender = space.getSender();
        STSet locations = space.getLocations();
        STSet times = space.getTimes();
        int direction = space.getDirection();
        
        object.put(ASIPInterest.TOPICS, ASIPSerializer.serializeSTSet(topics));
        object.put(ASIPInterest.TYPES, ASIPSerializer.serializeSTSet(types));
        object.put(ASIPInterest.APPROVERS, ASIPSerializer.serializeSTSet(approvers));
        object.put(ASIPInterest.RECEIVERS, ASIPSerializer.serializeSTSet(receivers));
        object.put(ASIPInterest.SENDER, ASIPSerializer.serializeTag(sender));
        object.put(ASIPInterest.LOCATIONS, ASIPSerializer.serializeSTSet(locations));
        object.put(ASIPInterest.TIMES, ASIPSerializer.serializeSTSet(times));
        object.put(ASIPInterest.DIRECTION, direction);
        
        return object;
    }
    
    public static JSONObject serializeKnowledge(){
        return new JSONObject();
    }
    
    public static JSONObject serializeTag(SemanticTag tag) throws JSONException {
        
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
            object.put(SpatialSemanticTag.GEOMETRY, sst);
        }
        
        
        //TODO Properties
        
        // properties
//        String serializedProperties = this.serializeProperties(tag);
//        if(serializedProperties != null) {
//            object.append("prooperties", serializedProperties);
//        }
        
        return object;
    }
    
    public static JSONArray serializeSTSet(STSet stset) throws SharkKBException, JSONException {
        
        if(stset == null){
            return null;
        }
        
        JSONArray set = new JSONArray();
        
        Enumeration<SemanticTag> tags = stset.tags();
        
        while(tags.hasMoreElements()) {
            set.put(ASIPSerializer.serializeTag(tags.nextElement()));
        }
        
        return set;
    }
    
    public static JSONObject serializeProperties(SystemPropertyHolder target){
        return new JSONObject();
    }
    
    public static JSONObject serializeRelations(Enumeration<SemanticTag> tagEnum){
        return new JSONObject();
    }
        
    public static JSONObject serializeASIPSpace(ASIPSpace space) {
        return new JSONObject();
    }
    
    public static ASIPMessage deserializeHeader(String header){
        return null;
    }
    
    public static Interest deserializeInterest(String interest) {
        return null;
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
     * Deserialzes and merges knowledge into an existing knowledge base
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
    
    public SemanticTag deserializeTag(STSet targetSet, String tag) throws SharkKBException {
        // deserialize something 
        String name = "exampleName";
        String[] sis = new String[] {"http://exampleSI.org"}; 
        
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
        ASIPSerializer wS = new ASIPSerializer();
        // there is no specific set - create one
        STSet stSet = InMemoSharkKB.createInMemoSTSet();
        return wS.deserializeTag(stSet, tag);
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
        Interest interest = InMemoSharkKB.createInMemoInterest();

        
        JSONObject deserialized = new JSONObject(sharkCS);
        
        JSONArray topicsArray = deserialized.getJSONArray("topics")
        
        // read topics dimension
        String topicsSerialized = deserialized.getString("topics")

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
    
    /**
     *
     * @param sharkCS
     * @return
     * @throws SharkKBException
     */
    public static ASIPSpace deserializeSharkCS(String sharkCS) throws SharkKBException {
        ASIPSerializer wS = new ASIPSerializer();
        
        InMemoSharkKB imkb = new InMemoSharkKB();
        
        return wS.deserializeCS(imkb, sharkCS);
    }
}
