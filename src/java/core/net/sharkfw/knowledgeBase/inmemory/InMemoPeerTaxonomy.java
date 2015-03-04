package net.sharkfw.knowledgeBase.inmemory;

import java.util.Enumeration;
import net.sharkfw.knowledgeBase.*;

public class InMemoPeerTaxonomy extends InMemoTaxonomy implements PeerTaxonomy, Taxonomy, STSet {

    public InMemoPeerTaxonomy(PeerSemanticNet storage) {
        super(storage);
    }
    
    public InMemoPeerTaxonomy() {
        super(new InMemoPeerSemanticNet());
    }
    
    private PeerTXSemanticTag castPST(SemanticTag st)  throws SharkKBException {
        if(st == null) return null;
        
        if(st instanceof PeerTXSemanticTag) {
            return (PeerTXSemanticTag)st;
        }
        
        throw new SharkKBException("don't like to use non PeerTXSemanticTag"
                + "into PeerTaxonomy");
                
    }
    
    @Override
    public PeerTXSemanticTag getSemanticTag(String[] sis) throws SharkKBException {
        return this.castPST(super.getSemanticTag(sis));
    }

    @Override
    public PeerTXSemanticTag getSemanticTag(String si) throws SharkKBException {
        return this.castPST(super.getSemanticTag(si));
    }

    @Override
    public PeerTaxonomy fragment(SemanticTag anchor, FragmentationParameter fp) 
            throws SharkKBException {
        
        PeerSemanticNet fragment = this.getStorage().fragment(anchor, fp);
        
        if(fragment == null) return null;
        
        return new InMemoPeerTaxonomy(fragment);
    }

    @Override
    public PeerTaxonomy fragment(SemanticTag anchor) 
            throws SharkKBException {
        
        return this.fragment(anchor, null);
    }

    @Override
    public PeerTaxonomy contextualize(Enumeration<SemanticTag> anchor) throws SharkKBException {
        PeerSemanticNet fragment = (PeerSemanticNet) this.getStorage().contextualize(anchor);
        
        if(fragment == null) return null;
        
        return new InMemoPeerTaxonomy(fragment);
    }

    @Override
    public PeerTaxonomy contextualize(STSet context) throws SharkKBException {
        return this.contextualize(context, null);
    }

    @Override
    public PeerTaxonomy contextualize(STSet context, FragmentationParameter fp) throws SharkKBException {
        PeerSemanticNet fragment = (PeerSemanticNet) this.getStorage().contextualize(context, fp);
        
        if(fragment == null) return null;
        
        return new InMemoPeerTaxonomy(fragment);
    }

    @Override
    public PeerTaxonomy contextualize(Enumeration<SemanticTag> anchor, FragmentationParameter fp) throws SharkKBException {
        PeerSemanticNet fragment = (PeerSemanticNet) this.getStorage().contextualize(anchor, fp);
        
        if(fragment == null) return null;
        
        return new InMemoPeerTaxonomy(fragment);
    }

    @Override
    public void merge(STSet source) throws SharkKBException {
        if(source instanceof InMemoPeerTaxonomy) {
            super.merge(source);
        } else {
            throw new SharkKBException("InMemoPeerTaxonomy can only merge "
                    + "another InMemoTaxonomy in this version - sorry");
        }
    }
    
    @Override
    protected PeerSemanticNet getStorage() {
        return (PeerSemanticNet) super.getStorage();
    }

    @Override
    public PeerTXSemanticTag merge(SemanticTag tag) throws SharkKBException {
        // merge in the rest but no relations
        super.merge(tag);
        
        return this.getSemanticTag(tag.getSI());
    }
    

    @Override
    public void move(PeerTXSemanticTag superPST, PeerTXSemanticTag subPST) throws SharkKBException {
        super.move(superPST, subPST);
    }

    @Override
    public PeerTaxonomy resolveSuperPeers(PeerTXSemanticTag pstGroup) 
            throws SharkKBException {
        
        FragmentationParameter fp = 
                new FragmentationParameter(false, true, Integer.MAX_VALUE);
        
        // create fragment that only contains this group and sub concepts
        PeerTaxonomy fragment = (PeerTaxonomy) this.fragment(pstGroup, fp);
        
        if(fragment == null) return null;
        
        // assume, there is no group tag
        boolean groupFound = false;
        
        do {
            groupFound = false;
            
            // if no root tag is a group we are done
            Enumeration<TXSemanticTag> rootEnum = fragment.rootTags();
            if(rootEnum != null) {
                while(rootEnum.hasMoreElements()) {
                    TXSemanticTag rootTag = rootEnum.nextElement();

                    // are their sub tags?
                    Enumeration<TXSemanticTag> tagEnum = rootTag.getSubTags();

                    // has is sub pst?
                    if(tagEnum != null && tagEnum.hasMoreElements()) {
                        /* remove it - Taxonomy implementation move sub tags a 
                        * level higher
                        */
                        fragment.removeSemanticTag(rootTag);
                    }
                }
            }
        } while(groupFound);
        
        return fragment;
    }

    @SuppressWarnings("unchecked")
    @Override
    public PeerSTSet asPeerSTSet() throws SharkKBException {
        PeerSemanticNet stSet = this.getStorage();
        
        if(stSet instanceof InMemoPeerSemanticNet) {
            InMemoPeerSemanticNet imSTSet = (InMemoPeerSemanticNet) stSet;
            return new InMemoPeerST_PeerSNWrapper(imSTSet.getTagStorage(), imSTSet);
            
        } else {
            throw new SharkKBException("cannot handle peer taxonomy as peer set");
        }
    }

    @Override
    public PeerTXSemanticTag createPeerTXSemanticTag(String name, String[] sis, String[] addresses) throws SharkKBException {
        PeerTXSemanticTag tag = this.getSemanticTag(sis);
        if(tag != null) {
            return tag;
        }
        
        tag = new InMemo_SN_TX_PeerSemanticTag(name, sis, addresses);
        this.add(tag);
        return tag;
    }

    @Override
    public PeerTXSemanticTag createPeerTXSemanticTag(String name, String si, String[] addresses) throws SharkKBException {
        return this.createPeerTXSemanticTag(name, new String[]{si}, addresses);
    }

    @Override
    public PeerTXSemanticTag createPeerTXSemanticTag(String name, String[] sis, String address) throws SharkKBException {
        return this.createPeerTXSemanticTag(name, sis, new String[]{address});
    }

    @Override
    public PeerTXSemanticTag createPeerTXSemanticTag(String name, String si, String address) throws SharkKBException {
        return this.createPeerTXSemanticTag(name, new String[]{si}, new String[]{address});
    }

    @Override
    public PeerTaxonomy contextualize(PeerSTSet context, FragmentationParameter fp) throws SharkKBException {
        if(context == null) return null;
        SemanticNet fragment = super.getStorage().contextualize(context, fp);
        
        if(fragment == null) return null;
        
        if(fragment instanceof InMemoSemanticNet) {
            PeerSemanticNet psn = new InMemoPeerSemanticNet(((InMemoSemanticNet) fragment).getTagStorage());
            return new InMemoPeerTaxonomy(psn);
        }
        
        // else
        throw new SharkKBException("current implementation only works with "
                + "InMemo implementations as parameters - contribute to "
                + "the open source project by adding better code. Thank you");
    }

    public Enumeration<PeerSemanticTag> peerTags() throws SharkKBException {
        return this.getStorage().peerTags();
    }

}
