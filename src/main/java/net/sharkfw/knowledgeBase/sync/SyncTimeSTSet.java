package net.sharkfw.knowledgeBase.sync;

import java.util.Enumeration;
import java.util.Iterator;
import net.sharkfw.knowledgeBase.FragmentationParameter;
import net.sharkfw.knowledgeBase.SharkKBException;
import net.sharkfw.knowledgeBase.TimeSTSet;
import net.sharkfw.knowledgeBase.TimeSemanticTag;
import net.sharkfw.knowledgeBase.inmemory.InMemoSharkKB;
import net.sharkfw.system.Iterator2Enumeration;

/**
 *
 * @author thsc
 */
class SyncTimeSTSet extends SyncSTSet implements TimeSTSet {
    private final TimeSTSet target;
    
    public SyncTimeSTSet(TimeSTSet target) {
        super(target);
        
        this.target = target;
    }
    
    SyncTimeSemanticTag wrapSyncObject(TimeSemanticTag target) {
        if(target != null) {
            return new SyncTimeSemanticTag(target);
        }
        
        return null;
    }

    @Override
    public TimeSTSet fragment(TimeSemanticTag anchor) throws SharkKBException {
        return this.target.fragment(anchor);
    }

    @Override
    public TimeSTSet contextualize(TimeSTSet context, FragmentationParameter fp) throws SharkKBException {
        return this.target.contextualize(context, fp);
    }

    @Override
    public TimeSemanticTag createTimeSemanticTag(long from, long duration) throws SharkKBException {
        return this.wrapSyncObject(this.target.createTimeSemanticTag(from, duration));
    }

    @Override
    public Enumeration<TimeSemanticTag> timeTags() throws SharkKBException {
        Enumeration<TimeSemanticTag> timeTags = this.target.timeTags();
        
        return new Iterator2Enumeration(this.wrapSTEnum(this, timeTags).iterator());
    }

    @Override
    public Iterator<TimeSemanticTag> tstTags() throws SharkKBException {
        return this.wrapSTIter(this, this.target.tstTags()).iterator();
    }

    @Override
    TimeSTSet getChanges(Long since) throws SharkKBException {
        TimeSTSet changes = InMemoSharkKB.createInMemoTimeSTSet();
        this.putChanges(since, changes);
        return changes;
    }
}