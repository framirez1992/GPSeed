package com.far.gpseed.Utils;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.far.gpseed.Models.SeedLocation;
import com.far.gpseed.R;
import com.google.android.gms.maps.model.Circle;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by Framirez on 30/12/2017.
 */

    public class SeedAdapter extends BaseAdapter {
        private Context context;
        ArrayList<SeedLocation> objects;

        public SeedAdapter(Context context, ArrayList<SeedLocation> list) {
            this.context = context;
            this.objects = list;
        }

        @Override
        public int getCount() {
            return objects.size();
        }

        @Override
        public SeedLocation getItem(int position) {
            return objects.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View view, ViewGroup viewGroup) {

            if (view == null) {
                LayoutInflater inflater = (LayoutInflater) context
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = inflater.inflate(R.layout.seed_layout, null, false);
            }

            TextView nombre = (TextView) view.findViewById(R.id.tvDescripcion);
            nombre.setText(objects.get(position).Description);
            if(!objects.get(position).imageUrl.equals("")) {
                ImageView img = (ImageView) view.findViewById(R.id.img);
                Picasso.with(context).load(Uri.fromFile(new File(objects.get(position).imageUrl))).transform(new CircleTransformation()).into(img);
            }


            return view;
        }


}
