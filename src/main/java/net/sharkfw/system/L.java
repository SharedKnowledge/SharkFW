package net.sharkfw.system;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.*;

import net.sharkfw.asip.ASIPInformation;
import net.sharkfw.asip.ASIPInformationSpace;
import net.sharkfw.asip.ASIPSpace;
import net.sharkfw.knowledgeBase.*;

/**
 *
 * @author redmann, Thomas Schwotzer
 */
public class L {
    
    public static final int LOGLEVEL_SILENT = 0;
    public static final int LOGLEVEL_ERROR = 1;
    public static final int LOGLEVEL_WARNING = 2;
    public static final int LOGLEVEL_DEBUG = 3;
    public static final int LOGLEVEL_ALL = 4;
    
    private static int loglevel;
    
    private static PrintStream out = System.out;
    private static PrintStream err = System.err;
    
    static {
        L.setLogLevel(L.LOGLEVEL_SILENT);
    }
    
    public static void setLogLevel(int level) {
        L.loglevel = level;
    }
    
    public static void setLogfile(String filename) {
        File file = new File(filename);
        try {
            FileOutputStream fos = new FileOutputStream(file, true);
            L.out = new PrintStream(fos);
            L.err = L.out;
        } catch (FileNotFoundException ex) {
            // 
        }
    }

    public static void setLogStreams(PrintStream out, PrintStream err) {
        L.out = out;
        L.err = err;
    }
    
    private static void doLog(Object o, String msg, int level) {
        String loglevelString = "LOG";

        switch(level) {
            case LOGLEVEL_ERROR: loglevelString = "ERROR"; break;
            case LOGLEVEL_WARNING: loglevelString = "WARNING"; break;
            case LOGLEVEL_DEBUG: loglevelString = "DEBUG"; break;
        }
        
        String className = "noClass";
        
        if(o != null) {
            className = o.getClass().getName();
        }
        
        String logString = getTimestamp() + " ["+ className +"] " + loglevelString + ": " + msg;
        
        if(loglevel == LOGLEVEL_ERROR) {
            L.err.println(logString);
            L.err.flush();
        } else {
            L.out.println(logString);
            L.out.flush();
        }
    }

    /**
     * Create a Log-Message msg for given object(class)
     *
     * @param msg
     * @param o
     */
    public static void l(String msg, Object o) {
        if(loglevel == LOGLEVEL_ALL) {
            L.doLog(o, msg, LOGLEVEL_ALL);
        }
    }

    /**
     * Create a Debug-Message
     *
     * @param msg
     * @param o
     */
    public static void d(String msg, Object o) {

        if(loglevel == LOGLEVEL_ALL 
                || loglevel == LOGLEVEL_DEBUG
                ) {
            L.doLog(o, msg, LOGLEVEL_DEBUG);
        }
    }

    public static void d(String msg) { L.d(msg, null); }
    public static void w(String msg) { L.w(msg, null); }
    public static void e(String msg) { L.e(msg, null); }
    public static void l(String msg) { L.l(msg, null); }
    
    /**
     * Create an Error-Message
     *
     * @param msg
     * @param o
     */
    public static void e(String msg, Object o) {

        if(loglevel == LOGLEVEL_ALL 
                || loglevel == LOGLEVEL_DEBUG
                || loglevel == LOGLEVEL_WARNING
                || loglevel == LOGLEVEL_ERROR
                ) {
            L.doLog(o, msg, LOGLEVEL_ERROR);
        }
    }


    /**
     * Create a Warning-Message
     *
     * @param msg
     * @param o
     */
    public static void w(String msg, Object o) {
    
        if(loglevel == LOGLEVEL_ALL 
                || loglevel == LOGLEVEL_DEBUG
                || loglevel == LOGLEVEL_WARNING
                || loglevel == LOGLEVEL_ERROR
                ) {
            L.doLog(o, msg, LOGLEVEL_WARNING);
        }
    }

    private static String getTimestamp() {
      long currentTime = System.currentTimeMillis();
      SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
      
      return sdf.format(new Date(currentTime));
    }
    
