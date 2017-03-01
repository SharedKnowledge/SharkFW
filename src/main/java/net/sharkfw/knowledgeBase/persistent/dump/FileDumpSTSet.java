package net.sharkfw.knowledgeBase.persistent.dump;

import net.sharkfw.knowledgeBase.*;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;

/**
 * Created by j4rvis on 2/28/17.
 */
public class FileDumpSTSet implements STSet {

    protected final FileDumpSharkKB kb;
    private final STSet set;

    public FileDumpSTSet(FileDumpSharkKB kb, STSet set) {
        this.kb = kb;
        this.set = set;
    }

    @Override
    public SemanticTag merge(SemanticTag tag) throws SharkKBException {
        SemanticTag merge = this.set.merge(tag);
        this.kb.persist();
        return new FileDumpSemanticTag(this.kb, merge);
    }

    @Override
    public SemanticTag createSemanticTag(String name, String[] sis) throws SharkKBException {
        SemanticTag semanticTag = this.set.createSemanticTag(name, sis);
        this.kb.persist();
        return new FileDumpSemanticTag(this.kb, semanticTag);
    }

    @Override
    public SemanticTag createSemanticTag(String name, String si) throws SharkKBException {
        SemanticTag semanticTag = this.set.createSemanticTag(name, si);
        this.kb.persist();
        return new FileDumpSemanticTag(this.kb, semanticTag);
    }

    @Override
    public void removeSemanticTag(SemanticTag tag) throws SharkKBException {
        this.set.removeSemanticTag(tag);
        this.kb.persist();
    }

    @Override
    public void removeSemanticTag(String si) throws SharkKBException {
        this.set.removeSemanticTag(si);
        this.kb.persist();
    }

    @Override
    public void removeSemanticTag(String[] sis) throws SharkKBException {
        this.set.removeSemanticTag(sis);
        this.kb.persist();
    }

    @Override
    public void setEnumerateHiddenTags(boolean hide) {
        this.set.setEnumerateHiddenTags(hide);
        this.kb.persist();
    }

    @Override
    public Enumeration<SemanticTag> tags() throws SharkKBException {
        return this.set.tags();
    }

    @Override
    public Iterator<SemanticTag> stTags() throws SharkKBException {
        return this.set.stTags();
    }

    @Override
    public SemanticTag getSemanticTag(String[] si) throws SharkKBException {
        SemanticTag semanticTag = this.set.getSemanticTag(si);
        this.kb.persist();
        return new FileDumpSemanticTag(this.kb, semanticTag);
    }

    @Override
    public SemanticTag getSemanticTag(String si) throws SharkKBException {
        SemanticTag semanticTag = this.set.getSemanticTag(si);
        this.kb.persist();
        return new FileDumpSemanticTag(this.kb, semanticTag);
    }

    @Override
    public Iterator<SemanticTag> getSemanticTagByName(String pattern) throws SharkKBException {
        Iterator<SemanticTag> semanticTagByName = this.set.getSemanticTagByName(pattern);
        List<SemanticTag> list = new ArrayList<>();
        while (semanticTagByName.hasNext()){
            list.add(new FileDumpSemanticTag(this.kb, semanticTagByName.next()));
        }
        return list.iterator();
    }

    @Override
    public STSet fragment(SemanticTag anchor) throws SharkKBException {
        STSet fragment = this.set.fragment(anchor);
        this.kb.persist();
        return new FileDumpSTSet(this.kb, fragment);
    }

    @Override
    public FragmentationParameter getDefaultFP() {
        return this.set.getDefaultFP();
        // TODO needs to wrapped as well?
    }

    @Override
    public void setDefaultFP(FragmentationParameter fp) {
        this.set.setDefaultFP(fp);
        this.kb.persist();
    }

    @Override
    public STSet fragment(SemanticTag anchor, FragmentationParameter fp) throws SharkKBException {
        STSet fragment = this.set.fragment(anchor, fp);
        this.kb.persist();
        return new FileDumpSTSet(this.kb, fragment);
    }

    @Override
    public STSet contextualize(Enumeration<SemanticTag> anchorSet, FragmentationParameter fp) throws SharkKBException {
        STSet fragment = this.set.contextualize(anchorSet, fp);
        this.kb.persist();
        return new FileDumpSTSet(this.kb, fragment);
    }

    @Override
    public STSet contextualize(Enumeration<SemanticTag> anchorSet) throws SharkKBException {
        STSet fragment = this.set.contextualize(anchorSet);
        this.kb.persist();
        return new FileDumpSTSet(this.kb, fragment);

    }

    @Override
    public STSet contextualize(STSet context, FragmentationParameter fp) throws SharkKBException {
        STSet fragment = this.set.contextualize(context, fp);
        this.kb.persist();
        return new FileDumpSTSet(this.kb, fragment);
    }

    @Override
    public STSet contextualize(STSet context) throws SharkKBException {
        STSet fragment = this.set.contextualize(context);
        this.kb.persist();
        return new FileDumpSTSet(this.kb, fragment);
    }

    @Override
    public void merge(STSet stSet) throws SharkKBException {
        this.set.contextualize(stSet);
        this.kb.persist();
    }

    @Override
    public void addListener(STSetListener listen) {
        this.set.addListener(listen);
        this.kb.persist();
    }

    @Override
    public void removeListener(STSetListener listener) throws SharkKBException {
        this.set.removeListener(listener);
        this.kb.persist();
    }

    @Override
    public boolean isEmpty() {
        return this.set.isEmpty();
    }

    @Override
    public int size() {
        return this.set.size();
    }
}
