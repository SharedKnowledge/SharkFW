package net.sharkfw.knowledgeBase.inmemory;

import net.sharkfw.system.TimeLong;
import net.sharkfw.knowledgeBase.SystemPropertyHolder;
import net.sharkfw.knowledgeBase.SharkKBException;
import net.sharkfw.knowledgeBase.SharkURI;
import net.sharkfw.knowledgeBase.TimeSemanticTag;

/**
 * The in-memory implementation of the TimeSemanticTag, describing a timespan.
 *
 * @author thsc
 * @author mfi
 */
@SuppressWarnings("unchecked")
public class InMemoTimeSemanticTag extends InMemo_SN_TX_SemanticTag 
                        implements TimeSemanticTag {
    
    private long from, duration;
    
    public InMemoTimeSemanticTag(String name, String[] sis) {
        super(name, sis);
        
        // TODO parse sis
        this.from = TimeSemanticTag.FIRST_MILLISECOND_EVER;
        this.duration = TimeSemanticTag.FOREVER;
    }
    
    /**
     * creates an TST covering a period from
     * @param from Start time - milliseconds beginning from 1.1.1970
     * @param duration duration of period in milliseconds
     */
    public InMemoTimeSemanticTag(long from, long duration) {
        super("timeST", new String[] {SharkURI.timeST(from, duration)});
        
        this.from = from;
        this.duration = duration;
    }
    
    /**
     * creates an TST that covers the whole time from eternity to eternity
     * @param from
     * @param duration
     */
    public InMemoTimeSemanticTag() {
        this(TimeSemanticTag.FIRST_MILLISECOND_EVER, TimeSemanticTag.FOREVER);
    }
    
    @SuppressWarnings("rawtypes")
    public InMemoTimeSemanticTag(SystemPropertyHolder ph, InMemoGenericTagStorage storage) {
        super(ph, storage);
    }

    /**
     * creates an time semantic tag by a URI
     * @param uri
     * @throws SharkKBException in case of malformed shark time URI
     */
    public static InMemoTimeSemanticTag createTimeSemanticTag(String uri) throws SharkKBException {
        // must start with sharktimeprefix
        int index = uri.indexOf(SharkURI.TIME_PREFIX);
        
        if(index == -1) {
            throw new SharkKBException("malformed shark time uri: prefix doesn't match");
        }
        
        // init with defaults
        long fromValue = TimeSemanticTag.FIRST_MILLISECOND_EVER;
        long durationValue = TimeSemanticTag.FOREVER;
        
        // find from
        long retval = parseLongValue(uri, SharkURI.TIME_FROM_TAG);
        if(retval != -1) {
            fromValue = retval;
        }
        
        retval = parseLongValue(uri, SharkURI.TIME_DURATION_TAG);
        if(retval != -1) {
            durationValue = retval;
        }
        
        return new InMemoTimeSemanticTag(fromValue, durationValue);
        
    } 
    
    private static long parseLongValue(String uri, String tag) throws SharkKBException {
        long fromValue = -1; // -1 means not found
        
        int index = uri.indexOf(tag);
        if(index != -1) {

            // where starts the value
            index += tag.length();

            // where does it end?
            int nextIndex = uri.indexOf(SharkURI.TAG_SEPARATOR);

            String fromString = uri.substring(index, nextIndex);

            try {
                fromValue = TimeLong.parse(fromString);
            }
            catch(NumberFormatException nfe) {
                throw new SharkKBException("cannot parse tag <" + tag + "> in time semantic tag uri: " + uri);
            }
        }
        
        return fromValue;
    }

    @Override
    public long getFrom() {
        return this.from;
    }

    @Override
    public long getDuration() {
        return this.duration;
    }
}
