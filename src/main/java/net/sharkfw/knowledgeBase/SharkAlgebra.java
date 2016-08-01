package net.sharkfw.knowledgeBase;

import java.util.Enumeration;
import java.util.Iterator;

import net.sharkfw.asip.ASIPInformation;
import net.sharkfw.asip.ASIPInterest;
import net.sharkfw.asip.ASIPSpace;
import net.sharkfw.knowledgeBase.inmemory.InMemoInterest;
import net.sharkfw.knowledgeBase.inmemory.InMemoSharkKB;
import net.sharkfw.system.L;
import net.sharkfw.system.Util;
import net.sharkfw.system.Utils;

/**
 *
 * @author thsc
 */
public class SharkAlgebra {
    
    //////////////////////////////////////////////////////////////////////////
    //                           isAny variants                             //
    //////////////////////////////////////////////////////////////////////////
    
    public static boolean isAny(ASIPSpace space) throws SharkKBException {
        return SharkAlgebra.isAny(SharkAlgebra.Space2Interest(space));
    }
    
    public static boolean isAny(ASIPInterest interest) {
        if(interest == null) return true;
        
        // else
        return (
            SharkCSAlgebra.isAny(interest.getTopics()) &&
            SharkCSAlgebra.isAny(interest.getTypes()) &&
            SharkCSAlgebra.isAny(interest.getApprovers()) &&
            SharkCSAlgebra.isAny(interest.getSenders()) &&
            SharkCSAlgebra.isAny(interest.getReceivers()) &&
            SharkCSAlgebra.isAny(interest.getTimes()) &&
            SharkCSAlgebra.isAny(interest.getLocations()) &&
            interest.getDirection() == ASIPSpace.DIRECTION_INOUT
        );
    }
    
    public static ASIPInterest contextualize(ASIPInterest source, 
            ASIPInterest context, FPSet fpSet) 
                throws SharkKBException {
        
        InMemoInterest interest = new InMemoInterest();
        SharkAlgebra.contextualize(interest, source, context, fpSet);
        
        return interest;
    }
    
    public static ASIPInterest Space2Interest(ASIPSpace source) 
            throws SharkKBException {
        
        PeerSTSet senders = InMemoSharkKB.createInMemoPeerSTSet();
        senders.merge(source.getSender());
        
        return InMemoSharkKB.createInMemoASIPInterest(source.getTopics(), 
                source.getTypes(), source.getApprovers(), senders, 
                source.getReceivers(), 
                source.getTimes(), source.getLocations(), 
                source.getDirection());
    }
    
    public static ASIPInterest contextualize(ASIPSpace source, ASIPSpace context, 
            FPSet fpSet) 
                throws SharkKBException {
        
        ASIPInterest i = InMemoSharkKB.createInMemoASIPInterest();
        
        if(SharkAlgebra.contextualize(i, source, context, fpSet)) {
            return i;
        } else {
            return null;
        }
    }
    
    public static boolean contextualize(ASIPInterest mutualInterest, 
            ASIPSpace source, ASIPSpace context, FPSet fpSet) 
            throws SharkKBException {
        
        ASIPInterest sourceInterest = SharkAlgebra.Space2Interest(source);
        ASIPInterest contextInterest = SharkAlgebra.Space2Interest(context);

        return SharkAlgebra.contextualize(mutualInterest, 
                sourceInterest, contextInterest, fpSet);
    }
    
