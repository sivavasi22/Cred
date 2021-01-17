package com.example.credappassignment


import android.animation.Animator
import android.animation.Animator.AnimatorListener
import android.animation.ValueAnimator
import android.animation.ValueAnimator.AnimatorUpdateListener
import android.content.Context
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import android.util.AttributeSet
import android.view.animation.Interpolator
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import com.example.credappassignment.ExpandableLayout.State.Companion.COLLAPSED
import com.example.credappassignment.ExpandableLayout.State.Companion.COLLAPSING
import com.example.credappassignment.ExpandableLayout.State.Companion.EXPANDED
import com.example.credappassignment.ExpandableLayout.State.Companion.EXPANDING


class ExpandableLayout  constructor(context: Context?, attrs: AttributeSet? = null) :
    FrameLayout(context!!, attrs) {
    interface State {
        companion object {
            const val COLLAPSED = 0
            const val COLLAPSING = 1
            const val EXPANDING = 2
            const val EXPANDED = 3
        }
    }

    var duration = DEFAULT_DURATION
    private var parallax = 0f
    private var expansion = 0f
    private var orientation = 0

    /**
     * Get expansion state
     *
     * @return one of [State]
     */
    var state = 0
        private set
    private var interpolator: Interpolator = FastOutSlowInInterpolator()
    private var animator: ValueAnimator? = null
    private var listener: OnExpansionUpdateListener? = null
    override fun onSaveInstanceState(): Parcelable? {
        val superState = super.onSaveInstanceState()
        val bundle = Bundle()
        expansion = if (isExpanded) 1F else 0.toFloat()
        bundle.putFloat(KEY_EXPANSION, expansion)
        bundle.putParcelable(KEY_SUPER_STATE, superState)
        return bundle
    }

    override fun onRestoreInstanceState(parcelable: Parcelable) {
        val bundle = parcelable as Bundle
        expansion = bundle.getFloat(KEY_EXPANSION)
        state = if (expansion == 1f) EXPANDED else COLLAPSED
        val superState = bundle.getParcelable<Parcelable>(KEY_SUPER_STATE)
        super.onRestoreInstanceState(superState)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val width = measuredWidth
        val height = measuredHeight
        val size = if (orientation == LinearLayout.HORIZONTAL) width else height
        visibility = if (expansion == 0f && size == 0) GONE else VISIBLE
        val expansionDelta = size - Math.round(size * expansion)
        if (parallax > 0) {
            val parallaxDelta = expansionDelta * parallax
            for (i in 0 until childCount) {
                val child = getChildAt(i)
                if (orientation == HORIZONTAL) {
                    var direction = -1
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1 && layoutDirection == LAYOUT_DIRECTION_RTL) {
                        direction = 1
                    }
                    child.translationX = direction * parallaxDelta
                } else {
                    child.translationY = -parallaxDelta
                }
            }
        }
        if (orientation == HORIZONTAL) {
            setMeasuredDimension(width - expansionDelta, height)
        } else {
            setMeasuredDimension(width, height - expansionDelta)
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        if (animator != null) {
            animator!!.cancel()
        }
        super.onConfigurationChanged(newConfig)
    }


    var isExpanded: Boolean
        get() = state == EXPANDING || state == EXPANDED
        set(expand) {
            setExpanded(expand, true)
        }

    fun expand(animate: Boolean = true) {
        setExpanded(true, animate)
    }


    fun collapse(animate: Boolean = true) {
        setExpanded(false, animate)
    }

    fun setExpanded(expand: Boolean, animate: Boolean) {
        if (expand == isExpanded) {
            return
        }
        val targetExpansion = if (expand) 1 else 0
        if (animate) {
            animateSize(targetExpansion)
        } else {
            setExpansion(targetExpansion.toFloat())
        }
    }



    fun setExpansion(expansion: Float) {
        if (this.expansion == expansion) {
            return
        }

        // Infer state from previous value
        val delta = expansion - this.expansion
        if (expansion == 0f) {
            state = COLLAPSED
        } else if (expansion == 1f) {
            state = EXPANDED
        } else if (delta < 0) {
            state = COLLAPSING
        } else if (delta > 0) {
            state = EXPANDING
        }
        visibility = if (state == COLLAPSED) GONE else VISIBLE
        this.expansion = expansion
        requestLayout()
        if (listener != null) {
            listener!!.onExpansionUpdate(expansion, state)
        }
    }



    fun setParallax(parallax: Float) {
        // Make sure parallax is between 0 and 1
        var parallax = parallax
        parallax = Math.min(1f, Math.max(0f, parallax))
        this.parallax = parallax
    }



    fun setOnExpansionUpdateListener(listener: OnExpansionUpdateListener?) {
        this.listener = listener
    }

    private fun animateSize(targetExpansion: Int) {
        if (animator != null) {
            animator!!.cancel()
            animator = null
        }
        animator = ValueAnimator.ofFloat(expansion, targetExpansion.toFloat())
        animator?.setInterpolator(interpolator)
        animator?.setDuration(duration.toLong())
        animator?.addUpdateListener(AnimatorUpdateListener { valueAnimator ->
            setExpansion(
                valueAnimator.animatedValue as Float
            )
        })
        animator?.addListener(ExpansionListener(targetExpansion))
        animator?.start()
    }

    interface OnExpansionUpdateListener {

        fun onExpansionUpdate(expansionFraction: Float, state: Int)
    }

    private inner class ExpansionListener(private val targetExpansion: Int) :
        AnimatorListener {
        private var canceled = false
        override fun onAnimationStart(animation: Animator) {
            state = if (targetExpansion == 0) COLLAPSING else EXPANDING
        }

        override fun onAnimationEnd(animation: Animator) {
            if (!canceled) {
                state = if (targetExpansion == 0) COLLAPSED else EXPANDED
                setExpansion(targetExpansion.toFloat())
            }
        }

        override fun onAnimationCancel(animation: Animator) {
            canceled = true
        }

        override fun onAnimationRepeat(animation: Animator) {}
    }

    companion object {
        const val KEY_SUPER_STATE = "super_state"
        const val KEY_EXPANSION = "expansion"
        const val HORIZONTAL = 0
        const val VERTICAL = 1
        private const val DEFAULT_DURATION = 300
    }

    init {
        if (attrs != null) {
            val a = getContext().obtainStyledAttributes(attrs, R.styleable.ExpandableLayout)
            duration = a.getInt(R.styleable.ExpandableLayout_el_duration, DEFAULT_DURATION)
            expansion = if (a.getBoolean(
                    R.styleable.ExpandableLayout_el_expanded,
                    false
                )
            ) 1F else 0.toFloat()
            orientation = a.getInt(R.styleable.ExpandableLayout_android_orientation, VERTICAL)
            parallax = a.getFloat(R.styleable.ExpandableLayout_el_parallax, 1f)
            a.recycle()
            state = if (expansion == 0f) COLLAPSED else EXPANDED
            setParallax(parallax)
        }
    }


}