package com.cy.loopviewpageradapter;

import android.os.Looper;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.widget.ViewPager2;

import java.util.Timer;
import java.util.TimerTask;

/**
 * @Description:
 * @Author: cy
 * @CreateDate: 2020/12/28 16:30
 * @UpdateUser:
 * @UpdateDate: 2020/12/28 16:30
 * @UpdateRemark:
 * @Version:
 */
public abstract class ViewPager2LoopAdapter<T> extends ViewPager2Adapter<T> {
    private boolean loopAuto = true;
    private ViewPager2 viewPager2;
    private IIndicatorView indicatorView;
    private Timer timer;
    private TimerTask timerTask;
    private android.os.Handler handler;
    private long periodLoop = 3000;
    private boolean isLoopStarted = false;

    public ViewPager2LoopAdapter(final ViewPager2 viewPager2, final IIndicatorView indicatorView) {
        this.viewPager2 = viewPager2;
        this.indicatorView = indicatorView;
        handler = new android.os.Handler(Looper.getMainLooper());
        viewPager2.addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() {
            @Override
            public void onViewAttachedToWindow(View v) {

            }

            @Override
            public void onViewDetachedFromWindow(View v) {
                stopLoop();
            }
        });

        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels);
                if (isLoopAuto() && !isLoopStarted && list_bean.size() > 1) {
                    startLoop();
                    isLoopStarted = true;
                }
                indicatorView.setCount(list_bean.size())
                        .scroll(position - 1, positionOffset)
                        .getView()
                        .invalidate();
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                super.onPageScrollStateChanged(state);
                //验证当前的滑动是否结束
                if (state == ViewPager.SCROLL_STATE_IDLE) {
                    if (viewPager2.getCurrentItem() == 0) {
                        viewPager2.setCurrentItem(getItemCount()-2, false);
                        return;
                    }
                    if (viewPager2.getCurrentItem() == getItemCount()-1) {
                        viewPager2.setCurrentItem(1, false);
                        return;
                    }
                }

            }
        });
    }

    private int getPosition(int position) {
        return position == 0 ? list_bean.size() - 1 : position == getItemCount() - 1 ? 0 : position - 1;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewPager2Holder holder, int position) {
        super.onBindViewHolder(holder, getPosition(position));
    }

    @Override
    public int getItemViewType(int position) {
        int p = getPosition(position);
        return getItemLayoutID(p, list_bean.get(p));
    }

    @Override
    protected void handleClick(final ViewPager2Holder holder) {
        //添加Item的点击事件
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition() - 1;
                onItemClick(holder, position, list_bean.get(position));
            }
        });
    }

    @Override
    public int getItemCount() {
        if (list_bean.size() <= 1) return list_bean.size();
        return list_bean.size() + 2;
    }

    public long getPeriodLoop() {
        return periodLoop;
    }

    public void setPeriodLoop(long periodLoop) {
        this.periodLoop = periodLoop;
    }

    public boolean isLoopAuto() {
        return loopAuto;
    }

    public void setLoopAuto(boolean loopAuto) {
        this.loopAuto = loopAuto;
    }

    public void startLoop() {
        if(!loopAuto)return;
        stopLoop();
        timer = new Timer();
        timerTask = new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        viewPager2.setCurrentItem(viewPager2.getCurrentItem() + 1);
                    }
                });
            }
        };
        try {
            timer.scheduleAtFixedRate(timerTask, periodLoop, periodLoop);
        } catch (Exception e) {

        }
    }

    public void stopLoop() {
        if (timer != null) timer.cancel();
        if (timerTask != null) timerTask.cancel();
        timer = null;
        timerTask = null;
    }
    public void setStartItem(){
        viewPager2.setCurrentItem(1,false);
    }

}
