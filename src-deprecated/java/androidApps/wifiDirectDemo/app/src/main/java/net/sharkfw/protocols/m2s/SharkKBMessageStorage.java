package net.sharkfw.protocols.m2s;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.HashMap;

import net.sharkfw.system.TimeLong;
import net.sharkfw.knowledgeBase.ContextCoordinates;
import net.sharkfw.knowledgeBase.ContextPoint;
import net.sharkfw.knowledgeBase.Information;
import net.sharkfw.knowledgeBase.SemanticTag;
import net.sharkfw.knowledgeBase.SharkCS;
import net.sharkfw.knowledgeBase.SharkKB;
import net.sharkfw.knowledgeBase.SharkKBException;
import net.sharkfw.system.L;
import net.sharkfw.system.SharkException;
import net.sharkfw.system.Streamer;

/**
 * Uses a SharkKB to implement the temporary message storage.
 * 
 * @author thsc
 */
public class SharkKBMessageStorage implements MessageStorage {
    private final SharkKB kb;
    private static final String RECIPIENT_ADDRESS = "kbStorage_recipientAddress";
    private static final String CURRENT_OFFSET = "kbStorage_currentOffset";
    private static final String MAXLEN = "kbStorage_maxlen";
    
    private HashMap<String, OutputStream> openOS = new HashMap();
    private static final String LAST_INFORMATION_INDEX = "kbStorage_lastInfoIndex";
    private static final String IS_LAST = "kbStorage_isLast";
    private static final String OFFSET = "kbStorage_offset";
    private static final String PACKAGENUMBER = "kbStorage_packageNumber";
    private static final String LAST_SEND_PACKAGE_NUMBER = "kbStorage_LastSendPackageNumber";
    private static final String MESSAGE_COMPLETE = "kbStorage_messageComplete";
    private static final String CURRENT_PACKAGE_TO_READ = "kbStorage_currentPackageToRead";
    private static final String SIZE = "kbStorage_size";
    
    public SharkKBMessageStorage(SharkKB kb) {
        this.kb = kb;
    }
    
    public SharkKB getKB() {
        return this.kb;
    }
    
    public ContextPoint createCP(String id, boolean toBeSent) throws SharkException {
        SemanticTag idTag = kb.createSemanticTag(id, id);

        int direction = toBeSent ? SharkCS.DIRECTION_OUT : SharkCS.DIRECTION_IN;
        ContextCoordinates cc = 
                this.kb.createContextCoordinates(idTag, null, null, null, null, null, direction);

        return this.kb.createContextPoint(cc);
    }
    
    private ContextPoint getCP(String id, boolean toBeSent) throws SharkException {
        try {
            SemanticTag idTag = kb.createSemanticTag(id, id);
            
            int direction = toBeSent ? SharkCS.DIRECTION_OUT : SharkCS.DIRECTION_IN;
            ContextCoordinates cc = 
                    this.kb.createContextCoordinates(idTag, null, null, null, null, null, direction);
            
            ContextPoint cp = this.kb.getContextPoint(cc);
            if(cp == null) {
                throw new SharkException("cannot find context point with id: " + id);
            }
            
            return cp;
            
        } catch (SharkKBException ex) {
            // impossible
        }
        
        throw new SharkException("cannot find context point with id: " + id);
    }
    
    /**
     * Create a storage for byte which cannot be sent yet.
     * 
     * The storage will be a cp with id as topic
     * 
     * @param id
     * @param recipientAddress
     * @param firstOffset
     * @param maxLen
     * @return 
     */
    public OutputStream getOutputStream(String id, String recipientAddress, int maxLen) throws SharkException {
        ContextPoint cp = this.createCP(id, true);
        
        cp.setProperty(RECIPIENT_ADDRESS, recipientAddress);
        cp.setProperty(CURRENT_OFFSET, Integer.toString(0));
        cp.setProperty(MAXLEN, Integer.toString(maxLen));
        
        try {
            Information i = cp.addInformation();
            i.setName(recipientAddress); // help debugging
            OutputStream os = i.getOutputStream();
            
            this.openOS.put(id, os);
            
            return os;
			// TODO: operations on this stream are not protected by a critical section 
        } catch (SharkKBException ex) {
            // won't happen
        }
        
        return null;
    }

    
    public void finishedStoringForLaterSending(String id) {
        OutputStream os = this.openOS.get(id);
        try {
            if(os != null) {
                os.close();
            }
        } catch (IOException ex) {
            L.d("couldn't close information output stream", this);
        }
        
        this.openOS.remove(id);
    }

