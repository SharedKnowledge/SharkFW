package net.sharkfw.knowledgeBase.inmemory;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import net.sharkfw.knowledgeBase.*;
import net.sharkfw.system.Iterator2Enumeration;

/**
 *
 * @author thsc
 * @param <ST>
 */
public class InMemoGenericTagStorage<ST extends SemanticTag> {
        // A Hashtable containing all tags in this stset
    private ArrayList<ST> tags;

    // local table for mapping si strings to id values
    private HashMap<String, ST> si2tag;
    
    private boolean hide = false;
    
    public InMemoGenericTagStorage() {
        this.tags = new ArrayList<ST>();
        this.si2tag = new HashMap<String, ST>();
//        this.initSi();
    }
    
    public int number() {
        return this.tags.size();
    }
    
    protected ST getSemanticTag(String si) throws SharkKBException {
        if(si == null) return null;
        return this.si2tag.get(si);
    }
    
    public ST getSemanticTag(String[] si)  throws SharkKBException {
        if(si == null) {
            throw new SharkKBException("cannot get a semantic tag with null as si");
        }
        // iterate and find first matching tag
        for(int i = 0; i < si.length; i++) {
            ST result = this.getSemanticTag(si[i]);
            if(result != null) return result;
        }
        
        // we are here - so we haven't found anything...
        return null;
    }

    protected Enumeration<ST> tags() {
        if(!this.hide) {
            return new Iterator2Enumeration(this.tags.iterator());
        } else {
            return new HideEnumeration(this.tags.iterator());
        }
    }
    
    void setEnumerateHiddenTags(boolean hide) {
        this.hide = hide;
    }
    
    protected final void put(ST tag) {
        this.tags.add(tag);
        
        // recreate si list - not a very performant implementation have to confess...
//        this.initSi();
        
        String[] sis = tag.getSI();
        if(sis == null) { return; }

        for(int i = 0; i < sis.length; i++) {
            this.si2tag.put(sis[i], tag);
        }
    }
    
    protected void add(ST tag) throws SharkKBException {
        // only add if not yet in storage
        ST st = this.getSemanticTag(tag.getSI());
        
        if(st != null) return; // do nothing
        
        if(tag instanceof InMemoSemanticTag) {
            ((InMemoSemanticTag) tag).setStorage(this);
        }

        this.put(tag);
    }

    protected void removeSemanticTag(ST tag) {
        this.tags.remove(tag);
        
        // tag is removed - remove reference in si2tag list
        
        String[] sis = tag.getSI();
        if(sis == null | sis.length == 0) { return; }
        
        for(int i = 0; i < sis.length; i++) {
            this.si2tag.remove(sis[i]);
        }
    }

    ST merge(ST source) throws SharkKBException {
        SemanticTag copyTag = null;
        
        if(source == null) {
            return null;
        }
        
        // try to find tag
        copyTag = this.getSemanticTag(source.getSI());
        
        if(copyTag == null) {
        
            if(source instanceof PeerTXSemanticTag) {
                copyTag = InMemoSharkKB.createInMemoCopy((PeerTXSemanticTag) source);
            } else 
            if(source instanceof PeerSNSemanticTag) {
                copyTag = InMemoSharkKB.createInMemoCopy((PeerTXSemanticTag) source);
            } else 
            if(source instanceof TimeSemanticTag) {
                copyTag = InMemoSharkKB.createInMemoCopy((TimeSemanticTag) source);
            } else 
            if(source instanceof SpatialSemanticTag) {
                copyTag = InMemoSharkKB.createInMemoCopy((SpatialSemanticTag) source);
            } else 
            if(source instanceof TXSemanticTag) {
                copyTag = InMemoSharkKB.createInMemoCopy((TXSemanticTag) source);
            } else 
            if(source instanceof PeerSemanticTag) {
                copyTag = InMemoSharkKB.createInMemoCopy((PeerSemanticTag) source);
            } else 
            if(source instanceof SNSemanticTag) {
                copyTag = InMemoSharkKB.createInMemoCopy((SNSemanticTag) source);
            } else 
            if(source instanceof SemanticTag) {
                copyTag = InMemoSharkKB.createInMemoCopy((SemanticTag) source);
            }
    
            this.add((ST) copyTag);
        } else {
            SharkCSAlgebra.merge(copyTag, source);
        }
        
//        this.initSi();

        return (ST) copyTag;
    }

    public void siAdded(String addSI, ST tag) {
        if(addSI == null) { return; }
        this.si2tag.put(addSI, tag);
    }

    public void siRemoved(String deleteSI, ST tag) {
        if(deleteSI == null) { return; }
        this.si2tag.remove(deleteSI);
    }

    private class HideEnumeration implements Enumeration<ST> {
        private final Iterator<ST> tagEnum;
        
        private ST nextElement;
        
        HideEnumeration(Iterator<ST> tagEnum) {
            this.tagEnum = tagEnum;
            this.prefetch();
        }
        
        private void prefetch() {
            // reset prefetched element
            this.nextElement = null;
            
            while(this.tagEnum.hasNext() && this.nextElement == null) {
                ST tag = this.tagEnum.next();
                
                // can tag be used?
                if(!tag.hidden()) {
                    this.nextElement = tag;
                }
            }
        }

        @Override
        public boolean hasMoreElements() {
            return (this.nextElement != null);
        }

        @Override
        public ST nextElement() {
            ST tag = this.nextElement;
            this.prefetch();
            return tag;
        }
    }
}
