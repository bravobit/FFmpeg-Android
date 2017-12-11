package nl.bravobit.ffmpeg;

public interface FFmpegObserver extends Runnable {

    void cancel();
}
