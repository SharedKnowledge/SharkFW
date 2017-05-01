package net.sharkfw.knowledgeBase.persistent.sql;

import net.sharkfw.asip.ASIPInterest;
import net.sharkfw.asip.ASIPSpace;
import net.sharkfw.knowledgeBase.*;

public class SqlVocabulary implements SharkVocabulary {



    @Override
    public PeerSemanticTag getOwner() {
        return null;
    }

    @Override
    public ASIPSpace asASIPSpace() throws SharkKBException {
        return null;
    }

    @Override
    public ASIPInterest asASIPInterest() throws SharkKBException {
        return null;
    }

    @Override
    public STSet getTopicSTSet() throws SharkKBException {
        return null;
    }

    @Override
    public SemanticNet getTopicsAsSemanticNet() throws SharkKBException {
        return null;
    }

    @Override
    public Taxonomy getTopicsAsTaxonomy() throws SharkKBException {
        return null;
    }

    @Override
    public STSet getTypeSTSet() throws SharkKBException {
        return null;
    }

    @Override
    public SemanticNet getTypesAsSemanticNet() throws SharkKBException {
        return null;
    }

    @Override
    public Taxonomy getTypesAsTaxonomy() throws SharkKBException {
        return null;
    }

    @Override
    public PeerSTSet getPeerSTSet() throws SharkKBException {
        return null;
    }

    @Override
    public PeerSemanticNet getPeersAsSemanticNet() throws SharkKBException {
        return null;
    }

    @Override
    public PeerTaxonomy getPeersAsTaxonomy() throws SharkKBException {
        return null;
    }

    @Override
    public TimeSTSet getTimeSTSet() throws SharkKBException {
        return null;
    }

    @Override
    public SpatialSTSet getSpatialSTSet() throws SharkKBException {
        return null;
    }

    @Override
    public ASIPInterest contextualize(ASIPSpace as) throws SharkKBException {
        return null;
    }

    @Override
    public ASIPInterest contextualize(ASIPSpace as, FPSet fps) throws SharkKBException {
        return null;
    }
}
