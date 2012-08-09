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

package prb.creations.chiringuito;

import prb.creations.chiringuito.ARviewer.ARviewer;
import prb.creations.chiringuito.rss.RssDownloadHelper;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;

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

        ChiringuitoApp app = (ChiringuitoApp) getApplication();
        DownloadChiringuitosTask task = new DownloadChiringuitosTask();
        task.execute(app.getRssUrl());

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

    private class DownloadChiringuitosTask extends
            AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... url) {

            RssDownloadHelper.updateRssData(url[0], getContentResolver());
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
        }
    }

}
