package net.sharkfw.knowledgeBase.sync;

import net.sharkfw.asip.ASIPInformation;
import net.sharkfw.asip.ASIPInformationSpace;
import net.sharkfw.asip.ASIPSpace;
import net.sharkfw.knowledgeBase.InformationListener;
import net.sharkfw.knowledgeBase.SharkKBException;
import net.sharkfw.knowledgeBase.inmemory.InMemoInformationSpace;

import java.util.Enumeration;
import java.util.Iterator;

/**
 * Created by j4rvis on 19.07.16.
 * author thsc42
 */
class SyncInformationSpace extends SyncPropertyHolder implements ASIPInformationSpace {

    SyncInformationSpace(ASIPInformationSpace target) {
        super(target);
    }

    protected SyncInformationSpace getTarget() {
        return (SyncInformationSpace) super.getTarget();
    }

    @Override
    public ASIPSpace getASIPSpace() throws SharkKBException {
        return this.getTarget().getASIPSpace();
    }

    @Override
    public int numberOfInformations() {
        return this.getTarget().numberOfInformations();
    }

    @Override
    public Iterator<ASIPInformation> informations() throws SharkKBException {
        return this.getTarget().informations();
    }
}
