
package com.adhoc.http.entity.mime;

/**
 *
 * @since 4.0
 */
public enum MyHttpMultipartMode {

    /** RFC 822, RFC 2045, RFC 2046 compliant */
    STRICT,
    /** browser-compatible mode, i.e. only write Content-Disposition; use content charset */
    BROWSER_COMPATIBLE

}
