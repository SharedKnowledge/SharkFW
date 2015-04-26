/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.sharkfw.protocols;

import net.sharkfw.kep.SharkProtocolNotSupportedException;

/**
 * Respresents the Address of a peer.
 * Addresses in shark use the GCF-notation like protocol://address[:port].
 *
 * Different protocols are defined through different constants as declared in
 * <code>Protocols</code>. This class offers methods to return the type of address
 * along with the address itself.
 * 
 * @author mfi
 */
public class PeerAddress {
    public static final String MAXLEN_PARAMETER = "maxLength"; // in kByte
    
    public static final int DEFAULT_MAXLEN = 1024; // default length is 1 MByte
    
    private String gcfAddressString = null;


  /**
   * 
   * @param addressString
   * @param parameter
   * @return Parameter or null if not set
   */
    public static String getPeerAddressParameter(String addressString, 
              String parameter) {
          
        parameter += "=";
        String parameterString = null;
        int index = addressString.indexOf("?");
        
        // are their parameter at all
        if(index != -1) {
            
            // is this parameter set
            index = addressString.indexOf(parameter);
        
            if(index != -1) {
                // we have got it - go behind parameter to get the value
                index += parameter.length();
                
                // are their other parameter behind it
                int endIndex = addressString.indexOf("&", index);
                
                if(endIndex != -1) {
                    parameterString = addressString.substring(index, endIndex);
                } else {
                    parameterString = addressString.substring(index);
                }
            }
            
            if(parameterString == "") {
                parameterString = null;
            }
        }

        // return found parameter string or null
        return parameterString;
    }

    /**
     * @param addressString
     * @return delay value that is set in address or an default defined in this class
     */
    public static int getMaxSize(String addressString) {
        int maxLen = PeerAddress.DEFAULT_MAXLEN;
        
        String maxLenString = PeerAddress.getPeerAddressParameter(addressString, 
                PeerAddress.MAXLEN_PARAMETER);
        
        if(maxLenString != null) {
            try {
                maxLen = Integer.parseInt(maxLenString);
            }
            catch(Exception e) {
                maxLen = PeerAddress.DEFAULT_MAXLEN;
            }
        }
        
        return maxLen;
    }
  
  /**
   * Create a new instance of this class for the following address.
   * @param gcfString A String containing a peer address in gcf-notation.
   */
  public PeerAddress(String gcfString) {
    this.gcfAddressString =  gcfString;
  }

  /**
   * Return the constant value for the protocol which is used by this address.
   * @return A constant value from <code>Protocol</code> or -1 if the protocol is unknown.
   */
  public int getProtocolType() {
    try {
      return Protocols.getValueByAddress(gcfAddressString);
    } catch (SharkProtocolNotSupportedException ex) {
      return -1; // Protocol is not supported
    }
  }

  /**
   * Return a String representing the address of a peer.
   * @return A String representing the address of a peer.
   */
  public String getAddressString() {
    return this.gcfAddressString;
  }  
}
