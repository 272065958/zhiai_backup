package com.cjx.zhiai.component.wheel;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import com.cjx.zhiai.R;

import java.util.Calendar;
import java.util.HashSet;

public class WheelLayout extends LinearLayout {

    private static int START_YEAR = 1950, END_YEAR = 2015;

    WheelView yearView, monthView, dayView, hourView, minuteView;
    Context context;

    public enum TimeStyle {
        PAST, NOW, FUTURE
    }

    int textSize = 0;
    LayoutParams lp;
    boolean isTimeView = false, isRangeView = false;

    public WheelLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init();
    }

    public WheelLayout(Context context) {
        super(context);
        this.context = context;
        init();
    }

    private void init() {
        textSize = getResources().getDimensionPixelSize(R.dimen.text_size_normal);
    }

    HashSet<Integer> list_big, list_little;

    private int getDayCount(int month, int year) {
        if (list_big == null) {
            list_big = new HashSet<>();
            list_big.add(1);
            list_big.add(3);
            list_big.add(5);
            list_big.add(7);
            list_big.add(8);
            list_big.add(10);
            list_big.add(12);
            list_little = new HashSet<>();
            list_little.add(4);
            list_little.add(6);
            list_little.add(9);
            list_little.add(11);
        }
        if (list_big.contains(month)) {
            return 31;
        } else if (list_little.contains(month)) {
            return 30;
        } else {
            if ((year % 4 == 0 && year % 100 != 0) || year % 400 == 0)
                return 29;
            else
                return 28;
        }
    }

    /**
     * 设置默认日期
     */
    public void setDate(int year, int month, int day, int hour, TimeStyle style) {
        if (!isTimeView) {
            removeAllViews();
            isTimeView = true;
            isRangeView = false;
        } else {
            return;
        }
        initDateWheelView(year, month, day, hour, -1, style);
    }

    /**
     * 初始化滚动的控件
     */
    public void initDateWheelView(int year, int month, int day, int hour,
                                  int minute, TimeStyle style) {
        Calendar c = Calendar.getInstance();
        int y;
        switch (style) {
            case PAST: //设置只选过去的日期
                END_YEAR = c.get(Calendar.YEAR);
                START_YEAR = END_YEAR - 70;
                y = year - START_YEAR;
                break;
            case NOW: //设置可以选过去将来的日期
                START_YEAR = c.get(Calendar.YEAR) - 10;
                END_YEAR = START_YEAR + 20;
                y = 10;
                break;
            default:
            case FUTURE: //设置只选将来的日期
                START_YEAR = c.get(Calendar.YEAR);
                END_YEAR = START_YEAR + 20;
                y = 0;
                break;
        }
        if (lp == null) {
            lp = new LayoutParams(
                    android.view.ViewGroup.LayoutParams.MATCH_PARENT,
                    android.view.ViewGroup.LayoutParams.WRAP_CONTENT, 1);
        }
        if (year > 0) {
            if (yearView == null) {
                yearView = new WheelView(context);
                yearView.TEXT_SIZE = textSize;
                yearView.setAdapter(new NumericWheelAdapter(START_YEAR,
                        END_YEAR));// 设置"年"的显示数据
                yearView.setCyclic(true);// 可循环滚动
                yearView.setLabel("年");// 添加文字
                yearView.setCurrentItem(y);
                yearView.setLayoutParams(lp);
                yearView.addChangingListener(new WheelView.OnWheelChangedListener() {
                    @Override
                    public void onChanged(WheelView wheel, int oldValue,
                                          int newValue) {
                        int year_num = newValue + START_YEAR;
                        // 判断大小月及是否闰年,用来确定"日"的数据
                        dayView.setAdapter(new NumericWheelAdapter(1,
                                getDayCount(monthView.getCurrentItem() + 1,
                                        year_num)));
                    }
                });
            }
            addView(yearView);
        }
        if (month > 0) {
            if (monthView == null) {
                monthView = new WheelView(context);
                monthView.TEXT_SIZE = textSize;
                monthView.setAdapter(new NumericWheelAdapter(1, 12));
                monthView.setCyclic(true);
                monthView.setLabel("月");
                monthView.setCurrentItem(month - 1);
                monthView.setLayoutParams(lp);
                monthView.addChangingListener(new WheelView.OnWheelChangedListener() {
                    @Override
                    public void onChanged(WheelView wheel, int oldValue,
                                          int newValue) {
                        int month_num = newValue + 1;
                        dayView.setAdapter(new NumericWheelAdapter(1,
                                getDayCount(month_num,
                                        yearView.getCurrentItem() + START_YEAR)));
                    }
                });
            }
            addView(monthView);
        }
        if (day > 0) {
            if (dayView == null) {
                dayView = new WheelView(context);
                dayView.TEXT_SIZE = textSize;
                dayView.setCyclic(true);
                dayView.setAdapter(new NumericWheelAdapter(1, getDayCount(
                        month, year)));
                dayView.setLabel("日");
                dayView.setCurrentItem(day - 1);
                dayView.setLayoutParams(lp);
            }
            addView(dayView);
        }
        if (hour > -1) {
            if (hourView == null) {
                hourView = new WheelView(context);
                hourView.TEXT_SIZE = textSize;
                hourView.setAdapter(new NumericWheelAdapter(0, 23));
                hourView.setCyclic(true);
                hourView.setLabel("时");
                hourView.setCurrentItem(hour);
                hourView.setLayoutParams(lp);
            }
            addView(hourView);
        }
        if (minute > -1) {
            if (minuteView == null) {
                minuteView = new WheelView(context);
                minuteView.TEXT_SIZE = textSize;
                minuteView.setAdapter(new NumericWheelAdapter(0, 59));
                minuteView.setCyclic(true);
                minuteView.setLabel("分");
                minuteView.setCurrentItem(minute);
                minuteView.setLayoutParams(lp);
            }
            addView(minuteView);
        }
    }

    /**
     * 获取当前设置的时间
     *
     * @return
     */
    public String getTime() {
        StringBuilder sb = new StringBuilder();
        if (yearView != null) {
            int month = monthView.getCurrentItem() + 1;
            int day = dayView.getCurrentItem() + 1;
            sb.append((yearView.getCurrentItem() + START_YEAR));
            sb.append("-");
            if(month < 10){
                sb.append("0");
            }
            sb.append(month);
            sb.append("-");
            if(day < 10){
                sb.append("0");
            }
            sb.append(day);
        }
        if (hourView != null) {
            if (sb.length() > 0) {
                sb.append(" ");
            }
            int hour = hourView.getCurrentItem();
            sb.append(hour < 10 ? ("0" + hour) : hour);
            sb.append(":");
            if (minuteView != null) {
                sb.append(minuteView.getCurrentItem());
            } else {
                sb.append("00");
            }
        }
        return sb.toString();
    }

