package net.sharkfw.knowledgeBase.inmemory;

import java.util.Enumeration;
import java.util.HashSet;
import net.sharkfw.knowledgeBase.*;
import net.sharkfw.system.Iterator2Enumeration;

/**
 * Follow delegate pattern
 * @author thsc
 */
public class InMemoTaxonomy implements Taxonomy, STSet {
    
    private SemanticNet storage;
    
    protected InMemoTaxonomy(SemanticNet storage) {
        this.storage = storage;
    }
    
    public InMemoTaxonomy() {
        this(new InMemoGenericTagStorage<PeerSemanticTag>());
    }
    
    InMemoTaxonomy(InMemoGenericTagStorage storage) {    
        this.storage = new InMemoSemanticNet(storage);
    }
    
    protected void add(TXSemanticTag tag) throws SharkKBException {
        this.storage.add(tag);
    }
    
    @Override
    public void merge(TXSemanticTag tag) throws SharkKBException {
            this.storage.merge(tag);
    }

    @Override
    public TXSemanticTag createTXSemanticTag(String name, String[] sis) throws SharkKBException {
        
        TXSemanticTag st = this.getSemanticTag(sis);
        if(st != null) {
            return st;
        }
        
        st = new InMemo_SN_TX_SemanticTag(name, sis);
        this.storage.add(st);
        
        return st;
    }

    @Override
    public TXSemanticTag createTXSemanticTag(String name, String si) throws SharkKBException {
        return this.createTXSemanticTag(name, new String[] {si});
    }

    @Override
    public TXSemanticTag createSemanticTag(TXSemanticTag superTag, String name, String[] sis) throws SharkKBException {
        TXSemanticTag txst = (TXSemanticTag) this.createTXSemanticTag(name, sis);
        
        txst.move(superTag);
        
        return txst;
    }

    @Override
    public void move(TXSemanticTag superTag, TXSemanticTag subTag) throws SharkKBException {
        subTag.move(superTag);
    }

    /**
     * 
     * @param tag new super tag - if null - this tag becomes root tag
     * @throws SharkKBException 
     */
    @Override
    public void removeSemanticTag(TXSemanticTag tag) throws SharkKBException {
        /* subtags are removed in semantic net implementation
         * But maybe there is a super tag that should become new supertag of
         * the subs.
         */
        
        SNSemanticTag snTag = this.storage.getSemanticTag(tag.getSI());
        
        if(snTag == null) return;
        
        // is there a super tag
        Enumeration<SNSemanticTag> superTagEnum = 
                snTag.targetTags(SemanticNet.SUPERTAG);

        SNSemanticTag superTag = null;
        
        if(superTagEnum != null) {
            if(superTagEnum.hasMoreElements()) {
                    superTag = superTagEnum.nextElement();
            }
        }
        
        // there is a super tag
        if(superTag != null) {
            Enumeration<SNSemanticTag> subTagEnum = 
                    snTag.sourceTags(SemanticNet.SUPERTAG);
            
            // first - tell super tag about removing
            snTag.removePredicate(SemanticNet.SUPERTAG, superTag);

            if(subTagEnum != null && subTagEnum.hasMoreElements()) {
                // is has sub tags

                // in any case - substitute super tag predicate in sub tags
                while(subTagEnum.hasMoreElements()) {
                    SNSemanticTag subTag = subTagEnum.nextElement();

                    // tell subtag about removal
                    subTag.removePredicate(SemanticNet.SUPERTAG, snTag);
                    
                    // tell sub tags new super tag
                    if(superTag != null) {
                        subTag.setPredicate(SemanticNet.SUPERTAG, superTag);
                    }
                }
            }
        }
        
        this.storage.removeSemanticTag((SemanticTag) tag);
    }
    
    @Override
    public void removeSemanticTag(SemanticTag tag) throws SharkKBException {
        // this might look odd but it's necessary.
        if(tag instanceof InMemo_SN_TX_SemanticTag) {
            this.removeSemanticTag((TXSemanticTag) tag);
        } else {
            this.storage.removeSemanticTag(tag);
        }
    }

    @Override
    public TXSemanticTag getSemanticTag(String[] sis) throws SharkKBException {
        SemanticTag st = this.storage.getSemanticTag(sis);
        if(st instanceof TXSemanticTag) {
            return (TXSemanticTag) st;
        } else {
            return null;
        }
    }

    @Override
    public TXSemanticTag getSemanticTag(String si) throws SharkKBException {
      return this.getSemanticTag(new String[] {si});
    }

    /**
     * @return enumeration of all root tags or null if there is no root tag.
     * @throws SharkKBException 
     */
    @Override
    public Enumeration<TXSemanticTag> rootTags() throws SharkKBException {
        Enumeration<SemanticTag> tagEnum = this.storage.tags();
        if(tagEnum == null) return null;
        
        HashSet rootTags = new HashSet();
        
        while(tagEnum.hasMoreElements()) {
            SemanticTag st = tagEnum.nextElement();
            if(st instanceof TXSemanticTag) {
                TXSemanticTag txst = (TXSemanticTag) st;
                if(txst.getSuperTag() == null) {
                    // no super tag - its a root tag
                    rootTags.add(txst);
                }
            }
        }
        
        if(rootTags.isEmpty()) {
            return null;
        } else {
            return new Iterator2Enumeration(rootTags.iterator());
        }
    }
    
