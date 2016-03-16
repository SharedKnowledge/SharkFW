package net.sharkfw.genericProfile;

import java.util.ArrayList;
import java.util.Iterator;
import net.sharkfw.knowledgeBase.ContextCoordinates;
import net.sharkfw.knowledgeBase.ContextPoint;
import net.sharkfw.knowledgeBase.Information;
import net.sharkfw.knowledgeBase.PeerSemanticTag;
import net.sharkfw.knowledgeBase.SharkKBException;

/**
<p><strong>Management of interests and information</strong> <br />
 * Interests and information can be stored and returned by a generic profile.
 * </p>
 * <p><strong>Expose status and allowed peers</strong> <br />
 * The profile has the ability to set an expose status in order to determine
 * which other peers are allowed to receive information from the profile.
 * </p> 
 * @author s0540042 and s0539752
 */
public interface GenericProfile {
    /**
     * Adds an interest to the existing interests of the profile. 
     * @param interest  the interest that shall be added
     * @throws SharkKBException 
     */
    public void addInterest(ContextCoordinates interest) throws SharkKBException;
    /**
     * Deletes an interest of the profile.
     * @param interest the interest that shall be deleted
     * @throws SharkKBException 
     */
    public void removeInterest(ContextCoordinates interest) throws SharkKBException;
     /**
     * Delivers an interest of the profile.
     * @param interest the interest that shall be delivered
     * @throws SharkKBException 
     */
    public ContextPoint getInterest(ContextCoordinates interest) throws SharkKBException;
    
    /**
     * Adds simple information of the profile.
     * @param key   assures the access to the information
     * @param daten represents the information
     * @throws SharkKBException 
     */
    public void addInformation(String key, byte[] daten) throws SharkKBException;
    /**
     * Delivers stored information of the profile with the help of the key
     * @param key   determines which information shall be delivered
     * @return  the requested information
     * @throws SharkKBException 
     */
    public Iterator<Information> getInformation(String key) throws SharkKBException;
    /**
     * Deletes the information which is saved under the given key
     * @param key   determines which information shall be deleted 
     * @throws SharkKBException 
     */
    public void removeInformation(String key) throws SharkKBException;
    
    /**
     * Allows the peers to receive a piece of information which is determined by the key.
     * @param key  determines which information expose status shall be changed
     * @param peers determines which peers expose status of the different pieces of information shall be changed
     * @throws SharkKBException 
     */
    public void setExposeStatusTrue(String key,  ArrayList<PeerSemanticTag> peers) throws SharkKBException;
    
    
   /**
     * Disallows the peers to receive a piece of information which is determined by the key.
     * @param key  determines which information expose status shall be changed
     * @param peers determines which peers expose status of the different pieces of information shall be changed
     * @throws SharkKBException 
     */
    public void setExposeStatusFalse(String key) throws SharkKBException;
    
    
    //public void setExposeStatusFalse(String key, Iterator<PeerSemanticTag> peers ) throws SharkKBException;
    
    /**
     * Passes back the respective Subject Identifier of the Peers which are able to receive the piece of information from the profile.
     * @param key   determines which information expose status shall be changed
     * @return  returns the authorized peers
     * @throws SharkKBException 
     */
    public String[] getAllowedPeers(String key) throws SharkKBException;
}
