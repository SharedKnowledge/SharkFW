package net.sharkfw.knowledgeBase;

/**
 * Context space is a very general concept which isn't implemented in it
 * generic form. Shark context space is a special cs which is used in 
 * the overall framework. Maybe, sometime another context space will be
 * defined and implemented - another (parallel) universe..
 * 
 * @author thsc
 */
public interface ContextSpace {
    /**
     * The name to be used when creating ANY tag
     */
    public static final String ANY = "any";

    /**
    * Return whether or not the dimension defined through dim is set to ANY
    *
    * @see net.sharkfw.knowledgeBase.ContextSpace
    * 
    * @param dim The integer value representing the dimension in question
    * @return <code>true</code> if the dimension is ANY, <code>false</code> otherwise.
    */
    public boolean isAny(int dim);

    /**
     * Return the given dimension of the context space.
     *
     * @see net.sharkfw.knowledgeBase.STSet
     *
     * @param dim An integer value to denote the dimension to be returned
     * @return An STSet representing the dimension
     * @throws SharkKBException If dimension isn't supported.
     */
    public STSet getSTSet(int dim) throws SharkKBException;
}
