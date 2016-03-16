package net.sharkfw.kep;

import java.io.IOException;
import net.sharkfw.knowledgeBase.*;
import net.sharkfw.protocols.SharkInputStream;
import net.sharkfw.protocols.SharkOutputStream;
import net.sharkfw.system.SharkNotSupportedException;

/**
 * This class offers serialization for a number of functions in the framework.
 * Especially KEP uses this serializer a lot, when it sends its messages.
 *
 * The methods can of course be used outside of KEP as well
 *
 * @author thsc
 * @author mfi
 */
public interface KnowledgeSerializer {
    
    /**
     * Serialize an interest unto the given OutputStream.
     *
     * @param remoteInterest The interest to be serialized
     * @param os The OutputStream on which to write.
     * @throws java.io.IOException Thrown if problems with the stream occur.
     */
    void write(SharkCS interest, SharkOutputStream os) throws IOException, SharkKBException;

    /**
     * Write a Knowledge object unto the given OutputStream. The knowledge object
     * will be serialized and written on the stream.
     *
     * @param k The Knowledge object to be written
     * @param os The OutputStream on which to write
     * @throws IOException Is thrown if problems with the stream occur.
     */
    void write(Knowledge k, SharkOutputStream os) throws IOException, SharkKBException;

    /**
     * Parse Knowledge from an InputStream and return a Knowledge object.
     *
     * @param is The InputStream to read from
     * @return A Knowledge object containing the deserialized knowledge
     * @throws IOException Is thrown if problems with the stream occur.
     */
    Knowledge parseKnowledge(SharkInputStream is) throws IOException, SharkKBException;

    /**
     * Deserialize an interest from a given InputStream.
     *
     * @param is The stream from which to read
     * @return a new Interest as read from the InputStream
     * @throws IOException Is thrown if problems with the stream occur.
     * @throws SharkKBException if format problems are ancountered
     * @throws IOException if problems whiling reading from stream
     */
    public SharkCS parseSharkCS(SharkKB target, SharkInputStream is) throws IOException, SharkKBException;
    public SharkCS parseSharkCS(SharkInputStream is) throws IOException, SharkKBException;

    /**
     * This method serializes a given STSet.
     * It also handles the derived interfaces:
     *
     * <ul>
     * <li> SemanticNet </li>
     * <li> Taxonomy </li>
     * <li> PeerSTSet </li>
     * <li> PeerSemanticNet </li>
     * <li> PeerTaxonomy </li>
     * <li> TimeSTSet </li>
     * <li> GeoSTSet </li>
     * </ul>
     *
     * @param stset
     * @return
     * @throws SharkNotSupportedException
     */
    public String serializeSTSet(STSet stset) throws SharkKBException;

    /**
     * Deserialize a STSet from a string
     *
     * @param serializedSTSet The string containing a serialized representation of a stset.
     * @return A deserialized STSet
     * @throws SharkKBException
     */
    public boolean deserializeSTSet(STSet target, String serializedSTSet) throws SharkKBException;

    /**
     * Serialize a Shark context space into a string. 
     * 
     * Tags and relations (if any) are stored in the string.
    *
    * @param sharkCS SharkCS to be serialized
    * @return A string containing the serialized context space
    * @throws SharkNotSupportedException
    */
    public String serializeSharkCS(SharkCS sharkCS) throws SharkKBException;

    /**
    * Deserialize an AnchorSet from a string representation
    *
    * @param serializedAnchorSet The string containing the serialized representation of an AnchorSet
    * @return an inmemory sharkcs
    * @throws SharkKBException
    */
    public SharkCS deserializeSharkCS(String serializedSharkCS) throws SharkKBException;

    
}
