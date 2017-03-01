package net.sharkfw.knowledgeBase.persistent.dump;

import net.sharkfw.knowledgeBase.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;

/**
 * Created by j4rvis on 2/28/17.
 */
public class FileDumpPeerTaxonomy extends FileDumpTaxonomy implements PeerTaxonomy {

    private final PeerTaxonomy peerTaxonomy;

    public FileDumpPeerTaxonomy(FileDumpSharkKB kb, PeerTaxonomy taxonomy) {
        super(kb, taxonomy);
        peerTaxonomy = taxonomy;
    }

    @Override
    public PeerTaxonomy resolveSuperPeers(PeerTXSemanticTag pstGroup) throws SharkKBException {
        PeerTaxonomy peerTaxonomy = this.peerTaxonomy.resolveSuperPeers(pstGroup);
        kb.persist();
        return new FileDumpPeerTaxonomy(kb, peerTaxonomy);
    }

    @Override
    public PeerSTSet asPeerSTSet() throws SharkKBException {
        return new FileDumpPeerSTSet(kb, peerTaxonomy.asPeerSTSet());
    }

    @Override
    public PeerTXSemanticTag createPeerTXSemanticTag(String name, String[] sis, String[] addresses) throws SharkKBException {
        PeerTXSemanticTag peerTXSemanticTag = peerTaxonomy.createPeerTXSemanticTag(name, sis, addresses);
        kb.persist();
        return new FileDumpPeerTXSemanticTag(kb, peerTXSemanticTag);
    }

    @Override
    public PeerTXSemanticTag createPeerTXSemanticTag(String name, String si, String[] addresses) throws SharkKBException {
        PeerTXSemanticTag peerTXSemanticTag = peerTaxonomy.createPeerTXSemanticTag(name, si, addresses);
        kb.persist();
        return new FileDumpPeerTXSemanticTag(kb, peerTXSemanticTag);
    }

    @Override
    public PeerTXSemanticTag createPeerTXSemanticTag(String name, String[] sis, String address) throws SharkKBException {
        PeerTXSemanticTag peerTXSemanticTag = peerTaxonomy.createPeerTXSemanticTag(name, sis, address);
        kb.persist();
        return new FileDumpPeerTXSemanticTag(kb, peerTXSemanticTag);
    }

    @Override
    public PeerTXSemanticTag createPeerTXSemanticTag(String name, String si, String address) throws SharkKBException {
        PeerTXSemanticTag peerTXSemanticTag = peerTaxonomy.createPeerTXSemanticTag(name, si, address);
        kb.persist();
        return new FileDumpPeerTXSemanticTag(kb, peerTXSemanticTag);
    }

    @Override
    public void move(PeerTXSemanticTag superPST, PeerTXSemanticTag subPST) throws SharkKBException {
        peerTaxonomy.move(superPST, subPST);
        kb.persist();
    }

    @Override
    public PeerTaxonomy contextualize(PeerSTSet context, FragmentationParameter fp) throws SharkKBException {
        PeerTaxonomy contextualize = peerTaxonomy.contextualize(context, fp);
        kb.persist();
        return new FileDumpPeerTaxonomy(kb, contextualize);
    }

    @Override
    public Enumeration<PeerSemanticTag> peerTags() throws SharkKBException {
        Enumeration<PeerSemanticTag> peerSemanticTagEnumeration = peerTaxonomy.peerTags();
        ArrayList<PeerSemanticTag> list = new ArrayList<>();
        while (peerSemanticTagEnumeration.hasMoreElements()){
            list.add(new FileDumpPeerSemanticTag(kb, peerSemanticTagEnumeration.nextElement()));
        }
        return Collections.enumeration(list);
    }

    @Override
    public PeerTXSemanticTag getSemanticTag(String[] sis) throws SharkKBException {
        return new FileDumpPeerTXSemanticTag(this.kb, peerTaxonomy.getSemanticTag(sis));
    }
}
