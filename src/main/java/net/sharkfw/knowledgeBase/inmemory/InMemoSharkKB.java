package net.sharkfw.knowledgeBase.inmemory;

import net.sharkfw.asip.ASIPInformation;
import net.sharkfw.asip.ASIPInformationSpace;
import net.sharkfw.asip.ASIPInterest;
import net.sharkfw.asip.ASIPSpace;
import net.sharkfw.knowledgeBase.*;
import net.sharkfw.knowledgeBase.geom.SharkGeometry;
import net.sharkfw.knowledgeBase.geom.inmemory.InMemoSharkGeometry;
import net.sharkfw.system.L;
import net.sharkfw.system.Util;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * @author mfi
 * @author thsc
 */
public class InMemoSharkKB extends AbstractSharkKB implements SharkKB, SystemPropertyHolder {

    ////////////////////////////////////////////////////////////////////////
    //                     in memo copies                                 //
    ////////////////////////////////////////////////////////////////////////
    public static SemanticTag createInMemoCopy(SemanticTag tag) {
        if (tag == null) {
            return null;
        }
        SemanticTag st = new InMemoSemanticTag(tag.getName(), tag.getSI());
        Util.copyPropertiesFromPropertyHolderToPropertyHolder(tag, st);
        return st;
    }

    /**
     * Creates a SNSemantic Tag and copies name and sis from tag into.
     *
     * @param tag
     * @return
     */
    public static SNSemanticTag createInMemoCopyToSNSemanticTag(SemanticTag tag) {
        if (tag == null) {
            return null;
        }
        SNSemanticTag st = new InMemo_SN_TX_SemanticTag(tag.getName(), tag.getSI());
        Util.copyPropertiesFromPropertyHolderToPropertyHolder(tag, st);
        return st;
    }

    /**
     * Creates a SNSemantic Tag and copies name and sis from tag into.
     *
     * @param tag
     * @return
     */
    public static TXSemanticTag createInMemoCopyToTXSemanticTag(SemanticTag tag) {
        if (tag == null) {
            return null;
        }
        TXSemanticTag st = new InMemo_SN_TX_SemanticTag(tag.getName(), tag.getSI());
        Util.copyPropertiesFromPropertyHolderToPropertyHolder(tag, st);
        return st;
    }

    public static SNSemanticTag createInMemoCopy(SNSemanticTag tag) {
        if (tag == null) {
            return null;
        }
        SNSemanticTag st = new InMemo_SN_TX_SemanticTag(tag.getName(), tag.getSI());
        Util.copyPropertiesFromPropertyHolderToPropertyHolder(tag, st);
        return st;
    }

    public static TXSemanticTag createInMemoCopy(TXSemanticTag tag) {
        if (tag == null) {
            return null;
        }
        TXSemanticTag st = new InMemo_SN_TX_SemanticTag(tag.getName(), tag.getSI());
        Util.copyPropertiesFromPropertyHolderToPropertyHolder(tag, st);
        return st;
    }

    public static PeerTXSemanticTag createInMemoCopy(PeerTXSemanticTag tag) {
        if (tag == null) {
            return null;
        }
        PeerTXSemanticTag st = new InMemo_SN_TX_PeerSemanticTag(tag.getName(), tag.getSI(), tag.getAddresses());
        Util.copyPropertiesFromPropertyHolderToPropertyHolder(tag, st);
        return st;
    }

    public static PeerSNSemanticTag createInMemoCopy(PeerSNSemanticTag tag) {
        if (tag == null) {
            return null;
        }
        PeerSNSemanticTag st = new InMemo_SN_TX_PeerSemanticTag(tag.getName(), tag.getSI(), tag.getAddresses());
        Util.copyPropertiesFromPropertyHolderToPropertyHolder(tag, st);
        return st;
    }

    public static PeerSemanticTag createInMemoCopy(PeerSemanticTag pst) {
        if (pst == null) {
            return null;
        }
        PeerSemanticTag st = new InMemo_SN_TX_PeerSemanticTag(pst.getName(), pst.getSI(), pst.getAddresses());
        Util.copyPropertiesFromPropertyHolderToPropertyHolder(pst, st);
        return st;
    }

    public static TimeSemanticTag createInMemoCopy(TimeSemanticTag tst) {
        if (tst == null) {
            return null;
        }
        TimeSemanticTag st = new InMemoTimeSemanticTag(tst.getFrom(), tst.getDuration());
        Util.copyPropertiesFromPropertyHolderToPropertyHolder(tst, st);
        return st;
    }

