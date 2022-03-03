package com.itwzh.repaymentcalc.utlis;

import android.content.Context;
import android.widget.Toast;

public class ToastUtils {
    private static Toast mToast;

    public static void init(Context context) {
        mToast = Toast.makeText(context, null, Toast.LENGTH_SHORT);
    }

    public static void init(Context context,CharSequence charSequence) {
        mToast = Toast.makeText(context, charSequence, Toast.LENGTH_SHORT);
    }

    public static void toast(Context context, int resId) {
        init(context);
        mToast.setText(resId);
        mToast.show();
    }

    public static void toast(Context context, CharSequence charSequence) {
        init(context,charSequence);
        mToast.show();
    }


}
