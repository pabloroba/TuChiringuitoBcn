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

package prb.creations.chiringuito.db;

import prb.creations.chiringuito.db.ChiringuitosDB.Chiringuitos;
import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;

/*
 * Content Provider to easy access DB
 */
public class ChiringuitoProvider extends ContentProvider {
    public static final String PROVIDER_NAME = "prb.creations.chiringuito";
    public static final Uri CONTENT_URI = Uri.parse("content://"
            + PROVIDER_NAME + "/chiringuitos");
    private static final int CHIRINGUITOS = 1;
    private static final int CHIRINGUITOS_ID = 2;
    private static final UriMatcher uriMatcher;
    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(PROVIDER_NAME, "chiringuitos", CHIRINGUITOS);
        uriMatcher.addURI(PROVIDER_NAME, "chiringuitos/#", CHIRINGUITOS_ID);
    }
    private SQLiteDatabase chiringuitosDB;

    @Override
    public int delete(Uri uri, String where, String[] whereArgs) {
        int count = 0;
        switch (uriMatcher.match(uri)) {
            case CHIRINGUITOS:
                count = chiringuitosDB.delete(Chiringuitos.TABLE_NAME, where,
                        whereArgs);
                break;
            case CHIRINGUITOS_ID:
                String id = uri.getPathSegments().get(1);
                count = chiringuitosDB.delete(Chiringuitos.TABLE_NAME,
                        Chiringuitos._ID
                                + " = "
                                + id
                                + (!TextUtils.isEmpty(where) ? " AND (" + where
                                        + ')' : ""), whereArgs);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    @Override
    public String getType(Uri uri) {
        switch (uriMatcher.match(uri)) {
        // para un conjunto de lugares
            case CHIRINGUITOS:
                return "vnd.android.cursor.dir/vnd.creations.chiringuito";
                // para un solo lugar
            case CHIRINGUITOS_ID:
                return "vnd.android.cursor.item/vnd.creations.chiringuito";
            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        // a–ade un nuevo lugar
        long rowID = chiringuitosDB.insert(Chiringuitos.TABLE_NAME, "",
                values);
        // si todo ha ido ok devolvemos su Uri
        if (rowID > 0) {
            Uri _uri = ContentUris.withAppendedId(CONTENT_URI, rowID);
            getContext().getContentResolver().notifyChange(_uri, null);
            return _uri;
        }
        throw new SQLException("Failed to insert row into " + uri);
    }

    @Override
    public boolean onCreate() {
        Context context = getContext();
        ChiringuitoSQLHelper dbHelper = new ChiringuitoSQLHelper(context);
        chiringuitosDB = dbHelper.getWritableDatabase();
        return (chiringuitosDB == null) ? false : true;
    }
    
    public SQLiteDatabase getDBHandle() {
        return chiringuitosDB;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
            String[] selectionArgs, String sortOrder) {
        SQLiteQueryBuilder sqlBuilder = new SQLiteQueryBuilder();
        sqlBuilder.setTables(Chiringuitos.TABLE_NAME);
        if (uriMatcher.match(uri) == CHIRINGUITOS_ID) {
            sqlBuilder.appendWhere(Chiringuitos._ID + " = "
                    + uri.getPathSegments().get(1));
        }
        if (sortOrder == null || sortOrder == "") {
            sortOrder = Chiringuitos.NAME;
        }
        Cursor c = sqlBuilder.query(chiringuitosDB, projection, selection,
                selectionArgs, null, null, sortOrder);
        c.setNotificationUri(getContext().getContentResolver(), uri);
        return c;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
            String[] selectionArgs) {
        int count = 0;
        switch (uriMatcher.match(uri)) {
            case CHIRINGUITOS:
                count = chiringuitosDB.update(Chiringuitos.TABLE_NAME, values,
                        selection, selectionArgs);
                break;
            case CHIRINGUITOS_ID:
                count = chiringuitosDB.update(Chiringuitos.TABLE_NAME, values,
                        Chiringuitos._ID
                                + " = "
                                + uri.getPathSegments().get(1)
                                + (!TextUtils.isEmpty(selection) ? " AND ("
                                        + selection + ')' : ""), selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

}
