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

import com.libresoft.sdk.ARviewer.Types.GenericLayer;
import com.libresoft.sdk.ARviewer.Types.GeoNode;

import prb.creations.chiringuito.R;
import prb.creations.chiringuito.ARviewer.Overlays.CustomViews;
import prb.creations.chiringuito.ARviewer.Utils.AsyncLGSNodes;
import prb.creations.chiringuito.ARviewer.Utils.AsyncLGSNodes.OnExecutionFinishedListener;

import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;

import java.util.ArrayList;

public class ARviewer extends ARBase {

    private static final int MENU_DISTANCE_FILTER = 101;
    private static final int DIALOG_EMPTY = 101;

    OnClickListener distFiltClickListener = new OnClickListener() {

        public void onClick(View v) {
            showMenu = true;
            getLayers().removeExtraElement((View) v.getParent());
            float dist = (float) (CustomViews.getSeekbarValue() * 1E3);
            if (distanceFilter != dist) {
                distanceFilter = dist;
                showResources();
            }
        }
    };

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        loadConfig(false);
        showResources();
    }

    @Override
    protected boolean loadParameters() {
        super.loadParameters();
        if (!getIntent().hasExtra("LAYER"))
            return false;
        else {
            setMyLayer((GenericLayer) getIntent().getSerializableExtra("LAYER"));
        }

        return true;
    }

    @Override
    public void showResources() {
        super.showResources();
        if (getResourcesList() == null) {
            setMyLayer(new GenericLayer(0, "", "Chiringuitos Layer",
                    "A layer to show Chiringuitos from Barcelona beach", null,
                    null,
                    null, null, null, null));
            getMyLayer().setNodes(new ArrayList<GeoNode>());
            new AsyncLGSNodes(this, getMyLayer(),
                    new OnExecutionFinishedListener() {
                        @Override
                        public void onFinish() {
                            if (!isFinishing())
                                showResources();
                        }
                    }).execute();
            showDialog(DIALOG_EMPTY);
            return;
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.clear();

        if (showMenu) {
            menu.add(0, MENU_DISTANCE_FILTER, 0, R.string.menu_distance)
                    .setIcon(R.drawable.ic_menu_filter);
        }

        super.onPrepareOptionsMenu(menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case MENU_DISTANCE_FILTER:
                View view = CustomViews.createSeekBars(this,
                        distanceFilter / 1E3, 50, " Km.", 10, 0,
                        distFiltClickListener);

                getLayers().addExtraElement(
                        view,
                        new LayoutParams(LayoutParams.FILL_PARENT,
                                LayoutParams.WRAP_CONTENT));

                showMenu = false;
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected Dialog onCreateDialog(int id) {

        switch (id) {

            case DIALOG_EMPTY:
                break;
        }
        return super.onCreateDialog(id);

    }
}
