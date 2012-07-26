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

package prb.creations.chiringuito.ARviewer;

import prb.creations.chiringuito.ARviewer.Overlays.DrawUserStatus;
import prb.creations.chiringuito.ARviewer.Utils.CompassController;

import java.util.List;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

public class ARCompassManager implements SensorEventListener {

    OnCompassChangeListener onCompassChangeListener = null;
    private SensorManager sm;

    private float[] acc_values = new float[3];
    private float[] mag_values = new float[3];
    private float correction;
    // private float[] or_values = new float[3];
    private CompassController azimuthPID = new CompassController(0.8f, true);
    private CompassController elevationPID = new CompassController(0.5f, false);

    private DrawUserStatus drawUserStatus = null;

    public ARCompassManager(Context mContext) {

        sm = (SensorManager) mContext.getSystemService(Context.SENSOR_SERVICE);

        correction = 0;

    }

    public static float getAzimuth(float[] values) {
        return values[0];
    }

    public static float getElevation(float[] values) {
        return values[1];
    }

    private void setSensors() {
        // Setting a sensor listener for accelerometer and magnetic field
        // sensors

        try {
            List<Sensor> sensors = sm.getSensorList(Sensor.TYPE_ACCELEROMETER);

            if (sensors.size() > 0) {
                Sensor sensor = sensors.get(0);
                sm.registerListener(this, sensor,
                        SensorManager.SENSOR_DELAY_NORMAL);
            }

            sensors = sm.getSensorList(Sensor.TYPE_MAGNETIC_FIELD);

            if (sensors.size() > 0) {
                Sensor sensor = sensors.get(0);
                sm.registerListener(this, sensor,
                        SensorManager.SENSOR_DELAY_NORMAL);
            }

        } catch (Exception e) {
            Log.e("ARCompassManager", "", e);
        }
    }

    public void setOnCompassChangeListener(
            OnCompassChangeListener onCompassChangeListener) {
        if (this.onCompassChangeListener == null)
            setSensors();
        this.onCompassChangeListener = onCompassChangeListener;
    }

    public void setDrawUserStatusElement(DrawUserStatus drawUserStatus) {
        this.drawUserStatus = drawUserStatus;
    }

    public void unregisterListeners() {
        sm.unregisterListener(this);
        onCompassChangeListener = null;
    }

    private void OnCompassMeasure() {
        if (onCompassChangeListener == null)
            return;

        float[] values = new float[3];
        float[] inr = new float[9];
        float[] outr = new float[9];
        float[] i = new float[9];

        float[] values_acc = acc_values.clone();
        float[] values_mag = mag_values.clone();

        SensorManager.getRotationMatrix(inr, i, values_acc, values_mag);

        if (inr == null)
            return;

        SensorManager.remapCoordinateSystem(inr, SensorManager.AXIS_X,
                SensorManager.AXIS_Z, outr);

        SensorManager.getOrientation(outr, values);
        values[0] = (float) Math.toDegrees(values[0]);
        values[1] = (float) Math.toDegrees(values[1]);
        values[2] = (float) Math.toDegrees(values[2]);

        // correction of the measures
        // azimuth
        values[0] += correction;
        if (values[0] < 0)
            values[0] += 360;
        // elevation
        values[1] = 90 - values[1];

        values[0] = azimuthPID.getValue(values[0]);
        values[1] = elevationPID.getValue(values[1]);

        onCompassChangeListener.onChange(values);
    }

    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        switch (sensor.getType()) {
            case Sensor.TYPE_MAGNETIC_FIELD:
                if (drawUserStatus != null)
                    drawUserStatus.setCompassAccurate(accuracy);
                break;
        }
    }

    public void onSensorChanged(SensorEvent event) {
        switch (event.sensor.getType()) {
            case Sensor.TYPE_ACCELEROMETER:
                acc_values = event.values.clone();
                break;

            case Sensor.TYPE_MAGNETIC_FIELD:
                mag_values = event.values.clone();
                OnCompassMeasure();
                break;

        }

    }

    public interface OnCompassChangeListener {
        public abstract void onChange(float[] values);
    }

}
