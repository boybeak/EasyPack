package easy.safr;

import androidx.annotation.NonNull;

public interface OnResultAdapter extends OnResult {
    @Override
    default void onStart(@NonNull String id) {}

    @Override
    default void onCancel(@NonNull String id) {}

    @Override
    default void onThrow(@NonNull String id, Throwable t) {
        t.printStackTrace();
    }
}
