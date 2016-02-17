package net.sharkfw.asip;

import java.util.Iterator;
import net.sharkfw.knowledgeBase.SharkKBException;
import net.sharkfw.knowledgeBase.SharkVocabulary;

/**
 *
 * @author thsc
 */
public interface ASIPKnowledge {
    public void addInformationSpace(ASIPInformationSpace space) throws SharkKBException;
    
    public ASIPInformationSpace createInformationSpace(ASIPSpace space) throws SharkKBException;

    public void removeInformationSpace(ASIPSpace space) throws SharkKBException;

    public Iterator<ASIPInformationSpace> informationSpaces() throws SharkKBException;

  /**
   * Return a ContextSapce containing Tag for the different coordinates of the
   * contained ContextPoints plus (optional) background information for these tags.
   * 
   * Note: Method can return null. Context map can also have empty dimensions.
   * 
   *
     * @throws net.sharkfw.knowledgeBase.SharkKBException
   * @see net.sharkfw.knowledgeBase.ContextSpace
   *
   * @return A ContextSpace with the above characteristics
   */
    public SharkVocabulary getVocabulary() throws SharkKBException;

    public int getNumberOfInformationSpaces() throws SharkKBException;
}
