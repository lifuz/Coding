package com.android.volley;

/**
 * 作者：李富 on 2015/11/4.
 * 邮箱：lifuzz@163.com
 */
public class NetworkError extends VolleyError {

    public NetworkError() {
        super();
    }

    public NetworkError(Throwable cause) {
        super(cause);
    }

    public NetworkError(NetworkResponse networkResponse) {
        super(networkResponse);
    }
}
