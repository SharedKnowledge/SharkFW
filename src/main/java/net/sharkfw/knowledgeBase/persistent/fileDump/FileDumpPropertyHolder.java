package net.sharkfw.knowledgeBase.persistent.fileDump;

import net.sharkfw.knowledgeBase.SharkKBException;
import net.sharkfw.knowledgeBase.SystemPropertyHolder;

import java.util.Enumeration;

/**
 * Created by j4rvis on 2/27/17.
 */
public class FileDumpPropertyHolder implements SystemPropertyHolder {

    private final FileDumpSharkKB kb;
    private final SystemPropertyHolder propertyHolder;

    public FileDumpPropertyHolder(FileDumpSharkKB fileDumpSharkKB, SystemPropertyHolder systemPropertyHolder) {
        this.kb = fileDumpSharkKB;
        this.propertyHolder = systemPropertyHolder;
    }

    @Override
    public void setSystemProperty(String name, String value) {
        this.propertyHolder.setSystemProperty(name, value);
        this.kb.persist();
    }

    @Override
    public String getSystemProperty(String name) {
        return this.propertyHolder.getSystemProperty(name);
    }

    @Override
    public void setProperty(String name, String value) throws SharkKBException {
        this.propertyHolder.setProperty(name, value);
        this.kb.persist();
    }

    @Override
    public String getProperty(String name) throws SharkKBException {
        return this.propertyHolder.getProperty(name);
    }

    @Override
    public void setProperty(String name, String value, boolean transfer) throws SharkKBException {
        this.propertyHolder.setProperty(name, value, transfer);
        this.kb.persist();
    }

    @Override
    public void removeProperty(String name) throws SharkKBException {
        this.propertyHolder.removeProperty(name);
        this.kb.persist();
    }

    @Override
    public Enumeration<String> propertyNames() throws SharkKBException {
        return this.propertyHolder.propertyNames();
    }

    @Override
    public Enumeration<String> propertyNames(boolean all) throws SharkKBException {
        return this.propertyHolder.propertyNames(all);
    }
}
