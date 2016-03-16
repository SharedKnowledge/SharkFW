package net.sharkfw.knowledgeBase;

import java.util.ArrayList;
import java.util.Enumeration;
import net.sharkfw.knowledgeBase.inmemory.InMemoSemanticNet;

/**
 *
 * @author thsc
 */
public class ShortestPath {
    private boolean found = false;
    private SNSemanticTag endpoint = null;
    private SemanticNet shortestPath = null;

    private ArrayList<Step> steps = new ArrayList<Step>();
    private int length;
    
    ShortestPath(SemanticTag start, STSet target, STSet source) throws SharkKBException {

        SemanticNet snSource = null;
        SemanticNet snTarget = null;
        SNSemanticTag snSourceTag = null;

        if(source instanceof SemanticNet) {
            snSource = (SemanticNet) source;
            snSourceTag = snSource.getSemanticTag(start.getSI());
        }

        if(target instanceof SemanticNet) {
            snTarget = (SemanticNet) target;
        }

        if(snSource == null || snTarget == null || snSourceTag == null ) {
            throw new SharkKBException("find shortest path requires non empty semantic networks and an existing start point");
        }

        // add first step
        Step firstStep = new Step();
        
        // set up step
        firstStep.path = new InMemoSemanticNet(); // empty path
        
        // copy first tag into fragment and remember as first endpoint
        firstStep.endpoint = firstStep.path.merge(snSourceTag);
        
        firstStep.sourceTag = snSourceTag; // remember tag in source
        
        firstStep.length = 0;
        
        // add to list
        this.steps.add(firstStep);

        // search
        this.findPath(snTarget);
    }

    private void findPath(SemanticNet target) 
            throws SharkKBException {
        
        Step lastStep;
        // take last step from list
        while(true) {
            try {
                lastStep = this.steps.get(0);
                this.steps.remove(0);
            }
            catch(IndexOutOfBoundsException  e) {
                // list is empty - nothing found - what a pitty.
                return;
            }

            Enumeration<String> predicateEnum = lastStep.sourceTag.predicateNames();

            if(predicateEnum == null) {
                // it doesn't have predicates - its a dead end - go on
                continue;
            }
            
            while(predicateEnum.hasMoreElements()) {
                String predicate = predicateEnum.nextElement();

                Enumeration<SNSemanticTag> sourceTargetTagEnum = 
                        lastStep.sourceTag.targetTags(predicate);

                if(sourceTargetTagEnum != null) {
                    while(sourceTargetTagEnum.hasMoreElements()) {

                        SNSemanticTag sourceTargetTag = sourceTargetTagEnum.nextElement();

                        // prevent circle
                        if(lastStep.path.getSemanticTag(sourceTargetTag.getSI()) == null) {
                            
                            // a tag was found - which is the next step in path
                            
                            // last step ?
                            // if the tag is known in target - we are done
                            if(SharkCSAlgebra.isIn(target, sourceTargetTag)) {
                                // finished - use current step and finish
                                // step is made - remember in path
                                SNSemanticTag newEndpoint = lastStep.path.merge(sourceTargetTag);
                                lastStep.endpoint.setPredicate(predicate, newEndpoint);
                                lastStep.endpoint = newEndpoint;
                                
                                this.endpoint = newEndpoint;
                                this.shortestPath = lastStep.path;
                                this.length = lastStep.length+1;
                                this.found = true;
                                return;
                            } else {
                                // not found - make a copy and add to list
                                
                                Step nextStep = new Step();
                                
                                /* remember sourceTag
                                 * which actually is identical to the new endpoint
                                 * but: sourceTag is in source, endpoint in
                                 * temporary step fragment
                                 */
                                nextStep.sourceTag = sourceTargetTag;
                                
                                // copy previous path
                                nextStep.path = new InMemoSemanticNet();
                                nextStep.path.merge(lastStep.path);
                                
                                // find endpoint in copy
                                SNSemanticTag lastEndpoint = 
                                        nextStep.path.getSemanticTag(lastStep.endpoint.getSI());
                                
                                // add related tag from source to step
                                SNSemanticTag nextPoint = nextStep.path.merge(sourceTargetTag);
                                
                                // add step
                                lastEndpoint.setPredicate(predicate, nextPoint);
                                
                                // replace endpoint
                                nextStep.endpoint = nextPoint;
                                
                                // increase length
                                nextStep.length = lastStep.length+1;

                                // add step and go ahead
                                this.steps.add(nextStep);
                            }
                        } // prevent circle
                    } // enum target tags
                }
            } // enum predicate names
        } // main loop
    }

    boolean found() {
        return this.found;
    }

    SNSemanticTag endpoint() {
        return endpoint;
    }
    
    SemanticNet shortestPath() {
        return this.shortestPath;
    }
    
    private class Step {
        public SemanticNet path;
        public SNSemanticTag endpoint;
        public SNSemanticTag sourceTag;
        public int length;
    }
}