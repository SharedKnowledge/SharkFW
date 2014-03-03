package net.sharkfw.knowledgeBase.filesystem;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;

import net.sharkfw.system.TimeLong;
import net.sharkfw.knowledgeBase.SharkKBException;
import net.sharkfw.knowledgeBase.inmemory.InMemoInformation;
import net.sharkfw.system.L;

/**
 *
 * @author thsc
 */
public class FSInformation extends InMemoInformation {
    private File contentFile;
    private String folder;
    public static final String INFO_FILE = "fsinfo_file";
    
    private final static String STD_CONTENT_NAME = "content";
    
    FSInformation(FSPropertyHolder fsph) throws SharkKBException {
        super(fsph);
        fsph.restore();
        
        this.folder = fsph.getFolderName();
        
        // create content file
//        this.contentFile = new File(fsph.getFolderName() + "/content");
        
        this.setupContentFile();
        this.setupUniqueID();
    }

    /**
     * calculate content file name by information name and content type
     */
    private void setupContentFile() throws SharkKBException {
        // is there already a content file
        if(this.contentFile != null) {
            if(this.contentFile.exists()) {
                // save its name
                try {
                    this.setSystemProperty(INFO_FILE, this.contentFile.getCanonicalPath());
                }
                catch(IOException ioe) {
                    throw new SharkKBException(ioe.getMessage());
                }
            }
        } else {
            // restore
            String filename = this.getSystemProperty(INFO_FILE);
            if(filename != null && filename.length() > 0) {
                this.contentFile = new File(filename);
            }
        }
        
        String name = this.getName();
        if(name == null) {
            name = FSInformation.STD_CONTENT_NAME;
        }
        
        String extension = this.contentType2Extension(this.getContentType());
        if(extension == null) {
            extension = "";
        } else {
            extension = "." + extension;
        }
                
		String newFilename = this.folder + "/" + FSGenericTagStorage.mapName(name) + extension;
        
        File newContentFile = new File(newFilename);
        
        // is there already a non empty content file?
        if(this.contentFile != null && this.contentFile.exists()) {
            if(this.contentFile.renameTo(newContentFile)) {
                try {
                    this.contentFile = newContentFile;
                    this.setSystemProperty(INFO_FILE, this.contentFile.getCanonicalPath());
                }
                catch(IOException ieo) {
                    throw new SharkKBException(ieo.getMessage());
                }
            }
        } else {
            this.contentFile = newContentFile;
        }
    }
    
    /*
     * set unique id for new added information
     */
    private void setupUniqueID(){
        if (this.getUniqueID().equals("")){
            //there is no id yet, so we set it
            this.setProperty(InMemoInformation.INFO_ID_PROPERTY_NAME, java.util.UUID.randomUUID().toString());
        }
    }
    
    ////////////////////////////////////////////////////////////////////
    //                      content handling                          //
    ////////////////////////////////////////////////////////////////////
    
    public String getContentFilename() throws IOException {
        return this.contentFile.getCanonicalPath();
    }
    
    @Override
    public long getContentLength() {
        return this.contentFile.length();
    }
    
    @Override
    public void setContent(String content) {
        FileOutputStream fos;
        this.setContentType("text/plain");
        
        try {
            fos = new FileOutputStream(this.contentFile);
            PrintStream ps = new PrintStream(fos);
            ps.print(content);
            fos.close();
            
            this.setTimes();
            
            this.persist();
        } catch (Exception ex) {
            L.e("couldn't write information to file: " + ex.getMessage(), this);
        }
    }
    
    @Override
    public void setContent(byte[] content) {
        FileOutputStream fos;
        try {
            fos = new FileOutputStream(this.contentFile);
            fos.write(content);
            fos.close();
            
            this.setTimes();
            
            this.persist();
        } catch (Exception ex) {
            L.e("couldn't write information to file: " + ex.getMessage(), this);
        }
    }
    
    private static final int MAX_BUFFER_LEN = 1024*100; // 100 kByte
    
    @Override
    public void setContent(InputStream is, long len) {
        byte[] buffer = null;
        int index = 0;
        
        FileOutputStream fos;
        try {
            fos = new FileOutputStream(this.contentFile);
            
            while(len > 0) {
                int byte2write = (int) (len > MAX_BUFFER_LEN ? MAX_BUFFER_LEN : len);
                if(buffer == null) {
                    buffer = new byte[byte2write];
                }

                // fill buffer
                int bytesRead = is.read(buffer);

                // write to content
                fos.write(buffer, 0, bytesRead);
                
                len -= bytesRead;
            }
            
            fos.close();
            
            this.setTimes();
            this.persist();
            
        } catch (Exception ex) {
            L.e("couldn't write information to file: " + ex.getMessage(), this);
        }
    }
    
    @Override
    public void removeContent() {
        this.contentFile.delete();
        this.setTimes();
    }
    
    @Override
    public void streamContent(OutputStream os) {
        FileInputStream fis;
        try {
            fis = new FileInputStream(this.contentFile);
            
            int b = fis.read();
            while(b != -1) {
                os.write(b);
                b = fis.read();
            }
            
            fis.close();
        } catch (Exception ex) {
            L.l("couldn't read information to file (might be ok - no content)" + ex.getMessage(), this);
        }
//        finally {
//            try {
//                os.flush(); // DON'T flush it - quite tricky M2S stream stops transmitting after flushing...
//            } catch (IOException ex1) {
//                // ignore
//            }
//        }
    }
    
