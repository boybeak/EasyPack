package easy.picker.utils;

import android.content.Context;
import android.net.Uri;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

import easy.picker.EasyPicker;

public class CopyUtils {
    public static File copySync(Context context, Uri uri) throws IOException {
        InputStream is = context.getContentResolver().openInputStream(uri);
        File toFile = new File(EasyPicker.getConfig().getCopyDir(), UUID.randomUUID().toString() + guessSuffix(uri));
        if (!toFile.getParentFile().exists()) {
            toFile.getParentFile().mkdirs();
        }
        FileOutputStream fos = new FileOutputStream(toFile);
        byte[] bytes = new byte[4096];
        int len = 0;
        while ((len = is.read(bytes)) > 0) {
            fos.write(bytes, 0, len);
        }
        fos.flush();
        fos.close();
        is.close();
        return toFile;
    }

    private static String guessSuffix(Uri uri) {
        String path = uri.getPath();
        int index = path.lastIndexOf('.');
        if (index > 0 && index < path.length()) {
            return path.substring(index);
        }
        return "";
    }
}
