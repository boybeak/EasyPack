package easy.picker.callback;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Pair;

import java.io.File;

import easy.picker.utils.UriResolver;
import easy.picker.utils.CopyUtils;

public class MayCopySingleGet extends ContextOnResultWrapper<Uri, File> {

    public MayCopySingleGet(Context context, OnGet<Uri, File> onGet) {
        super(context, onGet);
    }

    @Override
    public Pair<Uri, File> convertAsync(Intent data) throws Exception {
        Pair<Uri, File> pair = null;
        Uri uri = data.getData();
        String path = UriResolver.getPath(contextUnsafe(), uri);
        if (path != null) {
            pair = new Pair<>(uri, new File(path));
        } else {
            pair = new Pair<>(uri, CopyUtils.copySync(contextUnsafe(), uri));
        }

        release();
        return pair;
    }
}