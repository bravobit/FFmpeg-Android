package nl.bravobit.ffmpeg;

import java.util.Map;

import nl.bravobit.ffmpeg.exceptions.FFmpegCommandAlreadyRunningException;

interface FFmpegInterface {

    /**
     * Executes a command
     *
     * @param environmentVars              Environment variables
     * @param cmd                          command to execute
     * @param ffmpegExecuteResponseHandler {@link FFmpegExecuteResponseHandler}
     * @throws FFmpegCommandAlreadyRunningException throws exception when FFmpeg is already running
     */
    void execute(Map<String, String> environmentVars, String[] cmd, FFmpegExecuteResponseHandler ffmpegExecuteResponseHandler) throws FFmpegCommandAlreadyRunningException;

    /**
     * Executes a command
     *
     * @param cmd                          command to execute
     * @param ffmpegExecuteResponseHandler {@link FFmpegExecuteResponseHandler}
     * @throws FFmpegCommandAlreadyRunningException throws exception when FFmpeg is already running
     */
    void execute(String[] cmd, FFmpegExecuteResponseHandler ffmpegExecuteResponseHandler) throws FFmpegCommandAlreadyRunningException;

    /**
     * Checks if FFmpeg is supported on this device
     *
     * @return true if FFmpeg is supported on this device
     */
    boolean isSupported();

    /**
     * Checks if FFmpeg command is currently running
     *
     * @return true if FFmpeg command is running
     */
    boolean isFFmpegCommandRunning();

    /**
     * Kill Running FFmpeg process
     *
     * @return true if process is killed successfully
     */
    boolean killRunningProcesses();

    /**
     * Timeout for FFmpeg process, should be minimum of 10 seconds
     *
     * @param timeout in milliseconds
     */
    void setTimeout(long timeout);

    /**
     * Wait for ffmpeg to get ready asynchronously
     *
     * @param onReady code to run when FFmpeg is ready
     * @param timeout when to give up in milliseconds
     */
    FFmpegObserver whenFFmpegIsReady(Runnable onReady, int timeout);
}
