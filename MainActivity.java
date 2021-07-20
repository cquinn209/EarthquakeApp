package org.me.gcu.equakestartercode;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Dialog;
import android.content.DialogInterface;

import android.text.Editable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.lang.reflect.Array;
import java.net.URL;
import java.net.URLConnection;
import java.nio.Buffer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.me.gcu.equakestartercode.R;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;

//Conor Quinn
//S1705540



public class MainActivity extends AppCompatActivity implements OnClickListener, OnMapReadyCallback {

    private String result = "";
    private String url1 = "";
    private String urlSource = "http://quakes.bgs.ac.uk/feeds/MhSeismology.xml";

    LinkedList <titleitem> alistt = null;
    private GoogleMap mMap;
    ListView listView;
    List<String> values = new ArrayList<String>();
 // SearchView searchView;
    String date;

    double maxmag = 0;
    double maxlat = -90;
    double minlat = 90;
    double maxlong = 180;
    double minlong = -180;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.e("MyTag", "in onCreate");

        TextView textView = findViewById(R.id.textViewDate);
        TextView textViewNorth = findViewById(R.id.textVieNorth);
        TextView textViewEast = findViewById(R.id.textViewEast);
        TextView textViewWest = findViewById(R.id.textViewWest);
        date = getIntent().getStringExtra("date");
        listView =(ListView)findViewById(R.id.listview);
        textView.setText(date);
        textView.append(" \n");
     //   searchView = (SearchView)findViewById(R.id.nav_search);



