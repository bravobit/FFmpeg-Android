package nl.bravobit.ffmpeg;

import android.content.Context;

import java.io.File;
import java.util.Map;

class FileUtils {
    private static final String FFMPEG_FILE_NAME = "ffmpeg.so";

    static File getFFmpeg(Context context) {
        File libraryFolder = new File(context.getFilesDir().getParent(), "lib/");
        return new File(libraryFolder, FFMPEG_FILE_NAME);
    }

    static String getFFmpeg(Context context, Map<String, String> environmentVars) {
        String ffmpegCommand = "";
        if (environmentVars != null) {
            for (Map.Entry<String, String> var : environmentVars.entrySet()) {
                ffmpegCommand += var.getKey() + "=" + var.getValue() + " ";
            }
        }
        ffmpegCommand += getFFmpeg(context);
        return ffmpegCommand;
    }
}