package net.sharkfw.knowledgeBase.inmemory;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import net.sharkfw.asip.ASIPInformation;
import net.sharkfw.asip.ASIPInformationSpace;
import net.sharkfw.asip.ASIPInterest;
import net.sharkfw.asip.ASIPSpace;
import net.sharkfw.knowledgeBase.*;

/**
 *
 * @author j4rvis
 */
public class InMemoASIPKnowledge implements Knowledge {
    private ArrayList<ASIPInformationSpace> infoSpacesList;
    private SharkVocabulary cm = null;


    public InMemoASIPKnowledge() {
        this.infoSpacesList = new ArrayList<>();
    }

    public InMemoASIPKnowledge(SharkVocabulary background) {
        this();
        this.cm = background;
    }

    public ASIPInformationSpace addInformationSpace(ASIPSpace space) throws SharkKBException {
        InMemoInformationSpace infoSpace = new InMemoInformationSpace(space);
        infoSpacesList.add(infoSpace);
        return infoSpace;
    }

    private void addInfoToInformationSpace(Information info, ASIPSpace space) throws SharkKBException {
        InMemoInformationSpace infoSpace = this.createInformationSpace(space);
        infoSpace.addInformation(info);
    }

    private InMemoInformationSpace createInformationSpace(ASIPSpace space) throws SharkKBException {
        Iterator<ASIPInformationSpace> infoIter = this.informationSpaces();
        while(infoIter.hasNext()){
            ASIPInformationSpace current = infoIter.next();
            if(SharkCSAlgebra.identical(current.getASIPSpace(), space)){
//                current.setProperty(SyncKB.TIME_PROPERTY_NAME, String.valueOf(System.currentTimeMillis()), true);
                return (InMemoInformationSpace) current;
            }
        }
        ASIPInformationSpace infoSpace = new InMemoInformationSpace(space);
//        infoSpace.setProperty(SyncKB.TIME_PROPERTY_NAME, String.valueOf(System.currentTimeMillis()), true);
        infoSpacesList.add(infoSpace);
        return (InMemoInformationSpace) infoSpace;
    }


    @Override
    public void removeInformation(ASIPSpace space) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int getNumberInformation() throws SharkKBException {
        int infoCount = 0;
        Iterator<ASIPInformationSpace> spaceIter = this.infoSpacesList.iterator();
        
        while(spaceIter.hasNext()) {
            ASIPInformationSpace space = spaceIter.next();
            Iterator<ASIPInformation> infoIter = space.informations();
            if(infoIter != null) {
                while(infoIter.hasNext()) {
                    infoIter.next();
                    infoCount++;
                }
            }
        }
        
        return infoCount;
    }

    public ASIPInformationSpace getInformationSpace(ASIPSpace space) throws SharkKBException {
        while(informationSpaces().hasNext()){
            ASIPInformationSpace current = informationSpaces().next();
            if(SharkCSAlgebra.identical(current.getASIPSpace(), space)){
                return current;
            }
        }
        return null;
    }

    @Override
    public Iterator<ASIPInformationSpace> informationSpaces() {
        return this.infoSpacesList.iterator();
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
//            this.infoList.add(info);

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
        
        Iterator<ASIPInformation> infoIter = this.getInformationSpace(infoSpace).informations();
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
    public void addContextPoint(ContextPoint cp) {

    }

    @Override
    public void removeContextPoint(ContextPoint cp) {

    }

    @Override
    public Enumeration<ContextPoint> contextPoints() {
        return null;
    }

    @Override
    public SharkVocabulary getVocabulary() {
        return this.cm;
    }

    @Override
    public int getNumberOfContextPoints() {
        return 0;
    }

    @Override
    public ContextPoint getCP(int i) {
        return null;
    }

    @Override
    public void addListener(KnowledgeListener kListener) {

    }

    @Override
    public void removeListener(KnowledgeListener kListener) {

    }

    @Override
    public ASIPInformation addInformation(byte[] content, 
            ASIPSpace semanticAnnotations) throws SharkKBException {
        
        InMemoInformation info = new InMemoInformation(semanticAnnotations);
        info.setContent(content);
        this.addInfoToInformationSpace(info, semanticAnnotations);
        
        return info;
    }

    @Override
    public ASIPInformation addInformation(InputStream contentIS, 
            int numberOfBytes, ASIPSpace semanticAnnotations) 
            throws SharkKBException {
        
        InMemoInformation info = new InMemoInformation(semanticAnnotations);
        info.setContent(contentIS, numberOfBytes);
        this.addInfoToInformationSpace(info, semanticAnnotations);
        
        return info;
    }

    @Override
    public ASIPInformation addInformation(String content, 
            ASIPSpace semanticAnnotations) throws SharkKBException {
        
        InMemoInformation info = new InMemoInformation(semanticAnnotations);
        info.setContent(content);
        this.addInfoToInformationSpace(info, semanticAnnotations);
        
        return info;
    }
}
