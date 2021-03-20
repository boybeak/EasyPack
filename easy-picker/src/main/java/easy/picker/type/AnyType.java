package easy.picker.type;

public class AnyType implements MimeType {

    private final String prefix;
    private final String suffix;

    public AnyType(String prefix, String suffix) {
        this.prefix = prefix;
        this.suffix = suffix;
    }

    @Override
    public String getPrefix() {
        return prefix;
    }

    @Override
    public String getSuffix() {
        return suffix;
    }
}
