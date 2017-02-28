package net.sharkfw.knowledgeBase.persistent.fileDump;

import net.sharkfw.asip.ASIPInterest;
import net.sharkfw.asip.ASIPSpace;
import net.sharkfw.knowledgeBase.*;

/**
 * Created by j4rvis on 2/28/17.
 */
public class FileDumpSharkVocabulary implements SharkVocabulary {

    protected final FileDumpSharkKB kb;
    private final SharkVocabulary vocabulary;

    public FileDumpSharkVocabulary(FileDumpSharkKB kb, SharkVocabulary vocabulary) {
        this.kb = kb;
        this.vocabulary = vocabulary;
    }

    @Override
    public PeerSemanticTag getOwner() {
        return new FileDumpPeerSemanticTag(kb, vocabulary.getOwner());
    }

    @Override
    public ASIPSpace asASIPSpace() throws SharkKBException {
        return new FileDumpASIPSpace(kb, vocabulary.asASIPSpace());
    }

    @Override
    public ASIPInterest asASIPInterest() throws SharkKBException {
        return new FileDumpASIPInterest(kb, vocabulary.asASIPInterest());
    }

    @Override
    public STSet getTopicSTSet() throws SharkKBException {
        return new FileDumpSTSet(kb, vocabulary.getTopicSTSet());
    }

    @Override
    public SemanticNet getTopicsAsSemanticNet() throws SharkKBException {
        return new FileDumpSemanticNet(kb, vocabulary.getTopicsAsSemanticNet());
    }

    @Override
    public Taxonomy getTopicsAsTaxonomy() throws SharkKBException {
        return new FileDumpTaxonomy(kb, vocabulary.getTopicsAsTaxonomy());
    }

    @Override
    public STSet getTypeSTSet() throws SharkKBException {
        return new FileDumpSTSet(kb, vocabulary.getTypeSTSet());
    }

    @Override
    public SemanticNet getTypesAsSemanticNet() throws SharkKBException {
        return new FileDumpSemanticNet(kb, vocabulary.getTypesAsSemanticNet());
    }

    @Override
    public Taxonomy getTypesAsTaxonomy() throws SharkKBException {
        return new FileDumpTaxonomy(kb, vocabulary.getTypesAsTaxonomy());
    }

    @Override
    public PeerSTSet getPeerSTSet() throws SharkKBException {
        return new FileDumpPeerSTSet(kb, vocabulary.getPeerSTSet());
    }

    @Override
    public PeerSemanticNet getPeersAsSemanticNet() throws SharkKBException {
        return new FileDumpPeerSemanticNet(kb, vocabulary.getPeersAsSemanticNet());
    }

    @Override
    public PeerTaxonomy getPeersAsTaxonomy() throws SharkKBException {
        return new FileDumpPeerTaxonomy(kb, vocabulary.getPeersAsTaxonomy());
    }

    @Override
    public TimeSTSet getTimeSTSet() throws SharkKBException {
        return new FileDumpTimeSTSet(kb, vocabulary.getTimeSTSet());
    }

    @Override
    public SpatialSTSet getSpatialSTSet() throws SharkKBException {
        return new FileDumpSpatialSTSet(kb, vocabulary.getSpatialSTSet());
    }

    @Override
    public ASIPInterest contextualize(ASIPSpace as) throws SharkKBException {
        ASIPInterest contextualize = vocabulary.contextualize(as);
        kb.persist();
        return new FileDumpASIPInterest(kb, contextualize);
    }

    @Override
    public ASIPInterest contextualize(ASIPSpace as, FPSet fps) throws SharkKBException {
        ASIPInterest contextualize = vocabulary.contextualize(as, fps);
        kb.persist();
        return new FileDumpASIPInterest(kb, contextualize);
    }
}
