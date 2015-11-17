package com.android.volley.toolbox;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpUriRequest;

import java.io.IOException;
import java.util.Map;

/**
 * 作者：李富 on 2015/10/21.
 * 邮箱：lifuzz@163.com
 */

@SuppressWarnings("deprecation")
public class HttpClientStack implements HttpStack {

    protected HttpClient mClient;

    public HttpClientStack(HttpClient client) {

        mClient = client;
    }



    @Override
    public HttpResponse performRequest(Request<?> request, Map<String, String> additionalHeaders)
            throws IOException, AuthFailureError {

        HttpUriRequest httpRequest  = createHttpRequest(request,additionalHeaders);

        return null;
    }

    static HttpUriRequest createHttpRequest(Request<?> request,Map<String,String> additionalHeaders)
        throws AuthFailureError{

//        switch (request)
        return null;
    }
}
