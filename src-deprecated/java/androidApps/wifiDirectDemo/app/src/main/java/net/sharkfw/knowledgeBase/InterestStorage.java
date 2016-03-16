package net.sharkfw.knowledgeBase;

import java.util.Iterator;

/**
 *
 * @author thsc
 */
public interface InterestStorage {
    
    /**
     * Saves this interest into a list of interests
     * @param interest 
     * @throws net.sharkfw.knowledgeBase.SharkKBException 
     */
    public void addInterest(SharkCS interest) throws SharkKBException;
    
    /**
     * Removes this interest from the storage
     * @param interest 
     * @throws net.sharkfw.knowledgeBase.SharkKBException 
     */
    public void removeInterest(SharkCS interest) throws SharkKBException;
    
    /**
     * Return iteration of interests stored in the 
     * interest storage
     * 
     * @return 
     * @throws net.sharkfw.knowledgeBase.SharkKBException 
     */
    public Iterator<SharkCS> interests() throws SharkKBException;
    
}
