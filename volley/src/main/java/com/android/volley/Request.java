package com.android.volley;

import android.net.TrafficStats;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.text.TextUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Collections;
import java.util.Map;

/**
 *
 * 网络访问的基类
 *
 * 作者：李富 on 2015/10/20.
 * 邮箱：lifuzz@163.com
 */
public abstract class Request<T> implements Comparable<Request<T>>  {

    /**
     *POST请求和PUT请求的默认编码
     */
    private static final String DEFAULT_PARAMS_ENCODING = "utf-8";

    /**
     * 请求支持的方法
     */
    public interface Method{

        int DEPRECATED_GET_OR_POST = -1;
        int GET = 0;
        int POST = 1;
        int PUT = 2;
        int DELETE = 3;

    }

    /**
     * 建立一个日志跟踪请求的生命周期，并调试
     */
    private final VolleyLog.MarkerLog mEventLog = VolleyLog.MarkerLog.ENABLED ?
            new VolleyLog.MarkerLog() : null;

    /**
     * 请求的请求方式，目前只支持四种：GET,POST,PUT,DELETE;
     */
    private final int mMethod ;

    /**
     * 请求的URL
     */
    private final String mUrl ;

    /**
     * {@link TrafficStats} 的默认标签
     */
    private final int mDefaultTrafficStatsTag;

    /**
     * 错误响应接口
     */
    private final Response.ErrorListener mErrorListenner;

    /**
     * 此请求的序列号,用于执行先进先出顺序。
     */
    private Integer mSequence;

    /**
     *请求所在的队列
     */
    private RequestQueue mRequestQueue;

    /**
     *请求的响应是否被缓存
     */
    private boolean mShouldCache = true;

    /**
     * 请求是否取消
     */
    private boolean mCanceled = false;

    /**
     *这个请求的响应是否被完成
     */
    private boolean mResponseDelivered = false;

    // A cheap variant of request tracing used to dump slow requests.
    private long mRequestBirthTime = 0;

    /**
     * 请求日志的初始值(即使没有启用调试日志记录)。
     */
    private static final long SLOW_REQUEST_THRESHOLD_MS = 3000;

    /**
     * 请求的重试策略
     */
    private RetryPolicy mRetryPolicy;

    /**
     *当请求可以从缓存中得到，但是必须从网络刷新数据，数据存储这里。当没有修改时，我们可以
     * 确定不会被删除
     */
    private Cache.Entry cacheEntry = null;

    /**
     *请求的隐藏令牌标准，用于批量取消
     */
    private Object mTag;

    /**
     *由这两个参数创建一个新的请求。注：这个请求的响应没有提供正常的响应，它的响应由能更好的子类提供。
     */
    public Request(String url,Response.ErrorListener listener) {
        this(Method.DEPRECATED_GET_OR_POST,url,listener);
    }

    /**
     *由这四个参数创建一个新的请求。注：这个请求的响应没有提供正常的响应，它的响应由能更好的子类提供。
     */
    public Request(int method,String url,Response.ErrorListener listener) {
        mMethod = method;
        mUrl = url;
        mErrorListenner = listener;

        setRetryPolicy(new DefaultRetryPolicy());

        mDefaultTrafficStatsTag = TextUtils.isEmpty(url) ? 0: Uri.parse(url).getHost().hashCode();

    }

    /**
     * 获取请求方法，只能是{@link Method} 里的方法
     */
    public int getMethod(){
        return mMethod;
    }

    /**
     *给这个请求设置一个标签，这个标签可以取消所有的请求
     */
    public void setTag(Object tag) {
        mTag = tag;
    }

    /**
     * 获取请求标签
     */
    public Object getTag() {
        return mTag;
    }

    /**
     * @return A tag for use with {@link TrafficStats#setThreadStatsTag(int)}
     */
    public int getTrafficStatsTag() {
        return mDefaultTrafficStatsTag;
    }


    /**
     * 设置重试策略
     */
    public void setRetryPolicy(RetryPolicy retryPolicy) {

        mRetryPolicy = retryPolicy;

    }

    /**
     * 条件一个事件记录到请求的事件日志中；用来调试
     * @param tag
     */
    public void addMarker(String tag) {
        if (VolleyLog.MarkerLog.ENABLED) {
            mEventLog.add(tag,Thread.currentThread().getId());
        } else if (mRequestBirthTime == 0) {
            mRequestBirthTime = SystemClock.elapsedRealtime();
        }
    }

