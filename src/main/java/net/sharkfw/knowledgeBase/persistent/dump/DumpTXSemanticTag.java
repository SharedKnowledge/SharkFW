package net.sharkfw.knowledgeBase.persistent.dump;

import net.sharkfw.knowledgeBase.SemanticTag;
import net.sharkfw.knowledgeBase.TXSemanticTag;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;

/**
 * Created by j4rvis on 2/28/17.
 */
public class DumpTXSemanticTag extends DumpSemanticTag implements TXSemanticTag{

    private final TXSemanticTag txSemanticTag;

    public DumpTXSemanticTag(DumpSharkKB dumpSharkKB, TXSemanticTag tag) {
        super(dumpSharkKB, tag);
        txSemanticTag = tag;
    }

    @Override
    public Enumeration<SemanticTag> subTags() {
        Enumeration<SemanticTag> semanticTagEnumeration = this.txSemanticTag.subTags();
        ArrayList<SemanticTag> list = new ArrayList<>();
        while (semanticTagEnumeration.hasMoreElements()){
            list.add(new DumpSemanticTag(this.kb, semanticTagEnumeration.nextElement()));
        }
        return Collections.enumeration(list);
    }

    @Override
    public TXSemanticTag getSuperTag() {
        return new DumpTXSemanticTag(this.kb, this.txSemanticTag.getSuperTag());
    }

    @Override
    public Enumeration<TXSemanticTag> getSubTags() {
        Enumeration<TXSemanticTag> semanticTagEnumeration = this.txSemanticTag.getSubTags();
        ArrayList<TXSemanticTag> list = new ArrayList<>();
        while (semanticTagEnumeration.hasMoreElements()){
            list.add(new DumpTXSemanticTag(this.kb, semanticTagEnumeration.nextElement()));
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
