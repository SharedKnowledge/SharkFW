package net.sharkfw.knowledgeBase.inmemory;

import java.util.Enumeration;
import java.util.Iterator;
import net.sharkfw.knowledgeBase.*;

/**
 * In-Memory implementation of a TimeSTSet featuring the internal interfaces and
 * the simplifed interface for external programmers.
 *
 * TODO: Override addSemanticTag and check the type of the tag to be created.
 * Allow only TimeSemanticTags.
 * 
 * @author mfi, thsc
 */
public class InMemoTimeSTSet extends InMemoSTSet implements TimeSTSet {
    
    InMemoTimeSTSet() {
        super(new InMemoGenericTagStorage<TimeSemanticTag>());
    }
    
    @SuppressWarnings("rawtypes")
    public InMemoTimeSTSet(InMemoGenericTagStorage storage) {
        super(storage);
    }

    private TimeSemanticTag castTST(SemanticTag st) throws SharkKBException {
        if(st == null) return null;

        if(st instanceof TimeSemanticTag) return (TimeSemanticTag) st;
        
        throw new SharkKBException("cannot use non time semantic tag in time semantic tag set");
    }

    @Override
    public TimeSTSet fragment(TimeSemanticTag anchor) throws SharkKBException {
        Enumeration<SemanticTag> tagEnum = this.tags();
        if(tagEnum == null) return null;
        
        // create a fragment
        InMemoTimeSTSet fragment = new InMemoTimeSTSet();
        
        // no enumerate all tags and look for intersection
        long aFrom = anchor.getFrom();
        long aEnd = anchor.getDuration() + aFrom;

        while(tagEnum.hasMoreElements()) {
            TimeSemanticTag tst = this.castTST(tagEnum.nextElement());
            long bFrom = tst.getFrom();
            long bEnd = tst.getDuration() + bFrom;
            
            /**
            * 1. Dont overlap:
            * aFrom > bEnd || aEnd < bFrom
            * 
            * 2. A before B
            * aFrom <= bFrom && aEnd <= bEnd
            * -> bFrom, lt(aEnd, bEnd)
            * 
            * 3. B before A
            * bFrom <= aFrom && bEnd <= aEnd
            * -> bFrom
            */
            
            // case 1
            if(aFrom > bEnd || aEnd < bFrom) continue;
            
            if(aFrom <= bFrom && aEnd <= bEnd) {
                long earliestEnd = aEnd < bEnd ? aEnd : bEnd;
                fragment.createTimeSemanticTag(bFrom, earliestEnd - bFrom);
            } 
            else if(bFrom <= aFrom && bEnd <= aEnd) {
                long earliestEnd = aEnd < bEnd ? aEnd : bEnd;
                fragment.createTimeSemanticTag(aFrom, earliestEnd - aFrom);
            }
        }
        
        if(fragment.tags() == null) {
            return null;
        } 
        
        return fragment;
        
    }
    
    /**
     * Both tags are assumed to be already in the set. Concepts can be deleted
     * during this methode call.
     * 
     * @return the resulting tst i both tags overlap. Null is returned only
     * if both concepts don't overlap. 
     */
    @SuppressWarnings("unused")
    private TimeSemanticTag merge(TimeSemanticTag a, TimeSemanticTag b) throws SharkKBException {
        // check if two identical tags are to be merged - makes no sense
        if(a == b) return null;
        
        TimeSemanticTag retval = null;

        /**
            * Do tag and tst overlap
            * we have these cases:
            * 1. A is bevor B -> no overlapping
            * A.end < B.from
            * 
            * 2. A is behind B -> no overlapping
            * A.from > B.end
            * 
            * 3. A contains B -> remove B
            * A.from =< B.from && A.end >= B.end
            * 
            * 4. B contains A -> remove A
            * B.from =< A.from && B.end => A.end
            * 
            * 5. B begins in A -> merge and drop both
            * A.from <= B.from && A.end < B.end -> new.from = tag.from, end = greatest end.
            * 
            * 6. A begins in B -> merge and drop both
            * B.from <= A.from && B.end < A.end (tag starts in tst) -> new.from = tst.from, end = greatest end.
            */

        long aFrom = a.getFrom();
        long aEnd = a.getDuration() + aFrom;

        long bFrom = b.getFrom();
        long bEnd = b.getDuration() + bFrom;

        // case 1 and 2
        if(aEnd < bFrom || bEnd < aFrom) return null;

        // case 3: B in A
        if(aFrom <= bFrom && aEnd >= bEnd) {
            this.removeSemanticTag(b);
            return a;
        }

        // case 4: A in B
        if(aFrom >= bFrom && bEnd >= aEnd) {
            this.removeSemanticTag(a);
            return b;
        }

        // case 5 (overlaps slightly with 3 but 3 is already done)
        if(aFrom <= bFrom && aEnd <= bEnd) {
            long longestEnd = aEnd > bEnd ? aEnd : bEnd;
            this.removeSemanticTag(a);
            this.removeSemanticTag(b);
            return this.createTimeSemanticTag(aFrom, longestEnd - aFrom);
        }

        // case 6 (overlaps slightly with 4 but 4 is already done)
        if(aFrom >= bFrom && aEnd >= bEnd) {
            long longestEnd = aEnd > bEnd ? aEnd : bEnd;
            this.removeSemanticTag(a);
            this.removeSemanticTag(b);
            return this.createTimeSemanticTag(bFrom, longestEnd - bFrom);
        }
        
        return null;
    }
    
