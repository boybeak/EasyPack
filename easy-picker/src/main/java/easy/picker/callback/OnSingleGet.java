package easy.picker.callback;

import android.net.Uri;


import androidx.annotation.NonNull;

import java.io.File;

public interface OnSingleGet extends OnGet<Uri, File> {
    @Override
    void onGet(@NonNull Uri uri, @NonNull File file);
}
