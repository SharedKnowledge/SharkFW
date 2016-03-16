package net.sharkfw.knowledgeBase;

import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.sharkfw.knowledgeBase.inmemory.InMemoSharkKB;
import net.sharkfw.system.Iterator2Enumeration;
import net.sharkfw.system.L;
import net.sharkfw.system.Util;

/**
 * This class implements basic methods that makes up the algebra
 * of the context space theory. 
 * 
 * @author thsc
 */
public abstract class SharkCSAlgebra {
    private static net.sharkfw.knowledgeBase.geom.SpatialAlgebra spatialAlgebra;
    private static final String JTS_SPATIAL_ALGEBRA_CLASS = 
            "net.sharkfw.knowledgeBase.geom.jts.SpatialAlgebra";
    
    static {
        
        boolean done = false;
        
        try {
            Class spatialAlgebraClass = Class.forName(JTS_SPATIAL_ALGEBRA_CLASS);
            Object newInstance = spatialAlgebraClass.newInstance();
            
            SharkCSAlgebra.spatialAlgebra = (net.sharkfw.knowledgeBase.geom.SpatialAlgebra) newInstance;
            
            L.d("JTS Algebra instanciated");
            done = true;
                    
        } catch (ClassNotFoundException ex) {
            L.d("no JTS Spatial Algebra found - take default: " + ex.getMessage());
        } catch (InstantiationException | IllegalAccessException ex) {
            L.d("couldn't instantiate JTS Spatial Algebra - take default: " + ex.getMessage());
        } catch (ClassCastException ex) {
            L.d("weired: JTS Spatial Algebra found and instanziated but object isn't of type SpatialAlgebra - take default: " + ex.getMessage());
        }
        
        if(!done) {
            SharkCSAlgebra.spatialAlgebra = new net.sharkfw.knowledgeBase.geom.SpatialAlgebra();
            L.d("Default Spatial Algebra instantiated");
        }
    }

    public static net.sharkfw.knowledgeBase.geom.SpatialAlgebra getSpatialAlgebra() {
        return SharkCSAlgebra.spatialAlgebra;
    }
    
    
    /**
     * Determine whether or not <code>tagA</code> and <code>tagB</code> are
     * semantically identical. Is is checked by their subject identifier. At least 
     * one much make both identically.
     * @param tagA
     * @param tagB
     * @return 
     */
    public static boolean identical(SemanticTag tagA, SemanticTag tagB) { 
        // same object ?
        if(tagA == tagB) return true;
        
        if(SharkCSAlgebra.isAny(tagA) || SharkCSAlgebra.isAny(tagB)) return true;

        // Geometries super overrule sis
        if(tagA instanceof SpatialSemanticTag && tagB instanceof SpatialSemanticTag) {
            SpatialSemanticTag sTagA = (SpatialSemanticTag)tagA;
            SpatialSemanticTag sTagB = (SpatialSemanticTag)tagB;
            
            try {
                boolean identical = SharkCSAlgebra.spatialAlgebra.identical(sTagA, sTagB);
                return identical;
            }
            catch(SharkKBException e) {
                // weired
                L.w("exception while calculating whether two spatial tags are identical: " + e.getMessage());
                return false;
            }
            
        }
        
        String si_a[] = tagA.getSI();
        String si_b[] = tagB.getSI();
        
        boolean sisIdentical = SharkCSAlgebra.identical(si_a, si_b);
        
        if(!sisIdentical) return false;
        
        return sisIdentical;
    }

    /**
     * Checks whether two semantic tags are identical based on their subject
     * identifiers. Null is interpreted as any.
     * @param sisA
     * @param sisB
     * @return 
     */
    public static boolean identical(String[] sisA, String[] sisB) { 
        if (sisA == null || sisB == null) {
            return true;
        }
        
        // both si sets not null

        for (int a = 0; a < sisA.length; a++) {
            for (int b = 0; b < sisB.length; b++) {
                if (SharkCSAlgebra.identical(sisA[a], sisB[b])) {
                    return true;
                }
            }
        }

        return false;    
    }    
    
    /**
     * 
     * @param siA
     * @param siB
     * @return 
     */
    public static boolean identical(String siA, String siB) { 
        return siA.equalsIgnoreCase(siB);
    }
    

    /**
     * Merge two semantic tags. It's pretty simple:
     * 
     * Target name remains unchanged if there is a name. Otherwise target gets
     * the name of the tag to merge in.
     * 
     * Properties are merged as well.
     * 
     * All subject identifieres of <code>toMerge</code> are added to 
     * <code>target</code> without duplicates.
     * 
     * Target will be changed, toMerge remains unchanged.
     * 
     * @param target
     * @param toMerge 
     * @return target is returned
     */
    public static SemanticTag merge(SemanticTag target, SemanticTag toMerge) {
        String targetName = target.getName();
        if(targetName == null || targetName.length() == 0) {
            String name = new String(toMerge.getName());
            target.setName(name);
        }
        
        boolean checkAnySI = false;
        // iterate SIs now
        String[] toMergeSIS = toMerge.getSI();
        for(int iM = 0; iM < toMergeSIS.length; iM++) {
            String[] targetSIS = target.getSI();
            boolean found = false;
            int iT = 0;
            
            while(!found && iT < targetSIS.length) {
                if(SharkCSAlgebra.identical(targetSIS[iT++], toMergeSIS[iM])) {
                    
                    // identical SI already exists
                    found = true;
                }
            }
            
            // we haven't found toMerge Si in target - add it.
            if(!found) {
                String si2add = new String(toMergeSIS[iM]);
                
                /* if we add at least a single si that isn't any.
                requires a test later
                */
                try {
                    target.addSI(si2add);
                    if(!SharkCSAlgebra.isAny(new String[]{si2add})) {
                        checkAnySI = true;
                    }
                }
                catch(SharkKBException e) {
                    // si seems alread to exist
                }
            }
        } // next si from tag to merge into target.
        
        /* maybe target contained an ANY SI
        If we have added a non-any-SI that any Si must be removed
        */
        if(checkAnySI) {
            if(target.isAny()) {
                try {
                    target.removeSI(SharkCS.ANYURL);
                } catch (SharkKBException ex) {
                    // shouldn't happen here
                }
            }
        }
        
        // copy hidden
        target.setHidden(toMerge.hidden());
        
        // finally merge properties
        Util.mergeProperties(target, toMerge);
        
        return target;
    }
    
    public static PeerSemanticTag merge(PeerSemanticTag target, PeerSemanticTag toMerge) {    
        SharkCSAlgebra.merge((SemanticTag) target, (SemanticTag) toMerge);
        
        target.setAddresses(Util.mergeArrays(target.getAddresses(), toMerge.getAddresses()));
        
        return target;
    }
/*************************************************************************
 *                          Tag Set Fragmentation                        * 
 *************************************************************************/    

    /**
     * 
     * Implementation of ST set fragmentation. This implementation
     * is independent from actual implementing classes. Advantage: Any
     * ST set implementation can use it. Disadvantage: It's probably the
     * less efficient implementation.
     * 
     * @param fragment Must be an empty SemanticNet.
     * The fragment will be produced into this fresh semantic net.
     * This implementation doesn't care neither of implementing class (how 
     * could it?) nor its status. It should be an empty semantic net.
     * @param anchorEnum Enumeration of anchor
     * @param allowedPredicates Enumeration of allowed predicate types
     * @param forbiddenPredicates Enumeration of forbidden predicate types. Note: 
     * Forbidden types rule out allowed types. Meaning: If type X is allowed and
     * forbidden as well -> X is forbidden.
     * @param depth range (depth) to search in the network
     * parameter allows to define whether hidden properties should be copied into
     * the fragment or not.
     * @return The fragment is returned. Actually, it is the object that was
     * provided by methode callee as fragment parameter.
     * @throws SharkKBException 
     */
    public static SemanticNet fragment(SemanticNet fragment, 
            SemanticTag anchor, SemanticNet source,
            Enumeration<String> allowedPredicates, 
            Enumeration<String> forbiddenPredicates, int depth) 
                                            throws SharkKBException 
    {
        if (depth < 0) {
            depth = 0;
        }

        // read Enumerations and keep it
        HashSet<String> allowedTypes = null, forbiddenTypes = null;
        
        // store allowed types
        if(allowedPredicates != null) {
            allowedTypes = new HashSet<String>();
            while(allowedPredicates.hasMoreElements()) {
                allowedTypes.add(allowedPredicates.nextElement());
            }
            
            // have we got anything?
            if(allowedTypes.isEmpty()) {
                // no - drop it - easier in following if - statements
                allowedTypes = null;
            }
        }
        
        if(forbiddenPredicates != null) {
            forbiddenTypes = new HashSet<String>();
            while(forbiddenPredicates.hasMoreElements()) {
                String forbiddenTypeString = forbiddenPredicates.nextElement();
                
                // check whether it is also allowed and remove it in this case
                if(allowedTypes != null) {
                    allowedTypes.remove(forbiddenTypeString);
                }
                
                forbiddenTypes.add(forbiddenTypeString);
            }
            
            // have we got anything?
            if(forbiddenTypes.isEmpty()) {
                // no - drop it - easier in following if - statements
                forbiddenTypes = null;
            }
            
            // allowed types can have become empty - check again
            if(allowedTypes != null && allowedTypes.isEmpty()) {
                allowedTypes = null;
            }
        }

        /* find that anchor in source to get a hand on the actual tag 
         * and not a copy or something als
         */
        SNSemanticTag anchorSource = source.getSemanticTag(anchor.getSI());
        if(anchorSource == null) {
            // not here? We are done
            return fragment;
        }
        
        // it is there - lets go
        SharkCSAlgebra.fragment(fragment, anchorSource, source, 
                allowedTypes, forbiddenTypes, depth, true);
        
        /* we have now a kind of star with anchor in the centre.
         * Each predicate starting from anchor contains any 
         * predicates. This isn't the case for all other tags and
         * their relations. Now, all allowed relations between all
         * tags in the fragment has to be copied. 
         */
        
        // iterate concepts in fragment
        Enumeration<SemanticTag> fTagEnum = fragment.tags();
        while(fTagEnum.hasMoreElements()) {
            SemanticTag st = fTagEnum.nextElement();
            
            // anchor predicates are already copied
            if(SharkCSAlgebra.identical(anchor, st)) continue;
            
            // it must be there - it was extracted several milliseconds before
            SNSemanticTag sourceTag = source.getSemanticTag(st.getSI());

            // just add the references, perform no merge - its already done.
            SharkCSAlgebra.fragment(fragment, sourceTag, source, 
                    allowedTypes, forbiddenTypes, 
                    1, /* just predicates from this tag to others */
                    false /* dont merge. It already done */
                    );
        }
        
        return fragment;
    }
    
