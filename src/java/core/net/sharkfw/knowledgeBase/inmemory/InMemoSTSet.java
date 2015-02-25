package net.sharkfw.knowledgeBase.inmemory;

import net.sharkfw.system.Util;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.sharkfw.knowledgeBase.*;

/**
 * <p>
 * A new implementation of STSet that uses a Hashtable from the KB
 * as a resource for managing tags.
 *</p>
 * <p>
 * On startup it generates a si2id table to make it easier to retrieve
 * tags using their sis.
 *</p>
 * <p>
 * Implementing conventional STSet interface for internal use and
 * PlainSTSet for outside use by developers.
 * </p>
 * @author mfi, thsc
 */
public class InMemoSTSet implements STSet {

    @SuppressWarnings("rawtypes")
    private InMemoGenericTagStorage storage;
    
    /**
     * creates a semantic tag set by copying tags
     */
    InMemoSTSet(Enumeration<SemanticTag> initialTagEnum) throws SharkKBException {
        this();
        while(initialTagEnum.hasMoreElements()) {
            this.merge(initialTagEnum.nextElement());
        }
    }

    /**
     * That's the prefered way to create an empty stand alone semantic tag set.
     */
    InMemoSTSet() {
        this.storage = new InMemoGenericTagStorage<SemanticTag>();
    }

    /**
     * 
     * @return number of tags in this set
     */
    @Override
    public int size() {
        return this.storage.number();
    }

    
    @SuppressWarnings("rawtypes")
    public InMemoGenericTagStorage getTagStorage() {
        return this.storage;
    }

    /**
     * creates a new st set with same tags - be careful
     * @param storage 
     */
    @SuppressWarnings("rawtypes")
    public InMemoSTSet(InMemoGenericTagStorage storage) {
        this.storage = storage;
    }

    /**
     * Returns a semantic tag described by (one of its) subject identifier.
     * @param si
     * @return matching semantic tag or null if no tag with subject identifier
     * exists in this ST set.
     */
    @Override
    public SemanticTag getSemanticTag(String si) throws SharkKBException {
        return this.storage.getSemanticTag(si);
    }

    @Override
    public SemanticTag getSemanticTag(String[] si)  throws SharkKBException {
        return this.storage.getSemanticTag(si);
    }

    @SuppressWarnings("unchecked")
    @Override
    public Enumeration<SemanticTag> tags() {
        return this.storage.tags();
    }

    @Override
    public void setEnumerateHiddenTags(boolean hide) {
        this.storage.setEnumerateHiddenTags(hide);
    }
    
    /**
     * Merges another set into this set.
     * If an two identical tags are found in both set they will be merged
     * as well. 
     * 
     * Merging means: The name of the tag in <i>this</i> set remains unchanged.
     * Subject identifier are added. It uses the merge operation 
     * of AbstractSemanticTag.
     * 
     * @param set2merge 
     * @see AbstractSemanticTag
     */
    @SuppressWarnings("unused")
    @Override
    public void merge(STSet remoteSet) throws SharkKBException {
        // iterate 
        if(remoteSet == null) return;
        
        Enumeration<SemanticTag> remoteEnum = remoteSet.tags();
        boolean stMerged = false;

        while(remoteEnum.hasMoreElements()) {
            SemanticTag remoteST = remoteEnum.nextElement();
            
            this.merge(remoteST);
        }
    }
    
    /**
     * copies name, SIs and properties to the target set
     * @param targetSet
     * @param source 
     */
    @SuppressWarnings("unchecked")
    @Override
    public SemanticTag merge(SemanticTag source) throws SharkKBException {
        return this.getTagStorage().merge(source);
    }
    
