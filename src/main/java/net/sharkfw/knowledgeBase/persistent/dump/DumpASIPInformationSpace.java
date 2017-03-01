package net.sharkfw.knowledgeBase.persistent.dump;

import net.sharkfw.asip.ASIPInformation;
import net.sharkfw.asip.ASIPInformationSpace;
import net.sharkfw.asip.ASIPSpace;
import net.sharkfw.knowledgeBase.SharkKBException;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by j4rvis on 2/28/17.
 */
public class DumpASIPInformationSpace extends DumpSystemPropertyHolder implements ASIPInformationSpace {

    private final ASIPInformationSpace informationSpace;

    public DumpASIPInformationSpace(DumpSharkKB dumpSharkKB, ASIPInformationSpace informationSpace) {
        super(dumpSharkKB, informationSpace);
        this.informationSpace = informationSpace;
    }

    @Override
    public ASIPSpace getASIPSpace() throws SharkKBException {
        return new DumpASIPSpace(kb, informationSpace.getASIPSpace());
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
            list.add(new DumpASIPInformation(kb, informations.next()));
        }
        return list.iterator();
    }
}
