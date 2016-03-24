package net.sharkfw.knowledgeBase;

/**
 * Interest define a communication context. In some applications it
 * is useful to make an kepInterest relying on a knowledge base.
 * 
 * This is what the dynamic kepInterest provides. Implementations should
 * offer a constructor in which a dynamic kepInterest is created by means
 * of a SharkKB, an AnchorSet and FragmentationParameter.
 * 
 * Each call of getKEPInterest shall produce a fresh kepInterest instance
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
     * Contextualize a fresh kepInterest from the knowledge base
     * with AnchorSet and FragmentationParameter.
     * @return 
     */
    public SharkCS getInterest() throws SharkKBException;
    
    public void refresh() throws SharkKBException ;
}
