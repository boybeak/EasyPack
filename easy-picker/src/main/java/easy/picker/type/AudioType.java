package easy.picker.type;

public class AudioType extends AbsSuffixType {

    private static final String PREFIX = "audio";

    public static AudioType any() {
        return new AudioType("*");
    }

    AudioType(String suffix) {
        super(suffix);
    }

//    @Override
//    public String getActionForDevice() {
//        return MediaStore.Audio.Media.RECORD_SOUND_ACTION;
//    }

    @Override
    public String getPrefix() {
        return PREFIX;
    }

}
