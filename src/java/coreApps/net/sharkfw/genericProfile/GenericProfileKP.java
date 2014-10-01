package net.sharkfw.genericProfile;

import java.util.ArrayList;
import java.util.Iterator;
import net.sharkfw.knowledgeBase.ContextCoordinates;
import net.sharkfw.knowledgeBase.ContextPoint;
import net.sharkfw.knowledgeBase.Knowledge;
import net.sharkfw.knowledgeBase.SharkCS;
import net.sharkfw.knowledgeBase.SharkCSAlgebra;
import net.sharkfw.knowledgeBase.SharkKB;
import net.sharkfw.knowledgeBase.SharkKBException;
import net.sharkfw.knowledgeBase.SharkVocabulary;
import net.sharkfw.knowledgeBase.inmemory.InMemoSharkKB;
import net.sharkfw.peer.KEPConnection;
import net.sharkfw.peer.SharkEngine;
import net.sharkfw.peer.StandardKP;
import net.sharkfw.system.L;

/**
 *
 * @author s0540042 s0539752
 */
public class GenericProfileKP extends StandardKP {

    public GenericProfileKP(SharkEngine se, SharkCS interest, SharkKB kb) {
        super(se, interest, kb);

    }

    protected void doInsert(GenericProfileImpl profile, KEPConnection response) {

    }

    protected void doInsert(ContextCoordinates interest, KEPConnection response) {

    }

    protected void doInsert(byte[] daten, KEPConnection response) {

    }

    protected void doExpose(GenericProfileImpl profile, KEPConnection response) {
    }

    protected void doExpose(ContextCoordinates interes, KEPConnection response) {
    }

    protected void doExpose(byte[] date, KEPConnection response) {
    }

}
