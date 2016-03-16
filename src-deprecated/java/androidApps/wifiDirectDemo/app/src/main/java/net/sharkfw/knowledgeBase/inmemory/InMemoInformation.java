package net.sharkfw.knowledgeBase.inmemory;

import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.sharkfw.system.TimeLong;
import net.sharkfw.kep.KEPMessage;
import net.sharkfw.knowledgeBase.Information;
import net.sharkfw.knowledgeBase.PeerSTSet;
import net.sharkfw.knowledgeBase.PropertyHolderDelegate;
import net.sharkfw.knowledgeBase.SharkKBException;
import net.sharkfw.knowledgeBase.SystemPropertyHolder;
import net.sharkfw.protocols.UTF8SharkOutputStream;
import net.sharkfw.system.L;
import net.sharkfw.system.Streamer;

/**
 * An in memory implementation of the <code>Information</code> interface.
 *
 * This implementation stores its content in a <code>ByteArrayInputStream</code> to allow easy stream based access to it.
 *
 * It also keeps a <code>Hashtable</code> to manage its properties.
 *
 * @author mfi, thsc
 */
@SuppressWarnings("unchecked")
public class InMemoInformation extends PropertyHolderDelegate implements Information {

    public static final String INFO_CONTENT_TYPE = "info_content_type";
    public static final String INFO_DEFAULT_CONTENT_TYPE = "application/unknown";
    public static final String INFO_CREATION_TIME = "info_creation_time";
    public static final String INFO_LAST_MODIFED = "info_last_modified";
    public static final String INFO_NAME = "info_name";
    public static final String INFO_ORIGINATOR = "info_originator";
    public static final String INFO_ID_PROPERTY_NAME = "SharkNet_InfoID";
    
// Save the content. Manages internal byte array automatically.
    private ByteArrayOutputStream content = new ByteArrayOutputStream();

    /**
     * Create a new InMemoInformation from an existing bytearray.
     *
     * @param contentArray The array containing the content
     */
    public InMemoInformation(byte contentArray[]) {
        this();
        try {
            content.write(contentArray);
        } catch (IOException ex) {
            L.e(ex.getMessage(), this);
        }
    }

    /**
     * Create an empty Information object.
     */
    public InMemoInformation() {
        super();
        this.defaultInit();
    }

    /**
     * create it and set status
     */
    protected InMemoInformation(String contentType, long lastModified,
            long creationTime, PeerSTSet recipientSet) {

        try {
            this.setProperty(InMemoInformation.INFO_CONTENT_TYPE, contentType);
            this.setProperty(InMemoInformation.INFO_LAST_MODIFED, Long.toString(lastModified));
            this.setProperty(InMemoInformation.INFO_CREATION_TIME, Long.toString(creationTime));
            this.setProperty(InMemoInformation.INFO_ID_PROPERTY_NAME, java.util.UUID.randomUUID().toString());
        } catch (SharkKBException ex) {
            // cannot happen
        }

    }

    public InMemoInformation(SystemPropertyHolder ph) {
        super(ph);
//        this.defaultInit();
    }

    private void defaultInit() {

        setTimes();
        try {
            this.setProperty(InMemoInformation.INFO_ID_PROPERTY_NAME, java.util.UUID.randomUUID().toString());
        } catch (SharkKBException ex) {
            // cannot happen
        }

    }

    /**
     * @throws if name contains chars, that are not allowed inside file- or path-names
     */
    @Override
    public void setName(String name) throws SharkKBException {
        if (name != null && (
        			name.contains("<") ||		/* less than,  IO redirection (input) */
        			name.contains(">") || 		/* greater than, IO redirection (output) */
        			name.contains("|") || 		/* bar,  IO redirection (pipe) */
        			name.contains("/") || 		/* slash, directory separator */
        			name.contains(":") ||		/* colon, host separator on network filesystems, protocol separator */ 
        			name.contains("*") ||		/* asterisk, wildcard for any amount of chars */ 
        			name.contains("?") ||		/* question mark, wildcard for one single char */ 
        			name.contains("\"") ||		/* double quotes, commandline argument grouping (doesn't work on windows cmd.exe shell anyway, but the char is forbidden) */ 
        			name.contains("\\")			/* backslash, directory separator */
        		)) {
            throw new SharkKBException("The name contains not allowed characters.");
        }

        this.setProperty(InMemoInformation.INFO_NAME, name);
    }

    @Override
    public String getName() {
        try {
            return this.getProperty(InMemoInformation.INFO_NAME);
        } catch (SharkKBException ex) {
            // cannot happen
        }
        
        return null;
    }

