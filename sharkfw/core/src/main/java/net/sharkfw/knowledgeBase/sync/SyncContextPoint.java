package net.sharkfw.knowledgeBase.sync;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Objects;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.sharkfw.knowledgeBase.ContextCoordinates;
import net.sharkfw.knowledgeBase.ContextPoint;
import net.sharkfw.knowledgeBase.ContextPointListener;
import net.sharkfw.knowledgeBase.Information;
import net.sharkfw.knowledgeBase.InformationListener;
import net.sharkfw.knowledgeBase.SharkKBException;
import net.sharkfw.knowledgeBase.inmemory.InMemoSharkKB;
import net.sharkfw.system.L;

/**
 * Implements a synchronized context point. Delegates 
 * most of its functionality to the internal context point.
 * @author hellerve
 */
public class SyncContextPoint implements ContextPoint, InformationListener {
    private ContextPoint _localCP = null;
    protected static String VERSION_PROPERTY_NAME = "SyncCP_internalVersion";
    protected static String VERSION_DEFAULT_VALUE = "1";
    protected static String TIMESTAMP_PROPERTY_NAME = "SyncCP_internalTimestamp";

    /**
     * The constructor. Needs a context point to wrap.
     * @param c - the internal context point 
     */
    public SyncContextPoint(ContextPoint c) throws SharkKBException{
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
                if(info.getProperty(SyncInformation.VERSION_PROPERTY_NAME) == null){
                    info.setProperty(SyncInformation.VERSION_PROPERTY_NAME, SyncInformation.VERSION_DEFAULT_VALUE);
                }
            }
    }

    @Override
    public boolean equals(Object obj){
        if(obj == null){
            return false;
        }
        if(this == obj){
            return true;
        }

        if(obj instanceof SyncContextPoint){
            return _localCP.equals(((SyncContextPoint)obj)._localCP);
        } 
        else if(obj instanceof ContextPoint){
            return _localCP.equals(obj);
        } 
        else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 53 * hash + Objects.hashCode(this._localCP);
        return hash;
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
    public void setProperty(String name, String value) throws SharkKBException {
        _localCP.setProperty(name, value);
    }

    @Override
    public String getProperty(String name) throws SharkKBException {
        return _localCP.getProperty(name);
    }

    @Override
    public void setProperty(String name, String value, boolean transfer) throws SharkKBException {
        _localCP.setProperty(name, value);
    }

    @Override
    public void removeProperty(String name) throws SharkKBException {
        _localCP.removeProperty(name);
    }

    @Override
    public Enumeration<String> propertyNames() throws SharkKBException {
        return _localCP.propertyNames();
    }

    @Override
    public Enumeration<String> propertyNames(boolean all) throws SharkKBException {
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
        Collection<Information> temp = new ArrayList<>();
        while(infos.hasNext()){
            temp.add(new SyncInformation(infos.next()));
        }
        return temp.iterator();
    }

    @Override
    public Iterator<Information> getInformation() {
        Iterator<Information> infos = _localCP.getInformation();
        Collection<Information> temp = new ArrayList<>();
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
            try {
                _localCP.setProperty(VERSION_PROPERTY_NAME, VERSION_DEFAULT_VALUE);
            } catch (SharkKBException ex) {
                L.e("fatal: cannot set properties");
            }
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
        int oldVersion;
        try {
            oldVersion = Integer.parseInt(_localCP.getProperty(VERSION_PROPERTY_NAME));
            _localCP.setProperty(VERSION_PROPERTY_NAME, String.valueOf(oldVersion + 1));
            setTimestamp(new Date());
        } catch (SharkKBException ex) {
            L.e("fatal: cannot set properties");
        }
    }	

    	
    public void setVersion(String version) throws SharkKBException {
        setProperty(VERSION_PROPERTY_NAME, version);
    }    
        
    public int getVersion() {
        try {
            return Integer.parseInt(getProperty(VERSION_PROPERTY_NAME));
        } catch (SharkKBException ex) {
            // TODO
        }
        
        return 0;
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
    
    public void setTimestamp(Date d) throws SharkKBException {
        _localCP.setProperty(TIMESTAMP_PROPERTY_NAME, String.valueOf(d.getTime()));
    }
    
    public void getTimestamp() throws SharkKBException {
        _localCP.getProperty(TIMESTAMP_PROPERTY_NAME);
    }
}
