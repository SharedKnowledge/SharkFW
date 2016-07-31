package net.sharkfw.knowledgeBase.sync;

import net.sharkfw.knowledgeBase.TimeSemanticTag;

/**
 *
 * @author thsc
 */
class SyncTimeSemanticTag extends SyncSemanticTag implements TimeSemanticTag {
    private final TimeSemanticTag target;
    
    public SyncTimeSemanticTag(TimeSemanticTag target) {
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
    public long getFrom() {
        return this.target.getFrom();
    }

    @Override
    public long getDuration() {
        return this.target.getDuration();
    }
}