    public static String kb2String(SharkKB kb) {
        return L.kb2String(kb, false);
    }
    
    /**
     * @param kb
     * @return 
     */
    public static String kb2String(SharkKB kb, boolean showProperties) {
        StringBuilder buf = new StringBuilder();
        
        buf.append("+++++++ SharkKB ++++++++++++++++++++\n");

        buf.append(vocabulary2String(kb, showProperties));
/*
        try {
            buf.append(L.cps2String(kb.getAllContextPoints()));
        }
        catch(SharkKBException e) {
            return e.getMessage();
        }
*/
        try {
            buf.append(L.infoSpaces2String(kb.getAllInformationSpaces(), 
                    showProperties));
        }
        catch(SharkKBException e) {
            return e.getMessage();
        }

        buf.append("+++++++ End SharkKB ++++++++++++++++\n");
        return buf.toString();
    }

    public static String infoSpaces2String(
            Iterator<ASIPInformationSpace> infoSpacesIter) 
            throws SharkKBException {
        return L.infoSpaces2String(infoSpacesIter, false);
    }
    
    public static String infoSpaces2String(
            Iterator<ASIPInformationSpace> infoSpacesIter,
            boolean showProperties) throws SharkKBException {
        
        StringBuilder buf = new StringBuilder();

        buf.append("\n+++++++ InformationSpaces ++++++++++++++++++++\n");

        if(infoSpacesIter == null || !infoSpacesIter.hasNext()) {
            buf.append("\nempty - no information spaces");
            return buf.toString();
        }

        int i = 0;

        while(infoSpacesIter.hasNext()) {
            ASIPInformationSpace infoSpace = infoSpacesIter.next();

            buf.append("\nInfoSpace #");
            buf.append(i++);
            int infoNumber = infoSpace.numberOfInformations();
            buf.append(" has ");
            buf.append(infoNumber);
            buf.append(" information object(s)\n");

            ASIPSpace asipSpace = infoSpace.getASIPSpace();

            buf.append("+++ ASIPSpace\n");
            buf.append(L.asipSpace2String(asipSpace, showProperties));
            buf.append("+++ ASIPSpace END");
            buf.append("\n");
            if(infoNumber > 0) {
                int index = 0;
                Iterator<ASIPInformation> infoIter = infoSpace.informations();
                while(infoIter.hasNext()) {
                    ASIPInformation info = infoIter.next();
                    String name = info.getName();
                    String contentType = info.getContentType();
                    long len = info.getContentLength();

                    buf.append(index++);
                    buf.append(": name: ");
                    buf.append(name);
                    buf.append("; contentType: ");
                    buf.append(contentType);
                    buf.append("; size: ");
                    buf.append(len);
                    buf.append("; content: ");
                    try {
                        buf.append(info.getContentAsString());
                    } catch (SharkKBException e) {
                        buf.append("No string representation");

                    }
                    if(showProperties) {
                        if(info instanceof Information) {
                            Information infoinfo = (Information) info;
                            buf.append(L.properties2String(infoinfo));
                        }
                    }
                    
                    buf.append("\n");
                }
            }
            buf.append("-------------------------\n");
        }

        buf.append(i);
        buf.append(" info spaces total\n");
        buf.append("+++++++ End InformationSpaces ++++++++++++++++\n");

        return buf.toString();
    }

    public static String asipSpace2String(ASIPSpace asipSpace) throws SharkKBException {
        return L.asipSpace2String(asipSpace, false);
    }
    
