/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sharkfw.wasp;

import java.util.Enumeration;
import net.sharkfw.knowledgeBase.Interest;
import net.sharkfw.knowledgeBase.Knowledge;
import net.sharkfw.knowledgeBase.PeerSemanticTag;
import net.sharkfw.knowledgeBase.STSet;
import net.sharkfw.knowledgeBase.SemanticTag;
import net.sharkfw.knowledgeBase.SharkCS;
import net.sharkfw.knowledgeBase.SharkKBException;
import net.sharkfw.knowledgeBase.SystemPropertyHolder;
import net.sharkfw.knowledgeBase.TimeSemanticTag;
import net.sharkfw.knowledgeBase.inmemory.InMemoSharkKB;
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
    
    public static Knowledge deserializeKnowledge(String knowledge){
        return null;
    }
    
    public static SemanticTag deserializeTag(STSet targetSet, String tag) throws SharkKBException {
        // deserialize something 
        String name = "exampleName";
        String[] sis = new String[] {"http://exampleSI.org"}; 
        
        // if success - create a tag with targetSet
        return targetSet.createSemanticTag(name, sis);
    }
    
    public static SemanticTag deserializeTag(String tag) throws SharkKBException {
        // there is no specific set - create one
        STSet stSet = InMemoSharkKB.createInMemoSTSet();
        return WASPSerializer.deserializeTag(stSet, tag);
    }
    
    public static STSet deserializeSTSet(String stset){
        return null;
    }
    
    public static SystemPropertyHolder deserializeProperties(String properties){
        return null;
    }
    
    public static Enumeration<SemanticTag> deserializeRelations(String relations){
        return null;
    }
        
    public static SharkCS deserializeSharkCS(String sharkCS){
        return null;
    }
}
