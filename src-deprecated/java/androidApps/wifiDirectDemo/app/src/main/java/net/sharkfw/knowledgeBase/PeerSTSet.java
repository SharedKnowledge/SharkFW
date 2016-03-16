package net.sharkfw.knowledgeBase;

import java.util.Enumeration;


/**
 * <p>PeerSTSet holds a number of PeerSemanticTags.</p>
 *
 * <p>This class offers variations of the {@link STSet} methods, fit for handling {@link PeerSemanticTag}s.
 * No additional functionality is defined.</p>
 *
 * <p>When merging two PeerSTSets, all tags will be present in the local PeerSTSets along with all their addresses.</p>
 * 
 * @author mfi, thsc
 */
public interface PeerSTSet extends STSet {
    public PeerSemanticTag createPeerSemanticTag(String name, String[] sis, String[] addresses) throws SharkKBException;
    public PeerSemanticTag createPeerSemanticTag(String name, String[] sis, String address) throws SharkKBException;
    public PeerSemanticTag createPeerSemanticTag(String name, String si, String[] addresses) throws SharkKBException;
    public PeerSemanticTag createPeerSemanticTag(String name, String si, String address) throws SharkKBException;
    
    @Override
    public PeerSemanticTag getSemanticTag(String[] sis) throws SharkKBException;

    @Override
    public PeerSemanticTag getSemanticTag(String si) throws SharkKBException;

    @Override
    public PeerSTSet fragment(SemanticTag anchor) throws SharkKBException;

    @Override
    public PeerSTSet fragment(SemanticTag anchor, FragmentationParameter fp) throws SharkKBException;
    
    @Override
    PeerSTSet contextualize(Enumeration<SemanticTag> anchor, FragmentationParameter fp) throws SharkKBException;

    @Override
    PeerSTSet contextualize(Enumeration<SemanticTag> anchor) throws SharkKBException;

    @Override
    PeerSTSet contextualize(STSet context) throws SharkKBException;

    @Override
    PeerSTSet contextualize(STSet context, FragmentationParameter fp) throws SharkKBException;
    
    public Enumeration<PeerSemanticTag> peerTags();
}
