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
 *  		 Juan Francisco Gato Luis <jfcogato@gsyc.es
 *  		 Raœl Rom‡n L—pez <rroman@gsyc.es>
 *
 */

package prb.creations.chiringuito;

import prb.creations.chiringuito.ARviewer.ARviewer;
import prb.creations.chiringuito.db.ChiringuitoProvider;
import prb.creations.chiringuito.db.ChiringuitosDB.Chiringuitos;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import java.net.URL;

public class Splash extends Activity {
    private static final int ACTIVITY_RESULT = 1;

    private int sleepTime = 4000; // 4 seg
    protected Intent startIntent = null;

    private Handler altHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            final ImageView iv = (ImageView) findViewById(R.id.iv_splash);
            Animation anim = AnimationUtils.loadAnimation(getBaseContext(),
                    R.anim.zoom_exit);
            anim.setAnimationListener(new AnimationListener() {

                @Override
                public void onAnimationStart(Animation animation) {
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    iv.setImageBitmap(null);
                    iv.invalidate();

                    if (!isFinishing() && (startIntent != null)) {
                        if (getIntent().getExtras() != null)
                            startIntent.putExtras(getIntent().getExtras());
                        startActivityForResult(startIntent, ACTIVITY_RESULT);
                    } else {
                        finish();
                    }
                }
            });
            ((RelativeLayout) findViewById(R.id.rl_splash))
                    .startAnimation(anim);

        }
    };

    protected void onCreate(Bundle savedInstanceState) {
        startIntent = new Intent(getBaseContext(), ARviewer.class);

        super.onCreate(savedInstanceState);

        setContentView(R.layout.splash);

        Thread splashThread = new Thread() {

            @Override
            public void run() {

                try {
                    Thread.sleep(sleepTime);
                    if (altHandler != null)
                        altHandler.sendEmptyMessage(0);

                } catch (InterruptedException e) {
                    Log.e("Splash", "", e);
                }
            }
        };

        splashThread.start();

        generateChiringuitosDB();

    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (requestCode) {

            case ACTIVITY_RESULT:

                if (resultCode != Activity.RESULT_CANCELED) {
                    setResult(RESULT_OK, data);
                }
                finish();
                break;

            default:
                break;
        }
    }

    private void generateChiringuitosDB() {
        resetContentResolver();
        String names[] = new String[] {}, locations[] = new String[] {};
        int n = (int) (names.length / 3);
        for (int i = 0; i < n; i++) {
            ContentValues values = new ContentValues();
            values.put(Chiringuitos.NAME, names[3 * i]);
            values.put(Chiringuitos.WEB_LINK, names[3 * i + 1]);
            values.put(Chiringuitos.INFO, names[3 * i + 2]);
            values.put(Chiringuitos.LATITUDE, locations[2 * i]);
            values.put(Chiringuitos.LONGITUDE, locations[2 * i + 1]);
            URL url = getClassLoader().getResource(
                    "res/drawable/chir" + i + ".png");
            if (url != null)
                values.put(Chiringuitos.PHOTO, url.toString());

            getContentResolver()
                    .insert(ChiringuitoProvider.CONTENT_URI, values);
        }
    }

    private void resetContentResolver() {
        getContentResolver()
                .delete(ChiringuitoProvider.CONTENT_URI, null, null);
    }

}
