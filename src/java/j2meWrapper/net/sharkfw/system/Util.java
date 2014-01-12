/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sharkfw.system;

import java.util.Enumeration;
import java.util.Hashtable;

import java.io.OutputStream;

import net.sharkfw.knowledgeBase.ContextSpace;
import net.sharkfw.knowledgeBase.ROSTSet;
import net.sharkfw.system.SharkNotSupportedException;
import net.sharkfw.wrapper.Vector;
import net.sharkfw.knowledgeBase.ContextPoint;
import net.sharkfw.knowledgeBase.Information;
import net.sharkfw.knowledgeBase.SharkDuplicateException;
import net.sharkfw.knowledgeBase.SharkKBException;
import net.sharkfw.knowledgeBase.ExposedInterest;
import net.sharkfw.knowledgeBase.FragmentationParameter;
import net.sharkfw.knowledgeBase.LocalInterest;
import net.sharkfw.knowledgeBase.InMemoExposedInterest;


/**
 *
 * @author thsc
 */
public class Util {

    public static Hashtable cloneHashtable(Hashtable h) {
        Hashtable n = new Hashtable();

        Enumeration e = h.keys();
        while (e.hasMoreElements()) {
            Object key = e.nextElement();
            Object value = h.get(key);
            n.put(key, value);
        }

        return n;
    }
    private static final String STARTTAG = "<s>";
    private static final String ENDTAG = "</s>";

    static private class ArrayEnum implements Enumeration {

        private String[] s;
        private int i = 0;

        ArrayEnum(String[] s) {
            this.s = s;
        }

        public boolean hasMoreElements() {
            return i < s.length;
        }

        public Object nextElement() {
            return s[i++];
        }
    }

    public static void copyStringArray(String[] target, String[] source, int number) {
        for (int i = 0; i < number; i++) {
            target[i] = source[i];
        }
    }

    public static String[] addString(String[] source, String newItem) {
        if(source == null) {
            return new String[] {newItem};
        }
        
        String[] newArray = new String[source.length + 1];

        copyStringArray(newArray, source, source.length);

        newArray[source.length] = newItem;

        return newArray;
    }

    public static String[] removeSI(String[] source, String delItem) {
        String[] newArray = new String[source.length - 1];

        int index = 0;
        int numFound = 0;
        for (int i = 0; i < source.length; i++) {
            if (!source[i].equalsIgnoreCase(delItem)) {
                // not the same - copy
                newArray[index++] = source[i];
            } else {
                numFound++;
            }
        }

        if (numFound > 1) {
            // shrink array again
            String[] newArray2 = new String[source.length - numFound];

            copyStringArray(newArray2, newArray, newArray2.length);

            newArray = newArray2;
        }

        return newArray;
    }

    public static Enumeration array2Enum(String[] cpSI) {
        return new ArrayEnum(cpSI);
    }

    public static String array2string(String[] s) {
        StringBuffer buf = new StringBuffer();

        if (s == null) {
            return "";
        }

        for (int i = 0; i < s.length; i++) {
            buf.append(STARTTAG);
            buf.append(s[i]);
            buf.append(ENDTAG);
        }

        return buf.toString();
    }

    public static String[] string2array(String s) {
        if (s == null) {
            return null;
        }

        String[] a = null;

        int from, to;

        from = s.indexOf(Util.STARTTAG);
        if (from == -1) {
            return null;
        }

        // first count
        int i = 0;
        do {
            i++;
            from = s.indexOf(Util.STARTTAG, from + 1);
        } while (from != -1);

        a = new String[i];

        i = 0;
        from = s.indexOf(Util.STARTTAG);
        do {
            from += Util.STARTTAG.length();
            to = s.indexOf(Util.ENDTAG, from);
            a[i++] = s.substring(from, to);
            from = s.indexOf(Util.STARTTAG, from + 1);
        } while (from != -1);

        return a;
    }

    public static String[] mergeArrays(String[] a, String[] b) {
        if (a == null) {
            return b;
        }
        if (b == null) {
            return a;
        }

        // found all names in a which are not in b
        Vector v = new Vector();
        for (int i = 0; i < a.length; i++) {
            int j = 0;
            boolean found = false;
            do {
                if (a[i].equalsIgnoreCase(b[j])) {
                    found = true;
                } else {
                    j++;
                }
            } while (!found && j < b.length);

            if (!found) {
                v.add(a[i]);
            }
        }

        String[] c = new String[b.length + v.size()];

        Enumeration aEnum = v.elements();
        int i = 0;
        while (aEnum.hasMoreElements()) {
            c[i++] = (String) aEnum.nextElement();
        }

        for (int j = 0; j < b.length; j++) {
            c[i++] = b[j];
        }

        return c;
    }

    	public static boolean isPSI(String s) {

		if (s == null)
			return false;

		char c[] = s.toCharArray();
		int i = 0;

		// skip protocol name (if any) - letter and digits(??), e.g. http,
		// rfcomm
		while (i < c.length && c[i] != ':' && c[i + 1] != '/' && c[i + 2] != '/') {
			i++;
		}

		if (i + 2 >= c.length)
			return false;

		if (c[i] == ':' && c[i + 1] == '/' && c[i + 2] == '/')
			return true;

		return false;
        }

