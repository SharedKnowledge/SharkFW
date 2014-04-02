package net.sharkfw.apps.basicscenario;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

import net.sharkfw.system.L;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

/**
 * Provides a menu for alice and bob.
 * Put in mail data.
 *
 */
public class MainActivity extends Activity {
	
	private static final String ALICE_TCP = "141.45.203.59";
	private static final String BOB_TCP = "141.45.204.179";
	
	public static final String ALICE_MAIL = "carmen@sharksystem.net";
	public static final String ALICE_PWD = "???";
	public static final String ALICE_SMTP = "smtp.sharksystem.net";
	public static final String ALICE_POP3 = "pop3.sharksystem.net";
	
	public static final String BOB_MAIL = "douglas@sharksystem.net";
	public static final String BOB_PWD = "???";
	public static final String BOB_SMTP = "smtp.sharksystem.net";
	public static final String BOB_POP3 = "pop3.sharksystem.net";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		L.setLogLevel(L.LOGLEVEL_ALL);

		setContentView(R.layout.activity_main);

		Button aliceBtn = (Button) findViewById(R.id.activity_main_alice_btn);
		Button bobBtn = (Button) findViewById(R.id.activity_main_bob_btn);
		
		final EditText myIpEt = (EditText) findViewById(R.id.activity_main_my_ip_et);
		final EditText partnerIpEt = (EditText) findViewById(R.id.activity_main_partner_ip_et);
		
		String myIp = getIpAddress();
		myIpEt.setText(myIp);
		if(ALICE_TCP.equals(myIp)) {
			partnerIpEt.setText(BOB_TCP);
		} else {
			partnerIpEt.setText(ALICE_TCP);
		}

		aliceBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(MainActivity.this,
						AliceActivity.class);
				intent.putExtra("alice", myIpEt.getText().toString());
				intent.putExtra("bob", partnerIpEt.getText().toString());
				startActivity(intent);
			}
		});

		bobBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(MainActivity.this,
						BobActivity.class);
				intent.putExtra("bob", myIpEt.getText().toString());
				intent.putExtra("alice", partnerIpEt.getText().toString());
				startActivity(intent);
			}
		});
	}

	 public static String getIpAddress() {
		try {
			for (Enumeration en = NetworkInterface.getNetworkInterfaces(); en
					.hasMoreElements();) {
				NetworkInterface intf = (NetworkInterface) en.nextElement();
				for (Enumeration enumIpAddr = intf.getInetAddresses(); enumIpAddr
						.hasMoreElements();) {
					InetAddress inetAddress = (InetAddress) enumIpAddr
							.nextElement();
					if (!inetAddress.isLoopbackAddress()
							&& inetAddress instanceof Inet4Address) {
						String ipAddress = inetAddress.getHostAddress()
								.toString();
						return ipAddress;
					}
				}
			}
		} catch (SocketException ex) {
		}
		return null;
	}
}
