package com.android.volley;

import java.util.Collections;
import java.util.Map;

/**
 *
 * 一个缓存接口，键是String类型，值是字节数组
 *
 * 作者：李富 on 2015/10/22.
 * 邮箱：lifuzz@163.com
 */
public interface Cache {

    /**
     * 通过key值获取缓存数据的单元数据对象
     * @param key 键
     * @return 值
     */
    public Entry get(String key);

    /**
     * 添加或更新缓存的数据
     * @param key 键
     * @param entry 值的单元信息类
     */
    public void put(String key,Entry entry);

    /**
     *执行任何可能长时间运行的操作需要初始化缓存; 将从一个工作线程调用。
     */
    public void initialize();

    /**
     *使指定key的缓存值过期
     * @param key 键
     * @param fullExpire 如果这个值为true，则完全过期，为false则是软过期
     */
    public void invalidate(String key,boolean fullExpire);

    /**
     * 根据指定的可以，移除缓存数据
     * @param key 键
     */
    public void remove(String key);

    /**
     * 清空缓存
     */
    public void clear();

    /**
     * 缓存的单元数据，包括：缓存的内容，缓存过期的时间，缓存需要刷新的时间等等
     */
    public static class Entry{

        /**
         * 缓存的数据
         */
        public byte[] data;

        /**
         *缓存的ETag一致性
         * 用于查看服务器的缓存
         */
        public String etag;

        /**
         * 服务器响应的时间
         */
        public long serverDate;

        /**
         * 用于判断时间是否过期
         */
        public long ttl;

        /**
         * 这个时间内，是否需要刷新
         */
        public long softTtl;

        /**
         * 从服务器接收到的不可变响应头，不能为空；
         */
        public Map<String,String> responseHeaders = Collections.emptyMap();

        /**
         * 判断缓存是否过期
         * @return 如果为true则条目过期
         */
        public boolean isExpired() {
            return this.ttl < System.currentTimeMillis();
        }

        /**
         * 是否需要刷新数据
         * @return 如果为true，则需要刷新
         */
        public boolean refreshNeeded() {

            return this.softTtl < System.currentTimeMillis();
        }

    }

}
