package com.android.volley;

/**
 * 默认重试策略
 *
 * 作者：李富 on 2015/10/23.
 * 邮箱：lifuzz@163.com
 */
public class DefaultRetryPolicy implements RetryPolicy {

    /**
     *当前超时时间，以毫秒为单位
     */
    private int mCurrentTimeoutMs;

    /**
     * 当前重试次数
     */
    private int mCurrentRetryCount;

    /**
     * 最大重试次数
     */
    private final int mMaxNumRetries;

    /** The backoff multiplier for for the policy. */
    private final float mBackoffMultiplier;

    /**
     *默认Socket套接字超时时间，以毫秒为单位
     */
    private static final int DEFAULT_TIMEOUT_MS = 2500;

    /**
     * 默认最大重试次数
     */
    private static final int DEFAULT_MAX_RETRIES = 1;

    /** The default backoff multiplier */
    private static final float DEFAULT_BACKOFF_MUL = 1f;


    /**
     * 默认重试策略的构造器
     * @param initialTimeOutMs 最大超时时间
     * @param maxNumRetries 最大重试次数
     * @param backoffMultiplier Backoff multiplier for the policy.
     */
    public DefaultRetryPolicy(int initialTimeOutMs,int maxNumRetries,float backoffMultiplier){
        mCurrentTimeoutMs = initialTimeOutMs;
        mMaxNumRetries = maxNumRetries;
        mBackoffMultiplier = backoffMultiplier;
    }

    /**
     * 使用默认配置的构造器
     */
    public DefaultRetryPolicy() {
        this(DEFAULT_TIMEOUT_MS,DEFAULT_MAX_RETRIES,DEFAULT_BACKOFF_MUL);
    }

    /**
     * 返回真实超时时间
     */
    @Override
    public int getCurrentTimeout() {
        return mCurrentTimeoutMs;
    }

    /**
     * 返回当前重试次数
     */
    @Override
    public int getCurrentRetryCount() {
        return mMaxNumRetries;
    }

    /**
     * Prepares for the next retry by applying a backoff to the timeout.
     * @param error The error code of the last attempt.
     */
    @Override
    public void retry(VolleyError error) throws VolleyError {
        mCurrentRetryCount++;
        mCurrentTimeoutMs += (mCurrentTimeoutMs * mBackoffMultiplier);
        if (!hasAttemptRemaining()) {
            throw error;
        }
    }

    /**
     * 如果当前重试次数小于等于最大重试次数，返回true，否则返回false
     */
    protected boolean hasAttemptRemaining() {

        return mCurrentRetryCount <= mMaxNumRetries;

    }

}
