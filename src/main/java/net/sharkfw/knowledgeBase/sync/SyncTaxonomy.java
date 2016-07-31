package net.sharkfw.knowledgeBase.sync;

import java.util.Enumeration;
import net.sharkfw.knowledgeBase.FragmentationParameter;
import net.sharkfw.knowledgeBase.STSet;
import net.sharkfw.knowledgeBase.SemanticTag;
import net.sharkfw.knowledgeBase.SharkKBException;
import net.sharkfw.knowledgeBase.TXSemanticTag;
import net.sharkfw.knowledgeBase.Taxonomy;
import net.sharkfw.system.Iterator2Enumeration;

/**
 *
 * @author thsc42
 */
class SyncTaxonomy extends SyncSTSet implements Taxonomy {
    private final Taxonomy target;

    public SyncTaxonomy(Taxonomy tx) {
        super(tx);
        this.target = tx;
    }
    
    SyncTXSemanticTag wrapSyncObject(TXSemanticTag txTag) {
        if(txTag != null) {
            return new SyncTXSemanticTag(txTag);
        }
        return null;
    }
    

    @Override
    public void merge(TXSemanticTag tag) throws SharkKBException {
        this.target.merge(tag);
    }

    @Override
    public void move(TXSemanticTag superTag, TXSemanticTag subTag) throws SharkKBException {
        this.target.move(superTag, subTag);
    }

    @Override
    public TXSemanticTag createSemanticTag(TXSemanticTag superTag, String name, String[] sis) throws SharkKBException {
        return this.wrapSyncObject(this.target.createSemanticTag(superTag, name, sis));
    }

    /**
     * TODO: keep track of removed objects
     * @param tag
     * @throws SharkKBException 
     */
    @Override
    public void removeSemanticTag(TXSemanticTag tag) throws SharkKBException {
        super.removeSemanticTag(tag);
    }

    @Override
    public void removeSubTree(TXSemanticTag tag) throws SharkKBException {
        this.target.removeSubTree(tag);
    }

    @Override
    public Enumeration<TXSemanticTag> rootTags() throws SharkKBException {
        return new Iterator2Enumeration(
                this.wrapSTEnum(this, this.target.rootTags()).iterator());
    }

    @Override
    public boolean isSubTag(TXSemanticTag root, TXSemanticTag tag) {
        return this.target.isSubTag(root, tag);
    }

    @Override
    public TXSemanticTag createTXSemanticTag(String name, String[] sis) throws SharkKBException {
        return this.wrapSyncObject(this.createTXSemanticTag(name, sis));
    }

    @Override
    public TXSemanticTag createTXSemanticTag(String name, String si) throws SharkKBException {
        return this.wrapSyncObject(this.createTXSemanticTag(name, si));
    }

    @Override
    public TXSemanticTag getSemanticTag(String[] sis) throws SharkKBException {
        return this.wrapSyncObject(this.getSemanticTag(sis));
    }

    @Override
    public TXSemanticTag getSemanticTag(String si) throws SharkKBException {
        return this.wrapSyncObject(this.target.getSemanticTag(si));
    }

    @Override
    public Taxonomy contextualizeTaxonomy(STSet context, FragmentationParameter fp) throws SharkKBException {
        return this.target.contextualizeTaxonomy(context, fp);
    }

    @Override
    public Taxonomy fragmentTaxonomy(SemanticTag anchor, FragmentationParameter fp) throws SharkKBException {
        return this.target.fragmentTaxonomy(anchor, fp);
    }
}
