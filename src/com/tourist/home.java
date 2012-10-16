package com.tourist;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

public class home extends Activity {
    
	GridView grid_main;
	
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Remove title bar
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.main);
        
        GridView grid_main = (GridView)findViewById(R.id.gridview01);
        grid_main.setAdapter(new ImageAdapter(this));
        
        grid_main.setOnItemClickListener(new OnItemClickListener() {
        	public void onItemClick(AdapterView<?> parent, View v, int position, long id){
        		//startovanje aroundme activity
        		if(position == 0){
        			Intent i = new Intent(home.this, aroundme.class);
        			try{
        				startActivityForResult(i, 2);
        			}catch(Exception e){
        				e.fillInStackTrace();
        			}
        		}
        	}
		});
        
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        
        if(resultCode == 20) {
        	finishActivity(2);
        }
    }
    
    
    public class ImageAdapter extends BaseAdapter {
        private Context mContext;

        public ImageAdapter(Context c) {
            mContext = c;
        }

        public int getCount() {
            return mThumbIds.length;
        }

        public Object getItem(int position) {
            return null;
        }

        public long getItemId(int position) {
            return 0;
        }

        // create a new ImageView for each item referenced by the Adapter
        public View getView(int position, View convertView, ViewGroup parent) {
            View v;
            if (convertView == null) {  // if it's not recycled, initialize some attributes
            	LayoutInflater li = getLayoutInflater();
            	v = li.inflate(R.layout.home_gridview_cell, null);
            	TextView tv = (TextView)v.findViewById(R.id.icon_text);
            	tv.setText(mTextsIds[position]);
            	ImageView iv = (ImageView)v.findViewById(R.id.icon_image);
            	iv.setImageResource(mThumbIds[position]);
            } else {
                v = (View) convertView;
            }
            return v;
        }

        // references to our images
        private Integer[] mThumbIds = {
                R.drawable.aroundme_285, R.drawable.aroundme_285,
                R.drawable.aroundme_285, R.drawable.aroundme_285
        };
        
        // references to our texts
        private String[] mTextsIds = {
        		"Around Me", "Guide Me", "Info", "Jos nesto"
        };
    }
    
    
}