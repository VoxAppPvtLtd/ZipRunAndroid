package com.ziprun.consumer;

final class Modules {
    private static final String TAG = Modules.class.getCanonicalName();

    public static Object[] list(ZipRunApp app) {
        return new Object[] {
            new ApplicationModule(app)
        };
    }

    private Modules() {
        // No instances.
    }
}
