package net.sharkfw.knowledgeBase.sync;

import java.util.ArrayList;
import java.util.List;
import net.sharkfw.kep.format.XMLSerializer;
import net.sharkfw.knowledgeBase.ContextCoordinates;
import net.sharkfw.knowledgeBase.SharkKBException;
import net.sharkfw.knowledgeBase.inmemory.InMemoContextPoint;
import net.sharkfw.system.L;
import net.sharkfw.system.SharkException;

/**
 * Internal class that serializes Context Coordinates for the SyncKB.
 * @author s0539710
 */
class ContextCoordinatesSerializer {
    
    protected static final String LIST_TAG = "cc_list";
    protected static final String ITEM_TAG = "cc_item";  
    protected static final String CC_TAG = "cc_coordinates";
    protected static final String VERSION_TAG = "cc_version";
    
    protected static String startTag(String tag) { return "<" + tag + ">"; }
    protected static String endTag(String tag) { return "</" + tag + ">"; }
    
    /**
     * Serializes sync context points to a list of context coordinates with their version included.
     * Context points have to be used to communicate without losing the version.
     * @param l
     * @return
     * @throws SharkKBException 
     */
    protected static String serializeContextCoordinatesList(List<SyncContextPoint> l) {
         if(l == null) {
            L.d("serializeContextCoordinatesList in ContextCoordinateSerializer: parameter was null.");
            return null;
        }
        
        XMLSerializer s = new XMLSerializer();
        StringBuilder buf = new StringBuilder();
        
        // Start list with tag
        buf.append(startTag(LIST_TAG));
        
        for (SyncContextPoint cp : l) {
            buf.append(startTag(ITEM_TAG));
                buf.append(startTag(CC_TAG));
                try {
                    buf.append(s.serializeSharkCS(cp.getContextCoordinates()));
                } catch (SharkKBException e) {
                    L.d("Tried to serialize context point but context coordinates could not be retrieved."
                            + " Context point was: " + cp.toString());
                }
                buf.append(endTag(CC_TAG));
                buf.append(startTag(VERSION_TAG));
                    buf.append(cp.getVersion());
                buf.append(endTag(VERSION_TAG));
            buf.append(endTag(ITEM_TAG));
        }
        
        // End list with tag
        buf.append(endTag(LIST_TAG));
        //return enableXMLWorkaround(buf.toString());
        return buf.toString();
    }
    
    /**
     * Deserializes sync context points from a string.
     * @param serialized - the input string
     * @return a list of context coordinates
     * @throws SharkKBException
     * @throws SharkException 
     */
    protected static List<SyncContextPoint> deserializeContextCoordinatesList(String serialized) {
        //serialized = disableXMLWorkaround(serialized);
        
        String cs;
        List<SyncContextPoint> deserialized = new ArrayList<>();
        int index = 0;
        
        // Extract and iterate over all serialized context coordinates
        while ( (index = serialized.indexOf(startTag(ITEM_TAG), index)) != -1 ) {
            // Extract the exact substring of one <item></item>
            int end = serialized.indexOf(endTag(ITEM_TAG), index) + endTag(ITEM_TAG).length();
            String substr = serialized.substring(index, end);
            // Create a new sync context poin from that information
            try {
                SyncContextPoint cp = new SyncContextPoint(new InMemoContextPoint(extractCC(substr)));
                cp.setVersion(extractVersion(substr));
                deserialized.add(cp);
            } catch (IllegalArgumentException | SharkKBException e) {
                L.e("Context coordinates deserialization error: " + e);
                continue;
            }
            // Add one to index so we don't find that exact same tag again
            index += 1;
        }
        
        return deserialized;
    }
    
    /**
     * Extracts context coordinates from a string.
     * @param s - the input string
     * @return context coordinates
     * @throws SharkKBException
     * @throws IllegalArgumentException 
     */
    protected static ContextCoordinates extractCC(String s) throws IllegalArgumentException, SharkKBException {
        if (!s.startsWith(startTag(ITEM_TAG)) || !s.endsWith(endTag(ITEM_TAG))) {
            L.d("extractCC in ContextCoordinatesSerializer: parameter does not begin and end with item tag: \n" + s);
            throw new IllegalArgumentException("extractCC in ContextCoordinatesSerializer: parameter does not begin and end with item tag: \n" + s);
        }
                
        XMLSerializer x = new XMLSerializer();
        int start = s.indexOf(startTag(CC_TAG)) + startTag(CC_TAG).length();
        int end =  s.indexOf(endTag(CC_TAG));
        try {
            return x.deserializeContextCoordinates(s.substring(start,end));
        } catch (SharkKBException e) {
            L.e("Exception while extracting context coordinates from string in ContextCoordinatesSerializer");
            throw e;
        }
    }
    
    /**
     * extracts the version from a string.
     * @param s - the input string
     * @return the version string
     * @throws IllegalArgumentException 
     */
    protected static String extractVersion(String s) throws IllegalArgumentException {
        if (!s.startsWith(startTag(ITEM_TAG)) || !s.endsWith(endTag(ITEM_TAG))) {
            L.d("extractVersion in ContextCoordinatesSerializer: parameter does not begin and end with item tag: \n" + s);
            throw new IllegalArgumentException("extractVersion in ContextCoordinatesSerializer: parameter does not begin and end with item tag: \n" + s);
        }
        int start = s.indexOf(startTag(VERSION_TAG)) + startTag(VERSION_TAG).length();
        int end =  s.indexOf(endTag(VERSION_TAG));
        return s.substring(start,end);
    }
    
    private static final String XML_INTRO = "XML_INTRO_TAG_SUBSTITUTE";
    private static final String XML_OUTRO = "XML_OUTRO_TAG_SUBSTITUTE";
    
    /* This is the dirtiest hack Ive ever done. Sorry.
     * It enables us to put XML within a property without breaking the XMLSerializer.
     * Deprecated.
     */
    private static String enableXMLWorkaround(String s) {
        return s.replaceAll("<", XML_INTRO).replaceAll(">", XML_OUTRO);
    }
    
    /* This is the dirtiest hack Ive ever done. Sorry.
     * It enables us to put XML within a property without breaking the XMLSerializer.
     * Deprecated
     */
    private static String disableXMLWorkaround(String s) {
        return s.replaceAll(XML_INTRO, "<").replaceAll(XML_OUTRO, ">");
    }
}
