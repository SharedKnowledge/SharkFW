package net.sharkfw.knowledgeBase.inmemory;

import net.sharkfw.knowledgeBase.AbstractSemanticTag;
import net.sharkfw.knowledgeBase.SharkKBException;
import net.sharkfw.knowledgeBase.SystemPropertyHolder;
import net.sharkfw.system.Util;

/**
 * The implementation for an in-memory SemanticTag, keeping all values in RAM.
 *
 * TODO: Store all values in properties instead of private members?
 * 
 * @author thsc
 */
public class InMemoSemanticTag extends AbstractSemanticTag {

    private String name;
    private String[] si;
    private InMemoGenericTagStorage storage;

    InMemoSemanticTag(String name, String[] si) {
        this(name, si, null);
    }

    @SuppressWarnings("rawtypes")
    InMemoSemanticTag(String name, String[] si, InMemoGenericTagStorage storage) {
        super();
        this.name = name;
        this.si = this.checkNullAndDuplicates(si);
        this.storage = storage;
    }
    
    /**
     * Removes null references if any
     * @param sis
     * @return 
     */
    private String[] checkNullAndDuplicates(String[] sisOrig) {
        // first: check if not empty
        if(sisOrig == null || sisOrig.length == 0) {
            return null;
        }
        
        // copy the whole thing - for those who want to reuse that array
        String[] sis = new String[sisOrig.length];
        System.arraycopy(sisOrig, 0, sis, 0, sisOrig.length);
        
        // remove duplicates first
        for(int origIndex = 0; origIndex < sis.length -1; origIndex++) {
            if(sis[origIndex] != null) {
                for(int i = origIndex+1; i < sis.length; i++) {
                    if(sis[i] != null) {
                        if(sis[i].equalsIgnoreCase(sis[origIndex])) {
                            sis[i] = null;
                        }
                    }
                }
            }
        }
        
        // remove null sis
        int nullCounter = 0;
        
        // check if null in there
        for(int i = 0; i < sis.length; i++) {
            String siTmp = sis[i];
            if(siTmp == null) {
                nullCounter++;
            }
        }
        
        if(nullCounter > 0) {
            // compress
            String[] ret = new String[sis.length - nullCounter];
            int index = 0;
            for(int i = 0; i < sis.length; i++) {
                String siTmp = sis[i];
                if(siTmp != null) {
                    ret[index++] = siTmp;
                }
            }
            
            return ret;
            
        } else {
            // ok
            return sis;
        }
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public String[] getSI() {
        return this.si;
    }

    @Override
    public void removeSI(String deleteSI) throws SharkKBException {
        if(si == null || si.length == 0) {
            throw new SharkKBException("no SI set at all");
        }
        
        if(si.length == 1) {
            if(si[0].equalsIgnoreCase(deleteSI)) {
                throw new SharkKBException("removing final si is forbidden. Add another SI before removing this one");
            } else {
                return;
            }
        }
        
        this.si = Util.removeSI(this.si, deleteSI);
        if(this.storage != null) {
            this.storage.siRemoved(deleteSI, this);
            super.sisChanged();
        }
    }
    
//    private void syncStorage() {
//        if(this.storage != null) {
//            this.storage.initSi();
//        }
//        
//        this.persist();
//    }

    @Override
    public void addSI(String addSI) throws SharkKBException {
        // check for duplicates first:
        for(int i = 0; i < si.length; i++) {
            if(si[i].equalsIgnoreCase(addSI)) {
                throw new SharkKBException("si already exists - duplicates not permitted");
            }
        }
        
        this.si = Util.addString(this.si, addSI);
        if(this.storage != null) {
            this.storage.siAdded(addSI, this);
            super.sisChanged();
        }
    }

    @Override
    public void setName(String newName) {
        this.name = newName;
        this.persist();
    }
    
    void setStorage(InMemoGenericTagStorage storage) {
        this.storage = storage;
    }

    public InMemoGenericTagStorage getStorage() {
        return this.storage;
    }

    //////////////////////////////////////////////////////////
    //              write status into properties            //
    //////////////////////////////////////////////////////////
    
    public static final String ST_NAME = "SemanticTag_Name";
    public static final String ST_SIS = "SemanticTag_SIs";
    
    @Override
    public void persist() {
        super.persist();
        
        // persist name
        this.setSystemProperty(ST_NAME, this.name);

        // persist sis
        String sisString = Util.array2string(this.si);
        this.setSystemProperty(ST_SIS, sisString);
    }
    
    @Override
    public void refreshStatus() {
        super.refreshStatus();
        
        String newName = this.getSystemProperty(ST_NAME);
        if(newName != null) this.name = newName;
        
        String sisString = this.getSystemProperty(ST_SIS);
        if(sisString != null) {
            String[] newSIs = Util.string2array(sisString);
            if(newSIs != null) {
                this.si = newSIs;
            }
        }
    }
    
    public InMemoSemanticTag(SystemPropertyHolder persistentHolder) {
        super(persistentHolder);
        
        this.refreshStatus();
    }
}
