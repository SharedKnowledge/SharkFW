package net.sharkfw.knowledgeBase;

import java.util.Enumeration;

/**
 * <p>A STSet organizing its PeerTags in a hierarchical way.</p>
 *
 * <p>All methods in this class act exactly the same way as the methods
 * in {@link Taxonomy} only do they manage {@link PeerHierarchicalSemanticTag}s,
 * instead of {@link HierarchicalSemanticTag}s.</p>
 *
 * <p>A PeerTaxonomy can be seen as a specialised form of {@link PeerSemanticNet}.
 * Containing {@link PeerHierarchicalSemanticTag}s.</p>
 *
 * <p>All methods declared in this interface are variations of the more general
 * methods in Taxonomy. For JavaDoc regarding these methods, please see the
 * more general methods in {@link Taxonomy}.</p>
 * 
 * @author mfi, thsc
 */
public interface PeerTaxonomy extends Taxonomy {

    /**
     * Creates a fragment (copy) in which any group PST is replaces by its members.
     * 
     * Peers can be arranged in hierarchies. A super peer represents a
     * group of peer in this case. In most applications, groups shall be
     * only for local use, e.g. a user might create a group of "enemies".
     * Apparently, it could be wise not to let others know neither there is
     * such a group nor who is on it.
     * 
     * This methode resolves each super peers. Algorithm is pretty simple.
     * Each PST that has sub PST is replace be its sub PST. This process is
     * repeated until all super PST are resolved.
     * 
     * @param pstGroup
     * @return a set of peer semantic tags (in no hierarchical relation)
     */
    PeerTaxonomy resolveSuperPeers(PeerTXSemanticTag pstGroup)
                                throws SharkKBException;
    
    PeerSTSet asPeerSTSet() throws SharkKBException;
    
    @Override
    public PeerTXSemanticTag getSemanticTag(String[] sis) throws SharkKBException;
    
    public PeerTXSemanticTag createPeerTXSemanticTag(String name, String[] sis, String[] addresses) throws SharkKBException;
    public PeerTXSemanticTag createPeerTXSemanticTag(String name, String si, String[] addresses) throws SharkKBException;
    public PeerTXSemanticTag createPeerTXSemanticTag(String name, String[] sis, String address) throws SharkKBException;
    public PeerTXSemanticTag createPeerTXSemanticTag(String name, String si, String address) throws SharkKBException;
    
    //////////////////////////////////////////////////////////////////////////
    //    make methods type safe - should become a generic anyway           //
    //////////////////////////////////////////////////////////////////////////
    
    public void move(PeerTXSemanticTag superPST, PeerTXSemanticTag subPST) 
            throws SharkKBException;
    
    public PeerTaxonomy contextualize(PeerSTSet context, FragmentationParameter fp) throws SharkKBException; 
    
    public Enumeration<PeerSemanticTag> peerTags()  throws SharkKBException;    
}
