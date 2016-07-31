package net.sharkfw.knowledgeBase.sync;

import java.util.ArrayList;
import java.util.Enumeration;
import net.sharkfw.knowledgeBase.SemanticTag;
import net.sharkfw.knowledgeBase.TXSemanticTag;
import net.sharkfw.system.Iterator2Enumeration;

/**
 *
 * @author thsc
 */
class SyncTXSemanticTag extends SyncSemanticTag implements TXSemanticTag {
    private final TXSemanticTag target;
    
    SyncTXSemanticTag(TXSemanticTag target) {
        super(target);
        this.target = target;
    }
    
    SyncTXSemanticTag wrapSyncObject(TXSemanticTag target) {
        if(target != null) {
            return new SyncTXSemanticTag(target);
        }
        return null;
    }
    
    @Override
    public Enumeration<SemanticTag> subTags() {
        Enumeration<TXSemanticTag> subTags = this.target.getSubTags();
        
        // wrap them
        ArrayList wrapSTIter = this.wrapSTEnum(this, subTags);
        return new Iterator2Enumeration(wrapSTIter.iterator());
    }

    @Override
    public TXSemanticTag getSuperTag() {
        TXSemanticTag superTag = this.target.getSuperTag();
        return this.wrapSyncObject(superTag);
    }

    @Override
    public Enumeration<TXSemanticTag> getSubTags() {
        Enumeration<TXSemanticTag> subTags = this.target.getSubTags();
        
        // wrap them
        ArrayList wrapSTIter = this.wrapSTEnum(this, subTags);
        return new Iterator2Enumeration(wrapSTIter.iterator());
    }

    @Override
    public void move(TXSemanticTag supertag) {
        this.target.move(supertag);
    }

    @Override
    public void merge(TXSemanticTag toMerge) {
        this.target.merge(toMerge);
    }
    
}
