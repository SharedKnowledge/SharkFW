package net.sharkfw.kep;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import net.sharkfw.peer.KnowledgePort;
import net.sharkfw.peer.SharkEngine;
import net.sharkfw.protocols.StreamConnection;

/**
 *
 * @author thsc
 */
public abstract class AbstractSharkStub implements SharkStub {
    private List<KnowledgePort> kps;
    protected KnowledgePort notHandledRequestsHandler;
    protected SharkEngine se;
    private Hashtable<String, StreamConnection> table;

    public AbstractSharkStub(SharkEngine se) {
            this.kps = new ArrayList<>();
            this.se = se;
            this.table = new Hashtable<>();
    }
    
    @Override
    public final void addListener(KnowledgePort newListener) {
        // already in there?
        Iterator<KnowledgePort> kpIter = kps.iterator();
        while(kpIter.hasNext()) {
            if(newListener == kpIter.next()) {
                return; // already in - do nothing
            }
        }
        
        // not found - add
        this.kps.add(newListener);
    };

    @Override
    public final void withdrawListener(KnowledgePort listener) {
        this.kps.remove(listener);
    };
    
    @Override
    public Iterator<KnowledgePort> getListener() {
        return this.kps.iterator();
    }
}
