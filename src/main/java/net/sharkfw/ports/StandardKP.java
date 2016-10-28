package net.sharkfw.ports;

import net.sharkfw.asip.ASIPInterest;
import net.sharkfw.asip.ASIPKnowledge;
import net.sharkfw.asip.engine.ASIPConnection;
import net.sharkfw.asip.engine.ASIPInMessage;
import net.sharkfw.knowledgeBase.*;
import net.sharkfw.peer.SharkEngine;
import net.sharkfw.protocols.PeerAddress;
import net.sharkfw.system.L;

/**
 * Default implementation for
 * <p>
 * This KnowledgePort offers implemented standard behavior for handling expose and
 * insert messages, as well as implementations for extraction and assimilation
 * of {@link net.sharkfw.knowledgeBase.Knowledge}.
 * <p>
 * The standard KnowledgePort works with Standard {@link net.sharkfw.knowledgeBase.Interest} only, as it assumes,
 * that Interests have so called "anchor points" which are essential for the
 * implemented algorithms.
 * <p>
 * The KnowledgePort can be configured to use a relay. A relay is a designated
 * node, that receives all KEP traffic this peer is sending, despite other
 * recipients that may have been computed as REMOTEPEERs on the Interest or
 * Contextmap.
 * <p>
 * This KnowledgePort can also be configured to handle incoming Knowledge objects
 * in different ways. Upon reiceiving Knowledge which contains {@link net.sharkfw.knowledgeBase.Information}.
 * for tags, that were not part of the anchor points of this kp's Interest, the KowledgePort may either:
 * <ul>
 * <li> Check if the non-anchor-{@link net.sharkfw.knowledgeBase.SemanticTag}s are within range of the configured OTP and
 * extend its local vocabulary with those tags before adding the contextpoint to its kb</li>
 * <li> Check if a locally known tag can be found (using the OTP) which is
 * related to the unknown tag. If that is possible the coordinates of the said
 * will be changed to point to locally known tag instead of
 * pointing to the unknown tag</li>
 * </ul>
 * This behavior can be configured using <code>learnSTs(boolean learn)</code> method.
 *
 * @author thsc
 * @author mfi
 * @see #setRelayAddress(net.sharkfw.protocols.PeerAddress)
 * @see #unsetRelays()
 * @see #learnSTs(boolean)
 */
public class StandardKP extends KnowledgePort implements KnowledgeBaseListener {

    /**
     * Determine if the assimilation shall learn new tags or not.
     */
    private boolean learn = true;

    /**
     * A String containing address information for a relaying peer
     */
    private PeerAddress relayaddress = null;

    /**
     * Switch for auto-updating kepInterest of this knowledge port.
     */
    private boolean sync = false;

    /**
     * FragmentationParameter used for this KnowledgePort.
     */
    private FragmentationParameter[] fp;

    /**
     * Ontology-Transfer-Parameter used for this KnowledgePort.
     */
    private FragmentationParameter[] bgfp;

    /**
     * Whether or not delete assimiated context point from received knowledge.
     */
    private boolean deleteAssimilated = true;

    protected StandardKP(SharkEngine se, SharkKB kb) {
        super(se, kb);
    }

