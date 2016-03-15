package net.sharkfw.knowledgeBase.inmemory;

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import net.sharkfw.asip.ASIPInformation;
import net.sharkfw.asip.ASIPInformationSpace;
import net.sharkfw.asip.ASIPInterest;
import net.sharkfw.asip.ASIPKnowledge;
import net.sharkfw.asip.ASIPSpace;
import net.sharkfw.knowledgeBase.FPSet;
import net.sharkfw.knowledgeBase.Information;
import net.sharkfw.knowledgeBase.SharkAlgebra;
import net.sharkfw.knowledgeBase.SharkKBException;
import net.sharkfw.knowledgeBase.SharkVocabulary;

/**
 *
 * @author msc
 */
public class InMemoASIPKnowledge implements ASIPKnowledge {
    private ArrayList<ASIPInformation> infoList;
    private SharkVocabulary cm;

    protected InMemoASIPKnowledge() {
        this.infoList = new ArrayList<>();
    }
    
    public InMemoASIPKnowledge(SharkVocabulary background) {
        this();
        this.cm = background;
    }
    
    InMemoASIPKnowledge(SharkVocabulary cm, InMemoASIPKnowledge k) {
        this.cm = cm;
//        this.informationSpaces = k.getInformationSpaces();
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
//        return this.informationSpaces.iterator();
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public ASIPInformationSpace mergeInformation(Iterator<ASIPInformation> infos, ASIPSpace space) throws SharkKBException {
        if(infos == null || !infos.hasNext()) throw new SharkKBException("info list must no be null");

        ASIPInformationSpace isp = null;
        
        do {
            ASIPInformation info = infos.next();
            
            // copy information object
            InMemoInformation infoCopy = new InMemoInformation();
            infoCopy.setContent(info.getContentAsByte());
            
            // add to internal lists
            this.infoList.add(info);
            
            // TODO: gi ahead here..

        } while(infos.hasNext());
        
        return isp;
    }
    
    @Override
    public void removeInformation(Information info, ASIPSpace infoSpace) throws SharkKBException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Iterator<ASIPInformation> getInformation(ASIPSpace infoSpace) throws SharkKBException {
        return this.getInformation(infoSpace, false, true);
    }
    
    @Override
    public Iterator<ASIPInformation> getInformation(ASIPSpace infoSpace, boolean fullyInside, boolean matchAny) throws SharkKBException {
        // iterate information and see what space fits..
        
        List<ASIPInformation> result = new ArrayList<>();
        
        Iterator<ASIPInformation> infoIter = this.infoList.iterator();
        while(infoIter.hasNext()) {
            ASIPInformation info = infoIter.next();
            ASIPSpace asipSpace = info.getASIPSpace();
            
            ASIPInterest mutualInterest = InMemoSharkKB.createInMemoASIPInterest(); // just a container
        
            if(SharkAlgebra.contextualize(mutualInterest, 
                    asipSpace, infoSpace, FPSet.getZeroFPSet())) {
                
                // they have something in common - add info
                result.add(info);
            }
        }
        
        return result.iterator();
    }

    @Override
    public SharkVocabulary getVocabulary() {
        return this.cm;
    }

    private List<ASIPInformationSpace> getInformationSpaces() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
//        return this.informationSpaces;
    }

    private ArrayList<ASIPInformation> getInfoList() {
        return this.infoList;
    }
    
    private void addInfo(Information i) {
        this.infoList.add(i);
    }

    @Override
    public ASIPInformation addInformation(byte[] content, 
            ASIPSpace semanticAnnotations) throws SharkKBException {
        
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public ASIPInformation addInformation(OutputStream contentOS, 
            int numberOfBytes, ASIPSpace semanticAnnotations) 
            throws SharkKBException {
        
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public ASIPInformation addInformation(String content, 
            ASIPSpace semanticAnnotations) throws SharkKBException {
        
        InMemoInformation info = new InMemoInformation(semanticAnnotations);
        info.setContent(content);
        this.addInfo(info);
        
        return info;
    }
}
