package nl.bravobit.ffmpeg;

import android.content.Context;

import java.io.File;
import java.lang.reflect.Array;
import java.util.Map;

import nl.bravobit.ffmpeg.exceptions.FFmpegCommandAlreadyRunningException;

public class FFmpeg implements FFmpegInterface {

    private final FFmpegContextProvider context;
    private FFmpegExecuteAsyncTask ffmpegExecuteAsyncTask;

    private static final long MINIMUM_TIMEOUT = 10 * 1000;
    private long timeout = Long.MAX_VALUE;

    private static FFmpeg instance = null;

    private FFmpeg(FFmpegContextProvider context) {
        this.context = context;
        Log.setDebug(Util.isDebug(this.context.provide()));
    }

    public static FFmpeg getInstance(final Context context) {
        if (instance == null) {
            instance = new FFmpeg(new FFmpegContextProvider() {
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
        if (CpuArchHelper.getCpuArch() == CpuArch.NONE) {
            return false;
        }

        // get ffmpeg file
        File ffmpeg = FileUtils.getFFmpeg(context.provide());

        // check if ffmpeg file exists
        if (!ffmpeg.exists()) {
            return false;
        }

        // check if ffmpeg can be executed
        if (!ffmpeg.canExecute()) {
            // try to make executable
            try {
                if (!ffmpeg.setExecutable(true)) {
                    return false;
                }
            } catch (SecurityException e) {
                return false;
            }
        }

        return true;
    }

    @Override
    public void execute(Map<String, String> environvenmentVars, String[] cmd, FFmpegExecuteResponseHandler ffmpegExecuteResponseHandler) throws FFmpegCommandAlreadyRunningException {
        if (ffmpegExecuteAsyncTask != null && !ffmpegExecuteAsyncTask.isProcessCompleted()) {
            throw new FFmpegCommandAlreadyRunningException("FFmpeg command is already running, you are only allowed to run single command at a time");
        }
        if (cmd.length != 0) {
            String[] ffmpegBinary = new String[]{FileUtils.getFFmpeg(context.provide(), environvenmentVars)};
            String[] command = concatenate(ffmpegBinary, cmd);
            ffmpegExecuteAsyncTask = new FFmpegExecuteAsyncTask(command, timeout, ffmpegExecuteResponseHandler);
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
    public void execute(String[] cmd, FFmpegExecuteResponseHandler ffmpegExecuteResponseHandler) throws FFmpegCommandAlreadyRunningException {
        execute(null, cmd, ffmpegExecuteResponseHandler);
    }

    @Override
    public boolean isFFmpegCommandRunning() {
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
    public FFmpegObserver whenFFmpegIsReady(Runnable onReady, int timeout) {
        return Util.observeOnce(new Util.ObservePredicate() {
            @Override
            public Boolean isReadyToProceed() {
                return !isFFmpegCommandRunning();
            }
        }, onReady, timeout);
    }
}
