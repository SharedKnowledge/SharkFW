/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
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
 *
 * @author s0539710
 */
class ContextCoordinatesSerializer {
    
    private static final String LIST_TAG = "cc_list";
    private static final String ITEM_TAG = "cc_item";  
    private static final String CC_TAG = "cc_coordinates";
    private static final String VERSION_TAG = "cc_version";
    
    private static String startTag(String tag) { return "<" + tag + ">"; }
    private static String endTag(String tag) { return "</" + tag + ">"; }
    
    /**
     * Serializes sync context points to a list of context coordinates with their version included.
     * Context points have to be used to communicate without losing the version.
     * @param l
     * @return
     * @throws SharkKBException 
     */
    protected static String serializeContextCoordinatesList(List<SyncContextPoint> l) throws SharkKBException {
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
                    buf.append(s.serializeSharkCS(cp.getContextCoordinates()));
                buf.append(endTag(CC_TAG));
                buf.append(startTag(VERSION_TAG));
                    cp.getVersion();
                buf.append(endTag(VERSION_TAG));
            buf.append(endTag(ITEM_TAG));
        }
        
        // End list with tag
        buf.append(endTag(LIST_TAG));
        return buf.toString();
    }
    
    protected static List<SyncContextPoint> deserializeContextCoordinatesList(String serialized) throws SharkKBException, SharkException {
        String cs;
        List<SyncContextPoint> deserialized = new ArrayList<>();
        int index = 0;
        // Extract and iterate over all serialized context coordinates
        while ( (index = serialized.indexOf(startTag(ITEM_TAG), index)) != -1 ) {
            // Extract the exact substring of one <item></item>
            String substr = serialized.substring(index, serialized.indexOf(endTag(ITEM_TAG), index));
            // Create a new sync context poin from that information
            try {
                SyncContextPoint cp = new SyncContextPoint(new InMemoContextPoint(extractCC(substr)));
                cp.setVersion(extractVersion(substr));
                deserialized.add(cp);
            } catch (IllegalArgumentException e) {
                throw new SharkException("Context coordinates deserialization error: " + e);
            }
        }
        
        return deserialized;
    }
    
    protected static ContextCoordinates extractCC(String s) throws SharkKBException, IllegalArgumentException {
        if (!s.startsWith(startTag(ITEM_TAG)) || !s.endsWith(endTag(ITEM_TAG))) {
            L.d("extractCC in ContextCoordinatesSerializer: parameter does not begin and end with item tag: \n" + s);
            throw new IllegalArgumentException("extractCC in ContextCoordinatesSerializer: parameter does not begin and end with item tag: \n" + s);
        }
                
        XMLSerializer x = new XMLSerializer();
        int start = s.indexOf(startTag(CC_TAG)) + CC_TAG.length();
        int end =  s.indexOf(endTag(CC_TAG));
        try {
            return x.deserializeContextCoordinates(s.substring(start,end));
        } catch (SharkKBException e) {
            L.e("Exception while extracting context coordinates from string in ContextCoordinatesSerializer");
            throw e;
        }
    }
    
    protected static String extractVersion(String s) throws IllegalArgumentException {
        if (!s.startsWith(startTag(ITEM_TAG)) || !s.endsWith(endTag(ITEM_TAG))) {
            L.d("extractVersion in ContextCoordinatesSerializer: parameter does not begin and end with item tag: \n" + s);
            throw new IllegalArgumentException("extractVersion in ContextCoordinatesSerializer: parameter does not begin and end with item tag: \n" + s);
        }
        int start = s.indexOf(startTag(VERSION_TAG)) + VERSION_TAG.length();
        int end =  s.indexOf(endTag(VERSION_TAG));
        return s.substring(start,end);
    }
}
