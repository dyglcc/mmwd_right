
package com.adhoc.http.entity.mime;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import com.adhoc.http.entity.mime.content.MyContentBody;
import org.apache.http.util.ByteArrayBuffer;

public class MyHttpMultipart {

    private static ByteArrayBuffer encode(
            final Charset charset, final String string) {
        ByteBuffer encoded = charset.encode(CharBuffer.wrap(string));
        ByteArrayBuffer bab = new ByteArrayBuffer(encoded.remaining());
        bab.append(encoded.array(), encoded.position(), encoded.remaining());
        return bab;
    }

    private static void writeBytes(
            final ByteArrayBuffer b, final OutputStream out) throws IOException {
        out.write(b.buffer(), 0, b.length());
    }

    private static void writeBytes(
            final String s, final Charset charset, final OutputStream out) throws IOException {
        ByteArrayBuffer b = encode(charset, s);
        writeBytes(b, out);
    }

    private static void writeBytes(
            final String s, final OutputStream out) throws IOException {
        ByteArrayBuffer b = encode(MyMIME.DEFAULT_CHARSET, s);
        writeBytes(b, out);
    }

    private static void writeField(
            final My6MinimalField field, final OutputStream out) throws IOException {
        writeBytes(field.getName(), out);
        writeBytes(FIELD_SEP, out);
        writeBytes(field.getBody(), out);
        writeBytes(CR_LF, out);
    }

    private static void writeField(
            final My6MinimalField field, final Charset charset, final OutputStream out) throws IOException {
        writeBytes(field.getName(), charset, out);
        writeBytes(FIELD_SEP, out);
        writeBytes(field.getBody(), charset, out);
        writeBytes(CR_LF, out);
    }

    private static final ByteArrayBuffer FIELD_SEP = encode(MyMIME.DEFAULT_CHARSET, ": ");
    private static final ByteArrayBuffer CR_LF = encode(MyMIME.DEFAULT_CHARSET, "\r\n");
    private static final ByteArrayBuffer TWO_DASHES = encode(MyMIME.DEFAULT_CHARSET, "--");


    private final String subType;
    private final Charset charset;
    private final String boundary;
    private final List<MyFormBodyPart> parts;

    private final MyHttpMultipartMode mode;

    /**
     * Creates an instance with the specified settings.
     *
     * @param subType mime subtype - must not be {@code null}
     * @param charset the character set to use. May be {@code null}, in which case {@link MyMIME#DEFAULT_CHARSET} - i.e. US-ASCII - is used.
     * @param boundary to use  - must not be {@code null}
     * @param mode the mode to use
     * @throws IllegalArgumentException if charset is null or boundary is null
     */
    public MyHttpMultipart(final String subType, final Charset charset, final String boundary, MyHttpMultipartMode mode) {
        super();
        if (subType == null) {
            throw new IllegalArgumentException("Multipart subtype may not be null");
        }
        if (boundary == null) {
            throw new IllegalArgumentException("Multipart boundary may not be null");
        }
        this.subType = subType;
        this.charset = charset != null ? charset : MyMIME.DEFAULT_CHARSET;
        this.boundary = boundary;
        this.parts = new ArrayList<MyFormBodyPart>();
        this.mode = mode;
    }

    /**
     * Creates an instance with the specified settings.
     * Mode is set to {@link MyHttpMultipartMode#STRICT}
     *
     * @param subType mime subtype - must not be {@code null}
     * @param charset the character set to use. May be {@code null}, in which case {@link MyMIME#DEFAULT_CHARSET} - i.e. US-ASCII - is used.
     * @param boundary to use  - must not be {@code null}
     * @throws IllegalArgumentException if charset is null or boundary is null
     */
    public MyHttpMultipart(final String subType, final Charset charset, final String boundary) {
        this(subType, charset, boundary, MyHttpMultipartMode.STRICT);
    }

    public MyHttpMultipart(final String subType, final String boundary) {
        this(subType, null, boundary);
    }

    public String getSubType() {
        return this.subType;
    }

    public Charset getCharset() {
        return this.charset;
    }

    public MyHttpMultipartMode getMode() {
        return this.mode;
    }

    public List<MyFormBodyPart> getBodyParts() {
        return this.parts;
    }

    public void addBodyPart(final MyFormBodyPart part) {
        if (part == null) {
            return;
        }
        this.parts.add(part);
    }

    public String getBoundary() {
        return this.boundary;
    }

    private void doWriteTo(
        final MyHttpMultipartMode mode,
        final OutputStream out,
        boolean writeContent) throws IOException {

        ByteArrayBuffer boundary = encode(this.charset, getBoundary());
        for (MyFormBodyPart part: this.parts) {
            writeBytes(TWO_DASHES, out);
            writeBytes(boundary, out);
            writeBytes(CR_LF, out);

            MyHeader header = part.getHeader();

            switch (mode) {
            case STRICT:
                for (My6MinimalField field: header) {
                    writeField(field, out);
                }
                break;
            case BROWSER_COMPATIBLE:
                // Only write Content-Disposition
                // Use content charset
                My6MinimalField cd = part.getHeader().getField(MyMIME.CONTENT_DISPOSITION);
                writeField(cd, this.charset, out);
                String filename = part.getBody().getFilename();
                if (filename != null) {
                    My6MinimalField ct = part.getHeader().getField(MyMIME.CONTENT_TYPE);
                    writeField(ct, this.charset, out);
                }
                break;
            }
            writeBytes(CR_LF, out);

            if (writeContent) {
                part.getBody().writeTo(out);
            }
            writeBytes(CR_LF, out);
        }
        writeBytes(TWO_DASHES, out);
        writeBytes(boundary, out);
        writeBytes(TWO_DASHES, out);
        writeBytes(CR_LF, out);
    }

    /**
     * Writes out the content in the multipart/form encoding. This method
     * produces slightly different formatting depending on its compatibility
     * mode.
     *
     * @see #getMode()
     */
    public void writeTo(final OutputStream out) throws IOException {
        doWriteTo(this.mode, out, true);
    }

    /**
     * Determines the total length of the multipart content (content length of
     * individual parts plus that of extra elements required to delimit the parts
     * from one another). If any of the @{link BodyPart}s contained in this object
     * is of a streaming entity of unknown length the total length is also unknown.
     * <p/>
     * This method buffers only a small amount of data in order to determine the
     * total length of the entire entity. The content of individual parts is not
     * buffered.
     *
     * @return total length of the multipart entity if known, <code>-1</code>
     *   otherwise.
     */
    public long getTotalLength() {
        long contentLen = 0;
        for (MyFormBodyPart part: this.parts) {
            MyContentBody body = part.getBody();
            long len = body.getContentLength();
            if (len >= 0) {
                contentLen += len;
            } else {
                return -1;
            }
        }
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            doWriteTo(this.mode, out, false);
            byte[] extra = out.toByteArray();
            return contentLen + extra.length;
        } catch (IOException ex) {
            // Should never happen
            return -1;
        }
    }

}