    private static String[] cloneSIs(String[] sis) {
        if (sis == null) {
            return null;
        }

        String[] newSIS = new String[sis.length];

        for (int i = 0; i < sis.length; i++) {
            newSIS[i] = new String(sis[i]);
        }

        return newSIS;
    }

    public static SharkGeometry createInMemoCopy(SharkGeometry geom) throws SharkKBException {
        if (geom == null) {
            return null;
        }

        return InMemoSharkGeometry.createGeomByEWKT(geom.getEWKT());
    }

    public static SpatialSemanticTag createInMemoCopy(SpatialSemanticTag sst) throws SharkKBException {
        if (sst == null) {
            return null;
        }
        // copy each part of original tag
        String name = new String(sst.getName());
        String[] sis = InMemoSharkKB.cloneSIs(sst.getSI());
        SharkGeometry geom;
        geom = InMemoSharkKB.createInMemoCopy(sst.getGeometry());

        InMemoSpatialSemanticTag st = new InMemoSpatialSemanticTag(name, sis, geom);
        Util.copyPropertiesFromPropertyHolderToPropertyHolder(sst, st);
        st.refreshStatus(); // important to refresh status right here - it extracts it geometry from properties
        return st;
    }

    ///////////////////////////////////////////////////////////////////
    //    allow creating standalone structures                       //
    ///////////////////////////////////////////////////////////////////

    /**
     * Create an in memory implementation of a semantic tag. This tag won't be
     * part of a knowledge base. Use merge() for that task.
     *
     * @param name
     * @param sis
     * @return
     */
    public static SemanticTag createInMemoSemanticTag(String name, String[] sis) {
        return new InMemoSemanticTag(name, sis);
    }

    /**
     * creates an TST covering a period from
     *
     * @param from     Start time - milliseconds beginning from 1.1.1970
     * @param duration duration of period in milliseconds
     * @return
     */
    public static TimeSemanticTag createInMemoTimeSemanticTag(long from, long duration) {
        return new InMemoTimeSemanticTag(from, duration);
    }

    public static Information createInMemoInformation() {
        return new InMemoInformation();
    }

    public static Knowledge createInMemoKnowledge(SharkVocabulary background) {
        return new InMemoASIPKnowledge(background);
    }

    /**
     * Create an in memory implementation of a semantic tag. This tag won't be
     * part of a knowledge base. Use merge() for that task.
     *
     * @param name
     * @param si
     * @return
     */
    public static SemanticTag createInMemoSemanticTag(String name, String si) {
        return new InMemoSemanticTag(name, new String[]{si});
    }

    /**
     * Create an in memory implementation of a peer semantic tag. This tag won't be
     * part of a knowledge base. Use merge() for that task.
     *
     * @param name
     * @param sis
     * @param addresses
     * @return
     */
    public static PeerSemanticTag createInMemoPeerSemanticTag(String name, String[] sis,
                                                              String[] addresses) {
        return new InMemo_SN_TX_PeerSemanticTag(name, sis, addresses);
    }

    /**
     * Create an in memory implementation of a peer semantic tag. This tag won't be
     * part of a knowledge base. Use merge() for that task.
     *
     * @param name
     * @param si
     * @param address
     * @return
     */
    public static PeerSemanticTag createInMemoPeerSemanticTag(String name, String si,
            String address) {

        if(address==null){
            return new InMemo_SN_TX_PeerSemanticTag(name, new String[] {si},
                    null);
        } else {
            return new InMemo_SN_TX_PeerSemanticTag(name, new String[] {si},
                    new String[]{ address} );
        }
    }

    /**
     * Creates an in memory semantic tag set. This set is not part of any knowledge
     * base. It can be merged into an exiting kb, though.
     *
     * @return
     */
    public static STSet createInMemoSTSet() {
        return new InMemoSTSet();
    }

    public static Knowledge createInMemoKnowledge() {
        return new InMemoASIPKnowledge();
    }


    ///////////////////////////////////////////////////////////////////
    //                       in memo copies                          //
    ///////////////////////////////////////////////////////////////////

    public static STSet createInMemoCopy(STSet stSet) throws SharkKBException {
        // looks weird but necessary...
        if (stSet instanceof PeerSTSet) {
            return InMemoSharkKB.createInMemoCopy((PeerSTSet) stSet);
        }

        if(stSet instanceof TimeSTSet) {
            return InMemoSharkKB.createInMemoCopy((TimeSTSet) stSet);
        }

        if(stSet instanceof SpatialSTSet) {
            return InMemoSharkKB.createInMemoCopy((SpatialSTSet) stSet);
        }

        STSet copy = InMemoSharkKB.createInMemoSTSet();
        copy.merge(stSet);
        return copy;
    }

