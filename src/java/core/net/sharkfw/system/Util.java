package net.sharkfw.system;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.sharkfw.kep.format.XMLSerializer;
import net.sharkfw.knowledgeBase.*;
import net.sharkfw.knowledgeBase.inmemory.*;
import net.sharkfw.peer.KnowledgePort;

/**
 * A collection of static utility methods for
 * recurring tasks inside the framework
 *
 * @author thsc
 * @author mfi
 */
public class Util {

    private static final String STARTTAG = "|s|";
    private static final String ENDTAG = "|/s|";

    /**
     * Properties from ph2 are copied to ph1 only if a property with the
     * same name does not exist. If a property already exists nothing happens.
     * @param target
     * @param source 
     */
    public static void mergeProperties(SystemPropertyHolder target, SystemPropertyHolder source) {
        try {
            Enumeration<String> ph2KeyEnum = source.propertyNames();
            while (ph2KeyEnum != null && ph2KeyEnum.hasMoreElements()) {
                String ph2Key = ph2KeyEnum.nextElement();
                String ph2Value = source.getProperty(ph2Key);
                String ph1Value = target.getProperty(ph2Key);
                if(ph1Value == null) {
                    target.setProperty(ph2Key, ph2Value);
                }
            }
        } catch (SharkKBException ex) {
            L.e("cannot access properties");
        }
    }

    /**
     *
     * This class turns a String array into an <code>
     * Enumeration.
     */
    @SuppressWarnings("rawtypes")
    static private class ArrayEnum implements Enumeration {

        private String[] s;
        private int i = 0;

        /**
         * The array to created the Enumeration form
         * @param s String array to create the Enumeration
         */
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

    /**
     * Copy contents of string array source to string array target
     * target and source need to be at least of the size of <code>size</code>.
     *
     * This code acts exactly like:
     * <code>
     * for(int i = 0; i < number; i++) {
    target[i] = source[i];
    }
     * </code>
     *
     * @param target The string array to copy to
     * @param source The string array to copy from
     * @param number The amount of items to copy
     */
    public static void copyStringArray(String[] target, String[] source, int number) {
        for (int i = 0; i < number; i++) {
            target[i] = source[i];
        }
    }

    /**
     * Add a string into an array and extend its size.
     * If source is null, a new string array is created.
     *
     * @param source The string array to add a atring to
     * @param newItem The string to be added
     * @return The extended string array
     */
    public static String[] addString(String[] source, String newItem) {
        if (source == null) {
            return new String[]{newItem};
        }
        String[] newArray = new String[source.length + 1];

        copyStringArray(newArray, source, source.length);

        newArray[source.length] = newItem;

        return newArray;
    }

    /**
     * Remove a string - representing a SI - from an array
     * This method will remove every occurrence of that string from the array
     * returning a downsized array.
     *
     * @param source The array to delete from
     * @param delItem The string to be deleted from the array
     * @return The downsized array holding no more instances of <code>delItem</code>
     */
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

    /**
     * Return an Enumeration from a String array
     * @param cpSI The array to be turned into an Enumeration
     * @return An Enumeration of Strings created from the array
     */
    @SuppressWarnings("rawtypes")
    public static Enumeration array2Enum(String[] cpSI) {
        return new ArrayEnum(cpSI);
    }

    /**
     * Serialize a string array into a single string.
     * This method uses <code>STARTTAG</code> and <code>ENDTAG</code> to
     * serialize single entries of a string array into one string.
     *
     * @param s The array to be serialized
     * @return A string containing the serialized version of the array
     */
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

    /**
     * Deserialize a string into a string array. This is the deserialization method
     * to match <code>array2String</code>. It deserializes a string using
     * <code>STARTTAG</code> and <code>ENDTAG</code> as delimiters.
     *
     * @param s The string to deserialize
     * @return An array of strings
     */
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

