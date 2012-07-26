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
 *           Juan Francisco Gato Luis <jfcogato@gsyc.es
 *           Raœl Rom‡n L—pez <rroman@gsyc.es>
 *
 */

package prb.creations.chiringuito.Maps;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;
import com.google.android.maps.Overlay;

import prb.creations.chiringuito.R;
import prb.creations.chiringuito.ARviewer.ARviewer;
import prb.creations.chiringuito.ARviewer.Location.ARLocationManager;
import prb.creations.chiringuito.db.ChiringuitoProvider;
import prb.creations.chiringuito.db.ChiringuitosDB.Chiringuitos;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.database.Cursor;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MapChiringuitoActivity extends MapActivity {
    private Context mContext;
    private MapView mMap;
    private ImageButton ibSearch;
    private AutoCompleteTextView actvAddress;
    private MapController mController;
    private MyLocationOverlay mLocation;
    private List<Overlay> mOverlays = null;
    private MapIndicatorOverlay itemizedOverlay;
    private ArrayList<String> chiringuitos = null;
    private Cursor cursor = null;
    private Uri uri;
    private boolean showSearchButton = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map);
        mContext = this.getApplicationContext();
        uri = Uri.parse("content://" + ChiringuitoProvider.PROVIDER_NAME
                + "/chiringuitos");

        actvAddress = (AutoCompleteTextView) findViewById(R.id.searchView);
        actvAddress.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                final String[] columnas = new String[] {
                        Chiringuitos._ID,
                        Chiringuitos.NAME,
                };
                cursor = managedQuery(uri, columnas, null, null,
                        Chiringuitos.NAME
                                + " ASC");
                cursor.setNotificationUri(getContentResolver(), uri);
                startManagingCursor(cursor);
                chiringuitos = new ArrayList<String>();
                while (cursor.moveToNext())
                    chiringuitos.add(cursor.getString(1));
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                        getApplicationContext(),
                        android.R.layout.simple_dropdown_item_1line,
                        chiringuitos);

                actvAddress.setAdapter(adapter);
            }
        });
        actvAddress.setVisibility(View.GONE);
        ibSearch = (ImageButton) findViewById(R.id.searchButton);
        ibSearch.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                // comprobamos si pulsamos en "buscar"
                if (showSearchButton) {
                    actvAddress.setVisibility(View.VISIBLE);
                    ibSearch.setImageResource(R.drawable.bu_map_go);
                    showSearchButton = false;
                } else { // pulsamos en "ir"
                    getApplicationContext();
                    // ocultamos el teclado virtual de android
                    InputMethodManager imm = (InputMethodManager)
                            getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(actvAddress.getWindowToken(), 0);
                    searchAddress();
                    actvAddress.setVisibility(View.GONE);
                    ibSearch.setImageResource(R.drawable.bu_map_search);
                    showSearchButton = true;
                }
            }
        });
        mMap = (MapView) findViewById(R.id.map);
        mMap.displayZoomControls(true);
        mMap.setBuiltInZoomControls(true);
        mController = mMap.getController();
        mController.setZoom(14);
        mController.setCenter(new GeoPoint((int) (41.394196 * 1E6),
                (int) (2.211299 * 1E6)));
        // a–adimos los Overlays al mapa
        initializeOverlays();
        // LOCATION
        mLocation = new MyLocationOverlay(mContext, mMap);
        mLocation.runOnFirstFix(new Runnable() {

            public void run() {
                mController.animateTo(mLocation.getMyLocation());
            }

        });
        mMap.getOverlays().add(mLocation);
    }

    @Override
    protected void onStart() {
        super.onStart();
        try {
            Intent i = getIntent();
            if (i.hasExtra("idChiringuito") && i.hasExtra("centered")) {
                Bundle extras = getIntent().getExtras();
                long idChiringuito = -1;
                idChiringuito = extras.getLong("idChiringuito");
                boolean centered = extras.getBoolean("centered");
                if (centered) {
                    mController.setCenter(itemizedOverlay
                            .getLocalization(idChiringuito));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mLocation != null) {
            mLocation.enableMyLocation();
            mLocation.enableCompass();
        }
    }

    @Override
    protected void onPause() {
        if (mLocation != null) {
            mLocation.disableMyLocation();
            mLocation.disableCompass();
        }
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        if (cursor != null)
            cursor.close();
        if (mOverlays != null)
            mOverlays.clear();
        if (chiringuitos != null)
            chiringuitos.clear();
        super.onDestroy();
    }

    /*
     * MŽtodo que inicializa los overlays a mostar sobre el mapa as’ como sus
     * respectivos eventos para mover el mapa, o crear/editar un lugar en el
     * mapa, simplemente manteniendo el dedo un par de segundos sobre un punto.
     */
    private void initializeOverlays() {
        mOverlays = mMap.getOverlays();
        mOverlays.clear();

        final String[] columns = new String[] {
                Chiringuitos._ID,
                Chiringuitos.LATITUDE,
                Chiringuitos.LONGITUDE,
                Chiringuitos.NAME
        };
        cursor = managedQuery(uri, columns, null, null, Chiringuitos._ID);
        cursor.setNotificationUri(getContentResolver(), uri);
        startManagingCursor(cursor);
        itemizedOverlay = new MapIndicatorOverlay(this,
                getResources().getDrawable(R.drawable.indicator));
        while (cursor.moveToNext()) {
            itemizedOverlay.addLocalization(cursor.getLong(0),
                    cursor.getFloat(1),
                    cursor.getFloat(2),
                    cursor.getString(3));
        }
        if (cursor.getCount() > 0)
            mOverlays.add(itemizedOverlay);
    }

    @Override
    protected boolean isRouteDisplayed() {
        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_map, menu);
        return true;
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        Dialog diag = ARLocationManager.getInstance(this).onCreateDialog(this,
                id);
        if (diag != null)
            return diag;

        switch (id) {
            case R.id.menuAbout:
                LayoutInflater factory2 = LayoutInflater.from(this);
                View textEntryView2 = factory2.inflate(R.layout.custom_dialog,
                        null);

                TextView text2 = (TextView) textEntryView2
                        .findViewById(R.id.dialog_text);
                text2.setText(getString(R.string.app_name) + " "
                        + getString(R.string.version_arviewer) +
                        getString(R.string.revision_arviewer) + "\n"
                        + getString(R.string.about_message));
                return new AlertDialog.Builder(this)
                        .setIcon(R.drawable.ic_menu_about)
                        .setTitle(R.string.about_title)
                        .setView(textEntryView2)
                        .setPositiveButton(R.string.ok, new OnClickListener() {

                            public void onClick(DialogInterface dialog,
                                    int which) {

                            }
                        })
                        .setNeutralButton(R.string.about_web,
                                new OnClickListener() {

                                    public void onClick(DialogInterface dialog,
                                            int which) {
                                        Intent myIntent = new Intent(
                                                Intent.ACTION_VIEW,
                                                Uri.parse(getString(R.string.urlServer)));
                                        startActivity(myIntent);
                                    }
                                })
                        .create();
        }
        return null;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent i = null;
        switch (item.getItemId()) {
            case R.id.menuAbout:
                showDialog(R.id.menuAbout);
                return true;
            case R.id.menuChiringuito:
                i = new Intent();
                i.setClass(MapChiringuitoActivity.this,
                        ARviewer.class);
                startActivity(i);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /*
     * MŽtodo para buscar la direcci—n introducida por el usuario, y en caso de
     * encontrarla, desplazar el mapa hasta dicho punto.
     */
    protected void searchAddress() {
        final String address = actvAddress.getText().toString();
        // evitamos que se busque "Direcci—n a buscar"
        if (address.length() == 0) {
            return;
        }
        GeoPoint p = null;

        try {
            // miramos si es un lugar conocido
            final String[] columnas = new String[] {
                    Chiringuitos._ID,
                    Chiringuitos.NAME,
                    Chiringuitos.LATITUDE,
                    Chiringuitos.LONGITUDE
            };
            cursor = managedQuery(uri, columnas,
                    Chiringuitos.NAME + "='" + address + "'", null, null);
            cursor.setNotificationUri(getContentResolver(), uri);
            startManagingCursor(cursor);
            if (cursor.moveToFirst()) {
                p = new GeoPoint((int) (cursor.getFloat(2) * 1E6),
                        (int) (cursor.getFloat(3) * 1E6));
            } else {
                Geocoder geo = new Geocoder(MapChiringuitoActivity.this);
                List<Address> results;
                results = geo.getFromLocationName("" + address, 10);
                if (results.size() > 0) {
                    p = new GeoPoint((int) (results.get(0)
                            .getLatitude() * 1E6), (int) (results.get(0)
                            .getLongitude() * 1E6));

                }
            }
            if (p != null) {
                // Toast.makeText(getApplicationContext(),
                // getText(R.string.toast_ir_a_lugar) + " " + lugar,
                // Toast.LENGTH_SHORT).show();
                mController.animateTo(p);
                mMap.invalidate();
            } else {
                // Toast.makeText(getApplicationContext(),
                // R.string.toast_lugar_no_encontrado,
                // Toast.LENGTH_SHORT).show();
            }
            actvAddress.setText("");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