    /**
     * checks for overlapping and substitutes overlapping concepts
     * @return true if something was changed
     */
    private boolean fitin(TimeSemanticTag tst) throws SharkKBException {
        boolean changed = false;
        
        Enumeration<SemanticTag> tagEnum = this.tags();
        if(tagEnum == null) {
            // impossible - at least tst should be in
            return false;
        }
        
        while(tagEnum.hasMoreElements()) {
            TimeSemanticTag tag = this.castTST(tagEnum.nextElement());
            
            TimeSemanticTag result = this.merge(tag, tst);
            
            changed = result != tag && result != tst;
        }
        
        return changed;
    }
    
    @Override
    public void add(SemanticTag st) throws SharkKBException {
        TimeSemanticTag tst = this.castTST(st);

        // add tag at first
        super.add(st);
        
        // duplicate supression
        this.fitin(tst);
    }

    @Override
    public TimeSTSet fragment(SemanticTag anchor) throws SharkKBException {
        return this.fragment(this.castTST(anchor));
    }

    @Override
    public TimeSTSet fragment(SemanticTag anchor, FragmentationParameter fp) throws SharkKBException {
        
        // ignore fp - makes no sense here
        return this.fragment(this.castTST(anchor));
    }
    
    @Override
    public TimeSemanticTag merge(SemanticTag st) throws SharkKBException {
        if(st == null) return null;
        TimeSemanticTag tst = this.castTST(st);
        // it is a tst - add a copy
        return this.createTimeSemanticTag(tst.getFrom(), tst.getDuration());
    }
    
    @Override
    public void merge(STSet stSet) throws SharkKBException {
        if(stSet == null) return;
        
        Enumeration<SemanticTag> tagEnum = stSet.tags();
        if(tagEnum != null) {
            while (tagEnum.hasMoreElements()) {
                this.merge(tagEnum.nextElement());
            }
        }
    }

    @Override
    public TimeSemanticTag createTimeSemanticTag(long from, long duration) throws SharkKBException {
        TimeSemanticTag tst = new InMemoTimeSemanticTag(from, duration);
        
        this.add(tst);
        
        return tst;
    }

    @Override
    public SemanticTag createSemanticTag(String name, String[] sis) throws SharkKBException {
        throw new SharkKBException("don't create plain semantic tag in time semantic tag set");
    }
    
    @Override
    public SemanticTag createSemanticTag(String name, String si) throws SharkKBException {
        throw new SharkKBException("don't create plain semantic tag in time semantic tag set");
    }
    
    @Override
    public TimeSTSet contextualize(TimeSTSet context, FragmentationParameter fp) throws SharkKBException {
        if(context == null) {
            return null;
        }
        Enumeration<SemanticTag> anchorSet = context.tags();
        
        if(anchorSet == null || !anchorSet.hasMoreElements()) {
            return null;
        }
        
        InMemoTimeSTSet fragment = new InMemoTimeSTSet();
        
        while(anchorSet.hasMoreElements()) {
            TimeSemanticTag tag = this.castTST(anchorSet.nextElement());
            
            TimeSTSet s = this.fragment(tag);
            if(s != null) fragment.merge(s);
        }
        
        return fragment;
    }

    @Override
    public Enumeration<TimeSemanticTag> timeTags() throws SharkKBException {
        Enumeration tags = super.tags();
        return tags;
    }
    
    @Override
    public Iterator<TimeSemanticTag> tstTags() throws SharkKBException {
        Iterator stTags = super.stTags();
        return stTags;
    }
}
