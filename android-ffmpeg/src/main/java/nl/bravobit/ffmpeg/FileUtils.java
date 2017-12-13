package nl.bravobit.ffmpeg;

import android.content.Context;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

class FileUtils {
    private static final String FFMPEG_FILE_NAME = "ffmpeg";
    private static final String FFPROBE_FILE_NAME = "ffprobe";

    static File getFFmpeg(Context context) {
        File folder = context.getFilesDir();
        return new File(folder, FFMPEG_FILE_NAME);
    }

    static File getFFprobe(Context context) {
        File folder = context.getFilesDir();
        return new File(folder, FFPROBE_FILE_NAME);
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

    static boolean inputStreamToFile(InputStream stream, File file) {
        try {
            InputStream input = new BufferedInputStream(stream);
            OutputStream output = new FileOutputStream(file);
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = input.read(buffer, 0, buffer.length)) >= 0) {
                output.write(buffer, 0, bytesRead);
            }
            output.flush();
            output.close();
            input.close();
            return true;
        } catch (IOException e) {
            Log.e("error while writing ff binary file", e);
        }
        return false;
    }
}