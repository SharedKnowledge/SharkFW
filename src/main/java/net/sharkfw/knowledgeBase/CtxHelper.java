package net.sharkfw.knowledgeBase;

import net.sharkfw.knowledgeBase.inmemory.InMemoSharkKB;

/**
 *
 * @author thsc
 */
class CtxHelper {
    public STSet resultSet;
    public boolean success;
        
    public CtxHelper contextualizeSingleDimension(STSet source, 
            STSet context, FragmentationParameter fp) 
                throws SharkKBException {

        // test on any
        if(SharkCSAlgebra.isAny(source)) {
            // source is any - return copy of context
            this.resultSet = InMemoSharkKB.createInMemoCopy(context);
            this.success = true; // in any case thats a valid resultSet
        } else {
            if(SharkCSAlgebra.isAny(context)) {
                // source is not any but context - return source
                this.resultSet = InMemoSharkKB.createInMemoCopy(source);
                this.success = true;
            } else {
                // source and context are not any - contextualize
                this.resultSet = source.contextualize(context, fp);
                // that must be not null if mutual kepInterest exists
                if(this.resultSet == null || this.resultSet.isEmpty()) {
                    this.success = false;
                } else {
                    this.success = true;
                }
            }
        }

        // don' wast space with empty sets
        if(this.resultSet != null && this.resultSet.isEmpty()) {
            this.resultSet = null;
        }

        // target is not null and not empty
        return this;
    }
}
