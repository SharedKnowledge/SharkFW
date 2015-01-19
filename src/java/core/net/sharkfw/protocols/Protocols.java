/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.sharkfw.protocols;

import net.sharkfw.kep.SharkProtocolNotSupportedException;

/**
 * This abstract class contains a number of utility methods for dealing with
 * protocol related issues as well as a number of constants to identify
 * certain protocols throughout the framework
 * 
 * @author thsc
 * @author mfi
 */
public abstract class Protocols {
  /**
   * Constant value representing TCP
   */
  public static final int TCP = 0;

  /**
   * Constant value representing Mail (namely POP3 and IMAP
   */
  public static final int MAIL = 1;

  public static final int WIFI_DIRECT = 2;
        
  /**
   * Constant value number of supported protocols
   */
  public static final int NUMBERPROTOCOLS = 3;
  
  public static final String WIFI_DIRECT_CONNECTION_TOPIC = "http://Idon't know";

//  public static final int UDP = 1;
//  public static final int BT_L2CAP = 2;
//  public static final int BT_RFCOMM = 3;
//  public static final int HTTP = 4;


  /**
   * Constant value representing an arbitrary port (for tcp i.e.)
   */
  public static final int ARBITRARY_PORT = -1;

  /**
   * Constant value representing the prefix of Bluetooth RFCOMM addresses
   */
  public static final String BT_RFCOMM_PREFIX = "btr://";

  /**
   * Constant value representing the prefix of Mail addresses
   */
  public static final String MAIL_PREFIX = "mail://";

  /**
   * Constant value representing the prefix of TCP addresses
   */
  public static final String TCP_PREFIX = "tcp://";

  /**
   * Constant value representing the prefix of UDP addresses
   */
  public static final String UDP_PREFIX = "udp://";
  
  /**
   * Constant value representing the prefix of HTTP addresses
   */
  public static final String HTTP_PREFIX = "http://";
  
  /**
   * Constant value representing the prefix of Bluetooth L2CAP addresses
   */
  public static final String BT_L2CAP_PREFIX = "btl://";
  
    /**
     * Returns a string representation for a constant value
     * @param type An integer value denoting the protocol
     * @return A string representation of that protocol's name
     */
    public static String getProtocolName(int type) {
        switch(type) {
            case TCP: return "TCP";
//            case UDP: return "UDP";
//            case BT_L2CAP: return "Bluetooth L2CAP";
//            case BT_RFCOMM: return "Bluetooth RFCOMM";
//            case HTTP: return "HTTP";
            case MAIL: return "MAIL";
        }
        
        return "unknown protocol type: " + type;
    }

    /**
     * Return the proper type of protocol given an address (in gcf notation).
     * 
     * @param address A string containing a gcf address
     * @return An integer value representing the type of protocol to which the address belongs
     * @throws SharkProtocolNotSupportedException
     */
    public static int getValueByAddress(String address) throws SharkProtocolNotSupportedException {
        if(address.startsWith(TCP_PREFIX)) return Protocols.TCP;
//        if(address.startsWith(UDP_PREFIX)) return Protocols.UDP;
//        if(address.startsWith(BT_L2CAP_PREFIX)) return Protocols.BT_L2CAP;
//        if(address.startsWith(BT_RFCOMM_PREFIX)) return Protocols.BT_RFCOMM;
//        if(address.startsWith(HTTP_PREFIX)) return Protocols.HTTP;
        if(address.startsWith(MAIL_PREFIX)) return Protocols.MAIL;

        throw new SharkProtocolNotSupportedException("unknown protocol type: " + address);
    }

    /**
     * Return whether or not a certain type is considered a stream based protocol or not.
     *
     * @param type An integer value denoting the type in question
     * @return true if it is a streambased protocol, fase otherwise
     * @throws SharkProtocolNotSupportedException
     */
    public static boolean isStreamProtocol(int type) throws SharkProtocolNotSupportedException {
        switch(type) {
            case Protocols.TCP: return true;
//            case Protocols.BT_RFCOMM: return false;
//            case Protocols.UDP: return false;
//            case Protocols.BT_L2CAP: return false;
//            case Protocols.HTTP: return true;
            case Protocols.MAIL: return false;
        }

        throw new SharkProtocolNotSupportedException("unknown protocol type: " + type);
    }
    
    public static String removeProtocolPrefix(String address) throws SharkProtocolNotSupportedException {
		int protocol = Protocols.getValueByAddress(address);
		switch(protocol) {
//			case Protocols.BT_RFCOMM:
//				return address.replaceFirst(Protocols.BT_RFCOMM_PREFIX, "");
//			case Protocols.BT_L2CAP:
//				return address.replaceFirst(Protocols.BT_L2CAP_PREFIX, "");
//			case Protocols.HTTP:
//				return address.replaceFirst(Protocols.HTTP_PREFIX, "");
			case Protocols.MAIL:
				return address.replaceFirst(Protocols.MAIL_PREFIX, "");
			case Protocols.TCP:
				return address.replaceFirst(Protocols.TCP_PREFIX, "");
//			case Protocols.UDP:
//				return address.replaceFirst(Protocols.UDP_PREFIX, "");
		}
		return null;
	}
}
