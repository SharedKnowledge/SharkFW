package net.sharkfw.asip;

import java.util.Iterator;
import net.sharkfw.knowledgeBase.SharkKB;
import net.sharkfw.knowledgeBase.inmemory.InMemoSharkKB;
import net.sharkfw.peer.J2SESharkEngine;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import net.sharkfw.peer.ASIPPort;
import net.sharkfw.peer.SharkEngine;
import net.sharkfw.system.L;
import net.sharkfw.system.SharkException;
import org.junit.Assert;



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

    @Test
    public void overwriteNotUniqueObjects() throws SharkException {
        SharkKB kb = new InMemoSharkKB();
        SharkEngine se = new J2SESharkEngine(kb);
        ASIPPort port1 = new AnPersistentASIPPort(se, "Nummer eins", false);
        ASIPPort port2 = new AnPersistentASIPPort(se, "Nummer zwei", false);
        
        port1.getMemento();
        se.persistPort(port1);
        se.persistPort(port2);
        
        L.setLogLevel(L.LOGLEVEL_ALL);
        L.d(L.properties2String(kb), this);

        // re-fresh..
        SharkEngine se2 = new J2SESharkEngine(kb);
        
        Iterator<ASIPPort> allPorts = se2.getAllPorts();
        int i = 0;
        while(allPorts.hasNext()) {
            allPorts.next();
            i++;
        }
        
        // should only be one because port 1 and 2 considered the same
        Assert.assertSame(1, i);
    }
    
    @Test
    public void persistTwoUniqueObject() throws SharkException {
        SharkKB kb = new InMemoSharkKB();
        SharkEngine se = new J2SESharkEngine(kb);
        ASIPPort port1 = new AnPersistentASIPPort(se, "Nummer eins", true);
        ASIPPort port2 = new AnPersistentASIPPort(se, "Nummer zwei", true);
        
        port1.getMemento();
        se.persistPort(port1);
        se.persistPort(port2);
        
        L.setLogLevel(L.LOGLEVEL_ALL);
        L.d(L.properties2String(kb), this);

        // refresh..
        SharkEngine se2 = new J2SESharkEngine(kb);
        
        Iterator<ASIPPort> allPorts = se2.getAllPorts();
        int i = 0;
        while(allPorts.hasNext()) {
            allPorts.next();
            i++;
        }
        
        Assert.assertSame(2, i);
    }
    
    @Test
    public void removePort() throws SharkException {
        SharkKB kb = new InMemoSharkKB();
        SharkEngine se = new J2SESharkEngine(kb);
        ASIPPort port1 = new AnPersistentASIPPort(se, "Nummer eins", true);
        ASIPPort port2 = new AnPersistentASIPPort(se, "Nummer zwei", true);
        
        port1.getMemento();
        se.persistPort(port1);
        se.persistPort(port2);
        
        se.removePersistedPort(port1);
        
        L.setLogLevel(L.LOGLEVEL_ALL);
        L.d(L.properties2String(kb), this);

        // refresh
        SharkEngine se2 = new J2SESharkEngine(kb);
        
        Iterator<ASIPPort> allPorts = se2.getAllPorts();
        int i = 0;
        while(allPorts.hasNext()) {
            allPorts.next();
            i++;
        }
        
        // only one .. second was one removed
        Assert.assertSame(1, i);
    }
}
