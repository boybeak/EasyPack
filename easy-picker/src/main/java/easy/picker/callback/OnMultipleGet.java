package easy.picker.callback;

import android.net.Uri;

import androidx.annotation.NonNull;

import java.io.File;

public interface OnMultipleGet extends OnGet<Uri[], File[]> {
    @Override
    void onGet(@NonNull Uri[] uris, @NonNull File[] files);
}