    public static boolean contextualize(ASIPInterest mutualInterest, 
            ASIPInterest source, ASIPInterest context, FPSet fpSet) 
            throws SharkKBException {
        
        // start with the less complicated stuff - saves energy.
        
        ////////////////////////////////////////////////////////////
        //                      direction                         //  
        ////////////////////////////////////////////////////////////
        
        /*
         *   source | context | result
         * 1  IN       (IN)OUT   IN OUT
         * 2  OUT      IN        OUT IN
         * 3  INOUT    IN        OUT IN
         * 4  INOUT    OUT       IN OUT
         * 5  INOUT    INOUT     INOUT
         * 6  else               nothing
         * 7  ?        NOTHING   nothing
         * 8  NOTHING    ?       nothing
         */
        
        // assume case 6
        int mutualDirection = SharkCS.DIRECTION_NOTHING;
        int s, c;
        
        s = source.getDirection();
        c = context.getDirection();
        
        // exclude case 7 and 8
        if(s != SharkCS.DIRECTION_NOTHING 
                && c != SharkCS.DIRECTION_NOTHING) {
            
            // case 5
            if(s == SharkCS.DIRECTION_INOUT && c == SharkCS.DIRECTION_INOUT) {
                    mutualDirection = SharkCS.DIRECTION_INOUT;
            }
            
            // now, if both the same, nothing will happens
            else if(s == c) {
                mutualDirection = SharkCS.DIRECTION_NOTHING;
            }
            
            // case 1 and 2
            else if(c == SharkCS.DIRECTION_IN || c == SharkCS.DIRECTION_OUT) {
                mutualDirection = c;
            }
            
            // c == INOUT s is IN our OUT
            else {
                switch(s) {
                    case SharkCS.DIRECTION_IN : mutualDirection = SharkCS.DIRECTION_OUT; break;
                    case SharkCS.DIRECTION_OUT : mutualDirection = SharkCS.DIRECTION_IN; break;
                }
            }
        }
        
        // proceed or not?
        if(mutualDirection == SharkCS.DIRECTION_NOTHING) {
            // no mutual intterest at all
            return false;
        }
        
        ////////////////////////////////////////////////////////////
        //                      tags sets                         //  
        ////////////////////////////////////////////////////////////
        
        STSet mTopics;
        STSet mTypes;
        PeerSTSet mApprovers;
        PeerSTSet mSenders;
        PeerSTSet mReceivers;
        TimeSTSet mTimes;
        SpatialSTSet mLocations;
        
        /*
         * There are following interpretations:
         * A non existing dimension means anything and not nothing.
         * Thus, if no dimension is found either in source or context
         * it means that no constraints at all exists.
         * 
         * Producing an kepInterest means also:
         * We look at a source with a focus defined by the context.
         * Note: Context is meant to be declared locally. Source has been
         * retrieved from outside. Thus, we don't want to reveal things from
         * context but things that are already in the source and fit to context.
         *
         * In consequence, if context is ANY and source is a specific tag - use 
         * the source - it hides the local any tag
         * 
         * What happens if source is empty == null == any?
         * We interpret this situation as follows:
         * Contextualization is meant to narrow down an kepInterest. A contextualized
         * kepInterest shall be more specific than the source. We also assume that
         * the context can be revealed. Thus, if a source is not
         * specified - context is taken instead. 
         * Note: This is a harsh decision! An any source could triggers a sending
         * peer to reveal its interests. Application logic can (and should)
         * overwrite that beaviour.
         * 
         * Have this in mind when using this message in a knowledge port!
         * 
         * After checking this non / any context the actual contextualization
         * should take place
         */
        
        // topics
        CtxHelper h = new CtxHelper();
        h.contextualizeSingleDimension(source.getTopics(), context.getTopics(), 
                fpSet.getFP(ASIPSpace.DIM_TOPIC));

        if(!h.success) return false;
        mTopics = h.result;
        
        // types
        h = new CtxHelper();
        h.contextualizeSingleDimension(
                source.getTypes(), // source
                context.getTypes(), // context
                fpSet.getFP(ASIPSpace.DIM_TYPE) // fragmentation parameter
                );

        if(!h.success) return false;
        mTypes = h.result;
        
        // approvers
        h = new CtxHelper();
        h.contextualizeSingleDimension(
                source.getApprovers(), // source
                context.getApprovers(), // context
                fpSet.getFP(ASIPSpace.DIM_APPROVERS) // fragmentation parameter
                );

        if(!h.success) return false;
        mApprovers = (PeerSTSet) h.result;
        
        /* senders: twist: local senders = ctx(source.receivers, context.sender)
           receiver from senders perspective are sender from our local local
           point of view.
        */
        h = new CtxHelper();
        h.contextualizeSingleDimension(
                source.getReceivers(), // source
                context.getSenders(), // context
                fpSet.getFP(ASIPSpace.DIM_SENDER) // fragmentation parameter
                );

        if(!h.success) return false;
        mSenders = (PeerSTSet) h.result;

        // receivers: twist again, see comments above
        h = new CtxHelper();
        h.contextualizeSingleDimension(
                source.getSenders(), // source
                context.getReceivers(), // context
                fpSet.getFP(ASIPSpace.DIM_RECEIVER) // fragmentation parameter
                );

        if(!h.success) return false;
        mReceivers = (PeerSTSet) h.result;

        // times
        h = new CtxHelper();
        h.contextualizeSingleDimension(
                source.getTimes(), // source
                context.getTimes(), // context
                fpSet.getFP(ASIPSpace.DIM_TIME) // fragmentation parameter
                );

        if(!h.success) return false;
        mTimes = (TimeSTSet) h.result;
        
        // locations
        h = new CtxHelper();
        h.contextualizeSingleDimension(
                source.getLocations(), // source
                context.getLocations(), // context
                fpSet.getFP(ASIPSpace.DIM_LOCATION) // fragmentation parameter
                );
        
        if(!h.success) return false;
        mLocations = (SpatialSTSet) h.result;
        
        // if this point is reached - no contextualization failed.
        
        // lets fill mutual kepInterest
        mutualInterest.setTopics(mTopics);
        mutualInterest.setTypes(mTypes);
        mutualInterest.setApprovers(mApprovers);
        mutualInterest.setSenders(mSenders);
        mutualInterest.setReceivers(mReceivers);
        mutualInterest.setTimes(mTimes);
        mutualInterest.setLocations(mLocations);
        mutualInterest.setDirection(mutualDirection);
        
        return true;
    }
    
