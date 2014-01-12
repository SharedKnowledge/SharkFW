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
        return (SNSemanticTag) super.merge(tag);
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


//  /**
//   * The central way finding method. The wayfinding implemented works like a breadth first search
//   * to find the shortest possible paths.
//   *
//   * TODO: Add support for allowed/forbidden properties
//   *
//   * @param to destination tag
//   * @param queue The next tags to evaluate as a next step
//   * @param depth An integer value constraining the depth of the search
//   * @return An AssociatedSTSet containing the shortest way between the first member of <code>queue</code> and <code>to</code>
//   * @throws net.sharkfw.knowledgeBase.SharkKBException
//   */
//  private net.sharkfw.knowledgeBase.internal.SemanticNet findWay(SNSemanticTag to, java.util.Vector queue, java.util.Vector effAssocs, int depth) throws SharkKBException {
//
//    java.util.Vector todo = new java.util.Vector();
//
//    if (queue.size() == 0 || depth < 0) {
//      return null;
//    }
//
//    Enumeration senum = queue.elements();
//
//    // For each element from the last iteration
//    while (senum.hasMoreElements()) {
//      Stepstone s = (Stepstone) queue.elementAt(0);
//      queue.removeElementAt(0);
//      SNSemanticTag node = s.getOrigin();
//      if (Util.sameEntity(s.getOrigin().getSI(), to.getSI())) {
//        return s.getWay();
//      }
//
//      Enumeration types = node.predicateNames();
//      if (types == null) {
//        // Reached dead end
//        continue;
//      }
//      while (types.hasMoreElements()) {
//        String type = (String) types.nextElement();
//        if (effAssocs.contains(type)) {
//          Enumeration concepts = node.getAssociatedTags(type);
//          while (concepts.hasMoreElements()) {
//            SNSemanticTag nc = (SNSemanticTag) concepts.nextElement();
//            try {
//              // must not work, else the concept has been visited before
//              s.getWay().getSemanticTag(nc.getSI());
//              /*
//               * If the current concept is the destination,
//               * nothing more needs to be done.
//               */
//              if (Util.sameEntity(nc.getSI(), to.getSI())) {
//                return s.getWay();
//              }
//              continue;
//            } catch (SharkKBException sex) {
//              Stepstone ns = new Stepstone(s.getWay(), nc);
//
//              SNSemanticTag sNode;
//              try {
//                // If it already exists do not add it again
//                sNode = ns.getWay().getAssociatedSemanticTag(node.getSI());
//              } catch (SharkKBException ske) {
//                // If it isn't there, add it
//                sNode = this.createAssociatedTagInFragment(ns.getWay(), node);
//              }
//
//              SNSemanticTag ncNode = this.createAssociatedTagInFragment(ns.getWay(), nc);
//              sNode.setPredicate(type, ncNode); // Save the association inside the stset representing the way
//
//              todo.add(ns);
//            }
//          }
//        }
//      }
//    }
//    return findWay(to, todo, effAssocs, --depth);
//  }
//
//  /**
//   * This class remembers the path taken from <code>origin</code>.
//   * The path is saved in <code>way</code>.
//   */
//  private class Stepstone {
//
//    net.sharkfw.knowledgeBase.internal.SemanticNet way;
//    SNSemanticTag origin;
//    XMLSerializer xml = new XMLSerializer();
//
//    public Stepstone(net.sharkfw.knowledgeBase.internal.SemanticNet net, SNSemanticTag nc) {
//      try {
//        // FIXME: Nicer copy-method here.
//        this.way = (net.sharkfw.knowledgeBase.internal.SemanticNet) xml.deserializeSTSet(xml.serializeSTSet(net));
//        this.origin = nc;
//      } catch (SharkNotSupportedException ex) {
//        throw new RuntimeException(ex.getMessage());
//      } catch (SharkKBException ex) {
//        throw new RuntimeException(ex.getMessage());
//      }
//    }
//
//    public net.sharkfw.knowledgeBase.internal.SemanticNet getWay() {
//      return this.way;
//    }
//
//    public SNSemanticTag getOrigin() {
//      return this.origin;
//    }
//  }
//
//  // end of findWay related methods

    @Override
    public STSet asSTSet() {
        return this;
    }

}
