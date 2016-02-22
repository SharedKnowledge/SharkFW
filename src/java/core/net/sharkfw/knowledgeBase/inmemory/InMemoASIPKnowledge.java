package net.sharkfw.knowledgeBase.inmemory;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import net.sharkfw.asip.ASIPInformation;
import net.sharkfw.asip.ASIPInformationSpace;
import net.sharkfw.asip.ASIPKnowledge;
import net.sharkfw.asip.ASIPSpace;
import net.sharkfw.knowledgeBase.Information;
import net.sharkfw.knowledgeBase.SharkKBException;
import net.sharkfw.knowledgeBase.SharkVocabulary;

/**
 *
 * @author msc
 */
public class InMemoASIPKnowledge implements ASIPKnowledge {
    private List<ASIPInformationSpace> informationSpaces;
    private ArrayList<ASIPInformation> infoList;
    private SharkVocabulary cm;

    protected InMemoASIPKnowledge() {
        this.informationSpaces = new ArrayList<>();
        this.infoList = new ArrayList<>();
    }
    
    public InMemoASIPKnowledge(SharkVocabulary background) {
        this();
        this.cm = background;
    }
    
    InMemoASIPKnowledge(SharkVocabulary cm, InMemoASIPKnowledge k) {
        this.cm = cm;
        this.informationSpaces = k.getInformationSpaces();
        this.infoList = k.getInfoList();
    }
    
    /////////////////////////////////////////////////////////////////////////
    //                        information management                       //
    /////////////////////////////////////////////////////////////////////////
    
    @Override
    public void removeInformation(ASIPSpace space) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int getNumberInformation() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Iterator<ASIPInformationSpace> informationSpaces() {
        return this.informationSpaces.iterator();
    }

    @Override
    public ASIPInformationSpace mergeInformation(Iterator<ASIPInformation> infos, ASIPSpace space) throws SharkKBException {
        // TODO
        InMemoInformationSpace newIS = new InMemoInformationSpace(space);
        this.informationSpaces.add(newIS);
        
        return newIS;
    }
    
    ASIPInformationSpace mergeInformation(ASIPInformation info, ASIPSpace space) 
            throws SharkKBException {
        
        return null;
    }
    
    /**
     * Method adds (but does not make a copy) that info object.
     * @param info
     * @param space
     * @return
     * @throws SharkKBException 
     */
    ASIPInformationSpace addInformation(InMemoInformation info, ASIPSpace space) 
            throws SharkKBException {
        
        return null;
    }

    @Override
    public void removeInformation(Information info, ASIPSpace infoSpace) throws SharkKBException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Iterator<ASIPInformation> getInformation(ASIPSpace infoSpace) throws SharkKBException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public SharkVocabulary getVocabulary() {
        return this.cm;
    }

    private List<ASIPInformationSpace> getInformationSpaces() {
        return this.informationSpaces;
    }

    private ArrayList<ASIPInformation> getInfoList() {
        return this.infoList;
    }
}
