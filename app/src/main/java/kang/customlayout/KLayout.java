package kang.customlayout;

import android.content.Context;
import android.graphics.Point;
import android.text.Layout;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

public class KLayout extends ViewGroup {

    private final static String TAG = "KLayout";


    private final static int HOVER_LEFT = 0x000001;
    private final static int HOVER_RIGHT = 0x000002;
    private final static int VERTI_UP = 0x000003;
    private final static int VERTI_DOWN = 0x000004;

    private List<Point> originPos;

    private View mTarget;

    private View headerView;

    public KLayout(Context context) {
        super(context);
        initHeaderView();
    }

    public KLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        initHeaderView();
    }

    public KLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    private void initHeaderView() {
        if (headerView == null) {
            headerView = LayoutInflater.from(getContext()).inflate(R.layout.header_view, this, false);
        }
        addView(headerView);
    }

    public void setHeaderView(View view) {
        removeView(headerView);
        headerView = view;
        addView(headerView, 0);
        //addview match_parent can not work
        headerView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        invalidate();
    }


    /**
     * 计算控件的大小
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int measureWidth = measureWidth(widthMeasureSpec);
        int measureHeight = measureHeight(heightMeasureSpec);
        // 计算自定义的ViewGroup中所有子控件的大小
        measureChildren(widthMeasureSpec, heightMeasureSpec);
        /*measureViewChildren();*/
        // 设置自定义的控件MyViewGroup的大小
        setMeasuredDimension(measureWidth, measureHeight);
    }

    private int measureWidth(int pWidthMeasureSpec) {
        int result = 0;
        int widthMode = MeasureSpec.getMode(pWidthMeasureSpec);// 得到模式
        int widthSize = MeasureSpec.getSize(pWidthMeasureSpec);// 得到尺寸

        switch (widthMode) {
            /**
             * mode共有三种情况，取值分别为MeasureSpec.UNSPECIFIED, MeasureSpec.EXACTLY, 
             * MeasureSpec.AT_MOST。 
             *
             *
             * MeasureSpec.EXACTLY是精确尺寸， 
             * 当我们将控件的layout_width或layout_height指定为具体数值时如andorid 
             * :layout_width="50dip"，或者为FILL_PARENT是，都是控件大小已经确定的情况，都是精确尺寸。 
             *
             *
             * MeasureSpec.AT_MOST是最大尺寸， 
             * 当控件的layout_width或layout_height指定为WRAP_CONTENT时 
             * ，控件大小一般随着控件的子空间或内容进行变化，此时控件尺寸只要不超过父控件允许的最大尺寸即可 
             * 。因此，此时的mode是AT_MOST，size给出了父控件允许的最大尺寸。 
             *
             *
             * MeasureSpec.UNSPECIFIED是未指定尺寸，这种情况不多，一般都是父控件是AdapterView， 
             * 通过measure方法传入的模式。 
             */
            case MeasureSpec.AT_MOST:
            case MeasureSpec.EXACTLY:
                if (widthSize == LayoutParams.MATCH_PARENT) {
                    result = getScreenWidth();
                } else {
                    result = widthSize;
                }
                break;
        }
        return result;
    }

    private int measureHeight(int pHeightMeasureSpec) {
        int result = 0;

        int heightMode = MeasureSpec.getMode(pHeightMeasureSpec);
        int heightSize = MeasureSpec.getSize(pHeightMeasureSpec);

        switch (heightMode) {
            case MeasureSpec.AT_MOST:
            case MeasureSpec.EXACTLY:
                if (heightSize == LayoutParams.MATCH_PARENT) {
                    result = getScreenWidth();
                } else {
                    result = heightSize;
                }
                break;
        }
        return result;
    }

    /**
     * 覆写onLayout，其目的是为了指定视图的显示位置，方法执行的前后顺序是在onMeasure之后，因为视图肯定是只有知道大小的情况下，
     * 才能确定怎么摆放
     */
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        // 记录总高度
        int mTotalHeight = 0 - headerView.getMeasuredHeight();
        originPos = new ArrayList<>();
        // 遍历所有子视图
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {

            View childView = getChildAt(i);
            if (i == 1) {
                mTarget = childView;
            }
            // 获取在onMeasure中计算的视图尺寸
            int measureHeight = childView.getMeasuredHeight();
            int measuredWidth = childView.getMeasuredWidth();

            childView.layout(l, mTotalHeight, measuredWidth, mTotalHeight
                    + measureHeight);

            Point point = new Point(l, mTotalHeight);
            originPos.add(point);
            mTotalHeight += measureHeight;

        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {

        int action = ev.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                onTouchEvent(ev);
                return false;
            case MotionEvent.ACTION_MOVE:
                return true;


            default:
                return false;
        }
    }

    float preX;
    float preY;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float currentX = event.getX();
        float currentY = event.getY();
        int action = event.getActionMasked();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                Log.d(TAG, "KLayout DOWN");
                break;
            case MotionEvent.ACTION_MOVE:
                if (judgeMoveSlide(preX, preY, currentX, currentY) == VERTI_DOWN ||
                        judgeMoveSlide(preX, preY, currentX, currentY) == VERTI_UP) {
                    Log.d(TAG, "KLayout MOVING VERTI");
                    if (judgeMoveSlide(preX, preY, currentX, currentY) == VERTI_DOWN) {
                        mTarget.offsetTopAndBottom((int) (currentY - preY));
                        headerView.offsetTopAndBottom((int) (currentY - preY));
                        Log.d(TAG, "headview mtarget pos  x" + mTarget.getX() + "y   " + mTarget.getY());
                        Log.d(TAG, "headview pos  x" + headerView.getX() + "  y " + headerView.getY());
                    }
                } else {
                    Log.d(TAG, "KLayout MOVING HOVER");
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                Log.d(TAG, "KLayout UP OR CANCEL");
                headerView.offsetTopAndBottom(originPos.get(0).y - (int) headerView.getY());
                mTarget.offsetTopAndBottom(originPos.get(1).y - (int) mTarget.getY());
                break;
        }
        preX = currentX;
        preY = currentY;
        return true;
    }

    private int judgeMoveSlide(float preX, float preY, float curX, float curY) {
        if (Math.abs(preX - curX) > Math.abs(preY - curY)) {
            return curX - preX > 0 ? HOVER_RIGHT : HOVER_LEFT;
        } else {
            return curY - preY > 0 ? VERTI_DOWN : VERTI_UP;
        }
    }

/*        HOVER_RIGHT(1),
                HOVER_LEFT(2),
                VER_UP(3),
                VER_DOWN(4)
    */

    private int getScreenWidth() {
        WindowManager wm = (WindowManager) getContext()
                .getSystemService(Context.WINDOW_SERVICE);

        int width = wm.getDefaultDisplay().getWidth();
        return width;
    }
}