
package com.adhoc.http.entity.mime.content;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.adhoc.http.entity.mime.MyMIME;

/**
 *
 * @since 4.0
 */
public class FileBodyMy extends MyAbstractMyContentBody {

    private final File file;
    private final String filename;
    private final String charset;

    /**
     * @since 4.1
     */
    public FileBodyMy(final File file,
                      final String filename,
                      final String mimeType,
                      final String charset) {
        super(mimeType);
        if (file == null) {
            throw new IllegalArgumentException("File may not be null");
        }
        this.file = file;
        if (filename != null)
            this.filename = filename;
        else
            this.filename = file.getName();
        this.charset = charset;
    }

    /**
     * @since 4.1
     */
    public FileBodyMy(final File file,
                      final String mimeType,
                      final String charset) {
        this(file, null, mimeType, charset);
    }

    public FileBodyMy(final File file, final String mimeType) {
        this(file, mimeType, null);
    }

    public FileBodyMy(final File file) {
        this(file, "application/octet-stream");
    }

    public InputStream getInputStream() throws IOException {
        return new FileInputStream(this.file);
    }

    public void writeTo(final OutputStream out) throws IOException {
        if (out == null) {
            throw new IllegalArgumentException("Output stream may not be null");
        }
        InputStream in = new FileInputStream(this.file);
        try {
            byte[] tmp = new byte[4096];
            int l;
            while ((l = in.read(tmp)) != -1) {
                out.write(tmp, 0, l);
            }
            out.flush();
        } finally {
            in.close();
        }
    }

    public String getTransferEncoding() {
        return MyMIME.ENC_BINARY;
    }

    public String getCharset() {
        return charset;
    }

    public long getContentLength() {
        return this.file.length();
    }

    public String getFilename() {
        return filename;
    }

    public File getFile() {
        return this.file;
    }

}
