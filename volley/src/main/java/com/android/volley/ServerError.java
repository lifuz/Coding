package com.android.volley;

/**
 *
 * 与错误响应一起返回的错误信息
 *
 * 作者：李富 on 2015/11/3.
 * 邮箱：lifuzz@163.com
 */
public class ServerError extends VolleyError {

    public ServerError(NetworkResponse networkResponse) {
        super(networkResponse);
    }

    public ServerError() {
        super();
    }

}
