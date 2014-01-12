package net.sharkfw.knowledgeBase;

import java.util.Enumeration;

/**
 * TODO
 * @author mfi, thsc
 */
public interface TimeSTSet extends STSet {

  /**
   * Create a fragment of this TimeSTSet, by checking which tags overlap with
   * anchor.
   *
   * @param anchor The anchor point
   * @return A fragment of this TimeSTSet containing all the tags, that overlap with anchor.
   */
  public TimeSTSet fragment(TimeSemanticTag anchor) throws SharkKBException;
  
  public TimeSTSet contextualize(TimeSTSet context, FragmentationParameter fp) throws SharkKBException;

    /**
     * Create a TimeSemanticTag with absolute start and endpoint given in milliseconds since 1.1.1970.
     *
     * @param from Startpoint of the timespan.
     * @param to Endpoint of the timespan.
     * @param name The name of the tag.
     * @return A new TimeSemanticTag with the given values.
     * @throws SharkKBException
     */
    public TimeSemanticTag createTimeSemanticTag(long from, long duration) 
            throws SharkKBException;

    public TimeSemanticTag createTimeSemanticTag(String name, String[] sis) 
             throws SharkKBException;
    
    public TimeSemanticTag getTimeSemanticTag(String[] sis) throws SharkKBException;
    public TimeSemanticTag getTimeSemanticTag(String si) throws SharkKBException;
    
    public Enumeration<TimeSemanticTag> timeTags() throws SharkKBException;
}
