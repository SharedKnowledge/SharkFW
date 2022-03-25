package net.sharkfw.knowledgeBase.inmemory;

import net.sharkfw.knowledgeBase.*;
import net.sharkfw.system.Util;

import java.io.Serializable;

/**
 *
 * @author thsc
 */
@SuppressWarnings("unchecked")
public class InMemo_SN_TX_PeerSemanticTag extends InMemo_SN_TX_SemanticTag 
    implements PeerSemanticTag, PeerTXSemanticTag, PeerSNSemanticTag, Serializable {
    private String[] addresses;

    //TODO: Serializable only needed because PKI is not migrated to new SharkFW Version
    public InMemo_SN_TX_PeerSemanticTag() {
        super();

    }

    public InMemo_SN_TX_PeerSemanticTag(String name, String[] si, String[] addresses) {
        super(name, si);
        this.addresses = addresses;
    }
    
    @Override
    public String[] getAddresses() {
        return this.addresses;
    }

    @Override
    public void setAddresses(String[] addresses) {
        this.addresses = addresses;
        persist();
    }
    
    public final static String ADDRESS = "PeerST_Addresses";

    @Override
    public void persist() {
        super.persist();
        
        // persist sis
        String addrString = Util.array2string(this.addresses);
        this.setSystemProperty(ADDRESS, addrString);
    }
    
    @Override
    public void refreshStatus() {
        super.refreshStatus();
        
        String addrString = this.getSystemProperty(ADDRESS);
        if(addrString != null) {
            String[] newAddr = Util.string2array(addrString);
            if(newAddr != null) {
                this.addresses = newAddr;
            }
        }
    }
    
    @SuppressWarnings("rawtypes")
    public InMemo_SN_TX_PeerSemanticTag(SystemPropertyHolder persistentHolder, InMemoGenericTagStorage storage) {
        super(persistentHolder, storage);
    }

    @Override
    public void removeAddress(String address) {
        String[] oldAddresses = this.getAddresses();
        
        String[] newAddresses = new String[oldAddresses.length-1];
        
        int j = 0;
        int i = 0;
        for(; i < oldAddresses.length; i++) {
            if(!oldAddresses[i].equalsIgnoreCase(address)) {
                newAddresses[j++] = oldAddresses[i];
            }
        }
        
        // something changed?
        if(i > j) {
            this.setAddresses(newAddresses);
        }
    }

    @Override
    public void addAddress(String address) {

        if(address==null) return;

        if(this.getAddresses()==null){
            this.setAddresses(new String[0]);
        }

        String[] oldAddresses = this.getAddresses();
        
        String[] newAddresses = new String[oldAddresses.length+1];

        boolean found = false;
        for(int i = 0; i < oldAddresses.length; i++) {
            if(oldAddresses[i] != null) {
                if(oldAddresses[i].equalsIgnoreCase(address)) {
                    found = true;
                    break;
                } else {
                    newAddresses[i] = oldAddresses[i];
                }
            }
        }
        
        // new address not found?
        if(!found) {
            // add new address at the end
            newAddresses[oldAddresses.length] = address;
            this.setAddresses(newAddresses);
        } // else: do nothing
    }

    @Override
    public void removeSI(String deleteSI) throws SharkKBException {
        /* that a kind of trick: super implementation creates an new array
         * when adding this si is ok. We check this.
         */

        String[] sis = this.getSI();
        super.removeSI(deleteSI);

        if(sis != this.getSI()) {
            // something changed
            this.updateSIInPredicates();
        }
    }
}
