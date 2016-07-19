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
 * Created by msc on 19.07.16.
 */
public class SyncKB implements SharkKB {

    private SharkKB localKB;

    public SyncKB(SharkKB localKB) throws SharkKBException {
        this.localKB = localKB;
    }

    @Override
    public void setOwner(PeerSemanticTag owner) {
        this.localKB.setOwner(owner);
    }

    @Override
    public PeerSemanticTag getOwner() {
        return this.localKB.getOwner();
    }

    @Deprecated
    @Override
    public SharkCS asSharkCS() {
        return this.localKB.asSharkCS();
    }

    @Deprecated
    @Override
    public Interest asInterest() {
        return this.localKB.asInterest();
    }

    @Override
    public ASIPSpace asASIPSpace() throws SharkKBException {
        return this.localKB.asASIPSpace();
    }

    @Override
    public ASIPInterest asASIPInterest() throws SharkKBException {
        return this.localKB.asASIPInterest();
    }

    @Override
    public STSet getTopicSTSet() throws SharkKBException {
        return this.localKB.getTopicSTSet();
    }

    @Override
    public SemanticNet getTopicsAsSemanticNet() throws SharkKBException {
        return this.localKB.getTopicsAsSemanticNet();
    }

    @Override
    public Taxonomy getTopicsAsTaxonomy() throws SharkKBException {
        return this.localKB.getTopicsAsTaxonomy();
    }

    @Override
    public STSet getTypeSTSet() throws SharkKBException {
        return this.localKB.getTypeSTSet();
    }

    @Override
    public SemanticNet getTypesAsSemanticNet() throws SharkKBException {
        return this.localKB.getTypesAsSemanticNet();
    }

    @Override
    public Taxonomy getTypesAsTaxonomy() throws SharkKBException {
        return this.localKB.getTypesAsTaxonomy();
    }

    @Override
    public PeerSTSet getPeerSTSet() throws SharkKBException {
        return this.localKB.getPeerSTSet();
    }

    @Override
    public PeerSemanticNet getPeersAsSemanticNet() throws SharkKBException {
        return this.localKB.getPeersAsSemanticNet();
    }

    @Override
    public PeerTaxonomy getPeersAsTaxonomy() throws SharkKBException {
        return this.localKB.getPeersAsTaxonomy();
    }

    @Override
    public TimeSTSet getTimeSTSet() throws SharkKBException {
        return this.localKB.getTimeSTSet();
    }

    @Override
    public SpatialSTSet getSpatialSTSet() throws SharkKBException {
        return this.localKB.getSpatialSTSet();
    }

    @Deprecated
    @Override
    public Interest contextualize(SharkCS cs) throws SharkKBException {
        return this.localKB.contextualize(cs);
    }

    @Override
    public ASIPInterest contextualize(ASIPSpace as) throws SharkKBException {
        return this.localKB.contextualize(as);
    }

    @Deprecated
    @Override
    public Interest contextualize(SharkCS as, FragmentationParameter[] fp) throws SharkKBException {
        return this.localKB.contextualize(as, fp);
    }

    @Override
    public ASIPInterest contextualize(ASIPSpace as, FPSet fps) throws SharkKBException {
        return this.localKB.contextualize(as, fps);
    }

    @Deprecated
    @Override
    public ContextPoint getContextPoint(ContextCoordinates coordinates) throws SharkKBException {
        return this.localKB.getContextPoint(coordinates);
    }

    @Deprecated
    @Override
    public ContextCoordinates createContextCoordinates(SemanticTag topic, PeerSemanticTag originator, PeerSemanticTag peer, PeerSemanticTag remotepeer, TimeSemanticTag time, SpatialSemanticTag location, int direction) throws SharkKBException {
        return this.localKB.createContextCoordinates(topic, originator, peer,remotepeer, time, location, direction);
    }

    @Deprecated
    @Override
    public ContextPoint createContextPoint(ContextCoordinates coordinates) throws SharkKBException {
        return this.localKB.createContextPoint(coordinates);
    }

