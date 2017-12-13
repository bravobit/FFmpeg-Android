package nl.bravobit.ffmpeg;

import android.content.Context;

import java.io.File;
import java.lang.reflect.Array;
import java.util.Map;

import nl.bravobit.ffmpeg.exceptions.FFprobeCommandAlreadyRunningException;

public class FFprobe implements FFbinaryInterface {

    private final FFbinaryContextProvider context;
    private FFcommandExecuteAsyncTask ffprobeExecuteAsyncTask;

    private static final long MINIMUM_TIMEOUT = 10 * 1000;
    private long timeout = Long.MAX_VALUE;

    private static FFprobe instance = null;

    private FFprobe(FFbinaryContextProvider context) {
        this.context = context;
        Log.setDebug(Util.isDebug(this.context.provide()));
    }

    public static FFprobe getInstance(final Context context) {
        if (instance == null) {
            instance = new FFprobe(new FFbinaryContextProvider() {
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

        // get ffprobe file
        File ffprobe = FileUtils.getFFprobe(context.provide());

        // check if ffprobe file exists
        if (!ffprobe.exists()) {
            return false;
        }

        // check if ffprobe can be executed
        if (!ffprobe.canExecute()) {
            // try to make it executable
            try {
                if (!ffprobe.setExecutable(true)) {
                    return false;
                }
            } catch (SecurityException e) {
                return false;
            }
        }

        return true;
    }

    @Override
    public void execute(Map<String, String> environvenmentVars, String[] cmd, FFcommandExecuteResponseHandler ffcommandExecuteResponseHandler) throws FFprobeCommandAlreadyRunningException {
        if (ffprobeExecuteAsyncTask != null && !ffprobeExecuteAsyncTask.isProcessCompleted()) {
            throw new FFprobeCommandAlreadyRunningException("FFprobe command is already running, you are only allowed to run single command at a time");
        }
        if (cmd.length != 0) {
            String[] ffprobeBinary = new String[]{FileUtils.getFFprobeCommand(context.provide(), environvenmentVars)};
            String[] command = concatenate(ffprobeBinary, cmd);
            ffprobeExecuteAsyncTask = new FFcommandExecuteAsyncTask(command, timeout, ffcommandExecuteResponseHandler);
            ffprobeExecuteAsyncTask.execute();
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
    public void execute(String[] cmd, FFcommandExecuteResponseHandler ffcommandExecuteResponseHandler) throws FFprobeCommandAlreadyRunningException {
        execute(null, cmd, ffcommandExecuteResponseHandler);
    }

    @Override
    public boolean isCommandRunning() {
        return ffprobeExecuteAsyncTask != null && !ffprobeExecuteAsyncTask.isProcessCompleted();
    }

    @Override
    public boolean killRunningProcesses() {
        boolean status = Util.killAsync(ffprobeExecuteAsyncTask);
        ffprobeExecuteAsyncTask = null;
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
