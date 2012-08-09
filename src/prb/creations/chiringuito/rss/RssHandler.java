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

package prb.creations.chiringuito.rss;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.ext.LexicalHandler;
import org.xml.sax.helpers.DefaultHandler;

import prb.creations.chiringuito.db.ChiringuitoProvider;
import prb.creations.chiringuito.db.ChiringuitosDB.Chiringuitos;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.net.Uri;

public class RssHandler extends DefaultHandler implements LexicalHandler {
    protected final String RSS = "rss";

    protected final String CHANNEL = "channel";
    protected final String CHANNEL_TITLE = "title";
    protected final String CHANNEL_LINK = "link";
    protected final String CHANNEL_DESCRIPTION = "description";
    protected final String CHANNEL_IMAGE = "image";
    protected final String CHANNEL_IMAGE_URL = "url";

    protected final String ITEM = "item";
    protected final String ITEM_TITLE = "title";
    protected final String ITEM_LINK = "link";
    protected final String ITEM_DESCRIPTION = "description";
    protected final String ITEM_ENCLOSURE = "enclosure";
    protected final String ITEM_SOURCE = "source";
    protected final String ITEM_COMMENTS = "comments";

    ContentValues rssItem;

    private boolean in_item = false;
    private boolean in_title = false;
    private boolean in_link = false;
    private boolean in_description = false;
    private boolean in_enclosure = false;
    private boolean in_source = false;
    private boolean in_comments = false;

    private ContentResolver contentProv;
    final private Uri uri = ChiringuitoProvider.CONTENT_URI;

    public RssHandler(ContentResolver contentResolver) {
        this.contentProv = contentResolver;
    }

    @Override
    public void characters(char[] ch, int start, int length)
            throws SAXException {
        if (in_item) {
            if (in_title) {
                rssItem.put(Chiringuitos.NAME, new String(ch, start, length));
            } else if (in_link) {
                rssItem.put(Chiringuitos.WEB_LINK,
                        new String(ch, start, length));
            } else if (in_comments) {
                String strCoords = new String(ch, start, length);
                String[] coords = strCoords.split(",");
                double lat = Double.parseDouble(coords[0]);
                double lon = Double.parseDouble(coords[1]);
                rssItem.put(Chiringuitos.LATITUDE, lat);
                rssItem.put(Chiringuitos.LONGITUDE, lon);
            } else if (in_description) {
                rssItem.put(Chiringuitos.INFO, new String(ch, start, length));
            } else if (in_enclosure) {
                rssItem.put(Chiringuitos.PHOTO, new String(ch, start,
                        length));
            } else if (in_source) {
                rssItem.put(Chiringuitos.SOURCE, new String(ch, start,
                        length));
            }
        }
    }

    @Override
    public void endElement(String namespaceURI, String localName, String qName)
            throws SAXException {
        if (localName.equalsIgnoreCase(ITEM)) {
            contentProv.insert(uri, rssItem);
            rssItem = new ContentValues();
            in_item = false;
        } else if (localName.equalsIgnoreCase(ITEM_TITLE)) {
            in_title = false;
        } else if (localName.equalsIgnoreCase(ITEM_LINK)) {
            in_link = false;
        } else if (localName.equalsIgnoreCase(ITEM_DESCRIPTION)) {
            in_description = false;
        } else if (localName.equalsIgnoreCase(ITEM_ENCLOSURE)) {
            in_enclosure = false;
        } else if (localName.equalsIgnoreCase(ITEM_SOURCE)) {
            in_source = false;
        } else if (localName.equalsIgnoreCase(ITEM_COMMENTS)) {
            in_comments = false;
        }
    }

    @Override
    public void startElement(String uri, String localName, String qName,
            Attributes attributes) throws SAXException {
        if (localName.equalsIgnoreCase(ITEM)) {
            in_item = true;
            rssItem = new ContentValues();
        } else if (localName.equalsIgnoreCase(ITEM_TITLE)) {
            in_title = true;
        } else if (localName.equalsIgnoreCase(ITEM_LINK)) {
            in_link = true;
        } else if (localName.equalsIgnoreCase(ITEM_DESCRIPTION)) {
            in_description = true;
        } else if (localName.equalsIgnoreCase(ITEM_ENCLOSURE)) {
            in_enclosure = true;
        } else if (localName.equalsIgnoreCase(ITEM_SOURCE)) {
            in_source = true;
        } else if (localName.equalsIgnoreCase(ITEM_COMMENTS)) {
            in_comments = true;
        }
    }

    public void comment(char[] ch, int start, int length) throws SAXException {

    }

    public void endCDATA() throws SAXException {

    }

    public void endDTD() throws SAXException {

    }

    public void endEntity(String name) throws SAXException {

    }

    public void startCDATA() throws SAXException {

    }

    public void startDTD(String name, String publicId, String systemId)
            throws SAXException {

    }

    public void startEntity(String name) throws SAXException {

    }

}