    /**
     * Received a part and store until message is complete.
     * @param id
     * @param packageNumber
     * @param last
     * @param msg
     * @param offset 
     */
    public void savePart(String id, int packageNumber, boolean last, InputStream is) throws SharkException {
        ContextPoint cp = this.createCP(id, false);
        // got a new part - create information for it
        String infoIndexString = cp.getProperty(LAST_INFORMATION_INDEX);
        
        int infoIndex = 0; // default 0
        if(infoIndexString != null) {
            infoIndex = Integer.parseInt(infoIndexString);
            infoIndex++;
        }
        
        Information i = cp.addInformation();
        i.setName(id); // helps debugging
        
        try {
            OutputStream infoOS = i.getOutputStream();

            int size = Streamer.stream(is, infoOS, 100);

            i.setProperty(IS_LAST, Boolean.toString(last));
            i.setProperty(PACKAGENUMBER, Integer.toString(packageNumber));
            i.setProperty(SIZE, Integer.toString(size));

            cp.setProperty(LAST_INFORMATION_INDEX, Integer.toString(infoIndex));

            if(last) {
                cp.setProperty(MESSAGE_COMPLETE, String.valueOf(true));
            }
        }
        catch(Exception e) {
            // TODO
        }
    }

    /**
     * Returns number of the next package that should be send to complete
     * KEP message
     * @param id
     * @return 
     */
    public int nextPackageNumberToSend(String id) throws SharkException {
        ContextPoint cp = this.getCP(id, true);
        
        String infoIndexString = cp.getProperty(LAST_INFORMATION_INDEX);
        
        int infoIndex = 1; // default 1 - because 0 was the first part
        if(infoIndexString != null) {
            infoIndex = Integer.parseInt(infoIndexString);
            infoIndex++;
        }

        return infoIndex;
    }

    /**
     * Return number of byte in the information.
     * @param id
     * @param packageNumber
     * @return 
     */
    public int remainingNumberOfBytes(String id, int packageNumber) throws SharkException {
        ContextPoint cp = this.getCP(id, true);
        
        Enumeration<Information> infoEnum = cp.enumInformation();
        if(infoEnum != null) {
            Information i = infoEnum.nextElement();

            int currentOffset = 0;
            String value = i.getProperty(CURRENT_OFFSET);
            if(value != null) {
                currentOffset = Integer.parseInt(value);
            }
            
            // TODO: clean up types, remove cast to int
            int remainingNumber = ((int)i.getContentLength()) - currentOffset;
            
            if(remainingNumber < 0) {
                throw new SharkException("internal error: current offset in message storage is smaller than actual size - impossible");
            }
            
            return remainingNumber;
        }
        
        throw new SharkException("cannot find remaining message part - fatal");
    }
    
    /**
     * Returns information that is a part of a message that was received
     * from remote peer
     * @param id
     * @param packageNumber
     * @return 
     */
    private Information getInformation(String id, String packageNumber) throws SharkException {
        ContextPoint cp = this.getCP(id, false);
        
        Enumeration<Information> infoEnum = cp.enumInformation();
        if(infoEnum != null) {
            while(infoEnum.hasMoreElements()) {
                Information i = infoEnum.nextElement();
                
                String packageNumberString = i.getProperty(PACKAGENUMBER);
                if(packageNumberString.equalsIgnoreCase(packageNumber)) {
                    return i;
                }
            }
        }
        
        // happens when end of info reached
        return null;
    }