    /**
     * Create the union of two string arrays.
     * Duplicate entries are removed.
     *
     * @param a A string array
     * @param b A string array
     * @return The union of <code>a</code> and <code>b</code>
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
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

//  /**
//   * Return a serialized form of an <code>InMemoExposedInterest</code>
//   *
//   * @deprecated
//   * @param cm
//   * @return
//   */
//	public static String cm2string(InMemoExposedInterest cm) {
//		try {
//			return cm.serialize(KEPMessage.INTERNAL);
//		} catch (SharkNotSupportedException ex) {
//			return null;
//		}
//	}
//	public static InMemoExposedInterest string2contextMap(String s) {
//		try {
//			return InMemoExposedInterest.create(s, KEPMessage.INTERNAL);
//		} catch (SharkNotSupportedException ex) {
//			return null;
//		}
//	}
//    /**
//     * @deprecated
//     * @param cs
//     * @return
//     */
//    public static String cs2string(AnchorSet cs) {
//        return cs.serialize();
//    }
//
//    /**
//     * @deprecated
//     * @param s
//     * @return
//     */
//    public static AnchorSet string2contextSpace(String s) {
//        try {
//            return new AnchorSet(s);
//        } catch (net.sharkfw.system.SharkNotSupportedException ex) {
//            return null;
//        }
//    }
    /**
     * Deserializing a string into a vector using a given delimiter to
     * find single tokens.
     *
     * @param s The string to deserialize
     * @param delimiter The delimiter to tell tokens apart
     * @return A vector of String as taken from <code>s</code>
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static Vector string2Vector(String s, String delimiter) {
        if (s == null || delimiter == null) {
            return null;
        }

        Vector v = new Vector();

        StringTokenizer st = new StringTokenizer(s, delimiter);
        while (st.hasMoreTokens()) {
            v.add(st.nextToken());
        }

        return v;
    }

    /**
     * Serializing a Vector containing strings to a string using a given delimiter.
     *
     * @param v The vector to serialize
     * @param delimiter The delimiter to tell tokens apart
     * @return A string containing the serialized contents of the Vector
     */
    @SuppressWarnings("rawtypes")
    public static String vector2String(Vector v, String delimiter) {
        if (v == null || delimiter == null) {
            return null;
        }

        return enumeration2String(v.elements(), delimiter);
    }
    
    public static String PSTArrayList2String(ArrayList<PeerSemanticTag> stList) 
            throws SharkKBException {
        
        if(stList == null) {
            return null;
        }
        
        PeerSTSet pSet = InMemoSharkKB.createInMemoPeerSTSet();
        Iterator<PeerSemanticTag> pIter = stList.iterator();
        while(pIter.hasNext()) {
            pSet.merge(pIter.next());
        }
        
        XMLSerializer xs = new XMLSerializer();
        return xs.serializeSTSet(pSet);
    }
    
    public static ArrayList<PeerSemanticTag> String2PSTArrayList(String serialized) throws SharkKBException {
        XMLSerializer xs = new XMLSerializer();

        PeerSTSet pSet = InMemoSharkKB.createInMemoPeerSTSet();
        xs.deserializeSTSet(pSet, serialized);
        
        ArrayList<PeerSemanticTag> pArray = new ArrayList<PeerSemanticTag>();
        
        Enumeration<PeerSemanticTag> peerTags = pSet.peerTags();
        if(peerTags == null || !peerTags.hasMoreElements()) {
            return pArray;
        }
        
        while(peerTags.hasMoreElements()) {
            pArray.add(peerTags.nextElement());
        }
        
        return pArray;
    }
    
    @SuppressWarnings("rawtypes")
    public static String iteration2String(Iterator iter, String delimiter) {
        return enumeration2String(new Iterator2Enumeration(iter), delimiter);
    }

    @SuppressWarnings("rawtypes")
    public static String enumeration2String(Enumeration enume, String delimiter) {
        if (enume == null || delimiter == null) {
            return null;
        }

        StringBuffer buf = new StringBuffer();

        while (enume.hasMoreElements()) {
            buf.append(enume.nextElement());
            buf.append(delimiter);
        }

        return buf.toString();
    }

