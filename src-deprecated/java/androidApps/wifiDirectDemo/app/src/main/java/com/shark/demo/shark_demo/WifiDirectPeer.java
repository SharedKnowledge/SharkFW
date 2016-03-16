//package com.shark.demo.shark_demo;
//
//import java.io.IOException;
//
//import android.content.Context;
//
//import com.shark.demo.kp.WifiListenerKp;
//
//import net.sharkfw.kep.SharkProtocolNotSupportedException;
//import net.sharkfw.knowledgeBase.SharkCS;
//import net.sharkfw.peer.AndroidSharkEngine;
//
//import net.sharkfw.peer.KnowledgePort;
//import net.sharkfw.peer.SharkEngine;
//import net.sharkfw.system.L;
//
//public class WifiDirectPeer {
//    private final SharkEngine engine;
//    private final KnowledgePort knowledgePort;
//    private Context context;
//
//    public WifiDirectPeer(String name, Context context, SharkCS interest) throws SharkProtocolNotSupportedException, IOException {
//    	SharkEngine engine = new AndroidSharkEngine(context);
//
//
//        this.engine = engine;
//        this.context = context;
//        this.knowledgePort = new WifiListenerKp(engine, context, interest);
//    }
//
//    public void stop() {
//        try {
//            engine.stopWifiDirect();
//        } catch (SharkProtocolNotSupportedException e) {
//            L.d("Wiri DirectPeer", "Error while stopping wifi direct in  " + engine.getOwner() + "s engine: " + e.toString());
//        }
//    }
//
//    public void start() throws SharkProtocolNotSupportedException, IOException {
//    	engine.setAllowSendingEmptyContextPoints(true);
//    	engine.setConnectionTimeOut(1000);
//    	engine.startWifiDirect();
//
//
//    }
//}
