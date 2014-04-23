package net.sharkfw.knowledgeBase.inmemory;

import net.sharkfw.knowledgeBase.Knowledge;
import net.sharkfw.knowledgeBase.SharkKB;
import net.sharkfw.knowledgeBase.SharkKBException;

/**
 *
 * @author thsc
 */
public class SymbiantKB extends InMemoSharkKB {
    
    /**
     * creates a shark kb that doesn't hold any knowledge but references
     * all seven dimension of original knowledge base.
     * 
     * Knowledge must be set later if any information is ought to be added.
     * 
     * Dimension sets can be replaced afterwards. Take care. They shouldn't be 
     * changed after at least a single information is added. Changing a kb dimension
     * with a non empty knowledge can cause unexpected failure.
     * 
     * @param baseKB
     * @throws SharkKBException 
     */
    public SymbiantKB(SharkKB baseKB) throws SharkKBException {
        super(baseKB.getTopicsAsSemanticNet(), 
                baseKB.getPeersAsTaxonomy(), 
                baseKB.getSpatialSTSet(), 
                baseKB.getTimeSTSet());
    }
    
    @Override
    public void setKnowledge(Knowledge knowledge) {
        super.setKnowledge(knowledge);
    }
}
