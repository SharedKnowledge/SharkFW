package net.sharkfw.kep;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;
import net.sharkfw.knowledgeBase.SharkVocabulary;
import net.sharkfw.knowledgeBase.Knowledge;
import net.sharkfw.knowledgeBase.PeerSemanticTag;
import net.sharkfw.knowledgeBase.SharkCS;
import net.sharkfw.knowledgeBase.SharkKB;
import net.sharkfw.knowledgeBase.SharkKBException;
import net.sharkfw.peer.KEPInMessage;
import net.sharkfw.peer.KnowledgePort;
import net.sharkfw.peer.SharkEngine;
import net.sharkfw.protocols.MessageStub;
import net.sharkfw.protocols.StreamConnection;
import net.sharkfw.system.L;
import net.sharkfw.system.SharkNotSupportedException;
import net.sharkfw.system.SharkSecurityException;
import net.sharkfw.system.Util;

/**
 * Simple implementation of KEP-Protocol engine.
 * 
 * @author thsc
 * @author mfi
 */

public class SimpleKEPStub extends KEPStub {
  /**
   * A <code>Vector</code> containing all active KPs
   */
	private Vector<KnowledgePort> listener;
        
        private KnowledgePort notHandledRequestsHandler;

  /**
   * The instance of the <code>SharkEngine</code> of this peer.
   */
	private SharkEngine se;

  /**
   * A table containing the connection pool of this <code>KEPStub</code>
   */
  private Hashtable<String, StreamConnection> table;

  /**
   * The table that stores all messages' contextspaces in their serialized form plus the timestamp when they've been sent.
   */
  private Hashtable<String, Long> messages = new Hashtable<String, Long>();


  /**
   * This table stores all knowledges' contextspaces in their serialized form plus the timestamp when they've been sent.
   */
  private Hashtable<String, Long> knowledges = new Hashtable<String, Long>();
  /**
   * Create a new <code>SimpleKEPStub</code> for the <code>SharkEngine</code> se.
   *
   * @param se The <code>SharkEngine</code> for which a new <code>SimpleKEPStub</code> is instantiated.
   */
	public SimpleKEPStub(SharkEngine se) {
		this.listener = new Vector<KnowledgePort>();
		this.se = se;
                this.table = new Hashtable<String, StreamConnection>();
	}

	/**
	 * this message is called by underlying stream protocols 
         * whenever a new Stream is established.
	 * 
	 * @param is The received stream.
	 */
    @Override
	public final void handleStream(StreamConnection con) {
		KEPSession session = new KEPSession(this.se, con, this);
                session.initSecurity(this.privateKey, this.publicKeyStorage,
                                this.encryptionLevel, this.signatureLevel,
                                this.replyPolicy, this.refuseUnverifiably);
                session.start();
                
	}

    /**
     * This message is to be called when a new connection was establised e.g.
     * in a spontaneous network and this peer shall try to start KEP message
     * exchange.
     * 
     * @param con 
     */
    @Override
    public void handleNewConnectionStream(StreamConnection con) {
        // handle that stream in stub
        this.handleStream(con);
        
        // force engine to start a communication
        this.se.handleConnection(con);
    }
        
    @Override
	public final void handleMessage(byte[] msg, MessageStub stub) {
    // Use byte[] to avoid encoding issues. Encoding is job of the sending and receiving parties.
		L.d("KEPStub: message received: " + msg, this);
		try {
			KEPInMessage inMsg = new KEPInMessage(this.se, msg, stub);
                        inMsg.initSecurity(this.privateKey, this.publicKeyStorage,
                                this.encryptionLevel, this.signatureLevel,
                                this.replyPolicy, this.refuseUnverifiably);
                        inMsg.parse();
			this.callListener(inMsg);
		} catch (SharkNotSupportedException e) {
			L.e("unsupported KEP format: " + e.getMessage(), this);
		} catch (IOException ioe) {
			L.e("IOException while reading KEP message: " + ioe.getMessage(), this);
			ioe.printStackTrace();
		} catch (SharkSecurityException ioe) {
                    // connection closed - bye
                    L.d("Security Exception", this);
                } catch (SharkKBException ioe) {
                    // connection closed - bye
                    L.d("SharkKB Exception", this);
                }
	}
    
