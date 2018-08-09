package com.chiemy.viewpagerdemo;

import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    private int numOfStack = 3;
    private int size = 3;
    private int offsetCount = numOfStack - 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final ViewPager viewPager = findViewById(R.id.viewPager);
        viewPager.setOffscreenPageLimit(numOfStack);
        final DeviceListAdapter adapter = new DeviceListAdapter();
        viewPager.setPageTransformer(true, new StackPageTransformer(numOfStack));
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                Log.d(TAG, "onPageSelected: " + position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                if (state == ViewPager.SCROLL_STATE_IDLE) {
                    int position = viewPager.getCurrentItem();
                    // 第二个
                    if (position <= 1) {
                        viewPager.setCurrentItem(size + position, false);
                    }
                    // 倒数第二个
                    else if (position >= adapter.getCount() - 2) {
                        viewPager.setCurrentItem(position - size, false);
                    }
                    Log.d(TAG, "onPageScrollStateChanged: position = " + position);
                    // viewPager.setCurrentItem(realPosition, false);
                }
            }
        });
        viewPager.setCurrentItem(offsetCount, false);
    }

    public class DeviceListAdapter<T> extends PagerAdapter {

        private View.OnClickListener mOnClickListener;

        public void setOnPageClickListener(View.OnClickListener onClickListener) {
            mOnClickListener = onClickListener;
        }

        @Override
        public int getCount() {
            return getRealCount() + offsetCount * 2;
        }

        private int getRealCount() {
            return size;
        }

        private int getRealPosition(int position) {
            int realCount = getRealCount();
            return (position - offsetCount + realCount) % realCount;
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
            return view == object;
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            container.removeView((View) object);
        }

        @NonNull
        @Override
        public final Object instantiateItem(@NonNull ViewGroup container, int position) {
            int realPosition = getRealPosition(position);
            View view = LayoutInflater.from(container.getContext()).inflate(R.layout.item, container, false);
            TextView tv = view.findViewById(R.id.tv);
            tv.setText(String.valueOf(realPosition));
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mOnClickListener != null) {
                        mOnClickListener.onClick(v);
                    }
                }
            });
            view.setTag(position);
            container.addView(view);
            return view;
        }

    }
}
