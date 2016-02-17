/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sharkfw.knowledgeBase.inmemory;

import java.io.InputStream;
import java.util.Iterator;
import net.sharkfw.asip.ASIPInformationSpace;
import net.sharkfw.asip.ASIPKnowledge;
import net.sharkfw.asip.ASIPSpace;
import net.sharkfw.knowledgeBase.SharkKBException;
import net.sharkfw.knowledgeBase.SharkVocabulary;

/**
 *
 * @author msc
 */
public class InMemoASIPKnowledge implements ASIPKnowledge{
    
    private InputStream stream;

    public InMemoASIPKnowledge(InputStream stream) {
        this.stream = stream;
    }

    @Override
    public void addInformationSpace(ASIPInformationSpace space) throws SharkKBException {
    }

    @Override
    public ASIPInformationSpace createInformationSpace(ASIPSpace space) throws SharkKBException {
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
    public int getNumberOfInformationSpaces() throws SharkKBException {
        return 0;
    }
}
