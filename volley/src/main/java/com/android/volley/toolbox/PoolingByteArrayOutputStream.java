package com.android.volley.toolbox;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 *
 * {@link ByteArrayOutputStream} 的变体，用byte[]做缓存区，而不是重新分配，节省创建堆的时间
 *
 * 作者：李富 on 2015/11/3.
 * 邮箱：lifuzz@163.com
 */
public class PoolingByteArrayOutputStream extends ByteArrayOutputStream {

    /**
     *byt[] 数组的默认长度
     */
    private static final int DEFAULT_SIZE = 256;

    private ByteArrayPool mPool;

    /**
     * Constructs a new PoolingByteArrayOutputStream with a default size. If more bytes are written
     * to this instance, the underlying byte array will expand.
     */
    public PoolingByteArrayOutputStream(ByteArrayPool pool) {
        this(pool,DEFAULT_SIZE);
    }

    /**
     * Constructs a new {@code ByteArrayOutputStream} with a default size of {@code size} bytes. If
     * more than {@code size} bytes are written to this instance, the underlying byte array will
     * expand.
     *
     * @param size initial size for the underlying byte array. The value will be pinned to a default
     *        minimum size.
     */
    public PoolingByteArrayOutputStream(ByteArrayPool pool,int size){
        mPool = pool;
        buf = pool.getBuf(Math.max(size,DEFAULT_SIZE));
    }

    @Override
    protected void finalize() throws Throwable {
        mPool.returnBuf(buf);
    }

    @Override
    public synchronized void write(byte[] buffer, int offset, int len) {
        expand(len);
        super.write(buffer, offset, len);
    }

    @Override
    public synchronized void write(int oneByte) {
        expand(1);
        super.write(oneByte);
    }

    @Override
    public void close() throws IOException {

        mPool.returnBuf(buf);
        buf = null;
        super.close();
    }

    private void expand(int len) {
        if (count + len <= buf.length) {
            return;
        }

        byte[] newBuf = mPool.getBuf((count + len) * 2);
        System.arraycopy(buf,0,newBuf,0,count);
        mPool.returnBuf(buf);
        buf = newBuf;
    }
}
