package com.ziprun.consumer.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.astuetz.PagerSlidingTabStrip;
import com.ziprun.consumer.R;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class InstructionFragment extends ZipBaseFragment {
    private static final String TAG = InstructionFragment.class.getCanonicalName();


    @InjectView(R.id.tabs)
    PagerSlidingTabStrip tabs;

    @InjectView(R.id.pager)
    ViewPager viewPager;

    InstructionPagerAdapter instructionPagerAdapter;

    private static final int TAB_TEXT_SIZE = 14; //sp


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_instruction,
                container, false);

        ButterKnife.inject(this, view);

        instructionPagerAdapter = new InstructionPagerAdapter(getActivity());
        viewPager.setAdapter(instructionPagerAdapter);

        tabs.setViewPager(viewPager);

        DisplayMetrics dm = getResources().getDisplayMetrics();

        tabs.setTextSize((int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_SP, TAB_TEXT_SIZE, dm));

        return view;
    }


    @Override
    public void onStart() {
        super.onStart();
    }



    @Override
    public void setActionBar(ActionBar actionBar){
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setDisplayUseLogoEnabled(false);
        actionBar.setTitle(R.string.instruction_fragment_title);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);

    }

    public static class InstructionPagerAdapter extends PagerAdapter {

        private static final int BUY_TAB_RESID = R.string
                .instruction_buy_something;

        private static final int PICKUP_TAB_RESID = R.string
                .instruction_pickup_something;

        private static final int[]TABS_RESID_ARR = new int[]{ BUY_TAB_RESID,
                PICKUP_TAB_RESID};

        private Context context;

        public InstructionPagerAdapter(Context context){
            this.context = context;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return context.getResources().getString(TABS_RESID_ARR[position]);
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            LayoutInflater inflater = LayoutInflater.from(context);
            ViewGroup view = (ViewGroup)inflater.inflate(
                    R.layout.tabview_instruction, container, false);

            container.addView(view, position);
            return view;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeViewAt(position);
        }

        @Override
        public int getCount() {
            return TABS_RESID_ARR.length;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }
    }

    @Override
    public boolean onBackPressed() {
        return false;
    }
}
