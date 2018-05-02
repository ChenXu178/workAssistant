package com.chenxu.workassistant.widget;

import android.animation.ArgbEvaluator;
import android.animation.FloatEvaluator;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

/**
 * 自定义SlideMenu控件 实现侧滑效果
 * 为了避免自己去实现onMeasure和onLayout方法 我们继承FrameLayout,系统已经实现好了宽高的测量和位置的摆放
 * 为什么不使用LinearLayout和RelativeLayout,因为FrameLayout代码简洁
 */

public class SlideMenu extends FrameLayout {

    private static final String TAG = "SlideMenu";
    // 通过它来进行View拖拽的实现
    private ViewDragHelper mViewDragHelper;
    private View mMenuView;
    private View mMainView;
    // mainView的拖拽范围
    private int dragRange;
    private int mMainWidth;
    private int mMenuWidth;
    private FloatEvaluator mFloatEvaluator;
    private ArgbEvaluator mArgbEvaluator;
    private OnSlideListener mOnSlideListener;
    // 表示当前状态: 关闭
    private DragState mDragState = DragState.Close;

    public enum DragState{
        Open ,Close
    }

    public SlideMenu(Context context) {
        this(context , null);
    }

    public SlideMenu(Context context, AttributeSet attrs) {
        this(context, attrs , 0);
    }

    public SlideMenu(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initData();
    }

    private void initData(){
        // 父View、滑动敏感度、回调方法
        mViewDragHelper = ViewDragHelper.create(this , callback);
        // 浮点运算器
        mFloatEvaluator = new FloatEvaluator();
        // 颜色运算器
        mArgbEvaluator = new ArgbEvaluator();
    }

    /**
     * 该方法在ViewGroup将子View全部添加进来之后执行,但在onMeasure之前执行
     * 一般用来初始化子View的引用,但是还不能获取到子View的宽高
     */
    @Override
    protected void onFinishInflate() {
        mMenuView = getChildAt(0);
        mMainView = getChildAt(1);
        super.onFinishInflate();
    }

