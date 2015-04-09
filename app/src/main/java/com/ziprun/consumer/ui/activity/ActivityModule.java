package com.ziprun.consumer.ui.activity;

import com.ziprun.consumer.ui.custom.AddressAutocompleteView;

import dagger.Module;
import dagger.Provides;
import pl.charmas.android.reactivelocation.ReactiveLocationProvider;


@Module(injects = {
        LoginActivity.class,
        MainActivity.class,
        DeliveryActivity.class,
        AddressAutocompleteView.class,
    }, complete = false,
       library = true)
public class ActivityModule {
    private static final String TAG = ActivityModule.class.getCanonicalName();
    private final ZipBaseActivity activity;

    public ActivityModule(ZipBaseActivity activity) {
        this.activity = activity;
    }

    @Provides
    ReactiveLocationProvider provideReactiveLocationProvider(){
        return new ReactiveLocationProvider(activity);
    }


}
