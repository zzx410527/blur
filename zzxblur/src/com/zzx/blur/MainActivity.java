package com.zzx.blur;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.zzx.blur.util.Blur;


public class MainActivity extends Activity {

	private Button dummyButton;
	private Button btn_blur_list;
	private ImageView iv_image;
	private TextView tv_text;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		dummyButton = (Button) findViewById(R.id.dummy_button);
		iv_image = (ImageView) findViewById(R.id.iv_image);
		tv_text = (TextView) findViewById(R.id.tv_text);
		btn_blur_list = (Button) findViewById(R.id.btn_blur_list);
		
		btn_blur_list.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent i = new Intent(MainActivity.this, MyListActivity.class);
				startActivity(i);
			}
		});
		
		
		dummyButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
//
//				Runnable runnable = new Runnable() {
//					@Override
//					public void run() {
//						Intent intent = new Intent(MainActivity.this, BlurredActivity.class);
//						intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
//
//						startActivity(intent);
//					}
//				};
//				BlurBehind.getInstance().execute(MainActivity.this, runnable,null);
				tv_text.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_LOW);
				tv_text.setDrawingCacheEnabled(true);
				tv_text.buildDrawingCache();
				Bitmap drawingCache = tv_text.getDrawingCache();
				long startTime = SystemClock.currentThreadTimeMillis();
				Bitmap apply = Blur.apply(MainActivity.this,drawingCache);
				
				 BitmapDrawable bd = new BitmapDrawable(getResources(),apply);
		            bd.setAlpha(90);
		            bd.setColorFilter(Color.parseColor("#ddeeddee"), PorterDuff.Mode.DST_ATOP);
		            tv_text.setBackgroundDrawable(bd);
		            long endTime = SystemClock.currentThreadTimeMillis();
		            tv_text.setText(endTime-startTime+"ms");
			}
		});
	}

}
