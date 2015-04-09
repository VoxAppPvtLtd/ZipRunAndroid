package com.ziprun.consumer.ui.activity;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

import com.ziprun.consumer.ZipRunApp;
import com.ziprun.consumer.ui.fragment.BackHandlerFragment;

import dagger.ObjectGraph;

public abstract class ZipBaseActivity extends ActionBarActivity implements
        BackHandlerFragment.BackHandlerInterface {

    private ObjectGraph activityGraph;

    private BackHandlerFragment currentFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        ZipRunApp application = (ZipRunApp) getApplication();
        activityGraph = application.getApplicationGraph().plus(
                getModules());
        activityGraph.inject(this);
    }


    /**
     * A list of modules to use for the individual activity graph. Subclasses
     * can override this method to provide additional modules provided they call
     * and include the modules returned by calling {@code super.getModules()}.
     */
    protected Object[] getModules() {
        return new Object[]{new ActivityModule(this)};
    }

    
    /**
     * Inject the supplied {@code object} using the com.voxapp.sdk.activity-specific graph.
     */
    public void inject(Object object) {
        activityGraph.inject(object);
    }

    public ObjectGraph getActivityGraph() {
        return activityGraph;
    }

    @Override
    public void setSelectedFragment(BackHandlerFragment selectedFragment) {
        currentFragment = selectedFragment;
    }

    @Override
    public void onBackPressed() {
        if(currentFragment != null && currentFragment.onBackPressed()){
            return;
        }
        super.onBackPressed();
    }
}
