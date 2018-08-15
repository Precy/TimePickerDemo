package com.google.sxs.timepickerdemo.widget;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.PopupWindow;

import com.google.sxs.timepickerdemo.R;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * @author sxs
 * @date 2018/1/24
 */

public class TimePicker implements PopupWindow.OnDismissListener, View.OnClickListener {
    private OnTimeSelectListener mOnTimeSelectListener;
    private PopupWindow mPickerWindow;
    private WheelRecyclerView mWheelRv1;
    private WheelRecyclerView mWheelRv2;
    private WheelRecyclerView mWheelRv3;
    private WheelRecyclerView mWheelRv4;
    private WheelRecyclerView mWheelRv5;
    private Activity mContext;
    private View mParent;

    private List<String> mStartHourDatas;
    private List<String> mMinuteDatas;
    private List<String> mEndHourDatas;

    public static final int MAX_HOUR = 24;
    public static final int MAX_MINUTE = 60;

    public TimePicker(Activity mContext, View mParent) {
        this.mContext = mContext;
        this.mParent = mParent;
        View pickerView = LayoutInflater.from(mContext).inflate(R.layout.time_picker, null);
        mWheelRv1 = pickerView.findViewById(R.id.wheel_1);
        mWheelRv2 = pickerView.findViewById(R.id.wheel_2);
        mWheelRv3 = pickerView.findViewById(R.id.wheel_3);
        mWheelRv4 = pickerView.findViewById(R.id.wheel_4);
        mWheelRv5 = pickerView.findViewById(R.id.wheel_5);
        pickerView.findViewById(R.id.tv_exit).setOnClickListener(this);
        pickerView.findViewById(R.id.tv_ok).setOnClickListener(this);

        mPickerWindow = new PopupWindow(pickerView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        mPickerWindow.setBackgroundDrawable(new ColorDrawable(Color.WHITE));
        mPickerWindow.setFocusable(true);
        mPickerWindow.setAnimationStyle(R.style.ActionSheetDialogAnimation);
        mPickerWindow.setOnDismissListener(this);

        initData();
    }

    /**
     * 初始化数据
     */
    private void initData() {
        mStartHourDatas = new ArrayList<>();
        for (int i = 0; i < MAX_HOUR; i++) {
            mStartHourDatas.add(i + "时");
        }
        ArrayList<String> middleData = new ArrayList<>();
        middleData.add("至");
        mMinuteDatas = new ArrayList<>();
        for (int i = 0; i < MAX_MINUTE; i++) {
            mMinuteDatas.add(i + "分");
        }
        initEndData();
        mWheelRv1.setData(mStartHourDatas);
        mWheelRv2.setData(mMinuteDatas);
        mWheelRv3.setData(middleData);

        mWheelRv1.setOnSelectListener((position, data) -> initEndData());
    }

    private void initEndData() {
        final int startPosition = mWheelRv1.getSelected();
        mEndHourDatas = new ArrayList<>();
        for (int i = 0; i < MAX_HOUR; i++) {
            if (startPosition + i < MAX_HOUR) {
                mEndHourDatas.add(startPosition + i + "时");
            } else {
                mEndHourDatas.add("次日" + (startPosition + i) % MAX_HOUR + "时");
            }
        }
        mWheelRv4.setData(mEndHourDatas);
        mWheelRv5.setData(mMinuteDatas);
    }

    public TimePicker setOnTimeSelectListener(OnTimeSelectListener listener) {
        this.mOnTimeSelectListener = listener;
        return this;
    }

    /**
     * 弹出Window时使背景变暗
     *
     * @param alpha
     */
    private void backgroundAlpha(float alpha) {
        Window window = mContext.getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.alpha = alpha;
        window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        window.setAttributes(lp);
    }

    public void attachView(View attachView) {
        Utils.commonHideSystemSoftKeyboard(mContext, attachView);
        backgroundAlpha(0.5f);
        mPickerWindow.showAtLocation(mParent, Gravity.BOTTOM, 0, 0);
    }

    @Override
    public void onDismiss() {
        backgroundAlpha(1f);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_ok:
                if (mOnTimeSelectListener != null) {
                    String startHour = mStartHourDatas.get(mWheelRv1.getSelected());
                    String startMinute = mMinuteDatas.get(mWheelRv2.getSelected());
                    String endHour = mEndHourDatas.get(mWheelRv4.getSelected());
                    String endMinute = mMinuteDatas.get(mWheelRv5.getSelected());
                    mOnTimeSelectListener.onTimeSelect(Utils.getInteger(startHour), Utils.getInteger(startMinute),
                            Utils.getInteger(endHour), Utils.getInteger(endMinute));
                    mPickerWindow.dismiss();
                }
                break;
            case R.id.tv_exit:
                mPickerWindow.dismiss();
                break;
            default:
                break;
        }
    }

    public interface OnTimeSelectListener {
        /**
         * 确认选择回调
         *
         * @param startHour
         * @param startMinute
         * @param endHour
         * @param endMinute
         */
        void onTimeSelect(int startHour, int startMinute, int endHour, int endMinute);
    }


}
