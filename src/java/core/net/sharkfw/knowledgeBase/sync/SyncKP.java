package net.sharkfw.knowledgeBase.sync;

import net.sharkfw.knowledgeBase.Knowledge;
import net.sharkfw.knowledgeBase.SharkCS;
import net.sharkfw.peer.KEPConnection;
import net.sharkfw.peer.KnowledgePort;
import net.sharkfw.peer.SharkEngine;

public class SyncKP extends KnowledgePort{

	public SyncKP(SharkEngine se) {
		super(se);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void doInsert(Knowledge knowledge, KEPConnection kepConnection) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void doExpose(SharkCS interest, KEPConnection kepConnection) {
		// TODO Auto-generated method stub
		
	}

}
