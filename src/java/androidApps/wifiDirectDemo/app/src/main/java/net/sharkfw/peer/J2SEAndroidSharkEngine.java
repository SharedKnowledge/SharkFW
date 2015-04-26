package net.sharkfw.peer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Map;
import net.sharkfw.kep.SharkProtocolNotSupportedException;
import net.sharkfw.kep.SimpleKEPStub;
import net.sharkfw.kep.format.XMLSerializer;
import net.sharkfw.knowledgeBase.ContextCoordinates;
import net.sharkfw.knowledgeBase.ContextPoint;
import net.sharkfw.knowledgeBase.Information;
import net.sharkfw.knowledgeBase.Knowledge;
import net.sharkfw.knowledgeBase.PeerSemanticTag;
import net.sharkfw.knowledgeBase.SemanticTag;
import net.sharkfw.knowledgeBase.SharkCS;
import net.sharkfw.knowledgeBase.SharkCSAlgebra;
import net.sharkfw.knowledgeBase.SharkKB;
import net.sharkfw.knowledgeBase.SharkKBException;
import net.sharkfw.knowledgeBase.SystemPropertyHolder;
import net.sharkfw.knowledgeBase.inmemory.InMemoSharkKB;
import net.sharkfw.protocols.*;
import net.sharkfw.protocols.m2s.M2SStub;
import net.sharkfw.protocols.m2s.MessageStorage;
import net.sharkfw.protocols.m2s.SharkKBMessageStorage;
import net.sharkfw.protocols.mail.MailMessageStub;
import net.sharkfw.protocols.tcp.TCPStreamStub;
import net.sharkfw.system.L;
import net.sharkfw.system.Util;

/**
 * An implementation of SharkEngine for J2SE enable devices.
 *
 * Offers protocol support for:
 * <ul>
 * <li> TCP </li>
 * <li> Mail </li>
 * <li> HTTP </li>
 * </ul>
 * 
 * @author thsc
 * @author mfi
 */
public class J2SEAndroidSharkEngine extends SharkEngine {
    
    // Mail parameter
    private String smtpHost;
    private String smtpUserName;
    private String smtppwd;
    private String pop3Host;
    private String pop3MailAddress;
    private String pop3pwd;
    private int mailCheckInterval = 1;
    private String pop3user;
    
    // TCP parameter
    public static int defaultTCPPort = 7070;
    private int defaultUDPPort = 5555;
    private int defaultHTTPPort = 8080;
    private int kpStoreCount = 0;
    
	TCPStreamStub tcp;
    private static final boolean DEFAULT_SSL = false;
    private boolean sslSMTP = DEFAULT_SSL, sslPOP3 = DEFAULT_SSL;
    
    private static final int DEFAULT_MAX_MAIL_SIZE = 1024;  // default 1 MByte
    private int maxMailMessageSize = DEFAULT_MAX_MAIL_SIZE;

    /**
     * Create a new J2SESharkEngine.
     */
    public J2SEAndroidSharkEngine() {
        super();
        this.setKEPStub(new SimpleKEPStub(this));
		tcp = null;
    }

    @Override
    protected StreamStub createTCPStreamStub(RequestHandler handler, int port, boolean isHTTP) throws SharkProtocolNotSupportedException {
        try {
            tcp = new TCPStreamStub(handler, port);
            return tcp;
        } catch (IOException ioe) {
//            ioe.printStackTrace();
            throw new SharkProtocolNotSupportedException(ioe.getMessage());
        }
    }
    
