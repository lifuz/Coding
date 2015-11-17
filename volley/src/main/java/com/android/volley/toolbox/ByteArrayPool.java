package com.android.volley.toolbox;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * ByteArrayPool is a source and repository of <code>byte[]</code> objects. Its purpose is to
 * supply those buffers to consumers who need to use them for a short period of time and then
 * dispose of them. Simply creating and disposing such buffers in the conventional manner can
 * considerable heap churn and garbage collection delays on Android, which lacks good management of
 * short-lived heap objects. It may be advantageous to trade off some memory in the form of a
 * permanently allocated pool of buffers in order to gain heap performance improvements; that is
 * what this class does.
 * <p>
 * A good candidate user for this class is something like an I/O system that uses large temporary
 * <code>byte[]</code> buffers to copy data around. In these use cases, often the consumer wants
 * the buffer to be a certain minimum size to ensure good performance (e.g. when copying data chunks
 * off of a stream), but doesn't mind if the buffer is larger than the minimum. Taking this into
 * account and also to maximize the odds of being able to reuse a recycled buffer, this class is
 * free to return buffers larger than the requested size. The caller needs to be able to gracefully
 * deal with getting buffers any size over the minimum.
 * <p>
 * If there is not a suitably-sized buffer in its recycling pool when a buffer is requested, this
 * class will allocate a new buffer and return it.
 * <p>
 * This class has no special ownership of buffers it creates; the caller is free to take a buffer
 * it receives from this pool, use it permanently, and never return it to the pool; additionally,
 * it is not harmful to return to this pool a buffer that was allocated elsewhere, provided there
 * are no other lingering references to it.
 * <p>
 * This class ensures that the total size of the buffers in its recycling pool never exceeds a
 * certain byte limit. When a buffer is returned that would cause the pool to exceed the limit,
 * least-recently-used buffers are disposed.
 *
 * 作者：李富 on 2015/11/3.
 * 邮箱：lifuzz@163.com
 */
public class ByteArrayPool {

    /**
     *缓冲池，两个分别用于最后使用的和缓存池大小
     */
    private List<byte[]> mBuffersByLastUse = new ArrayList<>();
    private List<byte[]> mBuffersBySize = new ArrayList<>();

    /**
     * 缓冲池里的缓存区的总数
     */
    private int mCurrentSize = 0;

    /**
     *缓存池的最大的缓冲区，老的被丢弃
     */
    private final int mSizeLimit;

    /**
     * 比较缓冲区的大小
     */
    protected static final Comparator<byte[]> BUF_COMPARATOR = new Comparator<byte[]>() {
        @Override
        public int compare(byte[] lhs, byte[] rhs) {
            return lhs.length - rhs.length;
        }
    };

    /**
     * @param sizeLimit 缓存池的最大大小，以字节为单位
     */
    public ByteArrayPool(int sizeLimit) {
        mSizeLimit = sizeLimit;
    }

    /**
     *从缓存池中获取一个缓存区，如果缓冲池中的缓冲区大小符合我们需要的大小，则直接从缓存池中获取，
     * 如果不符合，则在创建一个。
     * @param len
     * @return
     */
    public synchronized byte[] getBuf(int len) {
        for (int i = 0 ; i <mBuffersBySize.size();i++) {
            byte[] buf = mBuffersBySize.get(i);
            if (buf.length >= len) {
                mCurrentSize -= buf.length;
                mBuffersBySize.remove(i);
                mBuffersByLastUse.remove(buf);
                return buf;
            }
        }

        return new byte[len];
    }

    /**
     * 从缓存池中获取一个缓存区，如果该缓存区超过规定大小，则被丢弃
     * @param buf 从缓冲池中获取的缓存
     */
    public synchronized void returnBuf(byte[] buf) {

        if (buf == null && buf.length > mSizeLimit) {
            return;
        }

        mBuffersByLastUse.add(buf);
        int pos = Collections.binarySearch(mBuffersBySize,buf,BUF_COMPARATOR);
        if (pos < 0) {
            pos = -pos - 1;

        }

        mBuffersBySize.add(pos,buf);
        mCurrentSize += buf.length;
        trim();

    }

    /**
     * 从缓冲池中删除缓冲区，直到缓冲池的大小符合规定的大小
     */
    private synchronized void trim() {

        while (mCurrentSize > mCurrentSize) {
            byte[] buf = mBuffersByLastUse.remove(0);
            mBuffersBySize.remove(buf);
            mCurrentSize -= buf.length;
        }

    }


}
