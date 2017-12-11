package nl.bravobit.ffmpeg.exceptions;

public class FFmpegCommandAlreadyRunningException extends Exception {

    public FFmpegCommandAlreadyRunningException(String message) {
        super(message);
    }

}