    @Override
    public ArrayList<ASIPSpace> assimilate(SharkKB target, ASIPSpace interest, FragmentationParameter[] backgroundFP, Knowledge knowledge, boolean learnTags, boolean deleteAssimilated) throws SharkKBException {
        return this.localKB.assimilate(target, interest, backgroundFP, knowledge, learnTags, deleteAssimilated);
    }

    @Override
    public Knowledge extract(ASIPSpace context) throws SharkKBException {
        return this.localKB.extract(context);
    }

    @Override
    public Knowledge extract(ASIPSpace context, FragmentationParameter[] fp) throws SharkKBException {
        return this.localKB.extract(context, fp);
    }

    @Override
    public Knowledge extract(ASIPSpace context, FragmentationParameter[] backgroundFP, PeerSemanticTag recipient) throws SharkKBException {
        return this.localKB.extract(context, backgroundFP, recipient);
    }

    @Override
    public Knowledge extract(ASIPSpace context, FragmentationParameter[] backgroundFP, boolean cutGroups) throws SharkKBException {
        return this.localKB.extract(context, backgroundFP, cutGroups);
    }

    @Override
    public Knowledge extract(SharkKB target, ASIPSpace context, FragmentationParameter[] backgroundFP, boolean cutGroups, PeerSemanticTag recipient) throws SharkKBException {
        return this.localKB.extract(target, context, backgroundFP, cutGroups, recipient);
    }

    @Deprecated
    @Override
    public Knowledge createKnowledge() {
        return this.localKB.createKnowledge();
    }

    @Deprecated
    @Override
    public void removeContextPoint(ContextCoordinates coordinates) throws SharkKBException {
        this.localKB.removeContextPoint(coordinates);
    }

    @Deprecated
    @Override
    public Enumeration<ContextPoint> getContextPoints(SharkCS cs) throws SharkKBException {
        return this.localKB.getContextPoints(cs);
    }

    @Deprecated
    @Override
    public Iterator<ContextPoint> contextPoints(SharkCS cs) throws SharkKBException {
        return this.localKB.contextPoints(cs);
    }

    @Deprecated
    @Override
    public Enumeration<ContextPoint> getContextPoints(SharkCS cs, boolean matchAny) throws SharkKBException {
        return this.localKB.getContextPoints(cs, matchAny);
    }

    @Deprecated
    @Override
    public Iterator<ContextPoint> contextPoints(SharkCS cs, boolean matchAny) throws SharkKBException {
        return this.localKB.contextPoints(cs, matchAny);
    }

    @Override
    public Iterator<ASIPInformationSpace> informationSpaces(ASIPSpace as, boolean matchAny) throws SharkKBException {
        return this.localKB.informationSpaces(as, matchAny);
    }

    @Deprecated
    @Override
    public Enumeration<ContextPoint> getAllContextPoints() throws SharkKBException {
        return this.localKB.getAllContextPoints();
    }

    @Override
    public ASIPSpace createASIPSpace(STSet topics, STSet types, PeerSTSet approvers, PeerSTSet sender, PeerSTSet receiver, TimeSTSet times, SpatialSTSet locations, int direction) throws SharkKBException {
        return this.localKB.createASIPSpace(topics, types, approvers, sender, receiver, times, locations, direction);
    }

    @Override
    public ASIPSpace createASIPSpace(STSet topics, STSet types, PeerSTSet approvers, PeerSemanticTag sender, PeerSTSet receiver, TimeSTSet times, SpatialSTSet locations, int direction) throws SharkKBException {
        return this.localKB.createASIPSpace(topics, types, approvers, sender, receiver, times, locations, direction);
    }

    @Override
    public ASIPSpace createASIPSpace(STSet topics, STSet types, PeerSTSet approvers, PeerSTSet sender, PeerSTSet receiver, TimeSTSet times, SpatialSTSet locations) throws SharkKBException {
        return this.localKB.createASIPSpace(topics, types, approvers, sender, receiver, times, locations);
    }

    @Override
    public Iterator<ASIPInformationSpace> getAllInformationSpaces() throws SharkKBException {
        return this.localKB.getAllInformationSpaces();
    }

    @Override
    public void addListener(KnowledgeBaseListener kbl) {
        this.localKB.addListener(kbl);
    }

