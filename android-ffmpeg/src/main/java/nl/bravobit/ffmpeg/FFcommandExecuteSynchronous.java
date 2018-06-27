package nl.bravobit.ffmpeg;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.concurrent.TimeoutException;

public class FFcommandExecuteSynchronous {

    private final String[] cmd;
    private Map<String, String> environment;
    private final ShellCommand shellCommand;
    private final long timeout;
    private long startTime;
    private Process process;
    private String output = "";

    FFcommandExecuteSynchronous(String[] cmd, Map<String, String> environment, long timeout) {
        this.cmd = cmd;
        this.timeout = timeout;
        this.environment = environment;
        this.shellCommand = new ShellCommand();
    }

    private CommandResult runCommand() {
        startTime = System.currentTimeMillis();
        try {
            process = shellCommand.run(cmd, environment);
            if (process == null) {
                return CommandResult.getDummyFailureResponse();
            }
            Log.d("Running publishing updates method");
            checkAndUpdateProcess();
            return CommandResult.getOutputFromProcess(process);
        } catch (TimeoutException e) {
            Log.e("FFmpeg binary timed out", e);
            return new CommandResult(false, e.getMessage());
        } catch (Exception e) {
            Log.e("Error running FFmpeg binary", e);
        } finally {
            Util.destroyProcess(process);
        }
        return CommandResult.getDummyFailureResponse();
    }

    public String execute() {
        CommandResult commandResult = runCommand();
        output += commandResult.output;
        return output;
    }

    private void checkAndUpdateProcess() throws TimeoutException {
        while (!Util.isProcessCompleted(process)) {

            // checking if process is completed
            if (Util.isProcessCompleted(process)) {
                return;
            }

            // Handling timeout
            if (timeout != Long.MAX_VALUE && System.currentTimeMillis() > startTime + timeout) {
                throw new TimeoutException("FFmpeg binary timed out");
            }

            try {
                String line;
                BufferedReader reader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
                while ((line = reader.readLine()) != null) {
                    output += line + "\n";
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