    public void deleteAssimilatedFromKnowledge(boolean delete) {
        this.deleteAssimilated = delete;
    }

//    /**
//     * <p>Initial check for correct IN/OUT dimension. Log information.
//     * Call the actual assimilation afterwards.</p>
//     *
//     * @param k A Knowledge object received from another peer
//     * @param response KEPResponse to create a response to this insert request
//     */
//    @Override
//    protected void handleInsert(Knowledge k, KEPConnection response) {
//          L.d("\n******************************************\n\t\tKP handleInsert\n******************************************\n", this);
//
//        if(!this.isIKP()) {
//            L.d("insert called but KP has no incomming kepInterest - don't do anything", this);
//            return;
//        }
//        /**
//         * Note:
//         * The OTP parameter must be choosen appropriate.
//         * That's especially for peers and groups.
//         *
//         * Usually, users will grant groups rights.
//         * Groups are usually unknown to remote peers. Thus,
//         * the matching must take place here.
//         *
//         * OTP should allow to follow super predicate with depth e.g.
//         * 2.
//         */
//        // Now all changes (if applicable) have been made. Standard procedure from now on.
//        SharkVocabulary context = k.getVocabulary();
//
//        // there must be a context
//        if(context == null) {
//            context = new InMemoSharkKB();
//        }
//
//        SharkCS background = context.asSharkCS();
//
//        L.d("handleInsert reached. Found content in request:\n ", this);
////        L.d(L.kbSpace2String(k.getBackgroundKnowledge()), this);
//
//        try {
//            L.d("handleInsert: local kepInterest:\n ", this);
//            L.d(L.contextSpace2String(this.getKEPInterest()), this);
//
//            this.notifyKnowledgeReceived(k);
//
////            // calculate effective kepInterest
//            SharkCS effectiveInterest;
//
//            effectiveInterest =
//                SharkCSAlgebra.contextualize(background,
//                this.getKEPInterest(), this.getFP());
//
//            // is there a mutual kepInterest ?
//            if(effectiveInterest == null) {
//                L.d("no mutual kepInterest", this);
//                return;
//            }
//
//            L.d("handleInsert: effective kepInterest:\n ", this);
//            L.d(L.contextSpace2String(effectiveInterest), this);
//
//			/* dead code removed */
//            // assimilate this knowledge
//            ArrayList<ContextCoordinates> assimilatedCC =
//                    SharkCSAlgebra.assimilate(this.getKB(), effectiveInterest,
//                                            this.getFP(), k, this.learn,
//                                            this.deleteAssimilated);
//
//            L.d("handleInsert: knowledge base after assimilation:\n " +
//                    L.kb2String(this.getKB()), this);
//
//            // notify
//            if(assimilatedCC != null) {
//                Iterator<ContextCoordinates> ccIter = assimilatedCC.iterator();
//                while(ccIter.hasNext()) {
//                    ContextCoordinates cc = ccIter.next();
//                    ContextPoint cp = this.getKB().getContextPoint(cc);
//                    if(cp != null) {
//                        this.notifyKnowledgeAssimilated(this, cp);
//                    }
//                }
//            }
//
//        }
//        catch(SharkKBException e) {
//            L.d("assimilation failed: " + e.getMessage(), this);
//        }
//    }

//    /**
//     * <p>Check whether to answer with insert or expose.<br />
//     * If answering with insert call extraction to extract {@link Knowledge} from the local KB.
//     * In case the extraction was successfull, send the resulting Knowledge back.</p>
//     *
//     * If answering with expose, send the contextualized kepInterest back.
//     *
//     * @param receivedInterest The SimpleInterest received by another peer
//     * @param response
////     */
//    @Override
//    protected void handleExpose(SharkCS receivedInterest, KEPConnection response) {
//          L.d("\n******************************************\n\t\tKP handleExpose\n******************************************\n", this);
//
//      try {
//          // an kepInterest has been retrieved from remote peer
//          L.d("handleExpose: \n receivedKEPInterest kepInterest is:\n"+ L.contextSpace2String(receivedInterest), this);
//          L.d("handleExpose: \n my Interest kepInterest is:\n"+ L.contextSpace2String(this.getKEPInterest()), this);
//
//          // check if internals would be revealed which isn't allowed.
//          if(!this.revealingAndAllowed(receivedInterest, this.getKEPInterest())) {
//              L.d("stop executing handleExpose: received kepInterest contains "
//                      + "unspecified (any) dimension which are defined in "
//                      + "local kepInterest - revealing of details "
//                      + "not permitted", this);
//              return;
//          }
//
//          // refresh kepInterest
//          SharkCS localInterest = this.getKEPInterest();
//
//          /*
//            * Remote kepInterest has passed the door. Now we enrich the
//            * kepInterest with background knowledge - to teach remote
//            * peer. Thus, local kb becomes source, mutualInterest becomes context.
//            *
//            * Result can be more general, larger, than the local kepInterest.
//          */
//          SharkCS effectiveInterest = this.getKB().contextualize(localInterest, this.getOTP());
//
//          // check it with the guarding kepInterest first
//          // local kepInterest is context, retrieved is source
////          SharkCS mutualInterest = SharkCSAlgebra.contextualize(
////                  receivedKEPInterest, this.getKEPInterest(), this.getFP());
//
//          Interest mutualInterest = SharkCSAlgebra.contextualize(
//                  receivedInterest, effectiveInterest, this.getFP());
//
//          if(mutualInterest == null) {
//              L.d("no mutual kepInterest - knowledge port stops executing", this);
//              return;
//          }
//
//          L.d("handleExpose: \n mutual kepInterest is:\n"+ L.contextSpace2String(mutualInterest), this);
//
//          int effectiveDirection = mutualInterest.getDirection();
//
//          /////////////////////////////////////////////////////////////////
//          //                    expose mutual kepInterest                   //
//          /////////////////////////////////////////////////////////////////
//
//          // remote peer wants to send something?
//          if(effectiveDirection == ASIPSpace.DIRECTION_INOUT ||
//                  effectiveDirection == ASIPSpace.DIRECTION_IN) {
//            // Effective kepInterest = receiving kepInterest. Send response.
//            L.d("Answering with expose", this);
//
//            response.expose(mutualInterest);
//            this.notifyExposeSent(this, mutualInterest);
//          }
//
//          /* Note: Don't change the order of expose and send.
//           * We are ging to manipulate mutual kepInterest in the next few lines
//          */
//
//          /////////////////////////////////////////////////////////////////
//          //                       send knowledge                        //
//          /////////////////////////////////////////////////////////////////
//
//          // remote peer wants to get something?
//          if(effectiveDirection == ASIPSpace.DIRECTION_INOUT ||
//                  effectiveDirection == ASIPSpace.DIRECTION_OUT) {
//
////              Interest extractionInterest = SharkCSAlgebra.contextualize(
////                  this.getKB().asSharkCS(), mutualInterest, this.getOTP());
//
////              Interest extractionInterest = InMemoSharkKB.createInMemoCopy(mutualInterest);
////              L.d("handleExpose: \n extraction kepInterest is:\n"+ L.contextSpace2String(extractionInterest), this);
//
//              // set direction: we take all cps that are explicitely set to out
////              extractionInterest.setDirection(ASIPSpace.DIRECTION_INOUT);
//
//              mutualInterest.setDirection(ASIPSpace.DIRECTION_INOUT);
//
//              L.d("handleExpose: \n extraction kepInterest is:\n"+ L.contextSpace2String(mutualInterest), this);
//
//            // Effective kepInterest = sending kepInterest. Extract knowledge.
//            InMemoSharkKB tempKB = new InMemoSharkKB();
//
//            Knowledge k = SharkCSAlgebra.extract(tempKB,
//                    this.getKB(), mutualInterest,
//                    this.getFP(), true);
//
//            if(k != null) {
//                L.d("extracted non-empty knowledge", this);
//
//                // send it back
//                response.insert(k, (String) null);
//                this.notifyInsertSent(this, k);
//            } else {
//                L.d("no knowledge found with those extraction cs", this);
//            }
//          }
//
//      } catch (SharkException ex) {
//          L.e(ex.getMessage(), this);
//      }
//    }

