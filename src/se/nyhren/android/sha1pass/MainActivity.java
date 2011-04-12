package se.nyhren.android.sha1pass;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.text.ClipboardManager;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.text.method.SingleLineTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;
/**

Copyright (c) 2011, Andreas Nyhrén andreas.nyhren@gmail.com

Permission to use, copy, modify, and/or distribute this software for any
purpose with or without fee is hereby granted, provided that the above
copyright notice and this permission notice appear in all copies.

THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES
WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF
MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR
ANY SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES
WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN
ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF
OR IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.

 */
public class MainActivity extends Activity {
	EditText in;
	EditText out;
	Button b1;
	Button b2;
	Button b3;
	Button b4;
	Button bh;
	CheckBox c1;
	CheckBox c2;
	boolean isSecure = false;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		in = (EditText) findViewById(R.id.editText1);
		out = (EditText) findViewById(R.id.editText2);
		b1 = (Button) findViewById(R.id.button1);
		b2 = (Button) findViewById(R.id.button2);
		b3 = (Button) findViewById(R.id.button3);
		b4 = (Button) findViewById(R.id.button4);
		c1 = (CheckBox) findViewById(R.id.checkBox1);
		c2 = (CheckBox) findViewById(R.id.checkBox2);
		bh = (Button) findViewById(R.id.buttonH);

		b1.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				calcSha1();
			}
		});
		
		b2.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				calcSha1Half();
			}
		});
		
		b3.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				calcBase64();
			}
		});
		
		b4.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				calcBase64Half();
			}
		});
		
		c1.setChecked(true);		
		
		c1.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				if (c1.isChecked()) {
					in.setInputType(InputType.TYPE_TEXT_VARIATION_NORMAL);
					in.setTransformationMethod(new SingleLineTransformationMethod ());
				} else {
					in.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
					in.setTransformationMethod(new PasswordTransformationMethod());
				}
			}
		});
		
		c2.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				if (c2.isChecked()) {					
					setSecure();
				} else {
					setNotSecure();
				}
			}
		});
		
		bh.setText("?");
		
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("About");
		builder.setMessage("Copyright 2011, Andreas Nyhren\n\nA sentence based password generation program. Enter a unique sentence, then click an encoding button to generate a strong, secure password based on that sentence. Use different sentences to generate different passwords.");
		final AlertDialog alert = builder.create();
		
		bh.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {				
				alert.show();
			}
		});

	}
	
	protected void setNotSecure() {
		c1.setChecked(true);
		isSecure = false;
		in.setText("");
		send2Clipboard("");
		in.setInputType(InputType.TYPE_TEXT_VARIATION_NORMAL);
		in.setTransformationMethod(new SingleLineTransformationMethod ());
	}
	
	protected void setSecure() {
		c1.setChecked(false);
		isSecure = true;
		in.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
		in.setTransformationMethod(new PasswordTransformationMethod());
	}

	protected void calcBase64Half() {
		String p = in.getText().toString();
		String b64 = String.valueOf(Base64Coder.encode(getSha1(p)));
		b64 = b64.substring(0, b64.length()/2);
		send2Clipboard(b64);
		outText(b64);
	}

	protected void calcSha1Half() {
		String p = in.getText().toString();		
		byte[] output = getSha1(p);
		String hex = bytesToHex(output);
		send2Clipboard(hex);
		outText(hex.substring(0, hex.length()/2));		
	}

	protected void calcBase64() {
		String p = in.getText().toString();
		String b64 = String.valueOf(Base64Coder.encode(getSha1(p)));
		send2Clipboard(b64);
		outText(b64);
	}
	
	private void outText(String s) {
		if (!isSecure) {
			out.setText(s.substring(0, 6));
		} else {
			out.setText("PassPeek");
		}
		
	}

	protected void calcSha1() {
		String p = in.getText().toString();		
		byte[] output = getSha1(p);
		String hex = bytesToHex(output);
		send2Clipboard(hex);
		outText(hex);
	}

	private byte[] getSha1(String p) {
		MessageDigest md = null;
		try {
			md = MessageDigest.getInstance("SHA1");
		} catch (NoSuchAlgorithmException e) {
			Log.e("SHA1Pass", "npe", e);
		}
		try {
			md.update(p.getBytes("utf-8"));
		} catch (UnsupportedEncodingException e) {
			Log.e("SHA1Pass", "error utf-8", e);
		}
		return md.digest();
	}
	
	protected void send2Clipboard(String s) {
		ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
		clipboard.setText(s);
		Toast.makeText(getApplicationContext(), "Copied to Clipboard", Toast.LENGTH_SHORT).show();
	}

	public static String bytesToHex(byte[] data) {
		StringBuffer buf = new StringBuffer();
		for (int i = 0; i < data.length; i++) {
			int halfbyte = (data[i] >>> 4) & 0x0F;
			int two_halfs = 0;
			do {
				if ((0 <= halfbyte) && (halfbyte <= 9))
					buf.append((char) ('0' + halfbyte));
				else
					buf.append((char) ('a' + (halfbyte - 10)));
				halfbyte = data[i] & 0x0F;
			} while (two_halfs++ < 1);
		}
		return buf.toString();
	}
}