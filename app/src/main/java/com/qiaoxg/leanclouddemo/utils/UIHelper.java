package com.qiaoxg.leanclouddemo.utils;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by admin on 2017/3/27.
 */

public class UIHelper {

    private static Toast mToast;

    public static void showToast(Context context, String msg) {

        if (mToast == null) {
            mToast = Toast.makeText(context, msg, Toast.LENGTH_SHORT);
        } else {
            mToast.setDuration(Toast.LENGTH_SHORT);
            mToast.setText(msg);
        }
        mToast.show();
    }
}
