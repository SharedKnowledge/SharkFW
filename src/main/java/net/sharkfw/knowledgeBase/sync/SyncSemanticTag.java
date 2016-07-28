package net.sharkfw.knowledgeBase.sync;

import net.sharkfw.knowledgeBase.SemanticTag;
import net.sharkfw.knowledgeBase.SharkKBException;


/**
 * Created by thsc42 on 28.07.16.
 */
class SyncSemanticTag extends SyncPropertyHolder implements SemanticTag {

    SyncSemanticTag(SemanticTag target) {
        super(target);
    }

    @Override
    protected SyncSemanticTag getTarget() {
        return (SyncSemanticTag) super.getTarget();
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public String[] getSI() {
        return new String[0];
    }

    @Override
    public void removeSI(String si) throws SharkKBException {

    }

    @Override
    public void addSI(String si) throws SharkKBException {

    }

    @Override
    public void setName(String newName) {

    }

    @Override
    public void merge(SemanticTag st) {

    }

    @Override
    public void setHidden(boolean isHidden) {

    }

    @Override
    public boolean hidden() {
        return false;
    }

    @Override
    public boolean isAny() {
        return false;
    }

    @Override
    public boolean identical(SemanticTag other) {
        return false;
    }
}
