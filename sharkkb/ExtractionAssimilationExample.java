package sharkkb;

import net.sharkfw.knowledgeBase.ContextCoordinates;
import net.sharkfw.knowledgeBase.ContextPoint;
import net.sharkfw.knowledgeBase.FragmentationParameter;
import net.sharkfw.knowledgeBase.Interest;
import net.sharkfw.knowledgeBase.Knowledge;
import net.sharkfw.knowledgeBase.PeerSemanticTag;
import net.sharkfw.knowledgeBase.PeerTXSemanticTag;
import net.sharkfw.knowledgeBase.PeerTaxonomy;
import net.sharkfw.knowledgeBase.STSet;
import net.sharkfw.knowledgeBase.SemanticTag;
import net.sharkfw.knowledgeBase.SharkCS;
import net.sharkfw.knowledgeBase.SharkCSAlgebra;
import net.sharkfw.knowledgeBase.SharkKB;
import net.sharkfw.knowledgeBase.SharkKBException;
import net.sharkfw.knowledgeBase.TXSemanticTag;
import net.sharkfw.knowledgeBase.Taxonomy;
import net.sharkfw.knowledgeBase.inmemory.InMemoSharkKB;
import net.sharkfw.system.L;

/**
 *
 * @author thsc
 */
public class ExtractionAssimilationExample {
    SharkKB kb = null;
    
    public static void main(String args[]) throws SharkKBException {
        ExtractionAssimilationExample e = new ExtractionAssimilationExample();
        e.example1();
        e.example2();
        e.example3();
        e.example4();
        e.example5();
        e.example6();
        e.example7();
        e.example8();
    }
    
    ExtractionAssimilationExample() throws SharkKBException {
        this.kb = new InMemoSharkKB();
        
        PeerSemanticTag alice = 
                this.kb.createPeerSemanticTag("Alice", 
                        "http://www.sharksystem.net/alice.html", 
                        "mail://alice@wonderland.net");
        
        // she owns that kb
        this.kb.setOwner(alice);
        
        // create background knowledge
        Taxonomy tx = this.kb.getTopicsAsTaxonomy();
        
        // describe programming languages and java as part of a taxonomy
        TXSemanticTag pl = tx.createTXSemanticTag("PL", "http://en.wikipedia.org/wiki/Programming_language");
        TXSemanticTag java = tx.createTXSemanticTag("Java", "http://en.wikipedia.org/wiki/Java_%28programming_language%29");
        
        // move java "under" pl
        java.move(pl);
        // create two coordinates to fill that kb
        ContextCoordinates ccPL = this.kb.createContextCoordinates(pl, alice, alice, null, null, null, SharkCS.DIRECTION_INOUT);
        ContextCoordinates ccJava = this.kb.createContextCoordinates(java, alice, alice, null, null, null, SharkCS.DIRECTION_INOUT);
        
        ContextPoint cpPL = this.kb.createContextPoint(ccPL);
        ContextPoint cpJava = this.kb.createContextPoint(ccJava);
        
        cpPL.addInformation("something about programming languages");
        cpJava.addInformation("something about java");
        
        System.out.println("kb after initialization: ");
        System.out.println(L.kb2String(this.kb));
    }
    
    public void example1() throws SharkKBException {
        SemanticTag plST = InMemoSharkKB.createInMemoSemanticTag("PL", "http://en.wikipedia.org/wiki/Programming_language");
        
        ContextCoordinates cc = InMemoSharkKB.createInMemoContextCoordinates(plST, null, null, null, null, null, SharkCS.DIRECTION_INOUT);
        Knowledge k = SharkCSAlgebra.extract(kb, cc);
        
        System.out.println("---------------------------------");
        System.out.println("example 1 - extracted knowledge:");
        System.out.println("---------------------------------");
        System.out.println(L.knowledge2String(k));
    }
    
    public void example2() throws SharkKBException {
        PeerSemanticTag aliceTag = InMemoSharkKB.createInMemoPeerSemanticTag("A", "http://www.sharksystem.net/alice.html", null);
        
        ContextCoordinates cc = InMemoSharkKB.createInMemoContextCoordinates(null, aliceTag, null, null, null, null, SharkCS.DIRECTION_INOUT);
        Knowledge k = SharkCSAlgebra.extract(kb, cc);
        
        System.out.println("---------------------------------");
        System.out.println("example 2 - extracted knowledge:");
        System.out.println("---------------------------------");
        System.out.println(L.knowledge2String(k));
    }
    
