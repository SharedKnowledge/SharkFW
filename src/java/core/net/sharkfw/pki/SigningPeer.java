/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sharkfw.pki;

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