    @SuppressWarnings("rawtypes")
    public static String fragmentationParameter2string(
            FragmentationParameter[] fp) {
        StringBuffer buf = new StringBuffer();

        // <dim
        // id=_><atypes>a,b,c</atypes><ftypes>d,e,f</ftypes><depth>_</depth></dim>

        for (int dim = 0; dim < fp.length && dim < SharkCS.MAXDIMENSIONS; dim++) {
            FragmentationParameter f = fp[dim];

            if (f != null) {
                buf.append("<dim id=");
                buf.append(dim);
                buf.append(">");

                Enumeration tEnum = f.getAllowedPredicates();
                if (tEnum != null && tEnum.hasMoreElements()) {
                    buf.append("<atypes>");
                    while (tEnum.hasMoreElements()) {
                        buf.append(tEnum.nextElement());
                        buf.append(",");
                    }
                    buf.append("</atypes>");
                }

                tEnum = f.getForbiddenPredicates();
                if (tEnum != null && tEnum.hasMoreElements()) {
                    buf.append("<ftypes>");
                    while (tEnum.hasMoreElements()) {
                        buf.append(tEnum.nextElement());
                        buf.append(",");
                    }
                    buf.append("</ftypes>");
                }

                buf.append("<depth>");
                buf.append(Integer.toString(f.getDepth()));
                buf.append("</depth>");

                buf.append("</dim>");
            }
        }

        return buf.toString();
    }

    /**
     * Deserialize a string to a <code>FragmentationParameter</code>
     *
     * @param s A string containing the serialized form of a <code>FragmentationParameter</code>
     * @return A <code>FragmentationParameter</code> created using <code>s</code>
     */
    @SuppressWarnings("rawtypes")
    public static FragmentationParameter[] string2fragmentationParameter(String s) {
        FragmentationParameter[] fpSet = new FragmentationParameter[SharkCS.MAXDIMENSIONS];

        if (s == null) {
            return fpSet;
        }

        FragmentationParameter fp;
        Vector allowed, forbidden;
        int depth;

        // <dim
        // id=_><atypes>a,b,c</atypes><ftypes>d,e,f</ftypes><depth>_</depth></dim>
        int index = s.indexOf("<dim");
        int nextIndex = s.indexOf("</dim>");

        do {
            int first = s.indexOf("id=", index);
            int behind = s.indexOf(">", index);
            String str = s.substring(first + 3, behind);
            int dimNumber = Integer.parseInt(str);

            allowed = null;
            forbidden = null;
            depth = -1;
            if (dimNumber >= 0 && dimNumber <= SharkCS.MAXDIMENSIONS) {

                index = s.indexOf("<atypes>", behind);
                if (index > -1 && index < nextIndex) {
                    first = index + "<atypes>".length();
                    behind = s.indexOf("</atypes>", first);

                    str = s.substring(first, behind);

                    allowed = Util.string2Vector(str, ",");
                }

                index = s.indexOf("<ftypes>", behind);

                if (index > -1 && index < nextIndex) {
                    first = index + "<ftypes>".length();
                    behind = s.indexOf("</ftypes>", first);

                    str = s.substring(first, behind);

                    forbidden = Util.string2Vector(str, ",");
                }

                index = s.indexOf("<depth>", behind);
                if (index > -1 && index < nextIndex) {
                    first = index + "<depth>".length();
                    behind = s.indexOf("</depth>", first);

                    str = s.substring(first, behind);

                    depth = Integer.parseInt(str);
                }

                if (allowed != null || forbidden != null || depth >= 0) {
                    fp = new FragmentationParameter(allowed, forbidden, depth);
                    fpSet[dimNumber] = fp;
                }
            }

            index = s.indexOf("<dim", nextIndex + 1);
            nextIndex = s.indexOf("</dim>", nextIndex + 1);

        } while (index > -1);

        return fpSet;
    }

