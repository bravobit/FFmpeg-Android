package nl.bravobit.ffmpeg;

import android.content.Context;
import android.content.SharedPreferences;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.util.Map;

import nl.bravobit.ffmpeg.exceptions.FFmpegCommandAlreadyRunningException;

public class FFmpeg implements FFbinaryInterface {
    private static final int VERSION = 10; // up this version when you add a new ffmpeg build
    private static final String KEY_PREF_VERSION = "ffmpeg_version";

    private final FFbinaryContextProvider context;
    private FFcommandExecuteAsyncTask ffmpegExecuteAsyncTask;

    private static final long MINIMUM_TIMEOUT = 10 * 1000;
    private long timeout = Long.MAX_VALUE;

    private static FFmpeg instance = null;

    private FFmpeg(FFbinaryContextProvider context) {
        this.context = context;
        Log.setDebug(Util.isDebug(this.context.provide()));
    }

    public static FFmpeg getInstance(final Context context) {
        if (instance == null) {
            instance = new FFmpeg(new FFbinaryContextProvider() {
                @Override
                public Context provide() {
                    return context;
                }
            });
        }
        return instance;
    }

    @Override
    public boolean isSupported() {
        // check if arch is supported
        CpuArch cpuArch = CpuArchHelper.getCpuArch();
        if (cpuArch == CpuArch.NONE) {
            Log.e("arch not supported");
            return false;
        }

        // get ffmpeg file
        File ffmpeg = FileUtils.getFFmpeg(context.provide());

        SharedPreferences settings = context.provide().getSharedPreferences("ffmpeg_prefs", Context.MODE_PRIVATE);
        int version = settings.getInt(KEY_PREF_VERSION, 0);

        // check if ffmpeg file exists
        if (!ffmpeg.exists() || version < VERSION) {
            String prefix = "arm/";
            if (cpuArch == CpuArch.x86) {
                prefix = "x86/";
            }
            Log.d("file does not exist, creating it...");

            try {
                InputStream inputStream = context.provide().getAssets().open(prefix + "ffmpeg");
                if (!FileUtils.inputStreamToFile(inputStream, ffmpeg)) {
                    return false;
                }

                Log.d("successfully wrote ffmpeg file!");

                settings.edit().putInt(KEY_PREF_VERSION, VERSION).apply();
            } catch (IOException e) {
                Log.e("error while opening assets", e);
                return false;
            }
        }

        // check if ffmpeg can be executed
        if (!ffmpeg.canExecute()) {
            // try to make executable
            try {
                try {
                    Runtime.getRuntime().exec("chmod -R 777 " + ffmpeg.getAbsolutePath()).waitFor();
                } catch (InterruptedException e) {
                    Log.e("interrupted exception", e);
                    return false;
                } catch (IOException e) {
                    Log.e("io exception", e);
                    return false;
                }

                if (!ffmpeg.canExecute()) {
                    // our last hope!
                    if (!ffmpeg.setExecutable(true)) {
                        Log.e("unable to make executable");
                        return false;
                    }
                }
            } catch (SecurityException e) {
                Log.e("security exception", e);
                return false;
            }
        }

        Log.d("ffmpeg is ready!");

        return true;
    }

    @Override
    public void execute(Map<String, String> environvenmentVars, String[] cmd, FFcommandExecuteResponseHandler ffmpegExecuteResponseHandler) throws FFmpegCommandAlreadyRunningException {
        if (ffmpegExecuteAsyncTask != null && !ffmpegExecuteAsyncTask.isProcessCompleted()) {
            throw new FFmpegCommandAlreadyRunningException("FFmpeg command is already running, you are only allowed to run single command at a time");
        }
        if (cmd.length != 0) {
            String[] ffmpegBinary = new String[]{FileUtils.getFFmpegCommand(context.provide(), environvenmentVars)};
            String[] command = concatenate(ffmpegBinary, cmd);
            ffmpegExecuteAsyncTask = new FFcommandExecuteAsyncTask(command, timeout, ffmpegExecuteResponseHandler);
            ffmpegExecuteAsyncTask.execute();
        } else {
            throw new IllegalArgumentException("shell command cannot be empty");
        }
    }

    private static <T> T[] concatenate(T[] a, T[] b) {
        int aLen = a.length;
        int bLen = b.length;

        @SuppressWarnings("unchecked")
        T[] c = (T[]) Array.newInstance(a.getClass().getComponentType(), aLen + bLen);
        System.arraycopy(a, 0, c, 0, aLen);
        System.arraycopy(b, 0, c, aLen, bLen);

        return c;
    }

    @Override
    public void execute(String[] cmd, FFcommandExecuteResponseHandler ffmpegExecuteResponseHandler) throws FFmpegCommandAlreadyRunningException {
        execute(null, cmd, ffmpegExecuteResponseHandler);
    }

    @Override
    public boolean isCommandRunning() {
        return ffmpegExecuteAsyncTask != null && !ffmpegExecuteAsyncTask.isProcessCompleted();
    }

    @Override
    public boolean killRunningProcesses() {
        boolean status = Util.killAsync(ffmpegExecuteAsyncTask);
        ffmpegExecuteAsyncTask = null;
        return status;
    }

    @Override
    public void setTimeout(long timeout) {
        if (timeout >= MINIMUM_TIMEOUT) {
            this.timeout = timeout;
        }
    }

    @Override
    public FFbinaryObserver whenFFbinaryIsReady(Runnable onReady, int timeout) {
        return Util.observeOnce(new Util.ObservePredicate() {
            @Override
            public Boolean isReadyToProceed() {
                return !isCommandRunning();
            }
        }, onReady, timeout);
    }
}
