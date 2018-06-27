package nl.bravobit.ffmpeg;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.util.Map;

public class FFmpeg implements FFbinaryInterface {
    private static final int VERSION = 17; // up this version when you add a new ffmpeg build
    private static final String KEY_PREF_VERSION = "ffmpeg_version";

    private final FFbinaryContextProvider context;

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
    public FFtask execute(Map<String, String> environvenmentVars, String[] cmd, FFcommandExecuteResponseHandler ffmpegExecuteResponseHandler) {
        if (cmd.length != 0) {
            String[] ffmpegBinary = new String[]{FileUtils.getFFmpeg(context.provide()).getAbsolutePath()};
            String[] command = concatenate(ffmpegBinary, cmd);
            FFcommandExecuteAsyncTask task = new FFcommandExecuteAsyncTask(command, environvenmentVars, timeout, ffmpegExecuteResponseHandler);
            task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            return task;
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
    public FFtask execute(String[] cmd, FFcommandExecuteResponseHandler ffmpegExecuteResponseHandler) {
        return execute(null, cmd, ffmpegExecuteResponseHandler);
    }

    @Override
    public String execute(Map<String, String> environmentVars, String[] cmd) {
        if (cmd.length != 0) {
            String[] ffmpegBinary = new String[]{FileUtils.getFFmpeg(context.provide()).getAbsolutePath()};
            String[] command = concatenate(ffmpegBinary, cmd);
            FFcommandExecuteSynchronous synchronous = new FFcommandExecuteSynchronous(command, environmentVars, timeout);
            return synchronous.execute();
        } else {
            throw new IllegalArgumentException("shell command cannot be empty");
        }
    }

    @Override
    public String execute(String[] cmd) {
        return execute(null, cmd);
    }

    @Override
    public boolean isCommandRunning(FFtask task) {
        return task != null && !task.isProcessCompleted();
    }

    @Override
    public boolean killRunningProcesses(FFtask task) {
        return task != null && task.killRunningProcess();
    }

    @Override
    public void setTimeout(long timeout) {
        if (timeout >= MINIMUM_TIMEOUT) {
            this.timeout = timeout;
        }
    }
}
