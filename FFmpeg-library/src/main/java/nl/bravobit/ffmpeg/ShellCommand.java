package nl.bravobit.ffmpeg;

import java.io.IOException;
import java.util.Arrays;

class ShellCommand {

    Process run(String[] commandString) {
        Process process = null;
        try {
            process = Runtime.getRuntime().exec(commandString);
        } catch (IOException e) {
            Log.e("Exception while trying to run: " + Arrays.toString(commandString), e);
        }
        return process;
    }

}