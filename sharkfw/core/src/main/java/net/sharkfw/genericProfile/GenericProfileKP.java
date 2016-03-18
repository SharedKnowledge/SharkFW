package net.sharkfw.genericProfile;

import net.sharkfw.knowledgeBase.ContextCoordinates;
import net.sharkfw.knowledgeBase.SharkCS;
import net.sharkfw.knowledgeBase.SharkKB;
import net.sharkfw.knowledgeBase.SharkKBException;
import net.sharkfw.peer.KEPConnection;
import net.sharkfw.peer.SharkEngine;
import net.sharkfw.peer.StandardKP;

/**
 *
 * @author s0540042 s0539752
 */
public class GenericProfileKP extends StandardKP {

    GenericProfileImpl profile;

    public GenericProfileKP(SharkEngine se, SharkCS interest, SharkKB kb, GenericProfileImpl profile) {
        super(se, interest, kb);
        if (profile != null) {
            this.profile = profile;
        }

    }

    protected void doInsert(String key, byte[] daten, KEPConnection response) {

        try {
            profile.addInformation(key, daten);
        } catch (SharkKBException ex) {

        }
    }

    protected void doExpose(ContextCoordinates interest, KEPConnection response) {
        try {
            profile.addInterest(interest);
        } catch (SharkKBException ex) {

        }
    }
    
    public GenericProfileImpl getGenericProfile() {
        return profile;
    }

}
