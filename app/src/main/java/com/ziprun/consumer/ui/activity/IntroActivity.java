package com.ziprun.consumer.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.viewpagerindicator.CirclePageIndicator;
import com.ziprun.consumer.R;
import com.ziprun.consumer.utils.Utils;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

public class IntroActivity extends ZipBaseActivity {
    private static final String TAG = IntroActivity.class.getCanonicalName();

    @InjectView(R.id.viewPager)
    ViewPager viewPager;

    @InjectView(R.id.circleIndicator)
    CirclePageIndicator circlePageIndicator;

    @InjectView(R.id.registerBtn)
    Button registerBtn;

    IntroPagerAdapter introPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);
        ButterKnife.inject(this);
        introPagerAdapter = new IntroPagerAdapter(this);
        viewPager.setAdapter(introPagerAdapter);

        circlePageIndicator.setFillColor(getResources()
                .getColor(R.color.secondary_color));
        circlePageIndicator.setViewPager(viewPager);
        circlePageIndicator.setRadius(10);

        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if(position == 2){
                    registerBtn.setVisibility(View.VISIBLE);
                }else{
                    registerBtn.setVisibility(View.GONE);
                }
                circlePageIndicator.onPageSelected(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        introPagerAdapter = null;
    }

    @OnClick(R.id.registerBtn)
    public void onClickRegister(){
        startActivity(new Intent(this,  LoginActivity.class));
        finish();
    }

}

class IntroPagerAdapter extends PagerAdapter{

    private static final String TAG = IntroPagerAdapter.class.getCanonicalName();
    private Context context;

    private static int[] INTRO_DRAWABLE = new int[]{
            R.drawable.ziprun_intro_01,
            R.drawable.ziprun_intro_02,
            R.drawable.ziprun_intro_03
    };

    @Inject
    Utils utils;



    public IntroPagerAdapter(Context context){
        this.context = context;
        ((ZipBaseActivity) context).inject(this);

    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
        Log.i(TAG, "Instantiate Item called");

        LayoutInflater inflater = LayoutInflater.from(context);
        final ImageView introImgView =  (ImageView)inflater.inflate(
                R.layout.introscreen, container, false);

        container.addView(introImgView);

        imageLoaderObservable(INTRO_DRAWABLE[position],
            utils.getScreenWidth(), utils.getScreenHeight())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.newThread())
            .subscribe(new Action1<Bitmap>() {
                @Override
                public void call(Bitmap bitmap) {
                    Log.i(TAG, "Bitmap Received Yipee " + position);
                    introImgView.setImageBitmap(bitmap);
                }
            });
        return introImgView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        Log.i(TAG, "Destroy Item called");
        container.removeView((View) object);
        object = null;
    }

    @Override
    public int getCount() {
        return INTRO_DRAWABLE.length;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    public Observable<Bitmap> imageLoaderObservable(final int resID, final int width,
                                                    final int height){


        return Observable.create(new Observable.OnSubscribe<Bitmap>() {
            @Override
            public void call(Subscriber<? super Bitmap> subscriber) {
                subscriber.onNext(decodeSampledBitmapFromResource(
                        context.getResources(), resID, width, height));
            }
        });
    }

    public Bitmap decodeSampledBitmapFromResource(Resources res, int resId,
                                                  int reqWidth, int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        Log.i(TAG, "Sampling Size: " + options.inSampleSize);

                // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(res, resId, options);
    }

    public int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;

        Log.i(TAG, reqWidth + " " + reqHeight + " "  + width + " " + height);
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

}
