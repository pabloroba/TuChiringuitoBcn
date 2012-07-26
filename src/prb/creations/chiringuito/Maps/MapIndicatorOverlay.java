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

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.location.Address;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;

import prb.creations.chiringuito.ShowChiringuitoActivity;

public class MapIndicatorOverlay extends
        ItemizedOverlay<ChiringuitoOverlayItem> {
    private ArrayList<ChiringuitoOverlayItem> mOverlays = new ArrayList<ChiringuitoOverlayItem>();
    private Context context;

    public MapIndicatorOverlay(Context context, Drawable defaultMarker) {
        super(boundCenterBottom(defaultMarker));
        this.context = context;
    }

    @Override
    protected ChiringuitoOverlayItem createItem(int i) {
        return mOverlays.get(i);
    }

    @Override
    public int size() {
        return mOverlays.size();
    }

    @Override
    public boolean onTap(int index) {
        ChiringuitoOverlayItem item = mOverlays.get(index);
        Intent i = new Intent();
        i.setClass(context, ShowChiringuitoActivity.class);
        i.putExtra("ChiringoID", item.getRowID());
        context.startActivity(i);
        return super.onTap(index);
    }

    public void addLocalization(long id, double lat, double lon, String etiqueta) {
        int lt = (int) (lat * 1E6);
        int ln = (int) (lon * 1E6);
        GeoPoint punto = new GeoPoint(lt, ln);
        ChiringuitoOverlayItem item = new ChiringuitoOverlayItem(punto,
                etiqueta, id, null);
        mOverlays.add(item);
        populate();
    }

    public void addLocalization(Address direccion) {
        GeoPoint punto = new GeoPoint((int) (direccion.getLatitude() * 1E6),
                (int) (direccion.getLongitude() * 1E6));

        ChiringuitoOverlayItem item = new ChiringuitoOverlayItem(punto,
                direccion.getAddressLine(0), null);

        mOverlays.add(item);
        populate();
    }

    public GeoPoint getLocalization(long id) {
        GeoPoint lugar = null;
        for (int i = 0; i < mOverlays.size(); i++) {
            if (mOverlays.get(i).getRowID() == id)
                lugar = mOverlays.get(i).getPoint();
        }

        return lugar;
    }

    public void clear() {
        mOverlays.clear();
        populate();
    }

}