    public static String asipSpace2String(ASIPSpace asipSpace, 
            boolean showProperties) throws SharkKBException {
        
        StringBuffer buf = new StringBuffer();

        buf.append("+++++++ Topics: ");
        L.dimension2StringBuffer(asipSpace.getTopics(), buf, showProperties);

        buf.append("+++++++ Types: ");
        L.dimension2StringBuffer(asipSpace.getTypes(), buf, showProperties);

        buf.append("+++++++ Approvers: ");
        L.dimension2StringBuffer(asipSpace.getApprovers(), buf, showProperties);

        buf.append("+++++++ Sender: ");
        PeerSemanticTag sender = asipSpace.getSender();
        if(sender != null) {
            buf.append(L.semanticTag2String(sender, showProperties));
        } else {
            buf.append("empty (means any)");
        }
        buf.append("\n");

        buf.append("+++++++ Receiver: ");
        L.dimension2StringBuffer(asipSpace.getReceivers(), buf, showProperties);

        buf.append("+++++++ Times: ");
        L.dimension2StringBuffer(asipSpace.getTimes(), buf, showProperties);

        buf.append("+++++++ Locations: ");
        L.dimension2StringBuffer(asipSpace.getLocations(), buf, showProperties);

        buf.append("Direction:\t");
        switch(asipSpace.getDirection()) {
            case ASIPSpace.DIRECTION_IN : buf.append("in"); break;
            case ASIPSpace.DIRECTION_OUT : buf.append("out"); break;
            case ASIPSpace.DIRECTION_INOUT : buf.append("in/out"); break;
            case ASIPSpace.DIRECTION_NOTHING : buf.append("nothing"); break;
            default: buf.append("L.asipSpace2String: unknown (shouldn't be here"); break;
        }
        buf.append("\n");

        return buf.toString();
    }

    public static String vocabulary2String(SharkVocabulary v) {
        return L.vocabulary2String(v, false);
    }
    
    public static String vocabulary2String(SharkVocabulary v, boolean showProperties) {
        if(v == null) return "";
        
        StringBuffer buf = new StringBuffer();
        
        STSet t, ty, p, lo, ti;
        SemanticTag o;
        
        try {
            t = v.getTopicsAsSemanticNet();
            ty = v.getTypeSTSet();
            p = v.getPeersAsSemanticNet();
            ti = v.getTimeSTSet();
            lo = v.getSpatialSTSet();
            o = v.getOwner();
        }
        catch(SharkKBException e) {
            return e.getMessage();
        }
        
        buf.append("::::::: Vocabulary ::::::::::::::\n");
        try {
            buf.append("\nKB-Owner: ");
            if(o != null) {
                L.semanticTag2StringBuffer(o, buf, showProperties);
            } else {
                buf.append("empty (means any)\n");
            }

            // topic
            buf.append("\nTopics: ");
            L.dimension2StringBuffer(t, buf, showProperties);

            // type
            buf.append("\nTypes: ");
            L.dimension2StringBuffer(ty, buf, showProperties);

            // peers
            buf.append("Peers: ");
            L.dimension2StringBuffer(p, buf, showProperties);

            // locations
            buf.append("Location: ");
            L.dimension2StringBuffer(lo, buf, showProperties);

            // time
            buf.append("Time: ");
            L.dimension2StringBuffer(ti, buf, showProperties);
    
            buf.append("\n");
        }
        catch(Exception e) {}
        buf.append("::::::: End Vocabulary ::::::::::\n");
        
        return buf.toString();
    }
    
    public static void semanticTag2StringBuffer(SemanticTag st, StringBuffer buf)
            throws SharkKBException {
        
        L.semanticTag2StringBuffer(st, buf, false);
    }
    
    public static void semanticTag2StringBuffer(SemanticTag st, 
            StringBuffer buf, boolean showProperties) throws SharkKBException {
        
        if(st == null) {
            return;
        }

        buf.append("\n\ttag: \"" + st.getName() + "\"\n");
        String[] sis = st.getSI();
        for(int j = 0; j < sis.length; j++) {
            buf.append("\t");
            buf.append(sis[j]);
            buf.append("\n");
        }

        if(st instanceof PeerSemanticTag) {
            PeerSemanticTag pst = (PeerSemanticTag)st;
            String[] addresses = pst.getAddresses();
            if(addresses.length > 0) {
                buf.append("\taddress:");
                for(int i = 0; i < addresses.length; i++) {
                    if(addresses[i] != null && addresses[i].length() > 0) {
                        buf.append("\n\t");
                        buf.append(addresses[i]);
                    }
                }
                buf.append("\n");
            }
        }
        
        if(st instanceof SNSemanticTag) {
            SNSemanticTag sn = (SNSemanticTag) st;
            Enumeration<String> pNameEnum = sn.predicateNames();
            if(pNameEnum != null) {
                while(pNameEnum.hasMoreElements()) {
                    String predicateName = pNameEnum.nextElement();
                    Enumeration<SNSemanticTag> tagEnum = sn.targetTags(predicateName);
                    while(tagEnum.hasMoreElements()) {
                        SNSemanticTag targetTag = tagEnum.nextElement();
                        String firstSI;
                        String[] si = targetTag.getSI();
                        if(si != null) {
                            firstSI = si[0];
                        } else {
                            firstSI = "ANY";
                        }
                        buf.append("\t").append(predicateName).append(" target: ").append(firstSI).append("\n");
                    }
                }
            }
        }
        
        if(showProperties) {
            L.properties2StringBuffer(st, buf);
        }
    }
    
