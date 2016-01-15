/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sharkfw.wasp.interfaces;

/**
 *
 * @author micha
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
    public STSet getSTSet(int dim);
}
