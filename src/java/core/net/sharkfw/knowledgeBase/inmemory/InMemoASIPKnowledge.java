package net.sharkfw.knowledgeBase.inmemory;

import java.io.InputStream;
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
    
    private InputStream stream;

    protected InMemoASIPKnowledge() {}
    
    public InMemoASIPKnowledge(InputStream stream) {
        this.stream = stream;
    }

    @Override
    public ASIPInformationSpace mergeInformation(Iterator<ASIPInformation> infos,
            ASIPSpace space) throws SharkKBException {
        return null;
    }

    @Override
    public void removeInformationSpace(ASIPSpace space) throws SharkKBException {
    }

    @Override
    public Iterator<ASIPInformationSpace> informationSpaces() throws SharkKBException {
        return null;
    }

    @Override
    public SharkVocabulary getVocabulary() throws SharkKBException {
        return null;
    }

    @Override
    public int getNumberInformation() throws SharkKBException {
        return 0;
    }

    @Override
    public ASIPInformationSpace addInformation(List<ASIPInformation> information, ASIPSpace space) throws SharkKBException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void removeInformation(Information info, ASIPSpace infoSpace) throws SharkKBException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Iterator<Information> getInformation(ASIPSpace infoSpace) throws SharkKBException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
