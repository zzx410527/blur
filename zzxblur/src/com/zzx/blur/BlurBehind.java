package com.zzx.blur;

import com.zzx.blur.util.Blur;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.support.v4.util.LruCache;
import android.view.View;

public class BlurBehind {

    private static final String KEY_CACHE_BLURRED_BACKGROUND_IMAGE = "KEY_CACHE_BLURRED_BACKGROUND_IMAGE";
    private static final int CONSTANT_BLUR_RADIUS = 12;
    private static final int CONSTANT_DEFAULT_ALPHA = 100;

    private static final LruCache<String, Bitmap> mImageCache = new LruCache<String, Bitmap>(1);
    private static CacheBlurBehindAndExecuteTask cacheBlurBehindAndExecuteTask;

    private int mAlpha = CONSTANT_DEFAULT_ALPHA;
    private int mFilterColor = -1;

    private enum State {
        READY,
        EXECUTING
    }

    private State mState = State.READY;

    private static BlurBehind mInstance;

    public static BlurBehind getInstance() {
        if (mInstance == null) {
            mInstance = new BlurBehind();
        }
        return mInstance;
    }

    public void execute(Activity activity, Runnable runnable,View v) {
        if (mState.equals(State.READY)) {
            mState = State.EXECUTING;
            cacheBlurBehindAndExecuteTask = new CacheBlurBehindAndExecuteTask(activity, runnable,v);
            cacheBlurBehindAndExecuteTask.execute();
        }
    }

    public BlurBehind withAlpha(int alpha) {
        this.mAlpha = alpha;
        return this;
    }

    public BlurBehind withFilterColor(int filterColor) {
        this.mFilterColor = filterColor;
        return this;
    }

    public void setBackground(Activity activity) {
        if (mImageCache.size() != 0) {
            BitmapDrawable bd = new BitmapDrawable(activity.getResources(), mImageCache.get(KEY_CACHE_BLURRED_BACKGROUND_IMAGE));
            bd.setAlpha(mAlpha);
            if (mFilterColor != -1) {
                bd.setColorFilter(mFilterColor, PorterDuff.Mode.DST_ATOP);
            }
            activity.getWindow().setBackgroundDrawable(bd);
            mImageCache.remove(KEY_CACHE_BLURRED_BACKGROUND_IMAGE);
            cacheBlurBehindAndExecuteTask = null;
        }
    }
    public void setBackground(Activity activity,View view) {
    	if (mImageCache.size() != 0) {
    		BitmapDrawable bd = new BitmapDrawable(activity.getResources(), mImageCache.get(KEY_CACHE_BLURRED_BACKGROUND_IMAGE));
    		bd.setAlpha(mAlpha);
    		if (mFilterColor != -1) {
    			bd.setColorFilter(mFilterColor, PorterDuff.Mode.DST_ATOP);
    		}
    		view.setBackgroundDrawable(bd);
    		mImageCache.remove(KEY_CACHE_BLURRED_BACKGROUND_IMAGE);
    		cacheBlurBehindAndExecuteTask = null;
    	}else {
    		
    	}
    }

    private class CacheBlurBehindAndExecuteTask extends AsyncTask<Void, Void, Void> {
        private Activity activity;
        private Runnable runnable;
        private View v;
        private View decorView;
        private Bitmap image;

        public CacheBlurBehindAndExecuteTask(Activity a, Runnable r,View v) {
            activity = a;
            runnable = r;
            v = v;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            decorView = activity.getWindow().getDecorView();
            decorView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_LOW);
            decorView.setDrawingCacheEnabled(true);
            decorView.buildDrawingCache();
            image = decorView.getDrawingCache();
//            View view = activity.findViewById(R.id.textView);
//            view=  decorView.findViewById(R.id.textView);
//            view.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_LOW);
//            view.setDrawingCacheEnabled(true);
//            view.buildDrawingCache();
//            image = view.getDrawingCache();
        }

        @Override
        protected Void doInBackground(Void... params) {
            Bitmap blurredBitmap = Blur.apply(activity, image, CONSTANT_BLUR_RADIUS);
            mImageCache.put(KEY_CACHE_BLURRED_BACKGROUND_IMAGE, blurredBitmap);

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            decorView.destroyDrawingCache();
            decorView.setDrawingCacheEnabled(false);

            activity = null;

            runnable.run();

            mState = State.READY;
        }
    }
}
