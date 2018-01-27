package com.google.sxs.timepickerdemo.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.sxs.timepickerdemo.R;


/**
 * @author sxs
 * @date 2018/1/16
 */

public class RightTextView extends LinearLayout {

    private TextView mLeftTv;
    private TextView mRightTv;

    public RightTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.right_text_layout, this, true);
        mLeftTv = findViewById(R.id.left_tv);
        mRightTv = findViewById(R.id.right_tv);
        if (attrs != null) {
            TypedArray a = context.obtainStyledAttributes(attrs,
                    R.styleable.RightTextView);
            String leftText = a.getString(R.styleable.RightTextView_m_left_tv_text);
            mLeftTv.setText(leftText);
            String hint = a.getString(R.styleable.RightTextView_m_hint);
            mRightTv.setHint(hint);
            String rightText = a.getString(R.styleable.RightTextView_m_right_tv_text);
            mRightTv.setText(rightText);
            a.recycle();
        }
    }

    public void setRightText(CharSequence rightText) {
        if (rightText != null) {
            mRightTv.setText(rightText);
        }
    }

    public void setRightText(@StringRes int id) {
        if (mRightTv != null) {
            mRightTv.setText(id);
        }
    }
}