    public static Taxonomy createInMemoCopy(Taxonomy taxonomy) throws SharkKBException {
        Taxonomy copy = InMemoSharkKB.createInMemoTaxonomy();
        copy.merge(taxonomy);
        return copy;
    }

    public static SemanticNet createInMemoCopy(SemanticNet semanticNet) throws SharkKBException {
        SemanticNet copy = InMemoSharkKB.createInMemoSemanticNet();
        copy.merge(semanticNet);
        return copy;
    }

    public static PeerSTSet createInMemoCopy(PeerSTSet peerSTSet) throws SharkKBException {
        PeerSTSet copy = InMemoSharkKB.createInMemoPeerSTSet();
        copy.merge(peerSTSet);
        return copy;
    }

    public static PeerTaxonomy createInMemoCopy(PeerTaxonomy peerTaxonomy) throws SharkKBException {
        PeerTaxonomy copy = InMemoSharkKB.createInMemoPeerTaxonomy();
        copy.merge(peerTaxonomy);
        return copy;
    }

    public static SpatialSTSet createInMemoCopy(SpatialSTSet spatialSTSet) throws SharkKBException {
        SpatialSTSet copy = InMemoSharkKB.createInMemoSpatialSTSet();
        copy.merge(spatialSTSet);
        return copy;
    }

    public static TimeSTSet createInMemoCopy(TimeSTSet timeSTSet) throws SharkKBException {
        TimeSTSet copy = InMemoSharkKB.createInMemoTimeSTSet();
        copy.merge(timeSTSet);
        return copy;
    }

    public static ASIPInterest createInMemoCopy(ASIPInterest interest)
            throws SharkKBException {
        return InMemoSharkKB.createInMemoCopy((ASIPSpace) interest);
    }

    public static ASIPInterest createInMemoCopy(ASIPSpace as)
            throws SharkKBException {

        STSet mTopics = null;
        STSet mTypes = null;
        PeerSTSet mApprovers = null;
        PeerSemanticTag mSender = null;
        PeerSTSet mReceivers = null;
        SpatialSTSet mLocations = null;
        TimeSTSet mTimes = null;

        if (as.getTopics() != null) {
            mTopics = InMemoSharkKB.createInMemoCopy(as.getTopics());
        }

        if (as.getTypes() != null) {
            mTypes = InMemoSharkKB.createInMemoCopy(as.getTypes());
        }

        if (as.getApprovers() != null) {
            mApprovers = InMemoSharkKB.createInMemoCopy(as.getApprovers());
        }

        if (as.getSender() != null) {
            mSender = InMemoSharkKB.createInMemoCopy(as.getSender());
        }

        if (as.getReceivers() != null) {
            mReceivers = InMemoSharkKB.createInMemoCopy(as.getReceivers());
        }

        if (as.getLocations() != null) {
            mLocations = InMemoSharkKB.createInMemoCopy(as.getLocations());
        }

        if (as.getTimes() != null) {
            mTimes = InMemoSharkKB.createInMemoCopy(as.getTimes());
        }

        return InMemoSharkKB.createInMemoASIPInterest(mTopics, mTypes, mSender,
                mApprovers, mReceivers, mTimes, mLocations, as.getDirection());
    }

    /**
     * Creates an in memory semantic tag set. This set is not part of any knowledge
     * base. It can be merged into an exiting kb, though.
     *
     * @return
     */
    public static SemanticNet createInMemoSemanticNet() {
        return new InMemoSemanticNet();
    }

    /**
     * Creates an in memory semantic tag set. This set is not part of any knowledge
     * base. It can be merged into an exiting kb, though.
     *
     * @return
     */
    public static Taxonomy createInMemoTaxonomy() {
        return new InMemoTaxonomy();
    }

    /**
     * Creates an in memory semantic tag set. This set is not part of any knowledge
     * base. It can be merged into an exiting kb, though.
     *
     * @return
     */
    public static PeerTaxonomy createInMemoPeerTaxonomy() {
        return new InMemoPeerTaxonomy();
    }

