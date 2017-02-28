package net.sharkfw.knowledgeBase.persistent.fileDump;

import net.sharkfw.asip.ASIPInformation;
import net.sharkfw.asip.ASIPInformationSpace;
import net.sharkfw.asip.ASIPInterest;
import net.sharkfw.asip.ASIPSpace;
import net.sharkfw.knowledgeBase.*;
import net.sharkfw.knowledgeBase.inmemory.InMemoSharkKB;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;

/**
 * Created by j4rvis on 2/27/17.
 */
public class FileDumpSharkKB implements SharkKB {

    private final SharkKB sharkKB;
    private final File file;

    public FileDumpSharkKB(InMemoSharkKB sharkKB, File file) {
        this.sharkKB = sharkKB;
        this.file = file;
    }

    public FileDumpSharkKB(File file) {
        this.sharkKB = new InMemoSharkKB();
        this.file = file;
    }

    public void persist(){

    }

    @Override
    public void setSystemProperty(String name, String value) {
        this.sharkKB.setSystemProperty(name, value);
        this.persist();
    }

    @Override
    public String getSystemProperty(String name) {
        return this.sharkKB.getSystemProperty(name);
    }

    @Override
    public void setOwner(PeerSemanticTag owner) {
        sharkKB.setOwner(owner);
        this.persist();
    }

    @Override
    public PeerSemanticTag getOwner() {
        return new FileDumpPeerSemanticTag(this, sharkKB.getOwner());
    }

    @Override
    public ArrayList<ASIPSpace> assimilate(SharkKB target, ASIPSpace interest, FragmentationParameter[] backgroundFP, Knowledge knowledge, boolean learnTags, boolean deleteAssimilated) throws SharkKBException {
        ArrayList<ASIPSpace> assimilate = this.sharkKB.assimilate(target, interest, backgroundFP, knowledge, learnTags, deleteAssimilated);
        this.persist();
        ArrayList<ASIPSpace> list = new ArrayList<>();
        for (ASIPSpace asipSpace : assimilate) {
            list.add(new FileDumpASIPSpace(this, asipSpace));
        }
        return list;
    }

    @Override
    public Knowledge extract(ASIPSpace context) throws SharkKBException {
        Knowledge extract = this.sharkKB.extract(context);
        persist();
        return new FileDumpKnowledge(this, extract);
    }

    @Override
    public Knowledge extract(ASIPSpace context, FragmentationParameter[] fp) throws SharkKBException {
        Knowledge extract = this.sharkKB.extract(context, fp);
        persist();
        return new FileDumpKnowledge(this, extract);
    }

    @Override
    public Knowledge extract(ASIPSpace context, FragmentationParameter[] backgroundFP, PeerSemanticTag recipient) throws SharkKBException {
        Knowledge extract = this.sharkKB.extract(context, backgroundFP, recipient);
        persist();
        return new FileDumpKnowledge(this, extract);
    }

    @Override
    public Knowledge extract(ASIPSpace context, FragmentationParameter[] backgroundFP, boolean cutGroups) throws SharkKBException {
        Knowledge extract = this.sharkKB.extract(context, backgroundFP, cutGroups);
        persist();
        return new FileDumpKnowledge(this, extract);
    }

    @Override
    public Knowledge extract(SharkKB target, ASIPSpace context, FragmentationParameter[] backgroundFP, boolean cutGroups, PeerSemanticTag recipient) throws SharkKBException {
        Knowledge extract = this.sharkKB.extract(context, backgroundFP, recipient);
        persist();
        return new FileDumpKnowledge(this, extract);
    }

    @Override
    public Iterator<ASIPInformationSpace> informationSpaces(ASIPSpace as, boolean matchAny) throws SharkKBException {
        Iterator<ASIPInformationSpace> asipInformationSpaceIterator = this.sharkKB.informationSpaces(as, matchAny);
        ArrayList<ASIPInformationSpace> list = new ArrayList<>();
        while (asipInformationSpaceIterator.hasNext()){
            list.add(new FileDumpASIPInformationSpace(this, asipInformationSpaceIterator.next()));
        }
        return list.iterator();
    }

    @Override
    public ASIPSpace createASIPSpace(SemanticTag topic, SemanticTag type, PeerSemanticTag approver, PeerSemanticTag sender, PeerSemanticTag receiver, TimeSemanticTag time, SpatialSemanticTag location, int direction) throws SharkKBException {
        ASIPSpace asipSpace = this.sharkKB.createASIPSpace(topic, type, approver, sender, receiver, time, location, direction);
        persist();
        return new FileDumpASIPSpace(this, asipSpace);
    }

    @Override
    public ASIPSpace createASIPSpace(STSet topics, STSet types, PeerSTSet approvers, PeerSemanticTag sender, PeerSTSet receiver, TimeSTSet times, SpatialSTSet locations, int direction) throws SharkKBException {
        ASIPSpace asipSpace = this.sharkKB.createASIPSpace(topics, types, approvers, sender, receiver, times, locations, direction);
        persist();
        return new FileDumpASIPSpace(this, asipSpace);
    }

