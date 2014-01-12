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
    
    public SigningPeer(){
    }

    public String[] getPeerSI(){
        return mPeerSI;
    }
    
    public byte[] getSignature(){
        return mSignatur;
    }
    
    public void setPeerSI(String[] sis){
        mPeerSI = sis.clone();
    }
    
    public void setSignature(byte[] signature){
        mSignatur = signature;
    }
    
}
