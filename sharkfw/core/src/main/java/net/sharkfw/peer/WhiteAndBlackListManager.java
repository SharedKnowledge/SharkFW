package net.sharkfw.peer;

import net.sharkfw.knowledgeBase.PeerSemanticTag;

/**
 * @author thsc
 */
public interface WhiteAndBlackListManager {
    
    /**
     * Add or remove peer to/from blacklist (filter.
     * @param peer 
     * @param accept true: peer invitations are accepted and result in a 
     * invitation notification: false: Invitations are dropped without further
     * comments
     */
    void acceptPeer(PeerSemanticTag peer, boolean accept);

    /**
     * Trigger what policy is used. This guard manages a white and
     * a black list.
     * 
     * Using a white list is more restrictive that using a black list:
     * 
     * <ul>
     * <li>Using a whitelist means: Only invitation are excepted which
     * are send from peer who a explicitely allowed to invite this peer.
     * <li>Using a black list means that peer can be set on a black list. Those
     * peers are not allowed to invite.
     * </ul>
     * 
     * The difference is for unknown peers: Invitation of unknown peers are
     * accepted with a black list but not with a whitelist
     * @param whiteList
     */
    void useWhiteList(boolean whiteList);
    
    /**
     * Method can be used to switch black and white list management.
     * Use useWhiteList to define what kind of list is to be used.
     * 
     * @param on true.. management is on, false management is off.
     */
    void useBlackWhiteList(boolean on);
    
    /**
     * Returns whether this peer is allowed to send a message.
     * Result depends on content of list of course but also if white or
     * black list is to be used.
     * 
     * @param peer
     * @return 
     */
    boolean isAccepted(PeerSemanticTag peer);
    
    /**
     * @return Iterator of white listed peers
     */
    //Iterator<PeerSemanticTag> getWhiteList();
}
