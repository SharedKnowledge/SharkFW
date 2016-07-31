package net.sharkfw.knowledgeBase.sync;

import net.sharkfw.knowledgeBase.SemanticTag;
import net.sharkfw.knowledgeBase.SharkKBException;

/**
 * Created by thsc42 on 28.07.16.
 */
class SyncSemanticTag extends SyncPropertyHolder implements SemanticTag {
    private SemanticTag target;

    SyncSemanticTag(SemanticTag target) {
        super(target);

        this.target = target;
    }

    SyncSemanticTag wrapSyncObject(SemanticTag target) {
        return new SyncSemanticTag(target);
    }
    
    @Override
    public String getName() {
        return this.target.getName();
    }

    @Override
    public String[] getSI() {
        return this.target.getSI();
    }

    @Override
    public void removeSI(String si) throws SharkKBException {
        this.target.removeSI(si);
        this.changed();
    }

    @Override
    public void addSI(String si) throws SharkKBException {
        this.target.addSI(si);
        this.changed();
    }

    @Override
    public void setName(String newName) {
        this.target.setName(newName);
        this.changed();
    }

    @Override
    public void merge(SemanticTag st) {
        this.target.merge(st);
    }

    @Override
    public void setHidden(boolean isHidden) {
        this.target.setHidden(isHidden);
    }

    @Override
    public boolean hidden() {
        return this.target.hidden();
    }

    @Override
    public boolean isAny() {
        return this.target.isAny();
    }

    @Override
    public boolean identical(SemanticTag other) {
        return this.target.identical(other);
    }

}