    @Override
    public Taxonomy fragment(SemanticTag anchor, FragmentationParameter fp) throws SharkKBException {
        SemanticNet fragment = this.storage.fragment(anchor, fp);

        if(fragment == null) return null;

        return new InMemoTaxonomy(fragment);
    }

    @Override
    public Taxonomy fragment(SemanticTag anchor) throws SharkKBException {
        SemanticNet fragment = this.storage.fragment(anchor);
        
        if(fragment == null) return null;
        
        return new InMemoTaxonomy(fragment);
    }

    @Override
    public Taxonomy contextualize(Enumeration<SemanticTag> anchorSet) throws SharkKBException {
        SemanticNet fragment = this.storage.contextualize(anchorSet);
        
        if(fragment == null) return null;
        
        return new InMemoTaxonomy(fragment);
    }

    @Override
    public Taxonomy contextualize(STSet context, FragmentationParameter fp) throws SharkKBException {
        SemanticNet fragment = this.storage.contextualize(context, fp);
        
        if(fragment == null) return null;
        
        return new InMemoTaxonomy(fragment);
    }

    @Override
    public Taxonomy contextualize(STSet context) throws SharkKBException {
        SemanticNet fragment = this.storage.contextualize(context);
        
        if(fragment == null) return null;
        
        return new InMemoTaxonomy(fragment);
    }

    @Override
    public void merge(STSet toMerge) throws SharkKBException {
        if(toMerge instanceof InMemoTaxonomy) {
            InMemoTaxonomy imt = (InMemoTaxonomy) toMerge;
            SemanticNet storage2Merge = imt.getStorage();
            
            this.storage.merge(storage2Merge);
        } else {
            throw new SharkKBException("InMemoTaxonomy can only merge "
                    + "another InMemoTaxonomy in this version - sorry");
        }
    }

    @Override
    public Enumeration<SemanticTag> tags() throws SharkKBException {
        return this.storage.tags();
    }

    @Override
    public FragmentationParameter getDefaultFP() {
        return this.storage.getDefaultFP();
    }

    @Override
    public void setDefaultFP(FragmentationParameter fp) {
        this.storage.setDefaultFP(fp);
    }

    @Override
    public Taxonomy contextualize(Enumeration<SemanticTag> anchorSet, FragmentationParameter fp) throws SharkKBException {
        SemanticNet fragment = this.storage.contextualize(anchorSet, fp);
        
        if(fragment == null) return null;
        
        return new InMemoTaxonomy(fragment);
    }

    @Override
    public SemanticTag merge(SemanticTag source) throws SharkKBException {
        if(source instanceof TXSemanticTag) {
            return this.storage.merge(source);
        } else {
            throw new SharkKBException("don't like to merge a non "
                    + "TXSemanticTag into a taxonomy");
        }
    }

    @Override
    public void addListener(STSetListener listen) throws SharkKBException {
        this.storage.addListener(listen);
    }

    @Override
    public void removeListener(STSetListener listener) throws SharkKBException {
        this.storage.removeListener(listener);
    }

    protected SemanticNet getStorage() {
        return this.storage;
    }

    @Override
    public boolean isEmpty() {
        return this.storage.isEmpty();
    }

    @Override
    public void setEnumerateHiddenTags(boolean hide) {
        this.storage.setEnumerateHiddenTags(hide);
    }

    @Override
    public void removeSubTree(TXSemanticTag tag) throws SharkKBException {
        TXSemanticTag root = this.getSemanticTag(tag.getSI());
        
        if(root != null) {
            // remove sub concepts
            Enumeration<TXSemanticTag> subEnum = root.getSubTags();
            if(subEnum != null) {
                while(subEnum.hasMoreElements()) {
                    this.removeSubTree(subEnum.nextElement());
                }
            }
            
            // remove this
            this.removeSemanticTag(root);
        }
    }
    
    /**
     * Checks whether tag is (transitiv) sub tag of root.
     * @param root presumed root tag
     * @param tag semantic tag to investigate
     * @return 
     */
    @Override
    public boolean isSubTag(TXSemanticTag root, TXSemanticTag tag) {
        if(tag == null || root == null) return false;
        
        TXSemanticTag superTag = tag.getSuperTag();
        
        if(superTag == null) return false;
        
        if(superTag.identical(root)) return true;
        
        return this.isSubTag(root, superTag);
    }

    @Override
    public SemanticTag createSemanticTag(String name, String[] sis) throws SharkKBException {
        return this.createTXSemanticTag(name, sis);
    }

    @Override
    public SemanticTag createSemanticTag(String name, String si) throws SharkKBException {
        return this.createTXSemanticTag(name, si);
    }

    @Override
    public Taxonomy contextualizeTaxonomy(STSet context, FragmentationParameter fp) throws SharkKBException {
        return this.contextualize(context, fp);
    }

    @Override
    public Taxonomy fragmentTaxonomy(SemanticTag anchor, FragmentationParameter fp) throws SharkKBException {
        return this.fragment(anchor, fp);
    }
}
