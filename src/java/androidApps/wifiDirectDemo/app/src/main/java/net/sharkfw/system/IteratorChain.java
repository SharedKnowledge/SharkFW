package net.sharkfw.system;

import java.util.ArrayList;
import java.util.Iterator;

/**
 *
 * @author thsc
 */
public class IteratorChain implements Iterator {
    ArrayList things = new ArrayList();
    private final Iterator iter;

    public IteratorChain(Iterator<Iterator> iterIter) {
        // copy that stuff
        
        if(iterIter != null) {
           while(iterIter.hasNext()) {
               Iterator i = iterIter.next();
               
               if(i != null) {
                   while(i.hasNext()) {
                       this.things.add(i.next());
                   }
               }
           }
        }
        
        this.iter = this.things.iterator();
    }

    public boolean hasNext() {
        return this.iter.hasNext();
    }

    public Object next() {
        return this.iter.next();
    }

    public void remove() {
        this.iter.remove();
    }
}