    public void example3() throws SharkKBException {
        PeerSemanticTag aliceTag = InMemoSharkKB.createInMemoPeerSemanticTag("A", "http://www.sharksystem.net/alice.html", null);
        
        ContextCoordinates cc = InMemoSharkKB.createInMemoContextCoordinates(null, null, aliceTag, null, null, null, SharkCS.DIRECTION_INOUT);
        Knowledge k = SharkCSAlgebra.extract(kb, cc);
        
        System.out.println("---------------------------------");
        System.out.println("example 3 - extracted knowledge:");
        System.out.println("---------------------------------");
        System.out.println(L.knowledge2String(k));
    }

    public void example4() throws SharkKBException {
        SemanticTag plST = InMemoSharkKB.createInMemoSemanticTag("PL", "http://en.wikipedia.org/wiki/Programming_language");
        
        FragmentationParameter[] fps = new FragmentationParameter[SharkCS.MAXDIMENSIONS];
        FragmentationParameter oneSubFP = new FragmentationParameter(false, true, 1);
        
        for(int d = 0; d < SharkCS.MAXDIMENSIONS; d++) {
           fps[d] = FragmentationParameter.getZeroFP();
        }

        fps[SharkCS.DIM_TOPIC] = oneSubFP;
        
        ContextCoordinates cc = InMemoSharkKB.createInMemoContextCoordinates(plST, null, null, null, null, null, SharkCS.DIRECTION_INOUT);
        Knowledge k = SharkCSAlgebra.extract(kb, cc, fps);
        
        System.out.println("---------------------------------");
        System.out.println("example 4 - extracted knowledge:");
        System.out.println("---------------------------------");
        System.out.println(L.knowledge2String(k));
    }

    public void example5() throws SharkKBException {
        // add another cp
        Taxonomy tTax = this.kb.getTopicsAsTaxonomy();
        TXSemanticTag plTag = tTax.getSemanticTag("http://en.wikipedia.org/wiki/Programming_language");
        
        TXSemanticTag csharpST = tTax.createTXSemanticTag("c#", "http://cshark.com");
        csharpST.move(plTag);
        
        ContextCoordinates ccCSharp = this.kb.createContextCoordinates(csharpST, null, null, null, null, null, SharkCS.DIRECTION_INOUT);
        ContextPoint cp = this.kb.createContextPoint(ccCSharp);
        cp.addInformation("information about c#");
        
        SemanticTag javaST = InMemoSharkKB.createInMemoSemanticTag("Java", "http://en.wikipedia.org/wiki/Java_%28programming_language%29");
        
        FragmentationParameter[] backgroundFPs = new FragmentationParameter[SharkCS.MAXDIMENSIONS];
        
        for(int d = 0; d < SharkCS.MAXDIMENSIONS; d++) {
           backgroundFPs[d] = FragmentationParameter.getZeroFP();
        }

        backgroundFPs[SharkCS.DIM_TOPIC] = new FragmentationParameter(true, true, 2);
        
        ContextCoordinates cc = InMemoSharkKB.createInMemoContextCoordinates(javaST, null, null, null, null, null, SharkCS.DIRECTION_INOUT);
        Knowledge k = SharkCSAlgebra.extract(kb, cc, backgroundFPs);
        
        System.out.println("---------------------------------");
        System.out.println("example 5 - extracted knowledge:");
        System.out.println("---------------------------------");
        System.out.println(L.knowledge2String(k));
    }
    
