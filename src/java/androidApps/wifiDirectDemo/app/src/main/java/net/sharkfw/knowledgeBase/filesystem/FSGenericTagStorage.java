package net.sharkfw.knowledgeBase.filesystem;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import net.sharkfw.knowledgeBase.AbstractSemanticTag;
import net.sharkfw.knowledgeBase.PropertyHolderDelegate;
import net.sharkfw.knowledgeBase.SemanticTag;
import net.sharkfw.knowledgeBase.SharkKBException;
import net.sharkfw.knowledgeBase.SystemPropertyHolder;
import net.sharkfw.knowledgeBase.inmemory.InMemoGenericTagStorage;
import net.sharkfw.knowledgeBase.inmemory.InMemoSemanticTag;
import net.sharkfw.knowledgeBase.inmemory.InMemoSpatialSemanticTag;
import net.sharkfw.knowledgeBase.inmemory.InMemoTimeSemanticTag;
import net.sharkfw.knowledgeBase.inmemory.InMemo_SN_TX_PeerSemanticTag;
import net.sharkfw.knowledgeBase.inmemory.InMemo_SN_TX_SemanticTag;
import net.sharkfw.system.L;

/**
 *
 * @author thsc
 */
public class FSGenericTagStorage<ST extends SemanticTag> extends 
        InMemoGenericTagStorage<ST> {
    
    private final String rootFolderName;
    private HashMap<String, String> si2folderName = new HashMap();
    
    FSGenericTagStorage(String rootFolderName) {
        this.rootFolderName = rootFolderName;
        
        // does it exist? create if no
        File folder = new File(rootFolderName);
        if(!folder.exists()) {
            folder.mkdirs();
            folder.setWritable(true);
        }
        
        try {
            // already data there?
            FSPropertyHolder.restoreFromFile(si2folderName, this.getPropertyFilename());
        } catch (SharkKBException ex) {
            // no data out there - ignore
        }
    }

    public String getFolderName() {
        return this.rootFolderName;
    }
    
	public static String hexByte(char c) {
		String ret = "";
		ret = "%" + Integer.toHexString(c >> 4).toUpperCase() + Integer.toHexString(c & 0x0F).toUpperCase();
		return ret;
	}
	
	public static String mapName(String input) {
		String ret = "";
	
		for (int i=0; i<input.length(); i++) {
			char c = input.charAt(i);	
			if (((c >= 'A') && (c <= 'Z'))  || 
				((c >= 'a') && (c <= 'z'))  ||
				((c >= '0') && (c <= '9'))  ||
				(c == '_') ||
				(c == '+') ||
				(c == '-') ||
				(c == '@') ||
				(c == '=') ||
				(c == '~') ||
				(c == '&') ||	/* Achtung! HTTP GET variable, funktioniert aber mit Windows file-API  */
				(c == '!') ||	/* Achtung! shell escape, funktioniert aber mit Windows file-API  */
				(c == '%') ||	/* unser escape Zeichen nicht weiter umwandeln */
				(c == '#') ||	/* ACHTUNG! Shell kommentar #, funktioniert aber mit Windows file-API */	
				(c == ' ') ||	/* gerade noch OK f??indows-file-API, Shit f??lle Kommandozeilenprogramme */
				(c == '.') 		/* schon im Windows-Explorer ein Problem, wenn am Anfang stehend */ 
				) {
					ret += c;
			} else {
				// nicht erlaubt mit Windows / \ | " * ? < > :		
				// degree ?
				// caret ^ 	
				ret += hexByte(c);
			}
		}		
		return ret;   	
	}

    @Override
    public void add(ST tag) throws SharkKBException {
        super.add(tag);
        
        String[] sis = tag.getSI();
        if(sis == null) {
            // makes no sense persisting a tag that cannot be retrieved
            return;
        }
        
        try {
            PropertyHolderDelegate pTag = (PropertyHolderDelegate) tag;
            
            // create unique filename 
            String tagName = tag.getName();
            
            if(tagName == null) {
                tagName = "noName";
            }
            
            // init
            String foldername = this.rootFolderName + "/" + mapName(tagName);

            // create a property holder in empty folder
            FSPropertyHolder fsProp = FSSharkKB.createFSPropertyHolder(foldername);
            
            // add to tag
            pTag.setPropertyHolder(fsProp);
            
            // persist tag - at least system properties are present
            pTag.persist();
            
            // get allocated foldername
            foldername = fsProp.getFolderName();
            
            // remember si -> folderName mapping
            for(int i = 0; i < sis.length; i++) {
                this.si2folderName.put(sis[i], foldername);
            }
            
            // make new mapping persistent
            this.persist();
        }
        catch(Exception e) {
            L.w("probleme while creating property folder: " + e.getMessage(), this);
        }
    }
    
    @Override
    public void removeSemanticTag(ST tag) {
        ST tag2Remove = null;
        try {
            // maybe that tag is just the identical object - take it from this storage
            tag2Remove = this.getSemanticTag(tag.getSI());
        } catch (SharkKBException ex) {
            // nothing to delete
            return;
        }
        
        super.removeSemanticTag(tag2Remove);
        
        try {
            PropertyHolderDelegate pTag = (PropertyHolderDelegate) tag2Remove;
            
            SystemPropertyHolder ph = pTag.getPropertyHolder();
            
            if(ph instanceof FSPropertyHolder) {
                FSPropertyHolder fsph = (FSPropertyHolder) ph;
                
                // remove property files
                fsph.remove();
                
                String folderName = fsph.getFolderName();
                
                // remove folder
                File folder = new File(folderName);
                folder.delete();
                
                // adjust foldernames
                String[] sis = tag2Remove.getSI();
                if(sis == null || sis.length == 0) { return; }
                
                for(int i = 0; i < sis.length; i++) {
                    this.si2folderName.remove(sis[i]);
                }
                
                this.persist();
            }
        }
        catch(Exception e) {
            L.w("probleme while creating property folder: " + e.getMessage(), this);
        }
    }

    @Override
    public ST getSemanticTag(String si) throws SharkKBException {    
        // already in memory ?
        ST tag = super.getSemanticTag(si);
        
        if(tag != null) {
            return tag;
        }
        
        String foldername = this.si2folderName.get(si);
        if(foldername != null) {
            tag = this.restoreSemanticTag(foldername);
            super.put(tag);
            return tag;
        }
        
        return null;
    }
    
    @Override
    public void siAdded(String addSI, ST tag) {
        if(addSI == null) { return; }
        
        super.siAdded(addSI, tag);
        
        if(tag instanceof AbstractSemanticTag) {
            AbstractSemanticTag st = (AbstractSemanticTag) tag;
            SystemPropertyHolder propertyHolder = st.getPropertyHolder();
            
            if(propertyHolder != null && propertyHolder instanceof FSPropertyHolder) {
                FSPropertyHolder fsph = (FSPropertyHolder) propertyHolder;
                this.si2folderName.put(addSI, fsph.getFolderName());
                this.persist();
            }
        }
    }
    
    @Override
    public void siRemoved(String deleteSI, ST tag) {
        if(deleteSI == null) { return; }
        
        super.siRemoved(deleteSI, tag);
        
        this.si2folderName.remove(deleteSI);
        this.persist();
    }
    
    
    private ST restoreSemanticTag(String foldername) throws SharkKBException {
        // first: create FSPropertyHolder
        FSPropertyHolder fsph = new FSPropertyHolder(foldername);
        
        // exists?
        if(!fsph.exists()) {
            throw new SharkKBException("fs property holder does not exist - cannot be refreshed: " + this.getFolderName());
        }
        
        fsph.restore();
        
        // check ST type..
        String className = fsph.getSystemProperty(AbstractSemanticTag.TYPE_SYSTEM_PROPERTY_NAME);
        if(className == null) {
            className = AbstractSemanticTag.PLAIN_ST;
        }
        
        AbstractSemanticTag tag;
        
        if(className.equalsIgnoreCase(AbstractSemanticTag.SN_TX_PST)) {
            tag = new InMemo_SN_TX_PeerSemanticTag(fsph, this);
        }
        else if(className.equalsIgnoreCase(AbstractSemanticTag.SN_TX_ST)) {
            tag = new InMemo_SN_TX_SemanticTag(fsph, this);
        }
        else if(className.equalsIgnoreCase(AbstractSemanticTag.SPATIAL_ST)) {
            tag = new InMemoSpatialSemanticTag(fsph, this);
        }
        else if(className.equalsIgnoreCase(AbstractSemanticTag.TIME_ST)) {
            tag = new InMemoTimeSemanticTag(fsph, this);
        }
        else {
            tag = new InMemoSemanticTag(fsph);
        }
        
        tag.refreshStatus();
        
        return (ST)tag;
        
    }

    private boolean refreshedAll = false;
    
    @Override
    public Enumeration<ST> tags() {    
        if(this.refreshedAll) {
            return super.tags();
        }
        
        // refresh any tag from file system
        this.refreshedAll = true;
        
        Iterator<String> siIter = this.si2folderName.keySet().iterator();
        while(siIter.hasNext()) {
            String si = siIter.next();
            try {
                // just touch it - it comes now into memory;
                this.getSemanticTag(si);
            } catch (SharkKBException ex) {
                L.w("cannot read tag from file system", this);
            }
        }
        
        return super.tags();
    }
    
    private String getPropertyFilename() {
        return this.rootFolderName + "/.tagSetProperties";
    }    
    
//    private void syncSI2Foldername() {
//        this.si2folderName = new HashMap();
//        
//        Enumeration<InMemoSemanticTag> stEnum = (Enumeration<InMemoSemanticTag>) this.tags();
//        if(stEnum != null) {
//            while(stEnum.hasMoreElements()) {
//                String foldername;
//                
//                InMemoSemanticTag tag = stEnum.nextElement();
//                SystemPropertyHolder propertyHolder = tag.getPropertyHolder();
//                if(propertyHolder instanceof FSPropertyHolder) {
//                    FSPropertyHolder fsPropertyHolder = (FSPropertyHolder) propertyHolder;
//                    foldername = fsPropertyHolder.getFolderName();
//                } else {
//                    // forget it - either all or non tag is fsTag.
//                    break;
//                }
//                
//                String[] sis = tag.getSI();
//                for(int i = 0; i < sis.length; i++) {
//                    this.si2folderName.put(sis[i], foldername);
//                }
//            }
//        }
//    }
    
//    @Override
//    protected void initSi() {
//        super.initSi();
//        this.syncSI2Foldername();
//        this.persist();
//    }

    protected void persist() {
        try {
            // persist si->filename mapping
            FSPropertyHolder.persistToFile(this.si2folderName, this.getPropertyFilename());
        } catch (FileNotFoundException ex) {
            L.w("cannot write tag set properties: " + ex.getMessage(), this);
        }
        catch (IOException ioe) {
            L.w("cannot write tag set properties: " + ioe.getMessage(), this);
        }
    }
}