  /**
   * Central method in which all listeners are called
   * This should be the only method in this class which
   * communicates with the listener and the Shark Engine
   *
   * @param msg The <code>KEPRequest</code> to handle.
   * @return True if at least one listener was able to handle the message. False otherwise.
  */
    @Override
    final synchronized protected boolean callListener(KEPInMessage msg) {
        Enumeration<KnowledgePort> lenum = listener.elements();
        /* make a copy of listener - kp can be added or withdrawn during message handling
         * which can cause strange side effects.
         */
        
        ArrayList<KnowledgePort> kpList = new ArrayList<KnowledgePort>();
        while(lenum.hasMoreElements()) {
            kpList.add(lenum.nextElement());
        }
        
        // iterate kp now
        boolean handled = false;
        
        Iterator<KnowledgePort> kpIter = kpList.iterator();
        while (kpIter.hasNext()) {
          KnowledgePort l = kpIter.next();
          if (l.handleMessage(msg)) {
            handled = true;
          }
        }
        
        // do we have a final handler for not handled messages ?
        if(!handled) {
            if(this.notHandledRequestsHandler != null) {
                handled = this.notHandledRequestsHandler.handleMessage(msg);
            }
            else {
                // remember unhandled message
                SharkCS interest = msg.getInterest();
                if(interest != null) {
                    this.rememberUnhandledInterest(interest);
                } else {
                    Knowledge knowledge;
                    try {
                        knowledge = msg.getKnowledge();
                        if(knowledge != null) {
                            this.rememberUnhandledKnowledge(knowledge);
                        }
                    } catch (IOException ex) {
                        // ignore
                    } catch (SharkKBException ex) {
                        // ignore
                    }
                }
            }
        }

        // that it - bye
        msg.finished();

        //    L.d("Having " + this.listener.size() + " listeners.", this);

        return handled;
    }
    
    public final void setNotHandledRequestKP(KnowledgePort kp) {
        this.notHandledRequestsHandler = kp;
    }

    public final void resetNotHandledRequestKP() {
        this.notHandledRequestsHandler = null;
    }

    @Override
    public final void addListener(KnowledgePort newListener) {
        final int size = this.listener.size();
            for (int i = 0; i < size; i++){
                    if (this.listener.elementAt(i).equals(newListener)){
                            return;
            }
        }
        this.listener.add(newListener);


//        L.d("Listener added.", this);
//        L.d("Having " + this.listener.size() + " listeners.", this);
        };

    @Override
    public final void withdrawListener(KnowledgePort listener) {
//        L.d("Listener withdrawn.", this);
//        L.d("Having " + this.listener.size() + " listeners.", this);
        this.listener.removeElement(listener);
    };


  /*
   * Prototypical implementation of connection pool for StreamConnections.
   * Does not work as expected yet.
   */
  
  /**
   * Reset the connection pool
   */
    @Override
  public void clear() {
    L.d("Clearing connection pool.", this);
    this.table.clear();
  }

    @Override
  public StreamConnection getConnectionByAddress(String address) {
    /*
     * Todo: maybe update the Timestamp of this connection
     */
    return (StreamConnection) this.table.get(address);
  }

    @Override
  public void addConnection(String address, StreamConnection connection) {
    address = Util.resolveDNtoIPinGCFString(address);
    this.table.put(address, connection);
    L.d("Adding connection with address: " + address + " to accounting.", this);
  }

  /**
   * Return a <code>StreamConnection</code> that has been stored to beong to a certain address.
   * 
   * @param tag A Tag representing a communication partner
   * @return A <code>StreamConnection</code> to that peer.
   */
    @Override
  public StreamConnection getConnectionByTag(PeerSemanticTag tag) {
    L.d("Trying to find connection to: " + tag.getName(), this);
    String[] addresses = tag.getAddresses();
    if(addresses != null) {
      for(int i = 0; i < addresses.length; i++) {
        StreamConnection con = this.getConnectionByAddress(addresses[i]);
        if(con != null) {
          return con;
        }
      }
    }
    return null;
  }

    @Override
  public void removeStreamConnection(StreamConnection con) {
    if(this.table != null) {
      // Check if the connection is in the table at all
      Enumeration<String> keys = this.table.keys();
      // check each key's value
      while(keys != null && keys.hasMoreElements()) {
        String key = (String) keys.nextElement();
        //ROPeerSemanticTag key = (ROPeerSemanticTag) keys.nextElement();
        StreamConnection storedCon = (StreamConnection) this.table.get(key);
        if(con.equals(storedCon)) {
          this.table.remove(key);
        }
      }
    }
  }

    @Override
  public Enumeration<String> getConnectedAddresses() {
    if(this.table != null) {
      return table.keys();

    } else {
      Vector<String> v = new Vector<String>();
      return v.elements();
    }
  }

    @Override
  public void setSilentPeriod(int millis) {
    this.silentPeriod = millis;
  }

