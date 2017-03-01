package net.sharkfw.knowledgeBase.persistent.dump;

import net.sharkfw.asip.ASIPInformation;
import net.sharkfw.asip.ASIPInformationSpace;
import net.sharkfw.asip.ASIPSpace;
import net.sharkfw.knowledgeBase.Knowledge;
import net.sharkfw.knowledgeBase.SharkKBException;
import net.sharkfw.knowledgeBase.SharkVocabulary;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by j4rvis on 2/28/17.
 */
public class DumpKnowledge implements Knowledge {

    protected final DumpSharkKB kb;
    private final Knowledge knowledge;

    public DumpKnowledge(DumpSharkKB kb, Knowledge knowledge) {
        this.kb = kb;
        this.knowledge = knowledge;
    }

    @Override
    public ASIPInformationSpace mergeInformation(Iterator<ASIPInformation> information, ASIPSpace space) throws SharkKBException {
        ASIPInformationSpace asipInformationSpace = knowledge.mergeInformation(information, space);
        kb.persist();
        return new DumpASIPInformationSpace(kb, asipInformationSpace);
    }

    @Override
    public ASIPInformation addInformation(byte[] content, ASIPSpace semanticAnnotations) throws SharkKBException {
        ASIPInformation asipInformation = knowledge.addInformation(content, semanticAnnotations);
        kb.persist();
        return new DumpASIPInformation(kb, asipInformation);
    }

    @Override
    public ASIPInformation addInformation(InputStream contentIS, int numberOfBytes, ASIPSpace semanticAnnotations) throws SharkKBException {
        ASIPInformation asipInformation = knowledge.addInformation(contentIS, numberOfBytes, semanticAnnotations);
        kb.persist();
        return new DumpASIPInformation(kb, asipInformation);
    }

    @Override
    public ASIPInformation addInformation(String content, ASIPSpace semanticAnnotations) throws SharkKBException {
        ASIPInformation asipInformation = knowledge.addInformation(content, semanticAnnotations);
        kb.persist();
        return new DumpASIPInformation(kb, asipInformation);
    }

    @Override
    public ASIPInformation addInformation(String name, String content, ASIPSpace semanticAnnotations) throws SharkKBException {
        ASIPInformation asipInformation = knowledge.addInformation(name, content, semanticAnnotations);
        kb.persist();
        return new DumpASIPInformation(kb, asipInformation);
    }

    @Override
    public ASIPInformation addInformation(String name, byte[] content, ASIPSpace semanticAnnotations) throws SharkKBException {
        ASIPInformation asipInformation = knowledge.addInformation(name, content, semanticAnnotations);
        kb.persist();
        return new DumpASIPInformation(kb, asipInformation);
    }

    @Override
    public ASIPInformation addInformation(String name, InputStream contentIS, int numberOfBytes, ASIPSpace semanticAnnotations) throws SharkKBException {
        ASIPInformation asipInformation = knowledge.addInformation(name, contentIS, numberOfBytes, semanticAnnotations);
        kb.persist();
        return new DumpASIPInformation(kb, asipInformation);
    }

    @Override
    public void removeInformation(ASIPInformation info, ASIPSpace infoSpace) throws SharkKBException {
        this.knowledge.removeInformation(info, infoSpace);
        kb.persist();
    }

    @Override
    public Iterator<ASIPInformation> getInformation(ASIPSpace infoSpace) throws SharkKBException {
        Iterator<ASIPInformation> informations = knowledge.getInformation(infoSpace);
        ArrayList<ASIPInformation> list = new ArrayList<>();
        while (informations.hasNext()){
            list.add(new DumpASIPInformation(kb, informations.next()));
        }
        return list.iterator();
    }

    @Override
    public Iterator<ASIPInformation> getInformation(ASIPSpace infoSpace, boolean fullyInside, boolean matchAny) throws SharkKBException {
        Iterator<ASIPInformation> informations = knowledge.getInformation(infoSpace, fullyInside, matchAny);
        ArrayList<ASIPInformation> list = new ArrayList<>();
        while (informations.hasNext()){
            list.add(new DumpASIPInformation(kb, informations.next()));
        }
        return list.iterator();
    }

    @Override
    public Iterator<ASIPInformationSpace> informationSpaces() throws SharkKBException {
        Iterator<ASIPInformationSpace> asipInformationSpaceIterator = knowledge.informationSpaces();
        ArrayList<ASIPInformationSpace> list = new ArrayList<>();
        while (asipInformationSpaceIterator.hasNext()){
            list.add(new DumpASIPInformationSpace(kb, asipInformationSpaceIterator.next()));
        }
        return list.iterator();
    }

    @Override
    public Iterator<ASIPInformationSpace> getInformationSpaces(ASIPSpace space) throws SharkKBException {
        Iterator<ASIPInformationSpace> asipInformationSpaceIterator = knowledge.getInformationSpaces(space);
        ArrayList<ASIPInformationSpace> list = new ArrayList<>();
        while (asipInformationSpaceIterator.hasNext()){
            list.add(new DumpASIPInformationSpace(kb, asipInformationSpaceIterator.next()));
        }
        return list.iterator();
    }

    @Override
    public void removeInformation(ASIPSpace space) throws SharkKBException {
        knowledge.removeInformation(space);
        kb.persist();
    }

    @Override
    public SharkVocabulary getVocabulary() {
        return new DumpSharkVocabulary(kb, knowledge.getVocabulary());
    }

    @Override
    public int getNumberInformation() throws SharkKBException {
        return knowledge.getNumberInformation();
    }
}
