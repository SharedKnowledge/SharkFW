package net.sharkfw.system;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * It makes now a copy of iterated elements because of allow changing
 * iteration source.
 * 
 * @author thsc
 */
public class Iterator2Enumeration implements Enumeration {
    private Iterator iter;
    private final Iterator iterHidden;
    
    private ArrayList tmpMemo;


    public Iterator2Enumeration(Iterator iter) {
        this(iter, null);
    }

    public Iterator2Enumeration(Iterator iter, Iterator iterHidden) {
        // make a copy first
        this.tmpMemo = new ArrayList();
        
        if(iter != null) {
            while(iter.hasNext()) {
                this.tmpMemo.add(iter.next());
            }
        }
        
        this.iter = this.tmpMemo.iterator();

        // keep this
        this.iterHidden = iterHidden;
    }

    @Override
    public boolean hasMoreElements() {
        if(!this.iter.hasNext()) {
            this.trySwitch();
        }

        return this.iter.hasNext();
    }

    @Override
    public Object nextElement() {
        try {
            Object o = this.iter.next();
            return o;
        }
        catch(NoSuchElementException nsee) {
            this.trySwitch();
        }

        // next try
        return this.iter.next();
    }

    boolean switched = false;
    private void trySwitch() {
        if(switched) { 
            return;
        }
            
        this.switched = true;
        this.tmpMemo = new ArrayList();
        
        // iter empty - do we have another set?
        if(this.iterHidden != null) {
            
            // make a copy and go ahead
            while(this.iterHidden.hasNext()) {
                this.tmpMemo.add(this.iterHidden.next());
            }
        }

        // in any case set new iter - even it is empty
        this.iter = this.tmpMemo.iterator();
    }
}    
