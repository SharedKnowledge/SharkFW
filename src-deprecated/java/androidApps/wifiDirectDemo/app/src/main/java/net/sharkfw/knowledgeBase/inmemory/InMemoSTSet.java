package net.sharkfw.knowledgeBase.inmemory;

import net.sharkfw.system.Util;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.sharkfw.knowledgeBase.*;
import net.sharkfw.system.EnumerationChain;

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
public class InMemoSTSet extends AbstractSTSet implements STSet {

    private InMemoGenericTagStorage storage;
    
    /**
     * That's the prefered way to create an empty stand alone semantic tag set.
     */
    public InMemoSTSet() {
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

    @Override
    public Enumeration<SemanticTag> tags() {
        return this.storage.tags();
    }
    
    public Iterator<SemanticTag> stTags() {
        EnumerationChain enumerationChain = new EnumerationChain();
        enumerationChain.addEnumeration(this.storage.tags());
        return enumerationChain;
    }

    @Override
    public void setEnumerateHiddenTags(boolean hide) {
        this.storage.setEnumerateHiddenTags(hide);
    }
    
    /**
     * copies name, SIs and properties to the target set
     * @param targetSet
     * @param source 
     */
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
    public void add(SemanticTag tag) throws SharkKBException {
        this.storage.add(tag);
        
        this.notifyCreated(tag);
    }

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
    
    public static Iterator<SemanticTag> getSemanticTagByName(STSet source, String pattern) throws SharkKBException {
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

