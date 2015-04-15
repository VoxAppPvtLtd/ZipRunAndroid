package com.ziprun.consumer.ui.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;

public abstract class BackHandlerFragment extends Fragment {
    protected BackHandlerInterface backHandlerInterface;
    public abstract boolean onBackPressed();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(!(getActivity()  instanceof BackHandlerInterface)) {
            throw new ClassCastException("Hosting activity must implement BackHandlerInterface");
        } else {
            backHandlerInterface = (BackHandlerInterface) getActivity();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        // Mark this fragment as the selected Fragment.
        backHandlerInterface.setSelectedFragment(this);
    }



    public interface BackHandlerInterface {
        public void setSelectedFragment(BackHandlerFragment backHandledFragment);
    }
}