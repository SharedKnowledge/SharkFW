package net.sharkfw.protocols.mail;

import com.sun.mail.util.MailSSLSocketFactory;

import net.sharkfw.protocols.MessageStub;
import net.sharkfw.protocols.PeerAddress;
import net.sharkfw.protocols.RequestHandler;
import net.sharkfw.system.Base64;
import net.sharkfw.system.L;
import net.sharkfw.system.Streamer;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Properties;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

/**
 * Implementation of a message stub based on SMTP and POP3
 * A previous version was written by Marian Volk (thanks.)
 *      
 * This implementation is based on some assumptions:
 * The address is defined as string in the following format:
 * 
 * mail://recipient@domain?maxLength=[len in kByte]
 * 
 * A malfored address is ignored - just an warning is issued
 * but no exceptions.
 * 
 * Sender identity must be provided by the Shark Engine
 * which must be transmitted as parameter in the constructor.
 * 
 * @author thsc
 */

public final class MailMessageStub implements MessageStub, Runnable {
    private static final String SSL_PORT = "465";
    private static final String SUBJECT = "Shark message - please leave in the box for your Shark application (sharksystem.net)";
    private static final int MINIMAL_CHECKING_DELAY = 60000; // one minute
    private static final int DEFAULT_MAX_MAIL_LENGTH = 1024; // MByte
    private static final int PARALLEL_MESSAGE_CAPACITY = 2; // 3 mails should fit into a mailbox at once

    private RequestHandler handler;
    private String smtpHost;
    private String smtpUserName;
    private final String smtppwd;
    private String pop3Host;
    private String pop3UserName;
    private String pop3Address;
    private String pop3pwd;
    private final String SHARK_CONTENT_TYPE = "application/x-sharkfw";
    private final int mailCheckInterval;
    private Thread mailCheckThread;
    private String replyAddress;
    
    // used for debugging
    private boolean dequeue;
    private boolean finished;
    
    public static final boolean SSL_IS_DEFAULT = false;
    private final boolean sslSMTP;
    private final boolean sslPOP3;
    private final int maxMessageLen;
    
    /**
     * 
     * @param handler
     * @param smtpHost
     * @param smtpUserName
     * @param smtppwd
     * @param pop3HostName
     * @param pop3UserName
     * @param pop3Address
     * @param pop3Pwd
     * @param mailCheckInterval 
     * 
     * @deprecated 
     */
    public MailMessageStub(RequestHandler handler, String smtpHost, 
            String smtpUserName, String smtppwd, String pop3HostName,
            String pop3UserName,  String pop3Address,  String pop3Pwd, 
            int mailCheckInterval) {
    
        this(handler, smtpHost, smtpUserName, smtppwd, 
                MailMessageStub.SSL_IS_DEFAULT,
                pop3HostName, pop3UserName,  pop3Address,  pop3Pwd, mailCheckInterval,
                MailMessageStub.SSL_IS_DEFAULT);
    }
    
    public MailMessageStub(RequestHandler handler, String smtpHost, 
            String smtpUserName, String smtppwd, boolean sslSMTP, 
            String pop3HostName,
            String pop3UserName,  String pop3Address,  String pop3Pwd, 
            int mailCheckInterval, boolean sslPOP3) {
        
        this(handler, smtpHost, 
            smtpUserName, smtppwd, sslSMTP, 
            pop3HostName, pop3UserName,  pop3Address, pop3Pwd, 
            mailCheckInterval, sslPOP3, DEFAULT_MAX_MAIL_LENGTH);
        
    }
    
    public MailMessageStub(RequestHandler handler, String smtpHost, 
            String smtpUserName, String smtppwd, boolean sslSMTP, 
            String pop3HostName,
            String pop3UserName,  String pop3Address,  String pop3Pwd, 
            int mailCheckInterval, boolean sslPOP3, int maxMessageLen) {
        
        this.handler = handler;
        this.smtpHost = smtpHost;
        this.smtpUserName = smtpUserName;
        this.smtppwd = smtppwd;
        this.pop3Host = pop3HostName;
        this.pop3UserName = pop3UserName;
        this.pop3Address = pop3Address;
        this.pop3pwd = pop3Pwd;
        this.mailCheckInterval = mailCheckInterval;
        
        this.maxMessageLen = maxMessageLen;
        this.setReplyAddressString(pop3Address);
        
        this.sslSMTP = sslSMTP;
        this.sslPOP3 = sslPOP3;
        this.dequeue = false; 
        this.finished = false;
    }
    
    public final void start() {
    	this.finished = false;
        if(!this.started()) {
            this.mailCheckThread = new Thread(this);
            this.mailCheckThread.start();
        }        
    }

