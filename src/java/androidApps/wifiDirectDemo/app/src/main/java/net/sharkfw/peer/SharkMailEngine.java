package net.sharkfw.peer;

import net.sharkfw.kep.SharkProtocolNotSupportedException;

/**
 *
 * @author Jacob Zschunke
 */
public interface SharkMailEngine {
    public void setMailConfiguration(String smtpHost, 
                                     String smtpUserName, 
                                     String smtppwd,
                                     String pop3Host, 
                                     String pop3user, 
                                     String pop3Address, 
                                     String pop3pwd, 
                                     int mailCheckInterval);
    public String getPOP3Address() throws SharkProtocolNotSupportedException;
    public String getSMTPHost() throws SharkProtocolNotSupportedException;
    public String getSMTPUserName() throws SharkProtocolNotSupportedException;
    public String getSMTPPassword() throws SharkProtocolNotSupportedException;
    public String getPOP3Password() throws SharkProtocolNotSupportedException;
    public String getPOP3Host() throws SharkProtocolNotSupportedException;
    public String getPOP3UserName() throws SharkProtocolNotSupportedException;    
}
