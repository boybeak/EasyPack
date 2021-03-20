package easy.picker.type;

public class VideoType extends AbsSuffixType {

    private static final String PREFIX = "video";

    public static VideoType any() {
        return new VideoType("*");
    }

    VideoType(String suffix) {
        super(suffix);
    }

//    @Override
//    public String getActionForDevice() {
//        return MediaStore.ACTION_VIDEO_CAPTURE;
//    }

    @Override
    public String getPrefix() {
        return PREFIX;
    }

}