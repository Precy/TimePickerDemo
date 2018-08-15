package com.google.sxs.timepickerdemo.widget;

import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author sxs
 * @date 2018/8/15
 */
public class Utils {
    /**
     * 隐藏系统输入法
     *
     * @param context
     * @param v
     */
    public static void commonHideSystemSoftKeyboard(Context context, View v) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
    }

    /**
     * 提取数字
     */
    public static int getInteger(String content) {
        final String REG_NUMBER = "[^0-9]";
        Pattern p = Pattern.compile(REG_NUMBER);
        Matcher m = p.matcher(content);
        return Integer.parseInt(m.replaceAll("").trim());
    }
}
