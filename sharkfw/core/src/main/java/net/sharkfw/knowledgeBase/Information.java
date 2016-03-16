package net.sharkfw.knowledgeBase;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import net.sharkfw.asip.ASIPInformation;

/**
 * <p>Information contains content. Content type should
 * be known, but are optional. The content type is stored as a property.</p>
 *
 * <p>The content-type is best provided as MIME-type.</p>
 *
 * <p>Information can be anything that can be streamed using java's
 * Input and OutputStreams.</p>
 *
 * <p>Information offer some convienience methods, like: <br />
 * <code>info.setContent("This is string content!");</code> All string set here, are treated as UTF-8 strings! <br />
 * If content shall be streamed the methods: <br />
 * <code>
 * [...]
 * FileInputStream fis = new FileInputStream(somefile);</code> <br />
 * <code>
 * info.setContent(fis, somefile.length());
 * [...]
 * </code> <br /> or <br />
 * <code>
 * FileOutputStream fos = new FileOutputStream(someOtherFile); </code> <br />
 * <code>
 * info.streamContent(fos);
 * </code>
 * </p>
 *
 * @see java.io.InputStream
 * @see java.io.OutputStream
 * 
 * @author thsc
 * @author mfi
 */
public interface Information extends SystemPropertyHolder, ASIPInformation {
    
    public final static String LASTMODIFIED = "LASTMODIFIED";
    public final static String CREATIONTIME = "CREATIONTIME";
    public final static String CONTENTASBYTE = "CONTENTASBYTE";
    public final static String CONTENTASSTRING = "CONTENTASSTRING";
    public final static String CONTENTLENGTH = "CONTENTLENGTH";
    public final static String CONTENTTYPE = "CONTENTTYPE";
    public final static String INFONAME = "INFONAME";
    public final static String UNIQUEID = "UNIQUEID";
    
    /**
     * Return an OutputStream containing the content of this Information.
     * @return An OutputStream containing the content of this Information.
     *     public OutputStream getWriteAccess();
     * @throws SharkKBException 
     * @deprecated use streamContent instead
     */
    public OutputStream getOutputStream() throws SharkKBException;
    
    /**
     * Return an InputStream that allows streaming data into information object.
     * @return InputStream to information object
     * @throws net.sharkfw.knowledgeBase.SharkKBException
     * @deprecated use setContent(InputStream instead)
     */
    public InputStream getInputStream() throws SharkKBException;
    /**
     * Returns the unique ID of that information object
     * @return unique ID as String, "" if there is no unique ID set
     */
    public String getUniqueID();
       
}
