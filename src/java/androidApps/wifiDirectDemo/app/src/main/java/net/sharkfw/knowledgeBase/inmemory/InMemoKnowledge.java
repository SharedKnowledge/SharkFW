package net.sharkfw.knowledgeBase.inmemory;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import net.sharkfw.knowledgeBase.*;
import net.sharkfw.system.Iterator2Enumeration;

/**
 * An in memory implementation of the <code>Knowledge</code> interface.
 *
 * InMemoKnowledge uses a <code>Vector</code> to manage its <code>ContextPoint</code>s and
 * a reference to to a <code>ContextSpace</code> for the contextmap.
 * 
 * @author thsc
 */
public class InMemoKnowledge implements Knowledge {
    private ArrayList<ContextPoint> cps;
    private SharkVocabulary cm;

    /** 
     * Create knowledge without background 
     */ 
    public InMemoKnowledge() {
        // create empty context point list
        cps = new ArrayList<>();
    }

    public InMemoKnowledge(SharkVocabulary background) {
        this();
        
        // use external background
        this.cm = background;
    }
    
    InMemoKnowledge(SharkVocabulary cm, InMemoKnowledge k) {
        this.cm = cm;
        this.cps = k.getCPS();
    }
    
    private ArrayList<ContextPoint> getCPS() {
        return this.cps;
    }

    /**
     * Returns the number of elements in the local <code>Vector</code> which stores the <code>ContextPoint</code>s.
     * 
     * @return An integer value representing the number of <code>ContextPoint</code>s  inside this <code>InMemoKnowledge</code>
     */
    @Override
    public int getNumberOfContextPoints() {
        return this.cps.size();
    }

    /**
     * Returns the <code>ContextPoint</code> at the given index.
     * 
     * @param i An integer value denoting the index.
     * @return A <code>ContextPoint</code> (<code>InMemoContextPoint</code> really).
     */
    @Override
    public ContextPoint getCP(int i) {
        return this.cps.get(i);
    }

    /**
     * Return a reference to the contextmap.
     * Note: Method can return null. Context map can also have empty dimensions.
     * 
     * @return A <code>ContextSpace</code> representing the contextmap of this <code>Knowledge</code>
     */
    @Override
    public SharkVocabulary getVocabulary() {
        return this.cm;
    }
    
       /**
     * Create a copy of the passed <code>ContextCoordinates</code>.
     * This includes creation of a <code>new ContextCoordinates()</code> object
     * in order to have a new object identity.
     *
     * @param co The <code>ContextCoordinates</code> to copy.
     * @return A new <code>ContextCoordinates</code> instance with the same values set as <code>co</code>
     */
    @SuppressWarnings("unused")
    private InMemoContextCoordinates copyCoords(ContextCoordinates co) {
      
        return new InMemoContextCoordinates(
            co.getTopic(), co.getOriginator(), co.getPeer(), co.getRemotePeer(),
            co.getTime(), co.getLocation(), co.getDirection());
    }

    // =========================================================================
    // API rev. methods

    @Override
    public final void addContextPoint(ContextPoint cp) {
        this.cps.add(cp);
        // notity
        Iterator<KnowledgeListener> listenerIter = this.listeners.iterator();
        while(listenerIter.hasNext()) {
            KnowledgeListener listener = listenerIter.next();
            listener.contextPointAdded(cp);
        }
    }

    @Override
    public void removeContextPoint(ContextPoint cp) {
        this.cps.remove(cp);
        // notity
        Iterator<KnowledgeListener> listenerIter = this.listeners.iterator();
        while(listenerIter.hasNext()) {
            KnowledgeListener listener = listenerIter.next();
            listener.contextPointRemoved(cp);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public Enumeration<ContextPoint> contextPoints() {
        return new Iterator2Enumeration(this.cps.iterator());
    }

    private ArrayList<KnowledgeListener> listeners = new ArrayList<KnowledgeListener>();

    @Override
    public void addListener(KnowledgeListener kbl) {
        this.listeners.add(kbl);
    }

    @Override
    public void removeListener(KnowledgeListener kbl) {
        this.listeners.remove(kbl);
    }
}
