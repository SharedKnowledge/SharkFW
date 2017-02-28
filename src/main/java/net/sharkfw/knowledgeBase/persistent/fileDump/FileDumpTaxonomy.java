package net.sharkfw.knowledgeBase.persistent.fileDump;

import net.sharkfw.knowledgeBase.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;

/**
 * Created by j4rvis on 2/28/17.
 */
public class FileDumpTaxonomy extends FileDumpSTSet implements Taxonomy {

    private final Taxonomy taxonomy;

    public FileDumpTaxonomy(FileDumpSharkKB kb, Taxonomy taxonomy) {
        super(kb, taxonomy);
        this.taxonomy = taxonomy;
    }

    @Override
    public void merge(TXSemanticTag tag) throws SharkKBException {
        this.taxonomy.merge(tag);
        this.kb.persist();
    }

    @Override
    public void move(TXSemanticTag superTag, TXSemanticTag subTag) throws SharkKBException {
        this.taxonomy.move(superTag, subTag);
        this.kb.persist();
    }

    @Override
    public TXSemanticTag createSemanticTag(TXSemanticTag superTag, String name, String[] sis) throws SharkKBException {
        TXSemanticTag semanticTag = this.taxonomy.createSemanticTag(superTag, name, sis);
        this.kb.persist();
        return new FileDumpTXSemanticTag(this.kb, semanticTag);
    }

    @Override
    public void removeSemanticTag(TXSemanticTag tag) throws SharkKBException {
        this.taxonomy.removeSemanticTag(tag);
        kb.persist();
    }

    @Override
    public void removeSubTree(TXSemanticTag tag) throws SharkKBException {
        taxonomy.removeSubTree(tag);
        kb.persist();
    }

    @Override
    public Enumeration<TXSemanticTag> rootTags() throws SharkKBException {
        Enumeration<TXSemanticTag> txSemanticTagEnumeration = this.taxonomy.rootTags();
        ArrayList<TXSemanticTag> list = new ArrayList<>();
        while (txSemanticTagEnumeration.hasMoreElements()){
            list.add(new FileDumpTXSemanticTag(this.kb, txSemanticTagEnumeration.nextElement()));
        }
        return Collections.enumeration(list);
    }

    @Override
    public boolean isSubTag(TXSemanticTag root, TXSemanticTag tag) {
        return taxonomy.isSubTag(root, tag);
    }

    @Override
    public TXSemanticTag createTXSemanticTag(String name, String[] sis) throws SharkKBException {
        TXSemanticTag semanticTag = this.taxonomy.createTXSemanticTag(name, sis);
        this.kb.persist();
        return new FileDumpTXSemanticTag(this.kb, semanticTag);
    }

    @Override
    public TXSemanticTag createTXSemanticTag(String name, String si) throws SharkKBException {
        TXSemanticTag semanticTag = this.taxonomy.createTXSemanticTag(name, si);
        this.kb.persist();
        return new FileDumpTXSemanticTag(this.kb, semanticTag);
    }

    @Override
    public Taxonomy contextualizeTaxonomy(STSet context, FragmentationParameter fp) throws SharkKBException {
        Taxonomy taxonomy = this.taxonomy.contextualizeTaxonomy(context, fp);
        kb.persist();
        return new FileDumpTaxonomy(this.kb, taxonomy);
    }

    @Override
    public Taxonomy fragmentTaxonomy(SemanticTag anchor, FragmentationParameter fp) throws SharkKBException {
        Taxonomy taxonomy = this.taxonomy.fragmentTaxonomy(anchor, fp);
        kb.persist();
        return new FileDumpTaxonomy(this.kb, taxonomy);

    }

    @Override
    public TXSemanticTag getSemanticTag(String[] sis) throws SharkKBException {
        return new FileDumpTXSemanticTag(this.kb, taxonomy.getSemanticTag(sis));
    }
    @Override
    public TXSemanticTag getSemanticTag(String si) throws SharkKBException {
        return new FileDumpTXSemanticTag(this.kb, taxonomy.getSemanticTag(si));
    }
}
