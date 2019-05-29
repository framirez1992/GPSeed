package com.far.gpseed;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;

public class PhotoAdapter extends RecyclerView.Adapter<PhotoAdapter.PhotoViewHolder> {


    ArrayList<File> objects;
    CameraA2 act;
    public PhotoAdapter(CameraA2 c, ArrayList<File> objs){
        this.act = c;
        this.objects = objs;
    }
    @Override
    public PhotoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = (LayoutInflater) act
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.image_preview, parent, false);
        return new PhotoViewHolder(view);
    }


    @Override
    public void onBindViewHolder(PhotoViewHolder holder, int position) {

        holder.fillData(act, objects.get(position));
        holder.getImg().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                act.goToDetail();
            }
        });
    }

    @Override
    public int getItemCount() {
        return objects.size();
    }

    public class PhotoViewHolder extends RecyclerView.ViewHolder {

        ImageView img;
        public PhotoViewHolder(View itemView) {
            super(itemView);
            img = (ImageView) itemView.findViewById(R.id.img);
            img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
        }

        public void fillData(Context context, File f){
            try {
                Picasso.with(context).load(Uri.fromFile(new File(f.getAbsolutePath())))
                        .memoryPolicy(MemoryPolicy.NO_CACHE )
                        .networkPolicy(NetworkPolicy.NO_CACHE)
                        //.transform(new CircleTransformation())
                        .into(img);
            }catch (Exception e){
                e.printStackTrace();
            }

        }

        public ImageView getImg(){
            return img;
        }
    }
}


