package net.sharkfw.knowledgeBase;

/**
 * 
 */
public interface TimeSemanticTag extends SemanticTag {
    public final static long FIRST_MILLISECOND_EVER = 0;
    public final static long FOREVER = 0;
    
    /**
     * 
     * @return first millisecond at which a communication shall take place. 
     * UNIX time is used.
     * 
     */
    public long getFrom();
    
    /**
     * 
     * @return duration of communication
     */
    public long getDuration();
    
}
