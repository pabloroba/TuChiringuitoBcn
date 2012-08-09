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

package prb.creations.chiringuito.ARviewer.Utils;

import java.util.ArrayList;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import prb.creations.chiringuito.R;
import prb.creations.chiringuito.TuChiringuitoBcn;
import prb.creations.chiringuito.ARviewer.Location.ARLocationManager;
//import prb.creations.chiringuito.ARviewerPlaces.LibreGeoSocial;
import com.libresoft.sdk.ARviewer.Types.GenericLayer;
import com.libresoft.sdk.ARviewer.Types.GeoNode;

public class AsyncLGSNodes extends AsyncTask<Void, Void, Void> {

    private GenericLayer mLayer = null;
    private OnExecutionFinishedListener onExecutionFinishedListener = null;
    private Context mContext;
    private ProgressDialog pd;

    public AsyncLGSNodes(Context mContext, GenericLayer mLayer,
            OnExecutionFinishedListener onExecutionFinishedListener) {
        this.mLayer = mLayer;
        this.onExecutionFinishedListener = onExecutionFinishedListener;
        this.mContext = mContext;
    }

    protected void onPreExecute() {
        // LibreGeoSocial.getInstance().setUrl(
        // mContext.getString(R.string.urlServer));
        // LibreGeoSocial.getInstance().setFormat("JSON");
        pd = ProgressDialog.show(mContext, "",
                mContext.getString(R.string.loading), true, true);
    }

    @Override
    protected Void doInBackground(Void... unused) {
        try {
            ArrayList<GeoNode> mNodeList = mLayer.getNodes();
            if (mNodeList != null)
                mNodeList.clear();
            ArrayList<GenericLayer> mLayers = TuChiringuitoBcn.getInstance(
                    mContext)
                    .getLayerList();

            for (int i = 0; i < mLayers.size(); i++)
                mNodeList.addAll(TuChiringuitoBcn.getInstance(mContext)
                        .getLayerNodes(
                                mLayers.get(i).getId(),
                                "",
                                "0",
                                ARLocationManager.getInstance(mContext)
                                        .getLocation()
                                        .getLatitude(),
                                ARLocationManager.getInstance(mContext)
                                        .getLocation()
                                        .getLongitude(),
                                10.0,
                                0,
                                5));
        } catch (Exception e) {
            Log.e("AsyncLGSNodes", "", e);
        }

        return null;
    }

    protected void onPostExecute(Void unused) {
        pd.dismiss();
        if (onExecutionFinishedListener != null)
            onExecutionFinishedListener.onFinish();
    }

    public interface OnExecutionFinishedListener {
        public abstract void onFinish();
    }
}
