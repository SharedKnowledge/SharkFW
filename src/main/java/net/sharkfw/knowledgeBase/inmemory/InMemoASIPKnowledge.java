package net.sharkfw.knowledgeBase.inmemory;

import net.sharkfw.asip.ASIPInformation;
import net.sharkfw.asip.ASIPInformationSpace;
import net.sharkfw.asip.ASIPInterest;
import net.sharkfw.asip.ASIPSpace;
import net.sharkfw.knowledgeBase.*;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

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
        Iterator<ASIPInformationSpace> iterator = informationSpaces();
        while(iterator.hasNext()){
            ASIPInformationSpace current = iterator.next();
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
    public void removeInformation(ASIPInformation info, ASIPSpace infoSpace) throws SharkKBException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Iterator<ASIPInformation> getInformation(ASIPSpace infoSpace) throws SharkKBException {
        return this.getInformation(infoSpace, false, true);
    }

    @Override
    public Iterator<ASIPInformationSpace> getInformationSpaces(ASIPSpace space) throws SharkKBException {
        Iterator<ASIPInformationSpace> informationSpaces = informationSpaces();
        List<ASIPInformationSpace> resultSet = new ArrayList<>();
        while(informationSpaces.hasNext()) {
            ASIPInformationSpace next = informationSpaces.next();

            ASIPSpace asipSpace = next.getASIPSpace();
            ASIPInterest mutualInterest = InMemoSharkKB.createInMemoASIPInterest(); // just a container

            if(SharkAlgebra.contextualize(mutualInterest,
                    asipSpace, space, FPSet.getZeroFPSet())) {

                // they have something in common - add next
                resultSet.add(next);
            }
        }

        return resultSet.iterator();
    }
    @Override
    public Iterator<ASIPInformation> getInformation(ASIPSpace infoSpace, boolean fullyInside, boolean matchAny) throws SharkKBException {
        // iterate information and see what space fits..

        if(this.getInformationSpace(infoSpace)==null){
            return null;
        }

//        return this.getInformationSpace(infoSpace).informations();

        List<ASIPInformation> resultSet = new ArrayList<>();

        Iterator<ASIPInformation> infoIter = this.getInformationSpace(infoSpace).informations();
        while(infoIter.hasNext()) {
            ASIPInformation info = infoIter.next();

            ASIPSpace asipSpace = info.getASIPSpace();
            ASIPInterest mutualInterest = InMemoSharkKB.createInMemoASIPInterest(); // just a container

            if(SharkAlgebra.contextualize(mutualInterest,
                    asipSpace, infoSpace, FPSet.getZeroFPSet())) {

                // they have something in common - add info
                resultSet.add(info);
            }
        }

        return resultSet.iterator();
    }

    @Override
    public SharkVocabulary getVocabulary() {
        return this.cm;
    }

    @Override
    public ASIPInformation addInformation(byte[] content, 
            ASIPSpace semanticAnnotations) throws SharkKBException {
        return this.addInformation(null, content, semanticAnnotations);
    }

    @Override
    public ASIPInformation addInformation(InputStream contentIS, 
            int numberOfBytes, ASIPSpace semanticAnnotations) 
            throws SharkKBException {

        return this.addInformation(null, contentIS, numberOfBytes, semanticAnnotations);
    }

    @Override
    public ASIPInformation addInformation(String name, String content, ASIPSpace semanticAnnotations) throws SharkKBException {
        Iterator<ASIPInformation> information = this.getInformation(semanticAnnotations);
        if(information!=null){
            while (information.hasNext()){
                ASIPInformation next = information.next();

                if((next.getName() == null && name == null) || next.getName().equals(name)){
                    if (next.getContentAsString().equals(content)){
                        return next;
                    }
                }
            }
        }
        InMemoInformation info = new InMemoInformation(semanticAnnotations);
        if(name!=null){
            info.setName(name);
        }
        info.setContent(content);
        this.addInfoToInformationSpace(info, semanticAnnotations);

        return info;
    }

    @Override
    public ASIPInformation addInformation(String name, byte[] content, ASIPSpace semanticAnnotations) throws SharkKBException {
        Iterator<ASIPInformation> information = this.getInformation(semanticAnnotations);
        if(information!=null){
            while (information.hasNext()){
                ASIPInformation next = information.next();
                if((next.getName() == null && name == null) || next.getName().equals(name)){
                    if (Arrays.equals(next.getContentAsByte(), content)){
                        return next;
                    }
                }
            }
        }

        InMemoInformation info = new InMemoInformation(semanticAnnotations);
        if(name!=null){
            info.setName(name);
        }
        info.setContent(content);
        this.addInfoToInformationSpace(info, semanticAnnotations);

        return info;
    }

    @Override
    public ASIPInformation addInformation(String name, InputStream contentIS, int numberOfBytes, ASIPSpace semanticAnnotations) throws SharkKBException {

        // TODO check if information already exists

        InMemoInformation info = new InMemoInformation(semanticAnnotations);
        if(name!=null){
            info.setName(name);
        }
        info.setContent(contentIS, numberOfBytes);
        this.addInfoToInformationSpace(info, semanticAnnotations);

        return info;
    }

    @Override
    public ASIPInformation addInformation(String content, 
            ASIPSpace semanticAnnotations) throws SharkKBException {

        return this.addInformation(null, content, semanticAnnotations);
    }
}
