package com.google.sxs.timepickerdemo.widget;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.google.sxs.timepickerdemo.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


/**
 * 年月日时分选择
 *
 * @author sxs
 * @date 2018/8/15
 */

public class DatePickerView implements PopupWindow.OnDismissListener, View.OnClickListener {

    public static final int MAX_MONTH = 12;
    public static final int MAX_HOUR = 24;
    public static final int MAX_SECOND = 60;

    private OnTimeSelectListener mOnTimeSelectListener;
    private PopupWindow mPickerWindow;
    private WheelRecyclerView mWheelRv1;
    private WheelRecyclerView mWheelRv2;
    private WheelRecyclerView mWheelRv3;
    private WheelRecyclerView mWheelRv4;
    private WheelRecyclerView mWheelRv5;
    private Activity mContext;
    private View mParent;

    private List<String> mYears;
    private List<String> mMonths;
    private List<String> mDays;
    private List<String> mHours;
    private List<String> mMinutes;
    private TextView mTitleTv;

    private Calendar mCalendar;

    public DatePickerView(Activity mContext, View mParent) {
        this.mContext = mContext;
        this.mParent = mParent;
        View pickerView = LayoutInflater.from(mContext).inflate(R.layout.time_picker, null);
        mTitleTv = pickerView.findViewById(R.id.tv_title);
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
        mCalendar = Calendar.getInstance();
        mYears = new ArrayList<>();
        //选择年范围
        for (int i = 0; i <= 4; i++) {
            mYears.add(mCalendar.get(Calendar.YEAR) - i + "年");
        }
        mWheelRv1.setData(mYears);
        initMonths();
        mWheelRv1.setOnSelectListener((position, data) -> {
            updateTitleText();
            //处理2月29号
            if (mWheelRv2.getSelected() == 1) {
                initDays();
            }
        });
        initHourAndMinute();
        updateTitleText();
    }

    private void initMonths() {
        mMonths = initWheelList(1, MAX_MONTH, "月");
        mWheelRv2.setData(mMonths);
        initDays();
        mWheelRv2.setOnSelectListener((position1, data) -> {
            updateTitleText();
            mCalendar.set(Calendar.MONTH, Utils.getInteger(data) - 1);
            initDays();
        });
    }

    private void initDays() {
        mDays = initWheelList(1, mCalendar.getActualMaximum(Calendar.DAY_OF_MONTH), "日");
        mWheelRv3.setData(mDays);
        mWheelRv3.setOnSelectListener((position, data) -> updateTitleText());
    }

    private void initHourAndMinute() {
        mHours = initWheelList(0, MAX_HOUR, "时");
        mMinutes = initWheelList(0, MAX_SECOND, "分");
        mWheelRv4.setData(mHours);
        mWheelRv5.setData(mMinutes);
        mWheelRv4.setOnSelectListener((position, data) -> updateTitleText());
        mWheelRv5.setOnSelectListener((position, data) -> updateTitleText());
    }

    /**
     * 生成WheelList数据
     *
     * @param start
     * @param end
     * @param endWith
     * @return
     */
    private List<String> initWheelList(int start, int end, String endWith) {
        List<String> list = new ArrayList<>();
        for (int i = start; i <= end; i++) {
            list.add(i + endWith);
        }
        return list;
    }

    /**
     * 更新标题文字
     */
    private void updateTitleText() {
        mTitleTv.setText(
                String.format("%s-%s-%s  %s:%s",
                        Utils.getInteger(mYears.get(mWheelRv1.getSelected())) + "",
                        (Utils.getInteger(mMonths.get(mWheelRv2.getSelected())) < 10 ? "0" + Utils.getInteger(mMonths.get(mWheelRv2.getSelected())) : Utils.getInteger(mMonths.get(mWheelRv2.getSelected())) + ""),
                        (Utils.getInteger(mDays.get(mWheelRv3.getSelected())) < 10 ? "0" + Utils.getInteger(mDays.get(mWheelRv3.getSelected())) : Utils.getInteger(mDays.get(mWheelRv3.getSelected())) + ""),
                        (Utils.getInteger(mHours.get(mWheelRv4.getSelected())) < 10 ? "0" + Utils.getInteger(mMinutes.get(mWheelRv4.getSelected())) : Utils.getInteger(mMinutes.get(mWheelRv4.getSelected())) + ""),
                        (Utils.getInteger(mMinutes.get(mWheelRv5.getSelected())) < 10 ? "0" + Utils.getInteger(mMinutes.get(mWheelRv5.getSelected())) : Utils.getInteger(mMinutes.get(mWheelRv5.getSelected())) + ""))
        );
    }

    public DatePickerView setOnTimeSelectListener(OnTimeSelectListener listener) {
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
        int id = v.getId();
        if (id == R.id.tv_ok) {
            if (mOnTimeSelectListener == null) {
                return;
            }
            String year = mYears.get(mWheelRv1.getSelected());
            String month = mMonths.get(mWheelRv2.getSelected());
            String day = mDays.get(mWheelRv3.getSelected());
            String hour = mHours.get(mWheelRv4.getSelected());
            String minute = mMinutes.get(mWheelRv5.getSelected());
            mOnTimeSelectListener.onDateSelect(Utils.getInteger(year), Utils.getInteger(month),
                    Utils.getInteger(day), Utils.getInteger(hour), Utils.getInteger(minute), mTitleTv.getText().toString());
            mPickerWindow.dismiss();
        } else if (id == R.id.tv_exit) {
            mPickerWindow.dismiss();
        }
    }

    public interface OnTimeSelectListener {
        /**
         * 确认选择回调
         *
         * @param year
         * @param month
         * @param day
         * @param hour
         * @param minute
         * @param total
         */
        void onDateSelect(int year, int month, int day, int hour, int minute, String total);
    }
}
