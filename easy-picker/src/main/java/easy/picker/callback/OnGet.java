package easy.picker.callback;

import androidx.annotation.NonNull;

public interface OnGet<F, S> {
    default void onStart() {}
    void onGet(@NonNull F first, @NonNull S second);
    default void onCancel() {}
    default void onThrow(Throwable e) {
        e.printStackTrace();
    }
}
