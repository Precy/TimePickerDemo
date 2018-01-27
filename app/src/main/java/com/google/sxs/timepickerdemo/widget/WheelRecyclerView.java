package com.google.sxs.timepickerdemo.widget;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Shader;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import com.google.sxs.timepickerdemo.R;

import java.util.ArrayList;
import java.util.List;

/**
 * @author sxs
 * @date 2018/1/24
 */

public class WheelRecyclerView extends RecyclerView {

    /**
     * 选项高度
     */
    private int mItemHeight = dp2px(50);
    /**
     * 处于中间的item为选中，在头尾需补充 offset个空白view，可显示的item数量=2*offset+1
     */
    private int mOffset = 2;
    private List<String> mDatas;
    private WheelAdapter mAdapter;
    private int mSelected;
    private LinearLayoutManager mLayoutManager;
    /**
     * 分割线的宽度
     */
    private float mDividerWidth;
    /**
     * 绘制分割线的paint
     */
    private Paint mPaint;

    private OnSelectListener mOnSelectListener;

    public WheelRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mDatas = new ArrayList<>();

        init();
    }

    @Override
    protected void onMeasure(int widthSpec, int heightSpec) {
        int height = (mOffset * 2 + 1) * mItemHeight;
        mDividerWidth = widthSpec;
        setMeasuredDimension(widthSpec, height);
    }

    private void init() {
        mLayoutManager = new LinearLayoutManager(getContext());
        setLayoutManager(mLayoutManager);
        addItemDecoration(new DividerItemDecoration());
        mAdapter = new WheelAdapter();
        setAdapter(mAdapter);
        addOnScrollListener(new OnWheelScrollListener());
    }

    public void setData(List<String> datas) {
        if (datas == null) {
            return;
        }
        mDatas.clear();
        mDatas.addAll(datas);
        mAdapter.notifyDataSetChanged();
        setSelect(0);
        scrollTo(0, 0);
    }

    public void setSelect(int position) {
        mSelected = position;
        mLayoutManager.scrollToPosition(mSelected);
    }

    public int getSelected() {
        return mSelected;
    }

    public void setOnSelectListener(OnSelectListener listener) {
        mOnSelectListener = listener;
    }

    private class OnWheelScrollListener extends OnScrollListener {

        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
            if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                //当控件停止滚动时，获取可视范围第一个item的位置，滚动调整控件以使选中的item刚好处于正中间
                int firstVisiblePos = mLayoutManager.findFirstVisibleItemPosition();
                if (firstVisiblePos == RecyclerView.NO_POSITION) {
                    return;
                }
                Rect rect = new Rect();
                mLayoutManager.findViewByPosition(firstVisiblePos).getHitRect(rect);
                if (Math.abs(rect.top) > mItemHeight / 2) {
                    smoothScrollBy(0, rect.bottom);
                    mSelected = firstVisiblePos + 1;
                } else {
                    smoothScrollBy(0, rect.top);
                    mSelected = firstVisiblePos;
                }
                if (mOnSelectListener != null) {
                    mOnSelectListener.onSelect(mSelected, mDatas.get(mSelected));
                }
            }
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
        }
    }

    private class DividerItemDecoration extends ItemDecoration {
        @Override
        public void onDrawOver(Canvas c, RecyclerView parent, State state) {
            //绘制分割线
            float startX = getMeasuredWidth() / 2 - mDividerWidth / 2;
            float topY = mItemHeight * mOffset;
            float endX = getMeasuredWidth() / 2 + mDividerWidth / 2;
            float bottomY = mItemHeight * (mOffset + 1);

            mPaint = new Paint();
            mPaint.setColor(ContextCompat.getColor(parent.getContext(), R.color.common_rgb_c));

            c.drawLine(startX, topY, endX, topY, mPaint);
            c.drawLine(startX, bottomY, endX, bottomY, mPaint);

            //上下透明渐变遮罩层
            Paint pTop = new Paint();
            int yHeight = mItemHeight * mOffset;
            LinearGradient lg = new LinearGradient(0, 0, 0, yHeight,
                    0xdfe5e5e5,
                    0x00000000, Shader.TileMode.CLAMP);
            pTop.setShader(lg);
            c.drawRect(startX, topY - yHeight, endX, topY, pTop);
            Paint pBottom = new Paint();
            LinearGradient lg2 = new LinearGradient(0, bottomY, 0, bottomY + yHeight,
                    0x00000000,
                    0xdfe5e5e5, Shader.TileMode.CLAMP);
            pBottom.setShader(lg2);
            c.drawRect(startX, bottomY, endX, bottomY + yHeight, pBottom);
        }
    }

    private class WheelAdapter extends Adapter<WheelAdapter.WheelHolder> {

        @Override
        public WheelHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            WheelHolder holder = new WheelHolder(LayoutInflater.from(getContext()).inflate(R.layout.item_wheel, parent, false));
            holder.name.getLayoutParams().height = mItemHeight;
            return holder;
        }

        @Override
        public void onBindViewHolder(WheelHolder holder, int position) {
            if (position < mOffset || position > mDatas.size() + mOffset - 1) {
                holder.name.setText("");
            } else {
                holder.name.setText(mDatas.get(position - mOffset));
            }

        }

        @Override
        public int getItemCount() {
            return mDatas.size() == 0 ? 0 : mDatas.size() + mOffset * 2;
        }

        class WheelHolder extends ViewHolder {

            TextView name;

            public WheelHolder(View itemView) {
                super(itemView);
                name = itemView.findViewById(R.id.tv_name);
            }
        }
    }

    public interface OnSelectListener {
        /**
         * 当前选中项
         *
         * @param position
         * @param data
         */
        void onSelect(int position, String data);
    }

    /**
     * dp转px
     *
     * @param dp
     */
    public static int dp2px(float dp) {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density + 0.5f);
    }
}
