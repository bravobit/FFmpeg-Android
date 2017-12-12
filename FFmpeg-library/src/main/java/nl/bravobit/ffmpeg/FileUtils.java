package nl.bravobit.ffmpeg;

import android.content.Context;

import java.io.File;
import java.util.Map;

class FileUtils {
    private static final String FFMPEG_FILE_NAME = "ffmpeg.so";
    private static final String FFPROBE_FILE_NAME = "ffprobe.so";

    static File getFFmpeg(Context context) {
        return new File(context.getApplicationInfo().nativeLibraryDir, FFMPEG_FILE_NAME);
    }

    static File getFFprobe(Context context) {
        return new File(context.getApplicationInfo().nativeLibraryDir, FFPROBE_FILE_NAME);
    }

    static String getFFmpegCommand(Context context, Map<String, String> environmentVars) {
        String ffCommand = "";
        if (environmentVars != null) {
            for (Map.Entry<String, String> var : environmentVars.entrySet()) {
                ffCommand += var.getKey() + "=" + var.getValue() + " ";
            }
        }
        ffCommand += getFFmpeg(context);
        return ffCommand;
    }

    static String getFFprobeCommand(Context context, Map<String, String> environmentVars) {
        String ffCommand = "";
        if (environmentVars != null) {
            for (Map.Entry<String, String> var : environmentVars.entrySet()) {
                ffCommand += var.getKey() + "=" + var.getValue() + " ";
            }
        }
        ffCommand += getFFprobe(context);
        return ffCommand;
    }
}