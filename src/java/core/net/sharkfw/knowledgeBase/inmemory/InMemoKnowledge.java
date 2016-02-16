package net.sharkfw.knowledgeBase.inmemory;

import net.sharkfw.asip.ASIPSpace;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import net.sharkfw.asip.ASIPInformationSpace;
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
    private ArrayList<ASIPInformationSpace> informationSpaces;
    
    private SharkVocabulary cm;

    /** 
     * Create knowledge without background 
     */ 
    public InMemoKnowledge() {
        // create empty context point list
        cps = new ArrayList<>();
        informationSpaces = new ArrayList<>();
    }

    public InMemoKnowledge(SharkVocabulary background) {
        this();
        
        // use external background
        this.cm = background;
    }
    
    InMemoKnowledge(SharkVocabulary cm, InMemoKnowledge k) {
        this.cm = cm;
        this.cps = k.getCPS();
        this.informationSpaces = k.getISS();
    }
    
    /**
     * @deprecated 
     * @return 
     */
    private ArrayList<ContextPoint> getCPS() {
        return this.cps;
    }

    private ArrayList<ASIPInformationSpace> getISS() {
        return this.informationSpaces;
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

    @Override
    public void addInformationSpace(ASIPSpace space) throws SharkKBException {
        InMemoInformationSpace is = new InMemoInformationSpace(space);
        
        this.informationSpaces.add(is);
    }
    
    private ASIPSpace point2space(InformationPoint ip) {
        return new InMemoInformationSpace();
    }

    @Override
    public void addInformationPoint(InformationPoint iPoint) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void removeInformationPoint(InformationPoint cp) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Iterator<InformationPoint> informationPoints() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int getNumberOfInformationPoints() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public InformationPoint getInformationPoint(int i) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void removeInformationSpace(ASIPSpace space) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int getNumberOfInformationSpaces() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
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

    @Override
    public Iterator<ASIPInformationSpace> informationSpaces() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    
}
