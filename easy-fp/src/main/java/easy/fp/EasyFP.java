package easy.fp;

import android.content.ComponentName;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.content.res.XmlResourceParser;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;

import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import org.xmlpull.v1.XmlPullParserException;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.xmlpull.v1.XmlPullParser.END_DOCUMENT;
import static org.xmlpull.v1.XmlPullParser.START_TAG;

public class EasyFP {

    private static final String TAG = EasyFP.class.getSimpleName();

    /**
     * This comes from FileProvider#META_DATA_FILE_PROVIDER_PATHS, ${@see FileProvider#META_DATA_FILE_PROVIDER_PATHS}
     */
    private static final String
            META_DATA_FILE_PROVIDER_PATHS = "android.support.FILE_PROVIDER_PATHS";

    private static final String TAG_ROOT_PATH = "root-path";
    private static final String TAG_FILES_PATH = "files-path";
    private static final String TAG_CACHE_PATH = "cache-path";
    private static final String TAG_EXTERNAL = "external-path";
    private static final String TAG_EXTERNAL_FILES = "external-files-path";
    private static final String TAG_EXTERNAL_CACHE = "external-cache-path";
    private static final String TAG_EXTERNAL_MEDIA = "external-media-path";

    private static final File DEVICE_ROOT = new File("/");

    private static final String ATTR_NAME = "name";
    private static final String ATTR_PATH = "path";

    private static final Map<String, PathStrategy> sCache = new HashMap<>();

    public static File guess(Context context, Uri uri) {
        return guess(context, uri, false);
    }
    public static File guess(Context context, Uri uri, boolean strictMode) {
        return obtainPathStrategy(context, uri.getAuthority(), strictMode).getFileForUri(uri);
    }

    private static PathStrategy obtainPathStrategy(Context context, String authority, boolean strictMode) {
        PathStrategy strategy = sCache.get(authority);
        if (strategy != null) {
            strategy.setStrictMode(strictMode);
            return strategy;
        }

        PackageManager pm = context.getPackageManager();
        ProviderInfo providerInfo = pm.resolveContentProvider(authority, PackageManager.GET_META_DATA);

        final SimplePathStrategy simplePS = new SimplePathStrategy(authority, strictMode);
        XmlResourceParser in = providerInfo.loadXmlMetaData(pm, META_DATA_FILE_PROVIDER_PATHS);

        if (in == null) {
            throw new IllegalArgumentException(
                    "Missing " + META_DATA_FILE_PROVIDER_PATHS + " meta-data");
        }

        int type;
        try {
            while ((type = in.next()) != END_DOCUMENT) {
                if (type == START_TAG) {
                    final String tag = in.getName();

                    final String name = in.getAttributeValue(null, ATTR_NAME);
                    String path = in.getAttributeValue(null, ATTR_PATH);

                    File target = null;
                    if (TAG_ROOT_PATH.equals(tag)) {
                        target = DEVICE_ROOT;
                    } else if (TAG_FILES_PATH.equals(tag)) {
                        target = context.getFilesDir();
                    } else if (TAG_CACHE_PATH.equals(tag)) {
                        target = context.getCacheDir();
                    } else if (TAG_EXTERNAL.equals(tag)) {
                        target = Environment.getExternalStorageDirectory();
                    } else if (TAG_EXTERNAL_FILES.equals(tag)) {
                        File[] externalFilesDirs = ContextCompat.getExternalFilesDirs(context, null);
                        if (externalFilesDirs.length > 0) {
                            target = externalFilesDirs[0];
                        }
                    } else if (TAG_EXTERNAL_CACHE.equals(tag)) {
                        File[] externalCacheDirs = ContextCompat.getExternalCacheDirs(context);
                        if (externalCacheDirs.length > 0) {
                            target = externalCacheDirs[0];
                        }
                    } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP
                            && TAG_EXTERNAL_MEDIA.equals(tag)) {
                        File[] externalMediaDirs = context.getExternalMediaDirs();
                        if (externalMediaDirs.length > 0) {
                            target = externalMediaDirs[0];
                        }
                    }

                    if (target != null) {
                        simplePS.addRoot(tag, name, buildPath(target, path));
                    }
                }
            }
        } catch (IOException | XmlPullParserException e) {
            throw new IllegalArgumentException(
                    "Failed to parse " + META_DATA_FILE_PROVIDER_PATHS + " meta-data", e);
        }
        sCache.put(authority, simplePS);

