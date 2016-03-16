package com.shark.demo.kp;

import java.util.logging.Level;
import java.util.logging.Logger;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.app.Activity;


import net.sharkfw.knowledgeBase.ContextCoordinates;
import net.sharkfw.knowledgeBase.Interest;
import net.sharkfw.knowledgeBase.Knowledge;
import net.sharkfw.knowledgeBase.PeerSemanticTag;
import net.sharkfw.knowledgeBase.SemanticTag;
import net.sharkfw.knowledgeBase.SharkCS;
import net.sharkfw.knowledgeBase.SharkKB;
import net.sharkfw.knowledgeBase.SharkKBException;
import net.sharkfw.knowledgeBase.inmemory.InMemoSharkKB;
import net.sharkfw.peer.KEPConnection;
import net.sharkfw.peer.KnowledgePort;
import net.sharkfw.peer.SharkEngine;
import net.sharkfw.system.L;
import com.shark.demo.shark_demo.MainActivity;

import net.sharkfw.system.SharkException;

/**
 * An example KP which will send an interest to the connecting device
 *
 * @author jgig
 */
public class WifiListenerKp extends KnowledgePort {
    private SharkCS myInterest;
    private ConnectionListener connectionListener;
    private SharkCS currentInterest;
    private String logText;

    /**
     * @param se       the shark engine
     */
    public WifiListenerKp(SharkEngine se, PeerSemanticTag myIdentity) {
        super(se);

        SharkKB kb = new InMemoSharkKB();
        PeerSemanticTag me = myIdentity;

        this.myInterest = new InMemoSharkKB().createInterest(null, myIdentity, null, null, null, null, SharkCS.DIRECTION_INOUT);

    }

    @Override
    protected void doInsert(Knowledge knowledge, KEPConnection kepConnection) {
        System.out.print("Knowledge received: (");
        System.out.println(L.knowledge2String(knowledge));
        L.knowledge2String(knowledge);

    }

    @Override
    protected void doExpose(SharkCS interest, KEPConnection kepConnection) {

        MainActivity.log("interest received " + L.contextSpace2String(interest));
        if (isAnyInterest(interest)) {
            Log.d("WifiKp", "any interest");
           MainActivity.log("any interest received " + L.contextSpace2String(interest));
            try {
                MainActivity.log("WifiListenerKp - trying to send interest\n" + L.contextSpace2String(myInterest));
                kepConnection.expose(myInterest);
            } catch (SharkException ex) {
              MainActivity.log("WifiListenerKp - problems:" + ex.getMessage());
            }
        } if (isPeerInterest(interest)){
            MainActivity.log("Peer Interest received");
            MainActivity.log("Peer interest received " + L.contextSpace2String(interest));
            connectionListener.onConnectionEstablished(kepConnection);
        }

    }




    public void setConnectionListener(ConnectionListener connectionListener) {
        this.connectionListener = connectionListener;
    }
    private boolean isAnyInterest(SharkCS theInterest) {
        return (theInterest.isAny(SharkCS.DIM_TOPIC) && theInterest.isAny(SharkCS.DIM_ORIGINATOR) &&
                theInterest.isAny(SharkCS.DIM_LOCATION) && theInterest.isAny(SharkCS.DIM_DIRECTION) &&
                theInterest.isAny(SharkCS.DIM_PEER) && theInterest.isAny(SharkCS.DIM_REMOTEPEER) &&
                theInterest.isAny(SharkCS.DIM_TIME)) ;
    }
    private boolean isPeerInterest(SharkCS theInterest) {
        return (theInterest.isAny(SharkCS.DIM_TOPIC) && !theInterest.isAny(SharkCS.DIM_ORIGINATOR) &&
                theInterest.isAny(SharkCS.DIM_LOCATION) && theInterest.isAny(SharkCS.DIM_DIRECTION) &&
                theInterest.isAny(SharkCS.DIM_PEER) && theInterest.isAny(SharkCS.DIM_REMOTEPEER) &&
                theInterest.isAny(SharkCS.DIM_TIME)) ;
    }
}

