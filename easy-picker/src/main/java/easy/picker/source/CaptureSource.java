package easy.picker.source;

import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.util.Pair;

import androidx.annotation.IntDef;
import androidx.annotation.NonNull;

import java.io.File;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import easy.fp.EasyFP;
import easy.picker.type.AudioType;
import easy.picker.EasyPicker;
import easy.picker.callback.MayCopySingleGet;
import easy.picker.callback.OnGet;
import easy.picker.callback.OnResultWrapper;
import easy.picker.callback.OnSingleGet;
import easy.safr.EasySAFR;

public abstract class CaptureSource implements Source {

    private Intent it = null;

    public abstract void capture(Context context, OnSingleGet singleGet);
    abstract String getAction();

    Intent getIntent() {
        if (it == null) {
            it = new Intent(getAction());
        }
        return it;
    }

    private static class IVCaptureOnGet extends OnResultWrapper<Uri, File> {

        private final EasyFP.Pair pair;

        private IVCaptureOnGet(EasyFP.Pair pair, OnGet<Uri, File> onGet) {
            super(onGet);
            this.pair = pair;
        }

        @Override
        public Pair<Uri, File> convertAsync(Intent data) throws Exception {
            return new Pair<>(pair.uri(), pair.file());
        }
    }

    public static class ImageCaptureSource extends CaptureSource {

        private EasyFP.Pair output = null;

        public ImageCaptureSource output(@NonNull EasyFP.Pair pair) {
            this.output = pair;
            return this;
        }

        @Override
        String getAction() {
            return MediaStore.ACTION_IMAGE_CAPTURE;
        }

        @Override
        public void capture(Context context, OnSingleGet singleGet) {
            if (output == null) {
                output(EasyPicker.getConfig().getOutputFactory().createOutput(context));
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                getIntent().addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            }
            getIntent().putExtra(MediaStore.EXTRA_OUTPUT, output.uri());
            getIntent().setClipData(ClipData.newRawUri("easy-picker-image-capture", output.uri()));

            EasySAFR.startActivityForResult(context, getIntent(), new IVCaptureOnGet(output, singleGet));
        }
    }

    public static class VideoCaptureSource extends ImageCaptureSource {

        public static final int QUALITY_LOW = 0, QUALITY_HIGH = 1;

        @Retention(RetentionPolicy.SOURCE)
        @IntDef({QUALITY_LOW, QUALITY_HIGH})
        public @interface Quality{}

        @Quality
        private int videoQuality = QUALITY_LOW;

        @Override
        String getAction() {
            return MediaStore.ACTION_VIDEO_CAPTURE;
        }

        public VideoCaptureSource videoQuality(@Quality int quality) {
            this.videoQuality = quality;
            return this;
        }

        @Override
        public void capture(Context context, OnSingleGet singleGet) {
            getIntent().putExtra(MediaStore.EXTRA_VIDEO_QUALITY, videoQuality);
            super.capture(context, singleGet);
        }
    }

    public static class AudioCaptureSource extends CaptureSource {
        @Override
        public void capture(Context context, OnSingleGet singleGet) {
            getIntent().setType(AudioType.any().create());
            EasySAFR.startActivityForResult(context, getIntent(), new MayCopySingleGet(context, singleGet));
        }

        @Override
        String getAction() {
            return MediaStore.Audio.Media.RECORD_SOUND_ACTION;
        }
    }
}
