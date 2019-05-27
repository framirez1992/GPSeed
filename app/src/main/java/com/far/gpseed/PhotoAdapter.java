package com.far.gpseed;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;

public class PhotoAdapter extends RecyclerView.Adapter<PhotoAdapter.PhotoViewHolder> {


    ArrayList<File> objects;
    Context context;
    public PhotoAdapter(Context c, ArrayList<File> objs){
        this.context = c;
        this.objects = objs;

    }
    @Override
    public PhotoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.image_preview, parent, false);
        return new PhotoViewHolder(view);
    }


    @Override
    public void onBindViewHolder(PhotoViewHolder holder, int position) {

        holder.fillData(context, objects.get(position));
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
                    v.getId();
                }
            });
        }

        public void fillData(Context context, File f){
            try {
                Picasso.with(context).load(Uri.fromFile(new File(f.getAbsolutePath())))
                        //.transform(new CircleTransformation())
                        .into(img);
            }catch (Exception e){
                e.printStackTrace();
            }

        }
    }
}


