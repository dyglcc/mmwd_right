package com.adhoc.volley.toolbox;

import com.adhoc.utils.T;
import com.adhoc.volley.AuthFailureError;
import com.adhoc.volley.NetworkResponse;
import com.adhoc.volley.Request;
import com.adhoc.volley.Response;
import com.adhoc.volley.VolleyLog;

import com.adhoc.http.entity.mime.MyMultipartEntity;
import com.adhoc.http.entity.mime.content.FileBodyMy;
import com.adhoc.http.entity.mime.content.StringBodyMy;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class MultipartRequest extends Request<String> {

    private MyMultipartEntity entity = new MyMultipartEntity();

    private static final String FILE_PART_NAME = "file";
    private static final String STRING_PART_NAME = "text";

    private final Response.Listener<String> mListener;
    private File mFilePart;
    private String mStringPart;

    public MultipartRequest(String url, Response.ErrorListener errorListener,
                            Response.Listener<String> listener, File file,
                            String stringPart) {
        super(Method.POST, url, errorListener);

        mListener = listener;
        mFilePart = file;
        mStringPart = stringPart;
        entity.addPart(FILE_PART_NAME, new FileBodyMy(file));
        try {
            entity.addPart(STRING_PART_NAME, new StringBodyMy(mStringPart));
        } catch (UnsupportedEncodingException e) {
            T.e(e);
        }
    }

    public MultipartRequest(String url, Response.ErrorListener errorListener,
                            Response.Listener<String> listener, HashMap<String,
            File> files, HashMap<String, String> stringParts) {
        super(Method.POST, url, errorListener);
        mListener = listener;
        buildMultipartEntity(files, stringParts);
    }

    private void buildMultipartEntity(HashMap<String, File> files, HashMap<String, String> strings) {

        for (Map.Entry<String, File> entry : files.entrySet()) {

            entity.addPart(entry.getKey(), new FileBodyMy(entry.getValue()));
        }


        for (Map.Entry<String, String> entryString : strings.entrySet()) {

            try {
                entity.addPart(entryString.getKey(), new StringBodyMy(entryString.getValue()));
            } catch (UnsupportedEncodingException e) {
                T.e(e);
            }
        }
    }

    @Override
    public String getBodyContentType() {
        return entity.getContentType().getValue();
    }

    @Override
    public byte[] getBody() throws AuthFailureError {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            entity.writeTo(bos);
        } catch (IOException e) {
            VolleyLog.e("IOException writing to ByteArrayOutputStream");
        }
        return bos.toByteArray();
    }

    @Override
    protected Response<String> parseNetworkResponse(NetworkResponse response) {
        return Response.success("Uploaded", getCacheEntry());
    }

    @Override
    protected void deliverResponse(String response) {
        mListener.onResponse(response);
    }
}