package net.sharkfw.system;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Streams bytes from an InputStream to an OutputStream, using a buffer.
 * 
 * @author pbs
 */
public class Streamer {

    private Streamer() {
    }

    /**
     * Transfers bytes from in to out, using a buffer. The buffer will not be
     * bigger than maxBufferLength. If there are very few bytes to transfer, a
     * shorter buffer will be allocated. If the InputStream doesn't provide
     * enough input, the call may block, that is, never return.
     * 
     * @param in
     *            Stream from which data is read
     * @param out
     *            Stream to which data is written
     * @param maxBufferLength
     *            the maximum buffer size. If the amount of data is smaller, a
     *            smaller buffer will be used. If the amount of data is bigger,
     *            the buffer will be used multiple times during the transfer
     * @param len
     *            Number of bytes to transfer.
     * @throws IOException
     */
    public static void stream(InputStream in, OutputStream out, int maxBufferLength, long len) throws IOException {
        int bufferLength = clampToMax(maxBufferLength, len);
        byte[] buffer = new byte[bufferLength];

        long lenLeft = len;
        long alreadyRead = 0;

        int numBytesToRead = bufferLength;
        if (lenLeft < numBytesToRead) {
            numBytesToRead = (int) lenLeft;
        }

        while (numBytesToRead > 0) {
            // following lines seem to be an artifact from "good old times" - should be removed
//            L.d("Bytes wrote:" + numBytesToRead + ", buffer: " + buffer.length + "Len: " + len, new Streamer());
//            if (len > 700000000) {
//                L.e("cannot handle incomming request - far too big", in);
//            }
            int numBytesReallyRead = in.read(buffer, 0, numBytesToRead);
//            L.d("Bytes really wrote: " + numBytesReallyRead, Streamer.class);
            out.write(buffer, 0, numBytesReallyRead);
            lenLeft -= numBytesReallyRead;
            alreadyRead += numBytesReallyRead;
            numBytesToRead = bufferLength;
            if (lenLeft >= 0 && lenLeft < numBytesToRead) {
                numBytesToRead = (int) lenLeft;
            }
//            L.d("NumBytesToRead:" + numBytesToRead, Streamer.class);
        }
    }

    /**
     * Transfers bytes from in to out, using a buffer. The buffer have the exact
     * size bufferLength.
     * 
     * Data is transfered until no more byte is available.
     * 
     * TODO When a slow InputStream is used, available() might return 0 even if
     * more bytes would follow a moment later. this might lead to unintended
     * transfer stops. A proper implementation would have to check if the stream
     * is really closed, an not rely on the availability of bytes.
     * 
     * @param in
     *            Stream from which data is read
     * @param out
     *            Stream to which data is written
     * @param bufferLength
     *            the buffer size. If the amount of data is bigger, the buffer
     *            will be used multiple times during the transfer
     * @throws IOException
     */
    public static int stream(InputStream in, OutputStream out, int bufferLength)
            throws IOException {
        byte[] buffer = new byte[bufferLength];

        int totalNumber = 0;
        int numBytesToRead = clampToMax(bufferLength, in.available());

        while (numBytesToRead > 0) {
            int numBytesReallyRead = in.read(buffer, 0, numBytesToRead);
            totalNumber += numBytesReallyRead;
            
            out.write(buffer, 0, numBytesReallyRead);
            numBytesToRead = clampToMax(bufferLength, in.available());
        }
        
        return totalNumber;
    }

    private static int clampToMax(int max, long value) {
        if (value < max) {
            return (int) value;
        } else {
            return max;
        }
    }
}
