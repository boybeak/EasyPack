package easy.picker.callback;

import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Pair;

import java.io.File;
import java.io.IOException;

import easy.picker.utils.UriResolver;
import easy.picker.utils.CopyUtils;

public class MayCopyMultipleGet extends ContextOnResultWrapper<Uri[], File[]> {

    public MayCopyMultipleGet(Context context, OnGet<Uri[], File[]> onGet) {
        super(context, onGet);
    }

    @Override
    public Pair<Uri[], File[]> convertAsync(Intent data) throws IOException {

        Uri[] uris = null;

        Uri uri = data.getData();
        if (uri != null) {
            uris = new Uri[1];
            uris[0] = uri;
        } else {
            ClipData clipData = data.getClipData();
            if (clipData != null) {
                uris = new Uri[clipData.getItemCount()];
                for (int i = 0; i < clipData.getItemCount(); i++) {
                    uris[i] = clipData.getItemAt(i).getUri();
                }
            }
        }
        if (uris == null) {
            throw new IllegalStateException("Can not find uris");
        }
        File[] files = new File[uris.length];
        for (int i = 0; i < uris.length; i++) {
            Uri u = uris[i];
            String path = UriResolver.getPath(contextUnsafe(), u);
            if (path == null) {
                files[i] = CopyUtils.copySync(contextUnsafe(), u);
            } else {
                files[i] = new File(path);
            }
        }
        return new Pair<>(uris, files);
    }
}