package net.sharkfw.system;

import java.util.concurrent.Callable;
import java.util.concurrent.ThreadFactory;

/**
 * Created by j4rvis on 9/26/16.
 */
public abstract class SharkTask<T> implements Callable<T>, Runnable {

    private SharkTask predecessor;
    private SharkTask successor;

//    public SharkTask(SharkTask predecessor, SharkTask successor) {
//        this.predecessor = predecessor;
//        this.successor = successor;
//    }
//
//    public void setPredecessor(SharkTask predecessor){
//        this.predecessor = predecessor;
//    }
//
//    public void setSuccessor(SharkTask successor){
//        this.successor = successor;
//    }

    @Override
    public void run() {
        if(Thread.currentThread().isInterrupted()){
            System.out.println("SharkTask interrupted.");
            return;
        }

        process();
    }

    @Override
    public T call() throws Exception {

        // look for interruptions and for predecessors or successors
        if(Thread.currentThread().isInterrupted()){
            System.out.println("SharkTask interrupted.");
            return null;
        }

        // do the logic here

        T returnValue = process();

        // do some other stuff

        return returnValue;
    }

    // put logic code here
    abstract protected T process();

    private void waitForPredecessor(){

    }

    private void callSuccessor(){

    }
}
