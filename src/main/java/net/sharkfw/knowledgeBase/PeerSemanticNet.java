package net.sharkfw.knowledgeBase;

import java.util.Enumeration;

/**
 * <p>A SemanticNet covering peer tags.</p>
 *
 * <p>SemanticNets offer a way to relate tags to each using custom relationtypes.
 * This class also implements the {@link PeerTaxonomy}, as all Taxonomies can be
 * seen a special sort of {@link SemanticNet} (w/ restricted association types as in {@link Taxonomy}).</p>
 *
 * <p>This class thus unites the SemanticNet and the PeerTaxonomy under one roof.
 * It also adds its own methods, which are related to handling PeerAssociatedSemanticTags.</p>
 *
 * @author mfi
 */
public interface PeerSemanticNet extends SemanticNet {
    
    public PeerSTSet asPeerSTSet();

    public PeerSNSemanticTag createSemanticTag(
            String name, String[] sis, String[] addresses) throws SharkKBException;

    public PeerSNSemanticTag createSemanticTag(
            String name, String si, String[] addresses) throws SharkKBException;

    public PeerSNSemanticTag createSemanticTag(
            String name, String si, String address) throws SharkKBException;

    public PeerSNSemanticTag createSemanticTag(
            String name, String[] sis, String address) throws SharkKBException;

    @Override
    public PeerSNSemanticTag getSemanticTag(String[] sis) throws SharkKBException;

    @Override
    public PeerSNSemanticTag getSemanticTag(String si) throws SharkKBException;
    
    
    @Override
    public PeerSemanticNet fragment(SemanticTag anchor, FragmentationParameter fp) throws SharkKBException;
    
    public Enumeration<PeerSemanticTag> peerTags() throws SharkKBException;
}
