package net.sharkfw.wasp.interfaces;

import java.util.Enumeration;
import java.util.Iterator;
import net.sharkfw.knowledgeBase.FragmentationParameter;

/**
 *
 * @author micha
 */
public interface SemanticTagSet {
    
    public SemanticTag merge(SemanticTag tag);
  
    public SemanticTag createSemanticTag(String name, String[] sis);

    public SemanticTag createSemanticTag(String name, String si);

    public void removeSemanticTag(SemanticTag tag);

    public void setEnumerateHiddenTags(boolean hide);

    public Enumeration<SemanticTag> tags();

    public Iterator<SemanticTag> stTags();

    public SemanticTag getSemanticTag(String[] si);

    public SemanticTag getSemanticTag(String si);

    public Iterator<SemanticTag> getSemanticTagByName(String pattern);

    public SemanticTagSet fragment(SemanticTag anchor);

    public FragmentationParameter getDefaultFP();

    public void setDefaultFP(FragmentationParameter fp);

    public SemanticTagSet fragment(SemanticTag anchor, FragmentationParameter fp);

    public SemanticTagSet contextualize(Enumeration<SemanticTag> anchorSet, FragmentationParameter fp);

    public SemanticTagSet contextualize(Enumeration<SemanticTag> anchorSet);

    public SemanticTagSet contextualize(SemanticTagSet context, FragmentationParameter fp);

    public SemanticTagSet contextualize(SemanticTagSet context);

    public void merge(SemanticTagSet stSet);

//    public void addListener(STSetListener listen);

//    public void removeListener(STSetListener listener) throws SharkKBException;

    public boolean isEmpty();

    public int size();
}
