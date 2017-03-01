package net.sharkfw.knowledgeBase.persistent.dump;

import net.sharkfw.asip.ASIPInterest;
import net.sharkfw.asip.ASIPSpace;
import net.sharkfw.knowledgeBase.*;

/**
 * Created by j4rvis on 2/28/17.
 */
public class DumpSharkVocabulary implements SharkVocabulary {

    protected final DumpSharkKB kb;
    private final SharkVocabulary vocabulary;

    public DumpSharkVocabulary(DumpSharkKB kb, SharkVocabulary vocabulary) {
        this.kb = kb;
        this.vocabulary = vocabulary;
    }

    @Override
    public PeerSemanticTag getOwner() {
        return new DumpPeerSemanticTag(kb, vocabulary.getOwner());
    }

    @Override
    public ASIPSpace asASIPSpace() throws SharkKBException {
        return new DumpASIPSpace(kb, vocabulary.asASIPSpace());
    }

    @Override
    public ASIPInterest asASIPInterest() throws SharkKBException {
        return new DumpASIPInterest(kb, vocabulary.asASIPInterest());
    }

    @Override
    public STSet getTopicSTSet() throws SharkKBException {
        return new DumpSTSet(kb, vocabulary.getTopicSTSet());
    }

    @Override
    public SemanticNet getTopicsAsSemanticNet() throws SharkKBException {
        return new DumpSemanticNet(kb, vocabulary.getTopicsAsSemanticNet());
    }

    @Override
    public Taxonomy getTopicsAsTaxonomy() throws SharkKBException {
        return new DumpTaxonomy(kb, vocabulary.getTopicsAsTaxonomy());
    }

    @Override
    public STSet getTypeSTSet() throws SharkKBException {
        return new DumpSTSet(kb, vocabulary.getTypeSTSet());
    }

    @Override
    public SemanticNet getTypesAsSemanticNet() throws SharkKBException {
        return new DumpSemanticNet(kb, vocabulary.getTypesAsSemanticNet());
    }

    @Override
    public Taxonomy getTypesAsTaxonomy() throws SharkKBException {
        return new DumpTaxonomy(kb, vocabulary.getTypesAsTaxonomy());
    }

    @Override
    public PeerSTSet getPeerSTSet() throws SharkKBException {
        return new DumpPeerSTSet(kb, vocabulary.getPeerSTSet());
    }

    @Override
    public PeerSemanticNet getPeersAsSemanticNet() throws SharkKBException {
        return new DumpPeerSemanticNet(kb, vocabulary.getPeersAsSemanticNet());
    }

    @Override
    public PeerTaxonomy getPeersAsTaxonomy() throws SharkKBException {
        return new DumpPeerTaxonomy(kb, vocabulary.getPeersAsTaxonomy());
    }

    @Override
    public TimeSTSet getTimeSTSet() throws SharkKBException {
        return new DumpTimeSTSet(kb, vocabulary.getTimeSTSet());
    }

    @Override
    public SpatialSTSet getSpatialSTSet() throws SharkKBException {
        return new DumpSpatialSTSet(kb, vocabulary.getSpatialSTSet());
    }

    @Override
    public ASIPInterest contextualize(ASIPSpace as) throws SharkKBException {
        ASIPInterest contextualize = vocabulary.contextualize(as);
        kb.persist();
        return new DumpASIPInterest(kb, contextualize);
    }

    @Override
    public ASIPInterest contextualize(ASIPSpace as, FPSet fps) throws SharkKBException {
        ASIPInterest contextualize = vocabulary.contextualize(as, fps);
        kb.persist();
        return new DumpASIPInterest(kb, contextualize);
    }
}
