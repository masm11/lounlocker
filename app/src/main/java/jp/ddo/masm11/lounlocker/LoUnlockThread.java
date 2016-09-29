package jp.ddo.masm11.lounlocker;

import com.jcraft.jsch.ChannelShell;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.UserInfo;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.KeyPair;

import java.io.File;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.IOException;
import java.io.EOFException;

public class LoUnlockThread implements Runnable {
    private final boolean unlock;
    private final Config config;
    public LoUnlockThread(boolean unlock, Config config) {
	this.unlock = unlock;
	this.config = config;
    }
    
    public void run() {
	Log.d("start.");
	
	try {
	    JSch jsch = new JSch();
	    
	    jsch.addIdentity(config.privkey.toString(), (String) null);
	    
	    Session session = jsch.getSession(config.username, config.hostname, 22);
	    UserInfo ui = new LoUnlockUserInfo();
	    session.setUserInfo(ui);
	    session.connect();
	    
	    ChannelShell ch = (ChannelShell) session.openChannel("shell");
	    ch.setPtyType("vt100");
	    ch.setEnv("LANG", "ja_JP.utf8");
	    ch.connect();
	    
	    BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(ch.getOutputStream()));
	    BufferedReader br = new BufferedReader(new InputStreamReader(ch.getInputStream()));
	    
	    if (unlock) {
		Log.d("waiting for prompt.");
		waitForString(br, "> ");
		
		Log.d("sending command.");
		bw.write("./export_dlna\n");
		bw.flush();
		
		boolean done = false;
		for (int i = 0; !done && i < 10; i++) {
		    Log.d("waiting for prompt.");
		    switch (waitForString(br, new String[] { "[sudo] ", "Enter passphrase for ", "> " })) {
		    case "[sudo] ":
			waitForString(br, ": ");
			Log.d("sending sudo password.");
			bw.write(config.pwd_sudo);
			bw.write("\n");
			bw.flush();
			break;
			
		    case "Enter passphrase for ":
			waitForString(br, ": ");
			Log.d("sending decrypt password.");
			bw.write(config.pwd_decr);
			bw.write("\n");
			bw.flush();
			break;
			
		    case "> ":
			Log.d("sending exit.");
			bw.write("exit\n");
			bw.flush();
			done = true;
			break;
		    }
		}
		
	    } else {
		Log.d("waiting for prompt.");
		waitForString(br, "> ");
		
		Log.d("sending command.");
		bw.write("./unexport_dlna\n");
		bw.flush();
		
		boolean done = false;
		for (int i = 0; !done && i < 10; i++) {
		    Log.d("waiting for prompt.");
		    switch (waitForString(br, new String[] { "[sudo] ", "> " })) {
		    case "[sudo] ":
			waitForString(br, ": ");
			Log.d("sending sudo password.");
			bw.write(config.pwd_sudo);
			bw.write("\n");
			bw.flush();
			break;
			
		    case "> ":
			Log.d("sending exit.");
			bw.write("exit\n");
			bw.flush();
			done = true;
			break;
		    }
		}
	    }
	    
	    Log.d("delaying..");
	    Thread.sleep(1000);
	    Log.d("disconnecting..");
	    session.disconnect();
	} catch (Exception e) {
	    Log.e(e, "exception.");
	}
	
	Log.d("end.");
    }
    
    private char[] readBuffer = new char[4096];
    private int charsInBuffer = 0;
    private String strBuffer = new String("");
    private String waitForString(BufferedReader br, String str)
	    throws IOException {
	return waitForString(br, new String[] { str });
    }
    private String waitForString(BufferedReader br, String[] strs)
	    throws IOException {
	String r;
	
	String dbg = "", delim = "";
	for (String s: strs) {
	    dbg += delim;
	    dbg += "\"" + s + "\"";
	    delim = ", ";
	}
	Log.d("strs=%s", dbg);
	
	r = removeIfExists(strs);
	if (r != null)
	    return r;
	
	while (true) {
	    int size = br.read(readBuffer, charsInBuffer, readBuffer.length - charsInBuffer);
	    if (size == -1)
		throw new EOFException();
	    charsInBuffer += size;
	    strBuffer = new String(readBuffer, 0, charsInBuffer);
	    Log.d("strBuffer=%s", strBuffer);
	    
	    r = removeIfExists(strs);
	    if (r != null)
		return r;
	}
    }
    
    private String removeIfExists(String[] strs) {
	Log.d("before strBuffer=%s", strBuffer);
	
	for (String s: strs) {
	    int offset = strBuffer.indexOf(s);
	    if (offset >= 0) {
		int rest = charsInBuffer - (offset + s.length());
		System.arraycopy(readBuffer, offset + s.length(), readBuffer, 0, rest);
		charsInBuffer = rest;
		strBuffer = new String(readBuffer, 0, charsInBuffer);
		Log.d("after strBuffer=%s", strBuffer);
		
		return s;
	    }
	}
	
	Log.d("not found.");
	return null;
    }
    
    private void waitForEOF(BufferedReader br)
	    throws IOException {
	char[] buf = new char[1024];
	while (true) {
	    int size = br.read(buf);
	    if (size == -1)
		break;
	    Log.d("buf=%s", new String(buf, 0, size));
	}
    }
    
    private class LoUnlockUserInfo implements UserInfo {
	public String getPassphrase() {
	    return null;
	}
	public String getPassword() {
	    return null;
	}
	public boolean promptPassword(String message) {
	    Log.i("%s", message);
	    return true;
	}
	public boolean promptPassphrase(String message) {
	    Log.i("%s", message);
	    return true;
	}
	public boolean promptYesNo(String message) {
	    Log.i("%s", message);
	    return true;
	}
	public void showMessage(String message) {
	    Log.i("%s", message);
	}
    }
}
