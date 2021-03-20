package easy.picker.type;

public interface MimeType {
    String getPrefix();
    String getSuffix();
    default String create() {
        return getPrefix() + "/" + getSuffix();
    }
}
