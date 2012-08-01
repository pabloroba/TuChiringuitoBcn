package prb.creations.chiringuito.rss;

import java.net.URL;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import android.content.ContentResolver;

public class RssDownloadHelper {
	public static void updateRssData (String rssUrl, ContentResolver contentResolver) {
		try {
			URL url = new URL(rssUrl);

			SAXParserFactory spf = SAXParserFactory.newInstance();
			SAXParser saxParser = spf.newSAXParser();

			RssHandler rssHandler = new RssHandler(contentResolver);

			saxParser.setProperty("http://xml.org/sax/properties/lexical-handler", rssHandler);

			XMLReader xr = saxParser.getXMLReader();
			xr.setContentHandler(rssHandler);

			InputSource is = new InputSource(url.openStream());
			is.setEncoding("utf-8");
			xr.parse(is);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
