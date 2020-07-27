package com.zjf.behaviordemo;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.NestedScrollingParent3;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by junfengzhou on 2020/7/26.
 */
public class NestedFrameLayout extends FrameLayout implements NestedScrollingParent3 {

    private static final String TAG = "NestedFrameLayout";

    private View mHeader;
    private RecyclerView mRecyclerView;
    private int mScrollRange;

    public NestedFrameLayout(@NonNull Context context) {
        this(context, null, 0);
    }

    public NestedFrameLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public NestedFrameLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        mHeader = findViewById(R.id.header);
        mRecyclerView = findViewById(R.id.recycleView);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);

        RecyclerView recyclerView = mRecyclerView;
        View header = mHeader;
        if (recyclerView != null && header != null) {
            int scrollRange = header.getHeight();
            top = scrollRange + ((LayoutParams) recyclerView.getLayoutParams()).topMargin;
            recyclerView.layout(recyclerView.getLeft(), top, recyclerView.getRight(), top + recyclerView.getHeight());
            mScrollRange = scrollRange;
        }
    }

    @Override
    public boolean onStartNestedScroll(@NonNull View child, @NonNull View target, int axes, int type) {
        Log.e(TAG, "onStartNestedScroll() called with: child = [" + child.getClass().getSimpleName() + "], target = [" + target.getClass().getSimpleName() + "], axes = [" + axes + "], type = [" + type + "]");
        return (ViewCompat.SCROLL_AXIS_VERTICAL & axes) != 0;
    }

    @Override
    public void onNestedScrollAccepted(@NonNull View child, @NonNull View target, int axes, int type) {

            Log.w(TAG, "onNestedScrollAccepted() called with: child = [" + child.getClass().getSimpleName() + "], target = [" + target.getClass().getSimpleName() + "], axes = [" + axes + "], type = [" + type + "]");
    }

    @Override
    public void onStopNestedScroll(@NonNull View target, int type) {

            Log.d(TAG, "onStopNestedScroll() called with: target = [" + target.getClass().getSimpleName() + "], type = [" + type + "]");
    }

    @Override
    public void onNestedScroll(@NonNull View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed, int type, @NonNull int[] consumed) {

        onNestedScrollInner(target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, type, consumed);
    }

    @Override
    public void onNestedScroll(@NonNull View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed, int type) {

        onNestedScrollInner(target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, type, null);
    }

    private void onNestedScrollInner(View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed, int type, int[] consumed) {

        if (dyUnconsumed != 0) {

            scrollContent(dyUnconsumed, consumed);
        }

    }

    @Override
    public void onNestedPreScroll(@NonNull View target, int dx, int dy, @NonNull int[] consumed, int type) {

        //上滑
        if (dy > 0) {

            scrollContent(dy, consumed);
        }
    }

    private void scrollContent(int dy, int[] consumed) {
        int oldScrollY = getScrollY();
        int scrollRange = mScrollRange;
        int scrollDistance = oldScrollY + dy;
        if (scrollDistance > scrollRange) {

            scrollDistance = scrollRange;

        } else if (scrollDistance < 0) {

            scrollDistance = 0;
        }

        scrollTo(0, scrollDistance);

        if (consumed != null) {

            consumed[1] = getScrollY() - oldScrollY;
        }
    }

    @Override
    protected int computeVerticalScrollRange() {

        return mHeader != null && mRecyclerView != null ? mHeader.getHeight() + mRecyclerView.getHeight() : super.computeVerticalScrollRange();
    }
}