    /**
     * Creates a in memory copy of given source tag. 
     * 
     * Note: This copy isn't part of any semantic tag set. It is just
     * a single non-persistent object in memory after creation.
     * 
     * @param source original tag to create a copy from
     * @return 
     */
    public static SemanticTag copySemanticTag(SemanticTag source) {
        SemanticTag copy = new InMemoSemanticTag(source.getName(), source.getSI());
        Util.mergeProperties(copy, source);
        
        return copy;
    }
    /**
     * Adds this semantic tag to the set. Note: The object itself is
     * added, no copy is made. It shouldn't be changed after adding.
     * Create a copy if required in the first step.
     * 
     * @param tag Tag to add.
     * @throws net.sharkfw.knowledgeBase.SharkKBException
     */
    @SuppressWarnings("unchecked")
    public void add(SemanticTag tag) throws SharkKBException {
        this.storage.add(tag);
        
        this.notifyCreated(tag);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void removeSemanticTag(SemanticTag tag) {
        this.storage.removeSemanticTag(tag);
        
        this.notifyRemoved(tag);
    }

    /**
     * A tag is created and added to the set
     * @param name Tag name
     * @param sis Subject Identifier list
     * @return
     * @throws SharkKBException 
     */
    @Override
    public SemanticTag createSemanticTag(String name, String[] sis) throws SharkKBException {
        
        SemanticTag tag = this.getSemanticTag(sis);
        if(tag != null) {
            return tag;
        }
        
        tag = new InMemoSemanticTag(name, sis, this.storage);
        
        this.add(tag);
        
        return tag;
    }

    /**
     * A tag is created and added to the set
     * @param name Tag name
     * @param sis Subject Identifier
     * @return
     * @throws SharkKBException 
     */
    @Override
    public SemanticTag createSemanticTag(String name, String si) throws SharkKBException {
        return this.createSemanticTag(name, new String[]{si});
    }

    /**
     * This methods checks whether a tag exists that is identical to anchor.
     * If so, a new STSet is created that contains a copy of the concept in
     * this set. 
     * 
     * Note1: The resulting set contains only a single tag which is a copy
     * of the fitting tag in this set. The anchor remains unchanged. The anchor 
     * is also not merged with the found tag.
     * 
     * @param anchor
     * @return set containing a tag or null
     */
    @Override
    public STSet fragment(SemanticTag anchor) throws SharkKBException {
        STSet fragment = new InMemoSTSet();
        
        return SharkCSAlgebra.fragment(fragment, this, anchor);
    }
    
    /**
     * Fragmentation Parameter are ignored in this implementation. It makes
     * no sense in plain semantic tag sets.
     * 
     * @param anchor
     * @param fp
     * @return
     * @throws SharkKBException 
     */
    @Override
    public STSet fragment(SemanticTag anchor, FragmentationParameter fp) throws SharkKBException {
        return this.fragment(anchor);
    }
    
    private FragmentationParameter defaultFP;
    
    /**
    * Each set has build fragmentation parameter. They can be retrieved.
    * @return default fragmentation parameter
    */
    @Override
    public FragmentationParameter getDefaultFP() {
        if(this.defaultFP == null) {
            // standard FP
            this.defaultFP = new FragmentationParameter(false, true, 2);
        }
        return this.defaultFP;
    }

    /**
    * Each set has build fragmentation parameter. They can be set with this 
    * methode.
    */
    @Override
    public void setDefaultFP(FragmentationParameter fp) {
        this.defaultFP = fp;
    }
    
    /**
     * 
     * @param anchorSet enumeration of semantic tags denoting the context
     * @param fp no used in this implementation
     * @return fragment of this set or null of anchorSet is empty. An empty 
     * set can also be returned.
     */
    @Override
    public STSet contextualize(Enumeration<SemanticTag> anchorSet, 
                                FragmentationParameter fp) throws SharkKBException {
        
        if(anchorSet == null || !anchorSet.hasMoreElements()) return null;
        
        InMemoSTSet fragment = new InMemoSTSet();
        
        return SharkCSAlgebra.contextualize(fragment, this, anchorSet);
    }
    
    @Override
    public STSet contextualize(Enumeration<SemanticTag> anchorSet) throws SharkKBException {
        return this.contextualize(anchorSet, null);
    }
    
    @Override
    public STSet contextualize(STSet context, FragmentationParameter fp) throws SharkKBException {
        return this.contextualize(context.tags(), fp);
    }

    @Override
    public STSet contextualize(STSet context) throws SharkKBException {
        return this.contextualize(context, null);
        
    }
    
    /*****************************************************
     *               st set listener                     * 
     *****************************************************/
    private ArrayList<STSetListener> listener;
    
    // make a late binding - I guess most apps wont use ST listener.
    private void checkInit() {
        if(this.listener == null) {
            this.listener = new ArrayList<STSetListener>();
        }
    }
    
    /**
     * Set a listener to listen for changes in this stset.
     * Each STSet can have exactly one listener (usually the SharkKB).
     *
     * @param listen The listener to be notified of changes.
     */
    @Override
    public void addListener(STSetListener listen) {
        this.checkInit();
        this.listener.add(listen);
    }

    /**
     * Remove a listener.
     * Will set the reference to the listener to <code>null</code>.
     */
    @Override
    public void removeListener(STSetListener listener) {
        if(this.listener != null) {
            this.listener.remove(listener);
        }
    }
    
    protected void notifyRemoved(SemanticTag st) {
        this.checkInit();
        Iterator<STSetListener> listenerIter = this.listener.iterator();
        
        while(listenerIter.hasNext()) {
            STSetListener nextListener = listenerIter.next();
            nextListener.semanticTagRemoved(st, this);
        }
    }

    protected void notifyCreated(SemanticTag st) {
        this.checkInit();
        Iterator<STSetListener> listenerIter = this.listener.iterator();
        
        while(listenerIter.hasNext()) {
            STSetListener nextListener = listenerIter.next();
            nextListener.semanticTagCreated(st, this);
        }
    }
    
    @Override
    public boolean isEmpty() {
        
        if(this.tags() == null) return true;
        
        if(!this.tags().hasMoreElements()) return true;
        
        return false;
    }

    @Override
    public Iterator<SemanticTag> getSemanticTagByName(String pattern) throws SharkKBException {
        return InMemoSTSet.getSemanticTagByName(this, pattern);
    }
    
    static Iterator<SemanticTag> getSemanticTagByName(STSet source, String pattern) throws SharkKBException {
        ArrayList<SemanticTag> result = new ArrayList<>();
        
        Enumeration<SemanticTag> tags = source.tags();
        if(tags != null) {
            try {
                Pattern p = Pattern.compile(pattern);

                while(tags.hasMoreElements()) {
                    SemanticTag st = tags.nextElement();

                    String name = st.getName();

                    if(name != null) {
                        Matcher matcher = p.matcher(name);
                        if(matcher.matches()) {
                            result.add(st);
                        }
                    }
                }
            }
            catch(IllegalArgumentException e) {
                // thrown if patter is in wrong shape
                throw new SharkKBException(e.getClass().getName() +  " catched: " + e.getMessage());
            }
        }
        return result.iterator();
    }
}