    /**
     * Create any spatial semantic tag. This tag has a geo location but no explicit
     * meaning.
     *
     * @param geometry
     * @return
     */
    public static SpatialSemanticTag createInMemoSpatialSemanticTag(SharkGeometry geometry) {
        return new InMemoSpatialSemanticTag(geometry);
    }

    public static SpatialSemanticTag createInMemoSpatialSemanticTag(String name, String si[], SharkGeometry geom) {
        return new InMemoSpatialSemanticTag(name, si, geom);
    }

    /**
     * Creates an in memory semantic tag set. This set is not part of any knowledge
     * base. It can be merged into an exiting kb, though.
     *
     * @return
     */
    public static PeerSemanticNet createInMemoPeerSemanticNet() {
        return new InMemoPeerSemanticNet();
    }

    /**
     * Creates an in memory semantic tag set. This set is not part of any knowledge
     * base. It can be merged into an exiting kb, though.
     *
     * @return
     */
    public static PeerSTSet createInMemoPeerSTSet() {
        return new InMemoPeerSTSet();
    }

    public static ASIPInterest createInMemoASIPInterest() throws SharkKBException {
        return new InMemoInterest();
    }

    public static ASIPInterest createInMemoASIPInterest(STSet topics, STSet types,
                                                        PeerSemanticTag sender, PeerSTSet approvers, PeerSTSet receivers,
                                                        TimeSTSet times, SpatialSTSet locations, int direction) throws SharkKBException {

        return new InMemoInterest(topics, types, sender, approvers,
                receivers, times, locations, direction);
    }

    public static TimeSTSet createInMemoTimeSTSet() {
        return new InMemoTimeSTSet();
    }

    public static SpatialSTSet createInMemoSpatialSTSet() {
        return new InMemoSpatialSTSet();
    }


    ///////////////////////////////////////////////////////////////////////////
    //                 actual kb implementation starts here                  //
    ///////////////////////////////////////////////////////////////////////////


    /**
     * Checks wether to tags are exactly the same. Means, that two concept
     * are NOT the same if one is ANY and the other is something else. Don't
     * mess up this methode with a similiar one in Shark algebra. If you don't
     * see the difference use shark algebra.
     *
     * @param s1
     * @param s2
     * @return
     */
    public static boolean exactMatch(SemanticTag s1, SemanticTag s2) {
        // same objects - ok
        if (s1 == s2) {
            return true;
        }
        // both any - ok
        if (SharkCSAlgebra.isAny(s1) && SharkCSAlgebra.isAny(s2)) {
            return true;
        }
        // just one is any - wrong
        if (SharkCSAlgebra.isAny(s1) || SharkCSAlgebra.isAny(s2)) {
            return false;
        }
        // both not null and both not any
        return SharkCSAlgebra.identical(s1, s2);
    }

    /**
     * Create an empty SharkKB.
     * <p>
     * The only tag, that will be created are the tags representing IN and OUT on
     * the direction and topic dimension.
     * <p>
     * It is advised to use the SharkEngine to create a new in-memory SharkKB though.
     */
    public InMemoSharkKB() {
        super(new InMemoSemanticNet(), // topic
                new InMemoSemanticNet(), // type
                new InMemoPeerTaxonomy(), // peers
                new InMemoSpatialSTSet(), // locations
                new InMemoTimeSTSet()); // times

        // this as knowledge background.
        Knowledge k = new InMemoASIPKnowledge(this);

        this.setKnowledge(k);
    }

    public InMemoSharkKB(SemanticNet topics, SemanticNet types,
                         PeerTaxonomy peers, SpatialSTSet locations,
                         TimeSTSet times) throws SharkKBException {

        super(topics, types, peers, locations, times);

        Knowledge k = new InMemoASIPKnowledge(this);

        this.setKnowledge(k);
    }

    public InMemoSharkKB(SemanticNet topics, SemanticNet types, PeerTaxonomy peers,
                         SpatialSTSet locations, TimeSTSet times,
                         Knowledge k) throws SharkKBException {

        super(topics, types, peers, locations, times, k);
    }

    @Override
    public PeerSemanticNet getPeersAsSemanticNet() throws SharkKBException {
        try {
            return super.getPeersAsSemanticNet();
        } catch (SharkKBException e) {
            // abstract implementation wasn't successfull
        }

        PeerSTSet peers = this.getPeerSTSet();

        if (peers instanceof InMemoPeerSTSet) {
            InMemoGenericTagStorage tagStorage = ((InMemoPeerSTSet) peers).getTagStorage();
            return new InMemoPeerSemanticNet(tagStorage);
        }

        throw new SharkKBException("peers dimension isn't a semantic net.");
    }

