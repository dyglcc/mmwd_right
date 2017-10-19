
package com.adhoc.http.entity.mime.content;

import java.io.IOException;
import java.io.OutputStream;

/**
 *
 * @since 4.0
 */
public interface MyContentBody extends MyContentDescriptor {

    String getFilename();

    void writeTo(OutputStream out) throws IOException;

}
