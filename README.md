# FFmpeg-Android
[ ![Download](https://api.bintray.com/packages/bravobit/Android-FFmpeg/android-ffmpeg/images/download.svg) ](https://bintray.com/bravobit/Android-FFmpeg/android-ffmpeg/_latestVersion)

FFMpeg/FFprobe compiled for Android.
Execute FFmpeg & FFprobe commands with ease in your Android project.

## About
This project is a continued fork of [FFmpeg Android Java](https://github.com/WritingMinds/ffmpeg-android-java) by WritingMinds.
This fork fixes the `CANNOT LINK EXECUTABLE ffmpeg: has text relocations` issue on x86 devices along with some other bugfixes, new features and the newest FFmpeg builds.

### Architectures
Bravobit FFmpeg-Android runs on the following architectures:
- armv7
- armv7-neon
- armv8
- x86
- x86_64

### FFmpeg build
FFmpeg in this project was built with the following libraries:
- x264
- libpng
- freetype2
- libmp3lame
- libvorbis

### Features
- Uses the latest FFmpeg release `n3.4.1`
- Uses native CPU capabilities on ARM architectures
- FFprobe is bundled in this library too

## Usage

### Getting Started
Include the dependency
```gradle
dependencies {
    implementation 'nl.bravobit:android-ffmpeg:1.0.8'
}
```

### Check if FFmpeg is supported
To check whether FFmpeg is available on your device you can use the following method.
```java
if (FFmpeg.getInstance(this).isSupported()) {
  // ffmpeg is supported
} else {
  // ffmpeg is not supported
}
```
This is all you have to do to load the FFmpeg library.

### Run FFmpeg command
In this sample code we will run the ffmpeg -version command.
```java
FFmpeg ffmpeg = FFmpeg.getInstance(context);
try {
  // to execute "ffmpeg -version" command you just need to pass "-version"
  ffmpeg.execute(cmd, new ExecuteBinaryResponseHandler() {

    @Override
    public void onStart() {}

    @Override
    public void onProgress(String message) {}

    @Override
    public void onFailure(String message) {}

    @Override
    public void onSuccess(String message) {}

    @Override
    public void onFinish() {}

  });
} catch (FFmpegCommandAlreadyRunningException e) {
  // Handle if FFmpeg is already running
}
```

### Check if FFprobe is supported
To check whether FFprobe is available on your device you can use the following method.
```java
if (FFprobe.getInstance(this).isSupported()) {
  // ffprobe is supported
} else {
  // ffprobe is not supported
}
```
This is all you have to do to load the FFprobe library.

### Run FFprobe command
In this sample code we will run the ffprobe -version command.
```java
FFprobe ffprobe = FFprobe.getInstance(context);
try {
  // to execute "ffprobe -version" command you just need to pass "-version"
  ffprobe.execute(cmd, new ExecuteBinaryResponseHandler() {

    @Override
    public void onStart() {}

    @Override
    public void onProgress(String message) {}

    @Override
    public void onFailure(String message) {}

    @Override
    public void onSuccess(String message) {}

    @Override
    public void onFinish() {}

  });
} catch (FFprobeCommandAlreadyRunningException e) {
  // Handle if FFprobe is already running
}
```

## Special Thanks To
- [hiteshsondhi88](https://github.com/hiteshsondhi88)
- [diegoperini](https://github.com/diegoperini)
- [ravindu1024](https://github.com/ravindu1024)
