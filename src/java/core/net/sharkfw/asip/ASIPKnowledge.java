package net.sharkfw.asip;

import java.io.OutputStream;
import java.util.Iterator;
import java.util.List;
import net.sharkfw.knowledgeBase.Information;
import net.sharkfw.knowledgeBase.SharkKBException;
import net.sharkfw.knowledgeBase.SharkVocabulary;

/**
 *
 * @author thsc
 */
public interface ASIPKnowledge {
    
    public final static String VOCABULARY = "VOCABULARY";
    public final static String INFORMATIONSPACES = "INFORMATIONSPACES";
//    public final static String INFORMATIONPOINTS = "INFORMATIONPOINTS";
    
    /**
     * Merge (create copies) of information into described space.
     * 
     * @param information
     * @param space
     * @return
     * @throws SharkKBException 
     */
    public ASIPInformationSpace mergeInformation(Iterator<ASIPInformation> information,
            ASIPSpace space) throws SharkKBException;

    /**
     * Add information with it semantical annotations to knowledge base.
     * 
     * @param content
     * @param semanticAnnotations
     * @return
     * @throws SharkKBException 
     */
    public ASIPInformationSpace addInformation(byte[] content, 
            ASIPSpace semanticAnnotations) 
            throws SharkKBException;
    
    /**
     * Add information with it semantical annotations to knowledge base.
     * 
     * @param contentOS
     * @param numberOfBytes
     * @param semanticAnnotations
     * @return
     * @throws SharkKBException 
     */
    public ASIPInformationSpace addInformation(OutputStream contentOS, 
            int numberOfBytes, ASIPSpace semanticAnnotations) 
            throws SharkKBException;
    
    /**
     * Add information with it semantical annotations to knowledge base.
     * 
     * @param content
     * @param semanticAnnotations
     * @return
     * @throws SharkKBException 
     */
    public ASIPInformationSpace addInformation(String content, 
            ASIPSpace semanticAnnotations) 
            throws SharkKBException;
    
    /**
     * Remove information from described space. The infoSpace does not have to
     * be the same as during adding that information. That method just takes that
     * information out of that space. Information can reside in another space,
     * though.
     * 
     * @param info
     * @param infoSpace
     * @throws SharkKBException 
     */
    public void removeInformation(Information info, ASIPSpace infoSpace)
          throws SharkKBException;
    
    public Iterator<ASIPInformation> getInformation(ASIPSpace infoSpace)
          throws SharkKBException;
    
    /**
     * Return info spaces. That message does not return mutli copies
     * of information. Each information is only present in on information
     * space.
     * 
     * @return
     * @throws SharkKBException 
     */
    public Iterator<ASIPInformationSpace> informationSpaces()
          throws SharkKBException;
            
    /**
     * Clean up that space by removing all information. Removing does not
     * necessarily mean that any information is lost. Information which are
     * also in another space are still stored in that knowledge but not in the
     * given space.
     * 
     * @param space
     * @throws SharkKBException 
     */
    public void removeInformation(ASIPSpace space) throws SharkKBException;

  /**
   * Return a ContextSpace containing Tag for the different coordinates of the
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
    public SharkVocabulary getVocabulary();

    public int getNumberInformation() throws SharkKBException;
}
