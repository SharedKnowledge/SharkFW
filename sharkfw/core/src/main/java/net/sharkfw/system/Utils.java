package net.sharkfw.system;

import java.util.StringTokenizer;
import java.util.Vector;

/**
 *
 * @author thsc
 */
public class Utils {

    private static final String DELIMITER = "|";
    
    public static String serialize(String[] si) {
        if(si.length < 1) return null;
        
        StringBuilder buf = new StringBuilder();
        buf.append(si[0]);
        for(int i = 1; i < si.length; i++) {
            buf.append(DELIMITER);
            buf.append(si[i]);
        }
        
        return buf.toString();
    }
    
    public static String[] deserialize(String siString) {
        StringTokenizer st = new StringTokenizer(siString, Utils.DELIMITER);
        String si[] = new String[st.countTokens()];
        
        int i = 0;
        while(st.hasMoreTokens()) {
            si[i] = st.nextToken();
        }
        
        return si;
    }
    
}
