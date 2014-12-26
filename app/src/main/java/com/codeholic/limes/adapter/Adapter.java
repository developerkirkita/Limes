package com.codeholic.limes.adapter;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.codeholic.limes.R;
import com.codeholic.limes.util.ImageLoader;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by DeveloperKirkita on 26.12.2014.
 */
public class Adapter extends BaseAdapter {

    static final String KEY_TITLE = "title";
    static final String KEY_AUTHOR = "author";
    static final String KEY_DATE = "date";
    static final String KEY_THUMB_URL = "thumbnail";

    private Activity activity;
    private ArrayList<HashMap<String, String>> data;
    private static LayoutInflater inflater=null;
    public ImageLoader imageLoader;

    public Adapter(Activity a, ArrayList<HashMap<String, String>> d) {
        activity = a;
        data=d;
        inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        imageLoader=new ImageLoader(activity.getApplicationContext());
    }

    public int getCount() {
        return data.size();
    }

    public Object getItem(int position) {
        return position;
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View vi=convertView;
        if(convertView==null)
            vi = inflater.inflate(R.layout.list_row, null);

        TextView title = (TextView)vi.findViewById(R.id.title); // title
        TextView author = (TextView)vi.findViewById(R.id.author); // artist name
        TextView date = (TextView)vi.findViewById(R.id.date); // duration
        ImageView thumb_image=(ImageView)vi.findViewById(R.id.list_image); // thumb image

        HashMap<String, String> post = new HashMap<String, String>();
        post = data.get(position);

        Log.e("TITLE: " , post.get(KEY_TITLE));

        // Setting all values in listview
        title.setText(post.get(KEY_TITLE));
        author.setText(post.get(KEY_AUTHOR));
        date.setText(post.get(KEY_DATE));
        //imageLoader.DisplayImage(song.get(KEY_THUMB_URL), thumb_image);
        return vi;
    }
}
