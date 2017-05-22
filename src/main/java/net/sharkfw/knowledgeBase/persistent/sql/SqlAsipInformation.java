package net.sharkfw.knowledgeBase.persistent.sql;

import net.sharkfw.asip.ASIPInformation;
import net.sharkfw.asip.ASIPSpace;
import net.sharkfw.knowledgeBase.SharkKBException;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;

public class SqlAsipInformation implements ASIPInformation {
    @Override
    public ASIPSpace getASIPSpace() throws SharkKBException {
        return null;
    }

    @Override
    public long lastModified() {
        return 0;
    }

    @Override
    public long creationTime() {
        return 0;
    }

    @Override
    public void setContent(InputStream is, long len) {

    }

    @Override
    public void setContent(byte[] content) {

    }

    @Override
    public void setContent(String content) {

    }

    @Override
    public void removeContent() {

    }

    @Override
    public void setContentType(String mimetype) {

    }

    @Override
    public String getContentType() {
        return null;
    }

    @Override
    public byte[] getContentAsByte() {
        return new byte[0];
    }

    @Override
    public void streamContent(OutputStream os) {

    }

    @Override
    public long getContentLength() {
        return 0;
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public String getContentAsString() throws SharkKBException {
        return null;
    }

    @Override
    public void setName(String name) throws SharkKBException {

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