    @Override
    public Taxonomy getTopicsAsTaxonomy() throws SharkKBException {
        try {
            return super.getTopicsAsTaxonomy();
        } catch (SharkKBException e) {
            // abstract implementation wasn't successfull
        }

        STSet topics = this.getTopicSTSet();
        if (topics instanceof InMemoSTSet) {
            InMemoGenericTagStorage tagStorage = ((InMemoSTSet) topics).getTagStorage();
            return new InMemoTaxonomy(tagStorage);
        }

        throw new SharkKBException("topic dimension isn't a taxonomy");
    }

    @Override
    protected Knowledge getKnowledge() {
        return super.getKnowledge();
    }

    @Override
    public PeerSemanticTag getOwner() {
        return this.owner;
    }

    /**
     * It must be proteced - later FSKB get confuses when using FSKnowledge.
     * It's bit messy.
     *
     * @param knowledge
     */
    protected final void setKnowledge(Knowledge knowledge) {
        this.knowledge = knowledge;
    }

    @Override
    public void setOwner(PeerSemanticTag owner) {
        // remove listener from old owner
        if (this.owner != null && this.owner instanceof AbstractSemanticTag) {
            AbstractSemanticTag st = (AbstractSemanticTag) this.owner;
        }
        try {
            // owner already known in kb?
            this.owner = (PeerSemanticTag) this.getPeerSTSet().merge(owner);
        } catch (SharkKBException ex) {
            // very strange
            L.e("cannot save kb owner in kb - go ahead with remote owner", this);
            this.owner = owner;
        }
        this.setOwnerListener();
        this.persist();
    }

    @Override
    public ASIPSpace createASIPSpace(SemanticTag topic, SemanticTag type, PeerSemanticTag approver,
                                     PeerSemanticTag sender, PeerSemanticTag receiver,
                                     TimeSemanticTag time, SpatialSemanticTag location,
                                     int direction) throws SharkKBException {

        STSet topicSet = null, typeSet = null;
        PeerSTSet approverSet = null, receiverSet = null;
        TimeSTSet timeSet = null;
        SpatialSTSet locationSet = null;

        if (topic != null) {
            topicSet = this.createInMemoSTSet();
            topicSet.merge(topic);
        }

        if (type != null) {
            typeSet = this.createInMemoSTSet();
            typeSet.merge(type);
        }

        if (approver != null) {
            approverSet = this.createInMemoPeerSTSet();
            approverSet.merge(approver);
        }

        if (receiver != null) {
            receiverSet = this.createInMemoPeerSTSet();
            receiverSet.merge(receiver);
        }

        if (time != null) {
            timeSet = this.createInMemoTimeSTSet();
            timeSet.merge(time);
        }

        if (location != null) {
            locationSet = this.createInMemoSpatialSTSet();
            locationSet.merge(location);
        }

        return this.createASIPSpace(topicSet, typeSet, approverSet, sender, receiverSet, timeSet, locationSet, direction);
    }

    @Override
    public Iterator<ASIPInformationSpace> getAllInformationSpaces() throws SharkKBException {
        if (this.knowledge == null) return null;

        return this.knowledge.informationSpaces();
    }

    @Override
    public FragmentationParameter[] getStandardFPSet() {
        return new FragmentationParameter[0];
    }


    @Override
    public Iterator<ASIPInformationSpace> informationSpaces(
            ASIPSpace as, boolean matchAny) throws SharkKBException {


        if (as == null) {
            if (matchAny) {
                // return all
                // TODO
            }
            return informationSpaces();
        }

        ArrayList<ASIPInformationSpace> list = new ArrayList<>();

        Iterator<ASIPInformationSpace> iterator = informationSpaces();
        while (iterator.hasNext()) {
            ASIPInformationSpace next = iterator.next();
            if (SharkCSAlgebra.identical(next.getASIPSpace(), as)) {
                list.add(next);
            }
        }
        return list.iterator();

    }


    @Override
    public void persist() {
        super.persist();
        // owner
        if (this.owner != null) {
            String ownerSIString = Util.array2string(this.owner.getSI());
            if (ownerSIString != null && ownerSIString.length() > 0) {
                this.setSystemProperty(AbstractSharkKB.OWNER, ownerSIString);
            }
        }
        // default fp
        if (this.standardFP != null) {
            String defaultFPString = Util.fragmentationParameter2string(standardFP);
            this.setSystemProperty(AbstractSharkKB.DEFAULT_FP, defaultFPString);
        }
    }

