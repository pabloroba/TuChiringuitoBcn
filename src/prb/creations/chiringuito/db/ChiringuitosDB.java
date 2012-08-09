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

package prb.creations.chiringuito.db;

import android.provider.BaseColumns;

public class ChiringuitosDB {
	/*
	 * Nombre de la base de datos
	 */
	public static final String DB_NAME = "chiringuitos.db";
	/*
	 * Version de la base de datos
	 */
	public static final int DB_VERSION = 1;

	/*
	 * Esta clase no debe ser instanciada
	 */
	private ChiringuitosDB() {
	}

	/*
	 * Definicion de la tabla lugares
	 */
	public static final class Chiringuitos implements BaseColumns {
		/*
		 * Abstraccion de los nombres de campos y tabla a constantes para
		 * facilitar cambios en la estructura de la BD
		 */
		public static final String TABLE_NAME = "chiringuitos";
		public static final String _ID = "_id";
		public static final String NAME = "name";
		public static final String INFO = "info";
		public static final String WEB_LINK = "web_link";
		public static final String LATITUDE = "latitude";
		public static final String LONGITUDE = "longitude";
		public static final String PHOTO = "picture";
		public static final String SOURCE = "source";
		public static final String _COUNT = "8";
	}
}
