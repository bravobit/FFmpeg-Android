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
- x264 `r2851 ba24899`
- libpng `1.6.21`
- freetype2 `2.8.1`
- libmp3lame `3.100`
- libvorbis `1.3.5`
- libvpx `v1.6.1-1456-g7d1bf5d`
- libopus `1.2.1`
- fontconfig `2.11.94`
- libass `0.14.0`
- fribidi `0.19.7`
- expat `2.1.0`

### Features
- Uses the latest FFmpeg release `n4.0-39-gda39990`
- Uses native CPU capabilities on ARM architectures
- FFprobe is bundled in this library too
- Enabled network capabilities
- Multithreading

## Usage

### Getting Started
Include the dependency
```gradle
dependencies {
    implementation 'nl.bravobit:android-ffmpeg:1.1.3'
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
```

## Special Thanks To
- [hiteshsondhi88](https://github.com/hiteshsondhi88)
- [diegoperini](https://github.com/diegoperini)

## Licensing
- [Library license](https://github.com/bravobit/FFmpeg-Android/blob/master/LICENSE)
- [FFmpeg license](https://www.ffmpeg.org/legal.html)