        return simplePS;
    }

    private static File buildPath(File base, String... segments) {
        File cur = base;
        for (String segment : segments) {
            if (segment != null) {
                cur = new File(cur, segment);
            }
        }
        return cur;
    }

    public static EasyFP withDefault(Context context) {
        return withDefault(context, false);
    }
    public static EasyFP withDefault(Context context, boolean strictMode) {
        PackageManager pm = context.getPackageManager();
        ProviderInfo providerInfo = null;
        try {
            providerInfo = pm.getProviderInfo(new ComponentName(context, FileProvider.class), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        if (providerInfo == null) {
            throw new IllegalStateException("ProviderInfo not found.");
        }
        return withAuthority(context, providerInfo.authority, strictMode);
    }

    public static EasyFP withAuthority(Context context, String authority) {
        return withAuthority(context, authority, false);
    }
    public static EasyFP withAuthority(Context context, String authority, boolean strictMode) {
        return new EasyFP(context, authority, strictMode);
    }

    private final PathStrategy mStrategy;

    private File mRoot;
    private File mDirs;
    private File mFile;

    private EasyFP(Context context, String authority, boolean strictMode){
        this(obtainPathStrategy(context, authority, strictMode));
    }

    private EasyFP(PathStrategy strategy) {
        mStrategy = strategy;
    }

    public EasyFP strictMode(boolean strictMode) {
        mStrategy.setStrictMode(strictMode);
        return this;
    }

    public EasyFP filesDir() {
        setRoot(TAG_FILES_PATH);
        return this;
    }
    public EasyFP cacheDir() {
        setRoot(TAG_CACHE_PATH);
        return this;
    }
    public EasyFP externalStorageDir() {
        setRoot(TAG_EXTERNAL);
        return this;
    }
    public EasyFP externalCacheDir() {
        setRoot(TAG_EXTERNAL_CACHE);
        return this;
    }
    public EasyFP externalFilesDir() {
        setRoot(TAG_EXTERNAL_FILES);
        return this;
    }
    public EasyFP externalMediaDir() {
        setRoot(TAG_EXTERNAL_MEDIA);
        return this;
    }

    public EasyFP availableCacheDir() {
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            return externalCacheDir();
        } else {
            return cacheDir();
        }
    }

    public EasyFP availableFilesDir() {
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            return externalFilesDir();
        } else {
            return filesDir();
        }
    }

    private void setRoot(String type) {
        this.mRoot = mStrategy.getFileByType(type);
        mDirs = mRoot;
        mFile = null;
    }

    public EasyFP mkdirs(String ... subDirs) {
        if (mRoot == null) {
            throw new IllegalStateException("You must call filesDir, cacheDir, externalStorageDir ... and so on before call this method");
        }
        if (subDirs != null || subDirs.length > 0) {
            StringBuilder pathSB = new StringBuilder();
            for(String s : subDirs) {
                pathSB.append(s).append(File.separatorChar);
            }
            mDirs = new File(mRoot, pathSB.toString());
        }
        mDirs.mkdirs();
        return this;
    }

    public EasyFP name(String name) {
        if (mRoot == null) {
            throw new IllegalStateException("You must call filesDir, cacheDir, externalStorageDir ... and so on before call this method");
        }
        mFile = new File(mDirs, name);
        return this;
    }

    public File file() {
        if (mFile == null) {
            throw new IllegalStateException("Set a name use name(String name) method before use this file.");
        }
        return mFile;
    }

    public Uri uri() {
        if (mFile == null) {
            throw new IllegalStateException("Set a name use name(String name) method before use this uri.");
        }
        return mStrategy.getUriForFile(mFile);
    }

    public Pair pair() {
        return new Pair(uri(), file());
    }

    public boolean isWorkingWithFP() {
        return !mStrategy.hasConflictItems();
    }

    public static final class Pair {

        private final Uri uri;
        private final File file;
        private Pair(Uri uri, File file) {
            this.uri = uri;
            this.file = file;
        }

        public Uri uri() {
            return uri;
        }

        public File file() {
            return file;
        }

    }

}
