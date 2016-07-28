package net.sharkfw.knowledgeBase.sync;

import net.sharkfw.asip.ASIPSpace;
import net.sharkfw.knowledgeBase.Information;
import net.sharkfw.knowledgeBase.SharkKBException;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by thsc42 on 28.07.16.
 */
class SyncInformation extends SyncPropertyHolder implements Information {

    SyncInformation(Information target) {
        super(target);
    }

    protected SyncInformation getTarget() {
        return (SyncInformation) super.getTarget();
    }

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
    public OutputStream getOutputStream() throws SharkKBException {
        return null;
    }

    @Override
    public InputStream getInputStream() throws SharkKBException {
        return null;
    }

    @Override
    public String getUniqueID() {
        return null;
    }
}
