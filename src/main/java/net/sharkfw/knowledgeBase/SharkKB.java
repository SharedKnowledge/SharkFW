package net.sharkfw.knowledgeBase;

import java.util.ArrayList;

import net.sharkfw.asip.ASIPSpace;
import net.sharkfw.asip.ASIPInformationSpace;

import java.util.Enumeration;
import java.util.Iterator;

import net.sharkfw.asip.ASIPKnowledge;
import net.sharkfw.ports.KnowledgePort;

/**
 * <p>The Shark knowledgebase.</p>
 * <p>
 * <p>The primary functions of the SharkKB are: <br />
 * <ul>
 * <li>Management of {@link SemanticTag}s</li>
 * <li>Creation of standard {@link Interest}s</li>
 * </ul>
 * </p>
 * <p>
 * <p><strong>Management of semantic tags</strong> <br />
 * The shark kb manages all semantic tags known to the local peer.
 * Semantic tags can be divided into different types (peers, locations, timespans, topics).
 * The SharkKB offers methods for creating instances of all of these types, sometimes,
 * in more than one way to offer a convienient interface. <br />
 * Peers, locations, and timespans can be used as topics as well (a place i.e. can be something to talk about),
 * while topics (i.e. 'Gardenwork'), are not fit to be used as anything but topics. Thus, whenever a new peer, location
 * or timespan is created, the created tag will automatically become part of the topics.
 * </p>
 * <p>
 * <p><strong>Management of context points</strong> <br />
 * ContextPoints tie {@link Information} and  together.
 * They can be created by providing a set of coordinates, and offer ways to
 * manage information.
 * </p>
 * <p>
 * <p><strong>Creation of standard interests</strong> <br />
 * Standard interests are used in {@link KnowledgePort} to define the details
 * of a communication with other peers. They define about what, when and where an
 * exchange may take place. The creation of these standard interests is handled here.
 * By providing a number of anchor points (in an AnchorSet) and a number of {@link FragmentationParameter}s,
 * the kepInterest creation extracts parts of the local stsets and puts them into
 * a new kepInterest.
 * </p>
 *
 * @author mfi, thsc
 * @see net.sharkfw.knowledgeBase.STSet
 */
public interface SharkKB extends SharkVocabulary, SystemPropertyHolder, STSetListener, ASIPKnowledge {

    public void setOwner(PeerSemanticTag owner);

    @Override
    public PeerSemanticTag getOwner();


    public ArrayList<ASIPSpace> assimilate(SharkKB target, ASIPSpace interest,
                                           FragmentationParameter[] backgroundFP, Knowledge knowledge,
                                           boolean learnTags, boolean deleteAssimilated) throws SharkKBException;

    /////////////////////////////////////////////////////////////////////////
    //                ASIP extraction support                              //
    /////////////////////////////////////////////////////////////////////////

    /**
     * Most simple version of extraction: Zero fragmentation parameter are used,
     * no recipient or groups are used
     *
     * @param context
     * @return
     * @throws SharkKBException
     */
    public Knowledge extract(ASIPSpace context)
            throws SharkKBException;

    public Knowledge extract(ASIPSpace context, FragmentationParameter[] fp)
            throws SharkKBException;

    public Knowledge extract(ASIPSpace context,
                             FragmentationParameter[] backgroundFP, PeerSemanticTag recipient)
            throws SharkKBException;

    public Knowledge extract(ASIPSpace context, FragmentationParameter[] backgroundFP,
                             boolean cutGroups)
            throws SharkKBException;

    public Knowledge extract(SharkKB target, ASIPSpace context,
                             FragmentationParameter[] backgroundFP, boolean cutGroups, PeerSemanticTag recipient)
            throws SharkKBException;

    /**
     * @param as
     * @param matchAny true: any tags in each dimensions are used as joker signs: There
     *                 are no contraints on the dimension in which the st set in any. If false:
     *                 any tag is used as each other tag and it is looked for an exact match
     * @return
     * @throws SharkKBException
     */
    public Iterator<ASIPInformationSpace> informationSpaces(ASIPSpace as, boolean matchAny) throws SharkKBException;

    /**
     * Convience method: create a space which is actually a single point
     *
     * @param topic
     * @param type
     * @param approver
     * @param sender
     * @param receiver
     * @param time
     * @param location
     * @param direction
     * @return
     * @throws SharkKBException
     */
    public ASIPSpace createASIPSpace(
            SemanticTag topic, SemanticTag type, PeerSemanticTag approver, PeerSemanticTag sender,
            PeerSemanticTag receiver, TimeSemanticTag time, SpatialSemanticTag location, int direction)
            throws SharkKBException;

    /**
     * @param topics
     * @param types
     * @param approvers
     * @param sender
     * @param receiver
     * @param times
     * @param locations
     * @param direction
     * @return
     * @throws SharkKBException
     */
    public ASIPSpace createASIPSpace(
            STSet topics, STSet types, PeerSTSet approvers, PeerSemanticTag sender,
            PeerSTSet receiver, TimeSTSet times, SpatialSTSet locations, int direction)
            throws SharkKBException;

    /**
     * Returns an interation of all informations points.
     * <p>
     * Use this methode very carefully. It produces a complete dump of that
     * knowledge base. That can be a lot.
     *
     * @return
     * @throws SharkKBException
     */
    public Iterator<ASIPInformationSpace> getAllInformationSpaces() throws SharkKBException;

    public Iterator<ASIPInformationSpace> getInformationSpaces(ASIPSpace space) throws SharkKBException;

    /**
     * Register a new listener for changes on this SharkKB
     *
     * @param kbl The listener to be added.
     */
    public void addListener(KnowledgeBaseListener kbl);

    /**
     * Remove a listener for changes on this SharkKB.
     *
     * @param kbl The listener to be removed.
     */
    public void removeListener(KnowledgeBaseListener kbl);

    /**
     * Set the standard FragmentationParameter for this SharkKB.
     * The standard fp will be used in kepInterest creation if no fp is provided otherwise.
     *
     * @param fps A FragmentationParameter array with ContextSpace.MAXDIMENSION fields
     */
    public void setStandardFPSet(FragmentationParameter fps[]);

    /**
     * Return the standard FragmentationParameter set for this SharkKB.
     *
     * @return A FragmentationParameter array with ContextSpace.MAXDIMENSION fields
     */
    public FragmentationParameter[] getStandardFPSet();

}