    public static String properties2String(SystemPropertyHolder ph) throws SharkKBException {
        StringBuffer buf = new StringBuffer();
        L.properties2StringBuffer(ph, buf);
        return buf.toString();
    }
        
    public static void properties2StringBuffer(SystemPropertyHolder ph, 
            StringBuffer buf) throws SharkKBException {
        
        buf.append("\n\tproperties:");
        
        Enumeration<String> propNamesEnum = ph.propertyNames(true);
        if(propNamesEnum == null || !propNamesEnum.hasMoreElements()) {
            buf.append("<none>\n");
            return;
        }
        
        while(propNamesEnum.hasMoreElements()) {
            String propName = propNamesEnum.nextElement();
            
            buf.append("\n\tname:");
            buf.append(propName);
            buf.append("\n\t\tproperty / system property: ");
            
            String propValue = ph.getProperty(propName);
            if(propValue != null) {
                buf.append(propValue);
            } else {
                buf.append("<none>");
            }
            
            buf.append("  /  ");
            
            propValue = ph.getSystemProperty(propName);
            if(propValue != null) {
                buf.append(propValue);
            } else {
                buf.append("<none>");
            }
        }
        buf.append("\n");
    }
    
    public static String semanticTag2String(SemanticTag st) throws SharkKBException {
        return L.semanticTag2String(st, false);
    }
    
    public static String semanticTag2String(SemanticTag st, 
            boolean showProperties) throws SharkKBException {
        
        StringBuffer buf = new StringBuffer();
        
        L.semanticTag2StringBuffer(st, buf, showProperties);
        
        return buf.toString();
    }

    public static void dimension2StringBuffer(STSet stSet, StringBuffer buf) throws SharkKBException {
        L.dimension2StringBuffer(stSet, buf, false);
    }
    
    public static void dimension2StringBuffer(STSet stSet, 
            StringBuffer buf, boolean showProperties) throws SharkKBException {
        
        if(stSet == null)  {
            buf.append("empty (means any)\n");
            return;
        }

        Enumeration<SemanticTag> e = stSet.tags();
        if(!e.hasMoreElements()) {
            buf.append("empty (means any)\n");
        } else {
            while(e.hasMoreElements()) {
                SemanticTag st = e.nextElement();
                L.semanticTag2StringBuffer(st, buf, showProperties);
            }
        }
    }

    public static void printByte(byte[] b, String label) {
        L.out.print(L.byteArrayToString(b,label));
        L.out.flush();
    }
    
    public static String byteArrayToString(byte[] b, String label) {
        StringBuilder buf = new StringBuilder();
        buf.append(">>>>>>>>>>>>>>>> bytearray / length / label: ");
        buf.append(b.length);
        buf.append(" / ");
        buf.append(label);
        buf.append("\n");
        
        for(int i = 0; i < b.length; i++) {
            buf.append(b[i]);
            buf.append(" ");
        }
        
        buf.append("\n");
        
        return buf.toString();
    }

    public static String stSet2String(STSet fragment) throws SharkKBException {
        StringBuffer buf = new StringBuffer();
        L.dimension2StringBuffer(fragment, buf);
        
        return buf.toString();
    }
}
