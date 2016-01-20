/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sharkfw.wasp;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import net.sharkfw.wasp.interfaces.SemanticTag;

/**
 *
 * @author micha
 */
public class BaseSemanticTag implements SemanticTag {
    
    public static final String NAME = "NAME";
    public static final String SIS = "SIS";
    private String name = "";
    private LinkedList<String> sis = null;
    private boolean isHidden = false;

    public BaseSemanticTag(String name) {
        this.name = name;
        this.sis = new LinkedList<>();
    }
    
    @Override
    public String getName() {
        return name;
    }

    @Override
    public String[] getSI() {
        return (String[]) sis.toArray();
    }
    
    public List<String> getSIAsList(){
        return  sis;
    }

    @Override
    public void removeSI(String si) {
        sis.remove(si);
    }

    @Override
    public void addSI(String si) {
        sis.add(si);
    }

    @Override
    public void setName(String newName) {
        this.name = newName;
    }

    @Override
    public void merge(SemanticTag st) {
    }

    @Override
    public void setHidden(boolean isHidden) {
        this.isHidden = isHidden;
    }

    @Override
    public boolean hidden() {
        return isHidden;
    }

    @Override
    public boolean isAny() {
        return getSI().length == 0;
    }

    @Override
    public boolean identical(SemanticTag other) {
        if((this instanceof BaseSemanticTag && other instanceof BaseSemanticTag) || 
            (this instanceof PeerSemanticTag && other instanceof PeerSemanticTag) ||
            (this instanceof SpatialSemanticTag && other instanceof SpatialSemanticTag) ||
            (this instanceof TimeSemanticTag && other instanceof TimeSemanticTag) ){
            
            if(this.name.equals(other.getName()) && Arrays.equals(this.getSI(), other.getSI()))
                return true;
        }
        return false;
    }
}
