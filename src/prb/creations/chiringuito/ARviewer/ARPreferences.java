/*
 * 
 *  Copyright (C) 2012 TuChiringuitoBcn.com
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see http://www.gnu.org/licenses/. 
 *
 *  Author : Pablo R�denas Barquero <prodenas@tuchiringuitobcn.com>
 *  
 *  Based on ARViewer of LibreGeoSocial.org:
 *
 *  Copyright (C) 2011 GSyC/LibreSoft, Universidad Rey Juan Carlos.
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see http://www.gnu.org/licenses/. 
 *
 *  Author : Roberto Calvo Palomino <rocapal@gsyc.es>
 *  		 Juan Francisco Gato Luis <jfcogato@gsyc.es
 *  		 Ra�l Rom�n L�pez <rroman@gsyc.es>
 *
 */

package prb.creations.chiringuito.ARviewer;


import prb.creations.chiringuito.R;
import prb.creations.chiringuito.ARviewer.Location.LocationPreferences;
import prb.creations.chiringuito.ARviewer.Utils.AltitudeManager;
import prb.creations.chiringuito.ARviewer.Utils.AltitudePreferences;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

public class ARPreferences extends PreferenceActivity implements OnSharedPreferenceChangeListener, OnPreferenceClickListener {
	private static final int DIALOG_SELECT_THRESHOLD = 1;

	public static final String KEY_HEIGHT				= "height";
	public static final String KEY_IS_DIST_FILTER		= "useDistFilter";
	public static final String KEY_DIST_FILTER			= "distFilter";
	public static final String KEY_MEASURES				= "showMeasures";
	public static final String KEY_CENTER_LABELS		= "centerLabels";
	public static final String KEY_NAMES_SHOWING		= "namesShowing";
	public static final String KEY_IMAGE_ICON			= "imageIcon";
	public static final String KEY_SEARCH_SYSTEM		= "searchSystem";
	public static final String KEY_ROTATING_COMPASS		= "rotatingCompass";
	public static final String KEY_LOCATION_INTENT		= "locationIntent";
	public static final String KEY_ALTITUDE_INTENT		= "altitudeIntent";
	
	
	EditTextPreference userTestPref;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
	    addPreferencesFromResource(R.xml.ar_preferences);
	    initPreferences();
	    
	    // Set up a listener whenever a key changes            
	    getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
	}

	private void initPreferences() {

	    SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
	    
        // threshold
        int thrhld = sharedPreferences.getInt(KEY_DIST_FILTER, 0);
        getPreferenceScreen().findPreference(KEY_DIST_FILTER).setSummary(Integer.toString(thrhld));
        getPreferenceScreen().findPreference(KEY_DIST_FILTER).setOnPreferenceClickListener(this);

        getPreferenceScreen().findPreference(KEY_ALTITUDE_INTENT).setOnPreferenceClickListener(this);
        getPreferenceScreen().findPreference(KEY_LOCATION_INTENT).setOnPreferenceClickListener(this);
        
        blockVisibility(sharedPreferences);
	        
	}

	public void onSharedPreferenceChanged (SharedPreferences sharedPreferences, String key) 
	{
		if(key.equals(KEY_DIST_FILTER)) {

			Preference pref = this.findPreference(key);
			if(pref == null) 
				return;
			
			pref.setSummary(Integer.toString(sharedPreferences.getInt(key, 0)));
		}else if(key.equals(KEY_HEIGHT)){

			blockVisibility(sharedPreferences);
			
		}
	    
	}
	
	private void blockVisibility(SharedPreferences sharedPreferences){

		Preference pref = this.findPreference(KEY_HEIGHT);
		if(pref == null) 
			return;
		
		boolean visible = !sharedPreferences.getString(KEY_HEIGHT, 
				AltitudeManager.EXISTING_HEIGHTS).equals(AltitudeManager.NO_HEIGHTS);

		pref = this.findPreference(KEY_IS_DIST_FILTER);
		pref.setEnabled(visible);

		pref = this.findPreference(KEY_DIST_FILTER);
		if(visible && sharedPreferences.getBoolean(KEY_IS_DIST_FILTER, false))
			pref.setEnabled(true);
		else
			pref.setEnabled(false);
	}

	@Override
	public boolean onPreferenceClick(Preference preference) {
		if(preference.getKey().equals(KEY_DIST_FILTER)){
			showDialog(DIALOG_SELECT_THRESHOLD);
			return true;
		}else if(preference.getKey().equals(KEY_ALTITUDE_INTENT)){
			Intent i1 = new Intent(getBaseContext(), AltitudePreferences.class);
			startActivity(i1);
			return true;
		}else if(preference.getKey().equals(KEY_LOCATION_INTENT)){
			Intent i1 = new Intent(getBaseContext(), LocationPreferences.class);
			startActivity(i1);
			return true;
		}
		return false;
	}
	
	@Override
    protected void onPrepareDialog(int id, Dialog dialog) { 
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
		
		switch(id){
		case DIALOG_SELECT_THRESHOLD:
			configureSeekbarDiag(dialog, KEY_DIST_FILTER, sharedPreferences.getInt(KEY_DIST_FILTER, 0), 500, " m.", 1, sharedPreferences);
			break;
		}
	}
	
	@Override
    protected Dialog onCreateDialog(int id) {  
		
		switch(id){
		case DIALOG_SELECT_THRESHOLD:
			LayoutInflater factory = LayoutInflater.from(this);
			View view = factory.inflate(R.layout.seekbar_num, null);
			
			return new AlertDialog.Builder(this)	    
			.setView(view)
			.setCancelable(true)
			.setPositiveButton(R.string.ok, null)
			.create();
		}
		
		return null;
	}
	
	private void configureSeekbarDiag(final Dialog dialog,
			final String key,
			int progress, 
			int max,
			final String units,
			final int divider,
			final SharedPreferences sharedPreferences){
		
		final TextView tv = (TextView)dialog.findViewById(R.id.tv_sb_text);
		
		String text = "";
		if(divider > 1)
			text += Float.toString(((float)progress)/divider);
		else
			text += Integer.toString(progress);
		tv.setText(text + units);
		
		final SeekBar sb = (SeekBar)dialog.findViewById(R.id.sb_bar); 
		sb.setMax(max);
		sb.setProgress(progress);
		sb.setKeyProgressIncrement(1);
		sb.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {}
			
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {}
			
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				String text = "";
				if(divider > 1)
					text += Float.toString(((float)progress)/divider);
				else
					text += Integer.toString(progress);
				tv.setText(text + units);
			}
		});
		sb.invalidate();
		
		Button bt_ok = ((AlertDialog)dialog).getButton(Dialog.BUTTON_POSITIVE);
		bt_ok.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Editor edit = sharedPreferences.edit();
				edit.putInt(key, sb.getProgress());
				edit.commit();
				dialog.dismiss();
			}
		});
		bt_ok.invalidate();
	}
	
}