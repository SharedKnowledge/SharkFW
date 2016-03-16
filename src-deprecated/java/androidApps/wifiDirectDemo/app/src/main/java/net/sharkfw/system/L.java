package net.sharkfw.system;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
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
    
    /**
     * @param kb
     * @return 
     */
    public static String kb2String(SharkKB kb) {
        StringBuilder buf = new StringBuilder();
        
        buf.append("+++++++ SharkKB ++++++++++++++++++++\n");

        buf.append(vocabulary2String(kb));
        
        try {
            buf.append(L.cps2String(kb.getAllContextPoints()));
        }
        catch(SharkKBException e) {
            return e.getMessage();
        }

        buf.append("+++++++ End SharkKB ++++++++++++++++\n");
        return buf.toString();
    }
    
    public static String vocabulary2String(SharkVocabulary v) {
        if(v == null) return "";
        
        StringBuffer buf = new StringBuffer();
        
        STSet t, p, lo, ti;
        SemanticTag o;
        
        try {
            t = v.getTopicsAsSemanticNet();
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
                L.semanticTag2StringBuffer(o, buf);
            } else {
                buf.append("empty (means any)\n");
            }
            
            // topic
            buf.append("\nTopics: ");
            L.dimension2StringBuffer(t, buf);
            
            buf.append("Peers: ");
            L.dimension2StringBuffer(p, buf);
            
            buf.append("Location: ");
            L.dimension2StringBuffer(lo, buf);
    
            buf.append("Time: ");
            L.dimension2StringBuffer(ti, buf);
    
            buf.append("\n");
        }
        catch(Exception e) {}
        buf.append("::::::: End Vocabulary ::::::::::\n");
        
        return buf.toString();
    }
    
    public static String knowledge2String(Knowledge k) {
        if(k == null) return "";
        
        StringBuilder buf = new StringBuilder();
        buf.append("+++++++ Knowledge ++++++++++++++++++++\n");
        
        buf.append(vocabulary2String(k.getVocabulary()));
        buf.append(L.cps2String(k.contextPoints()));
        buf.append("+++++++ End Knowledge ++++++++++++++++\n");

        return buf.toString();

    }
    
    /**
     * @deprecated use other variant with same name
     * @param cpEnum
     * @return 
     */
    public static String knowledge2String(Enumeration<ContextPoint> cpEnum) {
        return cps2String(cpEnum);
    }
    
    public static String cp2String(ContextPoint cp) {
        List<ContextPoint> cpList = new ArrayList<>(1);
        cpList.add(cp);
        return cps2String(Collections.enumeration(cpList));
    }
    
    public static String cps2String(Enumeration<ContextPoint> cpEnum) {
        StringBuilder buf = new StringBuilder();

        buf.append("+++++++ ContextPoints ++++++++++++++++++++\n");
        
        if(cpEnum == null || !cpEnum.hasMoreElements()) {
            buf.append("\nempty - no context points");
            return buf.toString();
        }
        
        int i = 0;
        
        while(cpEnum.hasMoreElements()) {
            ContextPoint cp = cpEnum.nextElement();
            
            buf.append("\nCP #");
            buf.append(i++);
            int infoNumber = cp.getNumberInformation();
            buf.append(" has ");
            buf.append(infoNumber);
            buf.append(" information object(s)\n");

            ContextCoordinates cc = cp.getContextCoordinates();

            buf.append("+++++++ Coordinates");
            buf.append(L.contextSpace2String(cc));
            buf.append("+++++++ Coordinates END");
            buf.append("\n");
            if(infoNumber > 0) {
                int index = 0;
                Enumeration<Information> infoEnum = cp.enumInformation();
                while(infoEnum.hasMoreElements()) {
                    Information info = infoEnum.nextElement();
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
                    buf.append("\n");
                }
            }
            buf.append("-------------------------\n");
        }
        
        buf.append(i);
        buf.append(" context points in total\n");
        buf.append("+++++++ End Context Points ++++++++++++++++\n");
        
        return buf.toString();
    }

    public static String contextSpace2String(SharkCS cs) {
        if(cs == null) {
            return null;
        }
        
        StringBuffer buf = new StringBuffer();
        
        STSet t, p, rp, lo, ti;
        SemanticTag o;
        
        t = cs.getTopics();
        p = cs.getPeers();
        rp = cs.getRemotePeers();
        ti = cs.getTimes();
        lo = cs.getLocations();
        o = cs.getOriginator();
        
        try {
            // topic
            buf.append("\nTopics: ");
            L.dimension2StringBuffer(t, buf);
            
            buf.append("Originator: ");
            if(o != null) {
                L.semanticTag2StringBuffer(o, buf);
            } else {
                buf.append("empty (means any)\n");
            }
            
            buf.append("Peer: ");
            L.dimension2StringBuffer(p, buf);
            
            buf.append("RemotePeer: ");
            L.dimension2StringBuffer(rp, buf);
            
            buf.append("Location: ");
            L.dimension2StringBuffer(lo, buf);
    
            buf.append("Time: ");
            L.dimension2StringBuffer(ti, buf);
    
            buf.append("Direction:\t");
            switch(cs.getDirection()) {
                case SharkCS.DIRECTION_IN : buf.append("in"); break;
                case SharkCS.DIRECTION_OUT : buf.append("out"); break;
                case SharkCS.DIRECTION_INOUT : buf.append("in/out"); break;
                case SharkCS.DIRECTION_NOTHING : buf.append("nothing"); break;
                default: buf.append("unknown (shouldn't be here"); break;
            }
            buf.append("\n");
        }
        catch(Exception e) {}

        return buf.toString();
    }
    
    public static void semanticTag2StringBuffer(SemanticTag st, StringBuffer buf) throws SharkKBException {
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
                buf.append("\taddress:\n");
                for(int i = 0; i < addresses.length; i++) {
                    buf.append("\t");
                    buf.append(addresses[i]);
                    buf.append("\n");
                }
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
    }
    
    public static String semanticTag2String(SemanticTag st) throws SharkKBException {
        StringBuffer buf = new StringBuffer();
        
        L.semanticTag2StringBuffer(st, buf);
        
        return buf.toString();
    }
    
    public static void dimension2StringBuffer(STSet stSet, StringBuffer buf) throws SharkKBException {
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
                L.semanticTag2StringBuffer(st, buf);
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
