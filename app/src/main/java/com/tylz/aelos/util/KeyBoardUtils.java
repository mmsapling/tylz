package com.tylz.aelos.util;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import java.util.Timer;
import java.util.TimerTask;

/**
 * 打开或关闭软键盘
 *
 * @author xdsjs
 */
public class KeyBoardUtils {
    /**
     * 打卡软键盘
     *
     * @param mEditText 输入框
     * @param mContext  上下文
     */
    public static void openKeybord(final EditText mEditText, final Context mContext) {

        //必须要等UI绘制完成之后，打开软键盘的代码才能生效，所以要设置一个延时
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                InputMethodManager imm = (InputMethodManager) mContext
                        .getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(mEditText, InputMethodManager.RESULT_SHOWN);
                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,
                        InputMethodManager.HIDE_IMPLICIT_ONLY);
            }
        }, 500);
    }

    /**
     * 关闭软键盘
     * 可能不管用
     * @param mEditText 输入框
     * @param mContext  上下文
     */
    public static void closeKeybord(EditText mEditText, Context mContext) {
        InputMethodManager imm = (InputMethodManager) mContext
                .getSystemService(Context.INPUT_METHOD_SERVICE);

        imm.hideSoftInputFromWindow(mEditText.getWindowToken(), 0);
    }
    /**强制隐藏软键盘 可能不管用*/
    public static void keyBoardCancle(Activity activity) {
        View view = activity.getWindow().peekDecorView();
        if (view != null) {
            InputMethodManager inputmanger = (InputMethodManager)activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            inputmanger.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    /**
     * 关闭软键盘
     * 管用
     * @param mEditText 输入框
     * @param mContext  上下文
     */
    public static void closeKeybordMethod(EditText mEditText, Context mContext){
        ((InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(mEditText.getWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS);
    }
}