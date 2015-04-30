package net.sharkfw.system;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 *
 * @author thsc
 */
public class EnumerationChain<T> implements Enumeration<T>, Iterator<T> {
    
    private ArrayList<Enumeration<T>> eList = new ArrayList<Enumeration<T>>();
    private Enumeration<T> currentEnum = null;
    
    public void addEnumeration(Enumeration<T> e) {
        this.eList.add(e);
    }

    private void refresh() {
        if(currentEnum == null) {
            if(this.eList.size() > 0) {
                this.currentEnum = this.eList.get(0);
                this.eList.remove(0);
                // maybe null in list
                this.refresh();
            }
        }
    }
    
    @Override
    public boolean hasMoreElements() {
        this.refresh();
        if(this.currentEnum == null) return false;
        
        if(!this.currentEnum.hasMoreElements()) {
            this.currentEnum = null;
            return this.hasMoreElements();
        }
        
        return true;
    }

    @Override
    public T nextElement() {
        this.refresh();
        
        if(this.currentEnum == null) {
            throw new NoSuchElementException();
        }
        
        return this.currentEnum.nextElement();
    }

    @Override
    public boolean hasNext() {
        return this.hasMoreElements();
    }

    @Override
    public T next() {
        return this.nextElement();
    }

    @Override
    public void remove() {
    }
}
