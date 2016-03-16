/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.sharkfw.knowledgeBase;

/**
 * This class represents a peer inside an <code>STSet</code>.
 *
 * <p>Peers are considered regular semantic tags which also have a number of addresses, that
 * can be used to communicate with the peer. The addresses must comply to the shark address
 * schema, see {@link net.sharkfw.protocols.Protocols}.</p>
 *
 * <p>Apart from the addresses the same rules that apply to {@link SemanticTag}s also
 * apply to PeerSemanticTags, including die equality rule. The address has no part
 * in defining the identity of a peer tag. The identity is solely defined through
 * the SIs.</p>
 *
 * <p>In the future, peers also should have a certificate to securely validate their identity.</p>
 *
 * @author mfi
 */
public interface PeerSemanticTag extends SemanticTag {
    /**
     * Return an array of string containing the addresses of the given peer.
     * @return An array of string with the addresses of this peer.
     */
    public String[] getAddresses();

  /**
   * Sets and replaces the peer's addresses.
   * @param addresses An array of strings containing all known addresses of this peer.
   */
    public void setAddresses(String[] addresses);
    
    public void removeAddress(String address);

    /**
     * add another address
     * @param newAddress the adress to be added
     */
    public void addAddress(String address);
}
