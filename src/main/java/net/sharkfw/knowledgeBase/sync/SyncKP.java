package net.sharkfw.knowledgeBase.sync;

import net.sharkfw.asip.engine.ASIPConnection;
import net.sharkfw.asip.engine.ASIPInMessage;
import net.sharkfw.knowledgeBase.*;
import net.sharkfw.peer.ContentPort;
import net.sharkfw.peer.SharkEngine;

import java.io.InputStream;

/**
 * Created by j4rvis on 19.07.16.
 */
public class SyncKP extends ContentPort implements KnowledgeBaseListener{
    public SyncKP(SharkEngine se) {
        super(se);
    }

    @Override
    public void topicAdded(SemanticTag tag) {

    }

    @Override
    public void peerAdded(PeerSemanticTag tag) {

    }

    @Override
    public void locationAdded(SpatialSemanticTag location) {

    }

    @Override
    public void timespanAdded(TimeSemanticTag time) {

    }

    @Override
    public void topicRemoved(SemanticTag tag) {

    }

    @Override
    public void peerRemoved(PeerSemanticTag tag) {

    }

    @Override
    public void locationRemoved(SpatialSemanticTag tag) {

    }

    @Override
    public void timespanRemoved(TimeSemanticTag tag) {

    }

    @Override
    public void predicateCreated(SNSemanticTag subject, String type, SNSemanticTag object) {

    }

    @Override
    public void predicateRemoved(SNSemanticTag subject, String type, SNSemanticTag object) {

    }

    @Override
    public void tagChanged(SemanticTag tag) {

    }

    @Override
    public void contextPointAdded(ContextPoint cp) {

    }

    @Override
    public void cpChanged(ContextPoint cp) {

    }

    @Override
    public void contextPointRemoved(ContextPoint cp) {

    }

    @Override
    protected boolean handleRaw(ASIPInMessage message, ASIPConnection connection, InputStream inputStream) {
        return false;
    }
}