    /**
     * Writes the content of the internal ByteArrayInputStream to the given <code>OutputStream</code>.
     *
     * Behaves like:      <code>
   * os.write(content.getByteArray());
     * </code>
     *
     * @param os The <code>OutputStream</code> to write to.
     */
    @Override
    public void streamContent(OutputStream os) {
        try {
            // vllt mit Streamer.stream arbeiten?
            //os.write(content.toByteArray());
            ByteArrayInputStream bais = new ByteArrayInputStream(content.toByteArray());
            Streamer.stream(bais, os, UTF8SharkOutputStream.STREAM_BUFFER_SIZE, content.size());
        } catch (IOException ex) {
            L.e(ex.getMessage(), this);
        }
    }

    /**
     * Fill the internal content memory from the <code>InputStream</code>
     *
     * @param is The <code>InputStream</code> to read from
     * @param len An integer value to denote the length of the content.
     */
    @Override
    public void setContent(InputStream is, long len) {
        this.setTimes();

        // Problems when casting long to int? Maybe use Streamer?
        byte[] contentIs = new byte[(int) len];
        try {

            is.read(contentIs);
            // Remove old content. This is not a must! One could also append.
            this.content = new ByteArrayOutputStream();
            this.content.write(contentIs);
        } catch (IOException ex) {
            throw new RuntimeException(ex.getMessage());
        }
    }

    /**
     * Returns a reference to the internal <code>ByteArrayOutputStream</code>
     *
     * @return A <code>ByteArrayOutputStream</code> holding the content of this information.
     */
    @Override
    public OutputStream getOutputStream() throws SharkKBException {
        return content;
    }

    /**
     * Returning the content of this information as a byte array. Behaves like calling:      <code>
   * return content.toByteArray();
     * </code>
     *
     * @return
     */
    @Override
    public byte[] getContentAsByte() {
		byte[] ba = content.toByteArray();
		return ba;
    }

    /**
     * Return an integer value representing the size of the internal content.
     *
     * @return An integer value to denote the size of the content.
     */
    @Override
    public long getContentLength() {
		if (this.content != null) {
			int sz = this.content.size();
			return sz;
		}
		return 0;
    }

    /**
     * Returns always "application/unknown" at the moment. Can be extended to check for MIME-type information in the local properties i.e.
     *
     * @return A string representing the type of the content.
     */
    @Override
    public String getContentType() {
        try {
            String contentTypeString = this.getProperty(InMemoInformation.INFO_CONTENT_TYPE);
            if (contentTypeString != null) {
                return contentTypeString;
            }
        } catch (SharkKBException ex) {
            // cannot happen
        }
            
        return InMemoInformation.INFO_DEFAULT_CONTENT_TYPE;
    }

    /**
     * Overridden hashcode method. This method implements a simple algorithm for determining the hashcode of an Information object taking the content of the object into account as well.
     *
     * It takes the size of the content into account when computing the value to prevent a too-high CPU load:
     *
     * <ul>
     * <li> Below 100 bytes, every byte of the content becomes part of the hashcode </li>
     * <li> Between 100 bytes and 1MB, the first 100 byte are becoming part of the hascode, plus, every 100th bytes becomes part of the hashcode. </li>
     * <li> Bigger than 1MB, the first 500 bytes will become part of the hashcode, plus, every 1000th bytes becomes part of the hascode </li>
     * </ul>
     *
     * @return A hashcode for this Information object, taking the content of the Information into account.
     */
    @Override
    public int hashCode() {
        int result = 0; //hashCode;
        if (result == 0) {
			byte[] contentArray = content.toByteArray();
            long size = contentArray.length;

            result = 17;
            if (size > 100 && size < 1024 * 1024) {
        // Medium sized, between 100 byte and 1MB

                int cur = 100;
                // compute hash of first 100 byte
                for (int i = 0; i < cur; i++) {
                    result = 31 * result + (int) contentArray[i];
                }

                while (cur < size) {
                    result = 31 * result + (int) contentArray[cur];
                    cur = cur + 100;
                }

            } else if (size > 1024 * 1024) {
                // Bigger than 1MB
                int cur = 500;
                // compute hash of first 500 byte
                for (int i = 0; i < cur; i++) {
                    result = 31 * result + (int) contentArray[i];
                }

                while (cur < size) {
                    result = 31 * result + (int) contentArray[cur];
                    cur = cur + 1000;
                }

            } else {
        // Small sized
                // Arrays.hashcode is not available in JavaME
                for (int i = 0; i < contentArray.length; i++) {
                    result = 31 * result + (int) contentArray[i];
                }
            }
        }
        return result;
    }

