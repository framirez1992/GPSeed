package com.far.gpseed.Utils;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.far.gpseed.Interfaces.ListableActivity;
import com.far.gpseed.Models.SeedLocation;
import com.far.gpseed.R;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by Framirez on 25/05/2019.
 */

public class SeedsRecycler extends RecyclerView.Adapter<SeedsRecycler.SeedViewHolder> {

    private Context context;
    ArrayList<SeedLocation> objects;
    ListableActivity listableActivity;

    public SeedsRecycler(Context context, ListableActivity la, ArrayList<SeedLocation> list) {
        this.context = context;
        this.objects = list;
        this.listableActivity = la;
    }

    @Override
    public SeedViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

      View v =  ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.seed_layout,parent, false);
        return new SeedViewHolder(v);
    }

    @Override
    public void onBindViewHolder(SeedViewHolder holder, final int position) {
     holder.fillData(objects.get(position));
        holder.getLlSeed().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listableActivity.onClick(objects.get(position));
            }
        });
    }

    @Override
    public int getItemCount() {
        return objects.size();
    }

    public class SeedViewHolder extends RecyclerView.ViewHolder {
        TextView name;
        ImageView img;
        LinearLayout llSeed;

        public SeedViewHolder(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.tvDescripcion);
            img = (ImageView) itemView.findViewById(R.id.img);
            llSeed = itemView.findViewById(R.id.llSeed);
        }

        public void fillData(SeedLocation object){
            name.setText(object.Description);
            if(!object.imageUrl.equals("")) {
                Picasso.with(context).load(Uri.fromFile(new File(object.imageUrl))).transform(new CircleTransformation()).into(img);
            }else{
               img.setImageResource(R.mipmap.seed);
            }
        }
        public LinearLayout getLlSeed(){
            return llSeed;
        }
    }
}
