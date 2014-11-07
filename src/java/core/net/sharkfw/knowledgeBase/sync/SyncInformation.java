package net.sharkfw.knowledgeBase.sync;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;

import net.sharkfw.knowledgeBase.Information;
import net.sharkfw.knowledgeBase.SharkKBException;

public class SyncInformation implements Information{
	
	private Information _localInformation;
	public static String VERSION_PROPERTY_NAME = "SyncI_version";
	public static String VERSION_DEFAULT_VALUE = "1";
	
	public SyncInformation(Information i){
		_localInformation = i;
		if(_localInformation.getProperty(VERSION_PROPERTY_NAME) == null)
			_localInformation.setProperty(VERSION_PROPERTY_NAME, VERSION_DEFAULT_VALUE);
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
	public void setProperty(String name, String value) {
		_localInformation.setProperty(name, value);
	}

	@Override
	public String getProperty(String name) {
		return _localInformation.getProperty(name);
	}

	@Override
	public void setProperty(String name, String value, boolean transfer) {
		_localInformation.setProperty(name, value, transfer);
	}

	@Override
	public void removeProperty(String name) {
		_localInformation.removeProperty(name);
		
	}

	@Override
	public Enumeration<String> propertyNames() {
		return _localInformation.propertyNames();
	}

	@Override
	public Enumeration<String> propertyNames(boolean all) {
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
		int version;
		_localInformation.setContent(is, len);
		try{
//			version = Integer.parseUnsignedInt(_localInformation.getProperty(VERSION_PROPERTY_NAME));
		}catch(NumberFormatException e){
			// TODO ?
			version = 1;
		}
//		version++;
//		_localInformation.setProperty(VERSION_PROPERTY_NAME,Integer.toString(version));
	}

	@Override
	public void setContent(byte[] content) {
		_localInformation.setContent(content);
		versionUp();
	}

	@Override
	public void setContent(String content) {
		_localInformation.setContent(content);
		versionUp();
	}

	@Override
	public void removeContent() {
		_localInformation.removeContent();
		versionUp();
	}

	@Override
	public void setContentType(String mimetype) {
		_localInformation.setContentType(mimetype);
		versionUp();
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

	@Override
	public void obtainLock(InputStream i) {
		_localInformation.obtainLock(i);
	}

	@Override
	public void obtainLock(OutputStream o) {
		_localInformation.obtainLock(o);
	}

	@Override
	public void releaseLock() {
		_localInformation.releaseLock();
	}
	
	private void versionUp() {
		int version = 1;
		try{
//			version = Integer.parseUnsignedInt(_localInformation.getProperty(VERSION_PROPERTY_NAME));
		}catch(NumberFormatException e){
			// TODO ?
		}
		version++;
		_localInformation.setProperty(VERSION_PROPERTY_NAME,Integer.toString(version));
	}
}
