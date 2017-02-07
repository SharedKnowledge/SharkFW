package net.sharkfw.knowledgeBase.persistent.dump;

import java.util.Enumeration;
import java.util.Iterator;
import net.sharkfw.asip.ASIPInformation;
import net.sharkfw.asip.ASIPInformationSpace;
import net.sharkfw.asip.ASIPSpace;
import net.sharkfw.knowledgeBase.SharkKBException;

/**
 *
 * @author thsc
 */
class DumpASIPInformationSpace extends DumpPropertyHolder implements ASIPInformationSpace {

    public DumpASIPInformationSpace(DumpPersistentSharkKB aThis, ASIPInformationSpace space) {
        super(aThis, space);
    }

    @Override
    public ASIPSpace getASIPSpace() throws SharkKBException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int numberOfInformations() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Iterator<ASIPInformation> informations() throws SharkKBException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
