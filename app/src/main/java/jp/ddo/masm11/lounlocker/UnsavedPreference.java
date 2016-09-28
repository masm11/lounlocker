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
import android.os.Parcelable;
import android.os.Parcel;

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
	final boolean wasBlocking = shouldDisableDependents();
	
	mText = text;
	
	// 保存しない。
	
	final boolean isBlocking = shouldDisableDependents();
	if (isBlocking != wasBlocking)
	    notifyDependencyChange(isBlocking);
    }
    
    @Override
    public String getText() {
	return mText;
    }
    
    @Override
    protected Parcelable onSaveInstanceState() {
	Parcelable superState = super.onSaveInstanceState();
	SavedState myState = new SavedState(superState);
	myState.text = getText();
	return myState;
    }
    
    @Override
    protected void onRestoreInstanceState(Parcelable state) {
	if (state == null || !state.getClass().equals(SavedState.class)) {
	    // Didn't save state for us in onSaveInstanceState
	    super.onRestoreInstanceState(state);
	    return;
	}
	
	SavedState myState = (SavedState) state;
	super.onRestoreInstanceState(myState.getSuperState());
	// summary 更新のため
	if (callChangeListener(myState.text))
	    setText(myState.text);
    }
    
    private static class SavedState extends BaseSavedState {
	String text;
	
	public SavedState(Parcel source) {
	    super(source);
	    text = source.readString();
	}
	
	@Override
	public void writeToParcel(Parcel dest, int flags) {
	    super.writeToParcel(dest, flags);
	    dest.writeString(text);
	}
	
	public SavedState(Parcelable superState) {
	    super(superState);
	}
	
	public static final Parcelable.Creator<SavedState> CREATOR =
		new Parcelable.Creator<SavedState>() {
		    public SavedState createFromParcel(Parcel in) {
			return new SavedState(in);
		    }
		    
		    public SavedState[] newArray(int size) {
			return new SavedState[size];
		    }
		};
    }
}