    /**
     * Check if context is completely inside target. Both, target and context
     * describe a sub space in the overall asip space. Overlapping regions
     * can be calculated with contextualization. This methods return only true
     * if context is inside target. It return false otherwise.
     * 
     * @param target
     * @param context
     * @return 
     */
    public static boolean isIn(ASIPInterest target, ASIPInterest context) throws SharkKBException {
        if(SharkCSAlgebra.isAny(target)) return true; // anything is in the universe
        
        if(SharkCSAlgebra.isAny(context)) return false; // context is everthing, target not..
        
        // both are not any / not null
        if(!SharkAlgebra.isIn(target.getTopics(), context.getTopics())) {
            return false;
        } else if(!SharkAlgebra.isIn(target.getTypes(), context.getTypes())) {
            return false;
        } else
        if(!SharkAlgebra.isIn(target.getApprovers(), context.getApprovers())) {
            return false;
        } else if(!SharkAlgebra.isIn(target.getSenders(), context.getSenders())) {
            return false;
        } else if(!SharkAlgebra.isIn(target.getReceivers(), context.getReceivers())) {
            return false;
        } else if(!SharkAlgebra.isIn(target.getTimes(), context.getTimes())) {
            return false;
        } else if(!SharkAlgebra.isIn(target.getLocations(), context.getLocations())) {
            return false;
        } 
        
        // direction
        int dTarget = target.getDirection();
        int dContext = context.getDirection();
        
        /*
         * NO / NO = true 
         * NO / * = false 
         * IN / IN = true 
         * IN / * = false 
         * OUT / OUT = true 
         * OUT / * = false 
         * INOUT / IN or OUT or INOUT = true 
         */
        
        // if target inout und context in/out/inout == !nothing -> ok
        if(
            (dTarget == ASIPSpace.DIRECTION_INOUT) && 
            (dContext == ASIPSpace.DIRECTION_NOTHING)
        ) return false;
        
        // else: context must have same direction as target
        if(dTarget != dContext) return false;
        
        return true;
    }
    
    public static boolean isIn(STSet target, STSet context) throws SharkKBException {
        if(SharkCSAlgebra.isAny(target)) return true; // anything is in the universe
        
        if(SharkCSAlgebra.isAny(context)) return false; // context is everthing, target not..
        Iterator<SemanticTag> stTags = context.stTags();
        
        if(stTags == null) return false;
        while(stTags.hasNext()) {
            SemanticTag tag = stTags.next();
            if(!SharkCSAlgebra.isIn(target, tag)) return false;
        }
        
        // no missing tag found in context - context is in target.
        return true;
    }

    public static boolean identical(ASIPSpace int1, ASIPSpace int2) throws SharkKBException {

        // both null means both any which is identical
        if(int1 == null && int2 == null) {
            return true;
        }

        if( (int1 == null && int2 != null) || (int1 != null && int2 == null)) {
            return false;
        }

        // we ignore semantics and make a direct test, e.g. IN != INOUT here
        if(int1.getDirection() != int2.getDirection()) {
            return false;
        }

        // topics identical ?
        if(!SharkCSAlgebra.identical(int1.getTopics(), int2.getTopics())) {
            return false;
        }

        // types identical ?
        if(!SharkCSAlgebra.identical(int1.getTypes(), int2.getTypes())) {
            return false;
        }

        // sender identical ?
        if(!SharkCSAlgebra.identical(int1.getSender(), int2.getSender())) {
            return false;
        }

        // approvers identical ?
        if(!SharkCSAlgebra.identical(int1.getApprovers(), int2.getApprovers())) {
            return false;
        }

        // receivers identical ?
        if(!SharkCSAlgebra.identical(int1.getReceivers(), int2.getReceivers())) {
            return false;
        }

        // locations identical ?
        if(!SharkCSAlgebra.identical(int1.getLocations(), int2.getLocations())) {
            return false;
        }

        // times identical ?
        if(!SharkCSAlgebra.identical(int1.getTimes(), int2.getTimes())) {
            return false;
        }

        // anythings got a match
        return true;
    }

    public static void mergeInformation(SharkKB target, ASIPInformation info) throws SharkKBException {
        ASIPInformation newInfo = target.addInformation(info.getContentAsByte(), info.getASIPSpace());

        // copy properties
        Util.copyPropertiesFromPropertyHolderToPropertyHolder(info, newInfo);
    }

    public static void mergeInformations(SharkKB target, Iterator<ASIPInformation> cInfoIter) throws SharkKBException {
        if(target == null || cInfoIter == null) return;

        while(cInfoIter.hasNext()) {
            ASIPInformation cInfo = cInfoIter.next();
            SharkAlgebra.mergeInformation(target, cInfo);
        }
    }
}
