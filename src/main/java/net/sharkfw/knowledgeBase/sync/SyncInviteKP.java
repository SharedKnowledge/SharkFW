package net.sharkfw.knowledgeBase.sync;

import net.sharkfw.asip.ASIPInterest;
import net.sharkfw.asip.ASIPKnowledge;
import net.sharkfw.asip.ASIPSpace;
import net.sharkfw.asip.engine.ASIPConnection;
import net.sharkfw.asip.engine.ASIPInMessage;
import net.sharkfw.asip.engine.ASIPSerializer;
import net.sharkfw.knowledgeBase.*;
import net.sharkfw.knowledgeBase.inmemory.InMemoSharkKB;
import net.sharkfw.peer.ContentPort;
import net.sharkfw.peer.KnowledgePort;
import net.sharkfw.peer.SharkEngine;
import net.sharkfw.system.L;
import net.sharkfw.system.SharkException;
import net.sharkfw.system.Util;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Created by j4rvis on 26.08.16.
 */
public class SyncInviteKP extends KnowledgePort {

    public static final String SHARK_SYNC_TYPE_SI = "http://www.sharksystem.net/sync";

    private final HashMap<PeerSemanticTag, Long> lastSeen = new HashMap<>();
    private final int INVITE_INTERVAL;

    private static final String LAST_SEEN_PROPERTY_NAME = "SHARK_SYNC_LAST_SEEN";
    private final PropertyHolder propertyHolder;

    public SyncInviteKP(SharkEngine se, PropertyHolder propertyHolder) {
        this(se, propertyHolder, 1000*60*10 ); // 10 minutes
    }

    public SyncInviteKP(SharkEngine se, PropertyHolder propertyHolder, int interval) {
        super(se);
        this.propertyHolder = propertyHolder;
        INVITE_INTERVAL = interval;
    }

    @Override
    protected void handleInsert(ASIPInMessage message, ASIPConnection asipConnection, ASIPKnowledge asipKnowledge) {

    }

    @Override
    protected void handleExpose(ASIPInMessage message, ASIPConnection asipConnection, ASIPInterest interest) throws SharkKBException {
        if(interest.getSender()!=null){
            Long peerLastSeen = this.lastSeen.get(interest.getSender());
            this.lastSeen.put(interest.getSender(), System.currentTimeMillis());

            if(peerLastSeen!=null){
                if(peerLastSeen < (System.currentTimeMillis() - INVITE_INTERVAL)){

                    // Build invitation interest w/ type: sync, sender: myself and receiver: the message's sender
                    STSet set = InMemoSharkKB.createInMemoSTSet();
                    set.createSemanticTag("SYNC", SHARK_SYNC_TYPE_SI);

                    PeerSTSet inMemoPeerSTSet = InMemoSharkKB.createInMemoPeerSTSet();
                    inMemoPeerSTSet.merge(message.getSender());

                    ASIPInterest asipInterest = InMemoSharkKB.createInMemoASIPInterest(
                            null, set, se.getOwner(), null, inMemoPeerSTSet, null, null, ASIPSpace.DIRECTION_OUT );

                    try {
                        asipConnection.expose(asipInterest, message.getSender().getAddresses());
                    } catch (SharkException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

    }


    private void saveLastSeen() {
        //test
//        PeerSemanticTag p = InMemoSharkKB.createInMemoPeerSemanticTag("Alice", "http://alice.de", "mail://alice@alice.de");
//        Long t = System.currentTimeMillis();
//        this.lastSeen.put(p, t);
//
//        p = InMemoSharkKB.createInMemoPeerSemanticTag("Alice", "http://bob.de", "mail://bob@bob.de");
//        t = System.currentTimeMillis();
//        this.lastSeen.put(p, t);
        // end test

        StringBuilder buf = new StringBuilder();
        Iterator<PeerSemanticTag> peerIter = this.lastSeen.keySet().iterator();
        try {
            while(peerIter.hasNext()) {
                PeerSemanticTag peer = peerIter.next();
                Long lastseen = this.lastSeen.get(peer);
                buf.append(ASIPSerializer.serializeTag(peer));
                buf.append("{" + Long.toString(lastseen) + "}");
            }
            L.d("write buf: " + buf.toString());
            this.propertyHolder.setProperty(LAST_SEEN_PROPERTY_NAME, buf.toString());
        } catch (Exception ex) {
            L.e("couldn't write last seen entries for sync - critical", this);
        }
    }

    /**
     * TODO
     */
    private void restoreLastSeen() {
        try {
            String prop = this.propertyHolder.getProperty(LAST_SEEN_PROPERTY_NAME);
            if(prop == null) return;

            Iterator<String> stringIter = Util.stringsBetween("{", "}", prop, 0);
            while(stringIter.hasNext()) {
                String peerString = "{" + stringIter.next() + "}";
                String timeString = stringIter.next();

                PeerSemanticTag peer = ASIPSerializer.deserializePeerTag(peerString);
                Long time = Long.parseLong(timeString);

                this.lastSeen.put(peer, time);
            }

        } catch (SharkKBException ex) {
            L.d("cannot read last seen entries for sync - critical", this);
        }
    }
}
