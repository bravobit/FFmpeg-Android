package nl.bravobit.ffmpeg;

import android.content.Context;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

class FileUtils {
    private static final String FFMPEG_FILE_NAME = "ffmpeg";
    private static final String FFPROBE_FILE_NAME = "ffprobe";

    static File getFFmpeg(Context context) {
        File folder = new File(context.getApplicationInfo().nativeLibraryDir);
        return new File(folder, FFMPEG_FILE_NAME);
    }

    static File getFFprobe(Context context) {
        File folder = new File(context.getApplicationInfo().nativeLibraryDir);
        return new File(folder, FFPROBE_FILE_NAME);
    }
}