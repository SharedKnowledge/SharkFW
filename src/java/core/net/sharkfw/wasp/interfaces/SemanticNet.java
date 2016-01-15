/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sharkfw.wasp.interfaces;

import java.util.Enumeration;
import net.sharkfw.knowledgeBase.FragmentationParameter;

/**
 *
 * @author micha
 */
public interface SemanticNet extends SemanticTagSet{
    
    public static final String SUBTAG = "sub";

    public static final String SUPERTAG = "super";

    public SemanticTagSet asSTSet();
    
    @Override
    public SemanticTag createSemanticTag(String name, String[] sis);
    
    @Override
    public SemanticTag createSemanticTag(String name, String si);

    @Override
    public void removeSemanticTag(SemanticTag tag);
    
    @Override
    public SemanticTag getSemanticTag(String[] sis);
    
    @Override
    public SemanticTag getSemanticTag(String si);

    public void setPredicate(SemanticTag source, SemanticTag target, String type);

    public void removePredicate(SemanticTag source, SemanticTag target, String type);

    @Override
    public SemanticNet fragment(SemanticTag anchor, FragmentationParameter fp);

    @Override
    public SemanticNet fragment(SemanticTag anchor);

    @Override
    public SemanticNet contextualize(Enumeration<SemanticTag> anchorSet, 
            FragmentationParameter fp);

    @Override
    public SemanticNet contextualize(SemanticTagSet context, FragmentationParameter fp);

    @Override
    public SemanticNet contextualize(Enumeration<SemanticTag> anchorSet);

    @Override
    public SemanticNet contextualize(SemanticTagSet context);

    public void merge(SemanticNet remoteSemanticNet);

    @Override
    public SemanticTag merge(SemanticTag source);
    
    public void add(SemanticTag tag);
    
}
