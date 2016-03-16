package net.sharkfw.kep;

import java.util.Enumeration;
import net.sharkfw.knowledgeBase.PeerSemanticTag;
import net.sharkfw.protocols.StreamConnection;

/**
 * This class pools <code>KEPSession</code>s. It manages them as a <code>Hashtable</code>,
 * using the <code>StreamConnection</code> that the <code>KEPSession</code> listens on as the key
 * and the <code>KEPSession</code> itself as the value.
 *
 * This class basically offers management for the pool of <code>KEPSession</code>s stored.
 * The <code>KEPRequest</code> creates an entry to that pool after it finished parsing all information from the wire.
 * The <code>KEPSession</code> removes <code>StreamConnection</code>s as it closes them.
 *
 * @see net.sharkfw.protocols.StreamConnection
 * @see net.sharkfw.knowledgeBase.ROPeerSemanticTag
 * @see java.util.Hashtable
 * 
 * @author mfi
 */
public interface KEPConnectionPool {

  /**
   * Clear the connection pool.
   */
  public void clear();

  /**
   * Add an entry that maps a StreamConnection to a certain address.
   * @param connection The <code>StreamConnection</code> to store.
   * @param tag The ip address to use as a key.
   */
  //public void addTag(StreamConnection con, ROPeerSemanticTag tag);
  public void addConnection(String address, StreamConnection connection);

  /**
   * Return a <code>StreamConnection</code> from the pool, that is connected
   * to the <code>ROPeerSemanticTag</code> described by <code>tag</code>
   *
   * @param tag The <code>ROPeerSemanticTag</code> that a connection is searched for.
   * @return A <code>StreamConnection</code> to that peer if successfull. null otherwise.
   */
  public StreamConnection getConnectionByTag(PeerSemanticTag tag);

  /**
   * Return a <code>StreamConnection</code> from the pool, that is connected
   * to a peer having <code>address</code> as reply address.
   *
   * @param address The reply address of the peer to which the connection is established.
   * @return A <code>StreamConnection</code>
   */
  public StreamConnection getConnectionByAddress(String address);

  /**
   * Remove a certain <code>StreamConnection</code> from the pool.
   * This method is called by <code>KEPSession</code> when it closes down
   * its <code>StreamConnection</code> to avoid 'zombie' <code>StreamConnection</code>s.
   *
   * @param con The <code>StreamConnection</code> to remove from the pool.
   */
  public void removeStreamConnection(StreamConnection con);

  /**
   * Returns all keys of connections.
   * The keys are Strings at least, containing addresses
   * in thc gcf notation.
   *
   * @return An <code>Enumeration</code> 
   */
  public Enumeration getConnectedAddresses();
}
