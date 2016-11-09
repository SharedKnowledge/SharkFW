package net.sharkfw.knowledgeBase;

import net.sharkfw.knowledgeBase.inmemory.InMemoSharkKB;

/**
 *
 * @author thsc
 */
class CtxHelper {
    public STSet result;
    public boolean success; 
        
    public CtxHelper contextualizeSingleDimension(STSet source, 
            STSet context, FragmentationParameter fp) 
                throws SharkKBException {

        // test on any
        if(SharkCSAlgebra.isAny(source)) {
            // source is any - return copy of context
            if(SharkCSAlgebra.isAny(context)) {
                // context also any
                this.result = null;
            } else {
                // not any, make copy
                this.result = InMemoSharkKB.createInMemoCopy(context);
            }
            this.success = true; // in any case thats a valid result
        } else {
            if(SharkCSAlgebra.isAny(context)) {
                // source is not any but context - return source
                if(SharkCSAlgebra.isAny(source)) {
                    // context also any
                    this.result = null;
                } else {
                    // not any, make copy
                    this.result = InMemoSharkKB.createInMemoCopy(source);
                }
                this.success = true;
            } else {
                // source and context are not any - contextualize
                this.result = source.contextualize(context, fp);
                // that must be not null if mutual kepInterest exists
                if(this.result == null || this.result.isEmpty()) {
                    this.success = false;
                } else {
                    this.success = true;
                }
            }
        }

        // don' wast space with empty sets
        if(this.result != null && this.result.isEmpty()) {
            this.result = null;
        }

        // target is not null and not empty
        return this;
    }
}
