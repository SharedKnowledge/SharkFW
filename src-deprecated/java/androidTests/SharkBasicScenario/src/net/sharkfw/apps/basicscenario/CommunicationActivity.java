package net.sharkfw.apps.basicscenario;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import net.sharkfw.kep.SharkProtocolNotSupportedException;
import net.sharkfw.knowledgeBase.SharkKBException;
import net.sharkfw.peer.J2SEAndroidSharkEngine;
import net.sharkfw.system.SharkSecurityException;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

/**
 * Provides basic menu options and thread handling for alice and bob.
 *
 */
public abstract class CommunicationActivity extends Activity {
	protected J2SEAndroidSharkEngine sharkEngine;
	protected String aliceIp;
	protected String bobIp;
	protected TextView messageTv;
	protected Handler handler;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_communication_test);
		sharkEngine = new J2SEAndroidSharkEngine();
		handler = new Handler();

		Button tcpBtn = (Button) findViewById(R.id.activity_communication_test_tcp_btn);
		Button mailBtn = (Button) findViewById(R.id.activity_communication_test_mail_btn);
		Button wifiBtn = (Button) findViewById(R.id.activity_communication_test_wifi_btn);
		messageTv = (TextView) findViewById(R.id.activity_communication_test_message_tv);
		
		tcpBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				testTcp();
			}
		});

		mailBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				testMail();
			}
		});

		wifiBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				testWifi();
			}
		});

		Intent intent = getIntent();
		aliceIp = intent.getStringExtra("alice");
		bobIp = intent.getStringExtra("bob");
	}
	
	protected void updateStatusMessage(final String message) {
		handler.post(new Runnable() {
			@Override
			public void run() {
				messageTv
						.setText(message);
			}
		});
	}
	
	protected void startCommunicationTest(final String aliceAddress, final String bobAddress) {
		TimerTask task = new TimerTask() {

			@Override
			public void run() {
				try {
					testCommunication(aliceAddress, bobAddress);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

		};
		Timer timer = new Timer();
		timer.schedule(task, 300);
	}
	
	protected abstract void testCommunication(String aliceAddress, String bobAddress) throws SharkKBException, SharkProtocolNotSupportedException, IOException, SharkSecurityException;
	
	protected abstract void testTcp();

	protected abstract void testMail();

	protected abstract void testWifi();

}
