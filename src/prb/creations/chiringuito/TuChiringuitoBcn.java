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
 *           Juan Francisco Gato Luis <jfcogato@gsyc.es
 *           Raœl Rom‡n L—pez <rroman@gsyc.es>
 *
 */

package prb.creations.chiringuito;

import com.libresoft.sdk.ARviewer.Types.GenericLayer;
import com.libresoft.sdk.ARviewer.Types.GeoNode;
import com.libresoft.sdk.ARviewer.Types.GeoNodePositionComparator;

import prb.creations.chiringuito.db.ChiringuitoProvider;
import prb.creations.chiringuito.db.ChiringuitosDB.Chiringuitos;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;

public class TuChiringuitoBcn {
    static private String TAG = "TuChiringuitoBcn";
    static private TuChiringuitoBcn singleton = null;
    static private Context mContext = null;

    static public TuChiringuitoBcn getInstance(Context c) {
        mContext = c;
        return getInstance();
    }

    static public TuChiringuitoBcn getInstance() {
        if (singleton == null) {
            singleton = new TuChiringuitoBcn();
        }
        return singleton;
    }

    public ArrayList<GenericLayer> getLayerList() {

        String layer_type, name, description, since;
        Integer id = 0;
        Double altitude, distance;
        String position_since;

        layer_type = name = description = since = position_since = "";
        distance = 0.0;
        altitude = -10000.0;

        ArrayList<GenericLayer> list = new ArrayList<GenericLayer>();

        GenericLayer mLayer = new GenericLayer(id, layer_type, name,
                description, since,
                null, null, altitude, distance, position_since);

        list.add(mLayer);

        return list;

    }

    public ArrayList<GeoNode> getLayerNodes(Integer id, String pattern,
            String category, Double latitude, Double longitude,
            Double distance,
            Integer page, Integer elems)
    {
        try
        {
            if (pattern == null)
                pattern = "";

            ArrayList<GeoNode> array = new ArrayList<GeoNode>();
            ArrayList<GeoNode> aux = null;
            array.clear();
            aux = parseNodes();
            if (aux != null)
                array.addAll(aux);

            Log.e("LGS: size array=", String.valueOf(array.size()));

            return array;
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
            return null;
        }
    }

    private ArrayList<GeoNode> parseNodes() {
        ArrayList<GeoNode> nodes = new ArrayList<GeoNode>();
        nodes.clear();
        final String[] cols = new String[] {
                Chiringuitos._ID,
                Chiringuitos.NAME,
                Chiringuitos.LATITUDE,
                Chiringuitos.LONGITUDE,
                Chiringuitos.PHOTO,
                Chiringuitos.WEB_LINK
        };
        Uri uri = ChiringuitoProvider.CONTENT_URI;
        try {
            Cursor cursor = mContext.getContentResolver().query(uri, cols,
                    null, null, Chiringuitos._ID + " ASC");
            int i = 0;
            while (cursor.moveToNext()) {
                long id = cursor.getLong(0);
                String name = cursor.getString(1);
                double lat = cursor.getDouble(2);
                double lon = cursor.getDouble(3);
                String sUrl = cursor.getString(4);
                String link = cursor.getString(5);
                Chiringuito node = new Chiringuito(i, lat, lon, 0.0, 1.0, name,
                        "", sUrl, link, id, null, null, null);

                nodes.add(node);
                i++;
            }
            cursor.close();

            Collections.sort(nodes, new GeoNodePositionComparator());

        } catch (Exception e) {
            e.printStackTrace();
        }
        return nodes;
    }
}
