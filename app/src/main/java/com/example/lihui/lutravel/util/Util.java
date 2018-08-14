package com.example.lihui.lutravel.util;

import android.content.Context;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Created by lihui on 2018/3/4.
 */

public class Util {

    private static Toast toast;

    public static void showToast(Context context, String content) {
        if (toast == null) {
            toast = Toast.makeText(context, content, Toast.LENGTH_SHORT);
        } else {
            toast.setText(content);
        }
        toast.show();
    }

    //编辑框获取焦点
    public static void getEditTextFocus(EditText editText){
        editText.setFocusable(true);
        editText.setFocusableInTouchMode(true);
        editText.requestFocus();
    }
}
