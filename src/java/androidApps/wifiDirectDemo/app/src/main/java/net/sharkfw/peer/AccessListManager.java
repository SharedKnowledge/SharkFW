package net.sharkfw.peer;

import java.util.ArrayList;
import java.util.Iterator;
import net.sharkfw.knowledgeBase.PeerSemanticTag;
import net.sharkfw.knowledgeBase.PropertyHolder;
import net.sharkfw.knowledgeBase.SharkCSAlgebra;
import net.sharkfw.knowledgeBase.SharkKBException;
import net.sharkfw.knowledgeBase.SystemPropertyHolder;
import net.sharkfw.knowledgeBase.inmemory.InMemoSharkKB;
import net.sharkfw.system.L;
import net.sharkfw.system.Util;

/**
 *
 * @author thsc
 */
public class AccessListManager implements WhiteAndBlackListManager {
    private final SystemPropertyHolder ph;
    private final String uniqueName;
    
    public AccessListManager(String uniqueName, SystemPropertyHolder propertyHolder) {
        this.ph = propertyHolder;
        this.uniqueName = uniqueName;
    }

    private ArrayList<PeerSemanticTag> blackList = new ArrayList<>();
    private ArrayList<PeerSemanticTag> whiteList = new ArrayList<>();
    
    public static final String WHITE_LIST = "SharkCore_whiteList";
    public static final String BLACK_LIST = "SharkCore_blackList";
    public static final String USE_WHITE_LIST = "SharkCore_useWhiteList";
    
    /**
     * Add or remove peer to/from blacklist (filter.
     * @param peer 
     * @param accept true: peer invitations are accepted and result in a 
     * invitation notification: false: Invitations are dropped without further
     * comments
     */
    @Override
    public void acceptPeer(PeerSemanticTag peer, boolean accept) {
        if(accept) {
            // add to white list
            this.whiteList.add(InMemoSharkKB.createInMemoCopy(peer));
            
            // try to remove from backlist
            Iterator<PeerSemanticTag> peerIter = this.blackList.iterator();
            while(peerIter.hasNext()) {
                PeerSemanticTag blackPeer = peerIter.next();
                
                if(SharkCSAlgebra.identical(blackPeer, peer)) {
                    this.blackList.remove(blackPeer);
                    return;
                }
            }
        } else {
            // make a copy and add to black list
            this.blackList.add(InMemoSharkKB.createInMemoCopy(peer));
            
            // try to remove from whitelist
            Iterator<PeerSemanticTag> peerIter = this.whiteList.iterator();
            while(peerIter.hasNext()) {
                PeerSemanticTag blackPeer = peerIter.next();
                
                if(SharkCSAlgebra.identical(blackPeer, peer)) {
                    this.whiteList.remove(blackPeer);
                    return;
                }
            }
        }

        // remember those settings
        try {
            this.persist();
        }
        catch(SharkKBException skbe) {
            L.e("cannot save shark net engine status", this);
        }
    }

    private boolean useWhiteList = false;
    
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
     * @param whiteYes
     */
    @Override
    public void useWhiteList(boolean whiteYes) {
        this.useWhiteList = whiteYes;
    }
    
    @Override
    public void useBlackWhiteList(boolean on) {
        this.useWhiteList(!on);
    }
    
    private boolean isIn(Iterator<PeerSemanticTag> peerIter, PeerSemanticTag peer) {
        if(peerIter == null) {
            return false;
        }
        
        while(peerIter.hasNext()) {
            PeerSemanticTag pst = peerIter.next();
            if(SharkCSAlgebra.identical(pst, peer)) {
                return true;
            }
        }
        
        return false;
    }

    /**
     * Move to core.SharkEngine soon.
     * @param sender
     * @return 
     */
    @Override
    public boolean isAccepted(PeerSemanticTag sender) {
        if(this.useWhiteList) {
            if(sender == null) {
                return false;
            }
            
            return this.isIn(this.whiteList.iterator(), sender);
        } else {
            if(sender == null) {
                return true;
            }
            return !this.isIn(this.blackList.iterator(), sender);
        }
    }    
    
    public Iterator<PeerSemanticTag> getWhiteList() {
        return this.whiteList.iterator();
    }
    
    public void persist() throws SharkKBException {
        if(this.ph != null) {
            // black / white list manager - move to SharkEngine
            String serializedList = Util.PSTArrayList2String(whiteList);
            this.ph.setProperty(WHITE_LIST, serializedList);

            serializedList = Util.PSTArrayList2String(blackList);
            this.ph.setSystemProperty(BLACK_LIST, serializedList);

            this.ph.setSystemProperty(USE_WHITE_LIST, Boolean.toString(this.useWhiteList));
            
            // others - move to J2SEAndroidSharkEngine
            
            
        }
    }
    
    public final void refreshStatus() throws SharkKBException {
        if(this.ph != null) {
            // restore white and black list and set guardKP
            // white list
            String serializedList = this.ph.getSystemProperty(WHITE_LIST);
            if(serializedList != null) {
                try {
                    this.whiteList = Util.String2PSTArrayList(serializedList);
                } catch (SharkKBException ex) {
                    // TODO
                }
            }

            // black list
            serializedList = this.ph.getSystemProperty(BLACK_LIST);
            if(serializedList != null) {
                try {
                    this.blackList = Util.String2PSTArrayList(serializedList);
                } catch (SharkKBException ex) {
                    // TODO
                }
            }

            if(this. whiteList == null) {
                this.whiteList = new ArrayList<PeerSemanticTag>();
            }

            if(this.blackList == null) {
                this.blackList = new ArrayList<PeerSemanticTag>();
            }

            this.useWhiteList = Boolean.parseBoolean(this.ph.getSystemProperty(USE_WHITE_LIST));
        }
    }
}
