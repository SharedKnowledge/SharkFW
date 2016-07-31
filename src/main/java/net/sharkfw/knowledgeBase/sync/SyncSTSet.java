package net.sharkfw.knowledgeBase.sync;

import java.util.ArrayList;
import net.sharkfw.knowledgeBase.*;

import java.util.Enumeration;
import java.util.Iterator;
import net.sharkfw.knowledgeBase.inmemory.InMemoSTSet;
import net.sharkfw.knowledgeBase.inmemory.InMemoSemanticNet;
import net.sharkfw.knowledgeBase.inmemory.InMemoTaxonomy;
import net.sharkfw.system.Iterator2Enumeration;

/**
 * Created by thsc42 on 28.07.16.
 */
class SyncSTSet extends Sync implements STSet {

    private STSet target;

    SyncSTSet(STSet target) {
        this.target = target;
    }

    SyncSemanticTag wrapSyncObject(SemanticTag target) {
        if(target != null) {
            return new SyncSemanticTag((SemanticTag) target);
        }
        
        return null;
    }
    
    @Override
    public SemanticTag merge(SemanticTag tag) throws SharkKBException {
        return this.wrapSyncObject(this.target.merge(tag));
    }
    
    @Override
    public SemanticTag createSemanticTag(String name, String[] sis) throws SharkKBException {
        return this.wrapSyncObject(this.target.createSemanticTag(name, sis));
    }

    @Override
    public SemanticTag createSemanticTag(String name, String si) throws SharkKBException {
        return this.wrapSyncObject(this.target.createSemanticTag(name, si));
    }

    @Override
    public void removeSemanticTag(SemanticTag tag) throws SharkKBException {
        this.target.removeSemanticTag(tag);
    }

    @Override
    public void removeSemanticTag(String si) throws SharkKBException {
        this.target.removeSemanticTag(si);
    }

    @Override
    public void removeSemanticTag(String[] sis) throws SharkKBException {
        this.target.removeSemanticTag(sis);
    }

    @Override
    public void setEnumerateHiddenTags(boolean hide) {
        this.target.setEnumerateHiddenTags(hide);
    }

    @Override
    public Enumeration<SemanticTag> tags() throws SharkKBException {
        Iterator<SemanticTag> stTags = this.stTags();
        if(stTags == null) return null;

        ArrayList wrapSTIter = this.wrapSTIter(this, stTags);
        return new Iterator2Enumeration(wrapSTIter.iterator());
    }
    
    @Override
    public Iterator<SemanticTag> stTags() throws SharkKBException {
        Iterator<SemanticTag> stTags = this.target.stTags();
        if(stTags == null) return null;
        
        return this.wrapSTIter(this, stTags).iterator();
    }

    @Override
    public SemanticTag getSemanticTag(String[] si) throws SharkKBException {
        return this.wrapSyncObject(this.target.getSemanticTag(si));
    }

    @Override
    public SemanticTag getSemanticTag(String si) throws SharkKBException {
        return this.wrapSyncObject(this.target.getSemanticTag(si));
    }

    @Override
    public Iterator<SemanticTag> getSemanticTagByName(String pattern) throws SharkKBException {
        Iterator<SemanticTag> stTags = this.target.getSemanticTagByName(pattern);
        if(stTags == null) return null;
        
        return this.wrapSTIter(this, stTags).iterator();
    }

    /**
     * No additional actions required. A fragment is a copy of that
     * set. Actions on that set do not impact the original set.
     * @param anchor
     * @return
     * @throws SharkKBException 
     */
    @Override
    public STSet fragment(SemanticTag anchor) throws SharkKBException {
        return this.target.fragment(anchor);
    }

    @Override
    public FragmentationParameter getDefaultFP() {
        return this.target.getDefaultFP();
    }

    @Override
    public void setDefaultFP(FragmentationParameter fp) {
        this.target.setDefaultFP(fp);
    }

    @Override
    public STSet fragment(SemanticTag anchor, FragmentationParameter fp) throws SharkKBException {
        return this.target.fragment(anchor, fp);
    }

    @Override
    public STSet contextualize(Enumeration<SemanticTag> anchorSet, FragmentationParameter fp) throws SharkKBException {
        return this.target.contextualize(anchorSet, fp);
    }

    @Override
    public STSet contextualize(Enumeration<SemanticTag> anchorSet) throws SharkKBException {
        return this.target.contextualize(anchorSet);
    }

    @Override
    public STSet contextualize(STSet context, FragmentationParameter fp) throws SharkKBException {
        return this.target.contextualize(context, fp);
    }

    @Override
    public STSet contextualize(STSet context) throws SharkKBException {
        return this.target.contextualize(context);
    }

    @Override
    public void merge(STSet stSet) throws SharkKBException {
        this.target.merge(stSet);
    }

    @Override
    public void addListener(STSetListener listen) {
        this.target.addListener(listen);
    }

    @Override
    public void removeListener(STSetListener listener) throws SharkKBException {
        this.target.removeListener(listener);
    }

    @Override
    public boolean isEmpty() {
        return this.target.isEmpty();
    }

    @Override
    public int size() {
        return this.target.size();
    }

    STSet getChanges(Long since) throws SharkKBException {
        STSet changes = new InMemoSTSet();
        this.putChanges(since, changes);
        return changes;
    }

    SemanticNet getChangesAsSemanticNet(Long since) throws SharkKBException {
        SemanticNet changes = new InMemoSemanticNet();
        this.putChanges(since, changes);
        return changes;
    }
    
    Taxonomy getChangesAsTaxonomy(Long since) throws SharkKBException {
        Taxonomy changes = new InMemoTaxonomy();
        this.putChanges(since, changes);
        return changes;
    }
    
    void putChanges(Long since, STSet changes) throws SharkKBException {
        if(changes == null) return;
        
        Enumeration<SemanticTag> tags = this.target.tags();
        if(tags == null) return;
        
        while(tags.hasMoreElements()) {
            SemanticTag st = tags.nextElement();
            
            if(SyncKB.getTimeStamp(st) > since) {
                changes.merge(st);
            }
        }
    }
}
