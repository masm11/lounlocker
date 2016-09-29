package jp.ddo.masm11.lounlocker;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.app.FragmentManager;
import android.preference.PreferenceManager;
import android.preference.PreferenceFragment;
import android.content.SharedPreferences;
import android.widget.Button;
import android.view.View;

import java.io.File;

public class MainActivity extends AppCompatActivity {
    private Thread thread;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
	Log.init(getExternalCacheDir());
        setContentView(R.layout.activity_main);
	
	Button button;
	button = (Button) findViewById(R.id.btn_unlock);
	assert button != null;
	button.setOnClickListener(new View.OnClickListener() {
	    public void onClick(View view) {
		unlock();
	    }
	});
	button = (Button) findViewById(R.id.btn_lock);
	assert button != null;
	button.setOnClickListener(new View.OnClickListener() {
	    public void onClick(View view) {
		lock();
	    }
	});
    }
    
    private Config getConfig() {
	FragmentManager fragMan = getFragmentManager();
	PreferenceFragment prefFrag = (PreferenceFragment) fragMan.findFragmentById(R.id.pref_frag);
	assert prefFrag != null;
	UnsavedPreference prefSudo = (UnsavedPreference) prefFrag.findPreference("sudo");
	assert prefSudo != null;
	UnsavedPreference prefDecr = (UnsavedPreference) prefFrag.findPreference("decr");
	assert prefDecr != null;
	
	SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
	String hostname = prefs.getString("hostname", null);
	String username = prefs.getString("username", null);
	File privkey = new File(getFilesDir(), "privkey");
	
	return new Config(hostname, username, privkey, prefSudo.getText(), prefDecr.getText());
    }
    
    private void lock() {
	Config config = getConfig();
	
	thread = new Thread(new LoUnlockThread(false, config));
	thread.start();
    }
    private void unlock() {
	Config config = getConfig();
	
	thread = new Thread(new LoUnlockThread(true, config));
	thread.start();
    }
}