//	public void setRange(int from, int to, String auto, String unit) {
//		if (!isRangeView) {
//			removeAllViews();
//			isTimeView = false;
//			isRangeView = true;
//		} else {
//			return;
//		}
//		if (lp == null) {
//			lp = new LayoutParams(
//					android.view.ViewGroup.LayoutParams.MATCH_PARENT,
//					android.view.ViewGroup.LayoutParams.WRAP_CONTENT, 1);
//		}
//		if (fromView == null) {
//			fromView = new WheelView(context);
//			fromView.TEXT_SIZE = textSize;
//			fromView.setAdapter(new RangeWheelAdapter(from, to, auto, unit));
//			fromView.setCyclic(true);
//			fromView.setCurrentItem(0);
//			fromView.setLayoutParams(lp);
//		}
//		addView(fromView);
//
//		if (unitView == null) {
//			LayoutParams llp = new LayoutParams(
//					android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
//					android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
//			unitView = new WheelView(context);
//			unitView.setCanScroll(false);
//			unitView.TEXT_SIZE = textSize;
//			unitView.setAdapter(new StringWheelAdapter(new String[] { "到" }));
//			unitView.setCurrentItem(0);
//			unitView.setLayoutParams(llp);
//		}
//		addView(unitView);
//
//		if (toView == null) {
//			toView = new WheelView(context);
//			toView.TEXT_SIZE = textSize;
//			toView.setCyclic(true);
//			toView.setAdapter(new RangeWheelAdapter(from, to, auto, unit));
//			toView.setCurrentItem(0);
//			toView.setLayoutParams(lp);
//		}
//		addView(toView);
//	}
//
//	public int[] getRange() {
//		int[] rang = new int[2];
//		rang[0] = fromView.getCurrentItem();
//		rang[1] = toView.getCurrentItem();
//		return rang;
//	}
//
//	public boolean isTimeView(){
//		return isTimeView;
//	}
}