	/**
         * TODO: what's that ????
         * 
	 * @param hostnameArg  	can be part of hostname, domainname, or textual ip address 
	 * 				        segment to narrow down in case more interfaces exists in 
	 * 						the system, if this argument is null and the resolver works properly
	 * 				        the environment variable COMPUTERNAME is used
	 * @returns the fully qualified domain name
	 */
	public static String getFQDN(String hostnameArg) { 
		// TODO: how to get hostname on any platform 	
		Map<String, String> env = System.getenv();
		String computername = env.get("COMPUTERNAME").toLowerCase();

		try {
//			int k = 0;
			Enumeration<NetworkInterface> a = NetworkInterface.getNetworkInterfaces();
			
			while (a.hasMoreElements()) {
				NetworkInterface b = a.nextElement();				
//				System.out.print("if["+k+"]:"+b.getDisplayName());
				
				if (!b.isLoopback() && b.isUp()) {				
					Enumeration<InetAddress> c = b.getInetAddresses();
//					int n = 0;
					while(c.hasMoreElements()) {
						InetAddress d = c.nextElement();		
						String fqdn = d.getCanonicalHostName().toLowerCase();
						
						if (fqdn.equals(d.getHostAddress())) {
							// the resolver didn't work, we've got the plain IP address
							if (hostnameArg != null) {
								if (fqdn.contains(hostnameArg)) {
									// this is a fallback for poor configurations
									// if plain IP or network address was requested, we have found it
									return fqdn;
								}
							} else {
								return fqdn;
							}
						} else {
							// the resolver got something
							if (hostnameArg != null) {
								// stick to what was given
								if (fqdn.contains(hostnameArg)) {
									// we have found what was requested
									return fqdn;
								}
							} else {
								// take the default hostname
								if (fqdn.contains(computername)) {
									// this is it
									return fqdn;
								}
							}
						}
//						System.out.print(" name["+n+"]:"+fqdn);
//						n++;
					}
				}				
//				System.out.println(" loopback:"+b.isLoopback()+"  up:"+b.isUp());
//				k++;
			}
		} catch (SocketException e) {
			e.printStackTrace();
		}

		return null;
	}


    // ===========================================================================
    // API rev. 3 methods
    /**
     * Start the TCP stub at the given portnumber.
     *
     * @param port The portnumber to use for TCP traffic.
     * @throws java.io.IOException
     */
    @Override
    public void startTCP(int port) throws IOException {
        try {
            this.start(Protocols.TCP, port);
        } catch (SharkProtocolNotSupportedException ex) {
            L.e(ex.getMessage(), this);
        }
    }

    /**
     * Stop the TCP communication stub.
     */
    @Override
    public void stopTCP() {
        try {
            this.stopProtocol(Protocols.TCP);
        } catch (SharkProtocolNotSupportedException ex) {
            L.d(ex.getMessage(), this);
        }
    }

    /**
     * Stop the UDP communication stub.
     */
//    public void stopUDP() {
//        try {
//            this.stopProtocol(Protocols.UDP);
//        } catch (SharkProtocolNotSupportedException ex) {
//            L.e(ex.getMessage(), this);
//        }
//    }

    /**
     * Start HTTP communication stub.
     */
//    public void startHTTP() throws IOException {
//        this.startHTTP(this.defaultHTTPPort);
//    }

    /**
     * Start HTTP communication stub using a custom port.
     * 
     * @param port The portnumber to listen on.
     */
//    public void startHTTP(int port) throws IOException {
//        try {
//            this.start(Protocols.HTTP, port);
//        } catch (SharkProtocolNotSupportedException ex) {
//            L.e(ex.getMessage(), this);
//        }
//    }

    /**
     * Stop HTTP communication stub.
     */
//    public void stopHTTP() {
//        try {
//            this.stopProtocol(Protocols.HTTP);
//        } catch (SharkProtocolNotSupportedException ex) {
//            L.e(ex.getMessage(), this);
//        }
//    }

    /**
     * Start mail stub with the given server addresses.
     *
     * @param smtpServer The smtp server to use.
     * @param popServer The popserver to use.
     */
    @Override
    public void startMail() throws IOException {
        try {
            this.start(Protocols.MAIL);
        } catch (SharkProtocolNotSupportedException ex) {
            L.e(ex.getMessage(), this);
        }
    }

