package com.android.volley;

/**
 * 异常的样式类，封装了volley的错误
 *
 * 作者：李富 on 2015/10/20.
 * 邮箱：lifuzz@163.com
 */
public class VolleyError extends Exception {

    public final NetworkResponse networkResponse;

    public VolleyError() {
        networkResponse = null;
    }

    public VolleyError(NetworkResponse response) {
        networkResponse = response;
    }

    public VolleyError(String exceptionMessage) {
        super(exceptionMessage);
        networkResponse = null;
    }

    public VolleyError(String exceptionMessage,Throwable reason) {
        super(exceptionMessage,reason);
        networkResponse = null;
    }

    public VolleyError(Throwable cause) {
        super(cause);
        networkResponse = null;
    }



}
