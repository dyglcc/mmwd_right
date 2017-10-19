
package com.adhoc.http.entity.mime;

import com.adhoc.http.entity.mime.content.MyContentBody;

/**
 * FormBodyPart class represents a content body that can be used as a part of multipart encoded
 * entities. This class automatically populates the header with standard fields based on
 * the content description of the enclosed body.
 *
 * @since 4.0
 */
public class MyFormBodyPart {

    private final String name;
    private final MyHeader header;

    private final MyContentBody body;

    public MyFormBodyPart(final String name, final MyContentBody body) {
        super();
        if (name == null) {
            throw new IllegalArgumentException("Name may not be null");
        }
        if (body == null) {
            throw new IllegalArgumentException("Body may not be null");
        }
        this.name = name;
        this.body = body;
        this.header = new MyHeader();

        generateContentDisp(body);
        generateContentType(body);
        generateTransferEncoding(body);
    }

    public String getName() {
        return this.name;
    }

    public MyContentBody getBody() {
        return this.body;
    }

    public MyHeader getHeader() {
        return this.header;
    }

    public void addField(final String name, final String value) {
        if (name == null) {
            throw new IllegalArgumentException("Field name may not be null");
        }
        this.header.addField(new My6MinimalField(name, value));
    }

    protected void generateContentDisp(final MyContentBody body) {
        StringBuilder buffer = new StringBuilder();
        buffer.append("form-data; name=\"");
        buffer.append(getName());
        buffer.append("\"");
        if (body.getFilename() != null) {
            buffer.append("; filename=\"");
            buffer.append(body.getFilename());
            buffer.append("\"");
        }
        addField(MyMIME.CONTENT_DISPOSITION, buffer.toString());
    }

    protected void generateContentType(final MyContentBody body) {
        StringBuilder buffer = new StringBuilder();
        buffer.append(body.getMimeType()); // MimeType cannot be null
        if (body.getCharset() != null) { // charset may legitimately be null
            buffer.append("; charset=");
            buffer.append(body.getCharset());
        }
        addField(MyMIME.CONTENT_TYPE, buffer.toString());
    }

    protected void generateTransferEncoding(final MyContentBody body) {
        addField(MyMIME.CONTENT_TRANSFER_ENC, body.getTransferEncoding()); // TE cannot be null
    }

}