        /** Calendar Setup */
        Button button = findViewById(R.id.buttoncal);
        button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent (MainActivity.this, CalendarActivity.class);
                startActivity(intent);
            }
        });

        //Async task call
        new LongRunningTask(urlSource).execute();

    }



    /** Start Async task to read and parse data*/

    private class LongRunningTask extends AsyncTask<Void, Void, Void> {

        private String url;
        public LongRunningTask(String aurl) { url = aurl; }


        //Background task
        @Override
        protected Void doInBackground(Void... voids) {

                URL aurl;
                URLConnection yc;
                BufferedReader in = null;
                String inputLine = "" ;

                Log.e("Async Start", "start of async");

                try {
                    Log.e("Get Data", "trying to get data...");
                    aurl = new URL(url);
                    yc = aurl.openConnection();
                    in = new BufferedReader(new InputStreamReader(yc.getInputStream()));

                    while ((inputLine = in.readLine()) != null) {
                        result = result + inputLine;
                    }
                    in.close();

                } catch (IOException ae) {
                    Log.e("Error", "ioexception in run");
                }

                Log.e("Got Data", result);


            Log.e("Starting Parse", "Parsing in progress...");
                try {
                    alistt = parseData(result);
                    if (alistt != null)
                    {
                        Log.e("Reading Data","Reading data to parse...");
                        for (Object o : alistt)
                        {
                            Log.e("Parse Successful!" ,o.toString());
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            return null;
        }


        /** Upon completing parse*/

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            //Get textviews to display calendar data
            TextView textView = findViewById(R.id.textViewDate);
            TextView textViewNorth = findViewById(R.id.textVieNorth);
            TextView textViewSouth = findViewById(R.id.textViewSouth);
            TextView textViewEast = findViewById(R.id.textViewEast);
            TextView textViewWest = findViewById(R.id.textViewWest);



            MainActivity.this.runOnUiThread(new Runnable() {
                public void run() {

                    Log.d("UI thread", "I am the UI thread");

                    String datatoshow = "";

                    for (int i = 0; i < alistt.size(); i++) {

                        //Get title of each earthquake
                        datatoshow = alistt.get(i).gettitle();

                        //Replace sections for ease of viewing
                        datatoshow = datatoshow.replaceAll("UK Earthquake alert :", "");
                        datatoshow = datatoshow.replaceAll(":", ",");

                        //Create new array to store split sections
                        List<String> stateList = Arrays.asList(datatoshow.split("\\,"));

                        //get magnitude and location
                        String mag = stateList.get(0);
                        String placeName = stateList.get(1);

                       //  SpannableString ss = new SpannableString(mag);
                       // ForegroundColorSpan fcsRed = new ForegroundColorSpan(Color.RED);
                       // ss.setSpan(fcsRed, 0, 8, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);


                        //Combine splits into one string and display
                        datatoshow = placeName + "," + mag;

                        values.add(datatoshow);

                    }


                    /** Displaying calendar results*/

                    for (int i = 0; i < alistt.size(); i++) {

                            int monthNumber = 0;

                            //get earthquake date
                            datatoshow = alistt.get(i).getpubDate();

                            //Split date into day, month and year
                            List<String> splited = Arrays.asList(datatoshow.split("\\s+"));

                            String day = splited.get(1);
                            String month = splited.get(2);
                            String year = splited.get(3);

                            //Getting numeric value of the month
                            try {
                                Date date = new SimpleDateFormat("MMM").parse(month);
                                Calendar cal = Calendar.getInstance();
                                cal.setTime(date);
                                monthNumber = cal.get(Calendar.MONTH);
                            }

                            catch (Exception e) {
                                e.printStackTrace();
                            }

                            //Convert day to int and back to get rid of leading 0 and match xml date format
                            day = Integer.valueOf(day).toString();


                            //Matching the format of parsed data
                            datatoshow = year + "/" + monthNumber + "/" + day;



                        /** Gather calendar search information*/

                            String datatoshow2 = "";


                            //Get earthquake titles
                            datatoshow2 = alistt.get(i).gettitle();

                            //Data becoming more readable
                            datatoshow2 = datatoshow2.replaceAll("UK Earthquake alert :", "");
                            datatoshow2 = datatoshow2.replaceAll(":", ",");

                            //Split data
                            List<String> stateList = Arrays.asList(datatoshow2.split("\\,"));

                            String mag = stateList.get(0);
                            String placeName = stateList.get(1);

                            //Concat both strings
                            String display = placeName + "," + mag;


                            //If date selected matches earthquake data with the same date
                            try {
                                if (date.equals(datatoshow)) {

                                    String testo =   mag.substring(4,7);
                                    double magdoub = Double.parseDouble(testo);


                                    // Display max mag for selected date
                                    if (magdoub > maxmag ){

                                       maxmag = magdoub;

                                        textView.append(" \n");
                                        textView.setText("Largest Magnitude was:");
                                        textView.append(" \n");
                                        textView.append(display);
                                        textView.append(" \n");
                                    }

                                    // Most northerly quake
                                    String getlat = alistt.get(i).getlat();
                                    double latdoub = Double.parseDouble(getlat);

                                   if(latdoub > maxlat)
                                    {
                                        maxlat = latdoub;

                                       textViewNorth.append(" \n");
                                       textViewNorth.setText("Most northerly earthquake was:");
                                       textViewNorth.append(" \n");
                                       textViewNorth.append(display);
                                       textViewNorth.append(" \n");
                                    }

                                    // Most southerly quake
                                    double latdoub2 = Double.parseDouble(getlat);

                                    if(latdoub2 < minlat)
                                    {
                                        minlat = latdoub2;

                                        textViewSouth.append(" \n");
                                        textViewSouth.setText("Most southerly earthquake was:");
                                        textViewSouth.append(" \n");
                                        textViewSouth.append(display);
                                        textViewSouth.append(" \n");
                                    }

                                    // Most easterly quake
                                    String getlong = alistt.get(i).getllong();
                                    double longdoub = Double.parseDouble(getlong);

                                    if(longdoub > minlong)
                                    {
                                        minlong = longdoub;

                                        textViewEast.append(" \n");
                                        textViewEast.setText("Most easterly earthquake was:");
                                        textViewEast.append(" \n");
                                        textViewEast.append(display);
                                        textViewEast.append(" \n");
                                    }

                                    // Most westerly quake
                                    double longdoub2 = Double.parseDouble(getlong);

                                    if(longdoub2 < maxlong)
                                    {
                                        maxlong = longdoub2;

                                        textViewWest.append(" \n");
                                        textViewWest.setText("Most westerly earthquake was:");
                                        textViewWest.append(" \n");
                                        textViewWest.append(display);
                                        textViewWest.append(" \n");
                                    }
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }





                    /** ListView OnClick */

                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                            //Alert Dialog
                            AlertDialog.Builder myAlertBuilder = new AlertDialog.Builder(MainActivity.this);
                            myAlertBuilder.setTitle("Earthquake Information");

                            String datatoshow = "";

                            //Show more earthquake information using description data of earthquakes
                            datatoshow = alistt.get(position).getdescription();
                            datatoshow = datatoshow.replaceAll(";", "\n");

                            //show the message
                            myAlertBuilder.setMessage(datatoshow);
                            myAlertBuilder.setPositiveButton("Close", new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            });
                            myAlertBuilder.show();
                        }
                    });

                }
            });

            /** Run Map setup now earthquake information has been established */
            getpins();
        }
    }

    public void onClick(View aview) {
    }


    /** Handle map and listview */
    public void getpins() {



        //map
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        //list view
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
               android.R.layout.simple_list_item_1, android.R.id.text1, values );
        listView.setAdapter(adapter);



        //Filter the listview via edittext
        EditText theFilter = (EditText) findViewById(R.id.searchFilter);

        theFilter.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                adapter.getFilter().filter(s);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }


    /** Pull Parser */
    public static LinkedList<titleitem> parseData(String DataToParse) {

        titleitem titletest = null;
        LinkedList<titleitem> alistt = null;


        try {

            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);

            XmlPullParser xpp = factory.newPullParser();

            xpp.setInput(new StringReader(DataToParse));
            int eventType = xpp.getEventType();


            while (eventType != XmlPullParser.END_DOCUMENT) {

                if (eventType == XmlPullParser.START_TAG) {

                  if (xpp.getName().equalsIgnoreCase("channel"))
                    {
                        alistt  = new LinkedList<titleitem>();
                    }

                    else
                    if (xpp.getName().equalsIgnoreCase("item")) {
                        Log.e("MyTag", "Item Start Tag found");
                    }

                    else if
                    (xpp.getName().equalsIgnoreCase("title")) {
                        titletest = new titleitem();
                        String temp = xpp.nextText();
                        titletest.settitle(temp);
                        Log.e("Titleinfo", "Title is:  " + titletest.gettitle());
                    }

                    else if (xpp.getName().equalsIgnoreCase("description")) {
                        String temp = xpp.nextText();
                        Log.e("DescriptionInfo", "Description is" + temp);
                        titletest.setdescription(temp);
                    }

                    else if (xpp.getName().equalsIgnoreCase("link")) {
                        String temp = xpp.nextText();
                        Log.e("LinkInfo", "Link is" + temp);
                        titletest.setlink(temp);
                    }

                    else if (xpp.getName().equalsIgnoreCase("pubDate")) {
                        String temp = xpp.nextText();
                        Log.e("DateInfo", "Date is" + temp);
                        titletest.setpubDate(temp);
                    }

                    else if (xpp.getName().equalsIgnoreCase("category")) {
                        String temp = xpp.nextText();
                        Log.e("CategoryInfo", "Category is" + temp);
                        titletest.setcategoryy(temp);
                    }

                  else if ( xpp.getName().equalsIgnoreCase( "lat")) {
                      String temp = xpp.nextText();
                      Log.e("GeolatInfo", "Geo Lat is" + temp);
                      titletest.setlat(temp);
                  }

                    else if (xpp.getName().equals("long")) {
                        String temp = xpp.nextText();
                        Log.e("GeoLongInfo", "Geo long is" + temp);
                        titletest.setllong(temp);
                   }
                }

                else if (eventType == XmlPullParser.END_TAG) {
                        if (xpp.getName().equalsIgnoreCase("item")) {
                            Log.e("Enditem", "This is the end of an item");
                            alistt.add(titletest);
                        }
                    }
                    eventType = xpp.next();
                }
            }
        catch (XmlPullParserException ae1)
        {
            Log.e("ParseError","Parsing error" + ae1.toString());
        }
        catch (IOException ae1)
        {
            Log.e("IOException","IO error during parsing");
        }
            return alistt;
    }


    /** Setting up map object */
    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap =  googleMap;

        String latt = "";
        String longg = "";
        String title = "";
        String testo = "";

        double latdoub;
        double longdoub;
        double testodoub;


        //Set default view to Glasgow
        LatLng glasgow = new LatLng(55.8642, 4.2518);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(glasgow));



        //Map pins setup
        for (int i = 0; i < alistt.size(); i++) {

            String datatoshow = "";

            datatoshow = alistt.get(i).gettitle();



            //set title
            datatoshow = datatoshow.replaceAll("UK Earthquake alert :", "");
            datatoshow = datatoshow.replaceAll(":", ",");

            List<String> stateList = Arrays.asList(datatoshow.split("\\,"));

            String mag = stateList.get(0);
            String placeName = stateList.get(1);

            String titlepin = placeName + "," + mag;


            //get magnitude for applying colour
            testo = alistt.get(i).gettitle();

            //Get the magnitude of each quake
            testo =   testo.substring(25,28);

            //Convert to double
            testodoub = Double.valueOf(testo);


            //Get lat and long for each quake
            latt = alistt.get(i).getlat();
            longg = alistt.get(i).getllong();

            //Convert to double
            latdoub = Double.valueOf(latt);
            longdoub = Double.valueOf(longg);

            //Location variable
            LatLng loc = new LatLng(latdoub, longdoub);


            //Set colour depending on severity
            if(testodoub <= 0.5){

                mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
                        .position(loc)
                        .title(titlepin));

            }
            else if (testodoub <= 1)
            {
                mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW))
                        .position(loc)
                        .title(titlepin));
            }
            else if (testodoub <= 1.5)
            {
                mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE))
                        .position(loc)
                        .title(titlepin));
            }
            else if (testodoub <= 2)
            {
                mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
                        .position(loc)
                        .title(titlepin));
            }

        }

    }

}