    /**
     * Stop mail-based stubs.
     */
    public void stopMail() {
        try {
            this.stopProtocol(Protocols.MAIL);
        } catch (SharkProtocolNotSupportedException ex) {
            L.d(ex.getMessage(), this);
        }
    }

    public PeerSensor startPeerSensor() {
        // Not supported yet
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public GeoSensor getGeoSensor() {
        // Not supported yet
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void stopPeerSensor() {
        // TODO
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void stopGeoSensor() {
        // TODO
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void startGeoSensor() {
        // TODO Auto-generated method stub
    }

    @Override
	public MessageStub createMailStub(RequestHandler handler) 
                throws SharkProtocolNotSupportedException  {
        
        return new MailMessageStub(handler, 
                this.getSMTPHost(), 
                this.getSMTPUserName(),
                this.getSMTPPassword(),
                this.sslSMTP,
                this.getPOP3Host(),
                this.getPOP3UserName(),
                this.getPOP3Address(),
                this.getPOP3Password(),
                this.mailCheckInterval,
                this.sslPOP3,
                this.maxMailMessageSize);
    }
    
    private SharkKBMessageStorage kbStorage = null;
    public MessageStorage getMessageStorage() throws SharkKBException {
        if(this.kbStorage == null) {
            this.kbStorage = new SharkKBMessageStorage(new InMemoSharkKB());
        }
        
        return this.kbStorage;
    }
    
    @Override
    protected StreamStub createMailStreamStub(RequestHandler handler)
            throws SharkProtocolNotSupportedException {
      
      MessageStub mailMessageStub = createMailStub(handler);
      StreamStub mailStreamStub;
        try {
            mailStreamStub = new M2SStub(
                    this.getMessageStorage(), mailMessageStub, handler);
        } catch (SharkKBException ex) {
            // TODO - that not actually a protocol not supported problem.
            throw new SharkProtocolNotSupportedException(ex.getMessage());
        }
      
      return mailStreamStub;
    }
    
    /**
     * 
     * @param smtpHost SMTP host used to transmit messages
     * @param smtpUserName user name on SMTP host
     * @param smtppwd password on SMTP host: This passwort is never made 
     * persistent in the Shark code. Application developers must ensure that their
     * applications handle passwords with the required care.
     * @param maxOutgoingMessageLength
     * @param sslSMTP use ssl for SMTP 
     * @param pop3Host POP3 host to get mails
     * @param pop3user POP3 user name
     * @param pop3ReplyAddress Adress that is used as reply address
     * @param pop3pwd POP3 password
     * @param mailCheckInterval integer number, delay in minutes
     * @param sslPOP3 
     */
    public void setMailConfiguration(
            String smtpHost, String smtpUserName, String smtppwd, 
            boolean sslSMTP,
            String pop3Host, String pop3user, String pop3ReplyAddress, String pop3pwd, 
            int mailCheckInterval, boolean sslPOP3) {

        this.setMailConfiguration(smtpHost, smtpUserName, smtppwd, 
            sslSMTP, pop3Host, pop3user, pop3ReplyAddress, pop3pwd, 
            mailCheckInterval, sslPOP3, DEFAULT_MAX_MAIL_SIZE);
    }
    
    public void setMailConfiguration(
            String smtpHost, String smtpUserName, String smtppwd, 
            boolean sslSMTP,
            String pop3Host, String pop3user, String pop3ReplyAddress, String pop3pwd, 
            int mailCheckInterval, boolean sslPOP3, int maxMailMessageSize) {
        
        // persist those settings
        this.persistMailSettings(smtpHost, smtpUserName, smtppwd, 
                sslSMTP, pop3Host, pop3user, pop3ReplyAddress, pop3pwd, 
                mailCheckInterval, sslPOP3, maxMailMessageSize);
        
        this.smtpHost = smtpHost;
        this.smtpUserName = smtpUserName;
        this.smtppwd = smtppwd;
        
        this.pop3Host = pop3Host;
        this.pop3user = pop3user;
        this.pop3MailAddress = pop3ReplyAddress;
        this.pop3pwd = pop3pwd;
        
        this.mailCheckInterval = mailCheckInterval;        
        this.sslPOP3 = sslPOP3;
        this.sslSMTP = sslSMTP;
        this.maxMailMessageSize = maxMailMessageSize;
        
        boolean restart = false;
        if(this.isProtocolStarted(Protocols.MAIL)) {
            restart = true;
        }
        
        // stop old mailstub - if any
        this.stopMail();
        
        if(restart) {
            try {
                this.startMail();
            } catch (IOException ex) {
                L.e("cannot restart e-mail: " + ex.getMessage(), this);
            }
        }
        
        
    }
    
    public String getPOP3Address() throws SharkProtocolNotSupportedException {
        if(this.pop3MailAddress == null) {
            L.w("POP3 mail address not set", this);
            throw new SharkProtocolNotSupportedException();
        }
        
        return this.pop3MailAddress;
    }

    public String getSMTPHost() throws SharkProtocolNotSupportedException {
        if(this.smtpHost == null) {
            L.w("SMTP host not set", this);
            throw new SharkProtocolNotSupportedException();
        }
        
        return this.smtpHost;
    }
    
    public String getSMTPUserName() throws SharkProtocolNotSupportedException {
        if(this.smtpUserName == null) {
            L.w("SMTP user name not set", this);
            throw new SharkProtocolNotSupportedException();
        }
        
        return this.smtpUserName;
    }

    public String getSMTPPassword() throws SharkProtocolNotSupportedException {
        if(this.smtppwd == null) {
            L.w("SMTP password not set", this);
            throw new SharkProtocolNotSupportedException();
        }
        
        return this.smtppwd;
    }

    public String getPOP3Password() throws SharkProtocolNotSupportedException {
        if(this.pop3pwd == null) {
            L.w("POP3 password not set", this);
            throw new SharkProtocolNotSupportedException();
        }
        
        return this.pop3pwd;
    }

    public String getPOP3Host() throws SharkProtocolNotSupportedException {
        if(this.pop3Host == null) {
            L.w("POP3 host not set", this);
            throw new SharkProtocolNotSupportedException();
        }
        
        return this.pop3Host;
    }

    public String getPOP3UserName() throws SharkProtocolNotSupportedException {
        if(this.pop3user == null) {
            L.w("POP3 user name not set", this);
            throw new SharkProtocolNotSupportedException();
        }
        
        return this.pop3user;
    }
    
    public boolean pop3UsesSSL() {
        return this.sslPOP3;
    }
    
    public boolean smtpUsesSSL() {
        return this.sslSMTP;
    }
    
    public int getMailCheckInterval() {
        return this.mailCheckInterval;
    }


    /**
     * Return the local address of this SharkEngine for the given protocol.
     * @param type An int constant from <code>net.sharkfw.protocols.Protocols</code> representing the comm protocol.
     * @return A string containing the local address for the given protocol.
     */
    public String getLocalAddress(int type) {
 	       
        switch (type) {
            case net.sharkfw.protocols.Protocols.TCP:
                return this.tcp == null ? null : this.tcp.getLocalAddress();
        }

        return null;
    }  
    
    private SystemPropertyHolder ph;
    
    public void setPropertyHolder(SystemPropertyHolder ph) {
        this.ph = ph;
    }
    
    @Override
    protected SystemPropertyHolder getSystemPropertyHolder() {
        return this.ph;
    }
    
    @Override
    public void persist() throws SharkKBException {
//        if(this.ph != null) {
//            // black / white list manager - move to SharkEngine
//            String serializedList = Util.PSTArrayList2String(whiteList);
//            this.ph.setSystemProperty(WHITE_LIST, serializedList);
//
//            serializedList = Util.PSTArrayList2String(blackList);
//            this.ph.setSystemProperty(BLACK_LIST, serializedList);
//
//            this.ph.setSystemProperty(USE_WHITE_LIST, Boolean.toString(this.useWhiteList));
            
            // others - move to J2SEAndroidSharkEngine
            
            
//        }
    }
    
    public final void refreshStatus() throws SharkKBException {
//        if(this.ph != null) {
//            // restore white and black list and set guardKP
//            // white list
//            String serializedList = this.ph.getSystemProperty(WHITE_LIST);
//            if(serializedList != null) {
//                try {
//                    this.whiteList = Util.String2PSTArrayList(serializedList);
//                } catch (SharkKBException ex) {
//                    // TODO
//                }
//            }
//
//            // black list
//            serializedList = this.ph.getSystemProperty(BLACK_LIST);
//            if(serializedList != null) {
//                try {
//                    this.blackList = Util.String2PSTArrayList(serializedList);
//                } catch (SharkKBException ex) {
//                    // TODO
//                }
//            }
//
//            if(this. whiteList == null) {
//                this.whiteList = new ArrayList<PeerSemanticTag>();
//            }
//
//            if(this.blackList == null) {
//                this.blackList = new ArrayList<PeerSemanticTag>();
//            }
//
//            this.useWhiteList = Boolean.parseBoolean(this.ph.getSystemProperty(USE_WHITE_LIST));
            
            this.refreshMailSettings();
//        }
    }

    private static final String SMTP_HOST = "se_smtpHost";
    private static final String SMTP_USER = "se_smtpUser";
    private static final String SMTP_PWD = "se_smtpPwd";
    private static final String SMTP_SSL = "se_sslSMTP";
    private static final String POP3_HOST = "se_pop3Host";
    private static final String POP3_USER = "se_pop3User";
    private static final String REPLAYADDRESS = "se_replyAddress";
    private static final String POP3_PWD = "se_pop3Pwd";
    private static final String MAILCHECK_INTERVAL = "se_mailCheckInterval";
    private static final String POP3_SSL = "se_sslPop3";
    private static final String MAX_MAIL_MESSAGE_LEN = "se_maxMailMessageLen";
    
    private void persistMailSettings(
            String smtpHost, String smtpUserName, String smtppwd, 
            boolean sslSMTP,
            String pop3Host, String pop3user, String replyAddress, String pop3pwd, 
            int mailCheckInterval, boolean sslPOP3, int maxMailMessageLen) {
        
        if(this.ph != null) {
            this.ph.setSystemProperty(SMTP_HOST, smtpHost);
            this.ph.setSystemProperty(SMTP_USER, smtpUserName);
            this.ph.setSystemProperty(SMTP_PWD, smtppwd);
            this.ph.setSystemProperty(SMTP_SSL, Boolean.toString(sslSMTP));
            this.ph.setSystemProperty(POP3_HOST, pop3Host);
            this.ph.setSystemProperty(POP3_USER, pop3user);
            this.ph.setSystemProperty(REPLAYADDRESS, replyAddress);
            this.ph.setSystemProperty(POP3_PWD, pop3pwd);
            this.ph.setSystemProperty(MAILCHECK_INTERVAL, Integer.toString(mailCheckInterval));
            this.ph.setSystemProperty(POP3_SSL, Boolean.toString(sslPOP3));
            this.ph.setSystemProperty(MAX_MAIL_MESSAGE_LEN, Integer.toString(maxMailMessageLen));
        }
    }
    
    /**
     * Set all parameter for which not default can exist.
     * It is assumed that smtp and pop3 user are the same and that
     * both passworts are the same as well
     * 
     * @param smtpHost
     * @param userName
     * @param pwd
     * @param pop3Host
     * @param replyAddress 
     */
    public void setBasicMailConfiguration(
            String smtpHost, String userName, String pwd, 
            String pop3Host, String replyAddress) {
        
        this.setMailConfiguration(smtpHost, 
                userName, pwd, 
                false, pop3Host, 
                userName, replyAddress, 
                pwd, 1, false, 10240);
    }
    
    private void refreshMailSettings() {
        if(this.ph != null) {
            String smtpHost = this.ph.getSystemProperty(SMTP_HOST);
            if(smtpHost == null) {
                return; // there was no previous call
            }
            
            String smtpUserName = this.ph.getSystemProperty(SMTP_USER);
            String smtppwd = this.ph.getSystemProperty(SMTP_PWD);
            boolean sslSMTP = Boolean.valueOf(this.ph.getSystemProperty(SMTP_SSL));
            String pop3Host = this.ph.getSystemProperty(POP3_HOST);
            String pop3user = this.ph.getSystemProperty(POP3_USER);
            String replyAddress = this.ph.getSystemProperty(REPLAYADDRESS);
            String pop3pwd = this.ph.getSystemProperty(POP3_PWD);
            
            String value = this.ph.getSystemProperty(MAILCHECK_INTERVAL);
            int mailCheckInterval = 1;
            if(value != null) {
                mailCheckInterval = Integer.valueOf(value);
            }
            
            boolean sslPOP3 = Boolean.valueOf(this.ph.getSystemProperty(POP3_SSL));
            
            // set again
            this.setMailConfiguration(smtpHost, smtpUserName, smtppwd, sslSMTP, 
                    pop3Host, pop3user, replyAddress, pop3pwd, mailCheckInterval, sslPOP3);
        }
    }
        
    /////////////////////////////////////////////////////////////////
    //                 remember unsent messages                    //
    /////////////////////////////////////////////////////////////////
    
    private SharkKB unsentMessagesKB;
    private static final String UNSENTMESSAGE_SI = "http://www.sharksystem.net/vocabulary/unsentMesssages";
    private SemanticTag unsentMessagesST = InMemoSharkKB.createInMemoSemanticTag("UnsentMessage", UNSENTMESSAGE_SI);
    
    private static final String INTEREST_CONTENT_TYPE = "x-shark/interest";
    private static final String KNOWLEDGE_CONTENT_TYPE = "x-shark/knowledge";
    
    public void setUnsentMessagesKB(SharkKB kb) {
       this.unsentMessagesKB = kb; 
    }
    
    private ContextCoordinates getUnsentCC(PeerSemanticTag recipient) {
        return InMemoSharkKB.createInMemoContextCoordinates(
                this.unsentMessagesST, recipient, null, null, 
                null, null, SharkCS.DIRECTION_NOTHING);
    }
    
    private ContextPoint getUnsentMessageCP(PeerSemanticTag recipient) {
        if(this.unsentMessagesKB != null) {
            try {
                ContextPoint cp = this.unsentMessagesKB.createContextPoint(
                        this.getUnsentCC(recipient));

                return cp;
            }
            catch(SharkKBException e) {
            }
        }
        
        return null;
    }
    
    private XMLSerializer xs = null;
    
    private XMLSerializer getXMLSerializer() {
        if(this.xs == null) {
            this.xs = new XMLSerializer();
        }
        
        return this.xs;
    }

    public void rememberUnsentInterest(SharkCS interest, PeerSemanticTag recipient) {
        ContextPoint cp = this.getUnsentMessageCP(recipient);
        
        if(cp == null) {
            L.w("cannot save unsent interest: ", this);
            return;
        }
        
        try {
            String interestString = this.getXMLSerializer().serializeSharkCS(interest);
            Information i = cp.addInformation(interestString);
            
            i.setContentType(INTEREST_CONTENT_TYPE);
            
        } catch (SharkKBException ex) {
            L.d("cannot serialize interest", this);
        }
    }
    
    public void rememberUnsentKnowledge(Knowledge k, PeerSemanticTag recipient) {
        ContextPoint cp = this.getUnsentMessageCP(recipient);
        
        if(cp == null) {
            L.w("cannot save unsent knowledge: ", this);
            return;
        }
        
        try {
            Information i = cp.addInformation();
            OutputStream os = i.getOutputStream();
            SharkOutputStream sos = new UTF8SharkOutputStream(os);
            this.getXMLSerializer().write(k, sos);
            i.setContentType(KNOWLEDGE_CONTENT_TYPE);
        } catch (Exception ex) {
            L.d("cannot serialize knowledge", this);
        }
    }
    
    public void sendUnsentMessages() {
        if(this.unsentMessagesKB != null) {
            try {
                Enumeration<ContextPoint> cpEnum = this.unsentMessagesKB.getAllContextPoints();
                if(cpEnum == null) {
                    return;
                }
                
                while(cpEnum.hasMoreElements()) {
                    ContextPoint cp = cpEnum.nextElement();
                    
                    this.unsentMessagesKB.removeContextPoint(cp.getContextCoordinates());
                    
                    Enumeration<Information> infoEnum = cp.enumInformation();
                    if(infoEnum == null) {
                        continue;
                    }
                    
                    while(infoEnum.hasMoreElements()) {
                        Information i = infoEnum.nextElement();
                        
                        if(i.getContentType().equalsIgnoreCase(INTEREST_CONTENT_TYPE)) {
                            // Interest
                            String serialeInterest = i.getContentAsString();
                            SharkCS deserializeSharkCS = this.getXMLSerializer().deserializeSharkCS(serialeInterest);
                            cp.removeInformation(i);
                            
                            // TODO reset - prevent loop!
                        }
                        else if(i.getContentType().equalsIgnoreCase(KNOWLEDGE_CONTENT_TYPE)) {
                            // knowledge
                            // TODO
                        }
                        
                    }
                }
                
            }
            catch(SharkKBException e) {
                
            }
        }
    }
    
    public void removeUnsentMessages() {
        if(this.unsentMessagesKB != null) {
            try {
                Enumeration<ContextPoint> cpEnum = this.unsentMessagesKB.getAllContextPoints();
                if(cpEnum == null) {
                    return;
                }
                
                while(cpEnum.hasMoreElements()) {
                    ContextPoint cp = cpEnum.nextElement();
                    this.unsentMessagesKB.removeContextPoint(cp.getContextCoordinates());
                }
            }
            catch(SharkKBException e) {
                L.d("problems while iterating stored unsent messages", this);
            }
        }
    }

    /////////////////////////////////////////////////////////////////
    //                           others                            //
    /////////////////////////////////////////////////////////////////
    
//    public void sendCP(ContextPoint cp, Iterator<PeerSemanticTag> recipients) throws SharkKBException {
//        
//        if(cp == null || recipients == null) {
//            throw new SharkKBException("parameter must not be null");
//        }
//        
//        ContextCoordinates cc = cp.getContextCoordinates();
//        
//        KnowledgePort senderKP = null;
//        
//        Enumeration<KnowledgePort> kpEnum = this.getKPs();
//        if(kpEnum != null) {
//            while(kpEnum.hasMoreElements()) {
//                KnowledgePort kp = kpEnum.nextElement();
//                SharkCS interest = kp.getInterest();
//                
//                if(interest != null) {
//                    if(SharkCSAlgebra.isIn(interest, cc)) {
//                        // we have a kp found
//                        senderKP = kp;
//                        break;
//                    }
//                }
//            }
//            
//            if(senderKP != null) {
//                Knowledge k = InMemoSharkKB.createInMemoKnowledge();
//                k.addContextPoint(cp);
//                
//                while(recipients.hasNext()) {
//                    try {
//                        PeerSemanticTag recipient = recipients.next();
//                        this.sendKnowledge(k, recipient, senderKP);
//                    }
//                    catch(SharkException e) {
//                        // ignore and try next
//                    }
//                }
//            }
//        }
//    }    
}
