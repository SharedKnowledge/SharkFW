/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package  testmidlets;

import net.sharkfw.kep.PeerHandler;
import java.io.IOException;
import java.io.InputStream;
import net.sharkfw.wrapper.Vector;
import javax.microedition.midlet.*;
import javax.microedition.lcdui.*;
import net.sharkfw.protocols.MessageStub;
import net.sharkfw.protocols.RequestHandler;
import net.sharkfw.protocols.StreamConnection;
import net.sharkfw.protocols.StreamStub;
import net.sharkfw.protocols.bt.BTL2CAPStub;
import net.sharkfw.protocols.bt.BTRFCOMMStub;

/**
 * @author mfi
 */
public class BT_Test_GUI extends MIDlet implements PeerHandler, CommandListener, RequestHandler{ 
        
    private Form f;
    
    private Display display;
    
    private Vector nearbyDevices = new Vector();
    
    private final String L2CAP_LBL = "l2cap";
    
    private final String RFCOMM_LBL = "rfcomm";
    
    private final String EXIT_LBL = "exit";

    private final String STATUS_LBL = "status:";
    
    private BTL2CAPStub l2c = null;
    
    private BTRFCOMMStub rfc = null;
    
    private Command exitCommand = new Command(EXIT_LBL, Command.EXIT, 1);
    private Command l2capCommand = new Command(L2CAP_LBL, Command.ITEM, 1);
    private Command rfcommCommand = new Command(RFCOMM_LBL, Command.ITEM, 1);
    
    private StringItem string1 = new StringItem(STATUS_LBL, "");
    
    public BT_Test_GUI(){
         
        display = Display.getDisplay(this);
        f = new Form("BT-Test");


        f.append(string1);


        f.addCommand(exitCommand);
        f.addCommand(l2capCommand);
        f.addCommand(rfcommCommand);
        
        f.setCommandListener(this);

        display.setCurrent(f);

        
    }
    public void startApp() {
        
    }

    public void pauseApp() {
    }

    public void destroyApp(boolean unconditional) {
    }

    
    /* PeerHandler-Methoden */
    public void handlePeer(String peerName, String serviceName, String addressString, int protocol, StreamStub stub) {
        this.string1.setText(this.string1.getText() + "\n" + "HandlePeer (RFCOMM)");
        this.string1.setText(this.string1.getText() + "\n" + "Found:" + peerName + " " + serviceName + " " + addressString + " " + protocol);
        
       StreamConnection conn;
        try{
            System.out.println("Sende RFCOMM Nachricht ... ");
            conn = this.rfc.createStreamConnection(addressString);
        } catch (IOException ex) {
            System.out.println("Fehler bei Verbindungsaufbau mit RFCOMM");
            ex.printStackTrace();
            return;
        }
        try {
            conn.sendMessage("TEST".trim());
            conn.close();
        } catch (IOException ex) {
            System.err.println("Senden mit RFCOMM fehlgeschlagen");
            ex.printStackTrace();
            return;
        }
     }
    

    public void handlePeer(String peerName, String serviceName, String addressString, int protocol, MessageStub stub) {
        this.string1.setText(this.string1.getText() + "\n" + "HandlePeer (L2Cap)");
        this.string1.setText(this.string1.getText() + "\n" + "Found:" + peerName + " " + serviceName + " " + addressString + " " + protocol);
        
        this.l2c.sendMessage("TEST", addressString);
    }
    
    /* Command Listener Methoden */
    public void commandAction(Command cmd, Displayable disp) {
        //if(disp==f){
            if(cmd==l2capCommand){
                 this.l2c = new BTL2CAPStub(this,this);
                 System.out.println("Nach dem stub geht es weiter ...");
                // if(!this.nearbyDevices.isEmpty()){
                     for(int i = 0; i < this.nearbyDevices.size(); i++){
                        System.out.println("Sende L2CAP Nachricht ..."); 
                        this.l2c.sendMessage("TEST", (String)this.nearbyDevices.elementAt(i));
                     }
                 //}
                 
            }
            if(cmd==rfcommCommand){
                this.rfc = new BTRFCOMMStub(this,this);
                System.out.println("Nach dem stub geht es weiter ...");
                StreamConnection conn = null;
                boolean active = true;
                 
                 //if(!this.nearbyDevices.isEmpty()){
                 

                 }
            //}
            if(cmd==exitCommand){
                this.notifyDestroyed();
      //  }
        
    }
    }

    /* RequestHandler-Methoden */
    public void handleMessage(String msg, MessageStub mStub) {
        this.string1.setText(this.string1.getText() + "\n" + msg.trim());
    }

    public void handleStream(StreamConnection con) {
        System.out.println("Erhalte Nachricht über RFCOMM ...");
        byte[] bytebuf = new byte[100];

        InputStream is = con.getInputStream();
        try {
            is.read(bytebuf);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        String nachricht = new String(bytebuf);
        nachricht.trim();
        this.string1.setText(this.string1.getText() + "\n" + nachricht + " (RFCOMM)");
                
    }
}
