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
import android.widget.EditText;

import com.astuetz.PagerSlidingTabStrip;
import com.ziprun.consumer.R;
import com.ziprun.consumer.data.model.Booking;
import com.ziprun.consumer.presenter.InstructionPresenter;
import com.ziprun.consumer.ui.activity.DeliveryActivity;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class InstructionFragment extends DeliveryFragment {
    private static final String TAG = InstructionFragment.class.getCanonicalName();


    @InjectView(R.id.tabs)
    PagerSlidingTabStrip tabs;

    @InjectView(R.id.pager)
    ViewPager viewPager;

    InstructionPagerAdapter instructionPagerAdapter;

    InstructionPresenter instructionPresenter;

    private static final int TAB_TEXT_SIZE = 14; //sp

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_instruction,
                container, false);

        ButterKnife.inject(this, view);

        Booking booking = Booking.fromJson(getArguments().getString
                (DeliveryActivity.KEY_BOOKING));

        instructionPagerAdapter = new InstructionPagerAdapter(getActivity(),
                booking);
        viewPager.setAdapter(instructionPagerAdapter);

        tabs.setViewPager(viewPager);

        DisplayMetrics dm = getResources().getDisplayMetrics();

        tabs.setTextSize((int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_SP, TAB_TEXT_SIZE, dm));

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        instructionPresenter = (InstructionPresenter) presenter;
    }

    @Override
    protected Object getCurrentModule() {
        return new InstructionModule(this);
    }

    @Override
    public void setActionBar(ActionBar actionBar){
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setDisplayUseLogoEnabled(false);
        actionBar.setTitle(R.string.instruction_fragment_title);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        instructionPagerAdapter = null;
    }

    @OnClick(R.id.nextBtn)
    public void onNextClicked(View view) {
        instructionPresenter.moveForward();
    }

    public void changeTab(int tabPos){
        viewPager.setCurrentItem(tabPos);
    }

    public int getCurrentTab() {
        return viewPager.getCurrentItem();
    }

    public String getCurrentInstruction() {
        return instructionPagerAdapter.getInstruction(getCurrentTab());
    }


    public static class InstructionPagerAdapter extends PagerAdapter {

        private static final int BUY_TAB_RESID = R.string
                .instruction_buy_something;

        private static final int PICKUP_TAB_RESID = R.string
                .instruction_pickup_something;

        private static final int[]TABS_RESID_ARR = new int[]{ BUY_TAB_RESID,
                PICKUP_TAB_RESID};

        private EditText[] instructionTextArr = new EditText[2];

        private Context context;

        private Booking booking;


        public InstructionPagerAdapter(Context context, Booking booking){
            this.context = context;
            this.booking = booking;
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

            EditText instructionText = (EditText) view.findViewById(R.id.edit_instruction);



            instructionText.setText(booking.getNotes());

            container.addView(view, position);

            instructionTextArr[position] = instructionText;
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

        public String getInstruction(int position){
            return instructionTextArr[position].getText().toString();
        }
    }

    @Override
    public boolean onBackPressed() {
        return false;
    }
}
