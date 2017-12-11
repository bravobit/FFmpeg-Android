package nl.bravobit.ffmpeg.example;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import nl.bravobit.ffmpeg.ExecuteBinaryResponseHandler;
import nl.bravobit.ffmpeg.FFmpeg;
import nl.bravobit.ffmpeg.exceptions.FFmpegCommandAlreadyRunningException;

/**
 * Created by Brian on 11-12-17.
 */
public class ExampleActivity extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (FFmpeg.getInstance(this).isSupported()) {
            version();
        }

    }

    private void version() {
        try {
            FFmpeg.getInstance(this).execute(new String[]{"-version"}, new ExecuteBinaryResponseHandler() {
                @Override
                public void onSuccess(String message) {
                    Log.e("ExampleActivity", message);
                }

                @Override
                public void onProgress(String message) {
                    Log.e("ExampleActivity", message);
                }
            });
        } catch (FFmpegCommandAlreadyRunningException e) {
            e.printStackTrace();
        }
    }
}
