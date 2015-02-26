package net.sharkfw.knowledgeBase.inmemory;

import java.util.*;
import net.sharkfw.knowledgeBase.*;
import net.sharkfw.system.Iterator2Enumeration;
import net.sharkfw.system.L;
import net.sharkfw.system.Util;

/**
 * In-Memory implementation of an SNSemanticTag and TXSemanticTag.
 * This class holds references to other SNSemanticTags along with the
 * association types.
 * 
 * @author thsc
 */
@SuppressWarnings("unchecked")
public class InMemo_SN_TX_SemanticTag extends InMemoSemanticTag
                                implements SNSemanticTag, TXSemanticTag {
    
    // targets means: referenced tags with a given predicate
    private HashMap<String, HashSet<SNSemanticTag>> targets;
    
    // sources means: tag that reference this target with a predicate
    private HashMap<String, HashSet<SNSemanticTag>> sources;
    
    private boolean refreshed = false;
    
    public InMemo_SN_TX_SemanticTag(String name, String[] si) {
        super(name, si);
    }
    
    /**
    /**
     * Return an <code>Enumeration</code> of strings. Each string representing
     * a predicate type in which this tag is involved as source
     * 
     * @return Return all predicate names which are present currently.
     * Null is returned if this tag doesn't reference any target tag.
     */
    @Override
    public Enumeration<String> predicateNames() {
        this.refreshPredicates();
        if(this.targets == null) return null;
        
        // there is at least one target
        return new Iterator2Enumeration(this.targets.keySet().iterator());
    }
    
    /**
     * Return an <code>Enumeration</code> of strings. Each string representing
     * a predicate type in which this tag is involved as target.
     * 
     * Note: This is a kind of reverse reference. Predicates are seen as
     * directed arcs. A tags points to another. It is the source in this case. 
     * The other is the target.
     * 
     * This methode return predicate names in which this tags plays the role of
     * the target.
     * 
     * @return An <code>Enumeration</code> of strings containing predicate types
     */
    @Override
    public Enumeration<String> targetPredicateNames() {
        this.refreshPredicates();
        if(this.sources == null) return null;
        
        // there is at least one target
        return new Iterator2Enumeration(this.sources.keySet().iterator());
    }
    
    
    /**
     * This tag can play the source role in a predicate. This methode
     * return all tags that play the target role in the same predicate.

     * @param predicateName predicate type name
     * @return enumeration or null, if there is no such tag
     */
    @SuppressWarnings({ "rawtypes" })
    @Override
    public Enumeration <SNSemanticTag> targetTags(String predicateName) {
        this.refreshPredicates();
        if(this.targets == null) return null;
        
        HashSet tags = this.targets.get(predicateName);
        
        if(tags != null) return new Iterator2Enumeration(tags.iterator());
        
        return null;
    }
    
    @Override
    public void addSI(String addSI) throws SharkKBException {
        /* that a kind of trick: super implementation creates an new array 
         * when adding this si is ok. We check this.
         */
        
        String[] sis = this.getSI();
        super.addSI(addSI);
        
        if(sis != this.getSI()) {
            // something changed
            this.updateSIInPredicates();
        }
    }
    
    @Override
    public void removeSI(String deleteSI) throws SharkKBException {
        /* that a kind of trick: super implementation creates an new array 
         * when adding this si is ok. We check this.
         */
        
        String[] sis = this.getSI();
        super.removeSI(deleteSI);
        
        if(sis != this.getSI()) {
            // something changed
            this.updateSIInPredicates();
        }
    }
    
    /**
     * Si of a tag can be changed. But just one SI can be changed.
     * Thus, a tag never looses its identity. Nevertheless, each 
     * change must be persistent even to referencing tags.
     * 
     * This problem only arises with persistency. An in memo implementation
     * works with object references - st identity is a gift of object
     * identity.
     * 
     * Thus, if there is a persistency - each referenced tag must just
     * write changed si set after each si change. That's done with this method
     */
    private void updateSIInPredicates() {
        // targets
        if(this.targets != null) {
            Collection<HashSet<SNSemanticTag>> targetSets = this.targets.values();
            
            if( targetSets != null ) {
                this.forcePredicateWriting(targetSets.iterator());
            }
        }

        // sources
        if(this.sources != null) {
            Collection<HashSet<SNSemanticTag>> sourceSets = this.sources.values();
            
            if(sourceSets != null) {
                this.forcePredicateWriting(sourceSets.iterator());
            }
        }
    }
    
    private void forcePredicateWriting(Iterator<HashSet<SNSemanticTag>> setIter) {
        
        while(setIter.hasNext()) {
            HashSet<SNSemanticTag> tagSet = setIter.next();
            Iterator<SNSemanticTag> tagSetIter = tagSet.iterator();
            while(tagSetIter.hasNext()) {
                SNSemanticTag tag = tagSetIter.next();
                
                if(tag instanceof AbstractSemanticTag) {
                    AbstractSemanticTag inTag = (AbstractSemanticTag) tag;
                    
                    inTag.persist();
                    
                } else {
                    L.w("There are others than InMemo_SN_TX_SemanticTag tags referenced - might get problems with si change synchronisation", this);
                }
            }
        }
    }
    
    /**
     * This tag can play the target role in a predicate. This methode
     * return all tags that play the source role in the same predicate.

     * @param predicateName predicate type name
     * @return enumeration or null, if there is no such tag
     */
    @SuppressWarnings({ "rawtypes" })
    @Override
    public Enumeration <SNSemanticTag> sourceTags(String predicateName) {
        this.refreshPredicates();
        if(this.sources == null) return null;
        
        HashSet tags = this.sources.get(predicateName);
        
        if(tags != null) return new Iterator2Enumeration(tags.iterator());
        
        return null;
    }

    /**
     * 
     * @param type
     * @param target 
     */
    @Override
    public void setPredicate(String type, SNSemanticTag target) {
        this.refreshPredicates();
        this.setPredicate(type, (SemanticTag) target);
        this.persist();
    }
    
    /**
     * Removes all predicates of this type - handle with care!
     * @param type 
     */
    public void removePredicate(String type) {
        if(this.targets == null) {
            // nothings exists - nothing todo
            return;
        }

        this.targets.remove(type);
    }
    
    /**
     * Removes an association. If target is null, all associations of
     * this type are removed!
     * 
     * @param type
     * @param target if null - all associations of this type are removed
     */
    @SuppressWarnings({ "unused", "rawtypes" })
    private void setPredicate(String type, SemanticTag target) {
        if(target == null) {
            this.removePredicate(type);
        }
        
        boolean hashSetAlreadyExists = true;
        HashSet targetHashSet = null;
        
        if(this.targets == null) {
            hashSetAlreadyExists = false;
            this.targets = new HashMap<String, HashSet<SNSemanticTag>>();
        } else {
            targetHashSet = this.targets.get(type);
        }

        if(targetHashSet == null) {
            hashSetAlreadyExists = false;
            targetHashSet = new HashSet<SNSemanticTag>();
            this.targets.put(type, targetHashSet);
        }
        
        // structures to store relations are no established

        // add target - hash set prevents duplicates
        targetHashSet.add(target);
        
        // inform target to let it make a cross reference if it is of this class
        if(target instanceof InMemo_SN_TX_SemanticTag) {
            ((InMemo_SN_TX_SemanticTag) target).addSourcePredicate(type, this);
        }
    }
    
    /**
     * internal use only - this tag was used as target for a predicate.
     * This method is called by the source tag
     * @param type
     * @param source 
     */
    @SuppressWarnings({ "rawtypes" })
    private void addSourcePredicate(String type, SNSemanticTag source) {
        this.refreshPredicates();
        
        /* I believe in my own code and don't check whether this reference 
         already exists */
        
        HashSet sourceTags = null;
        if(this.sources == null) {
            this.sources = new HashMap<String, HashSet<SNSemanticTag>>();
        } else {
            sourceTags = this.sources.get(type);
        }
        
        if(sourceTags == null) {
            sourceTags = new HashSet<SNSemanticTag>();
            this.sources.put(type, sourceTags);
        }
        
        // add 
        sourceTags.add(source);
        
        // remember
        this.persist();
    }

    /**
     * Remove a tag from the Vector of associated tags for the given type.
     *
     * @param type The predicate type.
     * @param target The tag to be removed from this association.
     */
    @Override
    public void removePredicate(String type, SNSemanticTag target) {
        this.refreshPredicates();
        if(this.targets == null) {
            return;
        }
        
        HashSet<SNSemanticTag> targetTags = this.targets.get(type);
        
        if(targetTags != null) {
            targetTags.remove(target);
            
            this.removePropertyEntry(TARGET_PREFIX, type, target);
            
            if(targetTags.isEmpty()) {
                this.targets.remove(type);
            }
        }
        
        // inform target to let it delete cross reference if it is a InMemoSemanticTag
        if(target instanceof InMemo_SN_TX_SemanticTag) {
            ((InMemo_SN_TX_SemanticTag) target).removeSourcePredicate(type, this);
        }
        
        this.persist();
    }
    
    private void removePropertyEntry(String prefix, String type,
            SemanticTag tag) {
        
        String propertyNamePrefix = prefix + "_" + type;
        
        // first - find number of entries
        String value = this.getSystemProperty(propertyNamePrefix + "_#");
        
        int maxNumber = 10; // just a guess
        
        if(value != null) {
            maxNumber = Integer.parseInt(value);
        } // else: very strange - should not happen.... use our guess
        
        // iterate entries
        for(int i = 0; i < maxNumber; i++) {
            String pName = propertyNamePrefix + "_" + String.valueOf(i);
            
            value = this.getSystemProperty(pName);
            
            if(value != null) {
                String[] sis = Util.string2array(value);
                
                if(SharkCSAlgebra.identical(sis, tag.getSI())) {
                    // entry found - remove it
                    this.setSystemProperty(pName, null);
                    
                    // remove maxNumber entry - will be set during persist()
                    this.setSystemProperty(propertyNamePrefix + "_#", null);
                    // done
                    return;
                }
            }
        }
    }

    @SuppressWarnings("rawtypes")
    private void removeSourcePredicate(String type, InMemo_SN_TX_SemanticTag source) {
        if(this.sources == null) return;
        
        HashSet sourceTags = this.sources.get(type);
        if(sourceTags == null) return;
        
        sourceTags.remove(source);
        
        this.removePropertyEntry(SOURCE_PREFIX, type, source);
        
        if(sourceTags.isEmpty()) {
            this.sources.remove(type);
        }
        
        // remember
        this.persist();
    }
    
    @Override
    public void merge(SNSemanticTag toMerge) {
        this.localMerge(toMerge);
    }
    

    @SuppressWarnings("unused")
    private void localMerge(SemanticTag toMerge) {
        // merge tags only
        super.merge(toMerge);
        
        SNSemanticTag sntxToMerge = null;
        
        // more than a ST - should be the case
        if(toMerge instanceof SNSemanticTag || 
                toMerge instanceof InMemo_SN_TX_SemanticTag ) {
            sntxToMerge = (SNSemanticTag)toMerge;
        } else {
            return;
        }
        
        // now copy target predicates
        Enumeration<String> targetTypeNames = sntxToMerge.predicateNames();
        if(targetTypeNames != null) {
            while(targetTypeNames.hasMoreElements()) {
                String type = targetTypeNames.nextElement();
                
                Enumeration<SNSemanticTag> tagEnum = sntxToMerge.targetTags(type);
                
                if(tagEnum != null) {
                    while(tagEnum.hasMoreElements()) {
                        this.setPredicate(type, tagEnum.nextElement());
                    }
                }
            }
        }
        
        // again in which toMerge is target
        Enumeration<String> sourceTypeNames = sntxToMerge.targetPredicateNames();
        if(targetTypeNames != null) {
            while(targetTypeNames.hasMoreElements()) {
                String type = targetTypeNames.nextElement();
                
                Enumeration<SNSemanticTag> tagEnum = sntxToMerge.sourceTags(type);
                
                if(tagEnum != null) {
                    while(tagEnum.hasMoreElements()) {
                        // tell the source, that this is also a target now
                        SNSemanticTag source = tagEnum.nextElement();
                        source.setPredicate(type, this);
                    }
                }
            }
        }
    }

/****************************************************************************
 *                        TXSemanticTag methods                             *
 ****************************************************************************/
    /* 
     * It works exaclty like a semantic tag but just one predicate type
     * is allowed - super relation to another tag. The sub relation is 
     * interpreted as opposite way of sub.
     */
    
    @SuppressWarnings({ "rawtypes" })
    @Override
    public Enumeration<SemanticTag> subTags() {
        this.refreshPredicates();
        
        /* find any tag to in which this tag plays target role in super 
           predicate 
        */
        
        Enumeration e = this.sourceTags(SemanticNet.SUPERTAG);
        
        /* e is enumeration of SNSemanticTag which is even more specific than
        SemanticTag. This trick is ok */
        
        return e;
    }

    @SuppressWarnings("rawtypes")
    @Override
    public TXSemanticTag getSuperTag() {
        this.refreshPredicates();
        Enumeration e = this.targetTags(SemanticNet.SUPERTAG);
        
        if(e == null) return null;
        if(!e.hasMoreElements()) return null;
        
        try {
            TXSemanticTag result = (TXSemanticTag) e.nextElement();
            return result;
        }
        catch(ClassCastException cce) {
            // strange - hwo could a non ST find its way into this collection?
            L.e("a non TXSemanticTag is supertag in a Taxonomy:" + cce.getMessage(), this);
        }
        
        return null;
    }
    
    @SuppressWarnings({ "rawtypes" })
    @Override
    public Enumeration<TXSemanticTag> getSubTags() {
        this.refreshPredicates();
        Enumeration e = this.sourceTags(SemanticNet.SUPERTAG);
        return e;
    }

    @Override
    public void move(TXSemanticTag supertag) {
        this.refreshPredicates();
        
        // is there already another super tag? if so - remove it.
        TXSemanticTag oldSuperTag = this.getSuperTag();
        this.removePredicate(SemanticNet.SUPERTAG);
        if(oldSuperTag instanceof InMemo_SN_TX_SemanticTag) {
            ((InMemo_SN_TX_SemanticTag) oldSuperTag).removePredicate(SemanticNet.SUBTAG, this);
        }
        
        // supertag is my new super tag. Remember by means of predicate.
        this.setPredicate(SemanticNet.SUPERTAG, supertag);
        // are we in this implementation with super tag
        if(supertag instanceof InMemo_SN_TX_SemanticTag) {
            ((InMemo_SN_TX_SemanticTag)supertag).setPredicate(SemanticNet.SUBTAG, this);
        }
       
    }

    @Override
    public void merge(TXSemanticTag toMerge) {
        this.refreshPredicates();
        this.localMerge(toMerge);
    }
    
    public static final String SOURCE_PREFIX = "SN_TAG_SRC";
    public static final String TARGET_PREFIX = "SN_TAG_TRG";
    
    @Override
    public void persist() {
        super.persist();

        // sources
        if(this.sources != null) {
            if (this.sources.keySet().size() > 0) {
                this.writePredicates(SOURCE_PREFIX, this.sources);
            } else {
                // remove property 
                this.setSystemProperty(SOURCE_PREFIX, null);
            }
        }
        
        // targets
        if(this.targets != null) {
            if (this.targets.keySet().size() > 0) {
                this.writePredicates(TARGET_PREFIX, this.targets);
            }
            else {
                this.setSystemProperty(TARGET_PREFIX, null);
            }
        }
    }
    
    private void writePredicates(String prefix, 
            HashMap<String, HashSet<SNSemanticTag>> map ) {
        
        Iterator<String> predicateIter = map.keySet().iterator();
        Vector<String> p = new Vector<String>();
        
        while(predicateIter.hasNext()) {
            String predicate = predicateIter.next();
            p.add(predicate);

            String propertyNamePrefix = prefix + "_" + predicate;

            // iterate referenced tags
            Iterator<SNSemanticTag> snIter = map.get(predicate).iterator();

            int snTagNumber = 0;
            while(snIter.hasNext()) {
                SNSemanticTag snTag = snIter.next();
                if (snTag == null) {
                	continue;
                }

                // create property name
                String pName = propertyNamePrefix + "_" + String.valueOf(snTagNumber++);

                // create value
                String siString = Util.array2string(snTag.getSI());

                // write property
                this.setSystemProperty(pName, siString);
            }
            
            // remember number of written properties
            this.setSystemProperty(propertyNamePrefix + "_#", 
                    Integer.toString(snTagNumber));
        }

        // write predicate names

        // create value
        String predicateNames = Util.enumeration2String(p.elements(), "|");

        // write property
        this.setSystemProperty(prefix, predicateNames);
    } 
    
    /**
     * TODO TODO
     * @param prefix
     * @param map 
     */
    @SuppressWarnings({ "rawtypes" })
    private void readPredicates(String prefix, 
            HashMap<String, HashSet<SNSemanticTag>> map,
            InMemoGenericTagStorage home) {
        
        // read prefix names
        String pNamesValue = this.getSystemProperty(prefix);
        
        if(pNamesValue == null) { return; }
        
        // split names
        Vector p = Util.string2Vector(pNamesValue, "|");
        
        Enumeration<String> pNames = p.elements();
        while(pNames.hasMoreElements()) {
            String pName = pNames.nextElement();
            
            // create set to store sis
            HashSet<SNSemanticTag> referencedTags = new HashSet();
            
            // construct property name
            String propNamePrefix = prefix + "_" + pName;
            
            // iterate
            int snNumber = 0;
            
            // get max counter 
            String value = this.getSystemProperty(propNamePrefix + "_#");
            int numberPredicates = 10;
            if(value != null) {
                numberPredicates = Integer.parseInt(value);
            }
            
            String siString = null;
            while(snNumber < numberPredicates) {
                String propName = propNamePrefix + "_" + String.valueOf(snNumber++);
                
                 siString = this.getSystemProperty(propName);
                
                if(siString != null) {
                    // deconstruct string
                    String[] sis  = Util.string2array(siString);
                    
                    // dereference tag
                    try {
                        SemanticTag tag = home.getSemanticTag(sis);
                        
                        try {
                            SNSemanticTag snTag = (SNSemanticTag) tag;
                            // remember
                            referencedTags.add(snTag);
                        }
                        catch(ClassCastException cce) {
                            // shouldn't happen
                           L.d("restored wrong semantic tag type from persistent storage: " + p, this);
                           continue;
                        }
                    }
                    catch(SharkKBException e) {
                        // cannot fix that problem
                        L.l("couldn't find refernced tag in memory while refreshing references from persistent storage", this);
                    }
                }
            }
            
            // hang in reference tag - if any
            if(!referencedTags.isEmpty()) {
                map.put(pName, referencedTags);
            }
        }
    }
    
    @Override
    public void refreshStatus() {
        super.refreshStatus();
    }
    
    @SuppressWarnings({ "rawtypes" })
    private void refreshPredicates() {
        if(this.refreshed) { return; }
        
        if(this.storage == null) {
            // there is no persistent storage
            this.refreshed = true;
            return;
        }

        this.refreshed = true;
        
        this.sources = new HashMap();
        this.readPredicates(SOURCE_PREFIX, this.sources, this.storage);

        this.targets = new HashMap();
        this.readPredicates(TARGET_PREFIX, this.targets, this.storage);
    }
    
    @SuppressWarnings("rawtypes")
    private InMemoGenericTagStorage storage;
    
    @SuppressWarnings("rawtypes")
    public InMemo_SN_TX_SemanticTag(SystemPropertyHolder persistentHolder, InMemoGenericTagStorage storage) {
        super(persistentHolder);
        
        this.storage = storage;
    }
}
