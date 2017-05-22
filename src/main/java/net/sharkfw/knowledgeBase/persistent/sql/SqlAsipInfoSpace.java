package net.sharkfw.knowledgeBase.persistent.sql;

import net.sharkfw.asip.ASIPInformation;
import net.sharkfw.asip.ASIPInformationSpace;
import net.sharkfw.asip.ASIPSpace;
import net.sharkfw.knowledgeBase.SharkKBException;

import java.util.Enumeration;
import java.util.Iterator;

public class SqlAsipInfoSpace implements ASIPInformationSpace {
    @Override
    public void setSystemProperty(String name, String value) {

    }

    @Override
    public String getSystemProperty(String name) {
        return null;
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

    @Override
    public ASIPSpace getASIPSpace() throws SharkKBException {
        return null;
    }

    @Override
    public int numberOfInformations() {
        return 0;
    }

    @Override
    public Iterator<ASIPInformation> informations() throws SharkKBException {
        return null;
    }
}
