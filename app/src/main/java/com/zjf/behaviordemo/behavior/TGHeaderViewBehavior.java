package com.zjf.behaviordemo.behavior;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;

/**
 * Created by zjf on 2019-04-22.
 */
public class TGHeaderViewBehavior extends TGBaseBehavior<View> {

    private static final String TAG = "TGHeaderViewBehavior";

    private int mTouchSlop;


    public TGHeaderViewBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);

        init(context);
    }

    private void init(Context context) {

        ViewConfiguration viewConfiguration = ViewConfiguration.get(context);
        mTouchSlop = viewConfiguration.getScaledTouchSlop();
    }

    private int mDownY;
    private boolean isBeingDragged;
    private int mActivePointerId = -1;

    @Override
    public boolean onInterceptTouchEvent(@NonNull CoordinatorLayout parent, @NonNull View child, @NonNull MotionEvent ev) {

        if (ev.getActionMasked() == MotionEvent.ACTION_MOVE && isBeingDragged) {

            return true;
        }

        int activePointId;

        switch (ev.getActionMasked()) {

            case MotionEvent.ACTION_DOWN:

                isBeingDragged = false;

                if (parent.isPointInChildBounds(child, (int) ev.getX(), (int) ev.getY()) && canScrollView(child)) {

                    mActivePointerId = ev.getPointerId(0);
                    mDownY = (int) ev.getY();

                    Log.e(TAG, "onInterceptTouchEvent: ACTION_DOWN " + mActivePointerId);
                }
                break;
            case MotionEvent.ACTION_MOVE:

                activePointId = mActivePointerId;
                Log.w(TAG, "onInterceptTouchEvent: ACTION_MOVE " + activePointId);
                if (activePointId == -1) {

                    return false;
                }

                int pointerIndex = ev.findPointerIndex(activePointId);
                if (pointerIndex == -1) {

                    return false;
                }

                int y = (int) ev.getY(pointerIndex);
                if (!isBeingDragged && Math.abs(mDownY - y) > mTouchSlop) {

                    isBeingDragged = true;
                    mDownY = y;
                }

                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:

                mActivePointerId = -1;
                mDownY = 0;
                isBeingDragged = false;
                break;

        }

        return isBeingDragged;
    }

    @Override
    public boolean onTouchEvent(@NonNull CoordinatorLayout parent, @NonNull View child, @NonNull MotionEvent ev) {

        int activePointId;
        boolean isPointAvailable = false;
        switch (ev.getActionMasked()) {

            case MotionEvent.ACTION_DOWN:

                isBeingDragged = false;

                isPointAvailable = parent.isPointInChildBounds(child, (int) ev.getX(), (int) ev.getY()) && canScrollView(child);
                if (isPointAvailable) {

                    mActivePointerId = ev.getPointerId(0);
                    mDownY = (int) ev.getY();
                }
                break;
            case MotionEvent.ACTION_MOVE:

                activePointId = mActivePointerId;

                if (activePointId == -1) {
                    return false;
                }

                int pointerIndex = ev.findPointerIndex(activePointId);
                if (pointerIndex == -1) {
                    return false;
                }

                int y = (int) ev.getY(pointerIndex);
                int deltaY = mDownY - y;
                if (!isBeingDragged && Math.abs(deltaY) > mTouchSlop) {

                    isBeingDragged = true;

                    if (deltaY > 0) {
                        deltaY -= mTouchSlop;
                    } else {
                        deltaY += mTouchSlop;
                    }
                }

                if (isBeingDragged) {

                    scroll(child, deltaY, getViewOffsetRange(child), 0);
                    mDownY = (int) ev.getY(activePointId);
                }

                break;
            case MotionEvent.ACTION_UP:

                isBeingDragged = false;
                mActivePointerId = -1;
                mDownY = 0;
                break;

        }

        return isPointAvailable;
    }

    private int mDependencyViewOffsetRange = 1;

    @Override
    protected int getViewOffsetRange(View view) {

        if (mDependencyViewOffsetRange == 1) {

            mDependencyViewOffsetRange = -view.getMeasuredHeight();
        }

        return mDependencyViewOffsetRange;
    }
}
