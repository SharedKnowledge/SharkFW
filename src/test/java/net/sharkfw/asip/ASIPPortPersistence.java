package net.sharkfw.asip;

import java.io.UnsupportedEncodingException;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.sharkfw.asip.engine.ASIPConnection;
import net.sharkfw.asip.engine.ASIPInMessage;
import net.sharkfw.knowledgeBase.SharkKB;
import net.sharkfw.knowledgeBase.inmemory.InMemoSharkKB;
import net.sharkfw.peer.ASIPPort;
import net.sharkfw.peer.J2SEAndroidSharkEngine;
import net.sharkfw.peer.KnowledgePort;
import net.sharkfw.peer.SharkEngine;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;


import net.sharkfw.asip.engine.ASIPConnection;
import net.sharkfw.asip.engine.ASIPInMessage;
import net.sharkfw.peer.ASIPPort;
import net.sharkfw.peer.SharkEngine;
import net.sharkfw.system.L;
import net.sharkfw.system.SharkException;



/**
 *
 * @author thsc
 */
public class ASIPPortPersistence {
    
    public ASIPPortPersistence() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    private class AnASIPPort extends ASIPPort {
        private byte[] memento;

        public AnASIPPort(SharkEngine se, String mementoString) {
            super(se);
            mementoString.getBytes();
        }
        
        @Override
        public byte[] getMemento() {
            try {
                return "MementoString".getBytes("ISO-8859-1");
            } catch (UnsupportedEncodingException ex) {
                return null;
            }
        }
        
        @Override
        public void setMemento(byte[] memento) {
            // nix.
            L.d("reached setMemento: " + memento.toString());
        }

        @Override
        public boolean handleMessage(ASIPInMessage message, ASIPConnection connection) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }
    }
    
    @Test
    public void basics() throws SharkException {
        SharkKB kb = new InMemoSharkKB();
        SharkEngine se = new J2SEAndroidSharkEngine(kb);
        ASIPPort port = new AnASIPPort(se, "Nummer eins");
        ASIPPort port2 = new AnASIPPort(se, "Nummer zwei");
        
        port.getMemento();
        se.persistPort(port);
        
        L.setLogLevel(L.LOGLEVEL_ALL);
        L.d(L.properties2String(kb), this);

        // re-fresh..
        SharkEngine se2 = new J2SEAndroidSharkEngine(kb);
    }
}
