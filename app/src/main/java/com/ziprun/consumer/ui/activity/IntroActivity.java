package com.ziprun.consumer.ui.activity;

import android.content.Context;
import android.content.Intent;
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

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

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
        viewPager.setOffscreenPageLimit(2);

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

    @OnClick(R.id.registerBtn)
    public void onClickRegister(){
        startActivity(new Intent(this, LoginActivity.class));
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


    public IntroPagerAdapter(Context context){
        this.context = context;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        Log.i(TAG, "Instantiate Item called");

        LayoutInflater inflater = LayoutInflater.from(context);
        ImageView introImgView =  (ImageView)inflater.inflate(
                R.layout.introscreen, container, false);
        container.addView(introImgView);
        introImgView.setImageResource(INTRO_DRAWABLE[position]);
        return introImgView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        Log.i(TAG, "Destroy Item called");
        container.removeView((View) object);
    }

    @Override
    public int getCount() {
        return INTRO_DRAWABLE.length;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }
}
