package com.android.volley.toolbox;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;

import org.apache.http.HttpResponse;

import java.io.IOException;
import java.util.Map;

/**
 * HTTP栈接口
 * 作者：李富 on 2015/10/20.
 * 邮箱：lifuzz@163.com
 */
public interface HttpStack {

    /**
     *
     *用给定的参数执行HTTP请求
     *
     * 如果request.getPostBody() == null发送GET请求，否则发送POST请求并设置Content-Type header为
     * request.getPostBodyContentType()
     *
     * @param request 需要执行的请求
     * @param additionalHeaders 附加头（和请求头一起发送）
     * @return HTTP响应
     */

    @SuppressWarnings("deprecation")
    public HttpResponse performRequest(Request<?> request,Map<String,String> additionalHeaders)
        throws IOException,AuthFailureError;

}
