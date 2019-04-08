package com.link.feeling.mvp.animator.impl

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import com.link.feeling.mvp.R
import com.link.feeling.mvp.animator.UIAnimator

/**
 * Created on 2019/1/14  11:20
 * chenpan pan.chen@linkfeeling.cn
 */
@Suppress("unused")
class DefaultUIAnimator : UIAnimator {

    override fun showLoading(loadingView: View, contentView: View, errorView: View) {
        loadingView.visibility = VISIBLE
        contentView.visibility = GONE
        errorView.visibility = GONE
    }

    override fun showErrorView(loadingView: View, contentView: View, errorView: View) {
        contentView.visibility = GONE

        if (errorView.visibility == VISIBLE) {
            loadingView.visibility = GONE
            return
        }

        val resources = loadingView.resources

        val animatorSet = AnimatorSet()
        val errorViewIn = ObjectAnimator.ofFloat(errorView, View.ALPHA, 1f)
        val loadingViewOut = ObjectAnimator.ofFloat(loadingView, View.ALPHA, 0f)
        animatorSet.playTogether(errorViewIn, loadingViewOut)
        animatorSet.duration = resources.getInteger(android.R.integer.config_shortAnimTime).toLong()
        animatorSet.addListener(object : AnimatorListenerAdapter() {

            override fun onAnimationStart(animation: Animator?) {
                super.onAnimationStart(animation)
                errorView.visibility = VISIBLE
            }

            override fun onAnimationEnd(animation: Animator?) {
                super.onAnimationEnd(animation)
                loadingView.visibility = GONE
                loadingView.alpha = 1f
            }
        })
        animatorSet.start()
    }

    override fun showContent(loadingView: View, contentView: View, errorView: View) {

        if (contentView.visibility == VISIBLE) {
            errorView.visibility = GONE
            loadingView.visibility = GONE
            return
        }

        errorView.visibility = GONE
        val resources = loadingView.resources
        val translateInPixel = resources.getDimensionPixelSize(R.dimen.content_view_animation_translate_y)

        val animatorSet = AnimatorSet()
        val contentFadeIn = ObjectAnimator.ofFloat(contentView, View.ALPHA, 1f)
        val contentTranslateIn = ObjectAnimator.ofFloat(contentView, View.TRANSLATION_Y,
                translateInPixel.toFloat(), 0f)
        val loadingFadeOut = ObjectAnimator.ofFloat(loadingView, View.ALPHA, 0f)
        val loadingTranslateOut = ObjectAnimator.ofFloat(loadingView, View.TRANSLATION_Y, 0f,
                -translateInPixel.toFloat())
        animatorSet.playTogether(contentFadeIn, contentTranslateIn, loadingFadeOut, loadingTranslateOut)
        animatorSet.duration = resources.getInteger(android.R.integer.config_shortAnimTime).toLong()
        animatorSet.addListener(object : AnimatorListenerAdapter() {

            override fun onAnimationStart(animation: Animator?) {
                super.onAnimationStart(animation)
                loadingView.translationY = 0f
                contentView.translationY = 0f
                contentView.visibility = VISIBLE
            }

            override fun onAnimationEnd(animation: Animator?) {
                super.onAnimationEnd(animation)
                loadingView.visibility = GONE
                loadingView.alpha = 1f
                loadingView.translationY = 0f
                contentView.translationY = 0f
            }
        })
        animatorSet.start()
    }
}