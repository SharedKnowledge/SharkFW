package net.sharkfw.knowledgeBase.sync;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Vector;

import net.sharkfw.knowledgeBase.ContextCoordinates;
import net.sharkfw.knowledgeBase.ContextPoint;
import net.sharkfw.knowledgeBase.ContextPointListener;
import net.sharkfw.knowledgeBase.Information;

public class SyncContextPoint implements ContextPoint {
	
	private ContextPoint _localCP = null;
	public static String VERSION_PROPERTY_NAME = "SyncCP_version";
	public static String VERSION_DEFAULT_VALUE = "1";
	
	public SyncContextPoint(ContextPoint c){
		_localCP = c;
		if(_localCP.getProperty(VERSION_PROPERTY_NAME) == null)
			_localCP.setProperty(VERSION_PROPERTY_NAME, VERSION_DEFAULT_VALUE);
	}

	@Override
	public void setSystemProperty(String name, String value) {
		_localCP.setSystemProperty(name, value);		
	}

	@Override
	public String getSystemProperty(String name) {
		return _localCP.getSystemProperty(name);
	}

	@Override
	public void setProperty(String name, String value) {
		_localCP.setProperty(name, value);
	}

	@Override
	public String getProperty(String name) {
		return _localCP.getProperty(name);
	}

	@Override
	public void setProperty(String name, String value, boolean transfer) {
		_localCP.setProperty(name, value);
	}

	@Override
	public void removeProperty(String name) {
		_localCP.removeProperty(name);
	}

	@Override
	public Enumeration<String> propertyNames() {
		return _localCP.propertyNames();
	}

	@Override
	public Enumeration<String> propertyNames(boolean all) {
		return _localCP.propertyNames(all);
	}

	@Override
	public Information addInformation() {
		Information i = _localCP.addInformation();
		versionUp();
		return new SyncInformation(i);
	}

	@Override
	public void addInformation(Information source) {
		versionUp();
		_localCP.addInformation(source);
	}

	@Override
	public Information addInformation(InputStream is, long len) {
		Information i =  _localCP.addInformation(is, len);
		versionUp();
		return new SyncInformation(i);
	}

	@Override
	public Information addInformation(byte[] content) {
		Information i = _localCP.addInformation(content);
		versionUp();
		return new SyncInformation(i);
	}

	@Override
	public Information addInformation(String content) {
		Information i = _localCP.addInformation(content);
		versionUp();
		return new SyncInformation(i);
	}

	@Override
	public Enumeration<Information> enumInformation() {
		Enumeration<Information> infos = _localCP.enumInformation();
		Vector<Information> temp = new Vector<Information>();
		while(infos.hasMoreElements()){
			temp.addElement(new SyncInformation(infos.nextElement()));
		}
		return temp.elements();
	}

	@Override
	public Iterator<Information> getInformation(String name) {
		Iterator<Information> infos = _localCP.getInformation(name);
		Collection<Information> temp = new ArrayList<Information>();
		while(infos.hasNext()){
			temp.add(new SyncInformation(infos.next()));
		}
		return temp.iterator();
	}

	@Override
	public Iterator<Information> getInformation() {
		Iterator<Information> infos = _localCP.getInformation();
		Collection<Information> temp = new ArrayList<Information>();
		while(infos.hasNext()){
			temp.add(new SyncInformation(infos.next()));
		}
		return temp.iterator();
	}

	@Override
	public void removeInformation(Information toDelete) {
		versionUp();
		_localCP.removeInformation(toDelete);
	}

	@Override
	public ContextCoordinates getContextCoordinates() {
		return _localCP.getContextCoordinates();
	}

	@Override
	// TODO Should this be versionized?
	// TODO Set anyway, even if its the same?
	public void setContextCoordinates(ContextCoordinates cc) {
		if(cc != _localCP.getContextCoordinates()){
			_localCP.setContextCoordinates(cc);
			_localCP.setProperty(VERSION_PROPERTY_NAME, VERSION_DEFAULT_VALUE);
		}
	}

	@Override
	public int getNumberInformation() {
		return _localCP.getNumberInformation();
	}

	@Override
	public void setListener(ContextPointListener cpl) {
		_localCP.setListener(cpl);
	}

	@Override
	public void removeListener() {
		_localCP.removeListener();
	}
	
	public void versionUp() {
		int version = 1;
		try{
			version = Integer.parseUnsignedInt(_localCP.getProperty(VERSION_PROPERTY_NAME));
		}catch(NumberFormatException e){
			// TODO: ?
		}
		version++;
		_localCP.setProperty(VERSION_PROPERTY_NAME, Integer.toString(version));
	}

}
