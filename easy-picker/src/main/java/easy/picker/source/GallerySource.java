package easy.picker.source;

import android.content.Context;
import android.content.Intent;

import easy.picker.type.MimeType;
import easy.picker.callback.MayCopyMultipleGet;
import easy.picker.callback.MayCopySingleGet;
import easy.picker.callback.OnMultipleGet;
import easy.picker.callback.OnSingleGet;
import easy.safr.EasySAFR;

public class GallerySource implements Source {

    private final Intent it = new Intent(Intent.ACTION_GET_CONTENT);

    public GallerySource(MimeType mimeType) {
        it.setType(mimeType.create());
    }

    public void singleGet(Context context, OnSingleGet singleGet) {
        EasySAFR.startActivityForResult(context, it, new MayCopySingleGet(context, singleGet));
    }

    public void multipleGet(Context context, OnMultipleGet multipleGet) {
        it.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        EasySAFR.startActivityForResult(context, it, new MayCopyMultipleGet(context, multipleGet));
    }

}
