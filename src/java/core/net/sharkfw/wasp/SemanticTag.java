/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sharkfw.wasp;

import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author micha
 */
public class SemanticTag {
    
    public static final String NAME = "NAME";
    
    public static final String SI = "SI";
    
    public static final String ID = "ID";
    
    private String nameField;
    
    private List<SubjectIdentifier> subjectIdentifiers;
    
    public SemanticTag(String name, SubjectIdentifier identifier){
        this.nameField = name;
        this.subjectIdentifiers.add(identifier);
    }
    
    public SemanticTag(String name, LinkedList<SubjectIdentifier> identifiers){
        this.nameField = name;
        this.subjectIdentifiers.addAll(identifiers);
    }

    public String getName() {
        return nameField;
    }

    public void setName(String nameField) {
        this.nameField = nameField;
    }

    public List<SubjectIdentifier> getSubjectIdentifiers() {
        return subjectIdentifiers;
    }

    public void setSubjectIdentifiers(List<SubjectIdentifier> subjectIdentifiers) {
        this.subjectIdentifiers = subjectIdentifiers;
    }
    
    public void addSubjectIdentifier(SubjectIdentifier identifier) {
        if(!subjectIdentifiers.contains(identifier)){
            subjectIdentifiers.add(identifier);
        }
    }    
    
}
