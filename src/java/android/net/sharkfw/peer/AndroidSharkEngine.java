package net.sharkfw.peer;

import java.io.IOException;

import net.sharkfw.kep.KEPStub;
import net.sharkfw.kep.SharkProtocolNotSupportedException;
import net.sharkfw.knowledgeBase.Knowledge;
import net.sharkfw.knowledgeBase.PeerSemanticTag;
import net.sharkfw.knowledgeBase.SharkKBException;
import net.sharkfw.protocols.Stub;
import net.sharkfw.protocols.wifidirect.WifiDirectStreamStub;
import net.sharkfw.system.SharkSecurityException;
import android.content.Context;

/**
 * <b>Description:</b>
 * <br> <br>
 *
 * @author thsc
 * @author df 
 */
public class AndroidSharkEngine extends J2SEAndroidSharkEngine {
	
	WifiDirectStreamStub _wifi;
	private Context _context;
	
    public AndroidSharkEngine(Context context) {
        super();
        _context = context;
    }
    
   
    public Context getContext() {
    	return _context;
    }
    
    /*
     * Wifi Direct methods
     * @see net.sharkfw.peer.SharkEngine#createWifiDirectStreamStub(net.sharkfw.kep.KEPStub)
     */
    
    @Override
    protected Stub createWifiDirectStreamStub(KEPStub kepStub) throws SharkProtocolNotSupportedException {
    	if (_wifi != null) {
    		_wifi.stop();
    	}
    	_wifi = new WifiDirectStreamStub(getContext(), this, kepStub);
    	_wifi.start();
        return _wifi;
    }
    
    @Override
    public void startWifiDirect() throws SharkProtocolNotSupportedException, IOException {
        this.createWifiDirectStreamStub(this.getKepStub());
    }
    
    public void stopWifiDirect() throws SharkProtocolNotSupportedException {
    	_wifi.stop();
    }
    
	@Override
    public void sendKnowledge(Knowledge k, PeerSemanticTag recipient,
    		KnowledgePort kp) throws SharkSecurityException, SharkKBException,
    		IOException {
		
    	if (_wifi != null){
    		recipient.setAddresses(new String[] {_wifi.getConnectionStr()});
    	}
    	
    	super.sendKnowledge(k, recipient, kp);
    }

}