    private boolean revealLocalInterest = false;

    /**
     * StandardKP has a local kepInterest. It is used as context when an kepInterest
     * arrives from another peer. Following situation can arise:
     * <p>
     * Local kepInterest can contain e.g. topics. Reveived kepInterest might not specify
     * a topic at all. Usually, the mutual kepInterest would contain the context
     * topics. Thus, it would reveal definitions made locally with its local kepInterest.
     * <p>
     * This behaviour works in any dimension. In consequence, a fully
     * unconstraint receiving kepInterest would trigger this StandardKP to reply
     * it local interests. This default behaviour can be switched of by defining
     * reveal to false.
     *
     * @param reveal True: reveal details of local kepInterest if received
     *               kepInterest has unspecified dimensions. False: Don't reply to interests which
     *               have an unspecified dimension where the local dimension is specified.
     *               Default: false
     *               <p>
     *               (Redo in version 3.0)
     */
    public void setRevealLocalInterest(boolean reveal) {
        this.revealLocalInterest = reveal;
    }


    /**
     * Set the Ontology-Transfer-Parameter for this KnowledgePort.
     *
     * @param otp An array of FragmentationParameter. One for each dimension.
     */
    public void setOtp(FragmentationParameter otp[]) {
        this.bgfp = otp;

    }

    /**
     * Set the fragmentation parameter that has been used to create the kepInterest of the KnowledgePort.
     *
     * @param fp An Array of FragmentationParameter. One for each dimension.
     */
    public void setFP(FragmentationParameter fp[]) {
        this.fp = fp;
    }

