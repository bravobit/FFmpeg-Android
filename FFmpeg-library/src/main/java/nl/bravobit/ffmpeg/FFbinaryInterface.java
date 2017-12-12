package nl.bravobit.ffmpeg;

import java.util.Map;

import nl.bravobit.ffmpeg.exceptions.FFcommandAlreadyRunningException;
import nl.bravobit.ffmpeg.exceptions.FFmpegCommandAlreadyRunningException;

interface FFbinaryInterface {

    /**
     * Executes a command
     *
     * @param environmentVars              Environment variables
     * @param cmd                          command to execute
     * @param ffcommandExecuteResponseHandler {@link FFcommandExecuteResponseHandler}
     * @throws FFmpegCommandAlreadyRunningException throws exception when binary is already running
     */
    void execute(Map<String, String> environmentVars, String[] cmd, FFcommandExecuteResponseHandler ffcommandExecuteResponseHandler) throws FFcommandAlreadyRunningException;

    /**
     * Executes a command
     *
     * @param cmd                          command to execute
     * @param ffcommandExecuteResponseHandler {@link FFcommandExecuteResponseHandler}
     * @throws FFmpegCommandAlreadyRunningException throws exception when binary is already running
     */
    void execute(String[] cmd, FFcommandExecuteResponseHandler ffcommandExecuteResponseHandler) throws FFcommandAlreadyRunningException;

    /**
     * Checks if FF binary is supported on this device
     *
     * @return true if FF binary is supported on this device
     */
    boolean isSupported();

    /**
     * Checks if a command is currently running
     *
     * @return true if a command is running
     */
    boolean isCommandRunning();

    /**
     * Kill running process
     *
     * @return true if process is killed successfully
     */
    boolean killRunningProcesses();

    /**
     * Timeout for binary process, should be minimum of 10 seconds
     *
     * @param timeout in milliseconds
     */
    void setTimeout(long timeout);

    /**
     * Wait for ffmpeg to get ready asynchronously
     *
     * @param onReady code to run when binary is ready
     * @param timeout when to give up in milliseconds
     */
    FFbinaryObserver whenFFbinaryIsReady(Runnable onReady, int timeout);
}