  /*
   * Implementing KEPMessageAccounting interface
   */
    @Override
  public void sentInterest(SharkCS interest) {
        /* there are two list of interest for two different purposes.
         * Will be revised soon
         */
        this.rememberInterest(interest);
        
    if(interest == null) {
      L.e("Can't add 'null' interest to silence table!", this);
      return;
    }

    try {
      // Create a string representation to be used as a key
      KnowledgeSerializer ks = KEPMessage.getKnowledgeSerializer(KEPMessage.XML);
      String key = ks.serializeSharkCS(interest);
      // Generate timestamp
      Long timestamp = System.currentTimeMillis();

      // Save key along with timestamp
      this.messages.put(key, timestamp);

    } catch (SharkKBException ex) {
      L.e("Exception while serializing context space:", this);
      ex.printStackTrace();
    } catch (SharkNotSupportedException ex) {
      L.e("Exception while serializing context space:", this);
      ex.printStackTrace();
    }
  }

        @Override
  public void sentKnowledge(Knowledge knowledge) {
        /* there are two list of knowledge for two different purposes.
         * Will be revised soon
         */
        this.rememberKnowledge(knowledge);
        
     if(knowledge == null) {
      L.e("Can't add 'null' knowledge to silence table!", this);
      return;
    }

    try {
      // Create a string representation to be used as a key
      KnowledgeSerializer ks = KEPMessage.getKnowledgeSerializer(KEPMessage.XML);
      
      SharkVocabulary context = knowledge.getVocabulary();
      if(context == null) {
          return;
      }
      
      SharkCS cs = context.asSharkCS();
      String key = ks.serializeSharkCS(cs);
      // Generate timestamp
      Long timestamp = System.currentTimeMillis();

      // Save key along with timestamp
      this.knowledges.put(key, timestamp);

    } catch (SharkKBException ex) {
      L.e("Exception while serializing context space:", this);
      ex.printStackTrace();
    } catch (SharkNotSupportedException ex) {
      L.e("Exception while serializing context space:", this);
      ex.printStackTrace();
    }
  }

        @Override
  public boolean interestAllowed(SharkCS interest) {

    if (interest == null) {
      return false;
    }

    try {
      // Generate a serialized representation to be used as a key in the table.
      KnowledgeSerializer ks = KEPMessage.getKnowledgeSerializer(KEPMessage.XML);
      String key = ks.serializeSharkCS(interest);

      // Find out when the interest has been sent last
      Long timestamp = null;
      timestamp = this.messages.get(key);

      if(timestamp == null) {
        // Interest is not inside the silence table. Allowed.
        return true;
      }

      Long currentTime = System.currentTimeMillis();
      Long delta = currentTime - timestamp;
      // Check if it is still inside the silence period. Return true.
      if(delta > this.silentPeriod) {
        // It's not. Remove it from the table.
        this.messages.remove(key);
        L.d("Interest is allowed. Silence period is over.", this);
        return true;
      } else {
        // It is INSIDE the silence period. Return false.
        L.l("Interest is inside silence period. Interest won't be sent.", this);
        return false;
//        L.d("Silence feature was switched OFF, though", this);
//        return true;
      }

    } catch (SharkKBException ex) {
      L.e("Exceptionin KB while checking message for allowance in message accountine", this);
    } catch (SharkNotSupportedException ex) {
      L.e("Exception while checking message for allowance in message accounting", this);
    }

    // If we can't find out if the message is allowed for some reasons we send it.
    return true;
  }

        @Override
  public boolean knowledgeAllowed(Knowledge knowledge) {
    if (knowledge == null) {
      return false;
    }

    try {
      // Generate a serialized representation to be used as a key in the table.
      KnowledgeSerializer ks = KEPMessage.getKnowledgeSerializer(KEPMessage.XML);
      
      SharkVocabulary context = knowledge.getVocabulary();
      if(context == null) {
          return true; // TODO 
      } 
      
      SharkCS cs = context.asSharkCS(); // using the context map
      
      String key = ks.serializeSharkCS(cs);

      // Find out when the knowledge has been sent last
      Long timestamp = null;
      timestamp = this.knowledges.get(key);

      if(timestamp == null) {
        L.d("Knowledge is not inside the silence table. Allowed.", this);
        return true;
      }

      Long currentTime = System.currentTimeMillis();
      Long delta = currentTime - timestamp;
      // Check if it is still inside the silence period. Return true.
      if(delta > this.silentPeriod) {
        // It's not. Remove it from the table.
        this.knowledges.remove(key);
        L.d("Knowledge is allowed. Silence period is over.", this);
        return true;
      } else {
        // It is INSIDE the silence period. Return false.
        L.d("Knowledge is inside silence period. Won't be sent.", this);
        return false;
      }

    } catch (SharkKBException ex) {
      L.e("Exceptionin KB while checking message for allowance in message accountine", this);
    } catch (SharkNotSupportedException ex) {
      L.e("Exception while checking message for allowance in message accounting", this);
    }

    // If we can't find out if the message is allowed for some reasons we send it.
    return true;
  }
}