           /**
     * Tries to resolve a domainname within a gcf string (that is "socket://domainname:port") into socket://IP-Address:Port.
     * @param gcfstring the address String in gcf notation
     * @return an address String in GCF notation in which a hostname has been replaced by the corresponding IP Addres. If that fails the hostname is kept
     */
    public static String resolveDNtoIPinGCFString(String gcfstring){
        //FIXME J2ME does not support INetAddress, therefore we will just return the parameter for now.
        //TODO find out how to implement this in J2ME
        return gcfstring;

    }
    
      /**
     * Return whether or not two entities (<code>SemanticTag</code> i.e.) are the
     * same, by checking their SIs. If at least one SI matches this method will
     * return true. If no SIs match the method will return false.
     *
     * <code>null</code> is interpreted as 'unset'. This method will only
     * return <bold>exact</bold> matches.
     *
     * @param si_a A string array from entity a
     * @param si_b A string array from entity b
     * @return <code>true</code> if at least on string is in both arrays, or if one of the arrays is <code>null</code>, <code>false</code> otherwise
     */
	public static boolean sameEntity(String[] si_a, String[] si_b) {
		if (si_a == null && si_b == null) {
			return true;
    }

    if(si_a == null && si_b != null) {
      return false;
    }

    if(si_a != null && si_b == null) {
      return false;
    }

		for (int a = 0; a < si_a.length; a++) {
			for (int b = 0; b < si_b.length; b++) {
				if (si_a[a].equalsIgnoreCase(si_b[b])) {
					return true;
				}
			}
		}

		return false;
	}	
	 
	 public static int lastIndexOf(String string, String substring) {
     int len = string.length();

     while(len >= -1) {
       if(string.startsWith(substring, len)) {
         return len;
       }
       len--;
     }
     return len;
    }
    
    public static String[] enumeration2StringArray(Enumeration enumeration) {
      Vector v = new Vector();

      while(enumeration.hasMoreElements()) {
        v.add(enumeration.nextElement());
      }

      int len = v.size();
      String[] retval = new String[len];
      for(int i = 0; i < len; i++) {
        retval[i] = (String) v.elementAt(i);
      }

      return retval;
    }

    public static boolean isAny(ROSTSet stset) {

      if(stset == null) {
        return true;
      }

      try {
        if (!stset.tags().hasMoreElements()) {
          return true;
        }
      } catch (SharkNotSupportedException ex) {
        L.d(ex.getMessage(), Util.class);
      }

      try{
        stset.getSemanticTag(ContextSpace.ANYURL);
        return true;
      } catch (SharkException sex) {
        // NOOP
      }

      // Nothing matches?
      return false;
    }

    public static boolean contains(String s1, String s2) {
        if ( s1.indexOf( s2 ) > -1 ) {
             return true;
        } else {
             return false;
        }
    }
    
    /**
     * Copy all information from one ContextPoint to another.
     *
     * @param original The <code>ContextPoint</code> to copy from.
     * @param copy The <code>ContextPoint</code> to copy to.
     */
    public static void copyInformation(ContextPoint original, ContextPoint copy) {
      Enumeration infoEnum = original.getInformation();
      while(infoEnum != null && infoEnum.hasMoreElements()) {
        try {
        Information info = (Information) infoEnum.nextElement();

        // Not the right place?
        if(Util.contextPointHasHashcode(copy, info.hashCode())) {
            L.d("Duplicate suppression omits information "  + info, Util.class);
            continue;
        }
        
        // Auch properties versuchen zu uebernehmen
        Hashtable localProps = new Hashtable();
        Enumeration props = info.getPropertyNames();
        if (props != null) {
          while (props.hasMoreElements()) {
            String name = (String) props.nextElement();
            String value = info.getProperty(name);
            localProps.put(name, value);
          }
        }
        try {
          // Stream content of information
          Information localInfo = copy.addInformation(localProps, false);
          OutputStream os = localInfo.getWriteAccess();
          info.streamContent(os);
          L.d("Creating Information with name:" + info, Util.class);
        } catch (SharkDuplicateException ex) {
          L.e(ex.getMessage(), Util.class);
        }
        } catch (SharkKBException ex) {
          L.e(ex.getMessage(), Util.class);
        }
    }
  }
  
    /**
     * Turns an <code>Enumeration</code> into a <code>Vector</code>.
     * @param e The <code>Enumeration</code> to convert.
     * 
     * @return A <code>Vector</code> containing all elements of <code>e</code>
     */
    public static Vector enum2Vector(Enumeration e)
    {
      if(e == null) {
        return new Vector();
      }
      
    	Vector vector = new Vector();
    	
    	while(e.hasMoreElements())
    	{
    		vector.add(e.nextElement());
    	}
    	
		return vector;
    }
    
