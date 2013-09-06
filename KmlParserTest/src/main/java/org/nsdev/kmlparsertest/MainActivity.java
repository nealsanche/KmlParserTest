package org.nsdev.kmlparsertest;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import java.io.InputStream;
import java.util.zip.ZipInputStream;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        AsyncTask<Void,Void,Void> task = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {

                InputStream inputStream = getResources().openRawResource(R.raw.esrd_wma);

                ZipInputStream zin = new ZipInputStream(inputStream);


                try
                {
                    zin.getNextEntry();

                    SAXParserFactory spf = SAXParserFactory.newInstance();
                    SAXParser sp = spf.newSAXParser();

                    /* Get the XMLReader of the SAXParser we created. */
                    XMLReader xr = sp.getXMLReader();

                    /* Create a new ContentHandler and apply it to the XML-Reader*/
                    ExampleHandler myExampleHandler = new ExampleHandler();
                    xr.setContentHandler(myExampleHandler);

                    Log.e("NAS", "Parsing started.");
                    /* Parse the xml-data from our URL. */
                    xr.parse(new InputSource(zin));

                    Log.e("NAS", "Parsing completed.");
                    /* Parsing has finished. */

                    zin.closeEntry();
                    zin.close();
                    inputStream.close();

                } catch (Exception ex) {
                    ex.printStackTrace();
                }

                return null;
            }
        };
        task.execute();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    private class ExampleHandler implements ContentHandler {

        StringBuilder mCharBuffer = new StringBuilder();

        boolean foundPlaceMark;
        boolean foundOutside;
        boolean foundInside;

        @Override
        public void setDocumentLocator(Locator locator) {

        }

        @Override
        public void startDocument() throws SAXException {
            Log.e("NAS", String.format("Start Document"));

        }

        @Override
        public void endDocument() throws SAXException {
            Log.e("NAS", String.format("End Document"));

        }

        @Override
        public void startPrefixMapping(String s, String s2) throws SAXException {

        }

        @Override
        public void endPrefixMapping(String s) throws SAXException {

        }

        @Override
        public void startElement(String s, String s2, String s3, Attributes attributes) throws SAXException {
            Log.e("NAS", String.format("Start Element: s=%s s2=%s s3=%s",s,s2,s3));
            mCharBuffer.setLength(0);

            for(int i = 0; i < attributes.getLength(); i++)
            {
                Log.e("NAS", String.format("Attribute: %s = %s", attributes.getQName(i), attributes.getValue(i)));
            }

            if (s2.equals("Placemark")) foundPlaceMark = true;
            else if (s2.equals("outerBoundaryIs")) foundOutside = true;
            else if (s2.equals("innerBoundaryIs")) foundInside = true;
        }

        @Override
        public void endElement(String s, String s2, String s3) throws SAXException {
            if (mCharBuffer.length() > 0)
            {
                String valueType = "Value: ";
                if (foundPlaceMark && s2.equals("name"))
                {
                    valueType = "Placemark Name: ";
                }
                else if (foundPlaceMark && s2.equals("styleUrl"))
                {
                    valueType = "Style URL: ";
                }
                else if (foundPlaceMark && foundOutside && s2.equals("coordinates"))
                {
                    valueType = "Outside Coordinates: ";
                }
                else if (foundPlaceMark && foundInside && s2.equals("coordinates"))
                {
                    valueType = "Inside Coordinates: ";
                }

                if (valueType != null)
                    Log.e("NAS", valueType + mCharBuffer.toString());
                mCharBuffer.setLength(0);
            }

            Log.e("NAS", String.format("End Element: s=%s s2=%s s3=%s",s,s2,s3));

            if (s2.equals("Placemark")) foundPlaceMark = false;
            else if (s2.equals("outerBoundaryIs")) foundOutside = false;
            else if (s2.equals("innerBoundaryIs")) foundInside = false;
        }

        @Override
        public void characters(char[] chars, int index, int len) throws SAXException {
            // Log.e("NAS", String.format("Characters: chars=%s i=%d i2=%d",new String(chars),i,i2));

            mCharBuffer.append(chars, index, len);
        }

        @Override
        public void ignorableWhitespace(char[] chars, int i, int i2) throws SAXException {

        }

        @Override
        public void processingInstruction(String s, String s2) throws SAXException {

        }

        @Override
        public void skippedEntity(String s) throws SAXException {

        }
    }
}
