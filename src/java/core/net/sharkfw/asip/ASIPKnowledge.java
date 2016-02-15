package net.sharkfw.asip;

import java.util.Iterator;
import net.sharkfw.knowledgeBase.InformationPoint;
import net.sharkfw.knowledgeBase.SharkVocabulary;

/**
 *
 * @author thsc
 */
public interface ASIPKnowledge {
    public void addInformationSpace(ASIPSpace space);

    public void removeInformationSpace(ASIPSpace space);

    public Iterator<InformationPoint> informationPoints();

  /**
   * Return a ContextSapce containing Tag for the different coordinates of the
   * contained ContextPoints plus (optional) background information for these tags.
   * 
   * Note: Method can return null. Context map can also have empty dimensions.
   * 
   *
   * @see net.sharkfw.knowledgeBase.ContextSpace
   *
   * @return A ContextSpace with the above characteristics
   */
    public SharkVocabulary getVocabulary();

    public int getNumberOfInformationSpaces();
}
