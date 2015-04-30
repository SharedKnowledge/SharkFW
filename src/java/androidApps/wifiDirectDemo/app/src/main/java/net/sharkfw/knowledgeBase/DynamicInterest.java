package net.sharkfw.knowledgeBase;

/**
 * Interest define a communication context. In some applications it
 * is useful to make an interest relying on a knowledge base.
 * 
 * This is what the dynamic interest provides. Implementations should
 * offer a constructor in which a dynamic interest is created by means
 * of a SharkKB, an AnchorSet and FragmentationParameter.
 * 
 * Each call of getInterest shall produce a fresh interest instance
 * that is calculated with knowledge base, anchor and fragmentation 
 * parameter.
 * 
 * @author thsc
 */
public interface DynamicInterest extends Interest {
    
    public SharkCS getInitialInterest();

    public FragmentationParameter[] getFragmentationParameter();
    
    public SharkKB getSharkKB();    
    
    /**
     * Contextualize a fresh interest from the knowledge base
     * with AnchorSet and FragmentationParameter.
     * @return 
     */
    public SharkCS getInterest() throws SharkKBException;
    
    public void refresh() throws SharkKBException ;
}
