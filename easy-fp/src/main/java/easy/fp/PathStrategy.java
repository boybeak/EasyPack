package easy.fp;

import android.net.Uri;

import java.io.File;

/**
 * Strategy for mapping between {@link File} and {@link Uri}.
 * <p>
 * Strategies must be symmetric so that mapping a {@link File} to a
 * {@link Uri} and then back to a {@link File} points at the original
 * target.
 * <p>
 * Strategies must remain consistent across app launches, and not rely on
 * dynamic state. This ensures that any generated {@link Uri} can still be
 * resolved if your process is killed and later restarted.
 *
 * @see SimplePathStrategy
 */
interface PathStrategy {
    /**
     * Return a {@link Uri} that represents the given {@link File}.
     */
    Uri getUriForFile(File file);

    /**
     * Return a {@link File} that represents the given {@link Uri}.
     */
    File getFileForUri(Uri uri);

    File getFileByType(String type);

    boolean hasConflictItems();
    boolean isStrictMode();
    void setStrictMode(boolean strictMode);
}