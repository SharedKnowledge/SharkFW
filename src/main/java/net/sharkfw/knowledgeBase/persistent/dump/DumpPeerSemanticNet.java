package net.sharkfw.knowledgeBase.persistent.dump;

import net.sharkfw.knowledgeBase.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;

/**
 * Created by j4rvis on 2/28/17.
 */
public class DumpPeerSemanticNet extends DumpSemanticNet implements PeerSemanticNet {

    private final PeerSemanticNet net;

    public DumpPeerSemanticNet(DumpSharkKB kb, PeerSemanticNet net) {
        super(kb, net);
        this.net = net;
    }

    @Override
    public PeerSTSet asPeerSTSet() {
        return new DumpPeerSTSet(this.kb, net.asPeerSTSet());
    }

    @Override
    public PeerSNSemanticTag createSemanticTag(String name, String[] sis, String[] addresses) throws SharkKBException {
        PeerSNSemanticTag semanticTag = net.createSemanticTag(name, sis, addresses);
        kb.persist();
        return new DumpPeerSNSemanticTag(this.kb, semanticTag);
    }

    @Override
    public PeerSNSemanticTag createSemanticTag(String name, String si, String[] addresses) throws SharkKBException {
        PeerSNSemanticTag semanticTag = net.createSemanticTag(name, si, addresses);
        kb.persist();
        return new DumpPeerSNSemanticTag(this.kb, semanticTag);

    }

    @Override
    public PeerSNSemanticTag createSemanticTag(String name, String si, String address) throws SharkKBException {
        PeerSNSemanticTag semanticTag = net.createSemanticTag(name, si, address);
        kb.persist();
        return new DumpPeerSNSemanticTag(this.kb, semanticTag);
    }

    @Override
    public PeerSNSemanticTag createSemanticTag(String name, String[] sis, String address) throws SharkKBException {
        PeerSNSemanticTag semanticTag = net.createSemanticTag(name, sis, address);
        kb.persist();
        return new DumpPeerSNSemanticTag(this.kb, semanticTag);
    }

    @Override
    public Enumeration<PeerSemanticTag> peerTags() throws SharkKBException {
        Enumeration<PeerSemanticTag> peerSemanticTagEnumeration = net.peerTags();
        ArrayList<PeerSemanticTag> list = new ArrayList<>();
        while (peerSemanticTagEnumeration.hasMoreElements()){
            list.add(new DumpPeerSemanticTag(this.kb, peerSemanticTagEnumeration.nextElement()));
        }
        return Collections.enumeration(list);

    }

    @Override
    public PeerSNSemanticTag getSemanticTag(String[] si) throws SharkKBException {
        PeerSNSemanticTag semanticTag = this.net.getSemanticTag(si);
        this.kb.persist();
        return new DumpPeerSNSemanticTag(this.kb, semanticTag);
    }

    @Override
    public PeerSNSemanticTag getSemanticTag(String si) throws SharkKBException {
        PeerSNSemanticTag semanticTag = this.net.getSemanticTag(si);
        this.kb.persist();
        return new DumpPeerSNSemanticTag(this.kb, semanticTag);
    }

    @Override
    public PeerSemanticNet fragment(SemanticTag anchor, FragmentationParameter fp) throws SharkKBException {
        PeerSemanticNet fragment = this.net.fragment(anchor, fp);
        this.kb.persist();
        return new DumpPeerSemanticNet(this.kb, fragment);
    }
}