    /**
     * Return the fragmenatation parameter that has been used to create the {@link net.sharkfw.knowledgeBase.Interest} of this KnowledgePort.
     *
     * @return An array of <code>FragmentationParameter</code>. One for each dimension.
     */
    public FragmentationParameter[] getFP() {
        return this.fp;
    }

    /**
     * Return the Ontology-Transfer-Parameter from this KowledgePort.
     *
     * @return An array of <code>FragmentationParameter</code>. One for each dimension.
     */
    public FragmentationParameter[] getOTP() {
        return this.bgfp;
    }


    /**
     * <p>Make the Knowledge Port listen for changes from the {@link net.sharkfw.knowledgeBase.SharkKB}.
     * Upon each received change, the KnowledgePort will call its
     * <code>refreshInterest()</code> method to update the kepInterest.</p>
     * <p>
     * <p>Thus the KP needs not be updated manually but will keep in syncObsolete with
     * the knowledgebase automatically.</p>
     *
     * @param sync <code>true</code> to start syncing, <code>false</code> to stop syncing.
     */
    public void keepInterestInSyncWithKB(boolean sync) {
        this.sync = sync;
    }

    /**
     * <p>Two different strategies can be followed when assimilating knowledge:
     * <ul>
     * <li> Learn new STs if your OTP allows it and thus extend the local STSets </li>
     * <li> Don't learn new tags but rather find the tags 'closest' to the unknown ones, in the local kb and save received contextpoints to the locally known tags.</li>
     * </ul>
     * </p>
     * <p>
     * <p>If you set learn to <code>true</code> strategy 1 will apply, if you set learn to <code>false</code> strategy 2 will apply.
     * The default strategy is: 1.</p>
     *
     * @param learn Either <code>true</code> for strategy one, or <code>false</code> for strategy 2.
     */
    public void learnSTs(boolean learn) {
        this.learn = learn;
    }

    /**
     * <p>Set an address to a relaying peer here. Every response to received messages will be sent to the relay.
     * The contents of the message will not be inspected for any address details.</p>
     *
     * @param relayaddress A string complying to the shark addressing scheme with the address of the relaying peer.
     */
    public void setRelayAddress(PeerAddress relayaddress) {
        this.relayaddress = relayaddress;
    }

    /**
     * <p>Remove the relay address if one has been set. Address information will be taken from the message (Interest or Knowledge) itself.
     * If no relay address has been set, nothing will happen.</p>
     */
    public void unsetRelays() {
        this.relayaddress = null;
    }

    @Override
    public void topicAdded(SemanticTag tag) {
        this.syncInterest();
    }

    @Override
    public void peerAdded(PeerSemanticTag tag) {
        this.syncInterest();
    }

    @Override
    public void locationAdded(SpatialSemanticTag tag) {
        this.syncInterest();
    }

    @Override
    public void timespanAdded(TimeSemanticTag time) {
        this.syncInterest();
    }

    @Override
    public void topicRemoved(SemanticTag tag) {
        this.syncInterest();
    }

    @Override
    public void peerRemoved(PeerSemanticTag tag) {
        this.syncInterest();
    }

    @Override
    public void locationRemoved(SpatialSemanticTag tag) {
        this.syncInterest();
    }

    @Override
    public void timespanRemoved(TimeSemanticTag tag) {
        this.syncInterest();
    }

    @Override
    public void predicateCreated(SNSemanticTag subject, String type, SNSemanticTag object) {
        this.syncInterest();
    }

    @Override
    public void predicateRemoved(SNSemanticTag subject, String type, SNSemanticTag object) {
        this.syncInterest();
    }

    /**
     * Refresh interests, if the user switched syncing on. Do nothing otherwise.
     */
    private void syncInterest() {
        if (this.sync) {
            // TODO sync
        }
    }

    @Override
    public void tagChanged(SemanticTag tag) {
        this.syncInterest();
    }

    @Override
    protected void handleInsert(ASIPInMessage message, ASIPConnection asipConnection, ASIPKnowledge asipKnowledge) {
        // Do things
    }

    @Override
    protected void handleExpose(ASIPInMessage message, ASIPConnection asipConnection, ASIPInterest interest) throws SharkKBException {
        // Do things
    }
}
