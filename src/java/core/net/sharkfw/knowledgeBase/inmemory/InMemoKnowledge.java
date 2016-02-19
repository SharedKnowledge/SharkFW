package net.sharkfw.knowledgeBase.inmemory;

import net.sharkfw.asip.ASIPSpace;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import net.sharkfw.asip.ASIPInformation;
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
public class InMemoKnowledge extends InMemoASIPKnowledge implements Knowledge {
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

    private ASIPSpace point2space(InformationPoint ip) {
        return new InMemoInformationSpace();
    }

    @Override
    public void removeInformation(ASIPSpace space) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int getNumberInformation() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Iterator<ASIPInformationSpace> informationSpaces() {
        return this.informationSpaces.iterator();
    }

    @Override
    public ASIPInformationSpace mergeInformation(Iterator<ASIPInformation> infos, ASIPSpace space) throws SharkKBException {
        // TODO
        InMemoInformationSpace newIS = new InMemoInformationSpace(space);
        this.informationSpaces.add(newIS);
        
        return newIS;
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
    public ASIPInformationSpace addInformation(List<ASIPInformation> information, ASIPSpace space) throws SharkKBException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
