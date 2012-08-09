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
 *  Author : Pablo R—denas Barquero <prodenas@tuchiringuitobcn.com>
 *  
 *  Powered by ARviewer:
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
 *  		 Raœl Rom‡n L—pez <rroman@gsyc.es>
 *
 */

package prb.creations.chiringuito.ARviewer;

import prb.creations.chiringuito.R;
import prb.creations.chiringuito.ARviewer.Location.ARLocationManager;
import prb.creations.chiringuito.ARviewer.Location.ARLocationManager.OnLocationUpdateListener;
import prb.creations.chiringuito.ARviewer.Location.LocationUtils;
import prb.creations.chiringuito.ARviewer.Overlays.ARSummaryBox;
import prb.creations.chiringuito.ARviewer.Overlays.CamPreview;
import prb.creations.chiringuito.ARviewer.Overlays.DrawFocus;
import prb.creations.chiringuito.ARviewer.Overlays.DrawParameters;
import prb.creations.chiringuito.ARviewer.Overlays.DrawRadar;
import prb.creations.chiringuito.ARviewer.Overlays.DrawResource;
import prb.creations.chiringuito.ARviewer.Overlays.DrawUserStatus;
import prb.creations.chiringuito.ARviewer.Utils.AltitudeManager;
import prb.creations.chiringuito.maps.MapChiringuitoActivity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;

public class ARBase extends ARActivity {
    private static final int ACTIVITY_PREFERENCES = 1;

    private static final int DIALOG_PBAR = 0;
    private static final int DIALOG_ABOUT = DIALOG_PBAR + 1;

    private static ARBase pointerObject = null;

    protected PowerManager.WakeLock mWakeLock;

    private CamPreview mPreview;
    private DrawParameters mParameters;
    private DrawFocus mFocus;
    private DrawRadar mRadar;
    private DrawUserStatus mUserStatus;

    protected float cam_altitude = 0;
    protected float distanceFilter = 0;
    private ARCompassManager compassManager;
    private int idGPS = -1;
    protected static boolean showMenu = true;

    private boolean refreshed;

    private String altitude_status = AltitudeManager.EXISTING_HEIGHTS;

    private ARCompassManager.OnCompassChangeListener compassListener = new ARCompassManager.OnCompassChangeListener() {

        public void onChange(float[] values) {

            float[] values_new = values.clone();

            // THIS PART IS CRITICAL IN ORDER TO REFRESH THE VIEW!!!
            if (mParameters != null) {
                mParameters.setValues(values_new, getLocation(), cam_altitude);
                mParameters.invalidate();
            }

            if (mRadar != null) {
                mRadar.setAzimuth(ARCompassManager.getAzimuth(values_new));
                mRadar.invalidate();
            }

            if (refreshed)
                refreshResourceDrawns(values_new);

        }

    };

    OnLocationUpdateListener locationListener = new OnLocationUpdateListener() {

        public void onUpdate(Location loc) {

            float[] location = {
                    (float) loc.getLatitude(), (float) loc.getLongitude(), 0
            };
            setLocation(location);

            if (mUserStatus != null)
                mUserStatus.setLocationServiceActive(true);

            if (ARLocationManager.getInstance(getBaseContext())
                    .isLocationServiceAltitude()) {
                cam_altitude = (float) loc.getAltitude();
                LocationUtils.setUserHeight(cam_altitude);
                if (mUserStatus != null)
                    mUserStatus.setAltitudeLoaded(true);
            }
        }

    };

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {

            ARSummaryBox.setShowRemoveButton(false);
            pointerObject = this;
            refreshed = false;

            showMenu = true;
            distanceFilter = 0;

            // Hide the window title and notifications bar.
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);

            final PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
            this.mWakeLock = pm.newWakeLock(
                    PowerManager.SCREEN_BRIGHT_WAKE_LOCK, "My Tag");
            this.mWakeLock.acquire();

            // Create our Preview view and set it as the content of our
            // activity.
            mPreview = new CamPreview(this);

