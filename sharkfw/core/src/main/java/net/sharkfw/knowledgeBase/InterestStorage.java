package net.sharkfw.knowledgeBase;

import java.util.Iterator;

/**
 *
 * @author thsc
 */
public interface InterestStorage {
    
    /**
     * Saves this kepInterest into a list of interests
     * @param interest 
     * @throws net.sharkfw.knowledgeBase.SharkKBException 
     * @deprecated 
     */
    public void addInterest(SharkCS interest) throws SharkKBException;
    
    /**
     * Removes this kepInterest from the storage
     * @param interest 
     * @throws net.sharkfw.knowledgeBase.SharkKBException 
     * @deprecated 
     */
    public void removeInterest(SharkCS interest) throws SharkKBException;
    
    /**
     * Return iteration of interests stored in the 
     * kepInterest storage
     * 
     * @return 
     * @throws net.sharkfw.knowledgeBase.SharkKBException 
     * @deprecated 
     */
    public Iterator<SharkCS> interests() throws SharkKBException;
    
}
