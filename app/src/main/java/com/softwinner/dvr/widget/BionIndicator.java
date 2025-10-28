package com.softwinner.dvr.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.CornerPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.softwinner.dvr.R;

import java.util.List;


public class BionIndicator extends LinearLayout {
    private static final String TAG = "BionIndicator";

    /**
     * 默认tab数目
     */
    private static final int DEF_TAB_COUNT = 3;

    /**
     * 默认文字正常时的颜色
     */
    private static final int DEF_TEXT_COLOR_NORMAL = Color.parseColor("#000000");

    /**
     * 默认文字选中时的颜色
     */
    private static final int DEF_TEXT_COLOR_SELECTED = Color.parseColor("#FF0000");

    /**
     * 默认文字的大小
     */
    private static final int DEF_TEXT_SIZE = 16;

    /**
     * 默认指示器的颜色
     */
    private static final int DEF_INDICATOR_COLOR = Color.parseColor("#f29b76");

    /*
     * Indicator的一些风格
     */
    /**
     * Indicator风格：直线
     */
    private static final int INDICATOR_STYLE_LINE = 1; // 直线

    /**
     * Indicator风格：方形
     */
    private static final int INDICATOR_STYLE_SQUARE = 2; // 方形

    /**
     * Indicator风格：三角形
     */
    private static final int INDICATOR_STYLE_TRIANGLE = 3; // 三角形

    // tab数目
    private int mTabCount = DEF_TAB_COUNT;

    // 文字正常时颜色
    private int mTextColorNormal = DEF_TEXT_COLOR_NORMAL;

    // 文字被选中时颜色
    private int mTextColorSelected = DEF_TEXT_COLOR_SELECTED;

    // 文字大小
    private int mTextSize = DEF_TEXT_SIZE;

    // 指示器类型
    private int mIndicatorStyle = INDICATOR_STYLE_LINE;

    // 指示器颜色
    private int mIndicatorColor = DEF_INDICATOR_COLOR;

    // tab标题
    private List<String> mTabTitles;

    // 当前所处的位置
    private int mCurrPosition = 0;

    // 指示器高度
    private int mIndicatorHeight;

    // 指示器宽度
    private int mIndicatorWidth;

    // 矩形
    private Rect mRect;

    // 画笔
    private Paint mPaint;

    // 关联的ViewPager
    private ViewPager mViewPager;

    private Path mPath;

    // 手指滑动的距离
    private float mTransactionY;

    private float mInitTransactionY;

    private boolean isInit = false;


    public BionIndicator(Context context) {
        this(context, null);
    }

    public BionIndicator(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.BionIndicator);
        mTabCount = ta.getInt(R.styleable.BionIndicator_item_count, DEF_TAB_COUNT);
        mTextColorNormal = ta.getColor(R.styleable.BionIndicator_text_color_normal,
                DEF_TEXT_COLOR_NORMAL);
        mTextColorSelected = ta.getColor(R.styleable.BionIndicator_text_color_selected,
                DEF_TEXT_COLOR_SELECTED);
        mTextSize = ta.getDimensionPixelSize(R.styleable.BionIndicator_text_size, DEF_TEXT_SIZE);
        mIndicatorStyle = ta.getInt(R.styleable.BionIndicator_indicator_style,
                INDICATOR_STYLE_LINE);
        mIndicatorColor = ta.getColor(R.styleable.BionIndicator_indicator_color,
                DEF_INDICATOR_COLOR);
        ta.recycle();

        mPaint = new Paint();
        mPaint.setColor(mIndicatorColor);
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setPathEffect(new CornerPathEffect(3));
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        // 绘制指示器
        drawIndicator(canvas);

