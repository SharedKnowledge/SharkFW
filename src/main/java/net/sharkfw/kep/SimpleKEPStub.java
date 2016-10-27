package net.sharkfw.kep;

import net.sharkfw.asip.ASIPInterest;
import net.sharkfw.asip.ASIPKnowledge;
import net.sharkfw.asip.ASIPSpace;
import net.sharkfw.asip.SharkStub;
import net.sharkfw.asip.engine.serializer.AbstractSharkStub;
import net.sharkfw.knowledgeBase.*;
import net.sharkfw.knowledgeBase.inmemory.InMemoSharkKB;
import net.sharkfw.peer.ASIPPort;
import net.sharkfw.peer.KEPInMessage;
import net.sharkfw.peer.SharkEngine;
import net.sharkfw.ports.KnowledgePort;
import net.sharkfw.protocols.MessageStub;
import net.sharkfw.protocols.StreamConnection;
import net.sharkfw.system.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;

/**
 * Simple implementation of KEP-Protocol engine.
 *
 * @author thsc
 * @author mfi
 * @deprecated
 */

public class SimpleKEPStub extends AbstractSharkStub implements KEPStub {
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
        super(se);
    }

    /**
     * this message is called by underlying stream protocols
     * whenever a new Stream is established.
     *
     * @param con The received stream.
     */
    @Override
    public final void handleStream(StreamConnection con) {
        KEPSession session = new KEPSession(this.se, con, this);
        session.initSecurity(this.privateKey, /*this.publicKeyStorage,*/ this.sharkPkiStorage,
                this.encryptionLevel, this.signatureLevel,
                this.replyPolicy, this.refuseUnverifiably);
        session.start();
    }

    @Override
    public void handleStream(StreamConnection con, ASIPKnowledge knowledge) {
        throw new UnsupportedOperationException(
                "wrong usage of this class: don't wrap a Stream Stub with this class: "
                        + this.getClass().getName());
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

        // a communication
        this.startConversion(con);
    }

    @Override
    public final void handleMessage(byte[] msg, MessageStub stub) {
        // Use byte[] to avoid encoding issues. Encoding is job of the sending and receiving parties.
        L.d("KEPStub: message received: " + msg, this);
        try {
            KEPInMessage inMsg = new KEPInMessage(this.se, msg, stub);
            inMsg.initSecurity(this.privateKey, /*this.publicKeyStorage,*/ this.sharkPkiStorage,
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
    final synchronized public boolean callListener(KEPInMessage msg) {
        Iterator<ASIPPort> kpIter = this.getListener();
        /* make a copy of listener - kp can be added or withdrawn during message handling
         * which can cause strange side effects.
         */

        ArrayList<ASIPPort> kpList = new ArrayList<>();
        while (kpIter.hasNext()) {
            kpList.add(kpIter.next());
        }

        // iterate copied list now
        boolean handled = false;

        kpIter = kpList.iterator();
        while (kpIter.hasNext()) {
            ASIPPort l = kpIter.next();
//            TODO deactivated KEPStub
//          if (l.handleMessage(msg., null)) {
//            handled = true;
//          }
        }

        // do we have a final handler for not handled messages ?
        if (!handled) {
            if (this.notHandledRequestsHandler != null) {
//                  TODO deactivated KEPStub
//                handled = this.notHandledRequestsHandler.handleMessage(msg);
            } else {
                // remember unhandled message
                SharkCS interest = msg.getInterest();
                if (interest != null) {
                    this.rememberUnhandledInterest(interest);
                } else {
                    Knowledge knowledge;
                    try {
                        knowledge = msg.getKnowledge();
                        if (knowledge != null) {
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

    /*
     * Implementing KEPMessageAccounting interface
     */
    @Override
    public void sentInterest(SharkCS interest) {
        /* there are two list of kepInterest for two different purposes.
         * Will be revised soon
         */
        this.rememberInterest(interest);

        if (interest == null) {
            L.e("Can't add 'null' kepInterest to silence table!", this);
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

        if (knowledge == null) {
            L.e("Can't add 'null' knowledge to silence table!", this);
            return;
        }

        try {
            // Create a string representation to be used as a key
            KnowledgeSerializer ks = KEPMessage.getKnowledgeSerializer(KEPMessage.XML);

            SharkVocabulary context = knowledge.getVocabulary();
            if (context == null) {
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

            // Find out when the kepInterest has been sent last
            Long timestamp = null;
            timestamp = this.messages.get(key);

            if (timestamp == null) {
                // Interest is not inside the silence table. Allowed.
                return true;
            }

            Long currentTime = System.currentTimeMillis();
            Long delta = currentTime - timestamp;
            // Check if it is still inside the silence period. Return true.
            if (delta > this.silentPeriod) {
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
            if (context == null) {
                return true; // TODO
            }

            SharkCS cs = context.asSharkCS(); // using the context map

            String key = ks.serializeSharkCS(cs);

            // Find out when the knowledge has been sent last
            Long timestamp = null;
            timestamp = this.knowledges.get(key);

            if (timestamp == null) {
                L.d("Knowledge is not inside the silence table. Allowed.", this);
                return true;
            }

            Long currentTime = System.currentTimeMillis();
            Long delta = currentTime - timestamp;
            // Check if it is still inside the silence period. Return true.
            if (delta > this.silentPeriod) {
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


    /**
     * An integer value that contains the default silence period in which no message is allowed to be sent twice
     */
    protected int silentPeriod = SharkEngine.DEFAULT_SILTENT_PERIOD;

    public boolean handleMessage(KEPInMessage msg) {
        return this.callListener(msg);
    }

    private InterestStore sentInterests = new InterestStore();
    private KnowledgeStore sentKnowledge = new KnowledgeStore();

    private InterestStore unhandledInterests = new InterestStore();
    private KnowledgeStore unhandledKnowledge = new KnowledgeStore();

    /**
     * Remember that this kepInterest was send now
     *
     * @param interest
     */
    protected void rememberInterest(SharkCS interest) {
        this.sentInterests.addInterest(interest);
    }

    /**
     * Remember that this kepInterest was send now
     *
     * @param
     */
    protected void rememberKnowledge(Knowledge k) {
        this.sentKnowledge.addKnowledge(k);
    }

    public Iterator<SharkCS> getSentInterests(long since) {
        return this.sentInterests.getInterests(since);
    }

    public Iterator<Knowledge> getSentKnowledge(long since) {
        return this.sentKnowledge.getKnowledge(since);
    }

    protected void rememberUnhandledInterest(SharkCS interest) {
        this.unhandledInterests.addInterest(interest);
    }

    protected void rememberUnhandledKnowledge(Knowledge knowledge) {
        // cut information to make it smaller
        if (knowledge != null) {
            Enumeration<ContextPoint> contextPoints = knowledge.contextPoints();
            if (contextPoints != null) {
                while (contextPoints.hasMoreElements()) {
                    ContextPoint cp = contextPoints.nextElement();

                    Enumeration<Information> infoEnum = cp.enumInformation();
                    if (infoEnum != null) {
                        while (infoEnum.hasMoreElements()) {
                            Information info = infoEnum.nextElement();
                            cp.removeInformation(info);
                        }
                    }
                }
            }

            // store cp without information - much smaller
            this.unhandledKnowledge.addKnowledge(knowledge);
        }
    }

    @Override
    public Iterator<SharkCS> getUnhandledInterests(long since) {
        return this.unhandledInterests.getInterests(since);
    }

    public Iterator<Knowledge> getUnhandledKnowledge(long since) {
        return this.unhandledKnowledge.getKnowledge(since);
    }

    public void removeSentHistory() {
        this.sentInterests = new InterestStore();
        this.sentKnowledge = new KnowledgeStore();

        this.unhandledInterests = new InterestStore();
        this.unhandledKnowledge = new KnowledgeStore();
    }

    @Override
    public void setSilentPeriod(int milliseconds) {
        if (milliseconds > 0) {
            this.silentPeriod = milliseconds;
        }
    }

    /**
     * Stream was established and can be used for conversion.
     * Something must happen. Best would be to call each knowledge
     * port with an all kepInterest.
     *
     * @param con
     */
    @Override
    public final void startConversion(StreamConnection con) {
        // creates an empty kepInterest - which is interpreted as any kepInterest.
        Interest anyInterest = InMemoSharkKB.createInMemoInterest();
        anyInterest.setDirection(ASIPSpace.DIRECTION_INOUT);

        Iterator<ASIPPort> kpIter = this.getListener();

        while (kpIter.hasNext()) {
            ASIPPort kp = kpIter.next();

            KEPInMessage internalMessage = new KEPInMessage(this.se,
                    KEPMessage.KEP_EXPOSE, anyInterest, con, this);

            this.handleMessage(internalMessage);
        }
    }

    @Override
    public void handleInterest(Interest interest) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void handleASIPInterest(ASIPInterest interest, SharkStub stub) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