    public void example6() throws SharkKBException {
        // add another cp
        PeerTaxonomy pTax = this.kb.getPeersAsTaxonomy();
        
        TXSemanticTag alice = pTax.getSemanticTag("http://www.sharksystem.net/alice.html");
        
        PeerTXSemanticTag group = pTax.createPeerTXSemanticTag("Group of Alice", "http://aGroup.org", (String)null);
        
        alice.move(group);
        
        ContextCoordinates groupCC = this.kb.createContextCoordinates(null, null, group, null, null, null, SharkCS.DIRECTION_INOUT);
        ContextPoint cp = this.kb.createContextPoint(groupCC);
        cp.addInformation("information from group");
        
        FragmentationParameter[] backgroundFPs = new FragmentationParameter[SharkCS.MAXDIMENSIONS];
        
        for(int d = 0; d < SharkCS.MAXDIMENSIONS; d++) {
           backgroundFPs[d] = FragmentationParameter.getZeroFP();
        }

        backgroundFPs[SharkCS.DIM_PEER] = new FragmentationParameter(false, true, 1);
        
        Knowledge k = SharkCSAlgebra.extract(kb, groupCC, backgroundFPs);
        
        System.out.println("---------------------------------");
        System.out.println("example 6 - extracted knowledge:");
        System.out.println("---------------------------------");
        System.out.println(L.knowledge2String(k));
    }
    
    public void example7() throws SharkKBException {
        PeerSemanticTag alice = this.kb.getPeerSTSet().getSemanticTag("http://www.sharksystem.net/alice.html");
        ContextCoordinates cc = this.kb.createContextCoordinates(null, null, alice, null, null, null, SharkCS.DIRECTION_INOUT);
        FragmentationParameter[] backgroundFPs = new FragmentationParameter[SharkCS.MAXDIMENSIONS];
        
        for(int d = 0; d < SharkCS.MAXDIMENSIONS; d++) {
           backgroundFPs[d] = FragmentationParameter.getZeroFP();
        }

        backgroundFPs[SharkCS.DIM_PEER] = new FragmentationParameter(true, true, 1);
        Knowledge k = SharkCSAlgebra.extract(kb, cc, backgroundFPs, alice);
        
        System.out.println("---------------------------------");
        System.out.println("example 7 - extracted knowledge:");
        System.out.println("---------------------------------");
        System.out.println(L.knowledge2String(k));
    }
    
    public void example8() throws SharkKBException {
        PeerSemanticTag alice = this.kb.getPeerSTSet().getSemanticTag("http://www.sharksystem.net/alice.html");
        ContextCoordinates cc = this.kb.createContextCoordinates(null, null, alice, null, null, null, SharkCS.DIRECTION_INOUT);
        FragmentationParameter[] backgroundFPs = new FragmentationParameter[SharkCS.MAXDIMENSIONS];
        
        for(int d = 0; d < SharkCS.MAXDIMENSIONS; d++) {
           backgroundFPs[d] = FragmentationParameter.getZeroFP();
        }

        backgroundFPs[SharkCS.DIM_PEER] = new FragmentationParameter(true, true, 1);
        Knowledge k = SharkCSAlgebra.extract(kb, cc, backgroundFPs, alice);
        
        
        STSet t = InMemoSharkKB.createInMemoSTSet();
        t.createSemanticTag(null, "http://en.wikipedia.org/wiki/Java_%28programming_language%29");
        
        Interest i = InMemoSharkKB.createInMemoInterest(t, null, null, null, null, null, SharkCS.DIRECTION_INOUT);

        SharkKB kb1 = new InMemoSharkKB();
        SharkCSAlgebra.assimilate(kb1, i, backgroundFPs, k, true /*learn*/, false/* remove cp*/);
        
        System.out.println("---------------------------------");
        System.out.println("example 8 - knowledge after assimilation kb1:");
        System.out.println("---------------------------------");
        System.out.println(L.knowledge2String(k));
        
        System.out.println("---------------------------------");
        System.out.println("example 8 - kb1 after assimilation:");
        System.out.println("---------------------------------");
        System.out.println(L.kb2String(kb));
        
        SharkKB kb2 = new InMemoSharkKB();
        // kb2 only knows about pl
        kb2.createSemanticTag("PL", "http://en.wikipedia.org/wiki/Java_%28programming_language%29");
        
        SharkCSAlgebra.assimilate(kb2, i, backgroundFPs, k, false /*learn*/, true/* remove cp*/);
        System.out.println("---------------------------------");
        System.out.println("example 8 - knowledge after assimilation kb2:");
        System.out.println("---------------------------------");
        System.out.println(L.knowledge2String(k));
        
        System.out.println("---------------------------------");
        System.out.println("example 8 - kb2 after assimilation:");
        System.out.println("---------------------------------");
        System.out.println(L.kb2String(kb2));
    }
}
