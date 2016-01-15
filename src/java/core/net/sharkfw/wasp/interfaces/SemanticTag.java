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
public interface SemanticTag {

    public static final String NAME = "NAME";

    public static final String SI = "SI";

    public static final String ID = "ID";
    
    public String getName();

    public String[] getSI();

    public void removeSI(String si);

    public void addSI(String si);

    public void setName(String newName);
   
    public void merge(SemanticTag st);
    
    public void setHidden(boolean isHidden);
    
    public boolean hidden();
    
    public boolean isAny();
    
    public boolean identical(SemanticTag other);
}
