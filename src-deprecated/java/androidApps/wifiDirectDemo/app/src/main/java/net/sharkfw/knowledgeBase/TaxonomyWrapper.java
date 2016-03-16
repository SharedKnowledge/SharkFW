package net.sharkfw.knowledgeBase;

import java.util.Enumeration;
import java.util.Iterator;
import net.sharkfw.knowledgeBase.inmemory.InMemoSTSet;
import net.sharkfw.knowledgeBase.inmemory.InMemoTaxonomy;

/**
 *
 * @author thsc
 */
public abstract class TaxonomyWrapper implements Taxonomy {
    protected SemanticNet sn;
        
    protected TaxonomyWrapper() { 
        this.sn = null;
    }
    
    protected void setStorage(SemanticNet storage) {
        this.sn = storage;
    }
    
    public TaxonomyWrapper(SemanticNet storage) {
        this.sn = storage;
    }
    
    protected void add(TXSemanticTag tag) throws SharkKBException {
        this.sn.add(tag);
    }
    
    @Override
    public void merge(TXSemanticTag tag) throws SharkKBException {
            this.sn.merge(tag);
    }

    @Override
    public TXSemanticTag createTXSemanticTag(String name, String si) throws SharkKBException {
        return this.createTXSemanticTag(name, new String[] {si});
    }


    @Override
    public void move(TXSemanticTag superTag, TXSemanticTag subTag) throws SharkKBException {
        subTag.move(superTag);
    }

    @Override
    public TXSemanticTag getSemanticTag(String si) throws SharkKBException {
      return this.getSemanticTag(new String[] {si});
    }
    
    @Override
    public Taxonomy fragment(SemanticTag anchor, FragmentationParameter fp) throws SharkKBException {
        SemanticNet fragment = this.sn.fragment(anchor, fp);

        if(fragment == null) return null;

        return new InMemoTaxonomy(fragment);
    }

    @Override
    public Taxonomy fragment(SemanticTag anchor) throws SharkKBException {
        SemanticNet fragment = this.sn.fragment(anchor);
        
        if(fragment == null) return null;
        
        return new InMemoTaxonomy(fragment);
    }

    @Override
    public Taxonomy contextualize(Enumeration<SemanticTag> anchorSet) throws SharkKBException {
        SemanticNet fragment = this.sn.contextualize(anchorSet);
        
        if(fragment == null) return null;
        
        return new InMemoTaxonomy(fragment);
    }

    @Override
    public Taxonomy contextualize(STSet context, FragmentationParameter fp) throws SharkKBException {
        SemanticNet fragment = this.sn.contextualize(context, fp);
        
        if(fragment == null) return null;
        
        return new InMemoTaxonomy(fragment);
    }

    @Override
    public Taxonomy contextualize(STSet context) throws SharkKBException {
        SemanticNet fragment = this.sn.contextualize(context);
        
        if(fragment == null) return null;
        
        return new InMemoTaxonomy(fragment);
    }

    @Override
    public void merge(STSet toMerge) throws SharkKBException {
        if(toMerge instanceof InMemoTaxonomy) {
            TaxonomyWrapper imt = (TaxonomyWrapper) toMerge;
            SemanticNet storage2Merge = imt.getStorage();
            
            this.sn.merge(storage2Merge);
        } else {
            throw new SharkKBException("InMemoTaxonomy can only merge "
                    + "another InMemoTaxonomy in this version - sorry");
        }
    }

    @Override
    public Enumeration<SemanticTag> tags() throws SharkKBException {
        return this.sn.tags();
    }

    @Override
    public Iterator<SemanticTag> stTags() throws SharkKBException {
        return this.sn.stTags();
    }

    @Override
    public FragmentationParameter getDefaultFP() {
        return this.sn.getDefaultFP();
    }

    @Override
    public void setDefaultFP(FragmentationParameter fp) {
        this.sn.setDefaultFP(fp);
    }

    @Override
    public Taxonomy contextualize(Enumeration<SemanticTag> anchorSet, FragmentationParameter fp) throws SharkKBException {
        SemanticNet fragment = this.sn.contextualize(anchorSet, fp);
        
        if(fragment == null) return null;
        
        return new InMemoTaxonomy(fragment);
    }

    @Override
    public SemanticTag merge(SemanticTag source) throws SharkKBException {
        return this.sn.merge(source);
    }

    @Override
    public void addListener(STSetListener listen) {
        this.sn.addListener(listen);
    }

    @Override
    public void removeListener(STSetListener listener) throws SharkKBException {
        this.sn.removeListener(listener);
    }

    protected SemanticNet getStorage() {
        return this.sn;
    }

    @Override
    public boolean isEmpty() {
        return this.sn.isEmpty();
    }

    @Override
    public void setEnumerateHiddenTags(boolean hide) {
        this.sn.setEnumerateHiddenTags(hide);
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
    public SemanticTag createSemanticTag(String name, String si) throws SharkKBException {
        return this.createTXSemanticTag(name, si);
    }

    @Override
    public TXSemanticTag createSemanticTag(TXSemanticTag superTag, String name, String[] sis) throws SharkKBException {
        TXSemanticTag txst = (TXSemanticTag) this.createTXSemanticTag(name, sis);
        txst.move(superTag);
        return txst;
    }

    @Override
    public SemanticTag createSemanticTag(String name, String[] sis) throws SharkKBException {
        return this.createTXSemanticTag(name, sis);
    }
    
    @Override
    public Taxonomy contextualizeTaxonomy(STSet context, FragmentationParameter fp) throws SharkKBException {
        return this.contextualize(context, fp);
    }

    @Override
    public Taxonomy fragmentTaxonomy(SemanticTag anchor, FragmentationParameter fp) throws SharkKBException {
        return this.fragment(anchor, fp);
    }

    @Override
    public int size() {
        return this.sn.size();
    }
    
    @Override
    public Iterator<SemanticTag> getSemanticTagByName(String pattern) throws SharkKBException {
        return InMemoSTSet.getSemanticTagByName(this, pattern);
    }
}
