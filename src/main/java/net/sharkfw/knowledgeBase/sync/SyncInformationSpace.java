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
 */
public class SyncInformationSpace implements ASIPInformationSpace, InformationListener{

    private ASIPInformationSpace informationSpace;

    public SyncInformationSpace(ASIPInformationSpace space) {
        this.informationSpace = informationSpace;
    }

    @Override
    public void contentChanged() {

    }

    @Override
    public void contentRemoved() {

    }

    @Override
    public void contentTypeChanged() {

    }

    @Override
    public ASIPSpace getASIPSpace() throws SharkKBException {
        return informationSpace.getASIPSpace();
    }

    @Override
    public Iterator<ASIPInformation> informations() throws SharkKBException {
        return informationSpace.informations();
    }

    @Override
    public void setSystemProperty(String name, String value) {
        informationSpace.setSystemProperty(name, value);
    }

    @Override
    public String getSystemProperty(String name) {
        return informationSpace.getSystemProperty(name);
    }

    @Override
    public void setProperty(String name, String value) throws SharkKBException {

    }

    @Override
    public String getProperty(String name) throws SharkKBException {
        return null;
    }

    @Override
    public void setProperty(String name, String value, boolean transfer) throws SharkKBException {

    }

    @Override
    public void removeProperty(String name) throws SharkKBException {

    }

    @Override
    public Enumeration<String> propertyNames() throws SharkKBException {
        return null;
    }

    @Override
    public Enumeration<String> propertyNames(boolean all) throws SharkKBException {
        return null;
    }
}
