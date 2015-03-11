package net.sharkfw.knowledgeBase.sync;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.sharkfw.knowledgeBase.Information;
import net.sharkfw.knowledgeBase.InformationListener;
import net.sharkfw.knowledgeBase.SharkKBException;
import net.sharkfw.system.L;

/**
 * Implements a synchronized information. Delegates most of its
 * functionality to the internal information.
 * @author hellerve
 */
public class SyncInformation implements Information{
	private final Information _localInformation;
	protected static String VERSION_PROPERTY_NAME = "SyncInformation_internalVersion";
	protected static String VERSION_DEFAULT_VALUE = "1";
	
	public SyncInformation(Information i) {
            _localInformation = i;
            try {
                if(_localInformation.getProperty(VERSION_PROPERTY_NAME) == null)
                    _localInformation.setProperty(VERSION_PROPERTY_NAME, VERSION_DEFAULT_VALUE);
            } catch (SharkKBException ex) {
                // TODO
            }
	}

	@Override
	public void setSystemProperty(String name, String value) {
		_localInformation.setSystemProperty(name, value);
	}

	@Override
	// TODO Should version be accessible by getter and setter
	public String getSystemProperty(String name) {
		return _localInformation.getSystemProperty(name);
	}

	@Override
	public void setProperty(String name, String value) throws SharkKBException {
		_localInformation.setProperty(name, value);
	}

	@Override
	public String getProperty(String name) throws SharkKBException {
		return _localInformation.getProperty(name);
	}

	@Override
	public void setProperty(String name, String value, boolean transfer) throws SharkKBException {
		_localInformation.setProperty(name, value, transfer);
	}

	@Override
	public void removeProperty(String name) throws SharkKBException {
		_localInformation.removeProperty(name);
		
	}

	@Override
	public Enumeration<String> propertyNames() throws SharkKBException {
		return _localInformation.propertyNames();
	}

	@Override
	public Enumeration<String> propertyNames(boolean all) throws SharkKBException {
		return _localInformation.propertyNames(all);
	}

	@Override
	public long lastModified() {
		return _localInformation.lastModified();
	}

	@Override
	public long creationTime() {
		return _localInformation.creationTime();
	}

	@Override
	public void setContent(InputStream is, long len) {
            _localInformation.setContent(is, len);
            this.versionUp();
            notifyContentChanged();
	}

	@Override
	public void setContent(byte[] content) {
            _localInformation.setContent(content);
            versionUp();
            notifyContentChanged();
	}

	@Override
	public void setContent(String content) {
            _localInformation.setContent(content);
            versionUp();
            notifyContentChanged();
	}

	@Override
	public void removeContent() {
            _localInformation.removeContent();
            versionUp();
            notifyContentRemoved();
	}

	@Override
	public void setContentType(String mimetype) {
            _localInformation.setContentType(mimetype);
            versionUp();
            notifyContentTypeChanged();
	}

	@Override
	public String getContentType() {
		return _localInformation.getContentType();
	}

	@Override
	public byte[] getContentAsByte() {
		return _localInformation.getContentAsByte();
	}

	@Override
	public void streamContent(OutputStream os) {
		_localInformation.streamContent(os);
	}

	@Override
	public long getContentLength() {
		return _localInformation.getContentLength();
	}

	@Override
	public OutputStream getOutputStream() throws SharkKBException {
		return _localInformation.getOutputStream();
	}

	@Override
	public InputStream getInputStream() throws SharkKBException {
		return _localInformation.getInputStream();
	}

	@Override
	public String getName() {
		return _localInformation.getName();
	}

	@Override
	public String getContentAsString() throws SharkKBException {
		return _localInformation.getContentAsString();
	}

	@Override
	public void setName(String name) throws SharkKBException {
		_localInformation.setName(name);
	}

	@Override
	public String getUniqueID() {
		return _localInformation.getUniqueID();
	}

	private void versionUp() {
            int oldVersion;
            try {
                oldVersion = Integer.parseInt(_localInformation.getProperty(VERSION_PROPERTY_NAME));
                _localInformation.setProperty(VERSION_PROPERTY_NAME, String.valueOf(oldVersion + 1));
            } catch (SharkKBException ex) {
                L.e("fatal: cannot access properties", this);
            }
	}
        
        /* Listeners */
        private Collection<InformationListener> listeners = new ArrayList<>();
        
        public void addListener(InformationListener l) {
            listeners.add(l);
        }
        public void removeListener(InformationListener l) {
            listeners.remove(l);
        }
        
        private void notifyContentChanged() {
            for (InformationListener l : listeners) {
                l.contentChanged();
            }
        }
        private void notifyContentRemoved() {
            for (InformationListener l : listeners) {
                l.contentRemoved();
            }
        }
        private void notifyContentTypeChanged() {
            for (InformationListener l : listeners) {
                l.contentTypeChanged();
            }
        }
}
