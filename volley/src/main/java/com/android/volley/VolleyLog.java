package com.android.volley;

import android.os.SystemClock;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 *
 * 日志帮助类
 *
 * 作者：李富 on 2015/10/22.
 * 邮箱：lifuzz@163.com
 */
public class VolleyLog {

    public static String TAG = "Volley";

    public static boolean DEBUG = Log.isLoggable(TAG,Log.VERBOSE);

    /**
     *定制Volley的日志标记，以便于与其他程序的日志标签区分。
     *
     * 在Volley使用之前设置并启用日志标签
     *
     * @param tag
     */
    public static void setTag(String tag) {
        //%s 为字符串占位符，在格式化的时候，可以把相应的字符串放到这个位置
        d("把日志的标签改为 %s",tag);
        TAG = tag;
        DEBUG = Log.isLoggable(TAG,Log.VERBOSE);
    }

    public static void d(String format,Object... args) {

        Log.d(TAG,buildMessage(format,args));

    }

    public static void v(String format,Object... args) {

        if (DEBUG) {
            Log.v(TAG,buildMessage(format,args));
        }

    }

    public static void e(String format,Object... args) {
        Log.e(TAG,buildMessage(format,args));
    }

    public static void e(Throwable tr,String format,Object... args) {
        Log.e(TAG,buildMessage(format,args),tr);
    }

    public static void wtf(String format,Object... args) {
        Log.wtf(TAG, buildMessage(format, args));
    }

    public static void wtf(Throwable tr,String formate,Object... args) {
        Log.wtf(TAG,buildMessage(formate,args),tr);
    }

    /**
     * 格式调用者提供并突出显示有用的消息，例如：调用的线程ID 和使用的方法名。
     * @param format
     * @param args
     * @return
     */
    private static String buildMessage(String format,Object... args) {

        String msg = (args == null) ? format : String.format(Locale.US,format,args);

        StackTraceElement[] trace = new Throwable().fillInStackTrace().getStackTrace();

        String caller = "<unknown>";

        //从堆栈外进入堆栈查找第一个调用者，这个会在至少第二个之后，所有从这个地方开始
        for(int i = 2; i < trace.length;i ++) {

            Class<?> clazz = trace[i].getClass();

            if (!clazz.equals(VolleyLog.class)){
                String callingClass = trace[i].getClassName();
                callingClass = callingClass.substring(callingClass.lastIndexOf(".") + 1);
                callingClass = callingClass.substring(callingClass.lastIndexOf("$") + 1);

                caller = callingClass + "." + trace[i].getMethodName();
                break;

            }

        }

        return String.format(Locale.US,"[%d] %s: %s",Thread.currentThread().getId(),caller,msg);
    }

    /**
     * 一个简单的事件日志，包含：名字，线程ID和时间。
     */
    static class MarkerLog{

        public static final boolean ENABLED = VolleyLog.DEBUG;

        /**
         *事件日志从第一个记录到最后一条记录的最短时间
         */
        private static final long MIN_DURATION_FOR_LOGGING_MS = 0;

        private static class Marker{
            public final String name;
            public final long thread;
            public final long time;

            public Marker(String name,Long thread,long time) {
                this.name = name;
                this.thread =thread;
                this.time = time;
            }
        }

        private final List<Marker> mMarkers = new ArrayList<>();
        private boolean mFinished = false;

        /**
         * 添加一个事件到指定的日志中。
         * @param name 事件的名字
         * @param threaId 事件的所在线程的ID号
         */
        public synchronized void add(String name,long threaId) {
            if (mFinished) {
                throw new IllegalStateException("日志已经添加到结尾");
            }

            mMarkers.add(new Marker(name,threaId, SystemClock.elapsedRealtime()));
        }

        /**
         *当第一条日志到最后一条日志的时间差大于{@link #MIN_DURATION_FOR_LOGGING_MS},
         * 则关闭这个日志，并撤销它的logcat
         * @param header 这个字符串是这个日志的名字。
         */
        public synchronized void finish(String header){

            mFinished = true;

            long duration = getToTalDuration();

            if (duration <= MIN_DURATION_FOR_LOGGING_MS) {
                return;
            }

            long preTime = mMarkers.get(0).time;
            //%-4d：'-' 字符代表左对齐，4代表最小字符数，如果不够则在右边补上相应的空格。
            d("(%-4d ms) %s", duration, header);

            for (Marker marker:mMarkers) {
                long thisTime = marker.time;
                d("(+%-4d) [%2d]  %s",(thisTime - preTime),marker.thread,marker.name);
                preTime = thisTime;
            }
        }

        @Override
        protected void finalize() throws Throwable {

            //捕捉已经完成的请求
            //但是不输出他们
            if(!mFinished) {

                finish("释放请求");
                e("日志已经完成但是没有finish() - 因为没有捕获到退出请求");

            }

        }

        /**
         * 在这个日志第一条日志到最后一条日志之间的差值。
         * @return 两条日志之间的时间差。
         */
        private long getToTalDuration() {
            if (mMarkers.size() ==0) {
                return 0;
            }

            long first = mMarkers.get(0).time;
            long last = mMarkers.get(mMarkers.size() -1 ).time;

            return last - first;

        }

    }


}
