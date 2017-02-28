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

    private final InMemoSharkKB sharkKB;
    private final File file;

    public FileDumpSharkKB(InMemoSharkKB sharkKB, File file) {
        this.sharkKB = sharkKB;
        this.file = file;
    }

    public FileDumpSharkKB(File file) {
        this.sharkKB = new InMemoSharkKB();
        this.file = file;
    }

    private void readFromFile(){

    }

    private void writeToFile(){

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
    public void semanticTagCreated(SemanticTag tag, STSet stset) {
        this.sharkKB.semanticTagCreated(tag, stset);
    }

    @Override
    public void semanticTagRemoved(SemanticTag tag, STSet stset) {
        this.sharkKB.semanticTagRemoved(tag, stset);
    }

    @Override
    public void semanticTagChanged(SemanticTag tag, STSet stset) {
        this.sharkKB.semanticTagChanged(tag, stset);
    }

    @Override
    public ASIPSpace asASIPSpace() throws SharkKBException {
        return this.sharkKB.asASIPSpace();
    }

    @Override
    public ASIPInterest asASIPInterest() throws SharkKBException {
        return null;
    }

    @Override
    public STSet getTopicSTSet() throws SharkKBException {
        return null;
    }

    @Override
    public SemanticNet getTopicsAsSemanticNet() throws SharkKBException {
        return null;
    }

    @Override
    public Taxonomy getTopicsAsTaxonomy() throws SharkKBException {
        return null;
    }

    @Override
    public STSet getTypeSTSet() throws SharkKBException {
        return null;
    }

    @Override
    public SemanticNet getTypesAsSemanticNet() throws SharkKBException {
        return null;
    }

    @Override
    public Taxonomy getTypesAsTaxonomy() throws SharkKBException {
        return null;
    }

    @Override
    public PeerSTSet getPeerSTSet() throws SharkKBException {
        return null;
    }

    @Override
    public PeerSemanticNet getPeersAsSemanticNet() throws SharkKBException {
        return null;
    }

    @Override
    public PeerTaxonomy getPeersAsTaxonomy() throws SharkKBException {
        return null;
    }

    @Override
    public TimeSTSet getTimeSTSet() throws SharkKBException {
        return null;
    }

    @Override
    public SpatialSTSet getSpatialSTSet() throws SharkKBException {
        return null;
    }

    @Override
    public ASIPInterest contextualize(ASIPSpace as) throws SharkKBException {
        return null;
    }

    @Override
    public ASIPInterest contextualize(ASIPSpace as, FPSet fps) throws SharkKBException {
        return null;
    }

    @Override
    public void setProperty(String name, String value) throws SharkKBException {

    }

    @Override
    public String getProperty(String name) throws SharkKBException {
        return null;
    }

    @Override
    public void setProperty(String name, String value, boolean transfer) throws SharkKBException {

    }

    @Override
    public void removeProperty(String name) throws SharkKBException {

    }

    @Override
    public Enumeration<String> propertyNames() throws SharkKBException {
        return null;
    }

    @Override
    public Enumeration<String> propertyNames(boolean all) throws SharkKBException {
        return null;
    }

    @Override
    public ASIPInformationSpace mergeInformation(Iterator<ASIPInformation> information, ASIPSpace space) throws SharkKBException {
        return null;
    }

    @Override
    public ASIPInformation addInformation(byte[] content, ASIPSpace semanticAnnotations) throws SharkKBException {
        return null;
    }

    @Override
    public ASIPInformation addInformation(InputStream contentIS, int numberOfBytes, ASIPSpace semanticAnnotations) throws SharkKBException {
        return null;
    }

    @Override
    public ASIPInformation addInformation(String content, ASIPSpace semanticAnnotations) throws SharkKBException {
        return null;
    }

    @Override
    public ASIPInformation addInformation(String name, String content, ASIPSpace semanticAnnotations) throws SharkKBException {
        return null;
    }

    @Override
    public ASIPInformation addInformation(String name, byte[] content, ASIPSpace semanticAnnotations) throws SharkKBException {
        return null;
    }

    @Override
    public ASIPInformation addInformation(String name, InputStream contentIS, int numberOfBytes, ASIPSpace semanticAnnotations) throws SharkKBException {
        return null;
    }

    @Override
    public void removeInformation(ASIPInformation info, ASIPSpace infoSpace) throws SharkKBException {

    }

    @Override
    public Iterator<ASIPInformation> getInformation(ASIPSpace infoSpace) throws SharkKBException {
        return null;
    }

    @Override
    public Iterator<ASIPInformation> getInformation(ASIPSpace infoSpace, boolean fullyInside, boolean matchAny) throws SharkKBException {
        return null;
    }

    @Override
    public Iterator<ASIPInformationSpace> informationSpaces() throws SharkKBException {
        return null;
    }

    @Override
    public void setOwner(PeerSemanticTag owner) {

    }

    @Override
    public PeerSemanticTag getOwner() {
        return null;
    }

    @Override
    public ArrayList<ASIPSpace> assimilate(SharkKB target, ASIPSpace interest, FragmentationParameter[] backgroundFP, Knowledge knowledge, boolean learnTags, boolean deleteAssimilated) throws SharkKBException {
        return null;
    }

    @Override
    public Knowledge extract(ASIPSpace context) throws SharkKBException {
        return null;
    }

    @Override
    public Knowledge extract(ASIPSpace context, FragmentationParameter[] fp) throws SharkKBException {
        return null;
    }

    @Override
    public Knowledge extract(ASIPSpace context, FragmentationParameter[] backgroundFP, PeerSemanticTag recipient) throws SharkKBException {
        return null;
    }

    @Override
    public Knowledge extract(ASIPSpace context, FragmentationParameter[] backgroundFP, boolean cutGroups) throws SharkKBException {
        return null;
    }

    @Override
    public Knowledge extract(SharkKB target, ASIPSpace context, FragmentationParameter[] backgroundFP, boolean cutGroups, PeerSemanticTag recipient) throws SharkKBException {
        return null;
    }

    @Override
    public Iterator<ASIPInformationSpace> informationSpaces(ASIPSpace as, boolean matchAny) throws SharkKBException {
        return null;
    }

    @Override
    public ASIPSpace createASIPSpace(SemanticTag topic, SemanticTag type, PeerSemanticTag approver, PeerSemanticTag sender, PeerSemanticTag receiver, TimeSemanticTag time, SpatialSemanticTag location, int direction) throws SharkKBException {
        return null;
    }

    @Override
    public ASIPSpace createASIPSpace(STSet topics, STSet types, PeerSTSet approvers, PeerSemanticTag sender, PeerSTSet receiver, TimeSTSet times, SpatialSTSet locations, int direction) throws SharkKBException {
        return null;
    }

    @Override
    public Iterator<ASIPInformationSpace> getAllInformationSpaces() throws SharkKBException {
        return null;
    }

    @Override
    public Iterator<ASIPInformationSpace> getInformationSpaces(ASIPSpace space) throws SharkKBException {
        return null;
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

    }

    @Override
    public SharkVocabulary getVocabulary() {
        return null;
    }

    @Override
    public int getNumberInformation() throws SharkKBException {
        return 0;
    }
}
