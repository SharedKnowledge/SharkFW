package net.sharkfw.wasp;

import java.util.Enumeration;
import net.sharkfw.knowledgeBase.ContextPoint;
import net.sharkfw.knowledgeBase.InformationSpace;
import net.sharkfw.knowledgeBase.Interest;
import net.sharkfw.knowledgeBase.Knowledge;
import net.sharkfw.knowledgeBase.LASP_CS;
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
import org.json.JSONObject;

/**
 *
 * @author micha
 */
public class WASPSerializer {
    
    public static JSONObject serializeExpose(Header header, SharkCS interest) 
            throws SharkKBException {
        
        JSONObject object = new JSONObject();
        object.put("header", serializeHeader(header));
        object.put("interest", serializeInterest(interest));
        return object;
    }
    
    public static JSONObject serializeInsert(Header header, Knowledge knowledge){
        return new JSONObject();
    }
    
    public static JSONObject serializeHeader(Header header){
        return new JSONObject()
            .put("encrypted", header.isEncrypted())
            .put("encryptedSessionKey", header.getEncyptedSessionKey())
            .put("version", header.getVersion())
            .put("format", header.getFormat())
            .put("command", header.getCommand())
            .put("senderInfo", header.getSenderInfo())
            .put("signature", header.getSignature());
    }
    
    
    public static JSONObject serializeInterest(SharkCS sharkCS) throws SharkKBException{
        JSONObject object = new JSONObject();
        
        STSet topics = sharkCS.getTopics();
//        SemanticTag type = sharkCS.getType();
        STSet approvers = sharkCS.getPeers();
        STSet peers = sharkCS.getRemotePeers();
        SemanticTag originator = sharkCS.getOriginator();
        STSet locations = sharkCS.getLocations();
        STSet times = sharkCS.getTimes();
        int direction = sharkCS.getDirection();
        
        object.put("topics", WASPSerializer.serializeSTSet(topics));
        object.put("approvers", WASPSerializer.serializeSTSet(approvers));
        object.put("peers", WASPSerializer.serializeSTSet(peers));
        object.put("originator", WASPSerializer.serializeTag(originator));
        object.put("locations", WASPSerializer.serializeSTSet(locations));
        object.put("times", WASPSerializer.serializeSTSet(times));
        object.put("direction", direction);
        
        return object;
    }
    
    public static JSONObject serializeKnowledge(){
        return new JSONObject();
    }
    
    public static JSONObject serializeTag(SemanticTag tag) {
        
        JSONObject object = new JSONObject();
        
        object.put("name", tag.getName());
        
        String[] sis = tag.getSI();
        JSONArray sisArray = new JSONArray();
        for(String si : sis){
            sisArray.put(si);
        }
        object.put("sis", sisArray);
        
        // pst
        if(tag instanceof PeerSemanticTag) {
            PeerSemanticTag pst = (PeerSemanticTag) tag;
            
            String[] addresses = pst.getAddresses();
            JSONArray addrArray = new JSONArray();
            for(String addr : addresses){
                addrArray.put(addr);
            }
            object.put("peer_semantic_tags", addrArray);
        }

        // tst
        if(tag instanceof TimeSemanticTag) {
            TimeSemanticTag tst = (TimeSemanticTag) tag;
            object.put("time_from", tst.getFrom());
            object.put("time_duration", tst.getDuration());
        }
        
        // properties
//        String serializedProperties = this.serializeProperties(tag);
//        if(serializedProperties != null) {
//            object.append("prooperties", serializedProperties);
//        }
        
        return object;
    }
    
    public static JSONArray serializeSTSet(STSet stset) throws SharkKBException{
        
        if(stset == null){
            return null;
        }
        
        JSONArray set = new JSONArray();
        
        Enumeration<SemanticTag> tags = stset.tags();
        
        while(tags.hasMoreElements()) {
            set.put(WASPSerializer.serializeTag(tags.nextElement()));
        }
        
        return set;
    }
    
    public static JSONObject serializeProperties(SystemPropertyHolder target){
        return new JSONObject();
    }
    
    public static JSONObject serializeRelations(Enumeration<SemanticTag> tagEnum){
        return new JSONObject();
    }
        
    public static JSONObject serializeSharkCS(SharkCS sharkCS) throws SharkKBException {
        return new JSONObject();
    }
    
    public static Header deserializeHeader(String header){
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
        WASPSerializer wS = new WASPSerializer();
        
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
        LASP_CS cs = this.deserializeCS(target, knowledgeString);
        
        // could add cs to knowledge base
        InformationSpace infoSpace = target.createContextSpace(cs);
        
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
        WASPSerializer wS = new WASPSerializer();
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
        
    public LASP_CS deserializeCS(SharkKB kb, String sharkCS) throws SharkKBException {
        Interest interest = InMemoSharkKB.createInMemoInterest();

        // read topics dimension
        String topicsSerialized = "topics dim as JSON String"; // muss man aus dem sharkCSString füllen

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
    public static LASP_CS deserializeSharkCS(String sharkCS) throws SharkKBException {
        WASPSerializer wS = new WASPSerializer();
        
        InMemoSharkKB imkb = new InMemoSharkKB();
        
        return wS.deserializeCS(imkb, sharkCS);
    }
}