    @Override
    public Iterator<ASIPInformationSpace> getAllInformationSpaces() throws SharkKBException {
        Iterator<ASIPInformationSpace> asipInformationSpaceIterator = this.sharkKB.getAllInformationSpaces();
        ArrayList<ASIPInformationSpace> list = new ArrayList<>();
        while (asipInformationSpaceIterator.hasNext()){
            list.add(new FileDumpASIPInformationSpace(this, asipInformationSpaceIterator.next()));
        }
        return list.iterator();
    }

    @Override
    public ASIPSpace asASIPSpace() throws SharkKBException {
        return new FileDumpASIPSpace(this, sharkKB.asASIPSpace());
    }

    @Override
    public ASIPInterest asASIPInterest() throws SharkKBException {
        return new FileDumpASIPInterest(this, sharkKB.asASIPInterest());
    }

    @Override
    public STSet getTopicSTSet() throws SharkKBException {
        return new FileDumpSTSet(this, sharkKB.getTopicSTSet());
    }

    @Override
    public SemanticNet getTopicsAsSemanticNet() throws SharkKBException {
        return new FileDumpSemanticNet(this, sharkKB.getTopicsAsSemanticNet());
    }

    @Override
    public Taxonomy getTopicsAsTaxonomy() throws SharkKBException {
        return new FileDumpTaxonomy(this, sharkKB.getTopicsAsTaxonomy());
    }

    @Override
    public STSet getTypeSTSet() throws SharkKBException {
        return new FileDumpSTSet(this, sharkKB.getTypeSTSet());
    }

    @Override
    public SemanticNet getTypesAsSemanticNet() throws SharkKBException {
        return new FileDumpSemanticNet(this, sharkKB.getTypesAsSemanticNet());
    }

    @Override
    public Taxonomy getTypesAsTaxonomy() throws SharkKBException {
        return new FileDumpTaxonomy(this, sharkKB.getTypesAsTaxonomy());
    }

    @Override
    public PeerSTSet getPeerSTSet() throws SharkKBException {
        return new FileDumpPeerSTSet(this, sharkKB.getPeerSTSet());
    }

    @Override
    public PeerSemanticNet getPeersAsSemanticNet() throws SharkKBException {
        return new FileDumpPeerSemanticNet(this, sharkKB.getPeersAsSemanticNet());
    }

    @Override
    public PeerTaxonomy getPeersAsTaxonomy() throws SharkKBException {
        return new FileDumpPeerTaxonomy(this, sharkKB.getPeersAsTaxonomy());
    }

    @Override
    public TimeSTSet getTimeSTSet() throws SharkKBException {
        return new FileDumpTimeSTSet(this, sharkKB.getTimeSTSet());
    }

    @Override
    public SpatialSTSet getSpatialSTSet() throws SharkKBException {
        return new FileDumpSpatialSTSet(this, sharkKB.getSpatialSTSet());
    }

    @Override
    public ASIPInterest contextualize(ASIPSpace as) throws SharkKBException {
        ASIPInterest contextualize = sharkKB.contextualize(as);
        this.persist();
        return new FileDumpASIPInterest(this, contextualize);
    }

    @Override
    public ASIPInterest contextualize(ASIPSpace as, FPSet fps) throws SharkKBException {
        ASIPInterest contextualize = sharkKB.contextualize(as, fps);
        this.persist();
        return new FileDumpASIPInterest(this, contextualize);
    }

    @Override
    public ASIPInformationSpace mergeInformation(Iterator<ASIPInformation> information, ASIPSpace space) throws SharkKBException {
        ASIPInformationSpace asipInformationSpace = sharkKB.mergeInformation(information, space);
        this.persist();
        return new FileDumpASIPInformationSpace(this, asipInformationSpace);
    }

    @Override
    public ASIPInformation addInformation(byte[] content, ASIPSpace semanticAnnotations) throws SharkKBException {
        ASIPInformation asipInformation = sharkKB.addInformation(content, semanticAnnotations);
        this.persist();
        return new FileDumpASIPInformation(this, asipInformation);
    }

    @Override
    public ASIPInformation addInformation(InputStream contentIS, int numberOfBytes, ASIPSpace semanticAnnotations) throws SharkKBException {
        ASIPInformation asipInformation = sharkKB.addInformation(contentIS, numberOfBytes, semanticAnnotations);
        this.persist();
        return new FileDumpASIPInformation(this, asipInformation);
    }

    @Override
    public ASIPInformation addInformation(String content, ASIPSpace semanticAnnotations) throws SharkKBException {
        ASIPInformation asipInformation = sharkKB.addInformation(content, semanticAnnotations);
        this.persist();
        return new FileDumpASIPInformation(this, asipInformation);
    }

    @Override
    public ASIPInformation addInformation(String name, String content, ASIPSpace semanticAnnotations) throws SharkKBException {
        ASIPInformation asipInformation = sharkKB.addInformation(name, content, semanticAnnotations);
        this.persist();
        return new FileDumpASIPInformation(this, asipInformation);
    }

