package jp.ddo.masm11.lounlocker;

import android.preference.Preference;
import android.preference.EditTextPreference;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.app.AlertDialog;
import android.widget.TimePicker;
import android.view.View;

import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UnsavedPreference extends EditTextPreference {
    private String mText;
    
    public UnsavedPreference(Context context, AttributeSet attrs) {
	super(context, attrs);
    }
    
    @Override
    public void setText(String text) {
	mText = text;
	// 保存しない。
    }
    
    @Override
    public String getText() {
	return mText;
    }
}
