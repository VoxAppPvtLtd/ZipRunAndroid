package com.ziprun.consumer.ui.fragment;

import android.support.v7.app.ActionBar;

import com.ziprun.consumer.R;

public class DestinationLocationPickerFragment extends LocationPickerFragment {
    private static final String TAG = DestinationLocationPickerFragment.class.getCanonicalName();

    @Override
    protected Object getCurrentModule(){
        return new DestinationLocationPickerModule(this);
    }

    @Override
    public void setActionBar(ActionBar actionBar){
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(getResources()
                .getString(R.string.title_delivery_location));
        actionBar.setDisplayUseLogoEnabled(false);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
    }


    @Override
    public int getMarkerResource() {
        return R.drawable.icon_green_map_marker;
    }

    @Override
    public int getNextBtnResource() {
        return R.string.go_to_confirmation;
    }

    @Override
    public int getHelpTextResource() {
        return R.string.help_text_destination;
    }
}