    private boolean checkAgain = true;
    @Override
    public void stop() {
        this.checkAgain = false;
        if(this.mailCheckThread != null) {
            this.mailCheckThread.interrupt();
        }        
        this.mailCheckThread = null;
    }
    
    public boolean started() {
        return this.mailCheckThread != null;
    }
    

	public void waitForStoppedMailQ(int limit, String msg) {
		int k = 0;
		while (!this.finished && (k<limit)) {
			try {
				Thread.sleep(20);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			k++;
		}
		if (k >= limit) {
			System.out.println("it took longer than "+(20*k)+"ms to "+msg+" mailqueue");
		}
	}
	
	public void emptyQueue() {
		stop();
		waitForStoppedMailQ(200, "stop");    	
		this.dequeue = true;
		start();
		// wait until stopped by itself
		waitForStoppedMailQ(600, "empty");    	    	
	}

    /**
     * for use in sub classes only
     * @param handler 
     */
    protected void setRequestHandler(RequestHandler handler) {
        this.handler = handler;
    }
    
    private HashMap<String, RequestHandler> rqHandlerTable = new HashMap<String, RequestHandler>();
    
    
    
    
    /**
     * for use in sub classes only
     * @param handler 
     */
    protected void setRequestHandler(RequestHandler handler, String channelID) {
        this.handler = handler;
    }
    
    @Override
    public void setReplyAddressString(String addr) {
        // calculate max message length
        int nettoLen = this.maxMessageLen / MailMessageStub.PARALLEL_MESSAGE_CAPACITY;
        
        // each message is base64 encoded - which doubles the length
        nettoLen /= 2;
        
        this.replyAddress = addr + "?" + PeerAddress.MAXLEN_PARAMETER + "=" + String.valueOf(nettoLen);
    }

    @Override
    public void sendMessage(byte[] msgBytes, String recAddress) throws IOException {
        recAddress = this.makePlainMailAddress(recAddress);
        L.d("Sending mail to: " + recAddress, this);
        
        if(recAddress.equals(this.pop3UserName)) {
          L.e("Messageloop! From " + this.pop3UserName + " to: " + recAddress, this);
        }
        
        msgBytes = Base64.encodeBytesToBytes(msgBytes);
        String msgText = new String(msgBytes, "UTF-8");
        
        if(!this.sslSMTP) {
            try {
                Properties props = new Properties();
                props.put("mail.smtp.host", this.smtpHost);
                props.put("mail.from", this.pop3Address);
                Session session = Session.getInstance(props, null);

                MimeMessage mimeMsg = new MimeMessage(session);
                mimeMsg.setFrom();
                mimeMsg.setRecipients(javax.mail.Message.RecipientType.TO, recAddress);

                mimeMsg.setSubject(SUBJECT);

                mimeMsg.setText(msgText, "UTF-8");
                mimeMsg.setHeader("Content-Type", SHARK_CONTENT_TYPE);

                Transport smtpTransport = session.getTransport("smtp");
                smtpTransport.connect(this.smtpHost, this.smtpUserName, this.smtppwd);

                smtpTransport.sendMessage(mimeMsg, mimeMsg.getAllRecipients());

                smtpTransport.close();
            } catch (MessagingException ex) {
                L.e("couldn't send message: " + ex.getMessage(), this);
                throw new IOException(ex.getMessage());
            }
        } else {
        
            // SSL
            L.d("set SSL / TLS parameters", this);
            Properties props = new Properties();
            props.put("mail.smtp.user", this.smtpUserName);
            props.put("mail.smtp.host", this.smtpHost);
            props.put("mail.smtp.port", SSL_PORT);
            props.put("mail.smtp.starttls.enable","true");
            props.put("mail.smtp.auth", "true");
            //props.put("mail.smtp.debug", "true");
            props.put("mail.smtp.socketFactory.port", SSL_PORT);
            props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
            props.put("mail.smtp.socketFactory.fallback", "false");

            SecurityManager security = System.getSecurityManager();

            try {
                Authenticator auth = new SharkAuthenticatorWrapper(this.smtpUserName, this.smtppwd);
                Session session = Session.getInstance(props, auth);
                //session.setDebug(true);

                MimeMessage msg = new MimeMessage(session);
                msg.setText(msgText, "UTF-8");
                msg.setSubject(SUBJECT);
                msg.setFrom(new InternetAddress(this.pop3Address));
                msg.setRecipients(javax.mail.Message.RecipientType.TO, recAddress);
                msg.setHeader("Content-Type", SHARK_CONTENT_TYPE);

                Transport.send(msg);
            }
            catch (Exception mex) {
                L.d("couldn't send message: " + mex.getMessage(), this);
                throw new IOException(mex.getMessage());
            }
        }
    }

    @Override
    public String getReplyAddressString() {
        // add maxLen
        
        // calculate: 3 message should fit to 
        return this.replyAddress;
    }

    @Override
    public void run() {
        /*
         * we do the whole process in each loop:
         * opening connection, checking for mails and closing
         * connection. Might be somewhat time consuming but
         * it's better safe resource on mail server side?
         */
        do {
        	L.d((this.dequeue?"Dequeue":"Check")+"ing mails for: " + this.pop3UserName, this);
            Store store = null;
            Folder folder = null;
            Session session = null;
            try {
                if(!this.sslSMTP) {
                    session = Session.getDefaultInstance( new Properties() );

                    store = session.getStore( "pop3" );
                } else {
                    // SSL
                    L.d("setting up SSL", this);
                    Properties props = new Properties();

                    props.put("mail.host", this.pop3Host);
                    props.put("mail.store.protocol", "pop3s");
                    props.put("mail.pop3s.auth", "true");
                    props.put("mail.pop3s.port", "995");
                    
                    MailSSLSocketFactory socketFactory= new MailSSLSocketFactory();
                    // that's hards stuff, though - I trust anyone, means: encryption is useless
                    // nevertheless - shark makes its own security.
                    socketFactory.setTrustAllHosts(true);
                    props.put("mail.pop3s.socketFactory", socketFactory);

                    session = Session.getDefaultInstance(props, null);              
                    store = session.getStore();
                }

                store.connect( this.pop3Host, this.pop3UserName, this.pop3pwd );
                folder = store.getFolder( "INBOX" );
                folder.open( Folder.READ_WRITE );

                Message messages[] = folder.getMessages();

                L.d( this.pop3UserName + " having " + messages.length + " messages waiting.", this);
                for ( int i = 0; i < messages.length; i++ )
                {
                  Message m = messages[i];

                  String contentType = m.getContentType();
                  
                  Address[] from = m.getFrom();

                  if(contentType.equalsIgnoreCase(SHARK_CONTENT_TYPE)) {
                      // mark message as deleted
                      m.setFlag(Flags.Flag.DELETED, true);

                      if (!this.dequeue) {	
                      // read it
                      ByteArrayOutputStream baos = new ByteArrayOutputStream();
                      
                      InputStream is = m.getInputStream();
                      Streamer.stream(is, baos, 1000);

                      // TODO: can this be null?
                      
                      // Decode Base64 encoded message
                      byte[] received = baos.toByteArray();
                      byte[] decoded = Base64.decode(received);
                      
                      // Pass message on as byte[] to the upper protocol levels.
                      //this.handleMessage(partNumber, maxParts, channelID, from, decoded);
                      L.d("Passing on message " + (i+1) + " of " + messages.length + " on " + this.pop3UserName, this);
                      this.handler.handleMessage(decoded, this);
                      
                      } else {
                    	  L.d("dequeueing message " + (i+1) + " of " + messages.length + " on " + this.pop3UserName, this);
                      }
                  }
                }
            } catch (Exception ex) {
                L.l("exception during "+(sslPOP3?"ssl":"")+"mail access for "+pop3UserName+"("+pop3pwd+") was handled:", this);
                L.d(ex.getMessage(), ex);
            } finally {
                try {
                    // in any case: close and expunge deleted mails
                    if(folder != null) {
                        folder.close( true );
                    }
                    
                    if(store != null) {
                        store.close();
                    }
                } catch (MessagingException ex) {
                    // I have done what I could - ignore that desaster.
                } catch (IllegalStateException ex2) {
                	// ignore
                }
            }
            
            try {
            	if (this.dequeue) {
            		// stop the whole thing
            		this.dequeue = false;            		
            		this.checkAgain = false;
            		this.mailCheckThread = null;
            		break;
            	}
                Thread.sleep(this.mailCheckInterval * MailMessageStub.MINIMAL_CHECKING_DELAY);
            } catch (InterruptedException ex) {
                // wake up call from stop() probably
                L.l("mail stub sleep interrupted - that's ok", this);
            }
            
        } while (this.checkAgain);
        this.finished = true;
    }
    
//    protected void handleMessage(String[] partNumber, String[] maxNumber, 
//            String[] channelID, Address[] from, byte[] msg) {
//        
//        handler.handleMessage(msg, this);
//        
//    }

    private String makePlainMailAddress(String recAddress) {
        
        // cut leading protocol definition
        if(recAddress.startsWith("mail://")) {
            recAddress = recAddress.substring("mail://".length());
        }
        
        // cut trailing parameters
        int index = recAddress.indexOf("?");
        if(index != -1) {
            recAddress = recAddress.substring(0, index);
        }
        
        return recAddress;
    }

    @Override
    public void setHandler(RequestHandler handler) {
        this.handler = handler;
    }
}	
