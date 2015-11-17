package com.android.volley;

/**
 * 请求的重试策略
 *
 * 作者：李富 on 2015/10/23.
 * 邮箱：lifuzz@163.com
 */
public interface RetryPolicy {

    /**
     * 返回当前超时时间(用于日志记录)
     * @return 超时时间值
     */
    public int getCurrentTimeout();

    /**
     * 获取当前重试的次数
     * @return 次数
     */
    public int getCurrentRetryCount();

    /**
     *准备下次重试连接的时间间隔
     * @param error 最新连接的错误代码
     * @throws VolleyError 如果无法进行重试抛出这个异常。
     */
    public void retry(VolleyError error) throws VolleyError;
}
