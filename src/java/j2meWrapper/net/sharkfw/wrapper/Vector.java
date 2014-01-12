package net.sharkfw.wrapper;

import java.util.Enumeration;

/**
 *
 * @author thsc
 */
public class Vector {

    private java.util.Vector v;

    public Vector() {
        this.v = new java.util.Vector();
    }
    
    /**
     * Create a new Vector containing all elements from the <code>Enumeration</code>.
     *
     * @param enumeration An <code>Enumeration</code> containing a number of <code>Object</code>s
     */
    public Vector(Enumeration enumeration) {
      this.v = new java.util.Vector();

      while(enumeration != null && enumeration.hasMoreElements()) {
        this.v.addElement(enumeration.nextElement());
      }
    }

    Vector(java.util.Vector vector) {
        this.v = vector;
    }

    public java.util.Enumeration elements() {
        return v.elements();
    }

    public Object clone() {
        Object copy[]=new Object[this.size()];
        v.copyInto(copy);
        Vector copyVector = new Vector();
        for(int i = 0; i < copy.length; i++) {
            copyVector.add(copy[i]);
        }
        return copyVector;
    }

    public int size() {
        return this.v.size();
    }

    public void add(Object o) {
        this.v.addElement(o);
    }

    public void removeElement(Object o) {
        this.v.removeElement(o);
    }

    public void remove(Object o) {
        this.v.removeElement(o);
    }

    public void removeElementAt(int i) {
        this.v.removeElementAt(i);
    }

    public Object elementAt(int index) {
        return this.v.elementAt(index);
    }

    public boolean contains(Object obj) {
        for (int i = 0; i < this.v.size(); i++) {
            if (obj.equals(this.v.elementAt(i))) {
                return true;
            }
        }
        return false;
    }
}
