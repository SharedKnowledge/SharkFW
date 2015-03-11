package net.sharkfw.knowledgeBase;

import java.util.Enumeration;
import java.util.Iterator;

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
     * @throws net.sharkfw.knowledgeBase.SharkKBException
   */
  public TimeSTSet fragment(TimeSemanticTag anchor) throws SharkKBException;
  
  public TimeSTSet contextualize(TimeSTSet context, FragmentationParameter fp) throws SharkKBException;

    /**
     * Create a TimeSemanticTag with absolute start and endpoint given in milliseconds since 1.1.1970.
     *
     * @param from Startpoint of the timespan.
     * @param duration
     * @return A new TimeSemanticTag with the given values.
     * @throws SharkKBException
     */
    public TimeSemanticTag createTimeSemanticTag(long from, long duration) 
            throws SharkKBException;

    public Enumeration<TimeSemanticTag> timeTags() throws SharkKBException;
    public Iterator<TimeSemanticTag> tstTags() throws SharkKBException;
}
