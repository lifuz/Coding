package com.android.volley;

/**
 *
 * 执行请求的接口
 *
 * 作者：李富 on 2015/11/2.
 * 邮箱：lifuzz@163.com
 */
public interface Network {

    /**
     *执行指定的请求
     * @param request
     * @return
     * @throws VolleyError
     */
    public NetworkResponse performRequest(Request<?> request) throws VolleyError;
}
