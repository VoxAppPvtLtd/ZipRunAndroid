package com.ziprun.consumer;

import com.ziprun.consumer.ui.UIModule;

final class Modules {
    private static final String TAG = Modules.class.getCanonicalName();

    public static Object[] list(ZipRunApp app) {
        return new Object[] {
            new ApplicationModule(app),
            new UIModule()
        };
    }

    private Modules() {
        // No instances.
    }
}
