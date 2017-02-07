package net.sharkfw.knowledgeBase.persistent.dump;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.sharkfw.asip.ASIPInformation;
import net.sharkfw.asip.ASIPInformationSpace;
import net.sharkfw.asip.ASIPInterest;
import net.sharkfw.asip.ASIPSpace;
import net.sharkfw.asip.serialization.ASIPKnowledgeConverter;
import net.sharkfw.asip.serialization.ASIPMessageSerializerHelper;
import net.sharkfw.knowledgeBase.Knowledge;
import net.sharkfw.knowledgeBase.PeerSTSet;
import net.sharkfw.knowledgeBase.PeerSemanticNet;
import net.sharkfw.knowledgeBase.PeerTaxonomy;
import net.sharkfw.knowledgeBase.STSet;
import net.sharkfw.knowledgeBase.SemanticNet;
import net.sharkfw.knowledgeBase.SharkKBException;
import net.sharkfw.knowledgeBase.SpatialSTSet;
import net.sharkfw.knowledgeBase.Taxonomy;
import net.sharkfw.knowledgeBase.TimeSTSet;
import net.sharkfw.knowledgeBase.inmemory.InMemoSharkKB;
import net.sharkfw.knowledgeBase.persistent.PersistentSharkKB;
import org.json.JSONArray;

/**
 *
 * @author thsc
 */
public abstract class DumpPersistentSharkKB extends PersistentSharkKB {

    @Override
    public void storageSaveAll(InMemoSharkKB inmemoKB) throws SharkKBException {
        this.storageSaveKnowledge(inmemoKB);
        this.storageSaveProperties(this, inmemoKB);
        
        this.storageSaveDimension(inmemoKB, ASIPSpace.DIM_TOPIC);
        this.storageSaveDimension(inmemoKB, ASIPSpace.DIM_TYPE);
        this.storageSaveDimension(inmemoKB, ASIPSpace.DIM_APPROVERS);
        this.storageSaveDimension(inmemoKB, ASIPSpace.DIM_SENDER);
        this.storageSaveDimension(inmemoKB, ASIPSpace.DIM_RECEIVER);
        this.storageSaveDimension(inmemoKB, ASIPSpace.DIM_LOCATION);
        this.storageSaveDimension(inmemoKB, ASIPSpace.DIM_TIME);
    }
    
    void storageSaveAll() {
        try {
            this.storageSaveAll(this.getInMemoSharkKB());
        } catch (SharkKBException ex) {
            // TODO
        }
    }
    
    @Override
    public void storageRestoreAll(InMemoSharkKB inmemoKB) throws SharkKBException {
        this.storageRestoreKnowledge(inmemoKB);
        this.storageRestoreProperties(inmemoKB);
        
        this.storageRestoreDimension(inmemoKB, ASIPSpace.DIM_TOPIC);
        this.storageRestoreDimension(inmemoKB, ASIPSpace.DIM_TYPE);
        this.storageRestoreDimension(inmemoKB, ASIPSpace.DIM_APPROVERS);
        this.storageRestoreDimension(inmemoKB, ASIPSpace.DIM_SENDER);
        this.storageRestoreDimension(inmemoKB, ASIPSpace.DIM_RECEIVER);
        this.storageRestoreDimension(inmemoKB, ASIPSpace.DIM_LOCATION);
        this.storageRestoreDimension(inmemoKB, ASIPSpace.DIM_TIME);
    }
    
    public void storageRestoreKnowledge(InMemoSharkKB inmemoKB) throws SharkKBException {
        String knowledgeString = this.readKnowledge(this);
        // TODO: rewrite knowledge into knowledge base
    }
    
    public void storageSaveKnowledge(InMemoSharkKB inmemoKB) throws SharkKBException {
        ASIPKnowledgeConverter converter = new ASIPKnowledgeConverter(inmemoKB);
        this.writeKnowledge(this, converter.getSerializedKnowledge());
    }

    void storageSaveKnowledge() {
        try {
            this.storageSaveKnowledge(this.getInMemoSharkKB());
        } catch (SharkKBException ex) {
            // TODO
        }
    }
    
    @Override
    public void storageSaveProperties(PersistentSharkKB persistentKB, InMemoSharkKB inmemoKB) throws SharkKBException {
        JSONArray serializeProperties = ASIPMessageSerializerHelper.serializeProperties(inmemoKB);
        
        // create string
        this.writeProperties(this, serializeProperties.toString());
    }

    void storageSaveProperties() {
        try {
            this.storageSaveProperties(this, this.getInMemoSharkKB());
        } catch (SharkKBException ex) {
            // TODO
        }
    }

    @Override
    public void storageRestoreProperties(InMemoSharkKB inmemoKB) throws SharkKBException {
        String serializedProps = this.readProperties(this);
        
        // TODO: re-set properties in knowledge base
    }
    
    public void storageRestoreDimension(InMemoSharkKB inmemoKB, int dimension) throws SharkKBException {
        // TODO
    }

    public void storageSaveDimension(InMemoSharkKB inmemoKB, int dimension) throws SharkKBException {
        // TODO
    }
    
