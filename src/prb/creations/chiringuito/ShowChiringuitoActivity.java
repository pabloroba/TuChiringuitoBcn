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

package prb.creations.chiringuito;

import com.libresoft.sdk.ARviewer.Utils.BitmapUtils;

import prb.creations.chiringuito.ARviewer.ARviewer;
import prb.creations.chiringuito.ARviewer.Location.ARLocationManager;
import prb.creations.chiringuito.db.ChiringuitoProvider;
import prb.creations.chiringuito.db.ChiringuitosDB.Chiringuitos;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentUris;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class ShowChiringuitoActivity extends Activity {
    private TextView tvName;
    private ImageView ivPhoto;
    private TextView tvInfo;
    private Button bLink;

    private long chiringuitoID;
    private Bitmap bmPhoto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.show_chiringuito);
        setTitle(R.string.app_name);
        tvName = (TextView) findViewById(R.id.chiringuitoName);
        ivPhoto = (ImageView) findViewById(R.id.chiringuitoPhoto);
        tvInfo = (TextView) findViewById(R.id.chiringuitoInfo);
        bLink = (Button) findViewById(R.id.buttonLink);

        bmPhoto = null;
    }

    @Override
    protected void onStart() {
        super.onStart();
        final String[] cols = new String[] {
                Chiringuitos._ID,
                Chiringuitos.NAME,
                Chiringuitos.INFO,
                Chiringuitos.PHOTO,
                Chiringuitos.WEB_LINK
        };

        Uri uri = ChiringuitoProvider.CONTENT_URI;
        Intent i = getIntent();
        Cursor cursor = null;
        try {
            if (i.hasExtra("ChiringoID")) {
                chiringuitoID = i.getExtras().getLong("ChiringoID");

                uri = ContentUris.withAppendedId(uri, chiringuitoID);

                cursor = managedQuery(uri, cols, null, null,
                        Chiringuitos.NAME);

            } else {
                return;
            }
                
            cursor.setNotificationUri(getContentResolver(), uri);
            startManagingCursor(cursor);

            if (cursor.moveToFirst()) {
                String sName = cursor.getString(1);
                String sInfo = cursor.getString(2);
                String sPhoto = cursor.getString(3);
                final String sLink = cursor.getString(4);

                tvName.setText(sName);
                if (sInfo != null && sInfo.length() > 0)
                    tvInfo.setText(sInfo);
                if (sPhoto != null && sPhoto.length() > 0) {
                    try {
                        bmPhoto = BitmapUtils.loadBitmap(sPhoto);
                        ivPhoto.setImageBitmap(bmPhoto);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                if (sLink != null && sLink.length() > 0) {
                    bLink.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            Intent browserIntent = new Intent(
                                    Intent.ACTION_VIEW, Uri.parse(sLink));
                            startActivity(browserIntent);
                        }
                    });
                } else {
                    bLink.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            Intent browserIntent = new Intent(
                                    Intent.ACTION_VIEW,
                                    Uri.parse("http://www.tuchiringuitobcn.com/"));
                            startActivity(browserIntent);
                        }
                    });
                }
            } 
        } catch (Exception e) {
            e.printStackTrace();
        }

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
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_map, menu);
        return true;
    }

    @Override
    protected void onDestroy() {
        if (bmPhoto != null)
            bmPhoto.recycle();
        super.onDestroy();
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
                i.setClass(ShowChiringuitoActivity.this,
                        ARviewer.class);
                startActivity(i);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
