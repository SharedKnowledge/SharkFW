package net.sharkfw.knowledgeBase.inmemory;

import java.util.Enumeration;
import net.sharkfw.knowledgeBase.*;

/**
 * This class is like conjoined twins. Tags are stored in a generic tag storage.
 * Getter and setter are performed by peerSTSet implementation. Contextualization and
 * fragmentation is re-routed to semantic net implementation, though. It takes
 * relations into account.
 * @author thsc
 */
class InMemoPeerST_PeerSNWrapper extends InMemoPeerSTSet {

    private final InMemoSemanticNet psn;
    
    InMemoPeerST_PeerSNWrapper(InMemoGenericTagStorage<PeerSemanticTag> tagStorage,
            InMemoSemanticNet psn) {
        
        super(tagStorage);
        
        this.psn = psn;
        
    }

    /* re - route fragmentation / contextualization calls to semantic net
    /* which takes relations into account
    * 
    */
    
    private PeerSTSet wrap(SemanticNet sn) throws SharkKBException {
        if(sn instanceof InMemoSemanticNet) {
            InMemoSemanticNet imsn = (InMemoSemanticNet) sn;
            return new InMemoPeerST_PeerSNWrapper(imsn.getTagStorage(), imsn);
        } else {
            throw new SharkKBException("cannot handle non PeerSTSet as PeerSTSet");
        }
    }
    
    @Override
    public PeerSTSet contextualize(STSet ctx, FragmentationParameter fp) throws SharkKBException {
        return this.wrap(this.psn.contextualize(ctx, fp));
    }

    @Override
    public PeerSTSet contextualize(STSet ctx) throws SharkKBException {
        return this.wrap(this.psn.contextualize(ctx));
    }

    @Override
    public PeerSTSet contextualize(Enumeration<SemanticTag> tagEnum) throws SharkKBException {
        return this.wrap(this.psn.contextualize(tagEnum));
    }

    @Override
    public PeerSTSet contextualize(Enumeration<SemanticTag> tagEnum, FragmentationParameter fp) throws SharkKBException {
        return this.wrap(this.psn.contextualize(tagEnum, fp));
    }

    @Override
    public PeerSTSet fragment(SemanticTag anchor, FragmentationParameter fp) throws SharkKBException {
        return this.wrap(this.psn.fragment(anchor, fp));
    }

    @Override
    public PeerSTSet fragment(SemanticTag anchor) throws SharkKBException {
        return this.wrap(this.psn.fragment(anchor));
    }
}
