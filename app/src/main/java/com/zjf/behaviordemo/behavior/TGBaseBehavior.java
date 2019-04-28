package com.zjf.behaviordemo.behavior;

import android.animation.ValueAnimator;
import android.content.Context;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.math.MathUtils;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.OverScroller;

/**
 * Created by zjf on 2019-04-22.
 */
public class TGBaseBehavior<T extends View> extends CoordinatorLayout.Behavior<T> {

    private OverScroller mOverScroller;

    public TGBaseBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * 滑动view
     *
     * @return 消耗的dy
     */
    public float scroll(View scrolledView, float dy, int minScrollOffset, int maxScrollOffset) {

        float consumed = 0;
        float translationY = scrolledView.getTranslationY();

        if (minScrollOffset != 0 && translationY >= minScrollOffset && translationY <= maxScrollOffset) {

            float newTranslationY = MathUtils.clamp(translationY - dy, minScrollOffset, maxScrollOffset);
            if (newTranslationY != translationY) {

                consumed = translationY - newTranslationY;
                scrolledView.setTranslationY(newTranslationY);
            }
        }

        return consumed;
    }

    private FlingRunnable mFlingRunnable;

    protected void fling(View view, float velocityY, int minOffset, int maxOffset) {

        if (mFlingRunnable != null) {

            view.removeCallbacks(mFlingRunnable);
            mFlingRunnable = null;
        }

        if (mOverScroller == null) {
            mOverScroller = new OverScroller(view.getContext());
        }

        mOverScroller.fling(0, (int) view.getTranslationY(), 0, Math.round(velocityY), 0, 0, minOffset, maxOffset);
        if (mOverScroller.computeScrollOffset()) {

            mFlingRunnable = new FlingRunnable(view);
            ViewCompat.postOnAnimation(view, mFlingRunnable);
        } else {

            onFlingFished(view);
        }

    }

    protected void abortOverScroller() {

        if (mOverScroller != null && !mOverScroller.isFinished()) {

            mOverScroller.abortAnimation();
        }
    }

    private ValueAnimator mOffsetAnimator;

    protected void animateToOffset(final View view, final float target, int duration) {

        float translationY = view.getTranslationY();
        if (translationY == target) {

            cancelOffsetAnimator();
            return;
        }

        if (mOffsetAnimator == null) {
            final int viewOffsetRange = getViewOffsetRange(view);
            mOffsetAnimator = new ValueAnimator();
            mOffsetAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
            mOffsetAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {

                    Object animatedValue = animation.getAnimatedValue();
                    float dy = view.getTranslationY() - (float) animatedValue;
                    scroll(view, dy, viewOffsetRange, 0);
                }
            });
        }

        mOffsetAnimator.setDuration(duration);
        mOffsetAnimator.setFloatValues(translationY, target);
        mOffsetAnimator.start();

    }

    protected void cancelOffsetAnimator() {
        if (mOffsetAnimator != null && mOffsetAnimator.isRunning()) {
            mOffsetAnimator.cancel();
        }
    }

    protected void onFlingFished(View dependencyView) {

    }

    protected boolean canScrollView(View view) {

        if (view == null) {
            return false;
        }

        return view.getTranslationY() >= getViewOffsetRange(view);
    }

    protected int getViewOffsetRange(View view) {

        return view == null ? 0 : view.getMeasuredHeight();
    }

    private class FlingRunnable implements Runnable {

        private View mView;

        FlingRunnable(View view) {
            mView = view;
        }

        @Override
        public void run() {
            if (mOverScroller.computeScrollOffset()) {

                int dy = (int) (mView.getTranslationY() - mOverScroller.getCurrY());
                scroll(mView, dy, getViewOffsetRange(mView), 0);
                ViewCompat.postOnAnimation(mView, mFlingRunnable);
            } else {

                onFlingFished(mView);
            }
        }

    }
}
