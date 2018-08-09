package com.chiemy.viewpagerdemo;

import android.support.annotation.NonNull;
import android.support.v4.view.ViewPager;
import android.view.View;

public class StackPageTransformer implements ViewPager.PageTransformer {
    private int mSpaceHorizontal;
    private int mSpaceVertical;
    private boolean mInit;
    private int mLastVisibleIndex;

    public StackPageTransformer(int numberOfStacked) {
        mLastVisibleIndex = numberOfStacked - 1;
    }

    @Override
    public void transformPage(@NonNull View view, float position) {
        if (!mInit) {
            mInit = true;
            mSpaceHorizontal = 20;
            mSpaceVertical = 30;
        }
        int dimen = view.getWidth();
        if (position <= 0) {
            view.setAlpha(1);
            view.setTranslationX(0);
            view.setTranslationY(0);
        } else {
            view.setTranslationX(-(dimen - mSpaceHorizontal) * position);
            view.setTranslationY(mSpaceVertical * position);
            if (position > mLastVisibleIndex) {
                view.setAlpha(mLastVisibleIndex - position + 1);
            } else {
                view.setAlpha(1f);
            }
        }
    }

}
