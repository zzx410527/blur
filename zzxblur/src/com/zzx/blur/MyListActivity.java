package com.zzx.blur;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import android.app.ListActivity;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.zzx.blur.util.Blur;

public class MyListActivity extends ListActivity {
	private ListView listView;
	Handler handler = new Handler();
	private BaseAdapter adapter = new BaseAdapter() {

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			ViewHolder holder;
			if (convertView == null) {
				convertView = inflater.inflate(R.layout.list_item, null);
				holder = new ViewHolder();
				holder.iv = (ImageView) convertView.findViewById(R.id.iv_image);
				holder.tv = (TextView) convertView.findViewById(R.id.tv_text);
				//需要先测量一下，要不取不到drawingCache，不知道为什么？
				holder.tv.measure(MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED),

						MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));

						holder.tv.layout(0, 0, holder.tv.getMeasuredWidth(), holder.tv.getMeasuredHeight());
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			Data data = map.get(position);
			holder.tv.setDrawingCacheEnabled(true);
			Bitmap drawingCache =holder.tv.getDrawingCache();
			//不能设置false,？？？？
//			holder.tv.setDrawingCacheEnabled(false);
			if (drawingCache == null){
				Toast.makeText(getApplication(), "drawingCache=null", 1000).show();
				Log.e("zzx", "drawingCache=null");
				drawingCache = Bitmap.createBitmap(holder.tv.getWidth(), holder.tv.getHeight(), Config.ARGB_8888);
			}
			//用子线程去跑
			appBitmap(drawingCache,holder.tv);
			holder.iv.setImageResource(data.id);
			return convertView;
		}
		 private void appBitmap(final Bitmap drawingCache,final TextView v) {
			 new Thread(new Runnable() {
				
				@Override
				public void run() {
					final long startTime = SystemClock.currentThreadTimeMillis();
					 final Bitmap apply = Blur.apply(MyListActivity.this, drawingCache);
					 final long endTime = SystemClock.currentThreadTimeMillis();
					 handler.postDelayed(new Runnable() {
						
						@Override
						public void run() {
							v.setText(endTime - startTime + "ms");
							 BitmapDrawable bd = new BitmapDrawable(getResources(), apply);
								bd.setAlpha(80);
								bd.setColorFilter(Color.parseColor("#dd5f533f"), PorterDuff.Mode.DST_ATOP);
								v.setBackgroundDrawable(bd);
						}
					}, 100);
				}
			}).start();
			
		}
		
		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return map.get(position).id;
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return map.get(position);
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return map.size();
		}

		class ViewHolder {
			TextView tv;
			ImageView iv;
		}
	};
	private LayoutInflater inflater;
	Map<Integer, Data> map;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		listView = getListView();
		inflater = getLayoutInflater();
		initdata();
		listView.setAdapter(adapter);

	}

	int[] resid = { R.drawable.login_guide_img1, R.drawable.login_guide_img2, R.drawable.login_guide_img3, R.drawable.login_guide_img4 };

	private void initdata() {
		// TODO Auto-generated method stub
		map = new HashMap<Integer, Data>();
		Random random = new Random();
		Data data;
		for (int i = 0; i < 10; i++) {
			int nextInt = random.nextInt(4);
			data = new Data(resid[nextInt], "text" + i);
			map.put(i, data);
		}
	}

	class Data {
		int id;
		String text;

		public Data(int id, String text) {
			super();
			this.id = id;
			this.text = text;
		}

	}
	public  Bitmap convertViewToBitmap(View view) {

        view.measure(MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED),

                MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));

        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());

        view.buildDrawingCache();

        Bitmap bitmap = view.getDrawingCache();

        if (bitmap != null) {

            System.out.println("这不是nullde1");

            Log.d("nullde1", "nullde1");

        } else {

            System.out.println("这nullnulllnulnlul");

        }

        return bitmap;

    }
   public Bitmap getViewBitmap(View v) {

        v.clearFocus();

        v.setPressed(false);

 

        boolean willNotCache = v.willNotCacheDrawing();

        v.setWillNotCacheDrawing(false);

 

        // Reset the drawing cache background color to fully transparent

        // for the duration of this operation

        int color = v.getDrawingCacheBackgroundColor();

        v.setDrawingCacheBackgroundColor(0);

 

        if (color != 0) {

            v.destroyDrawingCache();

        }

        v.buildDrawingCache();

        Bitmap cacheBitmap = v.getDrawingCache();

        if (cacheBitmap == null) {

            Log.e("TTTTTTTTActivity", "failed getViewBitmap(" + v + ")",

                    new RuntimeException());

            return null;

        }

 

        Bitmap bitmap = Bitmap.createBitmap(cacheBitmap);

 

        // Restore the view

        v.destroyDrawingCache();

        v.setWillNotCacheDrawing(willNotCache);

        v.setDrawingCacheBackgroundColor(color);

 

        return bitmap;

    }
}