    public static SemanticNet fragment(SemanticNet fragment, 
            SemanticTag anchor, SemanticNet source,
            Iterator<String> allowedPredicates, 
            Iterator<String> forbiddenPredicates, int depth) 
                                            throws SharkKBException {
        
        return SharkCSAlgebra.fragment(fragment, anchor, source, 
                new Iterator2Enumeration(allowedPredicates), 
                new Iterator2Enumeration(forbiddenPredicates), 
                depth);
        
    }
    
    public static SemanticNet contextualize(SemanticNet fragment, 
            SemanticNet source, STSet context,
            FragmentationParameter fp) throws SharkKBException {
        
        Enumeration<SemanticTag> tagEnum = context.tags();
        Enumeration<String> allowed = fp.getAllowedPredicates();
        Enumeration<String> forbidden = fp.getForbiddenPredicates();
        
        return SharkCSAlgebra.contextualize(fragment, tagEnum, source, allowed, forbidden, fp.getDepth());
    }

    public static SemanticNet contextualize(SemanticNet source, STSet context,
            FragmentationParameter fp) throws SharkKBException {
        
        SemanticNet fragment = InMemoSharkKB.createInMemoSemanticNet();
        
        return SharkCSAlgebra.contextualize(fragment, source, context, fp);
    }
        
    public static SemanticTag merge(STSet target, SemanticTag source) throws SharkKBException {
        SemanticTag copy = target.getSemanticTag(source.getSI());
        
        if(copy == null) {
            copy = target.createSemanticTag(source.getName(), source.getSI());
            Util.mergeProperties(copy, source);
        } 
//        else {
//            SharkCSAlgebra.merge(target, source);
//        }
        
        return copy;
    }
    
    public static boolean fragment(SemanticNet fragment,
            SemanticNet source,
            SNSemanticTag sourceTag,
            int depth) throws SharkKBException {
        
        return (SharkCSAlgebra.fragment(fragment, sourceTag, source,
                (HashSet<String>)null, (HashSet<String>)null, depth, true) != null);
    }
    
    public static SemanticNet fragment(
            SNSemanticTag sourceTag,
            SemanticNet source,
            int depth) throws SharkKBException {
        
        SemanticNet fragment = InMemoSharkKB.createInMemoSemanticNet();
        
        return SharkCSAlgebra.fragment(fragment, sourceTag, source,
                (HashSet<String>)null, (HashSet<String>)null, depth, true);
    }
    
    private static SemanticNet fragment(SemanticNet fragment, 
            SNSemanticTag sourceTag,
            SemanticNet source,
            HashSet<String> allowedTypes, 
            HashSet<String> forbiddenTypes, 
            int depth,
            boolean merge) throws SharkKBException 
    {
        
        SNSemanticTag fragmentTag = null;
        
        if(merge) {
            // first of all - copy the source anchor to fragment        
            fragmentTag = fragment.merge(sourceTag);
        } 
        // go into the network if allowed...
        
        if(depth < 1) return fragment;
        
        if(!merge && sourceTag != null) { // must already be there
            fragmentTag = fragment.getSemanticTag(sourceTag.getSI());
        }
        
        if(fragmentTag == null) return fragment;
        
        /* 
         * It is allowed to follow a predicate if 
         * it is allowed AND not forbidden 
         * 
         * Interpretations:
         * allowedPredicates empty - everthing allowed
         * forbiddenPredicates empty - nothing forbidden
         * 
         * There are theses cases:
         * 
         *         allowed | forbidden | result
         * 1.      null    | null      | any
         * 2.      null    | x         | any except x
         * 3.      x       | null      | x
         * 4.      x       | y         | if in(x) and !in(y)
         * 
         */
        
        // find predicates in source
        Enumeration<String> predicateEnum = sourceTag.predicateNames();
        if(predicateEnum == null) {
            // ready - not related tags
            return fragment;
        }
        
        while(predicateEnum.hasMoreElements()) {
            String predicateString = predicateEnum.nextElement();
            
            // allowed ?
            boolean follow = false;
            
            // case 1
            if(allowedTypes == null && forbiddenTypes == null) {
                follow = true;
            } 
            
            // case 2
            else if(allowedTypes == null && forbiddenTypes != null) {
                if(!forbiddenTypes.contains(predicateString)) {
                    follow = true;
                }
            }
            
            // case 3
            else if(allowedTypes != null && forbiddenTypes == null) {
                if(allowedTypes.contains(predicateString)) {
                    follow = true;
                }
            }
            
            // case 4
            else {
                if(allowedTypes.contains(predicateString) 
                        && !forbiddenTypes.contains(predicateString)) {
                    follow = true;
                }
            }
            
            if(follow) {
                Enumeration<SNSemanticTag> tagEnum = 
                        sourceTag.targetTags(predicateString);
                
                if(tagEnum != null) {
                    while(tagEnum.hasMoreElements()) {
                        SNSemanticTag referencedTag = tagEnum.nextElement();
                        if(referencedTag == null) {
                            continue;
                        }
                        
                        SharkCSAlgebra.fragment(fragment, referencedTag, source, 
                                allowedTypes, forbiddenTypes, depth-1, merge);
                        
                        // find copy of referenceTag
                        SNSemanticTag copyReferencedTag = 
                                fragment.getSemanticTag(referencedTag.getSI());
                        
                        // can be null if referenced tag is not part of the fragment
                        if(copyReferencedTag != null) {
                            // it was now copied - set predicates also in fragment
                            fragmentTag.setPredicate(predicateString, copyReferencedTag);
                        }
                    }
                }
            }
        }
        
        return fragment;
    }
    
    public static SemanticNet contextualize(SemanticNet fragment, 
            Enumeration<SemanticTag> contextTagEnum, 
            SemanticNet source, 
            Enumeration<String> allowedPredicates, 
            Enumeration<String> forbiddenPredicates, int depth) 
                throws SharkKBException {
        
        if(contextTagEnum == null) {
            /* Context is any
                * The context is assumed to be the thing that has to be kept
                * secret. So, what do we do with an empty context. We leave it 
                * to the fragmentation parameter. If depth is not null, we 
                * enumerate all concepts from source.
                */
            if(depth <= 0) {
                // context is null
                return null;
            } else {

            // there is a depth wished. Start with any tag in source.
            contextTagEnum = source.tags();
            }
        }
        
        if(contextTagEnum == null) {
            return fragment;
        }

        /*
        We need list of allowed and forbidden predicates more than one.
        We have got an enumeration - we must keep it.
        */
        
        Vector<String> allowed = new Vector();
        if(allowedPredicates != null) {
            while(allowedPredicates.hasMoreElements()) {
                allowed.add(allowedPredicates.nextElement());
            }
        }
        
        Vector<String> forbidden = new Vector();
        if(forbiddenPredicates != null) {
            while(forbiddenPredicates.hasMoreElements()) {
                forbidden.add(forbiddenPredicates.nextElement());
            }
        }
        
        while(contextTagEnum.hasMoreElements()) {
            SemanticTag anchor = contextTagEnum.nextElement();
            
            SharkCSAlgebra.fragment(fragment, anchor, source, allowed.elements(), 
                    forbidden.elements(), depth);
        }
        
        return fragment;
    }
    
