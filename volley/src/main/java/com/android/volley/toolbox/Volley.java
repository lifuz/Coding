package com.android.volley.toolbox;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.http.AndroidHttpClient;
import android.os.Build;

import com.android.volley.RequestQueue;

import java.io.File;

/**
 * 作者：李富 on 2015/10/20.
 * 邮箱：lifuzz@163.com
 */
@SuppressWarnings("deprecation")
public class Volley {

    /*
     * 默认磁盘存储目录
     */
    private static final String DEFAULT_CACHE_DIR = "volley";

    /*
     * 创建一个默认请求队列栈，并启用
     */

    /**
     *
     * @param context
     * @param stack
     * @return
     */

    public static RequestQueue newREquestQueue(Context context,HttpStack stack) {

        //缓存目录
        File cacheDir = new File(context.getCacheDir(),DEFAULT_CACHE_DIR);

        //拼接UA
        String userAgent = "volley/0";

        try {
            String packageName = context.getPackageName();

            PackageInfo info = context.getPackageManager().getPackageInfo(packageName,0);

            userAgent = packageName + "/" + info.versionCode;

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        //如果stack为空，则创建一个网络访实例。
        if (stack == null) {

            //根据系统版本创建网络访问实例
            if (Build.VERSION.SDK_INT >= 9) {

                //如果版本大于等于9，则创建HttpURLConnection实例
                stack = new HurlStack();

            } else {

                //如果版本小于9，则创建HttpClient实例
                stack = new HttpClientStack(AndroidHttpClient.newInstance(userAgent));

            }

        }

        return  null;
    }
}
