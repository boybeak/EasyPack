package easy.safr;

import android.content.Intent;

import androidx.annotation.NonNull;

public interface OnResult {
    void onStart(@NonNull String id);
    void onOk(@NonNull String id, int requestCode, Intent data);
    void onCancel(@NonNull String id);
    void onThrow(@NonNull String id, Throwable t);
}
