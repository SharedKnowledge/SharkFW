package net.sharkfw.knowledgeBase.sync;

import net.sharkfw.asip.ASIPInformation;
import net.sharkfw.asip.ASIPInformationSpace;
import net.sharkfw.asip.ASIPInterest;
import net.sharkfw.asip.ASIPSpace;
import net.sharkfw.knowledgeBase.*;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;

/**
 * Created by j4rvis on 19.07.16.
 *
 * @author thsc42
 */
public class SyncKB implements SharkKB {

    private SharkKB targetKB;
    public final static String TIME_PROPERTY_NAME = "shark_sync_time_property";

    public SyncKB(SharkKB localKB) throws SharkKBException {
        this.targetKB = localKB;
    }

    private void setCurrentTimeAsProperty(STSet set){
        Iterator<SemanticTag> iterator = null;
        try {
            iterator = set.stTags();
            while (iterator.hasNext()){
                SemanticTag tag = iterator.next();
                tag.setProperty(TIME_PROPERTY_NAME, String.valueOf(System.currentTimeMillis()), true);
                set.merge(tag);
            }
        } catch (SharkKBException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setOwner(PeerSemanticTag owner) {
        this.targetKB.setOwner(owner);
    }

    @Override
    public PeerSemanticTag getOwner() {
        return this.targetKB.getOwner();
    }

    @Deprecated
    @Override
    public SharkCS asSharkCS() {
        return this.targetKB.asSharkCS();
    }

    @Deprecated
    @Override
    public Interest asInterest() {
        return this.targetKB.asInterest();
    }

    @Override
    public ASIPSpace asASIPSpace() throws SharkKBException {
        return this.targetKB.asASIPSpace();
    }

    @Override
    public ASIPInterest asASIPInterest() throws SharkKBException {
        return this.targetKB.asASIPInterest();
    }

    @Override
    public STSet getTopicSTSet() throws SharkKBException {
        return this.targetKB.getTopicSTSet();
    }

    @Override
    public SemanticNet getTopicsAsSemanticNet() throws SharkKBException {
        return this.targetKB.getTopicsAsSemanticNet();
    }

    @Override
    public Taxonomy getTopicsAsTaxonomy() throws SharkKBException {
        return this.targetKB.getTopicsAsTaxonomy();
    }

    @Override
    public STSet getTypeSTSet() throws SharkKBException {
        return this.targetKB.getTypeSTSet();
    }

    @Override
    public SemanticNet getTypesAsSemanticNet() throws SharkKBException {
        return this.targetKB.getTypesAsSemanticNet();
    }

    @Override
    public Taxonomy getTypesAsTaxonomy() throws SharkKBException {
        return this.targetKB.getTypesAsTaxonomy();
    }

    @Override
    public PeerSTSet getPeerSTSet() throws SharkKBException {
        return this.targetKB.getPeerSTSet();
    }

    @Override
    public PeerSemanticNet getPeersAsSemanticNet() throws SharkKBException {
        return this.targetKB.getPeersAsSemanticNet();
    }

    @Override
    public PeerTaxonomy getPeersAsTaxonomy() throws SharkKBException {
        return this.targetKB.getPeersAsTaxonomy();
    }

    @Override
    public TimeSTSet getTimeSTSet() throws SharkKBException {
        return this.targetKB.getTimeSTSet();
    }

    @Override
    public SpatialSTSet getSpatialSTSet() throws SharkKBException {
        return this.targetKB.getSpatialSTSet();
    }

    @Deprecated
    @Override
    public Interest contextualize(SharkCS cs) throws SharkKBException {
        return this.targetKB.contextualize(cs);
    }

    @Override
    public ASIPInterest contextualize(ASIPSpace as) throws SharkKBException {
        return this.targetKB.contextualize(as);
    }

    @Deprecated
    @Override
    public Interest contextualize(SharkCS as, FragmentationParameter[] fp) throws SharkKBException {
        return this.targetKB.contextualize(as, fp);
    }

    @Override
    public ASIPInterest contextualize(ASIPSpace as, FPSet fps) throws SharkKBException {
        return this.targetKB.contextualize(as, fps);
    }

    @Deprecated
    @Override
    public ContextPoint getContextPoint(ContextCoordinates coordinates) throws SharkKBException {
        return this.targetKB.getContextPoint(coordinates);
    }

    @Deprecated
    @Override
    public ContextCoordinates createContextCoordinates(SemanticTag topic, PeerSemanticTag originator, PeerSemanticTag peer, PeerSemanticTag remotepeer, TimeSemanticTag time, SpatialSemanticTag location, int direction) throws SharkKBException {
        return this.targetKB.createContextCoordinates(topic, originator, peer,remotepeer, time, location, direction);
    }

    @Deprecated
    @Override
    public ContextPoint createContextPoint(ContextCoordinates coordinates) throws SharkKBException {
        return this.targetKB.createContextPoint(coordinates);
    }

    @Override
    public ArrayList<ASIPSpace> assimilate(SharkKB target, ASIPSpace interest, FragmentationParameter[] backgroundFP, Knowledge knowledge, boolean learnTags, boolean deleteAssimilated) throws SharkKBException {
        return this.targetKB.assimilate(target, interest, backgroundFP, knowledge, learnTags, deleteAssimilated);
    }

    @Override
    public Knowledge extract(ASIPSpace context) throws SharkKBException {
        return this.targetKB.extract(context);
    }

    @Override
    public Knowledge extract(ASIPSpace context, FragmentationParameter[] fp) throws SharkKBException {
        return this.targetKB.extract(context, fp);
    }

    @Override
    public Knowledge extract(ASIPSpace context, FragmentationParameter[] backgroundFP, PeerSemanticTag recipient) throws SharkKBException {
        return this.targetKB.extract(context, backgroundFP, recipient);
    }

    @Override
    public Knowledge extract(ASIPSpace context, FragmentationParameter[] backgroundFP, boolean cutGroups) throws SharkKBException {
        return this.targetKB.extract(context, backgroundFP, cutGroups);
    }

    @Override
    public Knowledge extract(SharkKB target, ASIPSpace context, FragmentationParameter[] backgroundFP, boolean cutGroups, PeerSemanticTag recipient) throws SharkKBException {
        return this.targetKB.extract(target, context, backgroundFP, cutGroups, recipient);
    }

    @Deprecated
    @Override
    public Knowledge createKnowledge() {
        return this.targetKB.createKnowledge();
    }

    @Deprecated
    @Override
    public void removeContextPoint(ContextCoordinates coordinates) throws SharkKBException {
        this.targetKB.removeContextPoint(coordinates);
    }

    @Deprecated
    @Override
    public Enumeration<ContextPoint> getContextPoints(SharkCS cs) throws SharkKBException {
        return this.targetKB.getContextPoints(cs);
    }

    @Deprecated
    @Override
    public Iterator<ContextPoint> contextPoints(SharkCS cs) throws SharkKBException {
        return this.targetKB.contextPoints(cs);
    }

    @Deprecated
    @Override
    public Enumeration<ContextPoint> getContextPoints(SharkCS cs, boolean matchAny) throws SharkKBException {
        return this.targetKB.getContextPoints(cs, matchAny);
    }

    @Deprecated
    @Override
    public Iterator<ContextPoint> contextPoints(SharkCS cs, boolean matchAny) throws SharkKBException {
        return this.targetKB.contextPoints(cs, matchAny);
    }

    @Override
    public Iterator<ASIPInformationSpace> informationSpaces(ASIPSpace as, boolean matchAny) throws SharkKBException {
        return this.targetKB.informationSpaces(as, matchAny);
    }

    @Deprecated
    @Override
    public Enumeration<ContextPoint> getAllContextPoints() throws SharkKBException {
        return this.targetKB.getAllContextPoints();
    }

    @Override
    public ASIPSpace createASIPSpace(STSet topics, STSet types, PeerSTSet approvers, PeerSTSet sender, PeerSTSet receiver, TimeSTSet times, SpatialSTSet locations, int direction) throws SharkKBException {
        setCurrentTimeAsProperty(topics);
        setCurrentTimeAsProperty(types);
        setCurrentTimeAsProperty(approvers);
        setCurrentTimeAsProperty(sender);
        setCurrentTimeAsProperty(times);
        setCurrentTimeAsProperty(locations);
        return this.targetKB.createASIPSpace(topics, types, approvers, sender, receiver, times, locations, direction);
    }

    @Override
    public ASIPSpace createASIPSpace(SemanticTag topic, SemanticTag type, PeerSemanticTag approver, PeerSemanticTag sender, PeerSemanticTag receiver, TimeSemanticTag time, SpatialSemanticTag location, int direction) throws SharkKBException {
        return this.targetKB.createASIPSpace(topic, type, approver, sender, receiver, time, location, direction);
    }

    @Override
    public ASIPSpace createASIPSpace(STSet topics, STSet types, PeerSTSet approvers, PeerSemanticTag sender, PeerSTSet receiver, TimeSTSet times, SpatialSTSet locations, int direction) throws SharkKBException {
        setCurrentTimeAsProperty(topics);
        setCurrentTimeAsProperty(types);
        setCurrentTimeAsProperty(approvers);
        sender.setProperty(TIME_PROPERTY_NAME, String.valueOf(System.currentTimeMillis()), true);
        setCurrentTimeAsProperty(times);
        setCurrentTimeAsProperty(locations);
        return this.targetKB.createASIPSpace(topics, types, approvers, sender, receiver, times, locations, direction);
    }

    @Override
    public ASIPSpace createASIPSpace(STSet topics, STSet types, PeerSTSet approvers, PeerSTSet sender, PeerSTSet receiver, TimeSTSet times, SpatialSTSet locations) throws SharkKBException {
        setCurrentTimeAsProperty(topics);
        setCurrentTimeAsProperty(types);
        setCurrentTimeAsProperty(approvers);
        setCurrentTimeAsProperty(sender);
        setCurrentTimeAsProperty(times);
        setCurrentTimeAsProperty(locations);
        return this.targetKB.createASIPSpace(topics, types, approvers, sender, receiver, times, locations);
    }

    @Override
    public Iterator<ASIPInformationSpace> getAllInformationSpaces() throws SharkKBException {
        return this.targetKB.getAllInformationSpaces();
    }

    @Override
    public void addListener(KnowledgeBaseListener kbl) {
        this.targetKB.addListener(kbl);
    }

    @Override
    public void removeListener(KnowledgeBaseListener kbl) {
        this.targetKB.removeListener(kbl);
    }

    @Override
    public void setStandardFPSet(FragmentationParameter[] fps) {
        this.targetKB.setStandardFPSet(fps);
    }

    @Override
    public FragmentationParameter[] getStandardFPSet() {
        return this.targetKB.getStandardFPSet();
    }

    @Override
    public ASIPInformationSpace mergeInformation(Iterator<ASIPInformation> information, ASIPSpace space) throws SharkKBException {
        return this.targetKB.mergeInformation(information, space);
    }

    @Override
    public ASIPInformation addInformation(byte[] content, ASIPSpace semanticAnnotations) throws SharkKBException {
        return this.targetKB.addInformation(content, semanticAnnotations);
    }

    @Override
    public ASIPInformation addInformation(InputStream contentIS, int numberOfBytes, ASIPSpace semanticAnnotations) throws SharkKBException {
        return this.targetKB.addInformation(contentIS, numberOfBytes, semanticAnnotations);
    }

    @Override
    public ASIPInformation addInformation(String content, ASIPSpace semanticAnnotations) throws SharkKBException {
        return this.targetKB.addInformation(content, semanticAnnotations);
    }

    @Override
    public void removeInformation(Information info, ASIPSpace infoSpace) throws SharkKBException {
        this.targetKB.removeInformation(info, infoSpace);
    }

    @Override
    public Iterator<ASIPInformation> getInformation(ASIPSpace infoSpace) throws SharkKBException {
        return this.targetKB.getInformation(infoSpace);
    }

    @Override
    public Iterator<ASIPInformation> getInformation(ASIPSpace infoSpace, boolean fullyInside, boolean matchAny) throws SharkKBException {
        return this.targetKB.getInformation(infoSpace, fullyInside, matchAny);
    }

    @Override
    public Iterator<ASIPInformationSpace> informationSpaces() throws SharkKBException {
        return this.targetKB.informationSpaces();
    }

    @Override
    public void removeInformation(ASIPSpace space) throws SharkKBException {
        this.targetKB.removeInformation(space);
    }

    @Override
    public SharkVocabulary getVocabulary() {
        return this.targetKB.getVocabulary();
    }

    @Override
    public int getNumberInformation() throws SharkKBException {
        return this.targetKB.getNumberInformation();
    }

    @Deprecated
    @Override
    public void addInterest(SharkCS interest) throws SharkKBException {
        this.targetKB.addInterest(interest);
    }

    @Deprecated
    @Override
    public void removeInterest(SharkCS interest) throws SharkKBException {
        this.targetKB.removeInterest(interest);
    }

    @Deprecated
    @Override
    public Iterator<SharkCS> interests() throws SharkKBException {
        return this.targetKB.interests();
    }

    @Override
    public void setProperty(String name, String value) throws SharkKBException {
        this.targetKB.setProperty(name, value);
    }

    @Override
    public String getProperty(String name) throws SharkKBException {
        return this.targetKB.getProperty(name);
    }

    @Override
    public void setProperty(String name, String value, boolean transfer) throws SharkKBException {
        this.targetKB.setProperty(name, value, transfer);
    }

    @Override
    public void removeProperty(String name) throws SharkKBException {
        this.targetKB.removeProperty(name);
    }

    @Override
    public Enumeration<String> propertyNames() throws SharkKBException {
        return this.targetKB.propertyNames();
    }

    @Override
    public Enumeration<String> propertyNames(boolean all) throws SharkKBException {
        return this.targetKB.propertyNames(all);
    }

    @Override
    public void semanticTagCreated(SemanticTag tag, STSet stset) {
        this.targetKB.semanticTagCreated(tag, stset);
    }

    @Override
    public void semanticTagRemoved(SemanticTag tag, STSet stset) {
        this.targetKB.semanticTagRemoved(tag, stset);
    }

    @Override
    public void semanticTagChanged(SemanticTag tag, STSet stset) {
        this.targetKB.semanticTagChanged(tag, stset);
    }

    @Override
    public void setSystemProperty(String name, String value) {
        this.targetKB.setSystemProperty(name, value);
    }

    @Override
    public String getSystemProperty(String name) {
        return this.targetKB.getSystemProperty(name);
    }
}
