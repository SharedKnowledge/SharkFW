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
public class InMemoKnowledge extends InMemoASIPKnowledge implements Knowledge {
    private final ArrayList<ContextPoint> cps;

    /** 
     * Create knowledge without background 
     */ 
    public InMemoKnowledge() {
        // create empty context point list
        cps = new ArrayList<>();
    }

    public InMemoKnowledge(SharkVocabulary background) {
        super(background);
        cps = new ArrayList<>();
    }
    
    InMemoKnowledge(SharkVocabulary cm, InMemoKnowledge k) {
        super(cm);
        this.cps = k.getCPS();
    }
    
    /**
     * @deprecated 
     * @return 
     */
    private ArrayList<ContextPoint> getCPS() {
        return this.cps;
    }

    /**
     * Returns the number of elements in the local <code>Vector</code> which stores the <code>ContextPoint</code>s.
     * 
     * @return An integer value representing the number of <code>ContextPoint</code>s  inside this <code>InMemoKnowledge</code>
     * @deprecated 
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
     * @deprecated 
     */
    @Override
    public ContextPoint getCP(int i) {
        return this.cps.get(i);
    }

    // =========================================================================
    // API rev. methods

    /**
     * @deprecated 
     * @param cp 
     */
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
    
    /**
     * @deprecated 
     * @param cp 
     */
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

    /**
     * @deprecated 
     * @return 
     */
    @Override
    public Enumeration<ContextPoint> contextPoints() {
        return new Iterator2Enumeration(this.cps.iterator());
    }

    //////////////////////////////////////////////////////////////////////////
    //                               knowledge listener                     //
    //////////////////////////////////////////////////////////////////////////
    private ArrayList<KnowledgeListener> listeners = new ArrayList<>();

    @Override
    public void addListener(KnowledgeListener kbl) {
        this.listeners.add(kbl);
    }

    @Override
    public void removeListener(KnowledgeListener kbl) {
        this.listeners.remove(kbl);
    }
}
