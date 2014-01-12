/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.sharkfw.protocols.bt;

import java.io.IOException;
import java.util.Vector;
import javax.bluetooth.L2CAPConnection;
import javax.microedition.io.Connector;

/**
 *
 * This class provides an interface to message queue which is being served by Thread to send messages.
 * This class accepts a message, and delivers it to the queue from which it is being sent.
 * @author mfi
 */
public class BTL2CAPClient {

    private SenderThread sender;

    
    private class SenderThread extends Thread{
        /**
         * Sender Thread maintains a queue in which messages are put to be sent asap
         */
        
        private Vector queue = new Vector();

        private boolean active = false;

        /**
         *
         * @param message
         * @param address
         */
        public void addMessage(String message, String address){
            System.out.println("Adding message ... ");
            if(this.queue == null){
                this.queue = new Vector();
            }
            this.queue.addElement(new Message(message,address));
        }

        /**
         *
         */
        public void go(){
            this.active = true;
        }

        /**
         *
         */
        public void hold(){
            this.active = false;
        }

        /**
         * 
         */
        public void run(){
            this.active = true;

            if(this.queue == null){
                this.queue = new Vector();
            }
            while(this.active){
                System.out.println("SenderThread running ...");

                System.out.println("Queuesize: " + this.queue.toString());
                
                while(this.queue.size() > 0 ){
                    System.out.println("Queue is not empty ..");
                    Message msg = (Message) this.queue.firstElement();

                    try {
                        System.out.println("Sending message in SenderThread ...");
                        // BT Stuff here
                        L2CAPConnection connection = (L2CAPConnection) Connector.open(msg.getAddress());

                        String content = msg.getMessage();
                        int len = content.length();
                        String lengthString = String.valueOf(len);

                        int txMtu = connection.getTransmitMTU(); // might be used in the future to fragment packets to the mtu size
                        connection.send(lengthString.getBytes()); // maybe supply encoding here
                        connection.send(content.getBytes());

                        connection.close();

                        // after succesfully submitting the message it is to be removed from the queue
                        this.queue.removeElement(msg);
                        
                    } catch (IOException ex) {
                        // What to do here?
                        ex.printStackTrace();
                    } finally {

                        // sleep a second
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException ex) {
                            ex.printStackTrace();
                        }
                    }
                }
            }
        }
        
    }

    /**
     * This class represents a message which is to be sent consisting of the message itself and the recipient
     */
    private class Message{

        private String address, message;

        /**
         *
         * @param message
         * @param address
         */
        public Message(String message, String address){
            this.address = address;
            this.message = message;
        }

        /**
         *
         * @return
         */
        public String getAddress(){
            return this.address;
        }

        /**
         *
         * @return
         */
        public String getMessage(){
            return this.message;
        }
    }

    /**
     *
     * @param message
     * @param address
     * @throws java.io.IOException
     */
    public void sendMessage(String message, String address) throws IOException{

        /* late init */
        if(this.sender == null){
            this.sender = new SenderThread();
            this.sender.start();
        }

        System.out.println("Client: sendMessage()");
        // that's it :) simply pass the parameters to the Thread and let it do its work
        this.sender.addMessage(message, address);


    }

    public void hold(){
        this.sender.hold();
    }

}
