package net.sharkfw.knowledgeBase.sync;

import net.sharkfw.knowledgeBase.*;

import java.util.Enumeration;
import java.util.Iterator;

/**
 * Created by thsc42 on 28.07.16.
 */
class SyncSTSet implements STSet {

    private final STSet target;

    SyncSTSet(STSet target) {
        this.target = target;
    }

    @Override
    public SemanticTag merge(SemanticTag tag) throws SharkKBException {
        SemanticTag mTag = this.target.merge(tag);

        if(mTag != null) {
            return new SyncSemanticTag(mTag);
        }

        return null;
    }

    @Override
    public SemanticTag createSemanticTag(String name, String[] sis) throws SharkKBException {
        return null;
    }

    @Override
    public SemanticTag createSemanticTag(String name, String si) throws SharkKBException {
        return null;
    }

    @Override
    public void removeSemanticTag(SemanticTag tag) throws SharkKBException {

    }

    @Override
    public void removeSemanticTag(String si) throws SharkKBException {

    }

    @Override
    public void removeSemanticTag(String[] sis) throws SharkKBException {

    }

    @Override
    public void setEnumerateHiddenTags(boolean hide) {

    }

    @Override
    public Enumeration<SemanticTag> tags() throws SharkKBException {
        return null;
    }

    @Override
    public Iterator<SemanticTag> stTags() throws SharkKBException {
        return null;
    }

    @Override
    public SemanticTag getSemanticTag(String[] si) throws SharkKBException {
        return null;
    }

    @Override
    public SemanticTag getSemanticTag(String si) throws SharkKBException {
        return null;
    }

    @Override
    public Iterator<SemanticTag> getSemanticTagByName(String pattern) throws SharkKBException {
        return null;
    }

    @Override
    public STSet fragment(SemanticTag anchor) throws SharkKBException {
        return null;
    }

    @Override
    public FragmentationParameter getDefaultFP() {
        return null;
    }

    @Override
    public void setDefaultFP(FragmentationParameter fp) {

    }

    @Override
    public STSet fragment(SemanticTag anchor, FragmentationParameter fp) throws SharkKBException {
        return null;
    }

    @Override
    public STSet contextualize(Enumeration<SemanticTag> anchorSet, FragmentationParameter fp) throws SharkKBException {
        return null;
    }

    @Override
    public STSet contextualize(Enumeration<SemanticTag> anchorSet) throws SharkKBException {
        return null;
    }

    @Override
    public STSet contextualize(STSet context, FragmentationParameter fp) throws SharkKBException {
        return null;
    }

    @Override
    public STSet contextualize(STSet context) throws SharkKBException {
        return null;
    }

    @Override
    public void merge(STSet stSet) throws SharkKBException {

    }

    @Override
    public void addListener(STSetListener listen) {

    }

    @Override
    public void removeListener(STSetListener listener) throws SharkKBException {

    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public int size() {
        return 0;
    }
}
