package qfpay.wxshop.image.net;

import java.io.File;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.apache.http.entity.mime.content.FileBody;

import qfpay.wxshop.image.uploader.ImageAsyncUploader;

public class CountingFileRequestEntity extends FileBody {
    private ImageAsyncUploader mUploader;
    
    public CountingFileRequestEntity(File file, ImageAsyncUploader uploader) {
    	super(file);
		this.mUploader = uploader;
	}
    
    @Override
    public void writeTo(final OutputStream out) throws IOException {
        super.writeTo(out instanceof CountingOutputStream ? out : new CountingOutputStream(out));
    }
    
    class CountingOutputStream extends FilterOutputStream {
    	private int size;
    	
        CountingOutputStream(final OutputStream out) {
            super(out);
            size = 0;
        }

        @Override
        public void write(final byte[] b, final int off, final int len) throws IOException {
            out.write(b, off, len);
            size += len;
            mUploader.onLoading(size);
        }

        @Override
        public void write(final int b) throws IOException {
            out.write(b);
            size += 1;
            mUploader.onLoading(size);
        }
    }
}
