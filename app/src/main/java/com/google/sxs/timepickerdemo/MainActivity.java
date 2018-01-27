package com.google.sxs.timepickerdemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.google.sxs.timepickerdemo.widget.RightTextView;
import com.google.sxs.timepickerdemo.widget.TimePicker;

/**
 * @author sxs
 * @date 2018/1/27
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener, TimePicker.OnTimeSelectListener {
    private RightTextView mTimeLayout;
    private TimePicker mTimePicker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mTimeLayout = findViewById(R.id.time_layout);
        mTimeLayout.setOnClickListener(this);
    }


    @Override
    public void onTimeSelect(int startHour, int startMinute, int endHour, int endMinute) {
        String start = (startHour < 10 ? "0" + startHour : startHour) + ":" + (startMinute < 10 ? "0" + startMinute : startMinute);
        String end = (endHour < 10 ? "0" + endHour : endHour) + ":" + (endMinute < 10 ? "0" + endMinute : endMinute);
        mTimeLayout.setRightText(start + "-" + end);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.time_layout:
                if (mTimePicker == null) {
                    mTimePicker = new TimePicker(this, findViewById(R.id.layout))
                            .setOnTimeSelectListener(this);
                }
                mTimePicker.attachView(view);
                break;
            default:
                break;
        }
    }
}