    /**
     * 通知请求队列，这个请求已经被完成（完成或失败）
     *
     *并记录到日志文件，为了调试。
     *
     * @param tag
     */
    void finish(final String tag) {

        if(mRequestQueue !=null) {
            mRequestQueue.finish(this);
        }

        if (VolleyLog.MarkerLog.ENABLED) {

            final long threadId = Thread.currentThread().getId();

            if (Looper.myLooper() != Looper.getMainLooper()){
                // If we finish marking off of the main thread, we need to
                // actually do it on the main thread to ensure correct ordering.
                /**
                 * 如果主线程完成了标记，我们需要在主线程上实际执行他，以确保正确的顺序
                 */
               Handler mainThread = new Handler(Looper.getMainLooper());

                mainThread.post(new Runnable() {
                    @Override
                    public void run() {
                        mEventLog.add(tag,threadId);
                        mEventLog.finish(this.toString());
                    }
                });

                return;
            }

            mEventLog.add(tag,threadId);
            mEventLog.finish(this.toString());

        } else {
            long requestTime = SystemClock.currentThreadTimeMillis() - mRequestBirthTime;

            if (requestTime >= SLOW_REQUEST_THRESHOLD_MS) {
                VolleyLog.d("%d ms: %s" ,requestTime,this.toString());
            }
        }

    }

    /**
     * 将请求添加到指定的请求队列，并在请求完成时发出通知
     * @param requestQueue 指定的请求的队列
     */
    public void setRequestQueue(RequestQueue requestQueue) {
        mRequestQueue = requestQueue;
    }

    /**
     *使用{@link RequestQueue} 设置请求的序列号
     * @param sequence
     */
    public final void setSequence(int sequence) {
        mSequence = sequence;
    }

    /**
     * 获取请求的序列号
     */
    public final int getSequence() {

        if (mSequence == null) {
            throw new IllegalStateException("获取序列号之前，请先设置序列号");
        }

        return mSequence;

    }

    /**
     * 获取请求的URL链接
     */
    public String getUrl() {
        return mUrl;
    }

    /**
     * 获取的请求的缓存的键(key) ，默认值：请求的URL链接
     * @return
     */
    public String getCacheKey() {
        return getUrl();
    }

    /**
     *设置这个请求的缓存，用于缓存条目检索
     *用于缓存支持
     * @param entry 缓存
     */
    public void setCacheEntry(Cache.Entry entry) {
        cacheEntry = entry;
    }

    /**
     * 获取缓存信息，如果没有检索到，则返回null;
     * @return
     */
    public Cache.Entry getCacheEntry() {
        return cacheEntry;
    }

    /**
     * 这个请求被标记为取消请求，将没有回调响应
     */
    public void cancele() {
        mCanceled = true;
    }

    /**
     * 如果这个请求被取消了，则返回true
     * @return
     */
    public boolean isCanceled(){
        return mCanceled;
    }

    /**
     *返回http额外的标头列表；在验证标头时，可能出现{@link AuthFailureError} 错误。
     * @throws AuthFailureError 验证失败错误
     */
    public Map<String,String> getHeaders()throws AuthFailureError{
        return Collections.emptyMap();
    }

    /**
     * 获取POST请求的参数，如果为空，则这个请求是GET请求；在验证这些值，可能出现异常{@link AuthFailureError}
     * @throws AuthFailureError 验证错误
     * @deprecated 被 {@link #getParams()} 这个方法取代
     */
    protected Map<String,String > getPostParams() throws AuthFailureError{
        return getParams();
    }

    /**
     * Returns which encoding should be used when converting POST parameters returned by
     * {@link #getPostParams()} into a raw POST body.
     *
     * <p>This controls both encodings:
     * <ol>
     *     <li>The string encoding used when converting parameter names and values into bytes prior
     *         to URL encoding them.</li>
     *     <li>The string encoding used when converting the URL encoded parameters into a raw
     *         byte array.</li>
     * </ol>
     *
     * @deprecated Use {@link #getParamsEncoding()} instead.
     *
     *  获取参数的编码方式
     */
    protected String getPostParamsEncoding(){
        return getParamsEncoding();
    }

    /**
     * @deprecated Use {@link #getBodyContentType()} instead.
     */
    public String getPostBodyContentType() {
        return getBodyContentType();
    }

    /**
     * 返回POST请求的消息体
     * @return
     * @throws AuthFailureError
     * @deprecated
     */
    public byte[] getPostBody() throws AuthFailureError{

        Map<String,String> postparams = getPostParams();

        if (postparams != null && postparams.size() > 0) {
            return encodeParameters(postparams,getPostParamsEncoding());
        }

        return null;
    }

    /**
     *获取POST或PUT请求的请求参数，在验证这些值，可能出现异常
     */
    protected Map<String,String> getParams() throws AuthFailureError{
        return null;
    }

