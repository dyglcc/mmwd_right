package qfpay.wxshop.image.processer.cache;

import java.io.File;

/**
 * An interface for writing to and reading from a disk cache.
 */
public interface DiskCache {
    /**
     * An interface to actually write data to a key in the disk cache.
     */
    interface Writer {
        /**
         * Writes data to the file and returns true if the write was successful and should be committed, and
         * false if the write should be aborted.
         *
         * @param file The File the Writer should write to.
         */
        boolean write(File file);
    }

    /**
     * Get the cache for the value at the given key.
     *
     * <p>
     *     Note - This is potentially dangerous, someone may write a new value to the file at any point in timeand we
     *     won't know about it.
     * </p>
     *
     * @param key The key in the cache.
     * @return An InputStream representing the data at key at the time get is called.
     */
    File get(String safeKey);

    /**
     * Write to a key in the cache. {@link qfpay.wxshop.image.processer.cache.DiskCache.Writer} is used so that the cache implementation can perform actions after
     * the write finishes, like commit (via atomic file rename).
     *
     * @param key The key to write to.
     * @param writer An interface that will write data given an OutputStream for the key.
     */
    void put(String safeKey, Writer writer);

    /**
     * Remove the key and value from the cache.
     *
     * @param key The key to remove.
     */
    void delete(String safeKey);
}
