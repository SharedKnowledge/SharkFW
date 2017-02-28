package net.sharkfw.knowledgeBase.persistent.fileDump;

import net.sharkfw.knowledgeBase.SNSemanticTag;
import net.sharkfw.knowledgeBase.SemanticTag;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;

/**
 * Created by j4rvis on 2/28/17.
 */
public class FileDumpSNSemanticTag extends FileDumpSemanticTag implements SNSemanticTag {

    private final SNSemanticTag snSemanticTag;

    public FileDumpSNSemanticTag(FileDumpSharkKB fileDumpSharkKB, SNSemanticTag tag) {
        super(fileDumpSharkKB, tag);
        snSemanticTag = tag;
    }

    @Override
    public Enumeration<String> predicateNames() {
        return this.snSemanticTag.predicateNames();
    }

    @Override
    public Enumeration<String> targetPredicateNames() {
        return this.snSemanticTag.targetPredicateNames();
    }

    @Override
    public Enumeration<SNSemanticTag> targetTags(String predicateName) {
        Enumeration<SNSemanticTag> snSemanticTagEnumeration = this.snSemanticTag.targetTags(predicateName);
        ArrayList<SNSemanticTag> list = new ArrayList<>();
        while (snSemanticTagEnumeration.hasMoreElements()){
            list.add(new FileDumpSNSemanticTag(this.kb, snSemanticTagEnumeration.nextElement()));
        }
        return Collections.enumeration(list);
    }

    @Override
    public Enumeration<SNSemanticTag> sourceTags(String predicateName) {
        Enumeration<SNSemanticTag> snSemanticTagEnumeration = this.snSemanticTag.sourceTags(predicateName);
        ArrayList<SNSemanticTag> list = new ArrayList<>();
        while (snSemanticTagEnumeration.hasMoreElements()){
            list.add(new FileDumpSNSemanticTag(this.kb, snSemanticTagEnumeration.nextElement()));
        }
        return Collections.enumeration(list);

    }

    @Override
    public void setPredicate(String type, SNSemanticTag target) {
        this.snSemanticTag.setPredicate(type, target);
        this.kb.persist();
    }

    @Override
    public void removePredicate(String type, SNSemanticTag target) {
        this.snSemanticTag.removePredicate(type, target);
        this.kb.persist();
    }

    @Override
    public void merge(SNSemanticTag toMerge) {
        this.snSemanticTag.merge(toMerge);
        this.kb.persist();
    }
}
