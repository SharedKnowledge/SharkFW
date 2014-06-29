/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sharkfw.genericProfile;

import java.util.Iterator;
import net.sharkfw.knowledgeBase.ContextCoordinates;
import net.sharkfw.knowledgeBase.ContextPoint;
import net.sharkfw.knowledgeBase.Information;
import net.sharkfw.knowledgeBase.PeerSTSet;
import net.sharkfw.knowledgeBase.PeerSemanticTag;
import net.sharkfw.knowledgeBase.SharkCS;
import net.sharkfw.knowledgeBase.SharkKB;
import net.sharkfw.knowledgeBase.SharkKBException;
import net.sharkfw.knowledgeBase.inmemory.InMemoSharkKB;
import net.sharkfw.peer.SharkEngine;

/**
 *
 * @author s0540042 dufe & resh
 */
class GenericProfileImpl implements GenericProfile {

    //private  Iterator<ContextPoint> infoSet;
    private ContextPoint profileCP;
    //protected final SharkEngine se;
    protected final SharkKB kb;

    public GenericProfileImpl(SharkKB kb) throws SharkKBException {
        
        this.kb = kb;

    }

    @Override
    public void addInterest(ContextCoordinates interest) throws SharkKBException {
        if (interest != null) {
            kb.createInterest(interest);
        }
    }

    @Override
    public void removeInterest(ContextCoordinates interest) throws SharkKBException {
        if (interest != null) {
            kb.removeContextPoint(interest);

        }
    }

    public ContextPoint getInterest(ContextCoordinates interest) throws SharkKBException {
        if (interest != null) {
            return kb.getContextPoint(interest);
        }
        else {
            return null;
        }
    }
    
    //SCHLUESSEL?
    @Override
    public void addInformation(String key, byte[] daten) throws SharkKBException {
        if (key != null && daten != null &&  (profileCP.getInformation(key) == null)) {
            profileCP.addInformation(daten);
        }
    }

    @Override
    public byte[] getInformation(String key) throws SharkKBException {
        if (key != null && (profileCP.getInformation(key) != null)) {
            Iterator<Information> i = profileCP.getInformation(key);
            return null;
        }
        else {
            return null;
        }
    }

    @Override
    public void removeInformation(String key) throws SharkKBException {
        if (key != null ) {
            //Access?
        }
    }

    @Override
    public void setExposeStatus(String[] keys, int ExposeStatus, Iterator<PeerSemanticTag> peers) throws SharkKBException {
        //Access?
    }

    @Override
    public Iterator<PeerSemanticTag> getAllowedPeers(String key) throws SharkKBException {
        //Access?
        return null;
    }

}
