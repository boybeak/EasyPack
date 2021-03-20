package easy.picker.type;

public abstract class AbsSuffixType implements MimeType {

    private final String suffix;

    AbsSuffixType(String suffix) {
        this.suffix = suffix;
    }

    @Override
    public String getSuffix() {
        return suffix;
    }
}
