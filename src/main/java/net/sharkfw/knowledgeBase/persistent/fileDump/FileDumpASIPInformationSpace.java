package net.sharkfw.knowledgeBase.persistent.fileDump;

import net.sharkfw.asip.ASIPInformation;
import net.sharkfw.asip.ASIPInformationSpace;
import net.sharkfw.asip.ASIPSpace;
import net.sharkfw.knowledgeBase.SharkKBException;
import net.sharkfw.knowledgeBase.SystemPropertyHolder;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by j4rvis on 2/28/17.
 */
public class FileDumpASIPInformationSpace extends FileDumpSystemPropertyHolder implements ASIPInformationSpace {

    private final ASIPInformationSpace informationSpace;

    public FileDumpASIPInformationSpace(FileDumpSharkKB fileDumpSharkKB, ASIPInformationSpace informationSpace) {
        super(fileDumpSharkKB, informationSpace);
        this.informationSpace = informationSpace;
    }

    @Override
    public ASIPSpace getASIPSpace() throws SharkKBException {
        return new FileDumpASIPSpace(kb, informationSpace.getASIPSpace());
    }

    @Override
    public int numberOfInformations() {
        return informationSpace.numberOfInformations();
    }

    @Override
    public Iterator<ASIPInformation> informations() throws SharkKBException {
        Iterator<ASIPInformation> informations = informationSpace.informations();
        ArrayList<ASIPInformation> list = new ArrayList<>();
        while (informations.hasNext()){
            list.add(new FileDumpASIPInformation(kb, informations.next()));
        }
        return list.iterator();
    }
}
