package com.shangying.JiYin.Utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import androidx.annotation.StringRes;

/**
 * Created with IntelliJ IDEA.
 * User: shangying.
 * Date: 2021/5/17$.
 * Time: 12:23$.
 * Explain:
 */
public class ToastUtil {
    private static ToastAdapter mAdapter;
    private static Toast mToast;

    public static void showToast(Context context, String msg) {  //直接显示字符串
        showToast(context, msg, Toast.LENGTH_SHORT);
    }

    public static void showToast(Context context,@StringRes int resId){
        showToast(context,resId,Toast.LENGTH_SHORT);
    }

    public static void showToast(Context context,@StringRes int resId,int duration){
        showToast(context,context.getResources().getText(resId),duration);
    }

    public static void showToast(Context context, CharSequence msg,int duration){
        boolean b = mAdapter == null || mAdapter.displayable();
        if (!b) {
            return;
        }
        if (Looper.getMainLooper() == Looper.myLooper()) {
            obtainAndShowToast(context, msg, duration);
        } else {
            showToastOnUiThread(context, msg, duration);
        }
    }

    public static void cancelToast() {  //取消
        boolean b = mToast!=null && (mAdapter == null || mAdapter.cancellable());
        if (!b) {
            return;
        }
        mToast.cancel();
    }

    public interface ToastAdapter {
        boolean displayable();
        boolean cancellable();
    }

    public static void setToastAdapter(ToastAdapter adapter) {
        mAdapter = adapter;
    }

    private static void showToastOnUiThread(final Context context, final CharSequence msg, final int
            duration) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                obtainAndShowToast(context, msg, duration);
            }
        });
    }

    @SuppressLint("ShowToast")
    private static void obtainAndShowToast(final Context context, final CharSequence msg, final int
            duration) {
        if (mToast == null) {
            mToast = Toast.makeText(context.getApplicationContext(), msg, duration);
        } else {
            mToast.setText(msg);
            mToast.setDuration(duration);
        }
        mToast.show();
    }

}
