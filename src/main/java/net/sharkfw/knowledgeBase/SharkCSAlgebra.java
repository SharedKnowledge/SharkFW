package net.sharkfw.knowledgeBase;

import net.sharkfw.asip.ASIPSpace;
import net.sharkfw.knowledgeBase.inmemory.InMemoSharkKB;
import net.sharkfw.system.Iterator2Enumeration;
import net.sharkfw.system.L;
import net.sharkfw.system.Util;

import java.net.URL;
import java.util.*;

/**
 * This class implements basic methods that makes up the algebra
 * of the context space theory.
 *
 * @author thsc
 */
public abstract class SharkCSAlgebra extends SharkAlgebra {
    private static net.sharkfw.knowledgeBase.geom.SpatialAlgebra spatialAlgebra;
    private static final String JTS_SPATIAL_ALGEBRA_CLASS =
            "net.sharkfw.knowledgeBase.geom.jts.SpatialAlgebra";

    private static ArrayList<ArrayList<SNSemanticTag>> tComponents;

    private static int tIndex;
    private static Stack<SNSemanticTag> tStack;

    private static HashMap<SNSemanticTag, Integer> tIndices;
    private static HashMap<SNSemanticTag, Integer> tLowLinks;
    private static HashMap<SNSemanticTag, Boolean> tChecked;

    static {

        boolean done = false;

        try {
            Class spatialAlgebraClass = Class.forName(JTS_SPATIAL_ALGEBRA_CLASS);
            Object newInstance = spatialAlgebraClass.newInstance();

            SharkCSAlgebra.spatialAlgebra = (net.sharkfw.knowledgeBase.geom.SpatialAlgebra) newInstance;

            L.d("JTS Algebra instanciated");
            done = true;

        } catch (ClassNotFoundException | java.lang.NoClassDefFoundError ex) {
//            L.d("no JTS Spatial Algebra found - take default: " + ex.getMessage());
        } catch (InstantiationException | IllegalAccessException ex) {
            L.d("couldn't instantiate JTS Spatial Algebra - take default: " + ex.getMessage());
        } catch (ClassCastException ex) {
            L.d("weired: JTS Spatial Algebra found and instanziated but object isn't of type SpatialAlgebra - take default: " + ex.getMessage());
        }

        if (!done) {
            SharkCSAlgebra.spatialAlgebra = new net.sharkfw.knowledgeBase.geom.SpatialAlgebra();
//            L.d("Default Spatial Algebra instantiated");
        }
    }

    public static net.sharkfw.knowledgeBase.geom.SpatialAlgebra getSpatialAlgebra() {
        return SharkCSAlgebra.spatialAlgebra;
    }


    /**
     * Determine whether or not <code>tagA</code> and <code>tagB</code> are
     * semantically identical. Is is checked by their subject identifier. At least
     * one much make both identically.
     *
     * @param tagA
     * @param tagB
     * @return
     */
    public static boolean identical(SemanticTag tagA, SemanticTag tagB) {
        // same object ?
        if (tagA == tagB) return true;

        if (SharkCSAlgebra.isAny(tagA) || SharkCSAlgebra.isAny(tagB)) return true;

        // Geometries super overrule sis
        if (tagA instanceof SpatialSemanticTag && tagB instanceof SpatialSemanticTag) {
            SpatialSemanticTag sTagA = (SpatialSemanticTag) tagA;
            SpatialSemanticTag sTagB = (SpatialSemanticTag) tagB;

            try {
                boolean identical = SharkCSAlgebra.spatialAlgebra.identical(sTagA, sTagB);
                return identical;
            } catch (SharkKBException e) {
                // weired
                L.w("exception while calculating whether two spatial tags are identical: " + e.getMessage());
                return false;
            }

        }

        String si_a[] = tagA.getSI();
        String si_b[] = tagB.getSI();

        boolean sisIdentical = SharkCSAlgebra.identical(si_a, si_b);

        if (!sisIdentical) return false;

        return sisIdentical;
    }

    /**
     * Checks whether two semantic tags are identical based on their subject
     * identifiers. Null is interpreted as any.
     *
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
     * @param siA
     * @param siB
     * @return
     */
    public static boolean identical(String siA, String siB) {
        return siA.equalsIgnoreCase(siB);
    }