    /**
     * 当onMeasure方法执行完之后执行,在该方法中可以获取所有控件的宽高
     */
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        dragRange = (int) (getMeasuredWidth()*0.6f);
        mMainWidth = mMainView.getMeasuredWidth();
        mMenuWidth = mMenuView.getMeasuredWidth();
        super.onSizeChanged(w, h, oldw, oldh);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        // 让ViewDragHelper帮助我们判断是否应该拦截
        boolean result = mViewDragHelper.shouldInterceptTouchEvent(ev);
        return result;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // 让ViewDragHelper帮助我们处理触摸事件
        mViewDragHelper.processTouchEvent(event);
        return true;
    }

    ViewDragHelper.Callback callback = new ViewDragHelper.Callback() {
        /**
         * 判断是否需要捕获View的触摸事件
         * 这里的child都是当前触摸的View
         */
        @Override
        public boolean tryCaptureView(View child, int pointerId) {
            // 捕获mainView和menuView的触摸事件
            if(child == mMainView || child == mMenuView){
                return true;
            }
            return false;
        }

        /**
         * 当一个View被捕获触摸事件调用
         */
        @Override
        public void onViewCaptured(View capturedChild, int activePointerId) {
            Log.i(TAG , "onViewCaptured:" + activePointerId);
            super.onViewCaptured(capturedChild, activePointerId);
        }

        /**
         * 通过返回值判断滑动方向 只要大于0就可以正常水平垂直滑动
         */
        @Override
        public int getViewHorizontalDragRange(View child) {
            return 1;
        }

        /**
         * 用于捕获子View在水平方向上的移动
         * @param left:是ViewDragHelper帮我们计算好的View最新的left的值
         *            left = child.getLeft() + dx
         * @return 真正想要View的Left变成的值
         */
        @Override
        public int clampViewPositionHorizontal(View child, int left, int dx) {
            // 限制主界面移动
            if(child == mMainView){
                left = clampLeft(left);
            }
            return left;
        }

//        /**
//         * 用于捕获子View在垂直方向上的移动
//         */
//        @Override
//        public int clampViewPositionVertical(View child, int top, int dy) {
//            return top;
//        }

        /**
         * 当View移动的时候调用,可以获取到手指移动的距离
         */
        @Override
        public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {
            // 当手指在menuView移动的时候,让mainView进行一个伴随的移动,但是menuView不动
            Log.i(TAG , "left: " + left + "dx: " + dx);
            // menuView固定住
            mMenuView.layout(0 ,0 , mMenuWidth , mMenuView.getBottom());

            if(changedView == mMenuView){
                int newLeft = mMainView.getLeft() + dx;
                // 限制newLeft否则,在menuView上滑动mainView无限制
                newLeft = clampLeft(newLeft);
                // 移动mainView
                mMainView.layout(newLeft ,0 , newLeft + mMainWidth , mMainView.getBottom());
            }

            // 增加伴随动画
            float fraction = mMainView.getLeft() * 1.0f / dragRange;
            // 得到百分比
            Log.i(TAG , "fraction" + fraction);
            // 执行动画效果
            execAnim(fraction);
            // 回调监听器的方法
            if(fraction == 0f && mDragState !=DragState.Close){
                mDragState =DragState.Close;
                // 说明关闭了
                if(mOnSlideListener != null){
                    mOnSlideListener.onClose();
                }
                // 说明打开了
            }else if(fraction == 1f && mDragState !=DragState.Open){
                mDragState =DragState.Open;
                if(mOnSlideListener != null){
                    mOnSlideListener.onOpen();
                }
            }

            // 说明在拖拽中
            if(mOnSlideListener != null){
                mOnSlideListener.onDraging(fraction);
            }
            super.onViewPositionChanged(changedView, left, top, dx, dy);
        }

        /**
         * 当手指从View上抬起的时候执行
         */
        @Override
        public void onViewReleased(View releasedChild, float xvel, float yvel) {
            // 判断mainView的left是否是大于dragRange的一半
            if(mMainView.getLeft() > dragRange / 2){
                // mainView平滑滚动到右边
                mViewDragHelper.smoothSlideViewTo(mMainView , dragRange , 0 );
            }else{
                // mainView平滑滚动到左边
                mViewDragHelper.smoothSlideViewTo(mMainView , 0 , 0 );
            }
            // 刷新操作
            ViewCompat.postInvalidateOnAnimation(SlideMenu.this);

            super.onViewReleased(releasedChild, xvel, yvel);
        }
    };

    /**
     * 根据拖拽的百分比来进行伴随动画效果
     */
    private void execAnim(float fraction) {
        // mainView的缩放
        mMainView.setScaleX(mFloatEvaluator.evaluate(fraction , 1.0f , 0.8f));
        mMainView.setScaleY(mFloatEvaluator.evaluate(fraction , 1.0f , 0.8f));

        // menuView的缩放
        mMenuView.setScaleX(mFloatEvaluator.evaluate(fraction , 0.4f , 1f));
        mMenuView.setScaleY(mFloatEvaluator.evaluate(fraction , 0.4f , 1f));

        // menuView 水平方向的平移
        mMenuView.setTranslationX(mFloatEvaluator.evaluate(fraction ,-mMenuWidth /2, 0));

        // 设置SlideMenu的颜色遮罩
        getBackground();
        if(getBackground() != null){
            // 由黑色变为透明的遮罩效果
            int color = (int) mArgbEvaluator.evaluate(fraction, Color.BLACK, Color.TRANSPARENT);
            getBackground().setColorFilter(color, PorterDuff.Mode.DARKEN);
        }
    }

    @Override
    public void computeScroll() {
        // 判断动画有没有结束,如果为true表示没有结束
        if(mViewDragHelper.continueSettling(true)){
            // 刷新操作
            ViewCompat.postInvalidateOnAnimation(SlideMenu.this);
        }
        super.computeScroll();
    }

    /**
     * 修正左边距
     */
    private int clampLeft(int left) {
        if(left > dragRange){
            left = dragRange;
        }else if(left < 0){
            left = 0;
        }
        return left;
    }

    public interface OnSlideListener{
        void onOpen();
        void onClose();
        void onDraging(float fraction);
    }

    public void setOnSlideListener(OnSlideListener mOnSlideListener){
        this.mOnSlideListener = mOnSlideListener;
    }
}