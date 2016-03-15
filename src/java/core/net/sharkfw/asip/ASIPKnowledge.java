package net.sharkfw.asip;

import java.io.InputStream;
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
    public ASIPInformation addInformation(byte[] content, 
            ASIPSpace semanticAnnotations) 
            throws SharkKBException;
    
    /**
     * Add information with it semantical annotations to knowledge base.
     * 
     * @param contentIS
     * @param numberOfBytes
     * @param semanticAnnotations
     * @return
     * @throws SharkKBException 
     */
    public ASIPInformation addInformation(InputStream contentIS, 
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
    public ASIPInformation addInformation(String content, 
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
     * Produces a list of information which are <i>in</i> the infoSpace.
     * The short word <i>in</i> has two meaning, though:
     * 
     * Each information is stored with its semantic annotations.
     * Let#s imagine some information are about topic A and B.
     * Those information are stored.
     * 
     * Lets look later for information which fit to topic A but
     * not necessarily B. Set fullyInside to false if you want to find 
     * our information.
     * 
     * That informations wouldn't be found if fullyInside is set to true.
     * It means, that infoSpace must fully cover all semantic annotations.
     * In our example, topics A and B <b>must</b> be part of infoSpace. 
     * 
     * There is another option: We don't have to define each semantic
     * aspect of information. We could look e.g. for information of <i>any</i>
     * topics in a dedicated location.
     * 
     * Set matchAny to true to allow any tags and to false otherwise.
     * 
     * @param infoSpace
     * @param fullyInside
     * @param matchAny
     * @return
     * @throws SharkKBException 
     */
    public Iterator<ASIPInformation> getInformation(ASIPSpace infoSpace, 
            boolean fullyInside, boolean matchAny)
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