    public static SemanticNet contextualize(SemanticNet fragment, 
            Iterator<SemanticTag> anchorSet, 
            SemanticNet source, 
            Enumeration<String> allowedPredicates, 
            Enumeration<String> forbiddenPredicates, int depth) 
                throws SharkKBException {
        
        return SharkCSAlgebra.contextualize(fragment, 
                new Iterator2Enumeration(anchorSet), 
                source, allowedPredicates, forbiddenPredicates, depth);
    }
    /**
     * merges source into target. Tags and their properties are copied
     * as well as their predicates. Merging is performed if required.
     * @param target
     * @param source 
     * @throws net.sharkfw.knowledgeBase.SharkKBException 
     */
    public static void merge(SemanticNet target, SemanticNet source) 
            throws SharkKBException {

        /* Could be made much better. Very time consuming because
         * each tag is tried to be merged in at least twice.
         * But it was so easy to implement - refine it later.
         */
        SharkCSAlgebra.contextualize(target, source.tags(), 
                source, null, null, 1);
    }
    
    /**
     * Merges a context space into an existing (empty or not) kb.
     * @param target
     * @param source
     * @throws SharkKBException 
     */
    public static void merge(SharkKB target, SharkCS source) 
            throws SharkKBException {
        
        target.getTopicsAsSemanticNet().merge(source.getTopics());
        target.getPeersAsTaxonomy().merge(source.getPeers());
        target.getPeersAsTaxonomy().merge(source.getRemotePeers());
        target.getPeersAsTaxonomy().merge(source.getOriginator());
        target.getSpatialSTSet().merge(source.getLocations());
        target.getTimeSTSet().merge(source.getTimes());
    }
    
    ///////////////////////////////////////////////////////////////////////
    //                   contextualization / fragmentation               //
    ///////////////////////////////////////////////////////////////////////
    
    public static STSet contextualize(STSet fragment, STSet source,
            Enumeration<SemanticTag> anchorSet) throws SharkKBException {
        
        if(fragment == null) {
            throw new SharkKBException("fragment must be initialized, null not permitted in SharkCS.contextualize()");
        }
        
        if(anchorSet == null || !anchorSet.hasMoreElements()) {
            return null;
        }
        
        while(anchorSet.hasMoreElements()) {
            SemanticTag tag = anchorSet.nextElement();
            
            STSet s = source.fragment(tag);
            if(s != null) {
                fragment.merge(s);
            }
        }
        
        return fragment;
    }
    
    public static STSet fragment(STSet fragment, STSet source, SemanticTag anchor) 
            throws SharkKBException {
        
        String sis[] = anchor.getSI();

        SemanticTag fittingTag = source.getSemanticTag(sis);
        
        if(fittingTag != null) {
            fragment.merge(fittingTag);
            return fragment;
        } 
        
        return null;
    }
    
    public static Interest contextualize(SharkCS source, SharkCS context, 
            FragmentationParameter[] fp) throws SharkKBException {
        
        Interest interest = InMemoSharkKB.createInMemoInterest();

        if(SharkCSAlgebra.contextualize(interest, source, context, fp)) {
            return interest;
        }
        
        return null;
    }
    
    
    /**
     * Implementation of interest contextualization. Result is written into
     * mutualInterest. MutualInterest will be empty if no mutual interest
     * at all could be calculated. Even non empty intersection of dimensions
     * will be deleted.
     * 
     * This method also twists peer and remote peer during calculation.
     * What? Let's explain: The source CS is meant to be sent by a remote
     * peer, e.g. Bob. He would write Alice into his 'remote peer'
     * dimension if he want's to talk with Alice.
     * 
     * The context is meant to be the cs defined locally on that engine.
     * Alice would put herself (or any) to the 'peer' dimension to denote
     * under which identity she likes to act.
     * 
     * Apparently, the Bobs' remote peer must be checked with Alice' peer
     * dimension to calculate the mutual interest. It is like an inertial frame 
     * by Einstein. Bob has his view, Alice hers.
     * 
     * This methode assumes to be in the inertial frame of context.
     * The mutualInterest' peer dimension is created by 
     * contextualize(source.remotePeer, context.peer);
     * 
     * mutualInterest.peer = contextualize(source.peer,context.remotePeer);
     * 
     * We have a related consideration in direction. Contextualization shall
     * lead to an interest that contains tags which are in the source and which
     * fit to the context without revealing additional information from context.
     * The result shall be sent back to sender.
     * 
     * Thus, the resulting direction must match with context. 
     * 
     * 
     * Note: Originator tag is just taken as reference. No copy is made.
     * 
     * @param mutualInterest Contains result if there is any
     * @param source 
     * @param context
     * @param fp
     * @return true if mutualInterest is not empty, false otherwise
     * @throws net.sharkfw.knowledgeBase.SharkKBException
     */
    public static boolean contextualize(Interest mutualInterest, 
            SharkCS source, SharkCS context, FragmentationParameter[] fp) 
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
        //                      originator                        //  
        ////////////////////////////////////////////////////////////
        PeerSemanticTag mutualOriginator;
        
        // context is any - take source. Maybe it is more specific
        if(SharkCSAlgebra.isAny(context.getOriginator())) {
            mutualOriginator = source.getOriginator();
            
        // source is any - take context. Algebra shall narrow down interest - make them more specific    
        } else if (SharkCSAlgebra.isAny(source.getOriginator())) {
            mutualOriginator = context.getOriginator();
        
        // both not any - means also - both not null    
        } else if(source.getOriginator().identical(context.getOriginator())) {
            mutualOriginator = source.getOriginator();
        } else {
            // no match
            return false;
        }
        
        
        ////////////////////////////////////////////////////////////
        //                      tags sets                         //  
        ////////////////////////////////////////////////////////////
        
        STSet mTopics;
        PeerSTSet mPeers;
        PeerSTSet mRemotePeers;
        TimeSTSet mTimes;
        SpatialSTSet mLocations;
        
        /*
         * There are following interpretations:
         * A non existing dimension means anything and not nothing.
         * Thus, if no dimension is found either in source or context
         * it means that no constraints at all exists.
         * 
         * Producing an interest means also:
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
         * Contextualization is meant to narrow down an interest. A contextualized
         * interest shall be more specific than the source. We also assume that
         * the context can be revealed. Note: This is a harsh decision! Thus,
         * if a source is not specified - context is taken instead. 
         * 
         * Have this in mind when using this message in a knowledge port!
         * 
         * After checking this non / any context the actual contextualization
         * should take place
         */
        
        // prevent nullpointer exceptions
        for(int i = 0; i < SharkCS.MAXDIMENSIONS; i++) {
            if(fp[i] == null) {
                fp[i] = FragmentationParameter.getZeroFP();
            }
        }

        // topics
        
        // test on any - if either of one is any - result is any, see above
        if(SharkCSAlgebra.isAny(source.getTopics())) {
            mTopics = context.getTopics(); // narrow down interests
        } else {
            if(SharkCSAlgebra.isAny(context.getTopics())) {
                if(fp[SharkCS.DIM_TOPIC].getDepth() > 0) {
                    mTopics = InMemoSharkKB.createInMemoCopy(source.getTopics());
                } else {
                    mTopics = InMemoSharkKB.createInMemoCopy(source.getTopics());
                }
            } else {
                // neither source or context is any at this point
                mTopics = source.getTopics().contextualize(
                            context.getTopics(), fp[SharkCS.DIM_TOPIC]);
                if(mTopics == null || mTopics.isEmpty()) {
                    return false;
                }
//                if(mTopics.isEmpty()) {
//                    mTopics = null;
//                } // any
            }
        }
        
        // peers
        // mutualInterest.peer = contextualize(source.remotePeer,context.peer);
        
        // test on any - if either of one is any - result is any, see above
        if(SharkCSAlgebra.isAny(source.getRemotePeers())) {
            mPeers = context.getPeers(); // narrow down interests
        } else {
            if(SharkCSAlgebra.isAny(context.getPeers())) {
                // source is copied if depth > 0
                if(fp[SharkCS.DIM_PEER].getDepth() > 0) {
                    mPeers = InMemoSharkKB.createInMemoCopy(source.getRemotePeers());
                } else {
                    mPeers = InMemoSharkKB.createInMemoCopy(source.getRemotePeers()); // ??? right ??
                }
            } else { // context.remotePeer != null
                // neither source or context is any at this point
                mPeers = source.getRemotePeers().contextualize(
                    context.getPeers(), fp[SharkCS.DIM_PEER]);
                
                if(mPeers == null) { // || mPeers.isEmpty()) { TODO!!
                    return false;
                }
                if(mPeers.isEmpty()) mPeers = null; // any
                
            }
        }
        
