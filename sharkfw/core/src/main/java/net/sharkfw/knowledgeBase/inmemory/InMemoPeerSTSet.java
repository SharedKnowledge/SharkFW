package net.sharkfw.knowledgeBase.inmemory;

import java.util.Enumeration;
import net.sharkfw.knowledgeBase.*;

/**
 *
 * @author thsc
 */
public class InMemoPeerSTSet extends InMemoSTSet implements PeerSTSet {
    
    private PeerSemanticTag castPST(SemanticTag st)  throws SharkKBException {
        if(st == null) return null;

        if(st instanceof PeerSemanticTag) {
            return (PeerSemanticTag)st;
        }
        
        throw new SharkKBException("don't like to use non PeerSemanticTag"
                + "in PeerSTSet");
    }
    
    public InMemoPeerSTSet() {
        super(new InMemoGenericTagStorage<PeerSemanticTag>());
    }
    
    InMemoPeerSTSet(InMemoGenericTagStorage<PeerSemanticTag> tagStorage) {
        super(tagStorage);
    }
    
    @Override
    public PeerSemanticTag getSemanticTag(String[] sis) throws SharkKBException {
        return this.castPST(super.getSemanticTag(sis));
    }

    @Override
    public PeerSemanticTag getSemanticTag(String si) throws SharkKBException {
        return this.castPST(super.getSemanticTag(si));
    }

    @Override
    public PeerSTSet fragment(SemanticTag anchor) throws SharkKBException {
        PeerSTSet fragment = new InMemoPeerSTSet();
        return (PeerSTSet) SharkCSAlgebra.fragment(fragment, this, anchor);
    }
    
    @Override
    public PeerSTSet fragment(SemanticTag anchor, FragmentationParameter fp) throws SharkKBException {
        return this.fragment(anchor);
    }

    @Override
    public PeerSTSet contextualize(Enumeration<SemanticTag> anchor, FragmentationParameter fp) throws SharkKBException {
        return this.contextualize(anchor);
    }

    @Override
    public PeerSTSet contextualize(Enumeration<SemanticTag> anchor) throws SharkKBException {
        PeerSTSet fragment = new InMemoPeerSTSet();
        
        return (PeerSTSet) SharkCSAlgebra.contextualize(fragment, this, anchor);
        
    }

    @Override
    public PeerSTSet contextualize(STSet context) throws SharkKBException {
        return this.contextualize(context.tags());
    }

    @Override
    public PeerSTSet contextualize(STSet context, FragmentationParameter fp) throws SharkKBException {
        return this.contextualize(context.tags());
    }

    @Override
    public PeerSemanticTag createPeerSemanticTag(String name, String[] sis, String[] addresses) throws SharkKBException {
        PeerSemanticTag pst = this.getSemanticTag(sis);
        if(pst != null) {
            return pst;
        }
                
        pst = new InMemo_SN_TX_PeerSemanticTag(name, sis, addresses);
        
        this.add(pst);
        
        return pst;
    }

    @Override
    public PeerSemanticTag createPeerSemanticTag(String name, String[] sis, String address) throws SharkKBException {
        return this.createPeerSemanticTag(name, sis, new String[]{address});
    }

    @Override
    public PeerSemanticTag createPeerSemanticTag(String name, String si, String[] addresses) throws SharkKBException {
        return this.createPeerSemanticTag(name, new String[]{si}, addresses);
    }

    @Override
    public PeerSemanticTag createPeerSemanticTag(String name, String si, String address) throws SharkKBException {
        return this.createPeerSemanticTag(name, new String[]{si},  new String[]{address});
    }

    @Override
    public Enumeration<PeerSemanticTag> peerTags() {
        Enumeration tags = super.tags();
        
        return (Enumeration<PeerSemanticTag>) tags;
    }
    
}
