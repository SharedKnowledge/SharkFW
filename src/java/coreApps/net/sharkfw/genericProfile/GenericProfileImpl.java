
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
 * This class implements the GenericProfile.
 * 
 * Generic Profiles are stored within a Shark knowledge base. 
 * 
 * Each generic profile has an own ContextPoint, the profileCP. The profileCP
 * enables the generic profile to store generic data (byte arrays) with keys
 * as anchors. Through the keys the generic information can be accessed. 
 * 
 * Not only generic information, but also interests can be stored by a generic 
 * Profile. Each interest is an additional ContextPoint of the generic Profile,
 * but only the profileCP can store generic information.
 * 
 * Of course, generic information and interests of the generic Profile can also
 * be removed.
 * 
 * generic information of the generic Profile can reveice an Expose Status. 
 * This status declares which information can be send to other peers ans which
 * shall not be send.
 *
 * @author dufe (540042) & resc (539752)
 */
public class GenericProfileImpl implements GenericProfile {

    /** Generic Information is stored underneath a ContextPoint, the profileCP */
    private ContextPoint profileCP;
    //protected final SharkEngine se;
    /** Generic Profiles are stored in a Shark knowledge base */
    protected final SharkKB kb;
    /** Generates a generic profile */
    public GenericProfileImpl(SharkKB kb) throws SharkKBException {
        
        this.kb = kb;

    }
/**
 * Adds an interest to the generic profile.
 * 
 * @param interest  the interest which shall be added
 * @throws SharkKBException 
 */
    @Override
    public void addInterest(ContextCoordinates interest) throws SharkKBException {
        if (interest != null) {
            kb.createInterest(interest);
        }
    }
    
/**
 * Removes an interest from the generic profile.
 * 
 * @param interest the interest which shall be removed
 * @throws SharkKBException 
 */
    @Override
    public void removeInterest(ContextCoordinates interest) throws SharkKBException {
        if (interest != null) {
            kb.removeContextPoint(interest);

        }
    }

    /**
     * Receives an interest from the genric profile.
     * 
     * @param interest the interest which shall be received
     * @return the requested interest or null
     * @throws SharkKBException 
     */
    public ContextPoint getInterest(ContextCoordinates interest) throws SharkKBException {
        if (interest != null) {
            return kb.getContextPoint(interest);
        }
        else {
            return null;
        }
    }
    
    /**
     * Adds generic information to the generic profile.
     * 
     * @param key defines with which key the information can later be received or removed. The key has to be unique! 
     * @param daten the generic information which shall be stored
     * @throws SharkKBException 
     */
    @Override
    public void addInformation(String key, byte[] daten) throws SharkKBException {
        if (key != null && daten != null &&  (profileCP.getInformation(key) == null)) {
            profileCP.addInformation(daten);
        }
    }

    /**
     * Receives generic information from the generic profile.
     * 
     * @param key defines which information shall be received
     * @return the requested information or null
     * @throws SharkKBException 
     */
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

    /**
     * Removes generic information from the generic profile
     * 
     * @param key defines which information shall be removed
     * @throws SharkKBException 
     */
    @Override
    public void removeInformation(String key) throws SharkKBException {
        if (key != null ) {
            //Access?
        }
    }

    /**
     * Sets an expose status for generic information of the generic profile.
     * The expose status determines whether the information can be send to other
     * peers or not.
     * 
     * @param keys defines which pieces' of information expose status shall be changed
     * @param ExposeStatus 0 = can not be sent to ohter peers, 1 = can be sent to ohter peers
     * @param peers defines which other peers' expose status of the selected pieces of information shall be changed.
     * @throws SharkKBException 
     */
    @Override
    public void setExposeStatus(String[] keys, int ExposeStatus, Iterator<PeerSemanticTag> peers) throws SharkKBException {
        //TODO
    }

    /**
     * Returns an iterator of peers which are allowed to receive the information which
     * is stored beneath the given key.
     * 
     * @param key defines which information is reckoned.
     * @return an iterator of peers which are allowed to receive the information which is stored beneath the given key.
     * @throws SharkKBException 
     */
    @Override
    public Iterator<PeerSemanticTag> getAllowedPeers(String key) throws SharkKBException {
        //TODO
        return null;
    }

}
