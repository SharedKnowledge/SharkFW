package net.sharkfw.asip;

import java.io.UnsupportedEncodingException;
import net.sharkfw.asip.engine.ASIPConnection;
import net.sharkfw.asip.engine.ASIPInMessage;
import net.sharkfw.peer.ASIPPort;
import net.sharkfw.peer.ASIPPortMemento;
import net.sharkfw.peer.SharkEngine;
import net.sharkfw.system.L;

/**
 *
 * @author thsc
 */
public class AnPersistentASIPPort extends ASIPPort {
        private String mementoString = "nix";
    private boolean uniqueObjects = false;

        public AnPersistentASIPPort(SharkEngine se, ASIPPortMemento memento) {
            super(se);
            L.d("reached memento constructor", this);
            L.d("className: " + memento.getPortClassName(), this);
            try {
                L.d("memento: " + new String(memento.getMemento(), SharkEngine.STRING_ENCODING), this);
            } catch (UnsupportedEncodingException ex) {
                L.d("String encoding not supported ???: " + ex.getLocalizedMessage());
            }
        }
        
        public AnPersistentASIPPort(SharkEngine se, String mementoString, boolean uniqueObjects) {
            super(se);
            this.mementoString = mementoString;
            
            this.uniqueObjects = uniqueObjects;
        }
        
        @Override
        public byte[] getMemento() {
            try {
                return this.mementoString.getBytes(SharkEngine.STRING_ENCODING);
            } catch (UnsupportedEncodingException ex) {
                return null;
            }
        }
        
        @Override
        public String getUniqueMementoObjectName() {
            if(this.uniqueObjects) {
                return mementoString;
            } 
            
            return null;
        }        
        
        @Override
        public boolean handleMessage(ASIPInMessage message, ASIPConnection connection) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }
    }
    
