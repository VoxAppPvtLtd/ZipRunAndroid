package com.ziprun.consumer.activity;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

import com.ziprun.consumer.ZipRunApplication;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import dagger.ObjectGraph;

public abstract class BaseActivity extends ActionBarActivity {

    private ObjectGraph activityGraph;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        ZipRunApplication application = (ZipRunApplication) getApplication();
        activityGraph = application.getApplicationGraph().plus(
                getModules().toArray());
        activityGraph.inject(this);
    }


    /**
     * A list of modules to use for the individual com.voxapp.sdk.activity graph. Subclasses
     * can override this method to provide additional modules provided they call
     * and include the modules returned by calling {@code super.getModules()}.
     */
    protected List<Object> getModules() {
        return new ArrayList<Object>(Arrays.<Object>asList(new ActivityModule
                (this)));
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

}
