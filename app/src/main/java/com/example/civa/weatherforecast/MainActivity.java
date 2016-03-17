package com.example.civa.weatherforecast;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
//import org.apache.http.client.HttpClient;
//import org.apache.http.client.methods.HttpGet;
//import org.apache.http.impl.client.DefaultHttpClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class MainActivity extends Activity {
    ListView list;
    String txtMain;
    Drawable imgMain;
    String[] txt1 = new String[9];
    String[] txt2 = new String[9];
    Drawable[] imageId = new Drawable[9];
    private JSONArray dataArr = new JSONArray();
    private static Customlist adapter;
    private JSONObject parseData = new JSONObject();
    private NetworkInfo mWifi;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        final Location location = getLastKnownLocation();
        getForeCastData(location);

        Geocoder geoCoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> address = geoCoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
        } catch (IOException e) {
            e.printStackTrace();
        }

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getForeCastData(location);
                ConnectivityManager connManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                mWifi = connManager.getActiveNetworkInfo();
                if (mWifi == null) {
                    // Do whatever
                    Toast.makeText(MainActivity.this, "Please enable Wifi/3G", Toast.LENGTH_LONG).show();
                }
                else{
                    Toast.makeText(MainActivity.this, "Data Refreshed", Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    public void getForeCastData(Location location) {
        new getData(location).execute();
    }

    class getData extends AsyncTask<String, String, String> {

        private final Location location;

        public getData(Location location) {
            this.location = location;
        }

        @TargetApi(Build.VERSION_CODES.KITKAT)
        @Override
        protected String doInBackground(String... params) {

            String urlCondString = "http://api.wunderground.com/api/8455ad221a4b78cb/conditions/q/"+location.getLatitude()+","+location.getLongitude()+".json";
            String urlString = "http://api.wunderground.com/api/8455ad221a4b78cb/forecast10day/q/"+location.getLatitude()+","+location.getLongitude()+".json";
            try {

                URL url1 = new URL(urlCondString);
                HttpURLConnection urlConnection1 = (HttpURLConnection) url1.openConnection();
                BufferedInputStream in1 = new BufferedInputStream(urlConnection1.getInputStream());
                BufferedReader streamReader1 = new BufferedReader(new InputStreamReader(in1, "UTF-8"));
                StringBuilder responseStrBuilder1 = new StringBuilder();

                String inputStr1;
                while ((inputStr1 = streamReader1.readLine()) != null)
                    responseStrBuilder1.append(inputStr1);
                JSONObject jsonCurrent = (JSONObject) new JSONObject(responseStrBuilder1.toString()).get("current_observation");
                JSONObject jsonCurrentDisplay = (JSONObject) jsonCurrent.get("display_location");
                txtMain = jsonCurrentDisplay.get("full")+" "+jsonCurrent.get("temp_c")+" C";
                InputStream is1 = (InputStream) new URL((String) jsonCurrent.get("icon_url")).getContent();
                Drawable weatherIcon1 = Drawable.createFromStream(is1, "main_image");
                imgMain = weatherIcon1;


                URL url = new URL(urlString);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                BufferedInputStream in = new BufferedInputStream(urlConnection.getInputStream());
                BufferedReader streamReader = new BufferedReader(new InputStreamReader(in, "UTF-8"));
                StringBuilder responseStrBuilder = new StringBuilder();

                String inputStr;
                while ((inputStr = streamReader.readLine()) != null)
                    responseStrBuilder.append(inputStr);
                JSONObject json = (JSONObject) new JSONObject(responseStrBuilder.toString()).get("forecast");
                JSONObject json2 = (JSONObject) json.get("simpleforecast");
                dataArr = (JSONArray) json2.get("forecastday");
                dataArr.remove(0);
                for(int i = 0; i < dataArr.length(); i++){

                    JSONObject dayData = (JSONObject) dataArr.get(i);
                    JSONObject tempDataDate = (JSONObject) dayData.get("date");
                    JSONObject tempDataHigh = (JSONObject) dayData.get("high");
                    JSONObject tempDataLow = (JSONObject) dayData.get("low");
                    JSONObject tempDataWind = (JSONObject) dayData.get("maxwind");
                    txt1[i] = "\t "+tempDataDate.get("day")+" "+tempDataDate.get("monthname_short")+" "+tempDataDate.get("weekday");
                    txt2[i] = "\t "+tempDataHigh.get("celsius")+" / "+tempDataLow.get("celsius");

                    InputStream is = (InputStream) new URL((String) dayData.get("icon_url")).getContent();
                    Drawable weatherIcon = Drawable.createFromStream(is, i+"_image");
                    imageId[i] = weatherIcon;


                    JSONObject newJson = new JSONObject();
                    newJson.put("icon",weatherIcon);
                    newJson.put("conditions",dayData.get("conditions"));
                    newJson.put("date",tempDataDate);
                    newJson.put("high",tempDataHigh);
                    newJson.put("low",tempDataLow);
                    newJson.put("wind",tempDataWind);
                    newJson.put("avehumidity",dayData.get("avehumidity"));

                    parseData.put(i+"",newJson);

                }

                adapter = new Customlist(MainActivity.this, txt1, imageId,txt2);
                updateView(adapter, txtMain, imgMain, parseData);

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    private void updateView(final Customlist adapter, final String txtMain,final Drawable imgMain,final JSONObject parseData) {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                TextView txtMainView = (TextView) findViewById(R.id.txtMain);
                ImageView imgMainView = (ImageView) findViewById(R.id.imgMain);
                txtMainView.setText(txtMain);
                imgMainView.setImageDrawable(imgMain);

                list = (ListView) findViewById(R.id.list);
                list.setAdapter(adapter);
                list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> parent, View view,
                                            int position, long id) {
                        Intent intent = new Intent(MainActivity.this, DayWeatherInfo.class);
                        intent.putExtra("weatherdata", parseData.toString());
                        intent.putExtra("position", position);
                        startActivity(intent);

                    }
                });
            }
        });


    }

    private Location getLastKnownLocation() {
        LocationManager mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        mLocationManager = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);
        List<String> providers = mLocationManager.getProviders(true);
        Location bestLocation = null;
        for (String provider : providers) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return null;
            }
            Location l = mLocationManager.getLastKnownLocation(provider);
            if (l == null) {
                continue;
            }
            if (bestLocation == null || l.getAccuracy() < bestLocation.getAccuracy()) {
                // Found best last known location: %s", l);
                bestLocation = l;
            }
        }
        return bestLocation;
    }

    private final LocationListener mLocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            Log.d("MyLocationNow", String.valueOf(location));
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }

    };

    @Override
    public void onPause(){
        super.onPause();
        overridePendingTransition(R.anim.transition2, R.anim.transition1);

    }
}