        // remote peers
        // mutualInterest.remotePeer = contextualize(source.peer,context.remotePeer);
        // test on any - if either of one is any - result is any, see above
        if(SharkCSAlgebra.isAny(source.getPeers())) {
            mRemotePeers = context.getRemotePeers(); // narrow down interests
        } else {
            if(SharkCSAlgebra.isAny(context.getRemotePeers())) {
                // source is copied if depth > 0
                if(fp[SharkCS.DIM_REMOTEPEER].getDepth() > 0) {
                    mRemotePeers = InMemoSharkKB.createInMemoCopy(source.getPeers());
                } else {
                    mRemotePeers = InMemoSharkKB.createInMemoCopy(source.getPeers());
                }
            } else { // context.peer != null
                mRemotePeers = source.getPeers().contextualize(
                    context.getRemotePeers(), fp[SharkCS.DIM_REMOTEPEER]);
                
                if(mRemotePeers == null) return false;
                if(mRemotePeers.isEmpty()) mRemotePeers = null; // any
            }
        }
        
        // times

        // test on any - if either of one is any - result is any, see above
        if(SharkCSAlgebra.isAny(source.getTimes())) {
            mTimes = context.getTimes(); // narrow down interests
        } else {
            if(SharkCSAlgebra.isAny(context.getTimes())) {
                if(fp[SharkCS.DIM_TIME].getDepth() > 0) {
                    mTimes = InMemoSharkKB.createInMemoCopy(source.getTimes());
                } else {
                    mTimes = InMemoSharkKB.createInMemoCopy(source.getTimes());
                }
                
            } else {
                mTimes = source.getTimes().contextualize(
                        context.getTimes(), fp[SharkCS.DIM_TIME]);
        
                if(mTimes == null) return false;
                if(mTimes.isEmpty()) mTimes = null; // any
            }
        }
        
        // locations

        // test on any - if either of one is any - result is any, see above
        if(SharkCSAlgebra.isAny(source.getLocations())) {
            mLocations = context.getLocations(); // narrow down interests
        } else {
            if(SharkCSAlgebra.isAny(context.getLocations())) {
                if(fp[SharkCS.DIM_LOCATION].getDepth() > 0) {
                    mLocations = InMemoSharkKB.createInMemoCopy(source.getLocations());
                } else {
                    mLocations = InMemoSharkKB.createInMemoCopy(source.getLocations());
                }
            } else {
                mLocations = source.getLocations().contextualize(
                        context.getLocations(), fp[SharkCS.DIM_LOCATION]);

                if(mLocations == null) return false;
                if(mLocations.isEmpty()) mLocations = null; // any
            }
        }
        
        // if this point is reached - no contextualization failed.
        
        // construct mutual interest
        mutualInterest.setDirection(mutualDirection);
        mutualInterest.setOriginator(mutualOriginator);
        mutualInterest.setTopics(mTopics);
        mutualInterest.setPeers(mPeers);
        mutualInterest.setRemotePeers(mRemotePeers);
        mutualInterest.setTimes(mTimes);
        mutualInterest.setLocations(mLocations);
        
