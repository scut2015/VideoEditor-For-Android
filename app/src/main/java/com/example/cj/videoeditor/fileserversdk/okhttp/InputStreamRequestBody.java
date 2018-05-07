package com.example.cj.videoeditor.fileserversdk.okhttp;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.internal.Util;
import okio.BufferedSink;
import okio.Okio;
import okio.Source;

import java.io.IOException;
import java.io.InputStream;

public class InputStreamRequestBody extends RequestBody {
    MediaType mediaType;
    InputStream inputStream;
    Long contentLength;

    /**
     * @param mediaType
     * @param inputStream
     * @param contentLength 当InputStream.available()返回所有大小时,可不传入;否则显式指定InputStream大小
     */
    public InputStreamRequestBody(MediaType mediaType, InputStream inputStream, Long contentLength) {
        this.mediaType = mediaType;
        this.inputStream = inputStream;
        this.contentLength = contentLength;
    }

    public InputStreamRequestBody(MediaType mediaType, InputStream inputStream) {
        this(mediaType, inputStream, null);
    }

    public MediaType contentType() {
        return mediaType;
    }

    @Override
    public long contentLength() throws IOException {
        return contentLength!=null?contentLength:inputStream.available();
    }

    public void writeTo(BufferedSink bufferedSink) throws IOException {
        Source source = null;
        try {
            source = Okio.source(inputStream);
            bufferedSink.writeAll(source);
        } finally {
            Util.closeQuietly(source);
        }
    }
}
