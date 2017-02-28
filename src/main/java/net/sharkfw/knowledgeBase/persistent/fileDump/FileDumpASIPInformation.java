package net.sharkfw.knowledgeBase.persistent.fileDump;

import net.sharkfw.asip.ASIPInformation;
import net.sharkfw.asip.ASIPSpace;
import net.sharkfw.knowledgeBase.SharkKBException;
import net.sharkfw.knowledgeBase.SystemPropertyHolder;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by j4rvis on 2/28/17.
 */
public class FileDumpASIPInformation extends FileDumpPropertyHolder implements ASIPInformation {

    private final ASIPInformation info;

    public FileDumpASIPInformation(FileDumpSharkKB fileDumpSharkKB, ASIPInformation info) {
        super(fileDumpSharkKB, info);
        this.info = info;
    }

    @Override
    public ASIPSpace getASIPSpace() throws SharkKBException {
        return new FileDumpASIPSpace(kb, info.getASIPSpace());
    }

    @Override
    public long lastModified() {
        return info.lastModified();
    }

    @Override
    public long creationTime() {
        return info.creationTime();
    }

    @Override
    public void setContent(InputStream is, long len) {
        info.setContent(is, len);
        kb.persist();
    }

    @Override
    public void setContent(byte[] content) {
        info.setContent(content);
        kb.persist();
    }

    @Override
    public void setContent(String content) {
        info.setContent(content);
        kb.persist();
    }

    @Override
    public void removeContent() {
        info.removeContent();
        kb.persist();
    }

    @Override
    public void setContentType(String mimetype) {
        info.setContentType(mimetype);
        kb.persist();
    }

    @Override
    public String getContentType() {
        return info.getContentType();
    }

    @Override
    public byte[] getContentAsByte() {
        return info.getContentAsByte();
    }

    @Override
    public void streamContent(OutputStream os) {
        info.streamContent(os);
    }

    @Override
    public long getContentLength() {
        return info.getContentLength();
    }

    @Override
    public String getName() {
        return info.getName();
    }

    @Override
    public String getContentAsString() throws SharkKBException {
        return info.getContentAsString();
    }

    @Override
    public void setName(String name) throws SharkKBException {
        info.setName(name);
        kb.persist();
    }
}
