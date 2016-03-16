package net.sharkfw.protocols.m2s;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import net.sharkfw.system.SharkException;

/**
 *
 * @author thsc
 */
public interface MessageStorage {

    ///////////////////////////////////////////////////////////
    //                    sender storage                     //
    ///////////////////////////////////////////////////////////
    
    /**
     * Create a temporary storage for a long message that is to be send
     * 
     * @param id message id
     * @param firstOffset first by offset in original message
     * @return 
     */
    public OutputStream getOutputStream(String id, String recipientAddress, int maxLen) throws SharkException;

    public void finishedStoringForLaterSending(String id) throws SharkException;

    /**
     * Save all bytes beginning at offset with some meta information.
     * @param id
     * @param packageNumber
     * @param last
     * @param msg
     * @param offset 
     */
    public void savePart(String id, int packageNumber, boolean last, InputStream is) throws SharkException;

    // sending out message after message ////////////////////////////////////

    /**
     * What is next message to read
     * @param id
     * @return 
     */
    public int nextPackageNumberToSend(String id) throws SharkException;

    /**
     * @param id
     * @param nextPackageNumber
     * @return 
     */
    public int remainingNumberOfBytes(String id, int nextPackageNumber) throws SharkException;

    public int getMaxPackageSize(String id) throws SharkException;

    public void streamNextPackageToSend(ByteArrayOutputStream baos, String id, int size) throws SharkException;
    
    ///////////////////////////////////////////////////////////
    //                    receiver storage                   //
    ///////////////////////////////////////////////////////////
    
    /**
     * Are all parts of this message stored
     * @param id
     * @return 
     */
    public boolean completelyReceived(String id) throws SharkException;

    public String getRecipientAddress(String id) throws SharkException;

    public int getNextPackageSizeToRead(String id) throws SharkException;

    public InputStream getNextPartInputStream(String id) throws SharkException;

    public void removeToRead(String id);
}