        super.dispatchDraw(canvas);
    }

    /**
     * 绘制指示器
     */
    private void drawIndicator(Canvas canvas) {
        // 保存canvas
        canvas.save();

        switch (mIndicatorStyle) {
            case INDICATOR_STYLE_LINE: {
                canvas.translate(0, mTransactionY);
                canvas.drawRect(mRect, mPaint);
                break;
            }

            case INDICATOR_STYLE_SQUARE: {

                break;
            }

            case INDICATOR_STYLE_TRIANGLE: {
//                canvas.translate(mTransactionX, 0);
                canvas.translate(getWidth(), mInitTransactionY + mTransactionY);
                canvas.drawPath(mPath, mPaint);
                break;
            }
        }

        canvas.restore();
    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        switch (mIndicatorStyle) {
            case INDICATOR_STYLE_LINE: { // 下划线指示器
                mTransactionY = 0;
                // 宽与tab item相同，高是tab item的1/10
                mIndicatorWidth = w / mTabCount;
                mIndicatorHeight = h / 10;
                mRect = new Rect(0, 0, mIndicatorWidth, mIndicatorHeight);
                break;
            }

            case INDICATOR_STYLE_TRIANGLE: {
                mTransactionY = 0;
                // 宽为tab item的1/6，高也是tab item的1/6
                mIndicatorHeight = h / mTabCount / 3;
                mIndicatorWidth = mIndicatorHeight * 2 / 5;
                mInitTransactionY = h / mTabCount / 2 - mIndicatorHeight / 2;
            }
        }

        initTabs();

    }

    /**
     * 初始化Tab
     */
    private void initTabs() {
        if (mTabTitles != null && mTabTitles.size() > 0 && !isInit) {
            mPath = new Path();
            mPath.moveTo(0f, 0f);
            mPath.lineTo(0f, mIndicatorHeight);
            mPath.lineTo(-mIndicatorWidth, mIndicatorHeight / 2f);
            mPath.close();


            if (getChildCount() != 0) {
                removeAllViews();
            }

            int index = 0;
            // 初始化Tab标题
            for (String title : mTabTitles) {
                addView(createTextView(title));
                if (index++ < mTabTitles.size() - 1) {
                    addView(createDivider());
                }
            }

            isInit = true;
            setHighlightTabTitleColor();
            setTabItemClickEvent();
        }
    }

    private View createDivider() {
        View view = new View(getContext());
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(LayoutParams
                .MATCH_PARENT, 1);
        view.setBackgroundColor(Color.parseColor("#ffffffff"));
        view.setLayoutParams(layoutParams);

        return view;
    }

    private void setTabItemClickEvent() {
        View view = null;
        for (int i = 0; i < getChildCount(); i++) {
            view = getChildAt(i);
            if (view instanceof TextView) {
                view.setOnClickListener(new TabCLickListener(i));
            }
        }
    }

    class TabCLickListener implements OnClickListener {
        int mIndex;

        TabCLickListener(int index) {
            mIndex = index;
        }

        @Override
        public void onClick(View v) {
            mViewPager.setCurrentItem(mIndex / 2);
        }
    }

    /**
     * 设置Tab标题
     *
     * @param tabTitles
     */
    public void setTabTitles(List<String> tabTitles) {
        mTabTitles = tabTitles;
        mTabCount = mTabTitles.size();
    }

    public void setViewPager(ViewPager viewPager, int pos) {
        mViewPager = viewPager;
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int
                    positionOffsetPixels) {
                scroll(position, positionOffset);
            }

            @Override
            public void onPageSelected(int position) {
                mCurrPosition = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        mViewPager.setCurrentItem(pos);
        mCurrPosition = pos;
    }

    /**
     * 滑动
     */
    private void scroll(int position, float offset) {
        int tabHeight = getHeight() / mTabCount;
        mTransactionY = tabHeight * offset + position * tabHeight;
        setHighlightTabTitleColor();
        invalidate();
    }

    /**
     * 创建tab标题
     *
     * @param text
     * @return
     */
    private TextView createTextView(String text) {
        TextView tv = new TextView(getContext());
        LayoutParams layoutParams = new LayoutParams(
                LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        layoutParams.height = getHeight() / mTabCount;
        tv.setGravity(Gravity.CENTER);
        tv.setTextColor(mTextColorNormal);
        tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, mTextSize);
        tv.setText(text);
        tv.setLayoutParams(layoutParams);

        return tv;
    }

    private void setHighlightTabTitleColor() {
        int mIndex = 0;
        View view = null;
        TextView tv = null;
        for (int i = 0; i < getChildCount(); i++) {
            view = getChildAt(i);
            if (view instanceof TextView) {
                tv = (TextView) view;
                if (mIndex++ == mCurrPosition) {
                    tv.setTextColor(mTextColorSelected);
                } else {
                    tv.setTextColor(mTextColorNormal);
                }
            }
        }
    }
}
