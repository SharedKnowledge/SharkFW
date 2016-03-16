package net.sharkfw.knowledgeBase.inmemory;

import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import net.sharkfw.knowledgeBase.*;
import net.sharkfw.system.Iterator2Enumeration;

/**
 * Follow delegate pattern
 * @author thsc
 */
public class InMemoTaxonomy extends TaxonomyWrapper implements Taxonomy, STSet {
    
    public InMemoTaxonomy(SemanticNet storage) {
        super(storage);
    }
    
    public InMemoTaxonomy() {
        this(new InMemoGenericTagStorage<>());
    }
    
    InMemoTaxonomy(InMemoGenericTagStorage storage) {  
        this.setStorage(new InMemoSemanticNet(storage));
    }

    @Override
    public TXSemanticTag createTXSemanticTag(String name, String[] sis) throws SharkKBException {
        TXSemanticTag st = this.getSemanticTag(sis);
        if (st != null) {
            return st;
        }
        st = new InMemo_SN_TX_SemanticTag(name, sis);
        this.sn.add(st);
        return st;
    }

    /**
     *
     * @param tag new super tag - if null - this tag becomes root tag
     * @throws SharkKBException
     */
    @Override
    public void removeSemanticTag(TXSemanticTag tag) throws SharkKBException {
        /* subtags are removed in semantic net implementation
         * But maybe there is a super tag that should become new supertag of
         * the subs.
         */
        SNSemanticTag snTag = this.sn.getSemanticTag(tag.getSI());
        if (snTag == null) {
            return;
        }
        // is there a super tag
        Enumeration<SNSemanticTag> superTagEnum = snTag.targetTags(SemanticNet.SUPERTAG);
        SNSemanticTag superTag = null;
        if (superTagEnum != null) {
            if (superTagEnum.hasMoreElements()) {
                superTag = superTagEnum.nextElement();
            }
        }
        // there is a super tag
        if (superTag != null) {
            Enumeration<SNSemanticTag> subTagEnum = snTag.sourceTags(SemanticNet.SUPERTAG);
            // first - tell super tag about removing
            snTag.removePredicate(SemanticNet.SUPERTAG, superTag);
            if (subTagEnum != null && subTagEnum.hasMoreElements()) {
                // is has sub tags
                // in any case - substitute super tag predicate in sub tags
                while (subTagEnum.hasMoreElements()) {
                    SNSemanticTag subTag = subTagEnum.nextElement();
                    // tell subtag about removal
                    subTag.removePredicate(SemanticNet.SUPERTAG, snTag);
                    // tell sub tags new super tag
                    if (superTag != null) {
                        subTag.setPredicate(SemanticNet.SUPERTAG, superTag);
                    }
                }
            }
        }
        this.sn.removeSemanticTag((SemanticTag) tag);
    }

    @Override
    public void removeSemanticTag(SemanticTag tag) throws SharkKBException {
        // this might look odd but it's necessary.
        if (tag instanceof TXSemanticTag) {
            this.removeSemanticTag((TXSemanticTag) tag);
        } else {
            this.sn.removeSemanticTag(tag);
        }
    }

    @Override
    public TXSemanticTag getSemanticTag(String[] sis) throws SharkKBException {
        SemanticTag st = this.sn.getSemanticTag(sis);
        if (st instanceof TXSemanticTag) {
            return (TXSemanticTag) st;
        } else {
            return null;
        }
    }

    /**
     * @return enumeration of all root tags or null if there is no root tag.
     * @throws SharkKBException
     */
    @Override
    public Enumeration rootTags() throws SharkKBException {
        Enumeration<SemanticTag> tagEnum = this.sn.tags();
        if (tagEnum == null) {
            return null;
        }
        HashSet rootTags = new HashSet();
        while (tagEnum.hasMoreElements()) {
            SemanticTag st = tagEnum.nextElement();
            if (st instanceof TXSemanticTag) {
                TXSemanticTag txst = (TXSemanticTag) st;
                if (txst.getSuperTag() == null) {
                    // no super tag - its a root tag
                    rootTags.add(txst);
                }
            }
        }
        if (rootTags.isEmpty()) {
            return null;
        } else {
            return new Iterator2Enumeration(rootTags.iterator());
        }
    }
}
