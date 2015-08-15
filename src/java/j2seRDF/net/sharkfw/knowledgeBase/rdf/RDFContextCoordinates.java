package net.sharkfw.knowledgeBase.rdf;

import net.sharkfw.knowledgeBase.ContextCoordinates;
import net.sharkfw.knowledgeBase.PeerSTSet;
import net.sharkfw.knowledgeBase.STSet;
import net.sharkfw.knowledgeBase.SharkKBException;
import net.sharkfw.knowledgeBase.SpatialSTSet;
import net.sharkfw.knowledgeBase.TimeSTSet;

public class RDFContextCoordinates implements ContextCoordinates {

	private RDFSemanticTag topic;
	private RDFPeerSemanticTag originator, peer, remotePeer;
	private RDFSpatialSemanticTag location;
	private RDFTimeSemanticTag time;
	private int direction;

	public RDFContextCoordinates(RDFSemanticTag topic,
			RDFPeerSemanticTag originator, RDFPeerSemanticTag peer,
			RDFPeerSemanticTag remotePeer, RDFTimeSemanticTag time,
			RDFSpatialSemanticTag location, int direction) {

		this.topic = topic;
		this.originator = originator;
		this.peer = peer;
		this.remotePeer = remotePeer;
		this.location = location;
		this.time = time;
		this.direction = direction;
	}

	@Override
	public int getDirection() {
		return direction;
	}

	@Override
	public RDFSpatialSemanticTag getLocation() {
		return location;
	}

	@Override
	public RDFPeerSemanticTag getOriginator() {
		return originator;
	}

	@Override
	public RDFPeerSemanticTag getPeer() {
		return peer;
	}

	@Override
	public RDFPeerSemanticTag getRemotePeer() {
		return remotePeer;
	}

	@Override
	public RDFTimeSemanticTag getTime() {
		return time;
	}

	@Override
	public RDFSemanticTag getTopic() {
		return topic;
	}
	
	public void setTopic(RDFSemanticTag tag) {
		topic = tag;
	}

	@Override
	public SpatialSTSet getLocations() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PeerSTSet getPeers() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PeerSTSet getRemotePeers() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TimeSTSet getTimes() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public STSet getTopics() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public STSet getSTSet(int arg0) throws SharkKBException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isAny(int arg0) {
		// TODO Auto-generated method stub
		return false;
	}

}