    /**
     * Returns which encoding should be used when converting POST or PUT parameters returned by
     * {@link #getParams()} into a raw POST or PUT body.
     *
     * <p>This controls both encodings:
     * <ol>
     *     <li>The string encoding used when converting parameter names and values into bytes prior
     *         to URL encoding them.</li>
     *     <li>The string encoding used when converting the URL encoded parameters into a raw
     *         byte array.</li>
     * </ol>
     *
     * 获取POST和PUT请求参数的编码方式
     */
    protected String getParamsEncoding() {
        return DEFAULT_PARAMS_ENCODING;
    }

    public String getBodyContentType() {
        return "application/x-www-form-urlencoded; charset=" + getParamsEncoding();
    }

    /**
     * 获取POST和PUT请求的消息体
     * @return
     * @throws AuthFailureError
     */
    public byte[] getBody() throws AuthFailureError{

        Map<String,String> params = getParams();

        if (params !=null && params.size() > 0) {
            return encodeParameters(params,getParamsEncoding());
        }

        return null;
    }

    /**
     * 根据编码方式把params参数编译成application/x-www-form-urlencoded
     * @param params
     * @param paramsEncoding
     * @return
     */
    private byte[]  encodeParameters(Map<String,String> params,String paramsEncoding){

        StringBuilder encodedParams = new StringBuilder();

        try {

            for (Map.Entry<String,String> entry:params.entrySet()){

                encodedParams.append(URLEncoder.encode(entry.getKey(),paramsEncoding));
                encodedParams.append("=");
                encodedParams.append(URLEncoder.encode(entry.getValue(),paramsEncoding));
                encodedParams.append("&");

            }

            return encodedParams.toString().getBytes(paramsEncoding);

        } catch (UnsupportedEncodingException uee) {
            throw new RuntimeException("这种编码不支持："+paramsEncoding,uee );
        }
    }

    /**
     * 设置请求是否缓存
     */
    public final void setShouldCache(boolean shouldCache) {
        mShouldCache =shouldCache;
    }

    /**
     *如果该请求可以缓存，则返回true
     * @return
     */
    public final boolean ShouldCache() {
        return mShouldCache;
    }

    /**
     *优先级：请求将从先执行高优先级，最后执行低优先级，遵循先进先出的原则
     */
    public enum Priority {
        LOW,
        NORMAL,
        HIGH,
        IMMEDIATE
    }

    /**
     * 获取这个请求的优先级{@link Priority},默认值为{@link Priority#NORMAL}
     * @return 优先级
     */
    public Priority getPriority() {
        return Priority.NORMAL;
    }

    /**
     *获取请求重试的socket套接字的超时时间（ms）；
     * @return
     */
    public final int getTimeoutMs() {
        return mRetryPolicy.getCurrentTimeout();
    }

    /**
     * 获取请求的重试的策略
     * @return
     */
    public RetryPolicy getRetryPolicy() {
        return mRetryPolicy;
    }

    /**
     *标记这个请求已经响应了完成，
     */
    public void markDelivered(){
        mResponseDelivered = true;
    }

    /**
     * 如果这个请求已经响应完成，则返回true；
     * @return
     */
    public boolean hasHadResponseDelivered(){
        return mResponseDelivered;
    }

    /**
     *子类必须实现这个方法，这个方法用于解析网络响应的数据，返回响应的响应类型。
     * 这个方法是从WorkerThread调用，如果响应的数据为null，响应则不会交付
     *
     * @param response 网络响应
     * @return 解析响应，可能出现null的错误
     */
    abstract protected Response<T> parseNetworkResponse(NetworkResponse response);

    /**
     * 子类可以重新实现这个方法，可以返回更详细的错误信息
     * @param error 网络响应的错误信息
     * @return 一个更加详细的错误信息
     */
    protected VolleyError parseNetworkError(VolleyError error) {
        return error;
    }

    /**
     * 交付响应
     * 子类必须实现，给定的响应不能为null，
     * 没有被解析的响应数据，也不能交付
     * @param response 被{@link #parseNetworkResponse(NetworkResponse)}解析完成的响应
     */
    abstract protected void deliverResponse(T response);

    /**
     * 给请求初始化的ErrorListenner提供错误信息
     * @param error 错误的描述
     */
    public void deliverError(VolleyError error) {
        if (mErrorListenner != null) {
            mErrorListenner.onErrorListener(error);
        }
    }

    /**
     * 根据优先级从高到低排序，如果优先级相同，则根据序列号进行排序
     * @param other
     * @return
     */
    @Override
    public int compareTo(Request<T> other) {

        Priority left = this.getPriority();
        Priority right = other.getPriority();

        return left == right?
                this.mSequence - other.mSequence : right.ordinal() - left.ordinal();
    }

    @Override
    public String toString() {
        String trafficStatsTag = "0x" + Integer.toHexString(getTrafficStatsTag());
        return (mCanceled ? "[X] " : "[ ] ") + getUrl() + " " + trafficStatsTag + " "
                + getPriority() + " " + mSequence;
    }
}
