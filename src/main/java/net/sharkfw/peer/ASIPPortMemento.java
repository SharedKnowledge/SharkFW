package net.sharkfw.peer;

/**
 *
 * @author thsc
 */
public class ASIPPortMemento {
    private final String portClassName;
    private final byte[] memento;
    
    ASIPPortMemento(String portClassName, byte[] memento) {
        this.portClassName = portClassName;
        this.memento = memento;
    }
    
    public String getPortClassName() {
        return this.portClassName;
    }
    
    public byte[] getMemento() {
        return this.memento;
    }
}
