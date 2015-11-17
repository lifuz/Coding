package com.android.volley;

import org.apache.http.HttpStatus;

import java.util.Collections;
import java.util.Map;

/**
 * 从网络访问返回的数据和报头
 * <p/>
 * 作者：李富 on 2015/10/20.
 * 邮箱：lifuzz@163.com
 */
public class NetworkResponse {

    /**
     * http状态码
     */
    public final int statusCode;

    /**
     * 这个响应的原始数据
     */
    public final byte[] data;

    /**
     * 响应头
     */
    public final Map<String, String> headers;

    /**
     * 如果服务器返回一个304，则这个值为true(表示为未修改)
     */
    public final boolean notModified;

    /**
     * 创建一个新的网络响应
     * @param statusCode HTTP状态码
     * @param data 响应体
     * @param headers 响应头或者没有为空
     * @param notModified 如果服务器返回一个304并且已经缓存到本地，则这个值为true
     */
    public NetworkResponse(int statusCode, byte[] data, Map<String, String> headers,
                           boolean notModified) {

        this.statusCode = statusCode;
        this.data = data;
        this.headers = headers;
        this.notModified = notModified;

    }

    public NetworkResponse(byte[] data){
        this(HttpStatus.SC_OK,data, Collections.<String,String>emptyMap(),false);
    }

    public NetworkResponse(byte[] data,Map<String,String> headers) {
        this(HttpStatus.SC_OK,data,headers,false);
    }

}
