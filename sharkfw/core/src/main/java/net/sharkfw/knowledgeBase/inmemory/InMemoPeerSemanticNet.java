package net.sharkfw.knowledgeBase.inmemory;

import java.util.Enumeration;
import net.sharkfw.knowledgeBase.*;

/**
 *
 * @author thsc
 */
public class InMemoPeerSemanticNet extends InMemoSemanticNet implements PeerSemanticNet {
    
    public InMemoPeerSemanticNet() {
        super(new InMemoGenericTagStorage<PeerSemanticTag>());
    }
    
    public InMemoPeerSemanticNet(InMemoGenericTagStorage storage) {    
        super(storage);
    }
    
    @Override
    public PeerSTSet asPeerSTSet() {
        return new InMemoPeerST_PeerSNWrapper(this.getTagStorage(), this);
    }

    @Override
    public PeerSNSemanticTag createSemanticTag(String name, String[] sis, String[] addresses) throws SharkKBException {
        
        PeerSNSemanticTag pst = this.getSemanticTag(sis);
        if(pst != null) {
            return pst;
        }
        
        pst = new InMemo_SN_TX_PeerSemanticTag(name, sis, addresses);
        
        this.add(pst);
        
        return pst;
    }

    @Override
    public PeerSNSemanticTag createSemanticTag(String name, String si, String[] addresses) throws SharkKBException {
        return this.createSemanticTag(name, new String[] {si}, addresses);
    }

    @Override
    public PeerSNSemanticTag createSemanticTag(String name, String si, String address) throws SharkKBException {
        return this.createSemanticTag(name, new String[] {si}, new String[] {address});
    }

    @Override
    public PeerSNSemanticTag createSemanticTag(String name, String[] sis, String address) throws SharkKBException {
        return this.createSemanticTag(name, sis, new String[] {address});
    }

    @Override
    public PeerSNSemanticTag getSemanticTag(String[] sis) throws SharkKBException {
        return (PeerSNSemanticTag) super.getSemanticTag(sis);
    }

    @Override
    public PeerSNSemanticTag getSemanticTag(String si) throws SharkKBException {
        return (PeerSNSemanticTag) super.getSemanticTag(si);
    }

    @Override
    public PeerSemanticNet fragment(SemanticTag anchor, FragmentationParameter fp) 
            throws SharkKBException {
        
        PeerSemanticNet fragment = new InMemoPeerSemanticNet();
        SharkCSAlgebra.fragment(fragment, anchor, this, 
                fp.getAllowedPredicates(), 
                fp.getForbiddenPredicates(), fp.getDepth());
        
        return fragment;
    }
    
    @Override
    public PeerSNSemanticTag merge(SemanticTag tag) throws SharkKBException {
        return (PeerSNSemanticTag) super.merge(tag);
    }

    public Enumeration<PeerSemanticTag> peerTags() throws SharkKBException {
        Enumeration tags = this.getTagStorage().tags();
        return (Enumeration<PeerSemanticTag>) tags;
    }
}
