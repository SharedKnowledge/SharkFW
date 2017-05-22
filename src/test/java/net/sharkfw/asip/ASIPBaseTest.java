package net.sharkfw.asip;

import net.sharkfw.knowledgeBase.*;
import net.sharkfw.knowledgeBase.inmemory.InMemoSharkKB;
import net.sharkfw.peer.J2SESharkEngine;
import net.sharkfw.peer.SharkEngine;
import net.sharkfw.system.L;
import org.junit.After;
import org.junit.Before;

/**
 * Created by j4rvis on 24.03.16.
 */
public class ASIPBaseTest {

    SharkEngine engine;
    TestConnection connection;

    PeerSemanticTag sender;
    PeerSemanticTag receiverPeer;
    SpatialSemanticTag receiverSpatial;
    TimeSemanticTag receiverTime;

    SemanticNet topicNet;
    SemanticNet typeNet;
    PeerTaxonomy peerTax;

    String[] sis;
    String[] addresses;

    PeerSTSet peers;
    SpatialSTSet locations;
    TimeSTSet times;

    public STSet topics;
    public STSet types;


    @Before
    public void setUp() throws Exception {
        engine = new J2SESharkEngine();
        connection = new TestConnection();

        L.setLogLevel(L.LOGLEVEL_ALL);

        sis = new String[]{"www.test.de", "www.test1.de"};
        addresses = new String[]{"tcp://test.de", "tcp://test1.de"};

        peers = InMemoSharkKB.createInMemoPeerSTSet();

        sender = peers.createPeerSemanticTag("SENDER", "www.si1.de", "tcp://addr1.de");
        receiverPeer = peers.createPeerSemanticTag("RECEIEVER", "www.si2.de", "tcp://addr2.de");

        topics = InMemoSharkKB.createInMemoSTSet();
        topics.createSemanticTag("Topcic1", "www.topic1.de");
        topics.createSemanticTag("Topcic2", "www.topic2.de");

        types = InMemoSharkKB.createInMemoSTSet();
        types.createSemanticTag("Types1", "www.types1.de");
        types.createSemanticTag("Types2", "www.types2.de");

        topicNet = InMemoSharkKB.createInMemoSemanticNet();
        SNSemanticTag topicTag1 = topicNet.createSemanticTag("Topcic1", "www.topic1.de");
        SNSemanticTag topicTag2 = topicNet.createSemanticTag("Topcic2", "www.topic2.de");
        topicNet.setPredicate(topicTag1, topicTag2, "pairs");

        typeNet = InMemoSharkKB.createInMemoSemanticNet();
        SNSemanticTag typeTag1 = typeNet.createSemanticTag("Types1", "www.types1.de");
        SNSemanticTag typeTag2 = typeNet.createSemanticTag("Types2", "www.types2.de");
        typeNet.setPredicate(typeTag1, typeTag2, "pairs");

        peerTax = InMemoSharkKB.createInMemoPeerTaxonomy();
        peerTax.createPeerTXSemanticTag("SENDER", "www.si1.de", "tcp://addr1.de");
    }

    @After
    public void tearDown() throws Exception {
    }

}
