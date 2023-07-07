package com.kt.whose.subordinate.ExtendView;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;

import com.airbnb.lottie.LottieAnimationView;
import com.kt.whose.subordinate.R;
import com.scwang.smart.refresh.layout.api.RefreshHeader;
import com.scwang.smart.refresh.layout.api.RefreshKernel;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.constant.RefreshState;
import com.scwang.smart.refresh.layout.constant.SpinnerStyle;

/**
 * Lottie动画
 *
 * @author wapchief
 * @date 2018 /1/30
 */
public class RefreshLottieHeader extends LinearLayout implements RefreshHeader {

    /**
     * The M animation view.
     */
    LottieAnimationView mAnimationView;

    /**
     * Instantiates a new My refresh lottie header.
     *
     * @param context the context
     */
    public RefreshLottieHeader(Context context) {
        super(context);
        initView(context);
    }

    /*@Override
    public void onPullingDown(float percent, int offset, int headerHeight, int extendHeight) {

    }

    @Override
    public void onReleasing(float percent, int offset, int headerHeight, int extendHeight) {

    }

    @Override
    public void onRefreshReleased(RefreshLayout layout, int headerHeight, int extendHeight) {

    }*/

    /**
     * 注意不能为null
     * @return
     */
    @NonNull
    @Override
    public View getView() {
        return this;
    }

    @Override
    public SpinnerStyle getSpinnerStyle() {
        return SpinnerStyle.Translate;
    }



    @Override
    public void setPrimaryColors(int... colors) {

    }

    @Override
    public void onInitialized(RefreshKernel kernel, int height, int extendHeight) {

    }

    @Override
    public void onMoving(boolean isDragging, float percent, int offset, int height, int maxDragHeight) {

    }

    @Override
    public void onReleased(@NonNull RefreshLayout refreshLayout, int height, int maxDragHeight) {

    }

    @Override
    public void onHorizontalDrag(float percentX, int offsetX, int offsetMax) {

    }

    @Override
    public void onStartAnimator(RefreshLayout layout, int height, int extendHeight) {
        mAnimationView.playAnimation();
    }

    @Override
    public int onFinish(RefreshLayout layout, boolean success) {
        mAnimationView.cancelAnimation();
        return 0;
    }

    @Override
    public boolean isSupportHorizontalDrag() {
        return false;
    }

    @Override
    public boolean autoOpen(int duration, float dragRate, boolean animationOnly) {
        return false;
    }

    @Override
    public void onStateChanged(RefreshLayout refreshLayout, RefreshState oldState, RefreshState newState) {

    }

    private void initView(Context context) {
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.loading_lottie, this);
        mAnimationView = (LottieAnimationView) view.findViewById(R.id.loading_lottie);
    }

    /**
     * Set animation view json.
     *
     * @param animName json文件名
     */
    public void setAnimationViewJson(String animName){
        mAnimationView.setAnimation(animName);
    }

    /**
     * Set animation view json.
     *
     * @param anim the anim
     */
    public void setAnimationViewJson(Animation anim){
        mAnimationView.setAnimation(anim);
    }

    /*@Override
    public void onStateChanged(@NonNull RefreshLayout refreshLayout, @NonNull RefreshState oldState, @NonNull RefreshState newState) {

    }*/
}