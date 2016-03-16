package net.sharkfw.genericProfile;

import java.util.ArrayList;
import java.util.Iterator;
import net.sharkfw.knowledgeBase.ContextCoordinates;
import net.sharkfw.knowledgeBase.ContextPoint;
import net.sharkfw.knowledgeBase.Information;
import net.sharkfw.knowledgeBase.PeerSemanticTag;
import net.sharkfw.knowledgeBase.SharkKB;
import net.sharkfw.knowledgeBase.SharkKBException;

/**
 * This class implements the GenericProfile.
 *
 * Generic Profiles are stored within a Shark knowledge base.
 *
 * Each generic profile has an own ContextPoint, the profileCP. The profileCP
 * enables the generic profile to store generic data (byte arrays) with keys as
 * anchors. Through the keys the generic information can be accessed.
 *
 * Not only generic information, but also interests can be stored by a generic
 * Profile. Each interest is an additional ContextPoint of the generic Profile,
 * but only the profileCP can store generic information.
 *
 * Of course, generic information and interests of the generic Profile can also
 * be removed.
 *
 * generic information of the generic Profile can reveice an Expose Status. This
 * status declares which information can be send to other peers ans which shall
 * not be send.
 *
 * @author dufe (540042) & resc (539752)
 */
public class GenericProfileImpl implements GenericProfile {

    /**
     * Generic Information is stored underneath a ContextPoint, the profileCP
     */
    private ContextPoint profileCP;
    
    

    //protected final SharkEngine se;
    /**
     * Generic Profiles are stored in a Shark knowledge base
     */
    protected final SharkKB kb;

    /**
     * Generates a generic profile
     * @param kb
     * @throws net.sharkfw.knowledgeBase.SharkKBException
     */
    public GenericProfileImpl(SharkKB kb) throws SharkKBException {

        this.kb = kb;
       profileCP = kb.createContextPoint(null);
    }

    /**
     * Adds an interest to the generic profile.
     *
     * @param interest the interest which shall be added
     * @throws SharkKBException
     */
    @Override
    public void addInterest(ContextCoordinates interest) throws SharkKBException {
        if (interest != null) {
            kb.createContextPoint(interest);
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
    @Override
    public ContextPoint getInterest(ContextCoordinates interest) throws SharkKBException {
        if (interest != null) {
            
            return kb.getContextPoint(interest);
        } else {
            return null;
        }
    }

    /**
     * Adds generic information to the generic profile.
     *
     * @param key defines with which key the information can later be received
     * or removed. The key has to be unique!
     * @param daten the generic information which shall be stored
     * @throws SharkKBException
     */
    @Override
    public void addInformation(String key, byte[] daten) throws SharkKBException {
        if ((key != null) && (daten != null) /*&& (profileCP.getInformation(key) == null)*/) {
            try {
                profileCP.addInformation(daten);
            } catch (NullPointerException npe) {
                npe.printStackTrace();
            }

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
    public Iterator<Information> getInformation(String key) throws SharkKBException {
        Iterator<Information> i = null;
        if (key != null) {
            try {
                profileCP.getInformation(key);
                i = profileCP.getInformation(key);
                return i;
            } catch (NullPointerException npe) {
                return i;
            }
        } else {
            return i;
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
        if (key != null) {
            //Access?
        }
    }

    /**
     * Sets the expose status to true for a piece of information of the generic
     * profile. The Peers are now being allowed to receive this piece of
     * information .
     *
     * @param key defines which piece of information expose status shall be
     * changed to ohter peers
     * @param peers defines which other peers' expose status of the selected
     * pieces of information shall be changed.
     * @throws SharkKBException
     */
    @Override
    public void setExposeStatusTrue(String key, ArrayList<PeerSemanticTag> peers) throws SharkKBException {
        String[] si;
        String selectedPeers = "";
        if (peers != null) {
            for(int i = 0; i < peers.size(); i++) {
                si = peers.get(i).getSI();
                selectedPeers += si[0];
                selectedPeers += ",";
            }
            if (profileCP.getProperty(key) == null) {
                profileCP.setProperty(key, selectedPeers);

            } else {
                throw new SharkKBException();
            }
        }
    }

    /**
     * Returns a String Array of Subject Identifiers of Peers who are allowed to
     * receive the information which is stored beneath the given key.
     *
     * @param key defines which information is reckoned.
     * @return a String Array of Subject Identifiers of Peers
     * @throws SharkKBException
     */
    @Override
    public String[] getAllowedPeers(String key) throws SharkKBException {
        if (key != null && profileCP.getProperty(key) != null) {
            String resultPeers = profileCP.getProperty(key);
            String[] allowedPeers = resultPeers.split(",");
            return allowedPeers;
        } else {
            return null;
        }
    }

     /**
     * Sets the expose status to false for a piece of information of the generic
     * profile. The Peers are not allowed to receive this piece of
     * information anymore.
     *
     * @param key defines which piece of information expose status shall be
     * changed to ohter peers
     * @param peers defines which other peers' expose status of the selected
     * pieces of information shall be changed.
     * @throws SharkKBException
     */
    @Override
    public void setExposeStatusFalse(String key) throws SharkKBException {
        if (profileCP.getProperty(key) != null) {
            profileCP.removeProperty(key);
        } else {
            throw new SharkKBException();
        }
    }

}
