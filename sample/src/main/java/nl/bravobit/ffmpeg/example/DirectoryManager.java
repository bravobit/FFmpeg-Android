package nl.bravobit.ffmpeg.example;


import android.os.Environment;

import java.io.File;

public class DirectoryManager {

    /**
     * Gets the project root folder and creates it when it does not exist
     *
     * @return the project root folder
     */
    private static File getProjectRoot() {
        File folder = new File(Environment.getExternalStorageDirectory(), "ffmpeg-test/");
        if (!folder.exists()) folder.mkdirs();

        return folder;
    }

    /**
     * Generates a random result file in the project root folder
     *
     * @param extension - the extension of the file you want
     * @return random file located in the project root folder
     */
    public static File getRandomFile(String extension) {
        return new File(getProjectRoot(), System.currentTimeMillis() + "." + extension);
    }
}