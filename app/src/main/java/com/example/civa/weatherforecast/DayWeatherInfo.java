package com.example.civa.weatherforecast;

import android.graphics.drawable.Drawable;
import android.media.Image;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

public class DayWeatherInfo extends AppCompatActivity {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private static ViewPager mViewPager;
    private static JSONObject weatherdataArr;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        Toast.makeText(getApplicationContext(),"Swipe Left/Right to change day",Toast.LENGTH_LONG).show();
        Bundle bundle = getIntent().getExtras();
        String weatherdataStr = (String) bundle.get("weatherdata");
        Integer pos = (Integer) bundle.get("position");
        try {
            weatherdataArr = new JSONObject(weatherdataStr);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        setContentView(R.layout.activity_day_weather_info);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.setCurrentItem(pos);


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.

        getMenuInflater().inflate(R.menu.menu_day_weather_info, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        public PlaceholderFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            Log.d("weatherdata", weatherdataArr.toString());
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
//            mViewPager.setCurrentItem(sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {

            View rootView = inflater.inflate(R.layout.fragment_day_weather_info, container, false);
            try {

                JSONObject dataObj = (JSONObject) weatherdataArr.get(String.valueOf(getArguments().getInt(ARG_SECTION_NUMBER)-1));
                JSONObject dataDateObj = (JSONObject) dataObj.get("date");
                JSONObject highDateObj = (JSONObject) dataObj.get("high");
                JSONObject lowDateObj = (JSONObject) dataObj.get("low");
                JSONObject windDateObj = (JSONObject) dataObj.get("wind");

                TextView title = (TextView) rootView.findViewById(R.id.title);
                title.setText(dataDateObj.get("day")+" "+dataDateObj.get("monthname")+" "+dataDateObj.get("weekday"));

                TextView conditions = (TextView) rootView.findViewById(R.id.conditions);
                conditions.setText("Condition: "+dataObj.get("conditions"));

                TextView high = (TextView) rootView.findViewById(R.id.high);
                high.setText("High: " + highDateObj.get("celsius")+"℃ / "+highDateObj.get("fahrenheit")+"℉");

                TextView low = (TextView) rootView.findViewById(R.id.low);
                low.setText("Low: " + lowDateObj.get("celsius")+"°C / "+lowDateObj.get("fahrenheit")+"℉");

                TextView wind = (TextView) rootView.findViewById(R.id.wind);
                wind.setText("Wind: " + windDateObj.get("kph")+"kph / "+ windDateObj.get("mph")+"mph");

                TextView humidity = (TextView) rootView.findViewById(R.id.avehumidity);
                humidity.setText("Humidity: " + dataObj.get("avehumidity"));


            } catch (JSONException e) {
                e.printStackTrace();
            }

            return rootView;
        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return PlaceholderFragment.newInstance(position + 1);
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 9;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "SECTION 1";
                case 1:
                    return "SECTION 2";
                case 2:
                    return "SECTION 3";
            }
            return null;
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        overridePendingTransition(R.anim.transition2, R.anim.transition1);
    }
}
