
import net.sharkfw.kp.HubKP;
import net.sharkfw.peer.J2SESharkEngine;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author mfi
 */
public class J2SEMailHub {
  
  private static long interestsValid = 300000; // Interests are valid 5 minutes.
  private static int interestCheckInterval = 1000; // Remove expired interests every second.
  
  private static String pathToStatusPage = "/opt/lampp/htdocs/status.html";
  private static int statusPageInterval = 10000; // Print a new status page every 10 seconds.
  
  // Mail config data
  private static String smtpHost = "smtp.sharksystem.net";
  private static String pop3Host = "pop3.sharksystem.net";
  private static String smtpUser = "hub@sharksystem.net";
  private static String pop3User = "hub@sharksystem.net";
  private static String smtpPassword = "hubhub";
  private static String pop3Password = "hubhub";
  private static String replyAddress = "hub@sharksystem.net";
  
  
  public static void main(String[] args) {
    J2SESharkEngine se = new J2SESharkEngine();
    
//     Set Hub's supported protocols
//     Start TCP protocol engine on port 8888
    se.startTCP(8888);
    System.out.println("Started TCP-Server on port 8888.");
    
    // Set mail-config, check mail every minute
    se.setMailConfiguration(smtpHost, smtpUser, smtpPassword, pop3Host, pop3User, replyAddress, pop3Password, 1);
    // Start mail protocol-engine
    se.startMail();
    System.out.println("Started Mail-Service for " + smtpUser);
    
    HubKP hub = new HubKP(se, interestsValid, interestCheckInterval, pathToStatusPage, statusPageInterval);
  }
  
}
