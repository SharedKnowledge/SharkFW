package net.sharkfw.apirev1;

import net.sharkfw.knowledgeBase.*;
import net.sharkfw.knowledgeBase.inmemory.InMemoSharkKB;

/**
 *
 * @author thsc
 */
public class TestData {
    
    public static final String INFO_1_CONTENT = "Information1";
    public static final String TOPIC1_SI = "http://topic1.de";
    public static final String PEER1_SI = "http://peer1.de";
    
    /**
     * creates a simple kb with just a single context point
     * Topic1, Peer1 (no valid mail address, just a dummy TCP-address) - rest is empty
     * @return 
     */
    public static SharkKB createKB1() throws SharkKBException {
        SharkKB kb = new InMemoSharkKB();
        
        SemanticTag topic1 = kb.getTopicSTSet().createSemanticTag("Topic1", TOPIC1_SI);
        PeerSemanticTag peer1 = kb.getPeerSTSet().createPeerSemanticTag("Peer1", PEER1_SI, "tcp://localhost:5555");
        
        ContextCoordinates cc = kb.createContextCoordinates(topic1, peer1, peer1, null, null, null, SharkCS.DIRECTION_INOUT);
        
        ContextPoint cp = kb.createContextPoint(cc);
        
        cp.addInformation(TestData.INFO_1_CONTENT);
        
        return kb;
    }
}
