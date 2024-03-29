package net.sharkfw.knowledgeBase.persistent.dump;

import net.sharkfw.asip.ASIPInformation;
import net.sharkfw.asip.ASIPInformationSpace;
import net.sharkfw.asip.ASIPInterest;
import net.sharkfw.asip.ASIPSpace;
import net.sharkfw.asip.serialization.ASIPKnowledgeConverter;
import net.sharkfw.asip.serialization.ASIPMessageSerializer;
import net.sharkfw.asip.serialization.ASIPSerializerException;
import net.sharkfw.knowledgeBase.*;
import net.sharkfw.knowledgeBase.inmemory.InMemoSharkKB;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;

/**
 * Created by j4rvis on 2/27/17.
 */
public class DumpSharkKB implements SharkKB {

    private final int CONFIG_JSON_LENGTH = 9;

    private SharkKB sharkKB;
    private final File file;

    public DumpSharkKB(InMemoSharkKB sharkKB, File file) {
        this.sharkKB = sharkKB;
        this.file = file;
        createFileIfNotExists();
        this.persist();
    }

    public DumpSharkKB(File file) {
        this.file = file;
        createFileIfNotExists();
        read();
    }

    private void read(){
        FileInputStream stream = null;
        ByteArrayOutputStream buffer = null;
        try {
            stream = new FileInputStream(this.file);

            byte[] msgLength = new byte[CONFIG_JSON_LENGTH];
            stream.read(msgLength);
            String s = new String(msgLength, StandardCharsets.UTF_8);
            s.replaceFirst("^0+(?!$)", "");
            int jsonLength = Integer.parseInt(s);

            byte[] jsonMessage = new byte[jsonLength];
            stream.read(jsonMessage);
            String serializedKB = new String(jsonMessage, StandardCharsets.UTF_8);

            buffer = new ByteArrayOutputStream();

            int nRead;
            byte[] data = new byte[16384];

            while ((nRead = stream.read(data, 0, data.length)) != -1) {
                buffer.write(data, 0, nRead);
            }

            buffer.flush();

            byte[] content = buffer.toByteArray();

            ASIPKnowledgeConverter converter = new ASIPKnowledgeConverter(serializedKB, content);
            this.sharkKB = (SharkKB) converter.getKnowledge();

//            L.d("File read: " + this.file.getName(), this);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ASIPSerializerException e) {
            e.printStackTrace();
        } catch (SharkKBException e) {
            e.printStackTrace();
        } finally {
            try {
                buffer.close();
                stream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void createFileIfNotExists(){
        if(this.file.exists()){
//            L.d("File exists.", this);
        } else {
//            L.d("File doesn't exist.", this);
            try {
                boolean newFile = this.file.createNewFile();
//                L.d("createNewFile: "+ newFile, this);
//                L.d("file.exists: "+ this.file.exists(), this);
//                L.d("file.canWrite: "+ this.file.canWrite(), this);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void persist(){
        FileOutputStream stream = null;
        try {
            ASIPKnowledgeConverter converter = ASIPMessageSerializer.serializeKB(this.sharkKB);

            stream = new FileOutputStream(this.file);

            // Write length of serialized kb
            String format = String.format("%09d", converter.getSerializedKnowledge().length());
            stream.write(format.getBytes(StandardCharsets.UTF_8));

            // Write serialized kb
            stream.write(converter.getSerializedKnowledge().getBytes(StandardCharsets.UTF_8));

            // Write content
            stream.write(converter.getContent());
            stream.flush();

//                L.d("Flushed to file: " + this.file.getName(), this);

        } catch (SharkKBException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                stream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
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
        return new DumpPeerSemanticTag(this, sharkKB.getOwner());
    }

    @Override
    public ArrayList<ASIPSpace> assimilate(SharkKB target, ASIPSpace interest, FragmentationParameter[] backgroundFP, Knowledge knowledge, boolean learnTags, boolean deleteAssimilated) throws SharkKBException {
        ArrayList<ASIPSpace> assimilate = this.sharkKB.assimilate(target, interest, backgroundFP, knowledge, learnTags, deleteAssimilated);
        this.persist();
        ArrayList<ASIPSpace> list = new ArrayList<>();
        for (ASIPSpace asipSpace : assimilate) {
            list.add(new DumpASIPSpace(this, asipSpace));
        }
        return list;
    }

    @Override
    public Knowledge extract(ASIPSpace context) throws SharkKBException {
        Knowledge extract = this.sharkKB.extract(context);
        persist();
        return new DumpKnowledge(this, extract);
    }

    @Override
    public Knowledge extract(ASIPSpace context, FragmentationParameter[] fp) throws SharkKBException {
        Knowledge extract = this.sharkKB.extract(context, fp);
        persist();
        return new DumpKnowledge(this, extract);
    }

    @Override
    public Knowledge extract(ASIPSpace context, FragmentationParameter[] backgroundFP, PeerSemanticTag recipient) throws SharkKBException {
        Knowledge extract = this.sharkKB.extract(context, backgroundFP, recipient);
        persist();
        return new DumpKnowledge(this, extract);
    }

    @Override
    public Knowledge extract(ASIPSpace context, FragmentationParameter[] backgroundFP, boolean cutGroups) throws SharkKBException {
        Knowledge extract = this.sharkKB.extract(context, backgroundFP, cutGroups);
        persist();
        return new DumpKnowledge(this, extract);
    }

    @Override
    public Knowledge extract(SharkKB target, ASIPSpace context, FragmentationParameter[] backgroundFP, boolean cutGroups, PeerSemanticTag recipient) throws SharkKBException {
        Knowledge extract = this.sharkKB.extract(context, backgroundFP, recipient);
        persist();
        return new DumpKnowledge(this, extract);
    }

    @Override
    public Iterator<ASIPInformationSpace> informationSpaces(ASIPSpace as, boolean matchAny) throws SharkKBException {
        Iterator<ASIPInformationSpace> asipInformationSpaceIterator = this.sharkKB.informationSpaces(as, matchAny);
        ArrayList<ASIPInformationSpace> list = new ArrayList<>();
        while (asipInformationSpaceIterator.hasNext()){
            list.add(new DumpASIPInformationSpace(this, asipInformationSpaceIterator.next()));
        }
        return list.iterator();
    }

    @Override
    public ASIPSpace createASIPSpace(SemanticTag topic, SemanticTag type, PeerSemanticTag approver, PeerSemanticTag sender, PeerSemanticTag receiver, TimeSemanticTag time, SpatialSemanticTag location, int direction) throws SharkKBException {
        ASIPSpace asipSpace = this.sharkKB.createASIPSpace(topic, type, approver, sender, receiver, time, location, direction);
        persist();
        return new DumpASIPSpace(this, asipSpace);
    }

    @Override
    public ASIPSpace createASIPSpace(STSet topics, STSet types, PeerSTSet approvers, PeerSemanticTag sender, PeerSTSet receiver, TimeSTSet times, SpatialSTSet locations, int direction) throws SharkKBException {
        ASIPSpace asipSpace = this.sharkKB.createASIPSpace(topics, types, approvers, sender, receiver, times, locations, direction);
        persist();
        return new DumpASIPSpace(this, asipSpace);
    }

    @Override
    public Iterator<ASIPInformationSpace> getAllInformationSpaces() throws SharkKBException {
        Iterator<ASIPInformationSpace> asipInformationSpaceIterator = this.sharkKB.getAllInformationSpaces();
        ArrayList<ASIPInformationSpace> list = new ArrayList<>();
        while (asipInformationSpaceIterator.hasNext()){
            list.add(new DumpASIPInformationSpace(this, asipInformationSpaceIterator.next()));
        }
        return list.iterator();
    }

    @Override
    public ASIPSpace asASIPSpace() throws SharkKBException {
        return new DumpASIPSpace(this, sharkKB.asASIPSpace());
    }

    @Override
    public ASIPInterest asASIPInterest() throws SharkKBException {
        return new DumpASIPInterest(this, sharkKB.asASIPInterest());
    }

    @Override
    public STSet getTopicSTSet() throws SharkKBException {
        return new DumpSTSet(this, sharkKB.getTopicSTSet());
    }

    @Override
    public SemanticNet getTopicsAsSemanticNet() throws SharkKBException {
        return new DumpSemanticNet(this, sharkKB.getTopicsAsSemanticNet());
    }

    @Override
    public Taxonomy getTopicsAsTaxonomy() throws SharkKBException {
        return new DumpTaxonomy(this, sharkKB.getTopicsAsTaxonomy());
    }

    @Override
    public STSet getTypeSTSet() throws SharkKBException {
        return new DumpSTSet(this, sharkKB.getTypeSTSet());
    }

    @Override
    public SemanticNet getTypesAsSemanticNet() throws SharkKBException {
        return new DumpSemanticNet(this, sharkKB.getTypesAsSemanticNet());
    }

    @Override
    public Taxonomy getTypesAsTaxonomy() throws SharkKBException {
        return new DumpTaxonomy(this, sharkKB.getTypesAsTaxonomy());
    }

    @Override
    public PeerSTSet getPeerSTSet() throws SharkKBException {
        return new DumpPeerSTSet(this, sharkKB.getPeerSTSet());
    }

    @Override
    public PeerSemanticNet getPeersAsSemanticNet() throws SharkKBException {
        return new DumpPeerSemanticNet(this, sharkKB.getPeersAsSemanticNet());
    }

    @Override
    public PeerTaxonomy getPeersAsTaxonomy() throws SharkKBException {
        return new DumpPeerTaxonomy(this, sharkKB.getPeersAsTaxonomy());
    }

    @Override
    public TimeSTSet getTimeSTSet() throws SharkKBException {
        return new DumpTimeSTSet(this, sharkKB.getTimeSTSet());
    }

    @Override
    public SpatialSTSet getSpatialSTSet() throws SharkKBException {
        return new DumpSpatialSTSet(this, sharkKB.getSpatialSTSet());
    }

    @Override
    public ASIPInterest contextualize(ASIPSpace as) throws SharkKBException {
        ASIPInterest contextualize = sharkKB.contextualize(as);
        this.persist();
        return new DumpASIPInterest(this, contextualize);
    }

    @Override
    public ASIPInterest contextualize(ASIPSpace as, FPSet fps) throws SharkKBException {
        ASIPInterest contextualize = sharkKB.contextualize(as, fps);
        this.persist();
        return new DumpASIPInterest(this, contextualize);
    }

    @Override
    public ASIPInformationSpace mergeInformation(Iterator<ASIPInformation> information, ASIPSpace space) throws SharkKBException {
        ASIPInformationSpace asipInformationSpace = sharkKB.mergeInformation(information, space);
        this.persist();
        return new DumpASIPInformationSpace(this, asipInformationSpace);
    }

    @Override
    public ASIPInformation addInformation(byte[] content, ASIPSpace semanticAnnotations) throws SharkKBException {
        ASIPInformation asipInformation = sharkKB.addInformation(content, semanticAnnotations);
        this.persist();
        return new DumpASIPInformation(this, asipInformation);
    }

    @Override
    public ASIPInformation addInformation(InputStream contentIS, int numberOfBytes, ASIPSpace semanticAnnotations) throws SharkKBException {
        ASIPInformation asipInformation = sharkKB.addInformation(contentIS, numberOfBytes, semanticAnnotations);
        this.persist();
        return new DumpASIPInformation(this, asipInformation);
    }

    @Override
    public ASIPInformation addInformation(String content, ASIPSpace semanticAnnotations) throws SharkKBException {
        ASIPInformation asipInformation = sharkKB.addInformation(content, semanticAnnotations);
        this.persist();
        return new DumpASIPInformation(this, asipInformation);
    }

    @Override
    public ASIPInformation addInformation(String name, String content, ASIPSpace semanticAnnotations) throws SharkKBException {
        ASIPInformation asipInformation = sharkKB.addInformation(name, content, semanticAnnotations);
        this.persist();
        return new DumpASIPInformation(this, asipInformation);
    }

    @Override
    public ASIPInformation addInformation(String name, byte[] content, ASIPSpace semanticAnnotations) throws SharkKBException {
        ASIPInformation asipInformation = sharkKB.addInformation(name, content, semanticAnnotations);
        this.persist();
        return new DumpASIPInformation(this, asipInformation);
    }

    @Override
    public ASIPInformation addInformation(String name, InputStream contentIS, int numberOfBytes, ASIPSpace semanticAnnotations) throws SharkKBException {
        ASIPInformation asipInformation = sharkKB.addInformation(name, contentIS, numberOfBytes, semanticAnnotations);
        this.persist();
        return new DumpASIPInformation(this, asipInformation);
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
            list.add(new DumpASIPInformation(this, informations.next()));
        }
        return list.iterator();
    }

    @Override
    public Iterator<ASIPInformation> getInformation(ASIPSpace infoSpace, boolean fullyInside, boolean matchAny) throws SharkKBException {
        Iterator<ASIPInformation> informations = sharkKB.getInformation(infoSpace, fullyInside, matchAny);
        ArrayList<ASIPInformation> list = new ArrayList<>();
        while (informations.hasNext()){
            list.add(new DumpASIPInformation(this, informations.next()));
        }
        return list.iterator();
    }

    @Override
    public Iterator<ASIPInformationSpace> informationSpaces() throws SharkKBException {
        Iterator<ASIPInformationSpace> asipInformationSpaceIterator = sharkKB.informationSpaces();
        ArrayList<ASIPInformationSpace> list = new ArrayList<>();
        while (asipInformationSpaceIterator.hasNext()){
            list.add(new DumpASIPInformationSpace(this, asipInformationSpaceIterator.next()));
        }
        return list.iterator();
    }

    @Override
    public Iterator<ASIPInformationSpace> getInformationSpaces(ASIPSpace space) throws SharkKBException {
        Iterator<ASIPInformationSpace> asipInformationSpaceIterator = sharkKB.getInformationSpaces(space);
        ArrayList<ASIPInformationSpace> list = new ArrayList<>();
        while (asipInformationSpaceIterator.hasNext()){
            list.add(new DumpASIPInformationSpace(this, asipInformationSpaceIterator.next()));
        }
        return list.iterator();
    }

    @Override
    public void removeInformationSpace(ASIPSpace space) throws SharkKBException {
        sharkKB.removeInformationSpace(space);
        this.persist();
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
        return new DumpSharkVocabulary(this, sharkKB.getVocabulary());
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
