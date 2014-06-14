package org.fbluemle.android.stockquery;

import android.net.Uri;
import android.util.Log;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Sahil on 6/14/2014.
 */
public class WebCall {

    private static final String TAG = "WebCall";
    private static final String ENDPOINT = "http://query.yahooapis.com/v1/public/yql";
    private static final String PARAM_QUERY = "q";
    private static final String PARAM_ENV = "env";
    private static final String ENV = "store://datatables.org/alltableswithkeys";

    String query = "select * from csv where url='http://download.finance.yahoo.com/d/quotes.csv?s=%s&f=nsd1l1c1p2ghp&e=.csv'";

    byte[] getURLBytes(String urlSpec) throws IOException {
        URL url = new URL(urlSpec);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = connection.getInputStream();
            ByteArrayOutputStream out = new ByteArrayOutputStream();

            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                return null;
            }

            int numOfBytesRead = 0;
            byte[] buffer = new byte[1024];
            while ((numOfBytesRead = in.read(buffer)) > 0) {
                out.write(buffer, 0, numOfBytesRead);
            }
            out.close();
            return out.toByteArray();
        } finally {
            connection.disconnect();
        }
    }

    public String getURL(String urlSpec) throws IOException {
        return new String(getURLBytes(urlSpec));
    }

    public Stock downloadStockInfo(String url) {
        Stock stock = null;
        try {
            String xmlString = getURL(url);
            Log.i(TAG, "Received xml: " + xmlString);
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            XmlPullParser parser = factory.newPullParser();
            parser.setInput(new StringReader(xmlString));
            stock = parse(parser);
        } catch (IOException e) {
            Log.e(TAG, "Failed to fetch items " + e);
        } catch (XmlPullParserException xppe) {
            Log.e(TAG, "Failed to parse items", xppe);
        }
        return stock;
    }

    public Stock fetchStock(String symbol) {
        String url = Uri.parse(ENDPOINT).buildUpon()
                .appendQueryParameter(PARAM_QUERY, String.format(query, symbol))
                .appendQueryParameter(PARAM_ENV, ENV)
                .toString();
        Log.d(TAG, url);
        return downloadStockInfo(url);
    }

    public Stock parse(XmlPullParser parser)
            throws XmlPullParserException, IOException {
        int eventType = parser.next();
        Stock stock = null;
        while (eventType != XmlPullParser.END_DOCUMENT) {
            String tagName = parser.getName();
            if (eventType == XmlPullParser.START_TAG) {
                if (tagName.equals("row")) {
                    stock = new Stock();
                } else if (stock != null) {
                    if (tagName.equals("col0")) {
                        stock.setName(parser.nextText());
                    } else if (tagName.equals("col1")) {
                        stock.setSymbol(parser.nextText());
                    } else if (tagName.equals("col4")) {
                        stock.setChange(parser.nextText());
                    }else if (tagName.equals("col5")) {
                        stock.setPercentChange(parser.nextText());
                    } else if (tagName.equals("col8")) {
                        stock.setPrice(parser.nextText());
                    }
                }
            }
            eventType = parser.next();
        }
        return stock;
    }
}