     /**
     * Create a copy of the given interest
     * @param interest
     * @return
     */
    public static ExposedInterest copyInterest(ExposedInterest interest) throws SharkKBException {

      if(interest == null) {
        return null;
      }
      
      LocalInterest copy = new InMemoExposedInterest();

      for(int i = 0 ; i < ContextSpace.MAXDIMENSIONS; i++) {
        ROSTSet dim = interest.getSTSet(i);
        ROSTSet copyDim = dim.copy();
        copy.setDimension(i, copyDim);
      }
      return copy;
    }
    
      /**
     * Sorts a <String> vector. Can sort other type of Objects, but will always
     * return a Vector of Strings (which can be parsed of course).
     * @param toSort the <String or Object> Vector to sort
     * @return Vector<String> sorted values.
     */
    public static Vector sortStringVector(Vector toSort) {
        if (toSort == null) {
            return new Vector();
        }
        int size = toSort.size();
        if (size == 0) {
            return toSort;
        }

        Vector sorted = new Vector();

        //Create a String array, since a Vector is not very flexible
        String[] elements = new String[size];
        for (int i = 0; i < size; i++) {
            //Using the toString method so other Objects coult be sorted as well.
            //This will cause a problem with the returned values (they will be strings!)
            elements[i] = toSort.elementAt(i).toString();
        }
        //Sorting the array
        stringQuicksort(elements, 0, size - 1);

        for (int i = 0; i < size; i++) {
            sorted.add(elements[i]);
        }
        return sorted;
    }

    /**
     * Sorts an <Integer> vector.
     * @param toSort the <Integer> Vector to sort
     * @return Vector<Integer> sorted values.
     */
    public static Vector sortIntegerVector(Vector toSort) {
        if (toSort == null) {
            return new Vector();
        }
        int size = toSort.size();
        if (size == 0) {
            return toSort;
        }

        Vector sorted = new Vector();

        //Create a String array, since a Vector is not very flexible
        Integer[] elements = new Integer[size];
        for (int i = 0; i < size; i++) {
            //Using the toString method so other Objects coult be sorted as well.
            //This will cause a problem with the returned values (they will be strings!)
            elements[i] = (Integer) toSort.elementAt(i);
        }
        //Sorting the array
        integerQuicksort(elements, 0, size - 1);

        for (int i = 0; i < size; i++) {
            sorted.add(elements[i]);
        }
        return sorted;
    }

    /**
     * A static strings- quicksort method.
     * @param elements - the String[] elements
     * @param low Lowest position in the array
     * @param hi Highest position in the array
     */
    private static void stringQuicksort(String[] elements, int low, int hi) {
        int i = low, j = hi;
        String temp;

        String x = elements[(low + hi) / 2];

        do {
            while (elements[i].compareTo(x) < 0) {
                i++;
            }
            while (elements[j].compareTo(x) > 0) {
                j--;
            }
            if (i <= j) {
                temp = elements[i];
                elements[i] = elements[j];
                elements[j] = temp;
                i++;
                j--;
            }
        } while (i <= j);

        if (low < j) {
            stringQuicksort(elements, low, j);
        }
        if (i < hi) {
            stringQuicksort(elements, i, hi);
        }
    }

    /**
     * A static integer- quicksort method.
     * Was implemented because i wasn't sure if JavaME supports Comparable Objects
     * @param elements - the String[] elements
     * @param low Lowest position in the array
     * @param hi Highest position in the array
     */
    private static void integerQuicksort(Integer[] elements, int low, int hi) {
        int i = low, j = hi;
        Integer temp;

        Integer x = elements[(low + hi) / 2];

        do {
            while (elements[i].intValue()-/*.compareTo*/(x).intValue()< 0) {
                i++;
            }
            while (elements[j].intValue()-(x).intValue() > 0) {
                j--;
            }
            if (i <= j) {
                temp = elements[i];
                elements[i] = elements[j];
                elements[j] = temp;
                i++;
                j--;
            }
        } while (i <= j);

        if (low < j) {
            integerQuicksort(elements, low, j);
        }
        if (i < hi) {
            integerQuicksort(elements, i, hi);
        }
    }
    
    /**
     * Return whether or not a certain hashcode is set already known on the
     * ContextPoint <code>cp</code>
     *
     * @param cp The ContextPoint to check.
     * @param hashcode The hashcode to check.
     * @return <code>true</code> if an Information exists on <code>cp</code> with <code>.getProperty(SharkKB.HASHCODE).equals(hashcode)</code>. <code>false</code> otherwise.
     */
    public static boolean contextPointHasHashcode(ContextPoint cp, int hashcode) {
      if(cp == null) {
        return false;
      }

      Enumeration infoEnum = cp.getInformation();
      while(infoEnum != null && infoEnum.hasMoreElements()) {
        Information info = (Information) infoEnum.nextElement();

        if(info.hashCode() == hashcode) {
          return true;
        }

      } // End of enumeration
      return false;
    }

     public static FragmentationParameter[] getZeroFP() {
      FragmentationParameter fp[] = new FragmentationParameter[ContextSpace.MAXDIMENSIONS];

      for(int i = 0; i < ContextSpace.MAXDIMENSIONS; i++) {
        fp[i] = new FragmentationParameter(false, false, 0);
      }
      return fp;
    }


}