    void storageSaveDimension(int dimension) {
        try {
            this.storageSaveDimension(this.getInMemoSharkKB(), dimension);
        } catch (SharkKBException ex) {
            // TODO
        }
    }

    @Override
    public ArrayList<ASIPSpace> wrapASIPSpaceList(PersistentSharkKB persistentKB, ArrayList<ASIPSpace> list) {
        if(list == null || list.isEmpty()) return list;
        
        ArrayList<ASIPSpace> wrappedList = new ArrayList<>();
        for(ASIPSpace space : list) {
            wrappedList.add(new DumpASIPSpace(this, space));
        }
        
        return wrappedList;
    }

    @Override
    public Knowledge wrapKnowledge(PersistentSharkKB persistentKB, Knowledge extract) {
        if(extract == null) return null;
        
        return new DumpKnowledge(this, extract);
    }

    @Override
    public ASIPInformationSpace wrapASIPInformationSpace(PersistentSharkKB persistentKB, ASIPInformationSpace space) {
        if(space == null) return null;
        
        return new DumpASIPInformationSpace(this, space);
    }

    @Override
    public ASIPSpace wrapASIPSpace(PersistentSharkKB persistentKB, ASIPSpace space) {
        if(space == null) return null;
        
        return new DumpASIPSpace(this, space);
    }

    @Override
    public ASIPInterest wrapASIPInterest(PersistentSharkKB persistentKB, ASIPInterest interest) {
        if(interest == null) return null;
        
        return new DumpASIPInterest(this, interest);
    }

    @Override
    public ASIPInformation wrapASIPInformation(PersistentSharkKB persistentKB, ASIPInformation information) {
        if(information == null) return null;
        
        return new DumpASIPInformation(this, information);
    }

    @Override
    public STSet wrapSTSet(PersistentSharkKB persistentKB, STSet stSet) {
        if(stSet == null) return null;
        
        return new DumpSTSet(this, stSet);
    }

    @Override
    public SemanticNet wrapSemanticNet(PersistentSharkKB persistentKB, SemanticNet semanticNet) {
        if(semanticNet == null) return null;
        
        return new DumpSemanticNet(this, semanticNet);
    }

    @Override
    public Taxonomy wrapTaxonomy(PersistentSharkKB persistentKB, Taxonomy taxonomy) {
        if(taxonomy == null) return null;
        
        return new DumpTaxonomy(this, taxonomy);
    }

    @Override
    public STSet wrapSTSet(PersistentSharkKB persistentKB, STSet stSet, int dim) {
        return this.wrapSTSet(persistentKB, stSet);
    }

    @Override
    public SemanticNet wrapSemanticNet(PersistentSharkKB persistentKB, SemanticNet semanticNet, int dim) {
        return this.wrapSemanticNet(persistentKB, semanticNet);
    }

    @Override
    public Taxonomy wrapTaxonomy(PersistentSharkKB persistentKB, Taxonomy taxonomy, int dim) {
        return this.wrapTaxonomy(persistentKB, taxonomy);
    }

    @Override
    public PeerSTSet wrapPeerSTSet(PersistentSharkKB persistentKB, PeerSTSet peerSTSet) {
        if(peerSTSet == null) return null;
        
        return new DumpPeerSTSet(this, peerSTSet);
    }

    @Override
    public PeerSemanticNet wrapPeerSemanticNet(PersistentSharkKB persistentKB, PeerSemanticNet peerSemanticNet) {
        if(peerSemanticNet == null) return null;
        
        return new DumpPeerSemanticNet(this, peerSemanticNet);
    }

    @Override
    public PeerTaxonomy wrapPeerTaxonomy(PersistentSharkKB persistentKB, PeerTaxonomy peerTaxonomy) {
        if(peerTaxonomy == null) return null;
        
        return new DumpPeerTaxonomy(this, peerTaxonomy);
    }

    @Override
    public TimeSTSet wrapTimeSTSet(PersistentSharkKB persistentKB, TimeSTSet timeSTSet) {
        if(timeSTSet == null) return null;
        
        return new DumpTimeSTSet(this, timeSTSet);
    }

    @Override
    public SpatialSTSet wrapSpatialSTSet(PersistentSharkKB persistentKB, SpatialSTSet spatialSTSet) {
        if(spatialSTSet == null) return null;
        
        return new DumpSpatialSTSet(this, spatialSTSet);
    }
    
    ////////////////////////////////////////////////////////////////////////////
    //                  methods that store serialized stuff                   //
    ////////////////////////////////////////////////////////////////////////////
    
    public abstract void writeKnowledge(DumpPersistentSharkKB aThis, String serializedKnowledge) throws SharkKBException;

    public abstract String readKnowledge(DumpPersistentSharkKB aThis) throws SharkKBException;

    public abstract void writeProperties(DumpPersistentSharkKB aThis, String serialized) throws SharkKBException;

    public abstract String readProperties(DumpPersistentSharkKB aThis) throws SharkKBException;
    
    public abstract void writeDimension(DumpPersistentSharkKB aThis, String serialized, int dimension) throws SharkKBException;
    
    public abstract String readDimension(DumpPersistentSharkKB aThis, int dimension) throws SharkKBException;
}
