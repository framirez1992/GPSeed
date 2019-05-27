package com.far.gpseed;

import android.content.Context;
import android.net.Uri;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;

public class CustomPageAdapter extends PagerAdapter {

    Context context;
    ArrayList<File> objects;
    public CustomPageAdapter(Context c, ArrayList<File> objs){
        this.context = c;
        this.objects = objs;
    }
    @Override
    public int getCount() {
        return objects.size();
    }



    @Override
    public Object instantiateItem(ViewGroup collection, int position) {
        LayoutInflater inflater = LayoutInflater.from(context);
        ViewGroup layout = (ViewGroup) inflater.inflate(R.layout.image_view, collection, false);
        ImageView img = (ImageView) layout.findViewById(R.id.img);
        try {
            Picasso.with(context).load(Uri.fromFile(new File(objects.get(position).getAbsolutePath())))
                    .memoryPolicy(MemoryPolicy.NO_CACHE )
                    .networkPolicy(NetworkPolicy.NO_CACHE)
                    //.transform(new CircleTransformation())
                    .into(img);
        }catch (Exception e){
            e.printStackTrace();
        }


        collection.addView(layout);
        return layout;
    }

    @Override
    public void destroyItem(ViewGroup collection, int position, Object view) {
        collection.removeView((View) view);
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        //ModelObject customPagerEnum = ModelObject.values()[position];
        return"";// mContext.getString(customPagerEnum.getTitleResId());
    }
}
