package net.sharkfw.knowledgeBase.sync;

import java.util.Enumeration;
import net.sharkfw.knowledgeBase.FragmentationParameter;
import net.sharkfw.knowledgeBase.SNSemanticTag;
import net.sharkfw.knowledgeBase.STSet;
import net.sharkfw.knowledgeBase.SemanticNet;
import net.sharkfw.knowledgeBase.SemanticTag;
import net.sharkfw.knowledgeBase.SharkKBException;

/**
 *
 * @author thsc42
 */
class SyncSemanticNet extends SyncSTSet implements SemanticNet {
    private final SemanticNet target;

    public SyncSemanticNet(SemanticNet net) {
        super(net);
        this.target = net;
    }
    
    SyncSNSemanticTag wrapSyncObject(SNSemanticTag target) {
        if(target != null) {
            return new SyncSNSemanticTag(target);
        }
        return null;
    }

    @Override
    public STSet asSTSet() {
        return this.target.asSTSet();
    }
    
    @Override
    public SNSemanticTag createSemanticTag(String name, String[] sis) throws SharkKBException {
        return this.wrapSyncObject(this.target.createSemanticTag(name, sis));
    }

    @Override
    public SNSemanticTag createSemanticTag(String name, String si) throws SharkKBException {
        return this.wrapSyncObject(this.target.createSemanticTag(name, si));
    }

    @Override
    public void removeSemanticTag(SNSemanticTag tag) throws SharkKBException {
        super.removeSemanticTag(tag);
    }

    @Override
    public SNSemanticTag getSemanticTag(String[] sis) throws SharkKBException {
        return this.wrapSyncObject(this.target.getSemanticTag(sis));
    }

    @Override
    public SNSemanticTag getSemanticTag(String si) throws SharkKBException {
        return this.wrapSyncObject(this.target.getSemanticTag(si));
    }

    /**
     * TODO: keep track of changes in relations
     * @param source
     * @param target
     * @param type
     * @throws SharkKBException 
     */
    @Override
    public void setPredicate(SNSemanticTag source, SNSemanticTag target, String type) throws SharkKBException {
        this.target.setPredicate(source, target, type);
    }

    /**
     * TODO: keep track of changes in relations
     * @param source
     * @param target
     * @param type
     * @throws SharkKBException 
     */
    @Override
    public void removePredicate(SNSemanticTag source, SNSemanticTag target, String type) throws SharkKBException {
        this.target.removePredicate(source, target, type);
    }

    @Override
    public SemanticNet fragment(SemanticTag anchor, FragmentationParameter fp) throws SharkKBException {
        return this.target.fragment(anchor, fp);
    }

    @Override
    public SemanticNet fragment(SemanticTag anchor) throws SharkKBException {
        return this.target.fragment(anchor);
    }

    @Override
    public SemanticNet contextualize(Enumeration<SemanticTag> anchorSet, FragmentationParameter fp) throws SharkKBException {
        return this.target.contextualize(anchorSet, fp);
    }

    @Override
    public SemanticNet contextualize(STSet context, FragmentationParameter fp) throws SharkKBException {
        return this.target.contextualize(context, fp);
    }

    @Override
    public SemanticNet contextualize(Enumeration<SemanticTag> anchorSet) throws SharkKBException {
        return this.target.contextualize(anchorSet);
    }

    @Override
    public SemanticNet contextualize(STSet context) throws SharkKBException {
        return this.target.contextualize(context);
    }

    @Override
    public void merge(SemanticNet remoteSemanticNet) throws SharkKBException {
        this.target.merge(this);
    }

    @Override
    public SNSemanticTag merge(SemanticTag source) throws SharkKBException {
        return this.wrapSyncObject(this.target.merge(source));
    }

    @Override
    public void add(SemanticTag tag) throws SharkKBException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
