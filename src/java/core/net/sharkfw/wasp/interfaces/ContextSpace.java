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
    
    public static final String ANY = "any";

    public boolean isAny(int dim);

    public SemanticTagSet getSTSet(int dim);
}
