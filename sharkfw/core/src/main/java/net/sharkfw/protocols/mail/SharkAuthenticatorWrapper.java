package net.sharkfw.protocols.mail;

import javax.mail.PasswordAuthentication;

/**
 *
 * @author thsc
 */
class SharkAuthenticatorWrapper extends javax.mail.Authenticator {
    private final String mailAddress;
    private final String pwd;
    
    SharkAuthenticatorWrapper(String mailAddress, String pwd) {
        this.mailAddress = mailAddress;
        this.pwd = pwd;
    }
    
    public PasswordAuthentication getPasswordAuthentication() {
        return new PasswordAuthentication(this.mailAddress, this.pwd);
    }
}
