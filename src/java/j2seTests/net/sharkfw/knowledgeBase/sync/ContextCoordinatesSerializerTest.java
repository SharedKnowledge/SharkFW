/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sharkfw.knowledgeBase.sync;

import java.util.ArrayList;
import java.util.List;
import net.sharkfw.knowledgeBase.ContextCoordinates;
import net.sharkfw.knowledgeBase.PeerSemanticTag;
import net.sharkfw.knowledgeBase.SemanticTag;
import net.sharkfw.knowledgeBase.SharkCS;
import net.sharkfw.knowledgeBase.SharkKBException;
import net.sharkfw.knowledgeBase.inmemory.InMemoSharkKB;
import net.sharkfw.system.L;
import net.sharkfw.system.SharkException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author s0539710
 */
public class ContextCoordinatesSerializerTest {
    
    private SyncKB syncKB;
    private SemanticTag st1, st2;
    private PeerSemanticTag pst;
    private List<SyncContextPoint> cps;
    
    
    @Before
    public void setUp() throws SharkKBException {
        syncKB = new SyncKB(new InMemoSharkKB());
        st1 = syncKB.createSemanticTag("Teapots", "www.teapots.org");
        st1 = syncKB.createSemanticTag("Noodles", "www.noodles.org");
        pst = syncKB.createPeerSemanticTag("Alice", "www.alice.org", "mail@alice.org");
        
        cps = new ArrayList<>();
        ContextCoordinates cc1 = syncKB.createContextCoordinates(st1, pst, pst, pst, null, null, SharkCS.DIRECTION_INOUT);
        ContextCoordinates cc2 = syncKB.createContextCoordinates(st2, pst, pst, pst, null, null, SharkCS.DIRECTION_INOUT);
        cps.add(new SyncContextPoint(syncKB.createContextPoint(cc1)));
        cps.add(new SyncContextPoint(syncKB.createContextPoint(cc2)));
    }
    
    @Test
    public void serializeCC_correctlySerialized() throws SharkKBException{
        // Dont ask
        String expected = "<cc_list><cc_item><cc_coordinates><cs><topics><stset><tags><tag><name>Noodles</name><si>www.noodles.org</si><props/></tag></tags></stset></topics><originator><tag><name>Alice</name><si>www.alice.org</si><addr>mail@alice.org</addr><props/></tag></originator><peer><stset><tags><tag><name>Alice</name><si>www.alice.org</si><addr>mail@alice.org</addr><props/></tag></tags></stset></peer><remotePeer><stset><tags><tag><name>Alice</name><si>www.alice.org</si><addr>mail@alice.org</addr><props/></tag></tags></stset></remotePeer><direction>2</direction></cs></cc_coordinates><cc_version>1</cc_version></cc_item><cc_item><cc_coordinates><cs><originator><tag><name>Alice</name><si>www.alice.org</si><addr>mail@alice.org</addr><props/></tag></originator><peer><stset><tags><tag><name>Alice</name><si>www.alice.org</si><addr>mail@alice.org</addr><props/></tag></tags></stset></peer><remotePeer><stset><tags><tag><name>Alice</name><si>www.alice.org</si><addr>mail@alice.org</addr><props/></tag></tags></stset></remotePeer><direction>2</direction></cs></cc_coordinates><cc_version>1</cc_version></cc_item></cc_list>";
        String result = ContextCoordinatesSerializer.serializeContextCoordinatesList(cps);
        Assert.assertEquals(expected, result);
    }
    
    @Test
    public void deserializeCC_correctlyDeserialized() throws SharkException {
        List<SyncContextPoint> result = ContextCoordinatesSerializer.deserializeContextCoordinatesList("<cc_list><cc_item><cc_coordinates><cs><topics><stset><tags><tag><name>Noodles</name><si>www.noodles.org</si><props/></tag></tags></stset></topics><originator><tag><name>Alice</name><si>www.alice.org</si><addr>mail@alice.org</addr><props/></tag></originator><peer><stset><tags><tag><name>Alice</name><si>www.alice.org</si><addr>mail@alice.org</addr><props/></tag></tags></stset></peer><remotePeer><stset><tags><tag><name>Alice</name><si>www.alice.org</si><addr>mail@alice.org</addr><props/></tag></tags></stset></remotePeer><direction>2</direction></cs></cc_coordinates><cc_version>1</cc_version></cc_item><cc_item><cc_coordinates><cs><originator><tag><name>Alice</name><si>www.alice.org</si><addr>mail@alice.org</addr><props/></tag></originator><peer><stset><tags><tag><name>Alice</name><si>www.alice.org</si><addr>mail@alice.org</addr><props/></tag></tags></stset></peer><remotePeer><stset><tags><tag><name>Alice</name><si>www.alice.org</si><addr>mail@alice.org</addr><props/></tag></tags></stset></remotePeer><direction>2</direction></cs></cc_coordinates><cc_version>1</cc_version></cc_item></cc_list>");
        Assert.assertEquals(cps, result);
    }
    
    @Test(expected = SharkException.class)
    public void deserializeCC_malFormattedXML_exceptionThrown() throws SharkException {
        String s = "<cc_list><cc_item><cc_coordinates><cs><topics><stset><tags><tag><name>Noodles</name><si>www.noodles.org</si><props/></tag></tags></stset></topics><originator><tag><name>Alice</name><si>www.alice.org</si><addr>mail@alice.org</addr><props/></tag></originator><peer><stset><tags><tag><name>Alice</name><si>www.alice.org</si><addr>mail@alice.org</addr><props/></tag></tags></stset></peer><remotePeer><stset><tags><tag><name>Alice</name><si>www.alice.org</si><addr>mail@alice.org</addr><props/></tag></tags></stset></remotePeer><direction>2</direction></cs></cc_coordinates><cc_version>1</cc_version><cc_item><cc_coordinates><cs><originator><tag><name>Alice</name><si>www.alice.org</si><addr>mail@alice.org</addr><props/></tag></originator><peer><stset><tags><tag><name>Alice</name><si>www.alice.org</si><addr>mail@alice.org</addr><props/></tag></tags></stset></peer><remotePeer><stset><tags><tag><name>Alice</name><si>www.alice.org</si><addr>mail@alice.org</addr><props/></tag></tags></stset></remotePeer><direction>2</direction></cs></cc_coordinates><cc_version>1</cc_version></cc_item></cc_list>";
        ContextCoordinatesSerializer.deserializeContextCoordinatesList(s);
    }
}