    /**
     * Messages have a maximal length. Each stored message for further 
     * transmission remembers those limits. (Parameter are that by creating 
     * the stored message with getOutputStream)
     * 
     * @param id
     * @return 
     */
    public int getMaxPackageSize(String id) throws SharkException {
        ContextPoint cp = this.getCP(id, true);
        
        String value = cp.getProperty(MAXLEN);
        if(value != null) {
            return Integer.parseInt(value);
        }
        
        // shouldn't happen
        return -1;
    }

    
    public void streamNextPackageToSend(ByteArrayOutputStream baos, String id, int size) throws SharkException {
        ContextPoint cp = this.getCP(id, true);
        
        String offsetString = cp.getProperty(CURRENT_OFFSET);
        
        long offset = 0;
        if(offsetString != null) {
            offset = TimeLong.parse(offsetString);
        }
        
        String packageNumberString = cp.getProperty(LAST_SEND_PACKAGE_NUMBER);
        int pNumber = 0;
        if(packageNumberString != null) {
            pNumber = Integer.parseInt(packageNumberString);
        }
        
        Information i = cp.enumInformation().nextElement();
        
        try {
            InputStream inputStream = i.getInputStream();
            long skipped = inputStream.skip(offset);
            
            if(skipped != offset) {
                throw new SharkException("couldn't skip as much as I wanted to: fatal");
            }
            
            Streamer.stream(inputStream, baos, 100, size);
            
            offset += size;

            L.d("streamed message part: new offset / size:" + offset + " / " + i.getContentLength(), this);
            if(offset >= i.getContentLength()) { 
				// TODO: is it really >= ??
                L.d("offset exceeds size - remove local storage", this);
                // we are done - remove saved message part
                this.kb.removeContextPoint(cp.getContextCoordinates());
            } else {
                L.d("set new offset and package number", this);
                cp.setProperty(CURRENT_OFFSET, String.valueOf(offset));
                cp.setProperty(LAST_SEND_PACKAGE_NUMBER, String.valueOf(++pNumber));
            }
        } catch (Exception ex) {
            L.d(ex.getMessage(), this);
        }
    }

    /**
     * Have all parts arrived yet? If so, the message can be reassamble 
     * and processed.
     * 
     * @param id
     * @return 
     */
    public boolean completelyReceived(String id) throws SharkException {
        ContextPoint cp = this.getCP(id, false);
        
        if(cp == null) {
            return false;
        }
        
        String value = cp.getProperty(MESSAGE_COMPLETE);
        if(value != null) {
            return Boolean.valueOf(value);
        }
        
        return false;
    }

    /**
     * Get address of recipient of a message that wasn't sent completely yet.
     * @param id
     * @return 
     */
    public String getRecipientAddress(String id) throws SharkException {
        ContextPoint cp = this.getCP(id, true);
        
        String value = cp.getProperty(RECIPIENT_ADDRESS);
        if(value == null) {
            throw new SharkException("recipient not sent in stored message part - fatal");
        }
        
        return value;
    }

    /**
     * Returns length of the next message part that can be read.
     * @param id
     * @return 
     */
    public int getNextPackageSizeToRead(String id) throws SharkException {
        ContextPoint cp = this.getCP(id, false);
        
        String value = cp.getProperty(CURRENT_PACKAGE_TO_READ);
        if(value == null) {
            value = "0";
        }
        
        Information info = this.getInformation(id, value);
        
        value = info.getProperty(SIZE);
        
        return Integer.parseInt(value);
    }

    /**
     * 
     * @param id
     * @return 
     */
    public InputStream getNextPartInputStream(String id) throws SharkException {
        ContextPoint cp = this.getCP(id, false);
        
        String value = cp.getProperty(CURRENT_PACKAGE_TO_READ);
        
        int currentIndex = 0;
        if(value != null) {
            currentIndex = Integer.parseInt(value);
        }
        
        Information info = null;
        if(currentIndex > 0) {
            // remove already consumed information
            info = this.getInformation(id, Integer.toString(currentIndex-1));
            cp.removeInformation(info);
        }
        
        // get next
        info = this.getInformation(id, Integer.toString(currentIndex));
        if(info == null) {
            return null;
        }
        
        currentIndex++;
        cp.setProperty(CURRENT_PACKAGE_TO_READ, Integer.toString(currentIndex));
        
        try {
            return info.getInputStream();
        } catch (SharkKBException ex) {
            // shouldn't happen
            L.d(ex.getMessage(), this);
        }
        
        return null;
    }

    public void removeToRead(String id) {
        ContextPoint cp;
        try {
            cp = this.getCP(id, false);
            this.kb.removeContextPoint(cp.getContextCoordinates());
        } catch (SharkException ex) {
            // ignore
        }
    }
}
