package net.sharkfw.knowledgeBase.inmemory;

import net.sharkfw.knowledgeBase.SharkKBException;
import net.sharkfw.system.L;

public class SpinLock {
	boolean entered;
	int end;
	final int gap = 20;
	
	public SpinLock(int delay) {
		entered = false;
		end = (delay*1000) / gap;
	}
	
	public void enter() {
		if (entered) {
			try {
				wait4();
			} catch (SharkKBException e) {
				System.out.println("SpinLock::enter(): wait exception");
				e.printStackTrace();
			}
		}
		entered = true;
	}
	
	public void leave() {
		entered = false;
	}
	
	public void wait4() throws SharkKBException {
		int count = 0;
//		System.out.println("Thread wait:"+Thread.currentThread().getName());
		while(entered && (count < end)) {
			try {
				Thread.yield();
				Thread.sleep(gap);
			} catch (InterruptedException e) {
				System.out.println("spinloop interrupted @ "+count+"  "+e.getLocalizedMessage()+"\n");				
				L.d("spinloop interrupted @ "+count+"  "+e.getLocalizedMessage(), this);
				e.printStackTrace();
				entered = false;
			}
			count++;
		}
		if (count >= end) {
			System.out.println(Thread.currentThread().getName()+" spinloop sleep exhausted");
			throw new SharkKBException(Thread.currentThread().getName()+" spinloop sleep exhausted");
		}
	}
}
