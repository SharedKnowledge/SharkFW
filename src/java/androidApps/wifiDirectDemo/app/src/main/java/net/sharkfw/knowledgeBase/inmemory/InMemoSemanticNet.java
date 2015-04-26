package net.sharkfw.knowledgeBase.inmemory;

import java.util.Enumeration;
import net.sharkfw.knowledgeBase.*;

/**
 * In memory implementation of SemanticNet interface. Most things a done simply by
 * delegating the jobs to InMemo_SN_TX_SemanticTag and to CSAlgebra.
 * 
 * @since 2.0
 * @author thsc
 */
public class InMemoSemanticNet extends InMemoSTSet implements SemanticNet {
    
    // just to make it explicit - this class can be used standalone
    public InMemoSemanticNet() {
        super();
    }
    
    @SuppressWarnings("rawtypes")
    public InMemoSemanticNet(InMemoGenericTagStorage storage) {    
        super(storage);
    }
    
    @Override
    public SNSemanticTag createSemanticTag(String name, String[] si) 
            throws SharkKBException {
        
        SNSemanticTag st = this.getSemanticTag(si);
        if(st != null) {
            return st;
        }
        
        st = new InMemo_SN_TX_SemanticTag(name, si);
        this.add(st);
        
        return st;
    }
    
    @Override
    public SNSemanticTag createSemanticTag(String name, String si) 
            throws SharkKBException {
        
        return this.createSemanticTag(name, new String[] {si});
    }

    @Override
    public SNSemanticTag getSemanticTag(String[] sis) throws SharkKBException {
      return (SNSemanticTag) super.getSemanticTag(sis);
    }

    @Override
    public SNSemanticTag getSemanticTag(String si) throws SharkKBException {
      return (SNSemanticTag) super.getSemanticTag(si);
    }

    /**
     * This methods just copies the tag as it is. No relations are copied
     * or even related tags. Use merging of tag set for that task.
     * @param tag
     * @return
     * @throws SharkKBException 
     */
    @Override
    public SNSemanticTag merge(SemanticTag tag) throws SharkKBException {
        // a SNSemanticTag is assumed as reply - therefore a SNSemanticTag must be inserted
        
        SNSemanticTag snTag;
        if(tag instanceof SNSemanticTag) {
            snTag = (SNSemanticTag) tag;
        } else {
            snTag = InMemoSharkKB.createInMemoCopyToSNSemanticTag(tag);
        }
        return (SNSemanticTag) super.merge(snTag);
    }

    protected void add(SNSemanticTag tag) throws SharkKBException {
        super.add(tag);
    }
    
    @Override
    public void removeSemanticTag(SemanticTag tag) {
        // this might look odd but it's necessary.
        if(tag instanceof InMemo_SN_TX_SemanticTag) {
            this.removeSemanticTag((SNSemanticTag) tag);
        } else {
            super.removeSemanticTag(tag);
        }
    }

    @Override
    public void removeSemanticTag(SNSemanticTag tag) {
        /*
         * We keep any predicate twice: in target tag as well as source tag
         * Thus, we have to remove targets as well as source references
         * when deleting that tag.
         */
        
        // inform source that this tag will leave
        Enumeration<String> targetPredicateNameEnum = tag.targetPredicateNames();
        if(targetPredicateNameEnum != null) {
            while(targetPredicateNameEnum.hasMoreElements()) {
                String predicateName = targetPredicateNameEnum.nextElement();
                
                Enumeration<SNSemanticTag> sourceTagEnum = 
                        tag.sourceTags(predicateName);
                
                if(sourceTagEnum != null) {
                    while(sourceTagEnum.hasMoreElements()) {
                        SNSemanticTag sourceTag = sourceTagEnum.nextElement();
                        
                        sourceTag.removePredicate(predicateName, tag);
                    }
                }
            }
        }
        
        /* remove predicates in which this tag is source
         * This also drops backward reference and thats the reason for 
         * this loop
         */
        Enumeration<String> predicateNames = tag.predicateNames();
        if(predicateNames != null) {
            while(predicateNames.hasMoreElements()) {
                String predicateName = predicateNames.nextElement();
                Enumeration<SNSemanticTag> targetTags = 
                        tag.targetTags(predicateName);
                
                if(targetTags != null) {
                    while(targetTags.hasMoreElements()) {
                        SNSemanticTag tTag = targetTags.nextElement();
                        tag.removePredicate(predicateName, tTag);
                    }
                }
            }
        }
        
        super.removeSemanticTag(tag);
    }
    
    @Override
    public void setPredicate(SNSemanticTag source, SNSemanticTag target, 
    String type) {
        
        source.setPredicate(type, target);
    }
    
    @Override
    public void removePredicate(SNSemanticTag source, SNSemanticTag target, 
            String type) throws SharkKBException {
        
        source.removePredicate(type, target);
    }
    
    @Override
    public SemanticNet fragment(SemanticTag anchor, 
        FragmentationParameter fp) throws SharkKBException {
        
        SemanticNet fragment = new InMemoSemanticNet();
        return SharkCSAlgebra.fragment(fragment, anchor, this, 
                fp.getAllowedPredicates(), 
                fp.getForbiddenPredicates(), fp.getDepth());
    }
    
    @Override
    public SemanticNet fragment(SemanticTag anchor) throws SharkKBException {
        return this.fragment(anchor, this.getDefaultFP());
    }

    @Override
    public SemanticNet contextualize(Enumeration<SemanticTag> anchorSet, 
        FragmentationParameter fp) throws SharkKBException {
        
            SemanticNet fragment = new InMemoSemanticNet();
            
            if(fp != null) {
                SharkCSAlgebra.contextualize(fragment, anchorSet, this, 
                        fp.getAllowedPredicates(), fp.getForbiddenPredicates(), 
                        fp.getDepth());
            } else {
                SharkCSAlgebra.contextualize(fragment, this, anchorSet);
            }
            
            return fragment;
    }

    @Override
    public SemanticNet contextualize(Enumeration<SemanticTag> anchorSet) 
            throws SharkKBException {
        
        return this.contextualize(anchorSet, this.getDefaultFP());
    }

    @Override
    public SemanticNet contextualize(STSet context, FragmentationParameter fp) 
            throws SharkKBException {
        
        if(context == null) return null;
        
        return this.contextualize(context.tags(), fp);
    }
    
    @Override
    public SemanticNet contextualize(STSet context) throws SharkKBException {
        return this.contextualize(context, this.getDefaultFP());
    }

    @Override
    public void merge(SemanticNet remoteSemanticNet) throws SharkKBException {
        SharkCSAlgebra.merge(this, remoteSemanticNet);
    }

    @Override
    public STSet asSTSet() {
        return this;
    }

}
