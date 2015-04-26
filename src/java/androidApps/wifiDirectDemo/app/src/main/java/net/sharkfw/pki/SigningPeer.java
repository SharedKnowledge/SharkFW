/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sharkfw.pki;

import net.sharkfw.knowledgeBase.PeerSemanticTag;

/**
 *
 * @author df
 */
public class SigningPeer {
    
    private String[] mPeerSI;
    private byte[] mSignatur;

    /**
     * @deprecated use other constructor.
     */
    public SigningPeer(){
    }

    public SigningPeer(PeerSemanticTag peer, byte[] signature){
        this(peer.getSI(), signature);
    }

    public SigningPeer(String[] peerSI, byte[] signature){
        this();
        this.mPeerSI = peerSI;
        this.mSignatur = signature;
    }

    public String[] getPeerSI(){
        return mPeerSI;
    }
    
    public byte[] getSignature(){
        return mSignatur;
    }
    
    /**
     * @param sis 
     * @deprecated 
     */
    public void setPeerSI(String[] sis){
        mPeerSI = sis.clone();
    }
    
    /**
     * @param signature 
     * @deprecated 
     */
    public void setSignature(byte[] signature){
        mSignatur = signature;
    }
    
}
