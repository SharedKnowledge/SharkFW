package net.sharkfw.peer;

import net.sharkfw.asip.SharkStub;
import net.sharkfw.asip.engine.ASIPConnection;
import net.sharkfw.asip.engine.ASIPInMessage;
import net.sharkfw.kp.KPListener;

import java.util.List;

/**
 * Created by j4rvis on 22.06.16.
 */
public abstract class ASIPPort {

    protected List<KPListener> listeners;
    protected SharkStub sharkStub;
    protected boolean isStarted;
    protected SharkEngine se;

    public ASIPPort(SharkEngine se) {
        this.se = se;
        if (se != null) {
            this.sharkStub = se.getAsipStub();
//            this.sharkStub = se.getKepStub();  /*TODO*/
            se.addKP(this);
        }
    }
    
    /**
     * Sometimes it's useful to keep the state of an ASIP port for further 
     * incarnation. That behaviour is already described in the mememto 
     * design pattern. Port developers can overwrite this methode to make
     * their ports persistent.
     * 
     * @return the memento as byte
     */
    public byte[] getMemento() {return null;};
    
    /**
     * Port developer must implement this methode if that port is going
     * to ve persistent, see comments in getMemento().
     * @param memento 
     */
    public void setMemento(byte[] memento) { }
    
    /**
     * Ports can save their memento with the shark engine. 
     * Engine has no way to distinguish two ports from same class.
     * The engine keeps <i>only one instance of each class</i> unless this
     * methode overwritten and returns a unique name for each object
     * of that class.
     * @return 
     */
    public String getUniqueMementoObjectName() {return null;}
    
    public abstract boolean handleMessage(ASIPInMessage message, ASIPConnection connection);

    public void addListener(KPListener listener){
        this.listeners.add(listener);
    }

    public void removeListener(KPListener listener) {
        this.listeners.remove(listener);
    }

    public void setSharkStub(SharkStub stub) {
        this.sharkStub = stub;
        this.sharkStub.addListener(this);
    }

    /**
     * Make this AbstractKP stop listening to incoming requests.
     */
    public void stop() {
        this.sharkStub.withdrawListener(this);
        this.isStarted = false;
    }

    /**
     * Make this AbstractKP start listening to incoming requests, by registering it on the KEPStub.
     */
    public void start() {
        // listen again
        this.sharkStub.addListener(this);
        this.isStarted = true;
        this.se.addKP(this);
    }

    /**
     * Has this AbstractKP been started to handle requests?
     * @return <code>true</code> if active , <code>false</code> if stopped.
     */
    public boolean isStarted() {
        return this.isStarted;
    }
}