        return true;
    }

    /**
     * Check whether a dimension of a SharkCS is any.
     * @param source
     * @param dim
     * @return 
     */
    public static boolean isAny(SharkCS source, int dim) {
        STSet dimension;
        
        switch(dim) {
            case SharkCS.DIM_TOPIC : 
                return SharkCSAlgebra.isAny(source.getTopics());
                
            case SharkCS.DIM_ORIGINATOR : 
                SemanticTag originator = source.getOriginator();
                return (originator == null || originator.isAny());
                
            case SharkCS.DIM_PEER : 
                return SharkCSAlgebra.isAny(source.getPeers());
                
            case SharkCS.DIM_REMOTEPEER : 
                return SharkCSAlgebra.isAny(source.getRemotePeers());
                
            case SharkCS.DIM_TIME :
                return SharkCSAlgebra.isAny(source.getTimes());
                
            case SharkCS.DIM_LOCATION : 
                return SharkCSAlgebra.isAny(source.getLocations());
                
            case SharkCS.DIM_DIRECTION : 
                return (source.getDirection() == SharkCS.DIRECTION_INOUT);
        }
        
        return false;
    }
    /**
     * Check whether a SharkCS is any (all dimensions any).
     * @param source
     * @return 
     */
    public static boolean isAny(SharkCS source) {
        return (source.isAny(SharkCS.DIM_TOPIC)
            && source.isAny(SharkCS.DIM_ORIGINATOR)
            && source.isAny(SharkCS.DIM_PEER)
            && source.isAny(SharkCS.DIM_REMOTEPEER)
            && source.isAny(SharkCS.DIM_TIME)
            && source.isAny(SharkCS.DIM_LOCATION)
            && source.isAny(SharkCS.DIM_DIRECTION)
        );
    }
    
    public static boolean isAny(STSet set) {
        if (set == null || set.isEmpty()) return true;
        
        try {
            // iterate and look for any tag
            Iterator<SemanticTag> stTags = set.stTags();
            while(stTags != null && stTags.hasNext()) {
                SemanticTag tag = stTags.next();
                if(tag.isAny()) {
                    return true;
                }
            }
            
        } catch (SharkKBException ex) {
            L.e("cannot iterate set"); // TODO
        }
        return false;
    }
    
    public static boolean isAny(SemanticTag tag) {
        if(tag == null) return true;
        
        String[] si = tag.getSI();
        
        if(si == null || si.length == 0) return true;
        
        return SharkCSAlgebra.isAny(si);
    }
    
    public static boolean isAny(String[] sis) {
        if(sis == null || sis.length == 0) return true;
        
        for(int i = 0; i < sis.length; i++) {
            if(sis[i] == null) {
                return true;
            }
        }
        
        return SharkCSAlgebra.identical(sis, SharkCS.ANYSI);
    }
    
    
    
    /**
     * Tests whether both coordinates are completely identical. Each dimension
     * must directly hit - no relations are follows etc. Note, even 
     * e.g. direction inout != direction out
     * 
     * @param cc1
     * @param cc2
     * @return 
     */
    public static boolean identical(ContextCoordinates cc1, ContextCoordinates cc2) {
        // if references are the same they are identical
        if(cc1 == cc2) {
            return true;
        }
        
        if( (cc1 != null && cc2 == null) || (cc1 == null && cc2 != null) ) {
            return false; // it a guess but a good one. The non empty cc could describe anytag in any dimension - should be tested..
        }
        
        // direction
        if(cc1.getDirection() == cc2.getDirection() || 
          (cc1.getDirection() == SharkCS.DIRECTION_INOUT && cc2.getDirection() != SharkCS.DIRECTION_NOTHING) ||
          (cc1.getDirection() != SharkCS.DIRECTION_NOTHING && cc2.getDirection() == SharkCS.DIRECTION_INOUT)) {
            
            // originator
            if(SharkCSAlgebra.identical(cc1.getOriginator(),cc2.getOriginator())) {
                
                // topic
                if(SharkCSAlgebra.identical(cc1.getTopic(),cc2.getTopic())) {
                    
                    // peer
                    if(SharkCSAlgebra.identical(cc1.getPeer(),cc2.getPeer())) {
                        
                        // remote peer
                        if(SharkCSAlgebra.identical(cc1.getRemotePeer(),cc2.getRemotePeer())) {
                            
                            // location
                            if(SharkCSAlgebra.identical(cc1.getLocation(),cc2.getLocation())) {
                                
                                // time
                                if(SharkCSAlgebra.identical(cc1.getTime(),cc2.getTime())) {
                                    return true;
                                }
                            }
                        }
                    }
                }
            }
        } 
        
        return false;
    }
    
    /**
     * Two tag sets are identical if each tag in set 1 has an identical
     * tag in set 2 and vice versa.
     * 
     * @param set1
     * @param set2
     * @return 
     * @throws net.sharkfw.knowledgeBase.SharkKBException 
     */
    public static boolean identical(STSet set1, STSet set2) throws SharkKBException {
        if( (set1 == null && set2 == null)
                || 
            (set1 == null && set2 != null && set2.isEmpty())
                || 
            (set2 == null && set1 != null && set1.isEmpty())
            )
        {
            return true;
        }
        
        
        
        if((set1 == null && set2 != null) || (set2 == null && set1 != null)) {
            return false;
        }
        
        // both are not null
        Enumeration<SemanticTag> tags2 = null;
        
        // try to find a tag in set 1 that has no identical tag in set 2
        Enumeration<SemanticTag> tags1 = set1.tags();
        if(tags1 != null) {
            while(tags1.hasMoreElements()) {
                SemanticTag tag1 = tags1.nextElement();
                SemanticTag tag2 = set2.getSemanticTag(tag1.getSI());
                
                if(tag2 == null) {
                    return false;
                }
            }
        } else {
            tags2 = set2.tags();
            if(tags2 == null) {
                return true; // both sets have no tags
            } else {
                return false; // set 1 empty, set 2 not
            }
        }
        
        tags2 = set2.tags();
        if(tags2 == null) {
            /* actually, this shouldn't happen - if 1 is not empty
             * but 2 is the first test in upper while-loop should fail.
             * Anyway:
             */
            return false; // set 1 wasn't empty
        }
        
        while(tags2.hasMoreElements()) {
            SemanticTag tag2 = tags2.nextElement();
            SemanticTag tag1 = set1.getSemanticTag(tag2.getSI());
            
            if(tag1 == null) {
                return false;
            }
        }

        // found all tags in both sets
        
        return true;
    }
    
    public static boolean identical(SharkCS cs1, SharkCS cs2) 
            throws SharkKBException {
        
        // both null means both any which is identical
        if(cs1 == null && cs2 == null) {
            return true;
        }
        
        if( (cs1 == null && cs2 != null) || (cs1 != null && cs2 == null)) {
            return false;
        }
        
        // we ignore semantics and make a direct test, e.g. IN != INOUT here
        if(cs1.getDirection() != cs2.getDirection()) {
            return false;
        }
        
        // topics identical ?
        if(!SharkCSAlgebra.identical(cs1.getTopics(), cs2.getTopics())) {
            return false;
        }
        
        // peers identical ?
        if(!SharkCSAlgebra.identical(cs1.getPeers(), cs2.getPeers())) {
            return false;
        }
        
        // remote peers identical ?
        if(!SharkCSAlgebra.identical(cs1.getRemotePeers(), cs2.getRemotePeers())) {
            return false;
        }
        
        // locations identical ?
        if(!SharkCSAlgebra.identical(cs1.getLocations(), cs2.getLocations())) {
            return false;
        }
        
        // times identical ?
        if(!SharkCSAlgebra.identical(cs1.getTimes(), cs2.getTimes())) {
            return false;
        }
        
        // anythings got a match
        return true;
    }
    /**
    * Add newly arrived knowledge.
    * 
    * This methode iterates the knowledge and adds each context point.
    * 
    * The methode takes any context point from the knowledge and performs
    * an algorithm with it.
    * 
    * <br/>Each context point is taken from the knowledge. Each CP has context 
    * coordinates which are up to six semantic tags and an integer value.
    * The tags are supposed to be from another peer / another knowledge. Those
    * tags don't necessarily exist in this knowledge base. There is no problem at
    * all if they exist. 
    * 
    * <br/>Imagine, Bob has sent knowledge to Alice let's say about 
    * 'soccer'. Alice wasn't aware of 'soccer' until know. She was just interested
    * in 'sports'. Bob now claims, that soccer is a kind of sport. More technically:
    * Bob has set the tag 'soccer' to be a sub tag of 'sports'. This relation is 
    * defined in the background knowledge in the knowledge object. The 'soccer'
    * tag is in the topic slot of the context coordinate.
    *  
    * <br/>Alice has now two choices: 
    * <ul>
    * <li>
    * She can learn the new semantic tag 'soccer'from Bob.
    * <li> She can replace the original tag 'soccer' with
    * 'sports'
    * </ul>
    * 
    * <br/><br/><br/>
    * How does assimilation work:
    * Interest is a guard for the kb. It defines a context space
    * in which information is accepted.
    * 
    * Target is the knowledge base in which the knowledge is to
    * be assimilated.
    * 
    * Each context points comes with coordinates. There are several cases:
    * 
    * 1. Coordinates can be found in interest - add it
    * 
    * 2. Coordinates are not directly in the interest. Now, background
    * knowledge is used. It comes with the knowledge from the remote
    * peer and describes relations between tag used in cp coordinates
    * and other tags. Thus, a CP can be added if there is a way from 
    * the semantic tag in the coordinate (which is unknown in the interest) 
    * to a tag in the background that matches with one in the interest.
    * 
    * Actually, this leads to a contextualization. Coordinates are taken 
    * from context point and used to extract a fragment from background
    * knowledge. This process can be configured with fragmentation 
    * parameter which are ought to be set be the local peer (and not the
    * remote peer). The result is called 'effective background'. It is
    * the context space that is used to check whether this context 
    * point shall be added or not. Adding an actual context point is another 
    * job.
    *
    * 
    * This behaviour is managed by the learnTags Parameter. New tags are added 
    * to the knowledge base if it is set on true. Tags are replace by already existing
    * tags otherwise.
    *  
     * @param target
     * @param interest
    * @param knowledge
    * @param backgroundFP
    * @param learnTags 
    * @param deleteAssimilated if true - context points will be deleted from
    * knowledge if they matched and have been added to target.
     * @return 
     * @throws net.sharkfw.knowledgeBase.SharkKBException 
    */
    public static ArrayList<ContextCoordinates> assimilate(SharkKB target, 
            SharkCS interest, FragmentationParameter backgroundFP[], Knowledge knowledge, 
            boolean learnTags, boolean deleteAssimilated) 
                throws SharkKBException {
        
        ArrayList<ContextCoordinates> assimilated = new ArrayList<ContextCoordinates>();
        ArrayList<ContextPoint> assimilatedCP = new ArrayList<ContextPoint>();
        
        // Iterate context points
        Enumeration<ContextPoint> enumCP = knowledge.contextPoints();
        
        // no cps - finish
        if(enumCP == null || !enumCP.hasMoreElements()) {
                        return assimilated;
                    }
        
//        System.out.println("Calculate effective background.");

//        System.out.println("Source: (background from received knowledge):");
//        System.out.println(L.contextSpace2String(knowledge.getContext().asSharkCS()));
//
//        System.out.println("Context: (local interest):");
//        System.out.println(L.contextSpace2String(interest));
        
        
        // create effective background
        Interest effectiveBackground = SharkCSAlgebra.contextualize(
                knowledge.getVocabulary().asSharkCS(), interest, backgroundFP);
        
        L.d("Effective background for assimilation\n" + L.contextSpace2String(effectiveBackground), target);
        
        if(effectiveBackground == null) {
            L.d("no effective background - no assimilation", null);
            return assimilated;
        }
        
//        System.out.println("Effective background:");
//        System.out.println(L.contextSpace2String(effectiveBackground));
        
        while(enumCP.hasMoreElements()) {
            ContextPoint remoteCP = enumCP.nextElement();
            /* we force direction to be IN
             * We don't want remote peers to define direction setting in 
             * this local knowledge base
             */
            ContextCoordinates cpCC = remoteCP.getContextCoordinates();
            
//            ContextCoordinates cc = InMemoSharkKB.createInMemoContextCoordinates(
//                    cpCC.getTopic(), cpCC.getOriginator(), cpCC.getPeer(), 
//                    cpCC.getRemotePeer(), cpCC.getTime(), cpCC.getLocation(), 
//                    SharkCS.DIRECTION_IN
//                    );

            // alread switch peer and remote peer to get it into this inertial system
            ContextCoordinates cc = InMemoSharkKB.createInMemoContextCoordinates(
                    cpCC.getTopic(), cpCC.getOriginator(), cpCC.getRemotePeer(), 
                    cpCC.getPeer(), cpCC.getTime(), cpCC.getLocation(), 
                    SharkCS.DIRECTION_IN
                    );
            
            L.d("Algebra: #1225: check, whether this cp fits:\n" + L.contextSpace2String(cc), target);
            // check if this cp fits to effective background
            
            if(SharkCSAlgebra.isIn(effectiveBackground, cc)) {
                L.d("CP fits", target);
                
//                // switch peer and remote peer
//                cc = InMemoSharkKB.createInMemoContextCoordinates(
//                    cc.getTopic(), cc.getOriginator(), cc.getRemotePeer(), 
//                    cc.getPeer(), cc.getTime(), cc.getLocation(), 
//                    cc.getDirection());
                
                remoteCP.setContextCoordinates(cc);
                
                // it fits
                SharkCSAlgebra.merge(target, effectiveBackground,
                        remoteCP, learnTags);
                
                // remember
                assimilatedCP.add(remoteCP);
            } else {
                L.d("CP does not fit\n", target);
            }
        }
        
//        System.out.println("Algebra #1074: target after assimilation:\n");
//        System.out.println(L.kbSpace2String(target));
        
        // prepare return set - remember coordinates
        Iterator<ContextPoint> cpIter = assimilatedCP.iterator();
        while(cpIter.hasNext()) {
            assimilated.add(cpIter.next().getContextCoordinates());
        }

        // now remove assimilated cp from knowledge - and give it to caller.
        if(deleteAssimilated) {
            cpIter = assimilatedCP.iterator();
            while(cpIter.hasNext()) {
                ContextPoint cpDelete = cpIter.next();
                knowledge.removeContextPoint(cpDelete);
            }
        }
        
        return assimilated;
    }
    
    /**
     * Check if given coordinates are within the given (sub) space.
     * @param space
     * @param cc
     * @return 
     */
    public static boolean isIn(SharkCS space, ContextCoordinates cc) 
            throws SharkKBException {
        
        // dimension
        int sD = space.getDirection();
        int cD = cc.getDirection();
        
        if(sD != cD) {
            // not same direction - should be false but..
            
            // what works is this:
            // sd = in/out && cd != nothing
            // and sd != nothing && cd == in/out
            if(!( 
               (sD == SharkCS.DIRECTION_INOUT && cD != SharkCS.DIRECTION_NOTHING)
               || 
               (cD == SharkCS.DIRECTION_INOUT && sD != SharkCS.DIRECTION_NOTHING)
               ))
            {
                // no way - that doesn't fit
                return false;
            }
        }
        
        // originator
        if(!SharkCSAlgebra.identical(space.getOriginator(), cc.getOriginator())) 
        {
            return false;
        }
        
        SemanticTag ccTag;
        STSet sourceSet;
        SemanticTag tag;
        
        // topic
        ccTag = cc.getTopic();
        sourceSet = space.getTopics();
        if((!SharkCSAlgebra.isIn(sourceSet, ccTag))) {
                    return false;
                }
        
        // peer
        ccTag = cc.getPeer();
        sourceSet = space.getPeers();
        if((!SharkCSAlgebra.isIn(sourceSet, ccTag))) {
                    return false;
                }
        
        // remote Peer
        ccTag = cc.getRemotePeer();
        sourceSet = space.getRemotePeers();
        if((!SharkCSAlgebra.isIn(sourceSet, ccTag))) {
                    return false;
                }
         
        // time
        ccTag = cc.getTime();
        sourceSet = space.getTimes();
        if((!SharkCSAlgebra.isIn((TimeSTSet) sourceSet, (TimeSemanticTag) ccTag))) {
                    return false;
                }
         
        // location
        ccTag = cc.getLocation();
        sourceSet = space.getLocations();
        if((!SharkCSAlgebra.isIn(sourceSet, ccTag))) {
                    return false;
                }
        
        return true;
    }
    
    /**
     * Checks if tag is in source and takes any definition into account.
 
     * @param source
     * @param tag
     * @return It return true if one of the parameter matches with any or of 
     * tag can be found in source.
     */
    public static boolean isIn(STSet source, SemanticTag tag) 
            throws SharkKBException {
        
        // test on any
        if(tag != null && !tag.isAny() && !SharkCSAlgebra.isAny(source)) {
            // not any - try to find ccTag in source
            tag = source.getSemanticTag(tag.getSI());
            if(tag == null) {
                // not found
                return false;
            } 
        }
        
        return true;
    }
    
    public static boolean isIn(SpatialSemanticTag sst1, SpatialSemanticTag sst2) throws SharkKBException {
        return SharkCSAlgebra.getSpatialAlgebra().isIn(sst1, sst2);
    }
    
    public static boolean isIn(TimeSTSet tSet, TimeSemanticTag tst) throws SharkKBException {
        // assume a packed time set
        if(tSet == null) {
            return true; // means anytime
        }
        
        Enumeration<TimeSemanticTag> timeTags = tSet.timeTags();
        if(timeTags == null || !timeTags.hasMoreElements()) {
            return true;
        }
        
        if(tst == null) {
            return false; // target is a limited time frame, so must tag
        }
        
        while(timeTags.hasMoreElements()) {
            TimeSemanticTag tFrame = timeTags.nextElement();
            if(SharkCSAlgebra.isIn(tFrame, tst)) {
                return true;
            }
        }
        
        return false;
        
    }
    
    /**
     * Return true if probe is not null and fits into the time frame of target.
     * An emoty target is assumed to specify anytime.
     * @param target
     * @param probe
     * @return 
     */
    public static boolean isIn(TimeSemanticTag target, TimeSemanticTag probe) {
        if(target == null) { return true; }
        if(probe == null) { return false;} // target is not null
        
        if(probe.getFrom() < target.getFrom()) { return false; } // earlier
        
        long probeEnd = probe.getDuration() == TimeSemanticTag.FOREVER ? 
                TimeSemanticTag.FOREVER: probe.getFrom() + probe.getDuration();

        long targetEnd = target.getDuration() == TimeSemanticTag.FOREVER ? 
                TimeSemanticTag.FOREVER: target.getFrom() + target.getDuration();

        if(probeEnd == targetEnd) { return true;}
        
        if(probeEnd > targetEnd) { return false; }
        
        return true;
    }
    
    public static boolean isIn(TimeSemanticTag target, long from, long duration) {
        TimeSemanticTag probe = InMemoSharkKB.createInMemoTimeSemanticTag(from, duration);
        
        return SharkCSAlgebra.isIn(target, probe);
    }
    
    /**
     * Merges a cp into a kb - learns semantic tag, if flag is set.
     * @param target
     * @param cp 
     * @param learnSTs
     */
    public static void merge(SharkKB target, SharkCS bg, 
            ContextPoint cp, boolean learnSTs) throws SharkKBException 
    {
        
        ContextCoordinates cc = cp.getContextCoordinates();
        
        PeerSemanticTag mOriginator, mPeer, mRemotePeer;
        SemanticTag mTopic;
        TimeSemanticTag mTime;
        SpatialSemanticTag mLocation = null;
        
        /* check for each facet if it already exists in target
         * if not take actions depending on learning strategy
         */
        
        PeerSemanticNet peers = target.getPeersAsSemanticNet();
        SemanticNet topics = target.getTopicsAsSemanticNet();
        TimeSTSet times = target.getTimeSTSet();
        SpatialSTSet locations = target.getSpatialSTSet();
        
        ////////////////////////////////////////////////////////////
        //                   direction                            //
        ////////////////////////////////////////////////////////////
        
        int mDirection = cc.getDirection();
        
        ////////////////////////////////////////////////////////////
        //                     topics                             //
        ////////////////////////////////////////////////////////////
        
        if(SharkCSAlgebra.isAny(cc.getTopic())) {
            mTopic = null; // means any
        } else {
            // find originator st in target kb
            
//            StringBuffer buf = new StringBuffer();
//            L.dimension2StringBuffer(topics, buf);
//            System.out.println("Algebra: #1217: topics dimension target == " + buf.toString());
            
            mTopic = topics.getSemanticTag(cc.getTopic().getSI());
            
//            System.out.println("Algebra: #1220: mTopic == " + mTopic + "cc.topic.si: " + cc.getTopic().getSI()[0]);
            if(mTopic == null) {
                // not found...
               
                /*
                 * situation is this: We have a peer that isn't in
                 * the target kb. We have background knowledge which actually
                 * describes relations of e.g. peers.
                 * 
                 * We can no try to find a match between a tag in background
                 * with a tag in target which has the shortest path to our
                 * tag.
                 */
                
//                System.out.println("Algebra #1220: before topic assimilation:\n");
//                System.out.println(L.kbSpace2String(target));
                
                mTopic = SharkCSAlgebra.assimilate(
                        cc.getTopic(), topics, 
                        bg.getTopics(), learnSTs);
                
//                System.out.println("Algebra #1226: after topic assimilation:\n");
//                System.out.println(L.kbSpace2String(target));
            } 
//            else {
//                buf = new StringBuffer();
//                L.semanticTag2StringBuffer(mTopic, buf);
//                System.out.println("\nAlgebra: #1231: mTopic == " + buf.toString());
//            }
        }
        
        ////////////////////////////////////////////////////////////
        //                  originator                            //
        ////////////////////////////////////////////////////////////
        
        if(SharkCSAlgebra.isAny(cc.getOriginator())) {
            mOriginator = null; // means any
        } else {
            // find originator st in target kb
            mOriginator = peers.getSemanticTag(cc.getOriginator().getSI());
            if(mOriginator == null) {
                mOriginator = (PeerSemanticTag) SharkCSAlgebra.assimilate(
                        cc.getOriginator(), target.getPeersAsSemanticNet(), 
                        bg.getPeers(), learnSTs);
                
            }
        }
        
        ////////////////////////////////////////////////////////////
        //                        peer                            //
        ////////////////////////////////////////////////////////////

        if(SharkCSAlgebra.isAny(cc.getPeer())) {
            mPeer = null; // means any
        } else {
            // find originator st in target kb
            mPeer = peers.getSemanticTag(cc.getPeer().getSI());
            if(mPeer == null) {
                mPeer = (PeerSemanticTag) SharkCSAlgebra.assimilate(
                        cc.getPeer(), target.getPeersAsSemanticNet(), 
                        bg.getPeers(), learnSTs);
            }
        }
        
        ////////////////////////////////////////////////////////////
        //                  remote peers                          //
        ////////////////////////////////////////////////////////////
        
        if(SharkCSAlgebra.isAny(cc.getRemotePeer())) {
            mRemotePeer = null; // means any
        } else {
            // find originator st in target kb
            mRemotePeer = peers.getSemanticTag(cc.getRemotePeer().getSI());
            if(mRemotePeer == null) {
                mRemotePeer = (PeerSemanticTag) SharkCSAlgebra.assimilate(
                        cc.getRemotePeer(), target.getPeersAsSemanticNet(), 
                        bg.getPeers(), learnSTs);
            }
        }
        
        ////////////////////////////////////////////////////////////
        //                          time                          //
        ////////////////////////////////////////////////////////////

        // time are always learnt. Full stop
        times.merge(cc.getTime());
        mTime = cc.getTime();
        
        ////////////////////////////////////////////////////////////
        //                      location                          //
        ////////////////////////////////////////////////////////////

        if(SharkCSAlgebra.isAny(cc.getLocation())) {
            mLocation = null; // means any
        } else {
            if(learnSTs) {
                mLocation = (SpatialSemanticTag) locations.merge(cc.getLocation());
            }
        }
        
        // a new cp can be created
        ContextCoordinates mCC = target.createContextCoordinates(mTopic, 
                mOriginator, mPeer, mRemotePeer, 
                mTime, mLocation, mDirection);
        
        ContextPoint mCP = target.createContextPoint(mCC);
        
        Enumeration<Information> infoEnum = cp.enumInformation();
        if(infoEnum != null) {
            while(infoEnum.hasMoreElements()) {
                mCP.addInformation(infoEnum.nextElement());
            }
        }
        
        // copy properties
        Util.copyPropertiesFromPropertyHolderToPropertyHolder(cp, mCP);
        
//        System.out.println("Algebra #1340: target after assimilation:\n");
//        System.out.println(L.kbSpace2String(target));
    }
    
    private static SemanticTag assimilate(SemanticTag tag, STSet target, 
            STSet source, boolean learnSTs) throws SharkKBException {

        if(! (target instanceof SemanticNet)) {
            throw new SharkKBException("find shortest path requires non empty semantic networks and an existing start point");
        }
        
        SemanticNet snTarget = (SemanticNet) target;
        
        boolean executed = false;
        ShortestPath path = null;
        try {
            path = new ShortestPath(tag, target, source);
            executed = true;
        }
        catch(SharkKBException e) {
            // something wrong
        }

        if(executed && path.found()) {
            // there is a path

            if(learnSTs) {
                // add path to target
                SemanticNet path2Merge = path.shortestPath();
                
                // make it copatible with taxonomy
                SharkCSAlgebra.checkSuperSubPairsInSemanticNet(path2Merge);

                SharkCSAlgebra.merge(snTarget, path2Merge);
                
                return snTarget.getSemanticTag(tag.getSI());
            } else {
                // substitute coordinate with tag in target
                return snTarget.getSemanticTag(path.endpoint().getSI());
            }
        } else {
            // no path
            if(learnSTs) {
                // just add the tag
                target.merge(tag);
                return snTarget.getSemanticTag(tag.getSI());
            } else {
                // there is no path, st in coordinate is no known
                return null;
            }
        }
    }
    
    /**
     * Checks if any super-relation has also a sub-relation counterpart
     * in semantic net. If not - add.
     */
    public static void checkSuperSubPairsInSemanticNet(SemanticNet sn) throws SharkKBException {
        if(sn == null) { return; }
        
        Enumeration<SemanticTag> tagEnum = sn.tags();
        if(tagEnum == null) { return; }
        while(tagEnum.hasMoreElements()) {
            SNSemanticTag sourceTag = (SNSemanticTag) tagEnum.nextElement();
            
            // check super only
            Enumeration<SNSemanticTag> superTagsEnum = sourceTag.targetTags(SemanticNet.SUPERTAG);
            if(superTagsEnum == null) { continue; }
            while(superTagsEnum.hasMoreElements()) {
                SNSemanticTag superTarget = superTagsEnum.nextElement();
                
                // just set predicate - duplicates are supressed
                superTarget.setPredicate(SemanticNet.SUBTAG, sourceTag);
            }
        }
    }
    
    /**
     * Most simple version of extraction: Zero fragmentation parameter are used,
     * no recipient or groups are used
     * @param source
     * @param context
     * @return
     * @throws SharkKBException 
     */
    public static Knowledge extract(SharkKB source, 
            SharkCS context) 
            throws SharkKBException {
        
        SharkKB target = new InMemoSharkKB();
        
        FragmentationParameter[] fps = FragmentationParameter.getZeroFPs();
        
        return SharkCSAlgebra.extract(target, source, context, 
                fps, false, null);
    }

    public static Knowledge extract(SharkKB source, 
            SharkCS context, FragmentationParameter[] fp) 
            throws SharkKBException {
        
        SharkKB target = new InMemoSharkKB();
        
        return SharkCSAlgebra.extract(target, source, context, 
                fp, false, null);
    }

    public static Knowledge extract(SharkKB source, 
            SharkCS context, 
            FragmentationParameter[] backgroundFP, PeerSemanticTag recipient) 
                throws SharkKBException {
        
        SharkKB target = new InMemoSharkKB();
        
        return SharkCSAlgebra.extract(target, source, context, 
                backgroundFP, true, recipient);
    }
    
    public static Knowledge extract(SharkKB source, 
            SharkCS context, FragmentationParameter[] backgroundFP, 
            boolean cutGroups) 
                throws SharkKBException {
        
        SharkKB target = new InMemoSharkKB();
        
        return SharkCSAlgebra.extract(target, source, context, 
                backgroundFP, true, null);
    }
    
    public static Knowledge extract(SharkKB target, SharkKB source, 
            SharkCS context, FragmentationParameter[] backgroundFP, 
            boolean cutGroups) 
                throws SharkKBException {
        
        return SharkCSAlgebra.extract(target, source, context, 
                backgroundFP, true, null);
    }
    

    /**
    * It returns knowledge that fits into the given parameter.
    * 
    * This methode is somewhat complex and actually one of the two core
    * concepts of Shark. It extracts context points that fit the the given
    * context. These context points are copied for further processing. The
    * <code>target</code> parameter is jused to create a copy. In most cases,
    * using an in-memo implementation would be advisable.
    * 
    * Now, the method makes the following:
    * 
    * <ul>
    * <li>This methode doesn't return any semantic tag that is hidden.
    * <li> This methode doesn't change anything in the source but in the copy
    * which resides in the target kb. 
    * </ul>

    * It also replaces group semantic tag with actual peer semantic tag.
    * 
    * Peers can be parts of peer groups. That's defined with sub relations
    * in PeerTaxonomy. Most application don't want to reveal local groups
    * to other peers. This methode removes any group in context point coordinates
    * and background knowledge to which recipient belongs.
    * 
    * The algorithm is this:
    * <ul>
    * <li>Iterate any context point.
    * <li>Check if one of the peer coordinates (originator, peer, remotePeer)
    * is a group and recipient is part of it.
    * <li> if so, group is replaced by recipient in coordinate and in background
    * knowledge
    * </ul>
    * 
    * @param target KB to which cps and st are copied - It must be empty.
    * @param source 
    * @param context
    * @param backgroundFP
    * @param cutGroups
    * @param recipient recipient of extracted knowledge - if null and cutGroupis set - all groups are resolved
    * is performed.
    * @return
    * @throws SharkKBException 
    */
    public static Knowledge extract(SharkKB target, SharkKB source, 
            SharkCS context, FragmentationParameter[] backgroundFP, 
            boolean cutGroups, PeerSemanticTag recipient) 
                throws SharkKBException {

        // create fragment
//        Interest effectiveInterest = source.createInterest();
//
//        // create intersection between received context and local kb.
//        SharkCSAlgebra.contextualize(effectiveInterest, source.asSharkCS(), 
//                context, extractionFP);

        // extract matching context points - matching with any is allowed
//        Enumeration<ContextPoint> cpEnum = source.getContextPoints(effectiveInterest, true);

        /* create background knowledge
         * means: merge fragments to target that describe context points
         * coordinates position into a net of concept
         */
        SemanticNet topicBG = 
                source.getTopicsAsSemanticNet().contextualize(context.getTopics(), backgroundFP[SharkCS.DIM_TOPIC]);
        if(topicBG != null) {
                    target.getTopicsAsSemanticNet().merge(topicBG);
                }

        PeerSTSet tmpPeerST = InMemoSharkKB.createInMemoPeerSTSet();
        tmpPeerST.merge(context.getOriginator());
        
        PeerSemanticTag originatorBG = null;
        PeerSTSet peerBG = null;
        PeerSTSet remotePeerBG = null;
        
        PeerTaxonomy peerTX = 
                source.getPeersAsTaxonomy().contextualize(tmpPeerST, backgroundFP[SharkCS.DIM_ORIGINATOR]);
        if(peerTX != null) {
               originatorBG = context.getOriginator();
               target.getPeerSTSet().merge(context.getOriginator());
        }
        
        peerTX = source.getPeersAsTaxonomy().contextualize(context.getPeers(), backgroundFP[SharkCS.DIM_PEER]);
        if(peerTX != null) {
            target.getPeersAsTaxonomy().merge(peerTX);
            peerBG = peerTX.asPeerSTSet();
        }
        
        PeerTaxonomy remotePeers = 
                source.getPeersAsTaxonomy().contextualize(context.getRemotePeers(), backgroundFP[SharkCS.DIM_REMOTEPEER]);
        if(peerTX != null) {
            target.getPeersAsTaxonomy().merge(remotePeers);
            remotePeerBG = remotePeers.asPeerSTSet();
        }
        
        TimeSTSet timesBG = 
                source.getTimeSTSet().contextualize(context.getTimes(), backgroundFP[SharkCS.DIM_TIME]);
        if(timesBG != null) {
                    target.getTimeSTSet().merge(timesBG);
                }
        
        SpatialSTSet locationsBG =
                source.getSpatialSTSet().contextualize(context.getLocations(), backgroundFP[SharkCS.DIM_LOCATION]);
        if(locationsBG != null) {
                    target.getSpatialSTSet().merge(locationsBG);
                }

        // create knowledge with target with background
        Knowledge resultKnowledge = target.createKnowledge();
        
        SharkCS effectiveCC = InMemoSharkKB.createInMemoInterest(
                topicBG, originatorBG, peerBG, remotePeerBG, 
                timesBG, locationsBG, context.getDirection());
        
//        Enumeration<ContextPoint> cpEnum = source.getContextPoints(target.asSharkCS(), true);
        Enumeration<ContextPoint> cpEnum = source.getContextPoints(effectiveCC, true);
        if(cpEnum == null) {
            return null;
        }

        // remember peer taxonomy which is part of knowledge' background
        PeerTXSemanticTag recipientTX = null;
        peerTX = source.getPeersAsTaxonomy();
        try {
            if(recipient != null && peerTX != null) {
                recipientTX = peerTX.getSemanticTag(recipient.getSI());
            }
        }
        catch(SharkKBException e) {
            /* in this case - peers are not structured in a taxonomy
             * whole method makes no sense.
             * Just go ahead
             */
        }

        // add cp from source into target
        while(cpEnum.hasMoreElements()) {
            
            ContextPoint cp = cpEnum.nextElement();
            ContextCoordinates newCC = null;
            
            // cut peer groups?
            if(cutGroups && peerTX != null) {
                if(recipientTX != null) {
                
                    // save coordinates
                    ContextCoordinates cc = cp.getContextCoordinates();

                    // is there a group?
                    PeerTXSemanticTag originator = null, peer = null, remotePeer = null;
                    PeerTXSemanticTag oldOriginator=null, oldPeer=null, oldRemotePeer=null;

                    if(cc.getOriginator() != null) {
                        originator = peerTX.getSemanticTag(cc.getOriginator().getSI());
                    }
                    
                    if(cc.getPeer() != null) {
                        peer = peerTX.getSemanticTag(cc.getPeer().getSI());
                    }
                    
                    if(cc.getRemotePeer() != null) {
                        remotePeer = peerTX.getSemanticTag(cc.getRemotePeer().getSI());
                    }

                    if(peerTX.isSubTag(originator, recipientTX)) {
                        // yes, there must be changed something
                        oldOriginator = originator;
                        originator = recipientTX;
                    } 

                    if(peerTX.isSubTag(peer, recipientTX)) {
                        oldPeer = peer;
                        peer = recipientTX;
                    }

                    if(peerTX.isSubTag(remotePeer, recipientTX)) {
                        oldRemotePeer = remotePeer;
                        remotePeer = recipientTX;
                    }

                    // something to remove in target vocabulary?
                    if(oldOriginator != null || oldPeer != null || oldRemotePeer != null) {
                        PeerTaxonomy targetPeerTX = target.getPeersAsTaxonomy();

                        if(oldOriginator != null) {
                            targetPeerTX.removeSubTree(oldOriginator);
                        }

                        // drop subtree if not already dropped
                        if(oldPeer != null 
                            && (oldOriginator == null || !oldPeer.identical(oldOriginator))) {
                            targetPeerTX.removeSubTree(oldPeer);
                        }

                        if(oldRemotePeer != null
                            && (oldOriginator == null || !oldRemotePeer.identical(oldOriginator))
                            && (oldPeer == null || !oldRemotePeer.identical(oldPeer)))
                        {
                            targetPeerTX.removeSubTree(oldRemotePeer);
                        }

                        // set new coordinates
                        newCC = target.createContextCoordinates(cc.getTopic(), 
                                originator, peer, remotePeer, cc.getTime(), 
                                cc.getLocation(), cc.getDirection());

                    } 
                } // recipient is known
                else { // no recipient set but cut groups
                    Enumeration<SemanticTag> tagEnum = target.getPeerSTSet().tags();
                    if(tagEnum != null) {
                        while(tagEnum.hasMoreElements()) {
                            target.getPeersAsTaxonomy().resolveSuperPeers(
                                    (PeerTXSemanticTag)tagEnum.nextElement());
                        }
                    }
                }
            } // cut groups

            ContextPoint copyCP = SharkCSAlgebra.copy(target, source, cp, newCC);
            resultKnowledge.addContextPoint(copyCP);
        }

        return resultKnowledge;
    }
    
    /**
     * Makes a copy of a context point. Each semantic tag in context coordinates
     * is copied as well to target kb. <b> Note: Information are not copied. 
     * Reference are takes instead</b>
     * @param target A kb that will contain copies of the context point
     * @param source
     * @param cp 
     * @return reference to newly created copy in target kb
     */
    public static ContextPoint copy(SharkKB target, SharkKB source, 
            ContextPoint cp) throws SharkKBException {
        
        return SharkCSAlgebra.copy(target, source, cp, null);
        
    }

    private static ContextPoint copy(SharkKB target, SharkKB source, 
            ContextPoint cp, ContextCoordinates copiedCC) throws SharkKBException {

        if(copiedCC == null) {
            copiedCC = SharkCSAlgebra.copy(target, cp.getContextCoordinates());
        }
        
        // create copy of cp
        ContextPoint copiedCP = target.createContextPoint(copiedCC);
        
        Enumeration<Information> infoEnum = cp.enumInformation();
        if(infoEnum != null) {
            while(infoEnum.hasMoreElements()) {
                copiedCP.addInformation(infoEnum.nextElement());
            }
        }
        
        // copy properties
        Util.copyPropertiesFromPropertyHolderToPropertyHolder(cp, copiedCP);
        
        return copiedCP;
        
    }
    
    /**
     * Makes a copy of each semantic tag in coordinates in target and return
     * a reference to that newly created coordinate
     * 
     * @param target SharkBK to which the semantic tags shall be copied
     * @param cc coordinates to copy
     * @return Copy made in target.
     */
    public static ContextCoordinates copy(SharkKB target, ContextCoordinates cc) 
        throws SharkKBException {
        
        SemanticTag copyTopic = null;
        PeerSemanticTag copyOriginator = null, copyPeer = null, 
                copyRemotePeer = null;
        
        TimeSemanticTag copyTime = null;
        SpatialSemanticTag copyLocation = null;
        
        // copy topic to target
        SemanticTag topic = cc.getTopic();
        if(topic != null) {
            copyTopic= target.getTopicSTSet().createSemanticTag(topic.getName(), 
                    topic.getSI());
        }
        
        // copy peer to target
        PeerSemanticTag peer = cc.getPeer();
        if(peer != null) {
            copyPeer = target.getPeerSTSet().createPeerSemanticTag(
                    peer.getName(), peer.getSI(), peer.getAddresses());
        }
        
        // copy remotepeer to target
        peer = cc.getRemotePeer();
        if(peer != null) {
            copyRemotePeer = target.getPeerSTSet().createPeerSemanticTag(
                    peer.getName(), peer.getSI(), peer.getAddresses());
        }

        // copy originator to target
        peer = cc.getOriginator();
        if(peer != null) {
            copyOriginator = target.getPeerSTSet().createPeerSemanticTag(
                    peer.getName(), peer.getSI(), peer.getAddresses());
        }
        
        // copy times
        TimeSemanticTag time = cc.getTime();
        if(time != null) {
            copyTime = target.getTimeSTSet().createTimeSemanticTag(
                    time.getFrom(), time.getDuration());
        }

        // copy location
        SpatialSemanticTag location = cc.getLocation();
        // TODO: now it is always null
//        if(location != null) {
//            copyLocation = target.getSpatialSTSet().createSpatialSemanticTag(
//                    location.getName(), location.getSI(), location.getPoints());
//        }
        
        ContextCoordinates copyCC = target.createContextCoordinates(
                copyTopic, copyOriginator, copyPeer, copyRemotePeer, 
                copyTime, copyLocation, cc.getDirection());
        
        return copyCC;
    }
    
    public static SemanticTag createAnyTag() throws SharkKBException {
        return InMemoSharkKB.createInMemoSemanticTag("any", SharkCS.ANYURL);
    }
    
	/**
	 * Checks if subject identifier is and valid URL.
	 * 
	 * @param si url to check
	 * @return true, if url is valid, else false
	 */
	public static boolean isValidSi(String si) {
		boolean valid = true;
		try {
			new URL(si);
		} catch (Exception e) {
			valid = false;
		}
		return valid;
	}

	/**
	 * Checks if address starts with tcp:// or is a valid e-mail address.
	 * 
	 * @param address address text
	 * @return true, if address is valid, else false
	 */
	public static boolean isValidAddress(String address) {
		boolean valid = false;
		String[] addressData = address.split("://");
		if (addressData.length == 2) {
			if ("tcp".equals(addressData[0])) {
				valid = true;
			} else if ("mail".equals(addressData[0])) {
				valid = true;
			} 
		} else {
			try {
				String emailreg = "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
				Boolean emailCheck = address.matches(emailreg);
				if (emailCheck) {
					valid = true;
				}
			} catch (Exception e) {
			}
		}
		return valid;
	}
}
