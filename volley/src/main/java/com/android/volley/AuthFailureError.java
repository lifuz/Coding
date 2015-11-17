package com.android.volley;

import android.content.Intent;

/**
 *
 * 这个错误表明，这个请求的权限验证失败
 *
 * 作者：李富 on 2015/10/20.
 * 邮箱：lifuzz@163.com
 */
@SuppressWarnings("serial")
public class AuthFailureError extends VolleyError {

    /**
     * 这个Intent用来解决这个异常(会弹出密码对话框。)
     */
    private Intent mResolutionIntent;

    public AuthFailureError(){};

    public AuthFailureError(Intent intent) {
        mResolutionIntent = intent;
    }

    public AuthFailureError(NetworkResponse response) {
        super(response);
    }

    public AuthFailureError(String message) {
        super(message);
    }

    public AuthFailureError(String message,Exception reason) {
        super(message,reason);
    }

    public Intent getmResolutionIntent() {
        return mResolutionIntent;
    }

    @Override
    public String getMessage() {

        if (mResolutionIntent != null) {
            return "用户需要（重新）输入密码。";
        }

        return super.getMessage();
    }
}
