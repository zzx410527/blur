package com.zzx.blur;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;


public class BlurredActivity extends Activity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_blurred);
		View textView = findViewById(R.id.textView);
		BlurBehind.getInstance()
                .withAlpha(90)
                .withFilterColor(Color.parseColor("#ee999999"))
                .setBackground(this,textView);
	}
}
