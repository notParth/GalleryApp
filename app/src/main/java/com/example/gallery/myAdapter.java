package com.example.gallery;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;



import java.util.ArrayList;

public class myAdapter extends ArrayAdapter<String> {

    ArrayList<Photo> photos;
    Context mContext;

    public myAdapter(@NonNull Context context, ArrayList<Photo> Albumphotos) {
        super(context, R.layout.photo);
        this.photos = Albumphotos;
        this.mContext = context;
    }

    @Override
    public int getCount(){
        if (photos == null)
            return 0;
        return photos.size();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ViewHolder mViewHolder = new ViewHolder();
        if(convertView == null) {
            LayoutInflater mInflater = (LayoutInflater) mContext.getSystemService((Context.LAYOUT_INFLATER_SERVICE));
            convertView = mInflater.inflate(R.layout.photo, parent, false);
            mViewHolder.mPhoto = (ImageView) convertView.findViewById(R.id.imageView);
            mViewHolder.mName = (TextView) convertView.findViewById(R.id.textView);
            convertView.setTag(mViewHolder);
        }
        else {
            mViewHolder = (ViewHolder)convertView.getTag();
        }

        //File imgFile = new File(photos.get(position).getPath());
        //Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());

        mViewHolder.mPhoto.setImageURI(Uri.parse(photos.get(position).getPath()));
        mViewHolder.mName.setText(photos.get(position).getFileName());

        return convertView;
    }

    static class ViewHolder{
        ImageView mPhoto;
        TextView mName;
    }
}
