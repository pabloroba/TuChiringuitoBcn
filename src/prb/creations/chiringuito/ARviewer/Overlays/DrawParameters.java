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

package prb.creations.chiringuito.ARviewer.Overlays;

import prb.creations.chiringuito.ARviewer.ARCompassManager;
import prb.creations.chiringuito.ARviewer.ARUtils;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;

public class DrawParameters extends View {

    private static final int COLUMN = 10;
    private static float TEXT_SIZE;
    private static float LINE_AZIMUTH;
    private static float LINE_ELEVATION;
    private static float LINE_POSITION;

    private float mValues[] = new float[3];
    private float coords[] = null;
    private float altitude = 0;

    public DrawParameters(Context context) {
        super(context);
        TEXT_SIZE = ARUtils.transformPixInDip(context, 10);
        LINE_AZIMUTH = TEXT_SIZE + ARUtils.transformPixInDip(context, 2.5f);
        LINE_ELEVATION = LINE_AZIMUTH * 2;
        LINE_POSITION = LINE_AZIMUTH * 3;
    }

    public void setValues(float[] values, float[] location, float altitude) {
        if (values != null)
            mValues = values.clone();
        coords = location.clone();
        this.altitude = altitude;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        float azimuth = 0, elevation = 0;
        int h = canvas.getHeight();

        Paint paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        paint.setTextSize(TEXT_SIZE);
        paint.setColor(Color.RED);

        azimuth = ARCompassManager.getAzimuth(mValues);
        elevation = ARCompassManager.getElevation(mValues);

        canvas.drawText("Azimuth = " + Float.toString(azimuth) + "Âº", COLUMN,
                h - LINE_AZIMUTH, paint);
        canvas.drawText("Elevation = " + Float.toString(elevation) + "Âº",
                COLUMN, h - LINE_ELEVATION, paint);

        if (coords != null)
            canvas.drawText("(" +
                    Float.toString(coords[0]) + "Âº, " +
                    Float.toString(coords[1]) + "Âº, " +
                    Float.toString(altitude) + "m.)", COLUMN,
                    h - LINE_POSITION, paint);
        super.onDraw(canvas);
    }
}
