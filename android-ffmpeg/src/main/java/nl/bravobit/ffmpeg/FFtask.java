package nl.bravobit.ffmpeg;

public interface FFtask {

    /**
     * Sends 'q' to the ff binary running process asynchronously
     */
    void sendQuitSignal();
}
