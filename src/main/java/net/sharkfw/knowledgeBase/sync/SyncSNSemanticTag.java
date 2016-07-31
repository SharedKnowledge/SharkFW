package net.sharkfw.knowledgeBase.sync;

import java.util.Enumeration;
import net.sharkfw.knowledgeBase.SNSemanticTag;
import net.sharkfw.knowledgeBase.SemanticTag;

/**
 *
 * @author thsc
 */
class SyncSNSemanticTag extends SyncSemanticTag implements SNSemanticTag {
    private SNSemanticTag target = null;

    public SyncSNSemanticTag(SNSemanticTag target) {
        super(target);
        
        this.target = target;
    }
    
    public SyncSNSemanticTag(SemanticTag target) {
        super(target);
    }
    
    @Override
    public Enumeration<String> predicateNames() {
        return this.target.predicateNames();
    }

    @Override
    public Enumeration<String> targetPredicateNames() {
        return this.target.targetPredicateNames();
    }

    @Override
    public Enumeration<SNSemanticTag> targetTags(String predicateName) {
        return this.target.targetTags(predicateName);
    }

    @Override
    public Enumeration<SNSemanticTag> sourceTags(String predicateName) {
        return this.target.sourceTags(predicateName);
    }

    @Override
    public void setPredicate(String type, SNSemanticTag target) {
        this.target.setPredicate(type, target);
    }

    @Override
    public void removePredicate(String type, SNSemanticTag target) {
        this.target.removePredicate(type, target);
    }

    @Override
    public void merge(SNSemanticTag toMerge) {
        this.target.merge(toMerge);
    }
}