  // API rev. methods
    /**
     * Set the given byte[] to be the content for this Information object. Internally, the content will be written into a newly created ByteArrayOutputStream. Calling this method will erase previously
     * set content on this object.
     *
     * @param content The content to be set.
     */
    @Override
    public void setContent(byte[] content) {
        this.setTimes();
        this.content = new ByteArrayOutputStream();

        try {
            this.content.write(content);
        } catch (IOException ex) {
            L.e(ex.getMessage(), this);
        }
    }

    
    protected void setTimes() {
        try {
            String nowString = Long.toString(System.currentTimeMillis());
            
            this.setProperty(InMemoInformation.INFO_LAST_MODIFED, nowString);
            this.setProperty(InMemoInformation.INFO_CREATION_TIME, nowString);
        } catch (SharkKBException ex) {
            // cannot happen
        }
    }

    /**
     * This method will set the content-string to be the content of this Information object. Internally, the content will be written into a newly created ByteArrayOutputStream. Calling this method
     * will erase previously set content on this object. The content-string will be read as UTF8.
     *
     * @param content The content to be set.
     */
    @Override
    public void setContent(String content) {
        try {
			setContent(content.getBytes(KEPMessage.ENCODING));
		} catch (UnsupportedEncodingException e) {
            //FIXME: Catch unknown encoding exception?!
			e.printStackTrace();
        }
    }

    /**
     * Create a new ByteArrayOutputStream and set it as the content of this object.
     */
    @Override
    public void removeContent() {
        this.setTimes();
        this.content = new ByteArrayOutputStream();
    }

    /**
     * Set the type of this information's content as a property (<code>Information.CONTENTTYPE</code>) to this Information object.
     *
     * @param mimetype The type of this information's content.
     */
    @Override
    public void setContentType(String mimeType) {
        try {
            this.setProperty(InMemoInformation.INFO_CONTENT_TYPE, mimeType);
        } catch (SharkKBException ex) {
            // cannot happen
        }
    }

    @Override
    public long lastModified() {
        String value = null;
        try {
            value = this.getProperty(InMemoInformation.INFO_LAST_MODIFED);
        } catch (SharkKBException ex) {
            // cannot happen
        }
        if (value == null) {
            return 0;
        }
        if (value.equals("")) {
            return 0;
        }
        return TimeLong.parse(value);
    }

    protected void setLastModified(long time) {
        try {
            this.setProperty(InMemoInformation.INFO_LAST_MODIFED, Long.toString(time));
        } catch (SharkKBException ex) {
            // cannot happen
        }
    }

    @Override
    public long creationTime() {
        String value = null;
        try {
            value = this.getProperty(InMemoInformation.INFO_CREATION_TIME);
        } catch (SharkKBException ex) {
            // cannot happen
        }
        if (value == null) {
            return 0;
        }

        return TimeLong.parse(value);
    }

    // allow classes on this package to modify that time
    protected void setCreationTime(long time) {
        try {
            this.setProperty(InMemoInformation.INFO_LAST_MODIFED, Long.toString(time));
        } catch (SharkKBException ex) {
            // cannot happen
        }
    }

    /**
     * @deprecated @return
     */
    public OutputStream getWriteAccess() {
        try {
            return this.getOutputStream();
        } catch (SharkKBException ex) {
            // ignore and return null
        }

        return null;
    }

    /**
     * Return an InputStream that allows streaming data into information object.
     *
     * @return InputStream to information object
     * @throws SharkKBException 
     */
    public InputStream getInputStream() throws SharkKBException {
		byte[] ba = content.toByteArray();
		ByteArrayInputStream is = new ByteArrayInputStream(ba);
		return is;
        }

	/* size() was a duplicate to getContentLength() */

    /**
     * Returns the unique ID of this information
     *
     * @return Unique ID as String, empty String ("") if there is no ID
     */
    public String getUniqueID() {
        String uniqueID = null;
        try {
            uniqueID = this.getProperty(InMemoInformation.INFO_ID_PROPERTY_NAME);
        } catch (SharkKBException ex) {
            // ??
        }
        if (uniqueID != null) {
            return uniqueID;
        } else {
            return "";
        }
    }

    @Override
    public String getContentAsString() {
        byte[] cBytes = getContentAsByte();
        String cString = new String(cBytes);
        return cString;
    }

	public void obtainLock(InputStream i) {
	}

	public void obtainLock(OutputStream i) {
	}
	
	public void releaseLock() {
	}

}
