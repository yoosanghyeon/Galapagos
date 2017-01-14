package com.galapagos.galapagos;

import android.animation.Animator;
import android.content.Context;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewPropertyAnimator;
import android.view.animation.Interpolator;

public class QuickReturnFooterBehavior extends CoordinatorLayout.Behavior<View> {


    private static final Interpolator INTERPOLATOR = new FastOutSlowInInterpolator();

    private int mDySinceDirectionChange;
    private boolean mIsShowing;
    private boolean mIsHiding;

    public QuickReturnFooterBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


    @Override
    public boolean onStartNestedScroll(CoordinatorLayout coordinatorLayout, View child, View directTargetChild, View target, int nestedScrollAxes) {
/*
        return nestedScrollAxes == ViewCompat.SCROLL_AXIS_VERTICAL
                || super.onStartNestedScroll(coordinatorLayout,child, directTargetChild, target, nestedScrollAxes);
        */

        return nestedScrollAxes == ViewCompat.SCROLL_AXIS_VERTICAL;

    }


    @Override
    public void onNestedPreScroll(CoordinatorLayout coordinatorLayout, View child, View target, int dx, int dy, int[] consumed) {


        // 스크롤방향이 바뀌는경우 모든동작을 취소하고 Y값을 다시 처음부터 시작한다
        if (dy > 0 && mDySinceDirectionChange < 0
                || dy < 0 && mDySinceDirectionChange > 0) {

            child.animate().cancel();
            mDySinceDirectionChange = 0;
        }

        mDySinceDirectionChange += dy;

        if (mDySinceDirectionChange > child.getHeight()
                && child.getVisibility() == View.VISIBLE
                && !mIsHiding) {
            hideView(child);
        } else if (mDySinceDirectionChange < 0
                && child.getVisibility() == View.GONE
                && !mIsShowing) {
            showView(child);
        }
    }


    /**
     * View를 숨긴다
     * <p/>
     * 아래로 슬라이딩하는 애니메이션.
     * 애니메이션 종료후 View를 없앤다.
     *
     * @param view The quick return view
     */
    private void hideView(final View view) {
        mIsHiding = true;
        ViewPropertyAnimator animator = view.animate()
                .translationY(view.getHeight())
                .setInterpolator(INTERPOLATOR)
                .setDuration(200);

        animator.setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                mIsHiding = false;
                view.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationCancel(Animator animator) {
                // 취소되면 다시 보여줌
                mIsHiding = false;
                if (!mIsShowing) {
                    showView(view);
                }
            }

            @Override
            public void onAnimationRepeat(Animator animator) {
            }
        });

        animator.start();
    }

    /**
     * View를 보여준다.
     * <p/>
     * 아래서 위로 슬라이딩 애니메이션.
     * 애니메이션을 시작하기전 View를 보여준다.
     *
     * @param view The quick return view
     */
    private void showView(final View view) {
        mIsShowing = true;
        ViewPropertyAnimator animator = view.animate()
                .translationY(0)
                .setInterpolator(INTERPOLATOR)
                .setDuration(200);

        animator.setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
                view.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                mIsShowing = false;
            }

            @Override
            public void onAnimationCancel(Animator animator) {
                // 취소되면 다시 숨김
                mIsShowing = false;
                if (!mIsHiding) {
                    hideView(view);
                }
            }

            @Override
            public void onAnimationRepeat(Animator animator) {
            }
        });

        animator.start();
    }
}