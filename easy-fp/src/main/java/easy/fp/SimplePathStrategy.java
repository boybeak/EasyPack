package easy.fp;

import android.net.Uri;
import android.text.TextUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Strategy that provides access to files living under a narrow whitelist of
 * filesystem roots. It will throw {@link SecurityException} if callers try
 * accessing files outside the configured roots.
 * <p>
 * For example, if configured with
 * {@code addRoot("myfiles", context.getFilesDir())}, then
 * {@code context.getFileStreamPath("foo.txt")} would map to
 * {@code content://myauthority/myfiles/foo.txt}.
 */
class SimplePathStrategy implements PathStrategy {

    private static final String TAG = SimplePathStrategy.class.getSimpleName();

    private final String mAuthority;
    private final HashMap<TypedName, File> mRoots = new HashMap<TypedName, File>();

    private boolean isStrictMode = false;

    SimplePathStrategy(String authority, boolean strictMode) {
        mAuthority = authority;
        isStrictMode = strictMode;
    }

    /**
     * Add a mapping from a name to a filesystem root. The provider only offers
     * access to files that live under configured roots.
     */
    void addRoot(String type, String name, File root) {
        if (TextUtils.isEmpty(name)) {
            throw new IllegalArgumentException("Name must not be empty");
        }

        try {
            // Resolve to canonical path to keep path checking fast
            root = root.getCanonicalFile();
        } catch (IOException e) {
            throw new IllegalArgumentException(
                    "Failed to resolve canonical path for " + root, e);
        }
        TypedName tn = new TypedName(type, name);
        if (isStrictMode && (mRoots.containsKey(tn) || mRoots.containsValue(root))) {
            throw new IllegalStateException("The path item (" + name + " - " + root.getPath()
                    + ") is conflict with other path item.");
        }
        mRoots.put(tn, root);
    }

    @Override
    public Uri getUriForFile(File file) {
        String path;
        try {
            path = file.getCanonicalPath();
        } catch (IOException e) {
            throw new IllegalArgumentException("Failed to resolve canonical path for " + file);
        }

        // Find the most-specific root path
        Map.Entry<TypedName, File> mostSpecific = null;
        for (Map.Entry<TypedName, File> root : mRoots.entrySet()) {
            final String rootPath = root.getValue().getPath();
            if (path.startsWith(rootPath) && (mostSpecific == null
                    || rootPath.length() > mostSpecific.getValue().getPath().length())) {
                mostSpecific = root;
            }
        }

        if (mostSpecific == null) {
            throw new IllegalArgumentException(
                    "Failed to find configured root that contains " + path);
        }

        // Start at first char of path under root
        final String rootPath = mostSpecific.getValue().getPath();
        if (rootPath.endsWith("/")) {
            path = path.substring(rootPath.length());
        } else {
            path = path.substring(rootPath.length() + 1);
        }

        // Encode the tag and path separately
        path = Uri.encode(mostSpecific.getKey().name) + '/' + Uri.encode(path, "/");
        return new Uri.Builder().scheme("content")
                .authority(mAuthority).encodedPath(path).build();
    }

    @Override
    public File getFileForUri(Uri uri) {
        String path = uri.getEncodedPath();

        final int splitIndex = path.indexOf('/', 1);
        final String tag = Uri.decode(path.substring(1, splitIndex));
        path = Uri.decode(path.substring(splitIndex + 1));

        TypedName tn = getTypedNameByName(tag);
        if (tn == null) {
            throw new IllegalStateException("It seems that you didn't set " + tn.type +
                    " attribute in R.xml.{your_paths} for authority=" + mAuthority);
        }
        final File root = mRoots.get(tn);
        if (root == null) {
            throw new IllegalArgumentException("Unable to find configured root for " + uri);
        }

        File file = new File(root, path);
        try {
            file = file.getCanonicalFile();
        } catch (IOException e) {
            throw new IllegalArgumentException("Failed to resolve canonical path for " + file);
        }

        if (!file.getPath().startsWith(root.getPath())) {
            throw new SecurityException("Resolved path jumped beyond configured root");
        }

        return file;
    }

    @Override
    public boolean hasConflictItems() {
        List<TypedName> tns = new ArrayList<>();
        List<File> files = new ArrayList<>();
        for (TypedName tn : mRoots.keySet()) {
            if (tns.contains(tn)) {
                return true;
            }
            tns.add(tn);
            File f = mRoots.get(tn);
            if (files.contains(f)) {
                return true;
            }
            files.add(f);
        }
        tns.clear();
        files.clear();
        return false;
    }

    @Override
    public boolean isStrictMode() {
        return isStrictMode;
    }

    @Override
    public void setStrictMode(boolean strictMode) {
        if (isStrictMode == strictMode) {
            return;
        }
        this.isStrictMode = strictMode;
        if (isStrictMode && hasConflictItems()) {
            throw new IllegalStateException("This FileProvider with authority(" + mAuthority +
                    ") can not work with EasyFP, because of containing conflict repeat path items. " +
                    "You must make sure both name and path are not the same with other items.");
        }
    }

    @Override
    public File getFileByType(String type) {
        TypedName key = getTypedNameByType(type);
        if (key == null) {
            throw new IllegalStateException("It seems that you didn't set " + type +
                    " attribute in R.xml.{your_paths} for authority=" + mAuthority);
        }
        return mRoots.get(key);
    }
    private TypedName getTypedNameByType(String type) {
        TypedName key = null;
        for (TypedName tn : mRoots.keySet()) {
            if (tn.type.equals(type)) {
                key = tn;
            }
        }
        return key;
    }
    private TypedName getTypedNameByName(String name) {
        TypedName key = null;
        for (TypedName tn : mRoots.keySet()) {
            if (tn.name.equals(name)) {
                key = tn;
            }
        }
        return key;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (TypedName key : mRoots.keySet()) {
            sb.append(key.toString()).append(" - ").append(mRoots.get(key)).append('\n');
        }
        sb.deleteCharAt(sb.length() - 1);
        return "SimplePathStrategy{" +
                "mAuthority='" + mAuthority + '\'' +
                ", mRoots=(" + sb.toString() + ")" +
                '}';
    }

    private static class TypedName {
        private final String type;
        private final String name;

        public TypedName(String type, String name) {
            this.type = type;
            this.name = name;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            TypedName typedName = (TypedName) o;
            return name.equals(typedName.name);
        }

        @Override
        public int hashCode() {
            return Objects.hash(name);
        }

        @Override
        public String toString() {
            return "(" +
                    "type='" + type + '\'' +
                    ", name='" + name + '\'' +
                    ')';
        }
    }
}