package nl.bravobit.ffmpeg.example;

import android.app.Application;

import timber.log.Timber;

public class FFmpegExample extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        }
    }
}
