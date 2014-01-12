package net.sharkfw.knowledgeBase.inmemory;

import net.sharkfw.knowledgeBase.PeerSNSemanticTag;
import net.sharkfw.knowledgeBase.PeerSemanticTag;
import net.sharkfw.knowledgeBase.PeerTXSemanticTag;
import net.sharkfw.knowledgeBase.SystemPropertyHolder;
import net.sharkfw.system.Util;

/**
 *
 * @author thsc
 */
@SuppressWarnings("unchecked")
public class InMemo_SN_TX_PeerSemanticTag extends InMemo_SN_TX_SemanticTag 
    implements PeerSemanticTag, PeerTXSemanticTag, PeerSNSemanticTag {
    private String[] addresses;
    
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
}
