package net.sharkfw.knowledgeBase.persistent.dump;

import net.sharkfw.knowledgeBase.SemanticTag;
import net.sharkfw.knowledgeBase.SharkCSAlgebra;
import net.sharkfw.knowledgeBase.SharkKBException;

/**
 * Created by j4rvis on 2/27/17.
 */
public class DumpSemanticTag extends DumpSystemPropertyHolder implements SemanticTag {

    private final SemanticTag semanticTag;

    public DumpSemanticTag(DumpSharkKB dumpSharkKB, SemanticTag tag) {
        super(dumpSharkKB, tag);
        semanticTag = tag;
    }

    @Override
    public String getName() {
        return this.semanticTag.getName();
    }

    @Override
    public String[] getSI() {
        return this.semanticTag.getSI();
    }

    @Override
    public void removeSI(String si) throws SharkKBException {
        this.semanticTag.removeSI(si);
        this.kb.persist();
    }

    @Override
    public void addSI(String si) throws SharkKBException {
        this.semanticTag.addSI(si);
        this.kb.persist();
    }

    @Override
    public void setName(String newName) {
        this.semanticTag.setName(newName);
        this.kb.persist();
    }

    @Override
    public void merge(SemanticTag st) {
        this.semanticTag.merge(st);
        this.kb.persist();
    }

    @Override
    public void setHidden(boolean isHidden) {
        this.semanticTag.setHidden(isHidden);
        this.kb.persist();
    }

    @Override
    public boolean hidden() {
        return this.semanticTag.hidden();
    }

    @Override
    public boolean isAny() {
        return this.semanticTag.isAny();
    }

    @Override
    public boolean identical(SemanticTag other) {
        return SharkCSAlgebra.identical(this, other);
    }
}
