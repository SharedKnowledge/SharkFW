package net.sharkfw.knowledgeBase.sync;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Vector;
import net.sharkfw.knowledgeBase.ContextCoordinates;
import net.sharkfw.knowledgeBase.ContextPoint;
import net.sharkfw.knowledgeBase.ContextPointListener;
import net.sharkfw.knowledgeBase.Information;
import net.sharkfw.knowledgeBase.InformationListener;
import net.sharkfw.knowledgeBase.inmemory.InMemoSharkKB;

public class SyncContextPoint implements ContextPoint, InformationListener {
	
	private ContextPoint _localCP = null;
	protected static String VERSION_PROPERTY_NAME = "SyncCP_internalVersion";
	protected static String VERSION_DEFAULT_VALUE = "1";
	protected static String TIMESTAMP_PROPERTY_NAME = "SyncCP_internalTimestamp";
        
	public SyncContextPoint(ContextPoint c){
		_localCP = c;
		if(_localCP.getProperty(VERSION_PROPERTY_NAME) == null){
			_localCP.setProperty(VERSION_PROPERTY_NAME, VERSION_DEFAULT_VALUE);
                }
                if(_localCP.getProperty(TIMESTAMP_PROPERTY_NAME) == null){
                    _localCP.setProperty(TIMESTAMP_PROPERTY_NAME, String.valueOf(System.currentTimeMillis()));
                }
		Iterator<Information> cpInfos = _localCP.getInformation();
		while(cpInfos.hasNext()){
			Information info = cpInfos.next();
			if(info.getProperty(SyncInformation.VERSION_PROPERTY_NAME) == null)
				info.setProperty(SyncInformation.VERSION_PROPERTY_NAME, SyncInformation.VERSION_DEFAULT_VALUE);
		}
	}
        
        @Override
        public boolean equals(Object obj){
            return _localCP.equals(((SyncContextPoint)obj)._localCP);
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
            SyncInformation s = new SyncInformation(InMemoSharkKB.createInMemoInformation());
            s.addListener(this);
            _localCP.addInformation(s);
            this.versionUp();
            return s;
	}
        
	@Override
	//TODO Should this info be converted?
	// No, document!!! Reference ought to be dropped.
	public void addInformation(Information source) {
            SyncInformation s = new SyncInformation(source);
            s.addListener(this);
            _localCP.addInformation(s);
            versionUp();
		
	}

	@Override
	public Information addInformation(InputStream is, long len) {
            SyncInformation s = new SyncInformation(InMemoSharkKB.createInMemoInformation());
            s.setContent(is, len);
            s.addListener(this);
            _localCP.addInformation(s);
            versionUp();
            return s;
	}

	@Override
	public Information addInformation(byte[] content) {
            SyncInformation s = new SyncInformation(InMemoSharkKB.createInMemoInformation());
            s.setContent(content);
            s.addListener(this);
            _localCP.addInformation(s);
            versionUp();
            return s;
	}

	@Override
	public Information addInformation(String content) {
            SyncInformation s = new SyncInformation(InMemoSharkKB.createInMemoInformation());
            s.setContent(content);
            s.addListener(this);
            _localCP.addInformation(s);
            versionUp();
            return s;
	}

	@Override
	public Enumeration<Information> enumInformation() {
		Enumeration<Information> infos = _localCP.enumInformation();
		Vector<Information> temp = new Vector<>();
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
	
	private void versionUp() {
            int oldVersion = Integer.parseInt(_localCP.getProperty(VERSION_PROPERTY_NAME));
            _localCP.setProperty(VERSION_PROPERTY_NAME, String.valueOf(oldVersion + 1));
            setTimestamp(new Date());
	}	
	
    public int getVersion() {
        return Integer.parseInt(getProperty(VERSION_PROPERTY_NAME));
    }

    @Override
    public void contentChanged() {
        versionUp();
    }

    @Override
    public void contentRemoved() {
        versionUp();
    }

    @Override
    public void contentTypeChanged() {
        versionUp();
    }
    
    public void setTimestamp(Date d) {
        _localCP.setProperty(VERSION_PROPERTY_NAME, String.valueOf(d.getTime()));
    }
    
    public void getTimestamp() {
        _localCP.getProperty(VERSION_PROPERTY_NAME);
    }
}
