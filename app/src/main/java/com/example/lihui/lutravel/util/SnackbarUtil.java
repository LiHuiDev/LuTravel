package com.example.lihui.lutravel.util;

import android.support.design.widget.Snackbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.lihui.lutravel.R;

/**
 * Created by lihui on 2017/11/27.
 */

public class SnackbarUtil {

    /**
     * 设置Snackbar背景颜色
     * @param snackbar
     * @param backgroundColor
     */
    public static void setSnackbarBgColor(Snackbar snackbar, int backgroundColor) {
        View view = snackbar.getView();
        if(view != null){
            view.setBackgroundColor(backgroundColor);
        }
    }

    /**
     * 设置Snackbar文字颜色
     * @param snackbar
     * @param messageColor
     */
    public static void setSnackbarTextColor(Snackbar snackbar, int messageColor) {
        View view = snackbar.getView();
        if(view != null){
            ((TextView) view.findViewById(R.id.snackbar_text)).setTextColor(messageColor);
        }
    }

    /**
     * 设置Snackbar背景和文字颜色
     * @param snackbar
     * @param backgroundColor
     * @param messageColor
     */
    public static void setSnackBarColor(Snackbar snackbar, int backgroundColor, int messageColor) {
        View view = snackbar.getView();
        if(view != null){
            view.setBackgroundColor(backgroundColor);
            ((TextView) view.findViewById(R.id.snackbar_text)).setTextColor(messageColor);
        }
    }

    /**
     * 向Snackbar中添加view
     * @param snackbar
     * @param layoutId
     * @param index 新加布局在Snackbar中的位置
     */
    public static void snackBarAddView(Snackbar snackbar, int layoutId, int index) {
        View snackbarview = snackbar.getView();
        Snackbar.SnackbarLayout snackbarLayout = (Snackbar.SnackbarLayout)snackbarview;

        View add_view = LayoutInflater.from(snackbarview.getContext()).inflate(layoutId, null);

        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams( LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        p.gravity = Gravity.CENTER_VERTICAL;

        snackbarLayout.addView(add_view, index, p);
    }

    /**
     * 邮箱格式检验
     * @param email
     * @return
     */
    public static boolean checkEmail(String email){
        return email.matches("^[a-zA-Z0-9._-]+@[a-zA-Z0-9_-]+(\\.[a-zA-Z0-9_-]+)+$");
    }
}
