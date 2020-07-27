package com.zjf.behaviordemo.behavior;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.View;

import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.zjf.behaviordemo.R;

import java.lang.ref.WeakReference;

/**
 * Created by zjf on 2019-04-22.
 */
public class TGRecycleViewBehavior extends TGBaseBehavior<RecyclerView> {

    private static final String TAG = "TGRecycleViewBehavior";

    private boolean isFling;
    private WeakReference<View> mDependencyViewRef;


    public TGRecycleViewBehavior(Context context, AttributeSet attrs) {

        super(context, attrs);
    }


    @Override
    public boolean layoutDependsOn(@NonNull CoordinatorLayout parent, @NonNull RecyclerView child, @NonNull View dependency) {

        if (dependency.getId() == R.id.header) {

            mDependencyViewRef = new WeakReference<>(dependency);
            return true;
        }

        return false;
    }


    @Override
    public boolean onDependentViewChanged(@NonNull CoordinatorLayout parent, @NonNull RecyclerView child, @NonNull View dependency) {

        View dependencyView = mDependencyViewRef.get();
        if (dependencyView == null) {
            return false;
        }

        float y = dependencyView.getTranslationY() + dependencyView.getHeight();

        int dependencyViewOffsetRange = getViewOffsetRange(dependencyView);
        if (y < dependencyViewOffsetRange) {

            y = dependencyViewOffsetRange;
        }

        child.setY(y);
        return true;
    }

    @Override
    public boolean onLayoutChild(@NonNull CoordinatorLayout parent, @NonNull RecyclerView child, int layoutDirection) {
        View dependencyView = mDependencyViewRef.get();
        if (dependencyView == null) {
            return false;
        }

        parent.onLayoutChild(child, layoutDirection);
        ViewCompat.offsetTopAndBottom(child, dependencyView.getBottom() - dependencyView.getTop());
        return true;
    }

    @Override
    public boolean onStartNestedScroll(@NonNull CoordinatorLayout coordinatorLayout, @NonNull RecyclerView child,
                                       @NonNull View directTargetChild, @NonNull View target, int axes, int type) {

        boolean isScrollVertical = (axes & ViewCompat.SCROLL_AXIS_VERTICAL) != 0;

        return isScrollVertical && canScrollView(mDependencyViewRef.get());
    }

    @Override
    public void onNestedScrollAccepted(@NonNull CoordinatorLayout coordinatorLayout, @NonNull RecyclerView child,
                                       @NonNull View directTargetChild, @NonNull View target, int axes, int type) {

        abortOverScroller();

        cancelOffsetAnimator();

        isFling = false;
    }


    @Override
    public void onNestedPreScroll(@NonNull CoordinatorLayout coordinatorLayout, @NonNull RecyclerView child,
                                  @NonNull View target, int dx, int dy, @NonNull int[] consumed, int type) {

        View dependencyView = mDependencyViewRef.get();
        if (dependencyView == null) {
            return;
        }

        if (dy > 0) {

            consumed[1] = (int) scroll(dependencyView, dy, getViewOffsetRange(dependencyView), 0);
        }

    }

    @Override
    public void onNestedScroll(@NonNull CoordinatorLayout coordinatorLayout, @NonNull RecyclerView child,
                               @NonNull View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed, int type) {

        View dependencyView = mDependencyViewRef.get();
        if (dependencyView == null) {
            return;
        }

        //向下滑动
        if (dyUnconsumed < 0) {
            scroll(dependencyView, dyUnconsumed, getViewOffsetRange(dependencyView), 0);
        }
    }


    @Override
    public boolean onNestedFling(@NonNull CoordinatorLayout coordinatorLayout, @NonNull RecyclerView child,
                                 @NonNull View target, float velocityX, float velocityY, boolean consumed) {

        isFling = true;
        return false;
    }

    @Override
    public boolean onNestedPreFling(@NonNull CoordinatorLayout coordinatorLayout, @NonNull RecyclerView child,
                                    @NonNull View target, float velocityX, float velocityY) {

        View dependencyView = mDependencyViewRef.get();
        if (dependencyView == null) {
            return false;
        }

        //向上滑动
        int dependencyViewOffsetRange = getViewOffsetRange(dependencyView);
        if (dependencyView.getTranslationY() > dependencyViewOffsetRange) {

            isFling = true;
            fling(dependencyView, -velocityY, dependencyViewOffsetRange, 0);
            return true;
        }

        return false;

    }


    @Override
    public void onStopNestedScroll(@NonNull CoordinatorLayout coordinatorLayout, @NonNull RecyclerView child,
                                   @NonNull View target, int type) {

        View dependencyView = mDependencyViewRef.get();

        if (isFling || dependencyView == null) {
            return;
        }

        checkSnap(dependencyView);
    }

    private void checkSnap(View dependencyView) {

        float translationY = dependencyView.getTranslationY();
        float viewOffsetRange = getViewOffsetRange(dependencyView);
        float threshold = viewOffsetRange / 3.f;
        float target = translationY > threshold ? 0 : viewOffsetRange;
        float distanceRatio = Math.abs(translationY) / Math.abs(viewOffsetRange);
        int duration = (int) ((distanceRatio + 1) * 150);
        animateToOffset(dependencyView, target, duration);
    }

    private int mDependencyViewOffsetRange = 1;

    @Override
    protected int getViewOffsetRange(View view) {
        if (mDependencyViewOffsetRange == 1) {

            mDependencyViewOffsetRange = -view.getMeasuredHeight();
        }

        return mDependencyViewOffsetRange;
    }


    @Override
    protected void onFlingFished(View dependencyView) {

        isFling = false;
        checkSnap(dependencyView);
    }

}
