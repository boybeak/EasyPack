package easy.picker.type;

public class ImageType extends AbsSuffixType {

    private static final String PREFIX = "image";

    public static ImageType any() {
        return new ImageType("*");
    }
    public static ImageType bmp() {
        return new ImageType("bmp");
    }
    public static ImageType gif() {
        return new ImageType("gif");
    }
    public static ImageType jpeg() {
        return new ImageType("jpeg");
    }
    public static ImageType jpg() {
        return jpeg();
    }
    public static ImageType png() {
        return new ImageType("png");
    }
    public static ImageType tiff() {
        return new ImageType("tiff");
    }
    public static ImageType tif() {
        return tiff();
    }
    public static ImageType webp() {
        return new ImageType("webp");
    }

    ImageType(String suffix) {
        super(suffix);
    }

//    @Override
//    public String getActionForDevice() {
//        return MediaStore.ACTION_IMAGE_CAPTURE;
//    }

    @Override
    public String getPrefix() {
        return PREFIX;
    }

}