    @Override
    public void refreshStatus() {
        super.refreshStatus();
        // owner
        String ownerSIString = this.getSystemProperty(AbstractSharkKB.OWNER);
        if (ownerSIString != null) {
            String[] ownerSIs = Util.string2array(ownerSIString);
            try {
                PeerSemanticTag storedOwner = this.getPeerSTSet().getSemanticTag(ownerSIs);
                if (storedOwner != null) {
                    this.owner = storedOwner;
                    // listen to changed in owner
                    this.setOwnerListener();
                }
            } catch (SharkKBException ex) {
                L.w("cannot find owner tag while restoring kb status from external memory", this);
            }
        }
        // default fp
        String defaultFPValue = this.getSystemProperty(AbstractSharkKB.DEFAULT_FP);
        if (defaultFPValue != null) {
            this.standardFP = Util.string2fragmentationParameter(defaultFPValue);
        }
    }

    @Override
    public void removeInformation(Information info, ASIPSpace infoSpace) throws SharkKBException {
        this.getKnowledge().removeInformation(infoSpace);
    }

    @Override
    public Iterator<ASIPInformation> getInformation(ASIPSpace infoSpace) throws SharkKBException {
        return this.getKnowledge().getInformation(infoSpace);
    }

    @Override
    public Iterator<ASIPInformation> getInformation(ASIPSpace infoSpace, boolean fullyInside, boolean matchAny) throws SharkKBException {
        return this.getKnowledge().getInformation(infoSpace, fullyInside, matchAny);
    }

    @Override
    public ASIPSpace createASIPSpace(STSet topics, STSet types, PeerSTSet approvers, PeerSemanticTag sender, PeerSTSet receiver, TimeSTSet times, SpatialSTSet locations, int direction) throws SharkKBException {
        return new InMemoInterest(topics, types, sender, approvers, receiver, times, locations, direction);
    }

    protected ASIPSpace mergeASIPSpace(ASIPSpace space) throws SharkKBException {
        if (this.topics != null && space.getTopics() != null) {
            this.topics.merge(space.getTopics());
        }

        if (this.types != null && space.getTypes() != null) {
            this.types.merge(space.getTypes());
        }

        if (this.peers != null) {
            if (space.getApprovers() != null) {
                this.peers.merge(space.getApprovers());
            }

            if (space.getSender() != null) {
                this.peers.merge(space.getSender());
            }

            if (space.getReceivers() != null) {
                this.peers.merge(space.getReceivers());
            }
        }

        if (this.locations != null && space.getLocations() != null) {
            this.locations.merge(space.getLocations());
        }

        if (this.times != null && space.getTimes() != null) {
            this.times.merge(space.getTimes());
        }

        return InMemoSharkKB.createInMemoCopy(space);
    }

    @Override
    public ASIPInformation addInformation(byte[] content, ASIPSpace semanticalAnnotations) throws SharkKBException {
        return this.addInformation(null, content, semanticalAnnotations);
    }

    @Override
    public ASIPInformation addInformation(InputStream contentIS, int numberOfBytes, ASIPSpace semanticalAnnotations) throws SharkKBException {
        return this.addInformation(null, contentIS, numberOfBytes, semanticalAnnotations);
    }

    @Override
    public ASIPInformation addInformation(String content, ASIPSpace semanticalAnnotations) throws SharkKBException {
        return this.addInformation(null, content, semanticalAnnotations);
    }

    @Override
    public ASIPInformation addInformation(String name, byte[] content, ASIPSpace semanticAnnotations) throws SharkKBException {
        ASIPSpace mergeASIPSpace = this.mergeASIPSpace(semanticAnnotations);
        return this.getKnowledge().addInformation(name, content, mergeASIPSpace);
    }

    @Override
    public ASIPInformation addInformation(String name, InputStream contentIS, int numberOfBytes, ASIPSpace semanticAnnotations) throws SharkKBException {
        ASIPSpace mergeASIPSpace = this.mergeASIPSpace(semanticAnnotations);
        return this.getKnowledge().addInformation(null, contentIS, numberOfBytes, mergeASIPSpace);
    }

    @Override
    public ASIPInformation addInformation(String name, String content, ASIPSpace semanticAnnotations) throws SharkKBException {
        ASIPSpace mergeASIPSpace = this.mergeASIPSpace(semanticAnnotations);
        return this.getKnowledge().addInformation(name, content, mergeASIPSpace);
    }
}
