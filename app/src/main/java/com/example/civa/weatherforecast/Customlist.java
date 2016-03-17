package com.example.civa.weatherforecast;

/**
 * Created by civa on 3/16/16.
 */

import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.View;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class Customlist extends ArrayAdapter<String> {
    private final Activity context;
    private final String[] txt1;
    private final String[] txt2;
    private final Drawable[] imageId;

    public Customlist(Activity context,
                      String[] txt1, Drawable[] imageId, String[] txt2) {
        super(context, R.layout.list_single, txt1);
        this.context = context;
        this.txt1 = txt1;
        this.imageId = imageId;
        this.txt2 = txt2;

    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {

        LayoutInflater inflater = context.getLayoutInflater();
        View mainView = inflater.inflate(R.layout.activity_main, null, true);
        View rowView = inflater.inflate(R.layout.list_single, null, true);
        TextView txtTitle1 = (TextView) rowView.findViewById(R.id.txt1);
        TextView txtTitle2 = (TextView) rowView.findViewById(R.id.txt2);
        ImageView imageView = (ImageView) rowView.findViewById(R.id.img);

        txtTitle1.setText(txt1[position]);
        imageView.setImageDrawable(imageId[position]);
        txtTitle2.setText(txt2[position]);

        return rowView;
    }
}