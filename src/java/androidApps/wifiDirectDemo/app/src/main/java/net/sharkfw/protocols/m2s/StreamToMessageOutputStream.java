package net.sharkfw.protocols.m2s;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import net.sharkfw.system.L;
import net.sharkfw.system.SharkException;
import net.sharkfw.system.Streamer;

/**
 * <p>An OutputStream that accepts maxSize bytes, before sending a message prefixed
 * by a header: <code>|len of Id|Id|Packet#|finished|</code>. The header
 * is directly followed by the payload.</p>
 * 
 * <p>The message is also sent if flush() is called.</p>
 * 
 * <p>If the stream is closed the buffered bytes will be sent, the <code>finished</code>
 * part of the header will indicate, that this is the last packet on this stream.</p>
 * 
 * @author thsc
 */
public class StreamToMessageOutputStream extends OutputStream {
    
  private final M2SConnection mssc;
    
    // local buffer
    private OutputStream buf;
    
    // The streamconnection's id
    private final String id;
    
    // max size of packets
    private int maxSize;
    
    // number of package on this stream
    private int packageNumber = M2SMessage.FIRST_PACKAGE_NUMBER;

    private int count;
    
    private final MessageStorage storage;
    private final String recipientAddress;

    /**
     * 
     * @param mssc
     * @param id
     * @param maxSize maximum message size (in byte)
     * @param delay time to wait until next package is to be sent (in milliseconds)
     * @param wait if true - fork a thread that waits until it's woken - TODO: not yet supported
     */
    StreamToMessageOutputStream(M2SConnection mssc, String id, 
            int maxSize, String recipientAddress, MessageStorage storage) {
        this.mssc = mssc;
        this.id = id;
        this.maxSize = maxSize - this.headerLen();
        this.recipientAddress = recipientAddress;
        this.buf = new ByteArrayOutputStream();
        this.count = 0;
        this.storage = storage;
    }

    /**
     * These data comes from the core. Each int value is a piece of a string.
     * Store values until max size is reached or data ar flushed
     * @param b
     * @throws IOException 
     */
    @Override
    public void write(int b) throws IOException {
        this.buf.write(b);
        count++;
        
        if(count == this.maxSize) {
            L.d("must split mail into chunks, count: " + count, this);
//        if(this.buf.toByteArray().length == this.maxSize) {
            this.send(false);
        }
    }

    /**
     * Tricky implementation:
     * 
     * I know, that KEPOutMessage calls flush after a complete
     * KEPMessage has been written to stream.
     * 
     * I also know, that KEPMessage is the only class using that
     * class. Until now...
     * 
     * Thus, whenever this flush is called - the flush is called.
     * The message can than be sent.
     * 
     * KEPOut doesn't call close because it recognizes this
     * implementation as a stream and our communication partner could
     * reply something. 
     * 
     * In that class we know better: A KEP message was written, we
     * should send it. It is tricky because: As soon as KEPOutMessage
     * implementation changes in the way that flush() is called more than once.
     * 
     */
    @Override
    public void flush() throws IOException {
        // thats a tricky decision, see above java doc
        
//        this.send(false);
        this.send(true);
    }
    
    /**
     * Return the length of the header in bytes.
     * @return An int value denoting the length of the header in byte[]
     */
    private int headerLen() {
        int len = 0;
        
        len += 4; // int value of the id-length
        len += this.id.length(); // id itself
        len += 4; // int value of the package Number length (int = 4 byte)
        len += 1; // boolean = one byte
        
        return len;
    }
    
    private boolean sentSomething = false;
    private boolean sendFinishedCalled = false;
    
    /**
     * Send is called when message is to be sent. This can have 
     * to reasons:
     * 
     * <ul>
     * <li> Data to be sent have reached maximal number. Thus the whole
     * message has to be split into parts. This reason is indicated by 
     * finished == false
     * <li> Data are complete and can be sent. This actually means that 
     * the whole KEP message fits to a single message. This reason is
     * indicated by finished == true
     * </ul>
     * 
     * Note: each call can one happens once - investigate the implementation
     * to find out why.
     */
    private void send(boolean finished) throws IOException {
        if(finished) {
            if(sendFinishedCalled) {
                // already called - ignore: happens when close is called after flush
                L.d("finished called more than once - don't do anything: ok", this);
                return;
            } else { 
                // remember: thats the only finished call allowed
                L.d("finished called first time - start sending", this);
                this.sendFinishedCalled = true;
            }
        }
        
        if(this.count == 0) {
            // nothing to send, go ahead
            L.d("send method called but buffer empty - do nothing - normal behaviour when stream is closed", this);
            return;
        }

        L.d("enter send", this);
        
        /* 
         * first data are sent immediately - subsequent are stored with 
         * storage
         */
        
        // no part is sent so far - that's the usual case
        if(!this.sentSomething) {
            // create a message that will be sent
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            
            M2SMessage.writeM2SHeader(baos, this.id, packageNumber++, finished);

            // Header written. Now add payload.
            if(this.buf instanceof ByteArrayOutputStream) {
                ByteArrayOutputStream bufBaos = (ByteArrayOutputStream) this.buf;
                byte[] payload = bufBaos.toByteArray();
                ByteArrayInputStream payloadStream = new ByteArrayInputStream(payload);
                Streamer.stream(payloadStream, baos, 1000);
                // Payload added to temp baos

                // Make sure all changes are written
                baos.flush();
//                count = 0;
            }

            // Send the package 'baos'
            this.mssc.sendMessage(baos.toByteArray());
            L.d("send bytes number: " + baos.size(), this);
            
            // remember - first message has been sent a few milliseconds ago
            this.sentSomething = true;
            
            // we expect further bytes
            if(!finished) {
                L.d("switched to local storage for later sending", this);
                L.d("id / recipient / maxSize " + id + "/" + this.recipientAddress + "/" + maxSize, this);
                // reroute subsequent write ca, id)
                // reroute subsequent write calls to local buffer
                try {
                    this.buf = this.storage.getOutputStream(this.id, 
                            this.recipientAddress, this.maxSize);
                }
                catch(SharkException e) {
                    throw new IOException(e.getMessage());
                }

                // there are no len constraints now
                this.maxSize = Integer.MAX_VALUE;
            }

        } else {
            /* this branch is only reached
            * when the rest of the message was stored locally
            */
            if(finished) {
                // we are done - tell it storage
                if(this.storage != null) {
                    try {
                        this.storage.finishedStoringForLaterSending(this.id);
                    }
                    catch(SharkException e) {
                        throw new IOException(e.getMessage());
                    }
                }
            }
        }
    }
    
    @Override
    public void close() throws IOException {
        this.send(true);
    }
}
