package net.sharkfw.knowledgeBase.persistent.dump;

import net.sharkfw.knowledgeBase.SemanticTag;
import net.sharkfw.knowledgeBase.TXSemanticTag;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;

/**
 * Created by j4rvis on 2/28/17.
 */
public class FileDumpTXSemanticTag extends FileDumpSemanticTag implements TXSemanticTag{

    private final TXSemanticTag txSemanticTag;

    public FileDumpTXSemanticTag(FileDumpSharkKB fileDumpSharkKB, TXSemanticTag tag) {
        super(fileDumpSharkKB, tag);
        txSemanticTag = tag;
    }

    @Override
    public Enumeration<SemanticTag> subTags() {
        Enumeration<SemanticTag> semanticTagEnumeration = this.txSemanticTag.subTags();
        ArrayList<SemanticTag> list = new ArrayList<>();
        while (semanticTagEnumeration.hasMoreElements()){
            list.add(new FileDumpSemanticTag(this.kb, semanticTagEnumeration.nextElement()));
        }
        return Collections.enumeration(list);
    }

    @Override
    public TXSemanticTag getSuperTag() {
        return new FileDumpTXSemanticTag(this.kb, this.txSemanticTag.getSuperTag());
    }

    @Override
    public Enumeration<TXSemanticTag> getSubTags() {
        Enumeration<TXSemanticTag> semanticTagEnumeration = this.txSemanticTag.getSubTags();
        ArrayList<TXSemanticTag> list = new ArrayList<>();
        while (semanticTagEnumeration.hasMoreElements()){
            list.add(new FileDumpTXSemanticTag(this.kb, semanticTagEnumeration.nextElement()));
        }
        return Collections.enumeration(list);
    }

    @Override
    public void move(TXSemanticTag supertag) {
        this.txSemanticTag.move(supertag);
        this.kb.persist();
    }

    @Override
    public void merge(TXSemanticTag toMerge) {
        this.txSemanticTag.merge(toMerge);
        this.kb.persist();
    }
}
