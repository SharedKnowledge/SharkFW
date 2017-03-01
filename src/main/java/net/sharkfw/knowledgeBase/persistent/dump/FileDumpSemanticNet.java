package net.sharkfw.knowledgeBase.persistent.dump;

import net.sharkfw.knowledgeBase.*;

import java.util.Enumeration;

/**
 * Created by j4rvis on 2/28/17.
 */
public class FileDumpSemanticNet extends FileDumpSTSet implements SemanticNet {

    private final SemanticNet net;

    public FileDumpSemanticNet(FileDumpSharkKB kb, SemanticNet net) {
        super(kb, net);
        this.net = net;
    }

    @Override
    public STSet asSTSet() {
        return new FileDumpSTSet(this.kb, net.asSTSet());
    }

    @Override
    public void removeSemanticTag(SNSemanticTag tag) throws SharkKBException {
        net.removeSemanticTag(tag);
        kb.persist();
    }

    @Override
    public void setPredicate(SNSemanticTag source, SNSemanticTag target, String type) throws SharkKBException {
        net.setPredicate(source, target, type);
        kb.persist();
    }

    @Override
    public void removePredicate(SNSemanticTag source, SNSemanticTag target, String type) throws SharkKBException {
        net.removePredicate(source, target, type);
        kb.persist();
    }

    @Override
    public void merge(SemanticNet remoteSemanticNet) throws SharkKBException {
        net.merge(remoteSemanticNet);
        kb.persist();
    }

    @Override
    public void add(SemanticTag tag) throws SharkKBException {
        net.add(tag);
        kb.persist();
    }

    @Override
    public SNSemanticTag createSemanticTag(String name, String[] sis) throws SharkKBException {
        SNSemanticTag semanticTag = this.net.createSemanticTag(name, sis);
        this.kb.persist();
        return new FileDumpSNSemanticTag(this.kb, semanticTag);
    }

    @Override
    public SNSemanticTag createSemanticTag(String name, String si) throws SharkKBException {
        SNSemanticTag semanticTag = this.net.createSemanticTag(name, si);
        this.kb.persist();
        return new FileDumpSNSemanticTag(this.kb, semanticTag);
    }

    @Override
    public SNSemanticTag getSemanticTag(String[] si) throws SharkKBException {
        SNSemanticTag semanticTag = this.net.getSemanticTag(si);
        this.kb.persist();
        return new FileDumpSNSemanticTag(this.kb, semanticTag);
    }

    @Override
    public SNSemanticTag getSemanticTag(String si) throws SharkKBException {
        SNSemanticTag semanticTag = this.net.getSemanticTag(si);
        this.kb.persist();
        return new FileDumpSNSemanticTag(this.kb, semanticTag);
    }

    @Override
    public SemanticNet fragment(SemanticTag anchor) throws SharkKBException {
        SemanticNet fragment = this.net.fragment(anchor);
        this.kb.persist();
        return new FileDumpSemanticNet(this.kb, fragment);
    }

    @Override
    public SemanticNet fragment(SemanticTag anchor, FragmentationParameter fp) throws SharkKBException {
        SemanticNet fragment = this.net.fragment(anchor, fp);
        this.kb.persist();
        return new FileDumpSemanticNet(this.kb, fragment);
    }

    @Override
    public SemanticNet contextualize(Enumeration<SemanticTag> anchorSet, FragmentationParameter fp) throws SharkKBException {
        SemanticNet fragment = this.net.contextualize(anchorSet, fp);
        this.kb.persist();
        return new FileDumpSemanticNet(this.kb, fragment);
    }

    @Override
    public SemanticNet contextualize(Enumeration<SemanticTag> anchorSet) throws SharkKBException {
        SemanticNet fragment = this.net.contextualize(anchorSet);
        this.kb.persist();
        return new FileDumpSemanticNet(this.kb, fragment);

    }

    @Override
    public SemanticNet contextualize(STSet context, FragmentationParameter fp) throws SharkKBException {
        SemanticNet fragment = this.net.contextualize(context, fp);
        this.kb.persist();
        return new FileDumpSemanticNet(this.kb, fragment);
    }

    @Override
    public SemanticNet contextualize(STSet context) throws SharkKBException {
        SemanticNet fragment = this.net.contextualize(context);
        this.kb.persist();
        return new FileDumpSemanticNet(this.kb, fragment);
    }

    @Override
    public SNSemanticTag merge(SemanticTag source) throws SharkKBException{
        SNSemanticTag merge = this.net.merge(source);
        kb.persist();
        return new FileDumpSNSemanticTag(this.kb, merge);
    }

    @Override
    public void merge(STSet stSet) throws SharkKBException {
        this.net.contextualize(stSet);
        this.kb.persist();
    }
}
