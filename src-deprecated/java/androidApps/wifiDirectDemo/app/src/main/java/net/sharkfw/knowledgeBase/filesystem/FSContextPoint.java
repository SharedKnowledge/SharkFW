package net.sharkfw.knowledgeBase.filesystem;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.Vector;
import net.sharkfw.kep.format.XMLSerializer;
import net.sharkfw.knowledgeBase.ContextCoordinates;
import net.sharkfw.knowledgeBase.Information;
import net.sharkfw.knowledgeBase.SharkKBException;
import net.sharkfw.knowledgeBase.inmemory.InMemoContextPoint;
import net.sharkfw.knowledgeBase.inmemory.InMemoSharkKB;
import net.sharkfw.system.L;
import net.sharkfw.system.Util;

/**
 *
 * @author thsc
 */
public class FSContextPoint extends InMemoContextPoint {
    private Vector<String> infoFolder = new Vector();
    
    FSContextPoint(ContextCoordinates coordinates, FSPropertyHolder persistentHolder) {
        super(coordinates);
        
        this.setPropertyHolder(persistentHolder);
    }

    FSContextPoint(FSPropertyHolder fsph) {
        super(fsph);
    }
    
    private String getInfoFoldername() {
        return ((FSPropertyHolder)this.getPropertyHolder()).getFolderName() + "/info";
    }
    
    private String getCPFildername(){
        return ((FSPropertyHolder)this.getPropertyHolder()).getFolderName();
    }
    
    @Override
    public FSInformation addInformation() {
        FSPropertyHolder fsph = FSSharkKB.createFSPropertyHolder(this.getInfoFoldername());
        
        FSInformation newInfo = null;
        try {
            newInfo = new FSInformation(fsph);
        } catch (SharkKBException ex) {
            L.w("couldn't restore information from file", this);
        }
        super.putInformation(newInfo);
        
        // remember folder
        this.infoFolder.add(fsph.getFolderName());
        
        newInfo.persist();
        this.persist();
        
        return newInfo;
    }

    /**
     * make a copy in this fs implementation
     * @param info 
     */
    @Override
    public void addInformation(Information info){
        FSInformation infoCopy = this.addInformation();
        
        Util.copyPropertiesFromPropertyHolderToPropertyHolder(info, infoCopy);
        OutputStream writeAccess = infoCopy.getOutputStream();
        
        InputStream readAccess = null;
        try {
            readAccess = info.getInputStream();
        } catch (SharkKBException e) {
        }
        
        info.streamContent(writeAccess);

        try {
            // close stream
            writeAccess.close();
        } catch (IOException ex) {
            L.d("cannot write fileoutputstream - strange", this);
        }
        
        /* information might have name and content-type
         * It looks very strange and actually it is:
         * 
         * But.. Info becomes aware of its name and content-type
         * by resetting.
         */
        String value = info.getName();
        try {
            infoCopy.setName(value);
        } catch (SharkKBException ex) {
            //this error can not be thrown, because the name was already
            //checked when the name of the infoCopy was set and therefore it can not contain false characters
        }
        
        value = info.getContentType();
        infoCopy.setContentType(value);
    }

    @Override
    public void removeInformation(Information info) {
        //this.infoFolder.remove(this.getInfoFoldername());
        super.removeInformation(info);
        
        FSInformation fsinfo = (FSInformation) info;
        
        FSSharkKB.removeFSStorage(fsinfo.getPath());        
        
        this.infoFolder.remove(fsinfo.getPath());
        if (this.information.isEmpty()){
            File file = new File(getCPFildername() + "/.sharkfw_st_systemProperties");
            file.delete();
        } else {
            this.persist();
        }
    }
    
    
    
    ///////////////////////////////////////////////////////////////
    //                       persistency                         //
    ///////////////////////////////////////////////////////////////
    
    public static final String CP_COORDINATE = "coordinates";
    public static final String INFO_FOLDERNAME_PROPERTY = "fscp_infoFolder";

    public static final String DELIMITER = "|";
    
    /**
     * write status into system properties
     */
    @Override
    public void persist() {
        super.persist();
        
        XMLSerializer xs = new XMLSerializer();
        try {
            String cooString = xs.serializeSharkCS(this.getContextCoordinates());
            
            // save
            this.setSystemProperty(CP_COORDINATE, cooString);
            
        } catch (SharkKBException ex) {
            L.w(ex.getMessage(), this);
        }

		if (this.infoFolder != null) {
        	// info folder
        	String foldernames = Util.enumeration2String(this.infoFolder.elements(), DELIMITER);
        
        	if(foldernames != null && foldernames.length() > 0) {
                    this.setSystemProperty(INFO_FOLDERNAME_PROPERTY, foldernames);
        	}
        }
    }
    
    @Override
    public void refreshStatus() {
        super.refreshStatus();
        
        // refresh coordinates from system
        String cooString = this.getSystemProperty(CP_COORDINATE);
        
        InMemoSharkKB imkb = null;
        imkb = new InMemoSharkKB();
        
        XMLSerializer xs = new XMLSerializer();
        try {
            // create in memory copy and use it
            this.setContextCoordinates(xs.deserializeContextCoordinates(imkb, cooString));
        } catch (SharkKBException ex) {
            L.w("cannot deserialize context coordinates from persistent storage: " + ex.getMessage(), this);
        }

        // info folder
        String foldernames = this.getSystemProperty(INFO_FOLDERNAME_PROPERTY);

        if(foldernames != null) {
            this.infoFolder = Util.string2Vector(foldernames, DELIMITER);

			if (this.infoFolder != null) {
				// recreate Information
				Enumeration<String> infoFoldernameEnum = this.infoFolder.elements();
				while(infoFoldernameEnum.hasMoreElements()) {
                	String infoFoldername = infoFoldernameEnum.nextElement();

                	// create property holder
                	FSPropertyHolder fsph = new FSPropertyHolder(infoFoldername);

                	// create cp with this propery holder
                	FSInformation fsInfo;
                	try {
                    	fsInfo = new FSInformation(fsph);
                    	fsInfo.refreshStatus();
                    	// add it to memory
                    	super.putInformation(fsInfo);
                	} catch (SharkKBException ex) {
                    	// 
                	}
            	}
            }
        } else {
            L.w("strange: there was a cp read from file without information - that cp shouldn't exist: TODO", this);
        }
    }
}
