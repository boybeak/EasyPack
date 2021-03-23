package easy.picker;

import easy.picker.type.MimeType;
import easy.picker.source.CaptureSource;
import easy.picker.source.GallerySource;
import easy.picker.source.Source;

public class EasyPicker {

    public static GallerySource fromGallery(MimeType mimeType) {
        return fromSource(new GallerySource(mimeType));
    }

    public static CaptureSource.ImageCaptureSource captureImage() {
        return fromSource(new CaptureSource.ImageCaptureSource());
    }

    public static CaptureSource.VideoCaptureSource captureVideo() {
        return fromSource(new CaptureSource.VideoCaptureSource());
    }

    public static CaptureSource.AudioCaptureSource captureAudio() {
        return fromSource(new CaptureSource.AudioCaptureSource());
    }

    public static <T extends Source> T fromSource(T t) {
        if (!isInitialized()) {
            throw new IllegalStateException("You must init EasyPicker before use it.");
        }
        return t;
    }

    private static Config sConfig;

    public static void init(Config config) {
        if (isInitialized()) {
            throw new IllegalStateException("Do not initialize EasyPicker twice.");
        }
        sConfig = config;
    }

    public static Config getConfig() {
        return sConfig;
    }

    public static boolean isInitialized() {
        return sConfig != null;
    }

    private EasyPicker(){
    }

}