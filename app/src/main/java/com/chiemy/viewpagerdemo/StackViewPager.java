package com.chiemy.viewpagerdemo;

import android.content.Context;
import android.database.DataSetObserver;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

public class StackViewPager extends ViewPager {
    private static final String TAG = "StackViewPager";
    private static final int DEF_STACK_COUNT = 3;
    private int stackCount = DEF_STACK_COUNT;
    private int offsetCount = stackCount;

    private PagerAdapter mPagerAdapter;
    private InnerAdapter mInnerAdapter;
    private int size;

    private List<OnPageChangeListener> mOnPageChangeListeners;

    public StackViewPager(@NonNull Context context) {
        this(context, null);
    }

    public StackViewPager(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        super.addOnPageChangeListener(new OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                Log.d(TAG, "onPageScrolled: " + position);
                int realPosition = mInnerAdapter.getRealPosition(position);
                Log.d(TAG, "onPageScrolled: real position = " + realPosition);
                dispatchOnPageScrolled(realPosition, positionOffset, positionOffsetPixels);
            }

            @Override
            public void onPageSelected(int position) {
                Log.d(TAG, "onPageSelected: " + position);
                int realPosition = mInnerAdapter.getRealPosition(position);
                Log.d(TAG, "onPageSelected: real position = " + realPosition);
                dispatchOnPageSelected(realPosition);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                if (state == ViewPager.SCROLL_STATE_IDLE) {
                    int position = getCurrentItem();
                    Log.d(TAG, "onPageScrollStateChanged: " + position);
                    // 第二个
                    if (position <= 1) {
                        setCurrentItem(size + position, false);
                        beginFakeDrag();
                        fakeDragBy(1);
                        endFakeDrag();
                    }
                    else if (position >= mInnerAdapter.getCount() - offsetCount) {
                        setCurrentItem(position - size, false);
                    }
                }
                dispatchOnScrollStateChanged(state);
            }
        });
        mOnPageChangeListeners = new ArrayList<>(2);
        setPageTransformer(true, new StackPageTransformer(stackCount));
    }

    public void setStackCount(int stackCount) {
        this.stackCount = stackCount;
        setPageTransformer(true, new StackPageTransformer(stackCount));
    }

    @Override
    public void addOnPageChangeListener(@NonNull OnPageChangeListener listener) {
        mOnPageChangeListeners.add(listener);
    }

    @Override
    public void removeOnPageChangeListener(@NonNull OnPageChangeListener listener) {
        mOnPageChangeListeners.remove(listener);
    }

    private void dispatchOnPageScrolled(int position, float offset, int offsetPixels) {
        if (mOnPageChangeListeners != null) {
            for (int i = 0, z = mOnPageChangeListeners.size(); i < z; i++) {
                OnPageChangeListener listener = mOnPageChangeListeners.get(i);
                if (listener != null) {
                    listener.onPageScrolled(position, offset, offsetPixels);
                }
            }
        }
    }

    private void dispatchOnPageSelected(int position) {
        if (mOnPageChangeListeners != null) {
            for (int i = 0, z = mOnPageChangeListeners.size(); i < z; i++) {
                OnPageChangeListener listener = mOnPageChangeListeners.get(i);
                if (listener != null) {
                    listener.onPageSelected(position);
                }
            }
        }
    }

    private void dispatchOnScrollStateChanged(int state) {
        if (mOnPageChangeListeners != null) {
            for (int i = 0, z = mOnPageChangeListeners.size(); i < z; i++) {
                OnPageChangeListener listener = mOnPageChangeListeners.get(i);
                if (listener != null) {
                    listener.onPageScrollStateChanged(state);
                }
            }
        }
    }

    @Override
    public void setAdapter(@Nullable PagerAdapter adapter) {
        if (mPagerAdapter != null) {
            mPagerAdapter.unregisterDataSetObserver(mDataSetObserver);
        }
        if (adapter != null) {
            adapter.registerDataSetObserver(mDataSetObserver);
            mPagerAdapter = adapter;
            super.setAdapter(mInnerAdapter = new InnerAdapter());
            size = mInnerAdapter.getRealCount();
            setCurrentItem(offsetCount, false);
            setOffscreenPageLimit(stackCount);
        } else {
            super.setAdapter(null);
        }
    }

    private DataSetObserver mDataSetObserver = new DataSetObserver(){
        @Override
        public void onChanged() {
            super.onChanged();
            mInnerAdapter.notifyDataSetChanged();
        }

        @Override
        public void onInvalidated() {
            super.onInvalidated();
        }
    };

    @Nullable
    @Override
    public PagerAdapter getAdapter() {
        return mPagerAdapter;
    }

    private class InnerAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            int realCount = getRealCount();
            if (realCount == 0) {
                return 0;
            }
            if (realCount == 1) {
                return 1;
            }
            return getRealCount() + 2 * offsetCount;
        }

        private int getRealCount() {
            return mPagerAdapter.getCount();
        }

        private int getRealPosition(int position) {
            int realCount = getRealCount();
            return Math.abs(position - offsetCount + realCount) % realCount;
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
            return mPagerAdapter.isViewFromObject(view, object);
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            mPagerAdapter.destroyItem(container, position, object);
        }

        @NonNull
        @Override
        public final Object instantiateItem(@NonNull ViewGroup container, int position) {
            int realPosition = getRealPosition(position);
            return mPagerAdapter.instantiateItem(container, realPosition);
        }

    }
}
