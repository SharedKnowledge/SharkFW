package com.shark.demo.shark_demo;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.shark.demo.kbs.KnowledgeBaseCreator;
import com.shark.demo.kp.ConnectionListener;
import com.shark.demo.kp.WifiListenerKp;

import net.sharkfw.kep.SharkProtocolNotSupportedException;
import net.sharkfw.knowledgeBase.sync.SyncKP;
import net.sharkfw.peer.AndroidSharkEngine;
import net.sharkfw.peer.KEPConnection;
import net.sharkfw.peer.SharkEngine;
import net.sharkfw.system.L;
import net.sharkfw.system.SharkException;

import java.io.IOException;


/*
 *       [ ]                    This is Fred. Fred is a prototype. If you want to use Fred,
 *      (   )                   you should really build him anew. He may work right now,
 *       |>|                    but he has three problems:
 *    __/===\__                     1. You don't know how he works.
 *   //| o=o |\\                    2. He does not work as well as you might think he does.
 * <]  | o=o |  [>                  3. He is a prototype!!!
 *     \=====/                  So, if you are lucky enough to be the one building something
 *    / / | \ \                 that extends what he does, get him away from that cliff!
 *   <_________>
 * ------------------           If you run into problems while working with Fred, contact
 *                  /           one of its original makers:
 *                 /            Veit Heller (veit@veitheller.de, @hellerve)
 *                /             Simon Arnold (?, @simonArnold)
 *               /              ?
 *              /               ?
 *             /
 *            /                 Thanks for caring about Fred.
 */


/**
 * The main activity class. Oddly enough, it is not the first activity to be called.
 * You will have to choose a character before being redirected here.
 * Most of the work is done here, though: You will see log outputs and interact with the app.
 */
public class MainActivity extends ActionBarActivity implements View.OnClickListener, ConnectionListener {


    private TextView _text;
    private Button _reload;
    private Button _refresh;
    private Intent intent;
    private SharkEngine _engine;
    public static Handler _handler ;
    private WifiListenerKp _wifiKp;

    KbTextViewWriter _kbTextViewWriter; // This is also a knowledge base listener that prints changes to the text view
    private net.sharkfw.knowledgeBase.sync.SyncKB _kb;
    private net.sharkfw.knowledgeBase.sync.SyncKP _kp;
    //public static final String HEADER_NAME = "com.shark.demo.shark_demo.MESSAGE";

    /**
     * The setup method. All things needed to get started are created here. This includes
     * KBs, the engine and all UI items.
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_main_activity);
        _handler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message inputMessage) {
                super.handleMessage(inputMessage);
                _kbTextViewWriter.appendToKbText((String) inputMessage.obj);
            }

        };

        _engine = new AndroidSharkEngine(this);
        _engine.setConnectionTimeOut(20000);

        intent = getIntent();
        String name = intent.getStringExtra(CharacterChooser.NAME);

        TextView heading = (TextView) findViewById(R.id.character);

        _text = (TextView) findViewById(R.id.editText);
        _text.setTextSize(11);

        _kbTextViewWriter = KbTextViewWriter.getInstance();
        _kbTextViewWriter.setOutputTextView(_text);

        _reload = (Button) findViewById(R.id.reload);
        _refresh = (Button) findViewById(R.id.refresh);

        _reload.setOnClickListener(this);
        _refresh.setOnClickListener(this);

        heading.setText(name);

        try {
            _kb = new KnowledgeBaseCreator().getKb(name);
            _kp = new SyncKP(_engine,_kb,1000);

        } catch(net.sharkfw.system.SharkException e) {
            Log.d("Internal", "Setting up the SyncKB failed.");
        }
        _wifiKp = new WifiListenerKp(_engine,_kb.getOwner());
        _wifiKp.setConnectionListener(this);


        _kb.addListener(_kbTextViewWriter);

        _kbTextViewWriter.writeKbToTextView(_kb);
    }


    /**
     * At the moment, only reload is implemented. It will restart
     * the widget.
     *
     * @param v
     */
    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.reload){
           /* Intent reloadIntent = new Intent(this, MainActivity.class);
            reloadIntent.putExtra(HEADER_NAME, intent.getStringExtra(CharacterChooser.NAME));*/
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
        else if(v.getId() == R.id.refresh){

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main_activity2, menu);
        return true;
    }

    /**
     * This will react to events from the top right menu.
     * You can choose which log output to look at and enable/disable wifi direct.
     *
     * @param item
     * @return a boolean that tells whether we reacted to the event
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if(id == R.id.show_log) {
            _kbTextViewWriter.showLogText();
            return true;
        }

        if (id == R.id.show_kb) {
            _kbTextViewWriter.showKbText();
            return true;
        }
        if (id == R.id.enable_wifi) {
            try {
                _engine.startWifiDirect();
                _kbTextViewWriter.appendToLogText("Wifi direct enabled");
            } catch (SharkProtocolNotSupportedException e) {
                _kbTextViewWriter.appendToLogText("Wifi direct couldn´t start");
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e) {
                _kbTextViewWriter.appendToLogText("Failure: " + e.getMessage());
            }

        }

        if (id == R.id.disable_wifi) {
            try {
                _engine.stopWifiDirect();
                _kbTextViewWriter.appendToLogText("Wifi direct disabled");
            } catch (SharkProtocolNotSupportedException e) {
                e.printStackTrace();
            } catch (Exception e) {
                _kbTextViewWriter.appendToLogText("Failure: " + e.getMessage());
            }
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * This handler reacts to an established connection.
     * @param connection
     */
    @Override
    public void onConnectionEstablished(KEPConnection connection) {
        log("Connection established");
        try {
            log("Trying to send sync interest " + L.contextSpace2String(_kp.getInterest()));
            connection.expose(_kp.getInterest());
        } catch (SharkException e) {
            log("Error - while sending sync interest" + e.getMessage());
        } catch (Exception e) {
            log("Error - while sending sync interest" + e.getMessage());
        }

    }

    /**
     * When the activity is destroyed, stop the engine, hit the breaks, burn the car
     * and run as fast as you can before it explodes.
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        _engine.stop();
    }

    /**
     * A logging helper function. Just put a string in there, it will make sure you can
     * see it.
     *
     * @param text
     */
    public static synchronized void log(String text) {
       final String logText = text;
        _handler.post(new Runnable() {
            public void run() {
                KbTextViewWriter.getInstance().appendToLogText(logText);
            }
        });
    }
}
