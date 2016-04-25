package net.sharkfw.asip.engine;

import java.io.IOException;
import java.util.*;

import net.sharkfw.asip.*;
import net.sharkfw.kep.AbstractSharkStub;
import net.sharkfw.kep.KEPMessage;
import net.sharkfw.knowledgeBase.*;
import net.sharkfw.knowledgeBase.inmemory.InMemoSharkKB;
import net.sharkfw.peer.KEPInMessage;
import net.sharkfw.peer.KnowledgePort;
import net.sharkfw.peer.SharkEngine;
import net.sharkfw.protocols.MessageStub;
import net.sharkfw.protocols.StreamConnection;
import net.sharkfw.system.*;

/**
 * Created by msc on 18.03.16.
 */
public class SimpleASIPStub extends AbstractSharkStub implements ASIPStub {
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
    public SimpleASIPStub(SharkEngine se) {
        super(se);
    }

    @Override
    public void handleMessage(byte[] msg, MessageStub stub) {
        // TODO implement MessageStub
        L.d("ASIPStub: message with length of " + msg.length + " bytes received: " + Arrays.toString(msg), this);
        try {
            ASIPInMessage inMsg = new ASIPInMessage(this.se, msg, stub);
            inMsg.initSecurity(this.privateKey, /*this.publicKeyStorage,*/ this.sharkPkiStorage,
                    this.encryptionLevel, this.signatureLevel,
                    this.replyPolicy, this.refuseUnverifiably);
            inMsg.parse();
            this.callListener(inMsg);
        } catch (IOException ioe) {
            L.e("IOException while reading ASIP message: " + ioe.getMessage(), this);
            ioe.printStackTrace();
        } catch (SharkSecurityException ioe) {
            // connection closed - bye
            L.d("Security Exception", this);
        }
    }

    /**
     * this message is called by underlying stream protocols
     * whenever a new Stream is established.
     *
     * @param con The received stream.
     */
    @Override
    public final void handleStream(StreamConnection con) {
        ASIPSession session = new ASIPSession(this.se, con, this);
        session.initSecurity(this.privateKey, /*this.publicKeyStorage,*/ this.sharkPkiStorage,
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

        // a communication
        this.startConversion(con);
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
    final synchronized public boolean callListener(ASIPInMessage msg) {
        Iterator<KnowledgePort> kpIter = this.getListener();
        /* make a copy of listener - kp can be added or withdrawn during message handling
         * which can cause strange side effects.
         */

        ArrayList<KnowledgePort> kpList = new ArrayList<>();
        while (kpIter.hasNext()) {
            kpList.add(kpIter.next());
        }

        // iterate copied list now
        boolean handled = false;

        kpIter = kpList.iterator();
        while (kpIter.hasNext()) {
            KnowledgePort l = kpIter.next();
            handled = l.handleMessage(msg, msg.getConnection());
        }

//        msg.finished();

        //    L.d("Having " + this.listener.size() + " listeners.", this);

        return handled;
    }

    public final void setNotHandledRequestKP(KnowledgePort kp) {
        this.notHandledRequestsHandler = kp;
    }

    public final void resetNotHandledRequestKP() {
        this.notHandledRequestsHandler = null;
    }

    /**
     * An integer value that contains the default silence period in which no message is allowed to be sent twice
     */
    protected int silentPeriod = SharkEngine.DEFAULT_SILTENT_PERIOD;

    public boolean handleMessage(ASIPInMessage msg) {
        return this.callListener(msg);
    }

    private ASIPInterestStore sentInterests = new ASIPInterestStore();
    private ASIPKnowledgeStore sentKnowledge = new ASIPKnowledgeStore();

    private ASIPInterestStore unhandledInterests = new ASIPInterestStore();
    private ASIPKnowledgeStore unhandledKnowledge = new ASIPKnowledgeStore();

    /**
     * Remember that this kepInterest was send now
     *
     * @param interest
     */
    protected void rememberInterest(ASIPInterest interest) {
        this.sentInterests.addInterest(interest);
    }

    protected void rememberKnowledge(ASIPKnowledge k) {
        this.sentKnowledge.addKnowledge(k);
    }

    public Iterator<ASIPInterest> getSentInterests(long since) {
        return this.sentInterests.getInterests(since);
    }

    public Iterator<ASIPKnowledge> getSentKnowledge(long since) {
        return this.sentKnowledge.getKnowledge(since);
    }

    protected void rememberUnhandledInterest(ASIPInterest interest) {
        this.unhandledInterests.addInterest(interest);
    }

    protected void rememberUnhandledKnowledge(ASIPKnowledge knowledge) {
        // cut information to make it smaller
        // FIXME store smaller knowledge
//        if(knowledge != null) {
//            Enumeration<ContextPoint> contextPoints = knowledge.contextPoints();
//            if(contextPoints != null) {
//                while(contextPoints.hasMoreElements()) {
//                    ContextPoint cp = contextPoints.nextElement();
//
//                    Enumeration<Information> infoEnum = cp.enumInformation();
//                    if(infoEnum != null) {
//                        while(infoEnum.hasMoreElements()) {
//                            Information info = infoEnum.nextElement();
//                            cp.removeInformation(info);
//                        }
//                    }
//                }
//            }
//
//            // store cp without information - much smaller
//            this.unhandledKnowledge.addKnowledge(knowledge);
//        }
        if (knowledge != null) {
            this.unhandledKnowledge.addKnowledge(knowledge);
        }
    }

    public Iterator<ASIPInterest> getUnhandledInterests(long since) {
        return this.unhandledInterests.getInterests(since);
    }

    public Iterator<ASIPKnowledge> getUnhandledKnowledge(long since) {
        return this.unhandledKnowledge.getKnowledge(since);
    }

    public void removeSentHistory() {
        this.sentInterests = new ASIPInterestStore();
        this.sentKnowledge = new ASIPKnowledgeStore();

        this.unhandledInterests = new ASIPInterestStore();
        this.unhandledKnowledge = new ASIPKnowledgeStore();
    }

//  @Override
//    public void setSilentPeriod(int milliseconds) {
//        if(milliseconds > 0) {
//            this.silentPeriod = milliseconds;
//        }
//    }
//

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
        anyInterest.setDirection(SharkCS.DIRECTION_INOUT);

        Iterator<KnowledgePort> kpIter = this.getListener();

        while (kpIter.hasNext()) {
            KnowledgePort kp = kpIter.next();

            ASIPInMessage internalMessage = new ASIPInMessage(this.se,
                    KEPMessage.KEP_EXPOSE, anyInterest, con, this);

            this.handleMessage(internalMessage);
        }
    }

    @Override
    public void handleInterest(Interest interest) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