    @Override
    public ASIPInformation addInformation(String name, byte[] content, ASIPSpace semanticAnnotations) throws SharkKBException {
        ASIPInformation asipInformation = sharkKB.addInformation(name, content, semanticAnnotations);
        this.persist();
        return new FileDumpASIPInformation(this, asipInformation);
    }

    @Override
    public ASIPInformation addInformation(String name, InputStream contentIS, int numberOfBytes, ASIPSpace semanticAnnotations) throws SharkKBException {
        ASIPInformation asipInformation = sharkKB.addInformation(name, contentIS, numberOfBytes, semanticAnnotations);
        this.persist();
        return new FileDumpASIPInformation(this, asipInformation);
    }

    @Override
    public void removeInformation(ASIPInformation info, ASIPSpace infoSpace) throws SharkKBException {
        this.sharkKB.removeInformation(info, infoSpace);
        this.persist();
    }

    @Override
    public Iterator<ASIPInformation> getInformation(ASIPSpace infoSpace) throws SharkKBException {
        Iterator<ASIPInformation> informations = sharkKB.getInformation(infoSpace);
        ArrayList<ASIPInformation> list = new ArrayList<>();
        while (informations.hasNext()){
            list.add(new FileDumpASIPInformation(this, informations.next()));
        }
        return list.iterator();
    }

    @Override
    public Iterator<ASIPInformation> getInformation(ASIPSpace infoSpace, boolean fullyInside, boolean matchAny) throws SharkKBException {
        Iterator<ASIPInformation> informations = sharkKB.getInformation(infoSpace, fullyInside, matchAny);
        ArrayList<ASIPInformation> list = new ArrayList<>();
        while (informations.hasNext()){
            list.add(new FileDumpASIPInformation(this, informations.next()));
        }
        return list.iterator();
    }

    @Override
    public Iterator<ASIPInformationSpace> informationSpaces() throws SharkKBException {
        Iterator<ASIPInformationSpace> asipInformationSpaceIterator = sharkKB.informationSpaces();
        ArrayList<ASIPInformationSpace> list = new ArrayList<>();
        while (asipInformationSpaceIterator.hasNext()){
            list.add(new FileDumpASIPInformationSpace(this, asipInformationSpaceIterator.next()));
        }
        return list.iterator();
    }

    @Override
    public Iterator<ASIPInformationSpace> getInformationSpaces(ASIPSpace space) throws SharkKBException {
        Iterator<ASIPInformationSpace> asipInformationSpaceIterator = sharkKB.getInformationSpaces(space);
        ArrayList<ASIPInformationSpace> list = new ArrayList<>();
        while (asipInformationSpaceIterator.hasNext()){
            list.add(new FileDumpASIPInformationSpace(this, asipInformationSpaceIterator.next()));
        }
        return list.iterator();
    }

    @Override
    public void addListener(KnowledgeBaseListener kbl) {

    }

    @Override
    public void removeListener(KnowledgeBaseListener kbl) {

    }

    @Override
    public void setStandardFPSet(FragmentationParameter[] fps) {

    }

    @Override
    public FragmentationParameter[] getStandardFPSet() {
        return new FragmentationParameter[0];
    }

    @Override
    public void removeInformation(ASIPSpace space) throws SharkKBException {
        sharkKB.removeInformation(space);
        this.persist();
    }

    @Override
    public SharkVocabulary getVocabulary() {
        return new FileDumpSharkVocabulary(this, sharkKB.getVocabulary());
    }

    @Override
    public int getNumberInformation() throws SharkKBException {
        return sharkKB.getNumberInformation();
    }

    @Override
    public void setProperty(String name, String value) throws SharkKBException {
        this.sharkKB.setProperty(name, value);
        this.persist();
    }

    @Override
    public String getProperty(String name) throws SharkKBException {
        return this.sharkKB.getProperty(name);
    }

    @Override
    public void setProperty(String name, String value, boolean transfer) throws SharkKBException {
        this.sharkKB.setProperty(name, value, transfer);
        this.persist();
    }

    @Override
    public void removeProperty(String name) throws SharkKBException {
        this.sharkKB.removeProperty(name);
        this.persist();
    }

    @Override
    public Enumeration<String> propertyNames() throws SharkKBException {
        return this.sharkKB.propertyNames();
    }

    @Override
    public Enumeration<String> propertyNames(boolean all) throws SharkKBException {
        return this.sharkKB.propertyNames(all);
    }

    @Override
    public void semanticTagCreated(SemanticTag tag, STSet stset) {
        this.sharkKB.semanticTagCreated(tag, stset);
        this.persist();
    }

    @Override
    public void semanticTagRemoved(SemanticTag tag, STSet stset) {
        this.sharkKB.semanticTagRemoved(tag, stset);
        this.persist();
    }

    @Override
    public void semanticTagChanged(SemanticTag tag, STSet stset) {
        this.sharkKB.semanticTagChanged(tag, stset);
        this.persist();
    }
}