    /**
     * Handle with care - this creates a byte array and copies any byte 
     * into that array. Might be huge.
     * 
     * @return 
     */
    @Override
    public byte[] getContentAsByte() {

        int len = (int) this.contentFile.length();
        
        byte[] content = new byte[len];
        
        FileInputStream fis;
        try {
            fis = new FileInputStream(this.contentFile);
            fis.read(content);
            fis.close();
        } catch (Exception ex) {
            L.l("couldn't read information to file (might be ok - no content)" + ex.getMessage(), this);
        }
        
        return content;
    }
    
    @Override
    public OutputStream getOutputStream() {
        try {
            return new FileOutputStream(this.contentFile);
        } catch (FileNotFoundException ex) {
        }
        
        return null;
    }

    @Override
    public void setContentType(String mimeType) {
        super.setContentType(mimeType);
        this.persist();
        
        try {
            this.setupContentFile();
        }
        catch(SharkKBException e) {
            L.e("unhandled exception: " + e.getMessage(), this);
        }
    }
    
    @Override
    public void setName(String name) throws SharkKBException{
        super.setName(name);
        
        try {
            this.setupContentFile();
        }
        catch(SharkKBException e) {
            L.e("unhandled exception: " + e.getMessage(), this);
        }
    }
    
    @Override
    protected void setLastModified(long time) {
        super.setLastModified(time);
        this.persist();
    }

    @Override
    protected void setCreationTime(long time) {
        super.setCreationTime(time);
        this.persist();
    }
    
    @Override
    public void persist() {
        super.persist();
        
        // content-Type
        this.setProperty(INFO_CONTENT_TYPE, this.getContentType());
        
        // creationTime
        this.setProperty(INFO_CREATION_TIME, String.valueOf(this.creationTime()));
        
        // lastModified
        this.setProperty(INFO_LAST_MODIFED, String.valueOf(this.lastModified()));
        
        //unique id
        this.setProperty(INFO_ID_PROPERTY_NAME, this.getUniqueID());
    }
    
    @Override
    public void refreshStatus() {
        super.refreshStatus();
        
        String value;
        // content-Type
        value = this.getProperty(INFO_CONTENT_TYPE);
        if(value != null) {
            this.setContentType(value);
        }

        long time;
        // creationTime
        value = this.getProperty(INFO_CREATION_TIME);
        if(value != null) {
            time = TimeLong.parse(value);
            this.setCreationTime(time);
        }
        
        // lastModified
        value = this.getProperty(INFO_LAST_MODIFED);
        if(value != null) {
            time = TimeLong.parse(value);
            this.setLastModified(time);
        }

    }
    
    private String contentType2Extension(String contentType) {
        if(contentType.equalsIgnoreCase(INFO_DEFAULT_CONTENT_TYPE)) {
            return null;
        } 
        
        ////////////////////////////////////////////////////////////////
        //                            txt                             //
        ////////////////////////////////////////////////////////////////

        if(contentType.equalsIgnoreCase("text/plain")) {
            return "txt";
        } 
        
        ////////////////////////////////////////////////////////////////
        //                           html                             //
        ////////////////////////////////////////////////////////////////

        if(contentType.equalsIgnoreCase("text/html")) {
            return "html";
        } 
        
        ////////////////////////////////////////////////////////////////
        //                            jpg                             //
        ////////////////////////////////////////////////////////////////

        if(contentType.equalsIgnoreCase("image/jpeg")) {
            return "jpeg";
        } 
        
        ////////////////////////////////////////////////////////////////
        //                            PDF                             //
        ////////////////////////////////////////////////////////////////

        if(contentType.equalsIgnoreCase("application/acrobat")) {
            return "pdf";
        } 
        
        if(contentType.equalsIgnoreCase("application/x-pdf")) {
            return "pdf";
        } 
        
        if(contentType.equalsIgnoreCase("applications/vnd.pdf")) {
            return "pdf";
        } 
        
        if(contentType.equalsIgnoreCase("text/pdf")) {
            return "pdf";
        } 
        
        if(contentType.equalsIgnoreCase("text/x-pdf")) {
            return "pdf";
        } 

        ////////////////////////////////////////////////////////////////
        //                          BITMAP                            //
        ////////////////////////////////////////////////////////////////

        if(contentType.equalsIgnoreCase("image/x-ms-bmp")) {
            return "bmp";
        } 
        
        if(contentType.equalsIgnoreCase("image/bmp")) {
            return "bmp";
        } 
        
        if(contentType.equalsIgnoreCase("image/x-bmp")) {
            return "bmp";
        } 
        
        return null;
    }
    
    /**
     * Return an InputStream that allows streaming data into information object.
     * @return InputStream to information object
     */
    @Override
    public InputStream getInputStream() throws SharkKBException {
        try {
            return new FileInputStream(this.contentFile);
        } catch (FileNotFoundException ex) {
            throw new SharkKBException(ex.getMessage());
        }
    }
    
    @Override
    public int size() {
        if(this.contentFile != null) {
            return (int) this.contentFile.length();
        }
        return 0;
    }
    
    /**
     * Returns the folder the information is stored in
     * @return 
     */
    public String getPath(){
        return folder;
    }
}
