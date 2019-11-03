package com.andlau.swipecard;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.database.DataSetObserver;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.customview.widget.ViewDragHelper;

public class SwipeCardLayout extends ViewGroup {
    private ViewDragHelper mDragHelper;
    //最顶层页面，随着手指滑动
    private View topView;
    //卡片中心点
    private int centerX, centerY;
    //手指离开屏幕的判断
    private boolean isRelise;
    //加载数据的adapter
    private MyAdapter adapter;
    //可见的卡片页面
    private int showCards = 3;
    //随手指滑动 卡片旋转的角度
    private int ROTATION = 20;
    //左滑右滑判断
    private boolean swipeLeft = false;
    //已经删除的页面的数量
    private int deleteNum;
    //子view的行宽度,高度
    int childWidth, childHeight;

    public SwipeCardLayout(Context context) {
        this(context, null);
    }


    public SwipeCardLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SwipeCardLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mDragHelper = ViewDragHelper.create(this, 1.0f, new ViewDragHelper.Callback() {
                    @Override
                    public boolean tryCaptureView(View child, int pointerId) {
                        return child == topView;
                    }

                    @Override
                    public int clampViewPositionHorizontal(View changedView, int left, int dx) {
                        if (isRelise) {
                            isRelise = false;
                        }
                        if (topView != null) {
                            if (swipeLeft) {
                                topView.setRotation(-getCenterX(changedView) * ROTATION);
                            } else {
                                topView.setRotation(getCenterX(changedView) * ROTATION);
                            }

                        }
                        return left;
                    }

                    @Override
                    public int clampViewPositionVertical(View child, int top, int dy) {
                        return top;
                    }

                    @Override
                    public void onViewReleased(View releasedChild, float xvel, float yvel) {
                        int left = releasedChild.getLeft();
                        if (Math.abs(left) / 2f > (getWidth() * 0.2f)) {
                            if (releasedChild == topView) {
                                topView.animate().translationX(left < 0 ? -getWidth() : getWidth()).translationY(getHeight() / 2f).setListener(new AnimatorListenerAdapter() {
                                    @Override
                                    public void onAnimationEnd(Animator animation) {
                                        super.onAnimationEnd(animation);
                                        removeView(topView);
                                        deleteNum++;
                                        adapter.notifyDataSetChanged();
                                    }
                                });
                            }
                        } else {
                            isRelise = true;
                            //回弹
                            mDragHelper.settleCapturedViewAt(centerX - childWidth / 2, centerY - childHeight / 2);
                            invalidate();
                        }
                    }

                    @Override
                    public void onViewPositionChanged(View changedView, int left, int top, int dx,
                                                      int dy) {
                        super.onViewPositionChanged(changedView, left, top, dx, dy);
                        //当手指松开后对顶层卡片进行移动
                        if (changedView == topView && isRelise) {
                            //根据角度来对卡片旋转角度进行测算
                            if (swipeLeft) {
                                topView.setRotation(-getCenterX(changedView) * ROTATION);
                            } else {
                                topView.setRotation(getCenterX(changedView) * ROTATION);
                            }
                        }
                    }

                    @Override
                    public int getViewHorizontalDragRange(@NonNull View child) {
                        return 1;
                    }
                }

        );
        mDragHelper.setEdgeTrackingEnabled(ViewDragHelper.EDGE_LEFT);
    }

    private float getCenterX(View child) {
        if (child.getWidth() / 2 + child.getX() - centerX < 0) {
            swipeLeft = true;
        } else {
            swipeLeft = false;
        }
        float width = Math.abs(child.getWidth() / 2 + child.getX() - centerX);
        if (width > centerX) {
            width = centerX;
        }
        return width / centerX;
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        centerX = widthSize / 2;
        centerY = heightSize / 2;
        measureChildren(widthMeasureSpec, heightMeasureSpec);
        if (getChildCount() > 0) {
            View child = getChildAt(0);
            measureChild(child, widthMeasureSpec, heightMeasureSpec);
            MarginLayoutParams params = (MarginLayoutParams) child.getLayoutParams();
            childWidth = child.getMeasuredWidth() + params.leftMargin + params.rightMargin;
            childHeight = child.getMeasuredHeight() + params.topMargin + params.bottomMargin;
        }
        setMeasuredDimension(widthSize, heightSize);
    }

    @Override
    public void computeScroll() {
        if (mDragHelper.continueSettling(true)) {
            postInvalidate();
        }
    }

    @Override
    protected void onLayout(boolean b, int i, int i1, int i2, int i3) {

        if (getChildCount() > 0) {

            topView = getChildAt(getChildCount() - 1);

            for (int j = 0; j < getChildCount(); j++) {
                View view = getChildAt(j);
                view.layout(centerX - childWidth / 2, centerY - childHeight / 2,
                        centerX + childWidth / 2, centerY + childHeight / 2);
            }
        }
    }

    public void setAdapter(@NonNull MyAdapter adapter) {
        this.adapter = adapter;
        //初始化数据 你需要显示几个页面
        changeViews();
        adapter.registerDataSetObserver(new DataSetObserver() {

            @Override
            public void onChanged() {
                getMore();
            }

            @Override
            public void onInvalidated() {
                getMore();
            }
        });
    }

    public void getMore() {
        if (getChildCount() + deleteNum < adapter.getCount()) {
            View view = adapter.getView(getChildCount() + deleteNum,
                    getChildAt(getChildCount()), this);
            //后面加载进来数据都放在最底层
            addView(view, 0);
        }

    }

    /**
     * showCards 是你需要显示几张卡片，showCards-j是为了排列顺序
     * viewgroup是最先加进来的view是在最底层的，所以我为了让第一个加进来的放在最上层，用了这个
     * eg:显示3张页面 showCards = 3，先加载第四个页面（因为最底层还要有一个你看不到的页面）放在最底层，
     * 到最后j=3时 加载第一张页面数据，同时将它显示优先级设为最高addView(view,j);
     * deleteNum是你右滑删掉的页面数量
     */
    private void changeViews() {
        View view = null;
        for (int j = 0; j <= showCards; j++) {
            if (j + deleteNum < adapter.getCount()) {
                view = adapter.getView(showCards - j, getChildAt(j), this);
                addView(view, j);
            }
        }
    }

    private float xDistance, yDistance, xLast, yLast;

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        boolean touchEvent = mDragHelper.shouldInterceptTouchEvent(ev);
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                xDistance = yDistance = 0f;
                xLast = ev.getX();
                yLast = ev.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                final float curX = ev.getX();
                final float curY = ev.getY();
                xDistance = Math.abs(curX - xLast);
                yDistance = Math.abs(curY - yLast);
                if (xDistance > yDistance) {
                    return true;
                }
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                break;
        }
        return touchEvent;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mDragHelper.processTouchEvent(event);
        return true;
    }


    @Override
    public ViewGroup.LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new MarginLayoutParams(getContext(), attrs);
    }

    public SwipeCardLayout setShowCards(int showCards) {
        this.showCards = showCards;
        return this;
    }
}