    /**
     * Merge two semantic tags. It's pretty simple:
     * <p>
     * Target name remains unchanged if there is a name. Otherwise target gets
     * the name of the tag to merge in.
     * <p>
     * Properties are merged as well.
     * <p>
     * All subject identifieres of <code>toMerge</code> are added to
     * <code>target</code> without duplicates.
     * <p>
     * Target will be changed, toMerge remains unchanged.
     *
     * @param target
     * @param toMerge
     * @return target is returned
     */
    public static SemanticTag merge(SemanticTag target, SemanticTag toMerge) {
        String targetName = target.getName();
        if (targetName == null || targetName.length() == 0) {
            String name = new String(toMerge.getName());
            target.setName(name);
        }

        boolean checkAnySI = false;
        // iterate SIs now
        String[] toMergeSIS = toMerge.getSI();
        for (int iM = 0; iM < toMergeSIS.length; iM++) {
            String[] targetSIS = target.getSI();
            boolean found = false;
            int iT = 0;

            while (!found && iT < targetSIS.length) {
                if (SharkCSAlgebra.identical(targetSIS[iT++], toMergeSIS[iM])) {

                    // identical SI already exists
                    found = true;
                }
            }

            // we haven't found toMerge Si in target - add it.
            if (!found) {
                String si2add = new String(toMergeSIS[iM]);
                
                /* if we add at least a single si that isn't any.
                requires a test later
                */
                try {
                    target.addSI(si2add);
                    if (!SharkCSAlgebra.isAny(new String[]{si2add})) {
                        checkAnySI = true;
                    }
                } catch (SharkKBException e) {
                    // si seems alread to exist
                }
            }
        } // next si from tag to merge into target.
        
        /* maybe target contained an ANY SI
        If we have added a non-any-SI that any Si must be removed
        */
        if (checkAnySI) {
            if (target.isAny()) {
                try {
                    target.removeSI(ASIPSpace.ANYURL);
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
     * Implementation of ST set fragmentation. This implementation
     * is independent from actual implementing classes. Advantage: Any
     * ST set implementation can use it. Disadvantage: It's probably the
     * less efficient implementation.
     *
     * @param fragment            Must be an empty SemanticNet.
     *                            The fragment will be produced into this fresh semantic net.
     *                            This implementation doesn't care neither of implementing class (how
     *                            could it?) nor its status. It should be an empty semantic net.
     * @param allowedPredicates   Enumeration of allowed predicate types
     * @param forbiddenPredicates Enumeration of forbidden predicate types. Note:
     *                            Forbidden types rule out allowed types. Meaning: If type X is allowed and
     *                            forbidden as well -> X is forbidden.
     * @param depth               range (depth) to search in the network
     *                            parameter allows to define whether hidden properties should be copied into
     *                            the fragment or not.
     * @return The fragment is returned. Actually, it is the object that was
     * provided by methode callee as fragment parameter.
     * @throws SharkKBException
     */
    public static SemanticNet fragment(SemanticNet fragment,
                                       SemanticTag anchor, SemanticNet source,
                                       Enumeration<String> allowedPredicates,
                                       Enumeration<String> forbiddenPredicates, int depth)
            throws SharkKBException {
        if (depth < 0) {
            depth = 0;
        }

        // read Enumerations and keep it
        HashSet<String> allowedTypes = null, forbiddenTypes = null;

        // store allowed types
        if (allowedPredicates != null) {
            allowedTypes = new HashSet<String>();
            while (allowedPredicates.hasMoreElements()) {
                allowedTypes.add(allowedPredicates.nextElement());
            }

            // have we got anything?
            if (allowedTypes.isEmpty()) {
                // no - drop it - easier in following if - statements
                allowedTypes = null;
            }
        }

        if (forbiddenPredicates != null) {
            forbiddenTypes = new HashSet<String>();
            while (forbiddenPredicates.hasMoreElements()) {
                String forbiddenTypeString = forbiddenPredicates.nextElement();

                // check whether it is also allowed and remove it in this case
                if (allowedTypes != null) {
                    allowedTypes.remove(forbiddenTypeString);
                }

                forbiddenTypes.add(forbiddenTypeString);
            }

            // have we got anything?
            if (forbiddenTypes.isEmpty()) {
                // no - drop it - easier in following if - statements
                forbiddenTypes = null;
            }

            // allowed types can have become empty - check again
            if (allowedTypes != null && allowedTypes.isEmpty()) {
                allowedTypes = null;
            }
        }

        /* find that anchor in source to get a hand on the actual tag 
         * and not a copy or something als
         */
        SNSemanticTag anchorSource = source.getSemanticTag(anchor.getSI());
        if (anchorSource == null) {
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
        while (fTagEnum.hasMoreElements()) {
            SemanticTag st = fTagEnum.nextElement();

            // anchor predicates are already copied
            if (SharkCSAlgebra.identical(anchor, st)) continue;

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

        return fragment(fragment, anchor, source,
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

        if (copy == null) {
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
                (HashSet<String>) null, (HashSet<String>) null, depth, true) != null);
    }

    public static SemanticNet fragment(
            SNSemanticTag sourceTag,
            SemanticNet source,
            int depth) throws SharkKBException {

        SemanticNet fragment = InMemoSharkKB.createInMemoSemanticNet();

        return SharkCSAlgebra.fragment(fragment, sourceTag, source,
                (HashSet<String>) null, (HashSet<String>) null, depth, true);
    }

    private static SemanticNet fragment(SemanticNet fragment,
                                        SNSemanticTag sourceTag,
                                        SemanticNet source,
                                        HashSet<String> allowedTypes,
                                        HashSet<String> forbiddenTypes,
                                        int depth,
                                        boolean merge) throws SharkKBException {

        SNSemanticTag fragmentTag = null;

        if (merge) {
            // first of all - copy the source anchor to fragment        
            fragmentTag = fragment.merge(sourceTag);
        }
        // go into the network if allowed...

        if (depth < 1) return fragment;

        if (!merge && sourceTag != null) { // must already be there
            fragmentTag = fragment.getSemanticTag(sourceTag.getSI());
        }

        if (fragmentTag == null) return fragment;
        
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
         *         allowed | forbidden | resultSet
         * 1.      null    | null      | any
         * 2.      null    | x         | any except x
         * 3.      x       | null      | x
         * 4.      x       | y         | if in(x) and !in(y)
         * 
         */

        // find predicates in source
        Enumeration<String> predicateEnum = sourceTag.predicateNames();
        if (predicateEnum == null) {
            // ready - not related tags
            return fragment;
        }

        while (predicateEnum.hasMoreElements()) {
            String predicateString = predicateEnum.nextElement();

            // allowed ?
            boolean follow = false;

            // case 1
            if (allowedTypes == null && forbiddenTypes == null) {
                follow = true;
            }

            // case 2
            else if (allowedTypes == null && forbiddenTypes != null) {
                if (!forbiddenTypes.contains(predicateString)) {
                    follow = true;
                }
            }

            // case 3
            else if (allowedTypes != null && forbiddenTypes == null) {
                if (allowedTypes.contains(predicateString)) {
                    follow = true;
                }
            }

            // case 4
            else {
                if (allowedTypes.contains(predicateString)
                        && !forbiddenTypes.contains(predicateString)) {
                    follow = true;
                }
            }

            if (follow) {
                Enumeration<SNSemanticTag> tagEnum =
                        sourceTag.targetTags(predicateString);

                if (tagEnum != null) {
                    while (tagEnum.hasMoreElements()) {
                        SNSemanticTag referencedTag = tagEnum.nextElement();
                        if (referencedTag == null) {
                            continue;
                        }

                        SharkCSAlgebra.fragment(fragment, referencedTag, source,
                                allowedTypes, forbiddenTypes, depth - 1, merge);

                        // find copy of referenceTag
                        SNSemanticTag copyReferencedTag =
                                fragment.getSemanticTag(referencedTag.getSI());

                        // can be null if referenced tag is not part of the fragment
                        if (copyReferencedTag != null) {
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

        if (contextTagEnum == null) {
            /* Context is any
                * The context is assumed to be the thing that has to be kept
                * secret. So, what do we do with an empty context. We leave it 
                * to the fragmentation parameter. If depth is not null, we 
                * enumerate all concepts from source.
                */
            if (depth <= 0) {
                // context is null
                return null;
            } else {

                // there is a depth wished. Start with any tag in source.
                contextTagEnum = source.tags();
            }
        }

        if (contextTagEnum == null) {
            return fragment;
        }

        /*
        We need list of allowed and forbidden predicates more than one.
        We have got an enumeration - we must keep it.
        */

        Vector<String> allowed = new Vector();
        if (allowedPredicates != null) {
            while (allowedPredicates.hasMoreElements()) {
                allowed.add(allowedPredicates.nextElement());
            }
        }

        Vector<String> forbidden = new Vector();
        if (forbiddenPredicates != null) {
            while (forbiddenPredicates.hasMoreElements()) {
                forbidden.add(forbiddenPredicates.nextElement());
            }
        }

        while (contextTagEnum.hasMoreElements()) {
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

        return contextualize(fragment,
                new Iterator2Enumeration(anchorSet),
                source, allowedPredicates, forbiddenPredicates, depth);
    }

    /**
     * merges source into target. Tags and their properties are copied
     * as well as their predicates. Merging is performed if required.
     *
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

    ///////////////////////////////////////////////////////////////////////
    //                   contextualization / fragmentation               //
    ///////////////////////////////////////////////////////////////////////

    public static STSet contextualize(STSet fragment, STSet source,
                                      Enumeration<SemanticTag> anchorSet) throws SharkKBException {

        if (fragment == null) {
            throw new SharkKBException("fragment must be initialized, null not permitted in SharkCS.contextualize()");
        }

        if (anchorSet == null || !anchorSet.hasMoreElements()) {
            return null;
        }

        while (anchorSet.hasMoreElements()) {
            SemanticTag tag = anchorSet.nextElement();

            STSet s = source.fragment(tag);
            if (s != null) {
                fragment.merge(s);
            }
        }

        return fragment;
    }

    public static STSet fragment(STSet fragment, STSet source, SemanticTag anchor)
            throws SharkKBException {

        String sis[] = anchor.getSI();

        SemanticTag fittingTag = source.getSemanticTag(sis);

        if (fittingTag != null) {
            fragment.merge(fittingTag);
            return fragment;
        }

        return null;
    }

    /**
     * Check whether a dimension of a SharkCS is any.
     *
     * @param source
     * @param dim
     * @return
     */
    public static boolean isAny(ASIPSpace source, int dim) {
        STSet dimension;

        switch (dim) {
            case ASIPSpace.DIM_TOPIC:
                return SharkCSAlgebra.isAny(source.getTopics());

            case ASIPSpace.DIM_TYPE:
                return SharkCSAlgebra.isAny(source.getTypes());

            case ASIPSpace.DIM_APPROVERS:
                return SharkCSAlgebra.isAny(source.getApprovers());

            case ASIPSpace.DIM_SENDER:
                return SharkCSAlgebra.isAny(source.getSender());

            case ASIPSpace.DIM_RECEIVER:
                return SharkCSAlgebra.isAny(source.getReceivers());

            case ASIPSpace.DIM_LOCATION:
                return SharkCSAlgebra.isAny(source.getLocations());

            case ASIPSpace.DIM_TIME:
                return SharkCSAlgebra.isAny(source.getTimes());

            case ASIPSpace.DIM_DIRECTION:
                return (source.getDirection() == ASIPSpace.DIRECTION_INOUT);
        }

        return false;
    }

    public static boolean isAny(STSet set) {
        if (set == null || set.isEmpty()) return true;

        try {
            // iterate and look for any tag
            Iterator<SemanticTag> stTags = set.stTags();
            while (stTags != null && stTags.hasNext()) {
                SemanticTag tag = stTags.next();
                if (tag.isAny()) {
                    return true;
                }
            }

        } catch (SharkKBException ex) {
            L.e("cannot iterate set"); // TODO
        }
        return false;
    }

    public static boolean isAny(SemanticTag tag) {
        if (tag == null) return true;

        String[] si = tag.getSI();

        if (si == null || si.length == 0) return true;

        return SharkCSAlgebra.isAny(si);
    }

    public static boolean isAny(String[] sis) {
        if (sis == null || sis.length == 0) return true;

        for (int i = 0; i < sis.length; i++) {
            if (sis[i] == null) {
                return true;
            }
        }

        return SharkCSAlgebra.identical(sis, ASIPSpace.ANYSI);
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
        if ((set1 == null && set2 == null)
                ||
                (set1 == null && set2 != null && set2.isEmpty())
                ||
                (set2 == null && set1 != null && set1.isEmpty())
                ) {
            return true;
        }


        if ((set1 == null && set2 != null) || (set2 == null && set1 != null)) {
            return false;
        }

        // both are not null
        Enumeration<SemanticTag> tags2 = null;

        // try to find a tag in set 1 that has no identical tag in set 2
        Enumeration<SemanticTag> tags1 = set1.tags();
        if (tags1 != null) {
            while (tags1.hasMoreElements()) {
                SemanticTag tag1 = tags1.nextElement();
                SemanticTag tag2 = set2.getSemanticTag(tag1.getSI());

                if (tag2 == null) {
                    return false;
                }
            }
        } else {
            tags2 = set2.tags();
            if (tags2 == null) {
                return true; // both sets have no tags
            } else {
                return false; // set 1 empty, set 2 not
            }
        }

        tags2 = set2.tags();
        if (tags2 == null) {
            /* actually, this shouldn't happen - if 1 is not empty
             * but 2 is the first test in upper while-loop should fail.
             * Anyway:
             */
            return false; // set 1 wasn't empty
        }

        while (tags2.hasMoreElements()) {
            SemanticTag tag2 = tags2.nextElement();
            SemanticTag tag1 = set1.getSemanticTag(tag2.getSI());

            if (tag1 == null) {
                return false;
            }
        }

        // found all tags in both sets

        return true;
    }

    public static boolean isIn(ASIPSpace source, ASIPSpace checked) throws SharkKBException {
        // dimension
        int sD = source.getDirection();
        int cD = checked.getDirection();

        if (sD != cD) {
            // not same direction - should be false but..

            // what works is this:
            // sd = in/out && cd != nothing
            // and sd != nothing && cd == in/out
            if (!(
                    (sD == ASIPSpace.DIRECTION_INOUT && cD != ASIPSpace.DIRECTION_NOTHING)
                            ||
                            (cD == ASIPSpace.DIRECTION_INOUT && sD != ASIPSpace.DIRECTION_NOTHING)
            )) {
                // no way - that doesn't fit
                return false;
            }
        }

        if (!SharkCSAlgebra.identical(source.getSender(), checked.getSender())) return false;

        if (!SharkCSAlgebra.isIn(source.getTopics(), checked.getTopics())) return false;
        if (!SharkCSAlgebra.isIn(source.getTypes(), checked.getTypes())) return false;
        if (!SharkCSAlgebra.isIn(source.getApprovers(), checked.getApprovers())) return false;
        if (!SharkCSAlgebra.isIn(source.getReceivers(), checked.getReceivers())) return false;
        if (!SharkCSAlgebra.isIn(source.getTimes(), checked.getTimes())) return false;
        if (!SharkCSAlgebra.isIn(source.getLocations(), checked.getLocations())) return false;

        return true;
    }

    public static boolean isIn(STSet source, STSet checked) throws SharkKBException {
        Iterator<SemanticTag> iterator = checked.stTags();
        boolean isIn = true;
        while (iterator.hasNext() && isIn) {
            isIn = SharkCSAlgebra.isIn(source, iterator.next());
        }
        return isIn;
    }

    /**
     * Checks if tag is in source and takes any definition into account.
     *
     * @param source
     * @param tag
     * @return It return true if one of the parameter matches with any or of
     * tag can be found in source.
     */
    public static boolean isIn(STSet source, SemanticTag tag)
            throws SharkKBException {

        // test on any
        if (tag != null && !tag.isAny() && !SharkCSAlgebra.isAny(source)) {
            // not any - try to find ccTag in source
            tag = source.getSemanticTag(tag.getSI());
            if (tag == null) {
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
        if (tSet == null) {
            return true; // means anytime
        }

        Enumeration<TimeSemanticTag> timeTags = tSet.timeTags();
        if (timeTags == null || !timeTags.hasMoreElements()) {
            return true;
        }

        if (tst == null) {
            return false; // target is a limited time frame, so must tag
        }

        while (timeTags.hasMoreElements()) {
            TimeSemanticTag tFrame = timeTags.nextElement();
            if (SharkCSAlgebra.isIn(tFrame, tst)) {
                return true;
            }
        }

        return false;

    }

    /**
     * Return true if probe is not null and fits into the time frame of target.
     * An emoty target is assumed to specify anytime.
     *
     * @param target
     * @param probe
     * @return
     */
    public static boolean isIn(TimeSemanticTag target, TimeSemanticTag probe) {
        if (target == null) {
            return true;
        }
        if (probe == null) {
            return false;
        } // target is not null

        if (probe.getFrom() < target.getFrom()) {
            return false;
        } // earlier

        long probeEnd = probe.getDuration() == TimeSemanticTag.FOREVER ?
                TimeSemanticTag.FOREVER : probe.getFrom() + probe.getDuration();

        long targetEnd = target.getDuration() == TimeSemanticTag.FOREVER ?
                TimeSemanticTag.FOREVER : target.getFrom() + target.getDuration();

        if (probeEnd == targetEnd) {
            return true;
        }

        if (probeEnd > targetEnd) {
            return false;
        }

        return true;
    }

    public static boolean isIn(TimeSemanticTag target, long from, long duration) {
        TimeSemanticTag probe = InMemoSharkKB.createInMemoTimeSemanticTag(from, duration);

        return SharkCSAlgebra.isIn(target, probe);
    }


    private static SemanticTag assimilate(SemanticTag tag, STSet target,
                                          STSet source, boolean learnSTs) throws SharkKBException {

        if (!(target instanceof SemanticNet)) {
            throw new SharkKBException("find shortest path requires non empty semantic networks and an existing start point");
        }

        SemanticNet snTarget = (SemanticNet) target;

        boolean executed = false;
        ShortestPath path = null;
        try {
            path = new ShortestPath(tag, target, source);
            executed = true;
        } catch (SharkKBException e) {
            // something wrong
        }

        if (executed && path.found()) {
            // there is a path

            if (learnSTs) {
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
            if (learnSTs) {
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
        if (sn == null) {
            return;
        }

        Enumeration<SemanticTag> tagEnum = sn.tags();
        if (tagEnum == null) {
            return;
        }
        while (tagEnum.hasMoreElements()) {
            SNSemanticTag sourceTag = (SNSemanticTag) tagEnum.nextElement();

            // check super only
            Enumeration<SNSemanticTag> superTagsEnum = sourceTag.targetTags(SemanticNet.SUPERTAG);
            if (superTagsEnum == null) {
                continue;
            }
            while (superTagsEnum.hasMoreElements()) {
                SNSemanticTag superTarget = superTagsEnum.nextElement();

                // just set predicate - duplicates are supressed
                superTarget.setPredicate(SemanticNet.SUBTAG, sourceTag);
            }
        }
    }

    /////////////////////////////////////////////////////////////////////////
    //                             KEP support                             //
    /////////////////////////////////////////////////////////////////////////

    public static SemanticTag createAnyTag() throws SharkKBException {
        return InMemoSharkKB.createInMemoSemanticTag("any", ASIPSpace.ANYURL);
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

    /**
     * Checks if the SemanticNet instance is a correct taxonomy
     * using Tarjan's Strongly Connected Components Algorithm
     * https://en.wikipedia.org/wiki/Tarjan%27s_strongly_connected_components_algorithm
     *
     * @param semanticNet A SemanticNet, probably with some SemanticTags that have some kind of connection
     * @param predicate   The predicate that should be checked.
     * @return true if the connections of Predicate in the SemanticNet
     * form a taxonomy, false if there are circular dependencies between the tags
     */
    public static boolean isTaxonomy(SemanticNet semanticNet, String predicate) throws SharkKBException {

        // Reset
        tComponents = new ArrayList<ArrayList<SNSemanticTag>>();
        tIndex = 0;
        tStack = new Stack<SNSemanticTag>();

        tIndices = new HashMap<SNSemanticTag, Integer>();
        tLowLinks = new HashMap<SNSemanticTag, Integer>();
        tChecked = new HashMap<SNSemanticTag, Boolean>();

        // Return immediately if SemanticNet is empty
        if (semanticNet.size() == 0) {
            return true;
        }

        Iterator<SemanticTag> iter = semanticNet.stTags();
        ArrayList<SNSemanticTag> tags = new ArrayList<SNSemanticTag>();
        while (iter.hasNext()) {
            tags.add((SNSemanticTag) iter.next());
        }

        for (SNSemanticTag tag : tags) {
            if (connectsToItself(tag, predicate)) {
                return false;
            }

            if (!tIndices.containsKey(tag)) {
                strongConnect(tag, predicate);
            }
        }

        // Check if there are strongly connected components
        // that are longer than one item
        int longestListSize = 0;
        for (ArrayList<SNSemanticTag> list : tComponents) {
            if (longestListSize < list.size()) {
                longestListSize = list.size();
            }
        }

        return longestListSize <= 1;
    }

    private static boolean connectsToItself(SNSemanticTag tag, String predicate) {
        Enumeration<SNSemanticTag> subTags = tag.targetTags(predicate);
        if (subTags != null) {
            while (subTags.hasMoreElements()) {
                SNSemanticTag subTag = subTags.nextElement();
                if (subTag.equals(tag)) {
                    return true;
                }
            }
        }
        return false;
    }

    private static void strongConnect(SNSemanticTag tag, String predicate) throws SharkKBException {
        tIndices.put(tag, tIndex);
        tLowLinks.put(tag, tIndex);
        tChecked.put(tag, true);
        tIndex = tIndex + 1;
        tStack.push(tag);

        Enumeration<SNSemanticTag> subTags = tag.targetTags(predicate);
        if (subTags != null) {
            while (subTags.hasMoreElements()) {
                SNSemanticTag w = subTags.nextElement();
                if (!tIndices.containsKey(w)) {
                    strongConnect(w, predicate);
                    tLowLinks.put(tag, Math.min(tLowLinks.get(tag), tLowLinks.get(w)));
                } else if (tChecked.containsKey(w)) {
                    tLowLinks.put(tag, Math.min(tLowLinks.get(tag), tIndices.get(w)));
                }
            }
        }

        if (tLowLinks.get(tag) == tIndices.get(tag)) {
            ArrayList<SNSemanticTag> component = new ArrayList<SNSemanticTag>();
            SNSemanticTag w = null;
            while (w != tag) {
                w = tStack.pop();
                tChecked.put(w, false);
                component.add(w);
            }

            tComponents.add(component);
        }
    }

    /**
     * Checks if all the connections of predicate in the SemanticNet instance
     * form a transitive net.
     *
     * @param semanticNet A SemanticNet, probably with some SemanticTags that have some kind of connection
     * @param predicate   The predicate that should be checked.
     * @return true if all connections of the Predicate are transitive, false if they are not
     */
    public static boolean isTransitive(SemanticNet semanticNet, String predicate) throws SharkKBException {
        tChecked = new HashMap<SNSemanticTag, Boolean>();

        Iterator<SemanticTag> iter = semanticNet.stTags();

        while (iter.hasNext()) {
            SNSemanticTag tag = (SNSemanticTag) iter.next();
            Enumeration<SNSemanticTag> subTags = tag.targetTags(predicate);

            if (!tChecked.containsKey(tag) && subTags != null) {
                ArrayList<SNSemanticTag> rootSubTags = new ArrayList<SNSemanticTag>();
                while (subTags.hasMoreElements()) {
                    rootSubTags.add((SNSemanticTag) subTags.nextElement());
                }

                if (!transitive(tag, rootSubTags, predicate)) {
                    return false;
                }
            }
        }

        return true;
    }

    private static boolean transitive(SNSemanticTag tag, ArrayList<SNSemanticTag> rootSubTags, String predicate) {
        tChecked.put(tag, true);

        Enumeration<SNSemanticTag> subTags = tag.targetTags(predicate);
        if (subTags != null) {
            while (subTags.hasMoreElements()) {
                SNSemanticTag w = subTags.nextElement();

                // w is not in the root's sub tags
                if (!rootSubTags.contains(w)) {
                    return false;
                }

                // w has no further children
                if (w.targetTags(predicate) == null) {
                    return true;
                }

                return transitive(w, rootSubTags, predicate);
            }
        }
        return true;
    }

    /**
     * @param semanticNet A SemanticNet, probably with some SemanticTags that have some kind of connection
     * @param predicate   The predicate that should be checked.
     * @return true if all connections with the predicate are symmetric
     */
    public static boolean isSymmetric(SemanticNet semanticNet, String predicate) throws SharkKBException {
        Iterator<SemanticTag> iter = semanticNet.stTags();

        while (iter.hasNext()) {
            SNSemanticTag tag = (SNSemanticTag) iter.next();

            Enumeration<SNSemanticTag> subTags = tag.targetTags(predicate);

            if (subTags != null) {
                while (subTags.hasMoreElements()) {
                    Enumeration<SNSemanticTag> deeperSubTags = subTags.nextElement().targetTags(predicate);

                    if (deeperSubTags != null) {
                        ArrayList<SNSemanticTag> deeperSubTagsList = new ArrayList<>();
                        while (deeperSubTags.hasMoreElements()) {
                            deeperSubTagsList.add(deeperSubTags.nextElement());
                        }

                        if (!deeperSubTagsList.contains(tag)) {
                            return false;
                        }
                    }
                }
            }
        }
        return true;
    }

    /**
     * Changes the SemanticNet to make all the connections of predicate symmetric,
     * that means that every connection that previously only went from A -> B, now also
     * goes from B -> A.
     * (The SemanticNet is then guaranteed to pass .isSymmetric for the same predicate.)
     *
     * @param semanticNet A SemanticNet, probably with some SemanticTags that have some kind of connection
     * @param predicate   The predicate that should be checked.
     */
    public static void makeSymmetric(SemanticNet semanticNet, String predicate) throws SharkKBException {
        Iterator<SemanticTag> iter = semanticNet.stTags();

        while (iter.hasNext()) {
            SNSemanticTag tag = (SNSemanticTag) iter.next();
            Enumeration<SNSemanticTag> subTags = tag.targetTags(predicate);

            if (subTags != null) {
                while (subTags.hasMoreElements()) {
                    subTags.nextElement().setPredicate(predicate, tag);
                }
            }
        }
    }

    /**
     * Changes the SemanticNet to make all the connections of predicate transitive.
     * (The SemanticNet is then guaranteed to pass .isTransitive for the same predicate.)
     *
     * @param semanticNet A SemanticNet, probably with some SemanticTags that have some kind of connection
     * @param predicate   The predicate that should be checked.
     */
    public static void makeTransitive(SemanticNet semanticNet, String predicate) throws SharkKBException {
        tChecked = new HashMap<>();

        Iterator<SemanticTag> iter = semanticNet.stTags();

        while (iter.hasNext()) {
            SNSemanticTag tag = (SNSemanticTag) iter.next();
            Enumeration<SNSemanticTag> subTags = tag.targetTags(predicate);

            if (subTags != null) {
                ArrayList<SNSemanticTag> rootSubTags = new ArrayList<>();
                while (subTags.hasMoreElements()) {
                    rootSubTags.add((SNSemanticTag) subTags.nextElement());
                }

                makeTransitiveHelper(tag, tag, rootSubTags, predicate);
            }
        }
    }

    private static void makeTransitiveHelper(SNSemanticTag tag, SNSemanticTag rootTag, ArrayList<SNSemanticTag> rootSubTags, String predicate) {
        Enumeration<SNSemanticTag> subTags = tag.targetTags(predicate);
        if (subTags != null) {
            while (subTags.hasMoreElements()) {
                SNSemanticTag w = subTags.nextElement();

                // w is not in the root's sub tags
                if (!rootSubTags.contains(w)) {
                    rootTag.setPredicate(predicate, w);
                }

                // w has no further children
                if (w.targetTags(predicate) == null) {
                    return;
                }

                makeTransitiveHelper(w, rootTag, rootSubTags, predicate);
            }
        }
    }
}