    /**
     * Turn a <code>List</code> into a <code>Vector</code>
     * 
     * @param list The <code>List</code> to convert
     * @return A <code>Vector</code> containing all elements from the <code>list</code>.
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static Vector list2Vector(List list) {
        Iterator listIterator = list.iterator();

        Vector vector = new Vector();

        while (listIterator.hasNext()) {
            vector.add(listIterator.next());
        }

        return vector;
    }

    /**
     * Turns an <code>Enumeration</code> into a <code>Vector</code>.
     * @param e The <code>Enumeration</code> to convert.
     * 
     * @return A <code>Vector</code> containing all elements of <code>e</code>
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static Vector enum2Vector(Enumeration e) {
        if (e == null) {
            return new Vector();
        }

        Vector vector = new Vector();

        while (e.hasMoreElements()) {
            vector.add(e.nextElement());
        }

        return vector;
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
     * @deprecated use {@link SharkCSAlgebra.identical}
     */
    public static boolean sameEntity(String[] si_a, String[] si_b) {

        if (si_a == null && si_b == null) {
            return true;
        }

        if (si_a == null && si_b != null) {
            return false;
        }

        if (si_a != null && si_b == null) {
            return false;
        }

        //Temp solution!! not very nice....
        /*if (DataUtil.sameDataEntity(si_a, si_b)) {
        return true;
        }*/
        /*(if (DataUtil.dataEntityContains(si_a[0], si_b[0])) {
        System.out.println("Same");
        return true;
        }*/

        for (int a = 0; a < si_a.length; a++) {
            for (int b = 0; b < si_b.length; b++) {
                if (si_a[a].equalsIgnoreCase(si_b[b])) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Return whether or not a string is considered a PSI.
     * This is determind by checking for the presence of
     * the mandatory '://' separator.
     *
     * @param s The string to check
     * @return <code>true</code> if '://' is present, <code>false</code> otherwise.
     */
    public static boolean isPSI(String s) {

        if (s == null) {
            return false;
        }

        char c[] = s.toCharArray();
        int i = 0;

        // skip protocol name (if any) - letter and digits(??), e.g. http,
        // rfcomm
        while (i < c.length && Character.isLetterOrDigit(c[i])) {
            i++;
        }

        if (i + 2 >= c.length) {
            return false;
        }

        if (c[i] == ':' && c[i + 1] == '/' && c[i + 2] == '/') {
            return true;
        }

        return false;

    }

    /**
     * Tries to delete a directory and all contained files and subdirectories
     *
     * @param path
     *            File or Directory to delete.
     * @return returns true if the directory could be deleted. Also return true
     *         if the directory did not exist from the start.
     */
    static public boolean deleteDirectory(File path) {
        if (!path.exists()) {
            return true;
        }
        if (path.exists()) {
            File[] files = path.listFiles();
            for (int i = 0; i < files.length; i++) {
                if (files[i].isDirectory()) {
                    deleteDirectory(files[i]);
                } else {
                    files[i].delete();
                }
            }
        }
        return (path.delete());
    }

    /**
     * Reimplements Propertis.store, but without the date. Its definition,
     * changes in <i>italic</i> font: <br/>
     * <br/>
     * If the comments argument is not null, then an ASCII # character, the
     * comments string, and a line separator are first written to the output
     * stream. Thus, the comments can serve as an identifying comment. <br/>
     * <br/>
     * <i>Attention: The comment is escaped as if it was a key.</i> <br/>
     * <br/>
     * Next, a comment line is always written, consisting of an ASCII #
     * character, <i>"no date"</i>, and a line separator as generated by the
     * Writer. <br/>
     * <br/>
     * Then every entry in this Properties table is written out, one per line.
     * For each entry the key string is written, then an ASCII =, then the
     * associated element string. Each character of the key and element strings
     * is examined to see whether it should be rendered as an escape sequence.
     * The ASCII characters \, tab, form feed, newline, and carriage return are
     * written as \\, \t, \f \n, and \r, respectively. Characters less than and
     * characters greater than \u007E are written as \\uxxxx for the appropriate
     * hexadecimal value xxxx. For the key, all space characters are written
     * with a preceding \ character. For the element, leading space characters,
     * but not embedded or trailing space characters, are written with a
     * preceding \ character. The key and element characters #, !, =, and : are
     * written with a preceding backslash to ensure that they are properly
     * loaded. <br/>
     * <br/>
     * After the entries have been written, the output stream is flushed. The
     * output stream remains open after this method returns.
     *
     * @param fos
     * @param prop
     * @param comment
     */
    @SuppressWarnings("rawtypes")
    public static void storeProps(OutputStream fos, Properties prop,
            String comment) {
        try {
            BufferedOutput writer = new BufferedOutput(fos);

            writer.write('#');
            writeEscaped(writer, comment, false);
            writer.write('\n');
            // Writer.newLine() should be used, but this method does not exist!
            writer.write('#');
            writeEscaped(writer, "no date", false);
            writer.write('\n');
            Enumeration keys = prop.keys();
            while (keys.hasMoreElements()) {
                String key = (String) keys.nextElement();
                writeEscaped(writer, key, true);
                writer.write('=');
                final String value = (String) prop.get(key);
                writeEscaped(writer, value, false);
                writer.write('\n');
            }
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Escapes a String as defined under storeProps. This method writes to a
     * Writer, because first Writing into a String would be inefficient.
     *
     * @param writer
     * @param value
     * @param escapeAllSpaces
     * @throws IOException
     */
    private static void writeEscaped(BufferedOutput writer, String value,
            boolean escapeAllSpaces) throws IOException {
        final int l = value.length();
        boolean onlySpacesYet = true;
        for (int i = 0; i < l; i++) {
            final char c = value.charAt(i);

            // This condition is pure optimization: Most characters will be
            // letters, so we handle them first before checking every possible
            // non-letter. If we do not catch the letter here, it will be
            // handled at the end.

            if (c >= 'a' && c <= 'Z') {
                writer.write(c);
                continue;
            }

            if (c != ' ') {
                onlySpacesYet = false;
            }

            if (c == '\t') {
                writer.write('\\');
                writer.write('t');
            } else if (c == '\n') {
                writer.write('\\');
                writer.write('n');
            } else if (c == '\r') {
                writer.write('\\');
                writer.write('r');
            } else if (c == '\f') {
                writer.write('\\');
                writer.write('f');
            } else if (c == '\\') {
                writer.write('\\');
                writer.write('\\');
            } else if (c < 0x20 || c > 0x7E) {
                writer.write('\\');
                writer.write('u');
                // TODO This way to convert an int to hex is very slow. As long
                // as we do not have many non-ascii-characters, this should be
                // ok
                writer.write(Integer.toHexString((c >> 3 * 4) & 15).charAt(0));
                writer.write(Integer.toHexString((c >> 2 * 4) & 15).charAt(0));
                writer.write(Integer.toHexString((c >> 1 * 4) & 15).charAt(0));
                writer.write(Integer.toHexString((c >> 0 * 4) & 15).charAt(0));
            } else {
                if (c == ':' || c == '#' || c == '!' || c == '=') {
                    writer.write('\\');
                }
                if (c == ' ' && (escapeAllSpaces || onlySpacesYet)) {
                    writer.write('\\');
                }
                writer.write(c);
            }
        }
    }

    /**
     * This is basically the same as a BufferedOutputStream, but runs several
     * Times faster than the one provided on Android.
     *
     * @author pbs
     */
    public final static class BufferedOutput extends OutputStream {

        private static final int BUFFER_LENGTH = 1000;
        byte[] buffer = new byte[BUFFER_LENGTH];
        int pos = 0;
        private OutputStream os;

        public BufferedOutput(OutputStream os) {
            this.os = os;
        }

        public final void flush() {
            try {
                os.write(buffer, 0, pos);
            } catch (IOException e) {
                e.printStackTrace();
            }
            pos = 0;
        }

        public final void write(int c) {
            if (pos >= BUFFER_LENGTH) {
                flush();
            }
            buffer[pos++] = (byte) c;
        }
    }

    /**
     * Retrieve the IP and port from the gcf notation for addresses, leaving out the protocol prefix
     * @param uri
     * @return Stringarray which [0] Element holds the ip address and [1] holds the port
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static String[] getIPandPortFromURI(String uri) {

        Vector strings = new Vector();
        String ip = new String();
        String port = new String();

        StringTokenizer st = new StringTokenizer(uri, "//");

        // disintegrate by "//" leaves us with socket: and IP:PORT
        while (st.hasMoreTokens()) {
            String s = st.nextToken();
            //System.out.println("Has more tokens ..." + s);
            strings.add(s);
        }

        // check every String in there for ":". If it exists it is the ip:port combo
        Enumeration stringsEnum = strings.elements();
        while (stringsEnum.hasMoreElements()) {
            String s = (String) stringsEnum.nextElement();

            if (s.contains(":") && !s.contains("socket")) {
                // another ST is needed to disintegrate the resulting string further
                StringTokenizer st2 = new StringTokenizer(s, ":");

                if (st2.hasMoreTokens()) {
                    ip = st2.nextToken();
                }

                if (st2.hasMoreTokens()) {
                    port = st2.nextToken();
                }
            }
        }
        String[] retval = {ip, port};
        return retval;
    }

    /**
     * resolve a domainname to an ip address
     * @param domainname the domainname of the hose
     * @return the ipaddress corresponding to the hostname or the passed argument if an exception occured
     */
    public static String DNtoIP(String domainname) {
        try {
            return InetAddress.getByName(domainname).getHostAddress();
        } catch (UnknownHostException ex) {
            return domainname;
        }
    }

    /**
     * resolve an ip address to a domainname
     * @param ipaddress the ipaddress in question
     * @return the name corresponding to the ip address (using etc/hosts or dns servers) or the passed argument in case of an error
     */
    public static String IPtoDN(String ipaddress) {
        try {
            return InetAddress.getByAddress(ipaddress.getBytes()).getHostName();
        } catch (UnknownHostException ex) {
            return ipaddress;
        }
    }

    /**
     * Tries to resolve a domainname within a gcf string (that is "socket://domainname:port") into socket://IP-Address:Port.
     * @param gcfstring the address String in gcf notation
     * @return an address String in GCF notation in which a hostname has been replaced by the corresponding IP Addres. If that fails the hostname is kept
     */
    public static String resolveDNtoIPinGCFString(String gcfstring) {
        String[] dnPort = Util.getIPandPortFromURI(gcfstring); // Stringarray consists of 2 elements [0] is the domainname or ip, [1] is the port

        String ipAddr = Util.DNtoIP(dnPort[0]); // resolve dn to ip. If it is a ip it will return the ip. If the hostname cannot be resolved it will keep the hostname
        String port = dnPort[1];

        return "socket://" + ipAddr + ":" + port; // build new gcf-notated string as returnvalue


    }

    /**
     * Reimplementation of <code>lastIndexOf</code> from <code>String</code>
     * class to make it work indepent of the platform used. JavaME's String i.e.
     * does not provide this method.
     *
     * @param string The String to search
     * @param substring The substring to be found inside <code>string</code>
     * @return The index of the start of <code>substring</code> in <code>string</code> or -1
     */
    public static int lastIndexOf(String string, String substring) {
        int len = string.length();

        while (len >= -1) {
            if (string.startsWith(substring, len)) {
                return len;
            }
            len--;
        }
        return len;
    }

    /**
     * Wrapper implementation of <code>Math.acos()</code>. Inside the framework
     * only this method must be used to ensure the availability on all platforms.
     * JavaME i.e. does not provide an acos function in its <code>Math</code> class.
     *
     * @param d A double value to determine the acos for.
     * @return The acos for <code>d</code>
     */
    public static double acos(double d) {
        return Math.acos(d);
    }

    /**
     * Turns an <code>Enumeration</code> of strings into an array of strings.
     *
     * @param enumeration The enumeration of strings to read from
     * @return A newly created array of strings as read from the <code>enumeration</code>
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static String[] enumeration2StringArray(Enumeration enumeration) {
        Vector v = new Vector();

        while (enumeration.hasMoreElements()) {
            v.add(enumeration.nextElement());
        }

        int len = v.size();
        String[] retval = new String[len];
        for (int i = 0; i < len; i++) {
            retval[i] = (String) v.elementAt(i);
        }

        return retval;
    }

    /**
     * Copy all information from one ContextPoint to another.
     *
     * @param original The <code>ContextPoint</code> to copy from.
     * @param copy The <code>ContextPoint</code> to copy to.
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public static void copyInformation(ContextPoint original, ContextPoint copy, boolean withTimestamp) throws SharkKBException {
        Enumeration infoEnum = original.enumInformation();
        while (infoEnum != null && infoEnum.hasMoreElements()) {
//            try {
            Information info = (Information) infoEnum.nextElement();
            // Is this the right place? -mfi
            // removed cause it just copied the first information not all!
//            if (Util.contextPointHasHashcode(copy, info.hashCode())) {
//                L.d("Duplicate suppression omits information " + info, Util.class);
//                continue;
//            }
            // Auch properties versuchen zu uebernehmen
            Hashtable localProps = new Hashtable();
            Enumeration props = info.propertyNames();
            if (props != null) {
                while (props.hasMoreElements()) {
                    String name = (String) props.nextElement();
                    String value = info.getProperty(name);
                    if(value != null) {
                        localProps.put(name, value);
                    }
                }
            }

            // Stream content of information
            Information localInfo = (Information) copy.addInformation();
            // Copy all properties from the original contextpoint to the copied ContextPoint
            Util.copyPropertiesFromPropertyHolderToPropertyHolder(original, copy);
            
            OutputStream os = localInfo.getOutputStream();
            info.streamContent(os);
            // Also copy all props from the original information to the newly created information
            Util.copyPropertiesFromPropertyHolderToPropertyHolder(info, localInfo);
            L.d("Creating Information with name:" + info, Util.class);

        }
    }

    /**
     * Return whether or not the given tag has the given address.
     * @param tag
     * @param address
     * @return
     */
    public static boolean tagHasAddress(PeerSemanticTag tag, String address) {
        String[] tagAddresses = tag.getAddresses();
        if (tagAddresses != null) {
            for (int i = 0; i < tagAddresses.length; i++) {
                if (address.equals(tagAddresses[i])) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Sorts a <String> vector. Can sort other type of Objects, but will always
     * return a Vector of Strings (which can be parsed of course).
     * @param toSort the <String or Object> Vector to sort
     * @return Vector<String> sorted values.
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
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
    @SuppressWarnings({ "rawtypes", "unchecked" })
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
            while (elements[i] - x/*.compareTo(x)*/ < 0) {
                i++;
            }
            while (elements[j] - x > 0) {
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
    @SuppressWarnings("rawtypes")
    public static boolean contextPointHasHashcode(ContextPoint cp, int hashcode) {
        if (cp == null) {
            return false;
        }

        Enumeration infoEnum = cp.enumInformation();
        while (infoEnum != null && infoEnum.hasMoreElements()) {
            Information info = (Information) infoEnum.nextElement();

            if (info.hashCode() == hashcode) {
                return true;
            }

        } // End of enumeration
        return false;
    }

    /**
     * Return an Array of FragmentationParameters like:
     * <code>new FragmentationParameter(false, false, 0);</code>
     *
     * @return An Array of size <code>ContextSpace.MAXDIMENSIONS</code> with FPs
     * @deprecated 
     */
    public static FragmentationParameter[] getZeroFP() {
        return KnowledgePort.getZeroFP();
    }

    // These Strings are used to denote if a property is serializable or not
    private static final String DONTSEND_OPEN = "|dontSend|";
    private static final String DONTSEND_CLOSE = "|/dontSend|";

    /**
     * Create a xml-like wrapping of the key to denote either the property,
     * tied to that key is serializable or not. If it is not serializable it
     * will not be transmitted when its property holder is transmitted
     * (i.e. SemanticTags in interests or knowledge).
     *
     * @param key The original key
     * @return A string containing the original key, plus an xml-like notation to define this property as non serializable.
     */
    public static String nonSerializableProperty(String key) {
        String retval = DONTSEND_OPEN + key + DONTSEND_CLOSE;
        return retval;
    }

    /**
     * Return whether or not a key is marked as serializable.
     * @param key The key string to check
     * @return
     */
    public static boolean isPropertySerializable(String key) {
        if (key != null && key.startsWith(DONTSEND_OPEN) && key.endsWith(DONTSEND_CLOSE)) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * Return the proper key from the String passed, that includes to strip transfer
     * marking from the key before returning it.
     * 
     * @param keystring A String used as a key in a property
     * @return A string which has been stripped of all transfer marks, if such were set.
     */
    public static String getKeyFromString(String keystring) {
        String retval = keystring;

        if (!isPropertySerializable(keystring)) {
            retval = keystring.substring(DONTSEND_OPEN.length(), keystring.length() - DONTSEND_CLOSE.length());
//            retval = keystring.substring(DONTSEND_OPEN.length()).substring(keystring.length() - DONTSEND_CLOSE.length(), keystring.length()); // Strip tags from proper key
        }
        return retval;
    }

    /**
     * Copy all properties from PropertyHolder <code>source</code> to PropertyHolder <code>copy</code>.
     *
     * @param source The property holder used as template.
     * @param copy The property holder to be filled with all props from source.
     */
    @SuppressWarnings("rawtypes")
    public static void copyPropertiesFromPropertyHolderToPropertyHolder(SystemPropertyHolder source, SystemPropertyHolder copy) {
        try {
            Enumeration nameEnum = source.propertyNames(true);
            while (nameEnum != null && nameEnum.hasMoreElements()) {
                String name = (String) nameEnum.nextElement();
                String value = source.getProperty(name);
                /*
                * TODO: Handle transferable and untransferable props
                */
                if(value != null) {
                    copy.setProperty(name, value);
                }
            }
        } catch (SharkKBException ex) {
            L.e("cannot access properties");
        }
    }

    /**
     * Return a string name for the contants from <code>Calendar</code>.
     * @param value An int value for a weekday
     * @return A string containing the name of the weekday or "unknown" if value is below 0 or above 6.
     */
    public static String getWeekdayForInt(int value) {

        switch (value) {
            case Calendar.MONDAY:
                return "Monday";
            case Calendar.TUESDAY:
                return "Tuesday";
            case Calendar.WEDNESDAY:
                return "Wednesday";
            case Calendar.THURSDAY:
                return "Thursday";
            case Calendar.FRIDAY:
                return "Friday";
            case Calendar.SATURDAY:
                return "Saturday";
            case Calendar.SUNDAY:
                return "Sunday";
            default:
                return "Unknown";
        }
    }

    public static final byte[] intToByteArray(int value) {
        return new byte[]{
                    (byte) (value >>> 24),
                    (byte) (value >>> 16),
                    (byte) (value >>> 8),
                    (byte) value};
    }

    public static final int byteArrayToInt(byte[] b) {
        return (b[0] << 24)
                + ((b[1] & 0xFF) << 16)
                + ((b[2] & 0xFF) << 8)
                + (b[3] & 0xFF);
    }
    
    public static final byte[] longToByteArray(long value) {
      return new byte[] {
        (byte) (value >>> 56),
        (byte) (value >>> 48),
        (byte) (value >>> 40),
        (byte) (value >>> 32),
        (byte) (value >>> 24),
        (byte) (value >>> 16),
        (byte) (value >>> 8),
        (byte) (value)
      };
    }
    
    public static final long byteArrayToLong(byte[] b) {
       return (b[0] << 56)
                + ((b[1] & 0xFF) << 48)
                + ((b[2] & 0xFF) << 40)
                + ((b[3] & 0xFF) << 32)
                + ((b[4] & 0xFF) << 24)
                + ((b[5] & 0xFF) << 16)
                + ((b[6] & 0xFF) << 8)
                + (b[7] & 0xFF);
    }

    /**
     * Checks wheather the String is null or empty. If its not null and not empty it returns <code>true</code>.
     * 
     * @param s string to check
     * @return false if the String is null or empty, else true
     */
	public static boolean isValidString(String s) {
		if(s != null && !s.equalsIgnoreCase("")) {
			return true;
		}
		return false;
	}
        
     public static void merge(SharkKB target, SharkCS source) throws SharkKBException {
         
        PeerSemanticNet peers = target.getPeersAsSemanticNet();
        SemanticNet topics = target.getTopicsAsSemanticNet();
        
        // topics
        STSet sTopics = source.getTopics();
        if(sTopics instanceof SemanticNet) {
            SemanticNet snTopics = (SemanticNet) sTopics;
            topics.merge(snTopics);
        } else {
            topics.merge(sTopics);
        }
        
        // peers
        STSet sPeers = source.getPeers();
        if(sPeers instanceof PeerSemanticNet) {
            PeerSemanticNet psnPeers = (PeerSemanticNet) sPeers;
            peers.merge(psnPeers);
        } else {
            peers.merge(sPeers);
        }
        
        // remote peers
        STSet sRemotePeers = source.getRemotePeers();
        if(sRemotePeers instanceof PeerSemanticNet) {
            PeerSemanticNet psnRemotePeers = (PeerSemanticNet) sRemotePeers;
            peers.merge(psnRemotePeers);
        } else {
            peers.merge(sRemotePeers);
        }
        
        peers.merge(source.getOriginator());

        target.getSpatialSTSet().merge(source.getLocations());
        target.getTimeSTSet().merge(source.getTimes());
         
     }
}