            // setting the layers which we use as graphical containers
            getLayers().setBaseLayer();
            getLayers().setResourceLayer();
            getLayers().setInfoLayer();
            getLayers().setExtraLayer();

            mFocus = new DrawFocus(this);
            mRadar = new DrawRadar(this);
            mUserStatus = new DrawUserStatus(this);

            getLayers().addInfoElement(mRadar, null);
            getLayers().addInfoElement(mFocus, null);
            getLayers().addInfoElement(mUserStatus, null);

            ARGeoNode.setRadar(mRadar);

            compassManager = new ARCompassManager(this);
            compassManager.setDrawUserStatusElement(mUserStatus);

        } catch (Exception e) {
            Toast.makeText(getBaseContext(),
                    R.string.error_environment,
                    Toast.LENGTH_LONG).show();
            Log.e("ARView", "", e);
        }

    }

    protected boolean loadParameters() {
        float[] location = new float[3];
        if (getIntent().hasExtra("LATITUDE")
                && getIntent().hasExtra("LONGITUDE")) {
            location[0] = (float) getIntent().getDoubleExtra("LATITUDE", 0);
            location[1] = (float) getIntent().getDoubleExtra("LONGITUDE", 0);
            ARLocationManager.getInstance(this).setLocation(location[0],
                    location[1], (float) AltitudeManager.NO_ALTITUDE_VALUE);
        } else {
            Location loc = ARLocationManager.getInstance(this)
                    .getLastKnownLocation(this);
            location[0] = (float) loc.getLatitude();
            location[1] = (float) loc.getLongitude();
        }
        setLocation(location);
        requestAltitudeInfo();

        return true;
    }

    protected void loadConfig(boolean refresh_altitude) {

        SharedPreferences sharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(this);

        altitude_status = sharedPreferences.getString(ARPreferences.KEY_HEIGHT,
                AltitudeManager.EXISTING_HEIGHTS);
        useHeight((!altitude_status.equals(AltitudeManager.NO_HEIGHTS)));
        if (refresh_altitude
                && altitude_status.equals(AltitudeManager.ALL_HEIGHTS))
            actionRequestHeight();

        if (isUsingHeight()) {
            useThreshold(sharedPreferences.getBoolean(
                    ARPreferences.KEY_IS_DIST_FILTER, false));
            setThreshold(sharedPreferences.getInt(
                    ARPreferences.KEY_DIST_FILTER, 0));
        }

        if (sharedPreferences.getBoolean(ARPreferences.KEY_MEASURES, false)) {
            if (mParameters == null) {
                mParameters = new DrawParameters(this);
                getLayers().addInfoElement(mParameters, null);
            }
        } else {
            if (mParameters != null) {
                getLayers().removeInfoElement(mParameters);
                mParameters = null;
            }
        }

        if (mRadar != null)
            mRadar.setRotateCompass(sharedPreferences.getBoolean(
                    ARPreferences.KEY_ROTATING_COMPASS, true));
        ARGeoNode.clearClicked(getResourcesList());
        ARGeoNode.setDinamicSummary(this, getLayers().getExtraLayer());
        ARGeoNode.setCenterSummary(sharedPreferences.getBoolean(
                ARPreferences.KEY_CENTER_LABELS, false));
        DrawResource.setNamesStatus(sharedPreferences.getString(
                ARPreferences.KEY_NAMES_SHOWING, DrawResource.ALL_NAMES));
        ARGeoNode.setRefreshIcon(getResourcesList(), sharedPreferences
                .getBoolean(ARPreferences.KEY_IMAGE_ICON, true));
        ARGeoNode.activeSearchSystem(sharedPreferences.getBoolean(
                ARPreferences.KEY_SEARCH_SYSTEM, true));
    }

    protected void showResources() {
        refreshed = false;
        ARGeoNode.clearClicked(getResourcesList());
        getLayers().cleanResouceLayer();

        ArrayList<ARGeoNode> res_list = null;

        if (getMyLayer() == null) {
            res_list = getResourcesList();
        } else {
            setResourcesList(null);
            res_list = ARUtils.cleanNoLocation(this, getLayers(), getMyLayer()
                    .getNodes(), getLocation(), distanceFilter);
        }

        if (res_list == null) {
            return;
        }

        if (res_list.size() > 50) {
            ArrayList<ARGeoNode> list = new ArrayList<ARGeoNode>();
            for (int i = 0; i < 50; i++)
                list.add(res_list.get(i));
            res_list.clear();
            res_list.addAll(list);
            Toast.makeText(getBaseContext(),
                    R.string.error_too_much_nodes,
                    Toast.LENGTH_LONG).show();
            Log.e("TuChiringuitoBcn", getString(R.string.error_too_much_nodes));
        }

        ARGeoNodeAzimuthComparator comparator = new ARGeoNodeAzimuthComparator();
        Collections.sort(res_list, comparator);

        setResourcesList(res_list);

        mRadar.setResourcesList(res_list);
        ARGeoNode.setResourcesList(res_list);

        refreshed = true;
        startLocation();
        if (altitude_status.equals(AltitudeManager.ALL_HEIGHTS))
            actionRequestHeight();
    }

    protected void onPause() {
        super.onPause();
        // orListener.stopAudio();
        getLayers().removeBaseElement(mPreview);
        compassManager.unregisterListeners();
        if (!(idGPS < 0))
            ARLocationManager.getInstance(this).pauseUpdates();

        Location loc = new Location("Manual");
        loc.setLatitude(getLocation()[0]);
        loc.setLongitude(getLocation()[1]);
        loc.setAltitude(cam_altitude);
        ARLocationManager.getInstance(this).setLocation(loc);
        this.mWakeLock.release();

    }

    protected void onResume() {
        super.onResume();
        getLayers().addBaseLayer(mPreview);
        showMenu = true;

        getLayers().cleanResouceLayer();
        getLayers().cleanExtraLayer();
        ARGeoNode.clearClicked(getResourcesList());
        compassManager.setOnCompassChangeListener(compassListener);

        if (!(idGPS < 0))
            ARLocationManager.getInstance(this).startUpdates(this);
        this.mWakeLock.acquire();

    }

    protected void onDestroy() {
        ARGeoNode.clearBox();
        ARLocationManager.getInstance(this).stopUpdates();
        ARLocationManager.getInstance(this).resetLocation();
        super.onDestroy();
    }

    protected void startLocation() {
        if (idGPS == -1) {
            if (mUserStatus != null)
                mUserStatus.setLocationServiceOnProgress();
            idGPS = ARLocationManager.getInstance(this)
                    .addLocationListener(locationListener);
            ARLocationManager.getInstance(this).startUpdates(this);
        } else {
            ARLocationManager.getInstance(this).stopUpdates();
            idGPS = -1;
            if (mUserStatus != null)
                mUserStatus.setLocationServiceActive(false);
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);

        super.onCreateOptionsMenu(menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        switch (item.getItemId()) {
            case R.id.menuMap:
                intent = new Intent();
                intent.setClass(this,
                        MapChiringuitoActivity.class);
                startActivity(intent);
                break;

            case R.id.menuPreferences:
                intent = new Intent(this, ARPreferences.class);
                startActivityForResult(intent, ACTIVITY_PREFERENCES);
                break;

            case R.id.menuAbout:
                showDialog(DIALOG_ABOUT);
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        Dialog diag = ARLocationManager.getInstance(this).onCreateDialog(this,
                id);
        if (diag != null)
            return diag;

        switch (id) {
            case DIALOG_ABOUT:
                LayoutInflater factory2 = LayoutInflater.from(this);
                View textEntryView2 = factory2.inflate(R.layout.custom_dialog,
                        null);

                TextView textChir = (TextView) textEntryView2
                        .findViewById(R.id.dialog_text_chiringuito);
                CharSequence str = textChir.getText();
                textChir.setText(getString(R.string.app_name) + " "
                        + getString(R.string.version_app) + "\n"
                        + str.toString());
                return new AlertDialog.Builder(this)
                        .setIcon(R.drawable.ic_menu_about)
                        .setTitle(R.string.about_title)
                        .setView(textEntryView2)
                        .setPositiveButton(R.string.ok, new OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog,
                                    int which) {

                            }
                        })
                        .setNeutralButton(R.string.about_web,
                                new OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface dialog,
                                            int which) {
                                        Intent myIntent = new Intent(
                                                Intent.ACTION_VIEW,
                                                Uri.parse(getString(R.string.urlServer)));
                                        startActivity(myIntent);
                                    }
                                })
                        .create();

            case DIALOG_PBAR:
                ProgressDialog dialog = new ProgressDialog(this);
                dialog.setMessage(getString(R.string.loading));
                dialog.setIndeterminate(true);
                dialog.setCancelable(true);
                return dialog;
        }
        return null;

    }

    private void actionRequestHeight() {
        final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                refreshed = true;
                removeDialog(DIALOG_PBAR);
            }
        };
        refreshed = false;
        showDialog(DIALOG_PBAR);
        new Thread() {
            public void run() {
                AltitudeManager.updateHeights(getResourcesList());
                handler.sendEmptyMessage(0);
            }
        }.start();
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (requestCode) {
            case ACTIVITY_PREFERENCES:
                requestAltitudeInfo();

                loadConfig(true);
                ARGeoNode.setResourcesList(getResourcesList());
                break;

            default:
                break;
        }
    }

    private synchronized void requestAltitudeInfo() {
        Log.i("ARView", "Request altitude");
        if (mUserStatus != null)
            mUserStatus.setAltitudeLoaded(false);
        final Handler altHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (isFinishing())
                    return;
                Log.i("ARView", "Altitude received");
                if ((msg.what == 0) && (mUserStatus != null))
                    mUserStatus.setAltitudeLoaded(true);
            }
        };

        new Thread() {
            public void run() {
                if (!ARLocationManager.getInstance(getBaseContext())
                        .isLocationServiceAltitude())
                    if (cam_altitude != AltitudeManager.NO_ALTITUDE_VALUE)
                        cam_altitude = (float) AltitudeManager
                                .getAbsoluteAltitude(
                                        getBaseContext(),
                                        (float) AltitudeManager
                                                .getAltitudeFromLatLong(
                                                        getLocation()[0],
                                                        getLocation()[1]),
                                        true);
                    else
                        cam_altitude = (float) AltitudeManager
                                .getAbsoluteAltitude(
                                        getBaseContext(),
                                        (float) ARLocationManager
                                                .getInstance(getBaseContext())
                                                .getLocation().getAltitude(),
                                        true);
                LocationUtils.setUserHeight(cam_altitude);
                altHandler.sendEmptyMessage(0);
            }
        }.start();
    }

    public static void GestureNext()
    {
        if (pointerObject == null)
            return;

        int num_click = ARGeoNode.getNodeClicked();
        if (num_click < 0)
            return;
        int fixed_num = num_click;
        do {
            num_click++;
            if (num_click >= pointerObject.getResourcesList().size())
                num_click = 0;
            if (fixed_num == num_click)
                break;
        } while (!pointerObject.getResourcesList().get(num_click).getDrawn()
                .forceClick());

        Log.e("Gesture", "NEXT " + Integer.toString(num_click));
        return;

    }

    public static void GesturePrevious()
    {
        if (pointerObject == null)
            return;

        int num_click = ARGeoNode.getNodeClicked();
        if (num_click < 0)
            return;
        int fixed_num = num_click;
        do {
            num_click--;
            if (num_click < 0)
                num_click = pointerObject.getResourcesList().size() - 1;
            if (fixed_num == num_click)
                break;
        } while (!pointerObject.getResourcesList().get(num_click).getDrawn()
                .forceClick());

        Log.e("Gesture", "PREVIOUS " + Integer.toString(num_click));
        return;
    }
}
