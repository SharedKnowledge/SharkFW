package net.sharkfw.knowledgeBase;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;

import net.sharkfw.knowledgeBase.geom.SharkGeometry;
import net.sharkfw.knowledgeBase.inmemory.InMemoGenericTagStorage;
import net.sharkfw.knowledgeBase.inmemory.InMemoSTSet;
import net.sharkfw.knowledgeBase.inmemory.InMemoSpatialSemanticTag;

/**
 *
 * @author thsc
 */
public abstract class AbstractSTSet implements STSet {
    private FragmentationParameter defaultFP;
    public InMemoGenericTagStorage storage;

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
     * Merges another set into this set.
     * If an two identical tags are found in both set they will be merged
     * as well. 
     * 
     * Merging means: The name of the tag in <i>this</i> set remains unchanged.
     * Subject identifier are added. It uses the merge operation 
     * of AbstractSemanticTag.
     * 
     * @param remoteSet
     * @see AbstractSemanticTag
     */
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
    
    @Override
    public void removeSemanticTag(String si) throws SharkKBException {
          SemanticTag st = this.getSemanticTag(si);

          if(st != null) {
              this.removeSemanticTag(st);
          }
    }
  
    @Override
    public void removeSemanticTag(String[] sis) throws SharkKBException {
          SemanticTag st = this.getSemanticTag(sis);

          if(st != null) {
              this.removeSemanticTag(st);
          }
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
            this.listener = new ArrayList<>();
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

    /**
     * Adds this semantic tag to the set. Note: The object itself is
     * added, no copy is made. It shouldn't be changed after adding.
     * Create a copy if required in the first step.
     *
     * @param tag Tag to add.
     * @throws SharkKBException
     */
    public void add(SemanticTag tag) throws SharkKBException {
        this.storage.add(tag);

        this.notifyCreated(tag);
    }

    /**
     * TODO
     *
     * @param gst
     */
    public void addGeoSemanticTag(SpatialSemanticTag gst) throws SharkKBException {
        add(gst);
    }

    public SpatialSemanticTag createSpatialSemanticTag(String name, String[] sis, SharkGeometry geom) throws SharkKBException {
        SpatialSemanticTag st = this.getSpatialSemanticTag(sis);
        if (st != null) {
            return st;
        }

        st = new InMemoSpatialSemanticTag(name, sis, geom);
        this.addGeoSemanticTag(st);

        return st;
    }

    protected SpatialSemanticTag castGST(SemanticTag st) throws SharkKBException {
        if (st == null) {
            return null;
        }

        if (st instanceof SpatialSemanticTag) {
            return (SpatialSemanticTag) st;
        }

        throw new SharkKBException("cannot use non geo semantic tag in geo semantic tag set");
    }

    public SpatialSemanticTag getSpatialSemanticTag(String[] sis) throws SharkKBException {
        return this.castGST(getSemanticTag(sis));
    }
}
