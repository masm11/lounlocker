package jp.ddo.masm11.lounlocker;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.content.SharedPreferences;
import android.widget.Button;
import android.view.View;

public class MainActivity extends AppCompatActivity {

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
    
    @Override
    protected void onDestroy() {
	SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
	SharedPreferences.Editor editor = prefs.edit();
	editor.remove("sudo");
	editor.remove("decr");
	editor.commit();
	
	super.onDestroy();
    }
    
    private void lock() {
	SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
	Log.d("sudo=%s", prefs.getString("sudo", null));
    }
    private void unlock() {
	SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
	Log.d("sudo=%s", prefs.getString("sudo", null));
    }

}
