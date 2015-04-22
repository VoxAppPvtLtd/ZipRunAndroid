package com.ziprun.consumer.ui.fragment;

import android.support.v7.app.ActionBar;

import com.ziprun.consumer.R;

public class SourceLocationPickerFragment extends LocationPickerFragment {
    public static final String TAG = SourceLocationPickerFragment.class.getCanonicalName();

    @Override
    protected Object getCurrentModule(){
        return new SourceLocationPickerModule(this);
    }

    @Override
    public void setActionBar(ActionBar actionBar){
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayUseLogoEnabled(true);
        actionBar.setHomeButtonEnabled(false);
        actionBar.setDisplayHomeAsUpEnabled(false);
        actionBar.setLogo(R.drawable.ziprun_white_emboss);
    }


    @Override
    public int getMarkerResource() {
        return R.drawable.icon_blue_map_marker;

    }

    @Override
    public int getNextBtnResource(){
        return R.string.go_to_order_instruction;
    }

    @Override
    public int getHelpTextResource() {
        return R.string.help_text_source_location;
    }
}
