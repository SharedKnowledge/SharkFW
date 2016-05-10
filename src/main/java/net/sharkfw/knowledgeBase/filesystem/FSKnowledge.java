package net.sharkfw.knowledgeBase.filesystem;

import java.util.Enumeration;
import java.util.Vector;
import net.sharkfw.knowledgeBase.ContextCoordinates;
import net.sharkfw.knowledgeBase.ContextPoint;
import net.sharkfw.knowledgeBase.SharkKBException;
import net.sharkfw.knowledgeBase.SharkVocabulary;
import net.sharkfw.knowledgeBase.inmemory.InMemoKnowledge;
import net.sharkfw.system.Util;

/**
 *
 * @author thsc
 */
public class FSKnowledge extends InMemoKnowledge {
    private final SharkVocabulary fskb;
    private final String foldername;
    private Vector<String> cpFolderNames;
    
    private FSPropertyHolder fsph;
    
    public FSKnowledge(SharkVocabulary background, String foldername) {
        super(background);
        
        this.foldername = foldername;
        
        this.fskb = background;
        
        this.cpFolderNames = new Vector();
        
        this.fsph = new FSPropertyHolder(this.foldername);
    }
    
    ContextPoint createContextPoint(ContextCoordinates coordinates) throws SharkKBException {
        // create property holder
        FSPropertyHolder cp_fsph = FSSharkKB.createFSPropertyHolder(this.getCPsFolderName());
        
        FSContextPoint cp = new FSContextPoint(coordinates, cp_fsph);
        cp.persist();
        
        super.addContextPoint(cp);
        
        // remember that folder
        this.cpFolderNames.add(cp_fsph.getFolderName());
        
        this.persist();
        
        return cp;
    }
    
    @Override
    public void removeContextPoint(ContextPoint cp) {
        // remove persistent data
        try {
            FSContextPoint fscp = (FSContextPoint) cp;
            
            // if fs cp - delete folder
            FSPropertyHolder cpFSPh = (FSPropertyHolder) fscp.getPropertyHolder();
            
            String folderName = cpFSPh.getFolderName();

            // remove entry in folder list
            this.cpFolderNames.remove(folderName);
            
            this.persist();
            
            // remove directory and subdirectories
            FSSharkKB.removeFSStorage(folderName);
        }
        catch(ClassCastException cce) {
            // ignore
        }
        
        // remove from memory
        super.removeContextPoint(cp);
    }
    
    String getCPsFolderName() {
        return this.foldername + "/cp";
    }
    
    public static final String CP_FOLDERNAME_PROPERTY = "fsk_cpFolder";
    public static final String DELIMITER = "|";
    
    public void persist() {
        try {
            this.fsph.persist();
        } catch (SharkKBException ex) {
            //
        }
        // write cp folder names
        String foldernames = Util.enumeration2String(this.cpFolderNames.elements(), DELIMITER);
        
        this.fsph.setSystemProperty(CP_FOLDERNAME_PROPERTY, foldernames);
    }
    
    public void refreshStatus() throws SharkKBException {
        try {
            this.fsph.restore();
        } catch (SharkKBException ex) {
            //
        }
        
        String foldernames = this.fsph.getSystemProperty(CP_FOLDERNAME_PROPERTY);
        
        Vector<String> folderNames = Util.string2Vector(foldernames, DELIMITER);
        if(folderNames == null) {
            return; // nothing to refresh here
        }
        
        this.cpFolderNames = folderNames;
        
        // bring all cps (back) into memory
        Enumeration<String> cpFoldernameEnum = this.cpFolderNames.elements();
        while(cpFoldernameEnum.hasMoreElements()) {
            String cpFoldername = cpFoldernameEnum.nextElement();
            
            // create property holder
            FSPropertyHolder fsph = new FSPropertyHolder(cpFoldername);
            fsph.restore();
            
            // create cp with this propery holder
            FSContextPoint fscp = new FSContextPoint(fsph);
            fscp.refreshStatus();
            
            // add it to memory
            super.addContextPoint(fscp);
        }
    }
}
