package nl.bravobit.ffmpeg.example;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import nl.bravobit.ffmpeg.ExecuteBinaryResponseHandler;
import nl.bravobit.ffmpeg.FFmpeg;
import nl.bravobit.ffmpeg.FFprobe;
import nl.bravobit.ffmpeg.FFtask;
import nl.bravobit.ffmpeg.exceptions.FFmpegCommandAlreadyRunningException;
import nl.bravobit.ffmpeg.exceptions.FFprobeCommandAlreadyRunningException;

/**
 * Created by Brian on 11-12-17.
 */
public class ExampleActivity extends AppCompatActivity {
    private Handler handler = new Handler();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (FFmpeg.getInstance(this).isSupported()) {
            // ffmpeg is supported
            //versionFFmpeg();
            ffmpegTestTaskQuit();
        } else {
            // ffmpeg is not supported
        }

        if (FFprobe.getInstance(this).isSupported()) {
            // ffprobe is supported
            versionFFprobe();
        } else {
            // ffprobe is not supported
        }

    }

    private void versionFFmpeg() {
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

    private void versionFFprobe() {
        try {
            Log.e("ExampleActivity", "version ffprobe");
            FFprobe.getInstance(this).execute(new String[]{"-version"}, new ExecuteBinaryResponseHandler() {
                @Override
                public void onSuccess(String message) {
                    Log.e("ExampleActivity", message);
                }

                @Override
                public void onProgress(String message) {
                    Log.e("ExampleActivity", message);
                }
            });
        } catch (FFprobeCommandAlreadyRunningException e) {
            e.printStackTrace();
        }
    }

    private void ffmpegTestTaskQuit() {
        try {
            String[] command = {
                    "-y",
                    "-i",
                    "/storage/emulated/0/DCIM/Camera/VID_20171222_104945.mp4",
                    "/storage/emulated/0/DCIM/Camera/" + System.currentTimeMillis() + ".webm"
            };

            final FFtask task = FFmpeg.getInstance(this).execute(command, new ExecuteBinaryResponseHandler() {
                @Override
                public void onStart() {
                    Log.e("ExampleActivity", "on start");
                }

                @Override
                public void onFinish() {
                    Log.e("ExampleActivity", "on finish");
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Log.e("ExampleActivity", "RESTART RENDERING");
                            ffmpegTestTaskQuit();
                        }
                    }, 5000);
                }

                @Override
                public void onSuccess(String message) {
                    Log.e("ExampleActivity", message);
                }

                @Override
                public void onProgress(String message) {
                    Log.e("ExampleActivity", message);
                }

                @Override
                public void onFailure(String message) {
                    Log.e("ExampleActivity", message);
                }
            });

            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Log.e("ExampleActivity", "STOPPING THE RENDERING!");
                    task.sendQuitSignal();
                }
            }, 8000);
        } catch (FFmpegCommandAlreadyRunningException e) {
            Log.e("ExampleActivity", "command already running");
        }
    }
}
