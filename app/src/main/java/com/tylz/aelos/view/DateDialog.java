package com.tylz.aelos.view;


import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.bruce.pickerview.LoopView;
import com.tylz.aelos.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;

public class DateDialog
        extends Dialog
{

    @Bind(R.id.view_btn_cancel)
    Button   mViewBtnCancel;
    @Bind(R.id.view_btn_confirm)
    Button   mViewBtnConfirm;
    @Bind(R.id.view_loop_month)
    LoopView mViewLoopMonth;
    @Bind(R.id.view_loop_data)
    LoopView mViewLoopData;
    private List<String> mMonthDatas;
    private List<String> mDayDatas;
    private int mMonthPos = 0;
    private int mDayPos   = 0;

    public DateDialog(Context context) {
        super(context, R.style.ThemeIOSDialog);
        mMonthDatas = new ArrayList<>();
        mDayDatas = new ArrayList<>();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_select_age);
        initData();
        initDay();
    }

    private void initDay() {
           /*初始化日数据*/
        int      dayMaxInMonth;
        Calendar calendar = Calendar.getInstance();
    }

    private void initData() {
        /*初始化月份数据*/
        for (int i = 0; i < 12; i++) {
            mMonthDatas.add(format2LenStr(i + 1));
        }

    }

    public static String format2LenStr(int num) {

        return (num < 10)
               ? "0" + num
               : String.valueOf(num);
    }

    @OnClick({R.id.view_btn_cancel,
              R.id.view_btn_confirm})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.view_btn_cancel:
                dismiss();
                break;
            case R.id.view_btn_confirm:
                confirm();
                break;
        }
    }

    /**
     * 选择日期并且在编辑页面设置数据
     */
    private void confirm() {
        dismiss();
    }
}
