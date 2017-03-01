package net.sharkfw.knowledgeBase.persistent.dump;

import net.sharkfw.knowledgeBase.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

/**
 * Created by j4rvis on 2/28/17.
 */
public class FileDumpPeerSTSet extends FileDumpSTSet implements PeerSTSet {

    private final PeerSTSet peerSTSet;

    public FileDumpPeerSTSet(FileDumpSharkKB kb, PeerSTSet set) {
        super(kb, set);
        peerSTSet = set;
    }

    @Override
    public PeerSemanticTag createPeerSemanticTag(String name, String[] sis, String[] addresses) throws SharkKBException {
        PeerSemanticTag peerSemanticTag = this.peerSTSet.createPeerSemanticTag(name, sis, addresses);
        this.kb.persist();
        return new FileDumpPeerSemanticTag(this.kb, peerSemanticTag);
    }

    @Override
    public PeerSemanticTag createPeerSemanticTag(String name, String[] sis, String address) throws SharkKBException {
        PeerSemanticTag peerSemanticTag = this.peerSTSet.createPeerSemanticTag(name, sis, address);
        this.kb.persist();
        return new FileDumpPeerSemanticTag(this.kb, peerSemanticTag);

    }

    @Override
    public PeerSemanticTag createPeerSemanticTag(String name, String si, String[] addresses) throws SharkKBException {
        PeerSemanticTag peerSemanticTag = this.peerSTSet.createPeerSemanticTag(name, si, addresses);
        this.kb.persist();
        return new FileDumpPeerSemanticTag(this.kb, peerSemanticTag);
    }

    @Override
    public PeerSemanticTag createPeerSemanticTag(String name, String si, String address) throws SharkKBException {
        PeerSemanticTag peerSemanticTag = this.peerSTSet.createPeerSemanticTag(name, si, address);
        this.kb.persist();
        return new FileDumpPeerSemanticTag(this.kb, peerSemanticTag);
    }

    @Override
    public PeerSemanticTag getSemanticTag(String[] sis) throws SharkKBException {
        PeerSemanticTag peerSemanticTag = this.peerSTSet.getSemanticTag(sis);
        this.kb.persist();
        return new FileDumpPeerSemanticTag(this.kb, peerSemanticTag);
    }

    @Override
    public PeerSemanticTag getSemanticTag(String si) throws SharkKBException {
        PeerSemanticTag peerSemanticTag = this.peerSTSet.getSemanticTag(si);
        this.kb.persist();
        return new FileDumpPeerSemanticTag(this.kb, peerSemanticTag);
    }

    @Override
    public PeerSTSet fragment(SemanticTag anchor) throws SharkKBException{
        PeerSTSet fragment = this.peerSTSet.fragment(anchor);
        this.kb.persist();
        return new FileDumpPeerSTSet(this.kb, fragment);
    }

    @Override
    public PeerSTSet fragment(SemanticTag anchor, FragmentationParameter fp) throws SharkKBException {
        PeerSTSet fragment = this.peerSTSet.fragment(anchor, fp);
        this.kb.persist();
        return new FileDumpPeerSTSet(this.kb, fragment);
    }

    @Override
    public PeerSTSet contextualize(Enumeration<SemanticTag> anchor, FragmentationParameter fp) throws SharkKBException {
        PeerSTSet contextualize = this.peerSTSet.contextualize(anchor, fp);
        this.kb.persist();
        return new FileDumpPeerSTSet(this.kb, contextualize);
    }

    @Override
    public PeerSTSet contextualize(Enumeration<SemanticTag> anchor) throws SharkKBException {
        PeerSTSet contextualize = this.peerSTSet.contextualize(anchor);
        this.kb.persist();
        return new FileDumpPeerSTSet(this.kb, contextualize);
    }

    @Override
    public PeerSTSet contextualize(STSet context) throws SharkKBException {
        PeerSTSet contextualize = this.peerSTSet.contextualize(context);
        this.kb.persist();
        return new FileDumpPeerSTSet(this.kb, contextualize);
    }

    @Override
    public PeerSTSet contextualize(STSet context, FragmentationParameter fp) throws SharkKBException {
        PeerSTSet contextualize = this.peerSTSet.contextualize(context, fp);
        this.kb.persist();
        return new FileDumpPeerSTSet(this.kb, contextualize);
    }

    @Override
    public Enumeration<PeerSemanticTag> peerTags() {
        Enumeration<PeerSemanticTag> peerSemanticTagEnumeration = this.peerSTSet.peerTags();
        List<PeerSemanticTag> list = new ArrayList<>();
        while (peerSemanticTagEnumeration.hasMoreElements()){
            list.add(new FileDumpPeerSemanticTag(this.kb, peerSemanticTagEnumeration.nextElement()));
        }
        return Collections.enumeration(list);
    }
}