    @Override
    public void removeListener(KnowledgeBaseListener kbl) {
        this.localKB.removeListener(kbl);
    }

    @Override
    public void setStandardFPSet(FragmentationParameter[] fps) {
        this.localKB.setStandardFPSet(fps);
    }

    @Override
    public FragmentationParameter[] getStandardFPSet() {
        return this.localKB.getStandardFPSet();
    }

    @Override
    public ASIPInformationSpace mergeInformation(Iterator<ASIPInformation> information, ASIPSpace space) throws SharkKBException {
        return this.localKB.mergeInformation(information, space);
    }

    @Override
    public ASIPInformation addInformation(byte[] content, ASIPSpace semanticAnnotations) throws SharkKBException {
        return this.localKB.addInformation(content, semanticAnnotations);
    }

    @Override
    public ASIPInformation addInformation(InputStream contentIS, int numberOfBytes, ASIPSpace semanticAnnotations) throws SharkKBException {
        return this.localKB.addInformation(contentIS, numberOfBytes, semanticAnnotations);
    }

    @Override
    public ASIPInformation addInformation(String content, ASIPSpace semanticAnnotations) throws SharkKBException {
        return this.localKB.addInformation(content, semanticAnnotations);
    }

    @Override
    public void removeInformation(Information info, ASIPSpace infoSpace) throws SharkKBException {
        this.localKB.removeInformation(info, infoSpace);
    }

    @Override
    public Iterator<ASIPInformation> getInformation(ASIPSpace infoSpace) throws SharkKBException {
        return this.localKB.getInformation(infoSpace);
    }

    @Override
    public Iterator<ASIPInformation> getInformation(ASIPSpace infoSpace, boolean fullyInside, boolean matchAny) throws SharkKBException {
        return this.localKB.getInformation(infoSpace, fullyInside, matchAny);
    }

    @Override
    public Iterator<ASIPInformationSpace> informationSpaces() throws SharkKBException {
        return this.localKB.informationSpaces();
    }

    @Override
    public void removeInformation(ASIPSpace space) throws SharkKBException {
        this.localKB.removeInformation(space);
    }

    @Override
    public SharkVocabulary getVocabulary() {
        return this.localKB.getVocabulary();
    }

    @Override
    public int getNumberInformation() throws SharkKBException {
        return this.localKB.getNumberInformation();
    }

    @Deprecated
    @Override
    public void addInterest(SharkCS interest) throws SharkKBException {
        this.localKB.addInterest(interest);
    }

    @Deprecated
    @Override
    public void removeInterest(SharkCS interest) throws SharkKBException {
        this.localKB.removeInterest(interest);
    }

    @Deprecated
    @Override
    public Iterator<SharkCS> interests() throws SharkKBException {
        return this.localKB.interests();
    }

    @Override
    public void setProperty(String name, String value) throws SharkKBException {
        this.localKB.setProperty(name, value);
    }

    @Override
    public String getProperty(String name) throws SharkKBException {
        return this.localKB.getProperty(name);
    }

    @Override
    public void setProperty(String name, String value, boolean transfer) throws SharkKBException {
        this.localKB.setProperty(name, value, transfer);
    }

    @Override
    public void removeProperty(String name) throws SharkKBException {
        this.localKB.removeProperty(name);
    }

    @Override
    public Enumeration<String> propertyNames() throws SharkKBException {
        return this.localKB.propertyNames();
    }

    @Override
    public Enumeration<String> propertyNames(boolean all) throws SharkKBException {
        return this.localKB.propertyNames(all);
    }

    @Override
    public void semanticTagCreated(SemanticTag tag, STSet stset) {
        this.localKB.semanticTagCreated(tag, stset);
    }

    @Override
    public void semanticTagRemoved(SemanticTag tag, STSet stset) {
        this.localKB.semanticTagRemoved(tag, stset);
    }

    @Override
    public void semanticTagChanged(SemanticTag tag, STSet stset) {
        this.localKB.semanticTagChanged(tag, stset);
    }

    @Override
    public void setSystemProperty(String name, String value) {
        this.localKB.setSystemProperty(name, value);
    }

    @Override
    public String getSystemProperty(String name) {
        return this.localKB.getSystemProperty(name);
    }
}
