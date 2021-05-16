package com.udacity

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Typeface
import android.util.AttributeSet
import android.view.View
import kotlin.properties.Delegates

class LoadingButton @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private var widthSize = 0
    private var heightSize = 0
    private var valueAnimator = ValueAnimator()

    private var circleColor: Int = 0
    private var buttonTextColor: Int = 0
    private var buttonBackgroundColor: Int = 0

    private var buttonText = resources.getString(R.string.button_download)

    private var paintButton = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        isAntiAlias = true
        color = buttonBackgroundColor
    }

    private var paintLoading = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        isAntiAlias = true
        color = context.getColor(R.color.colorPrimaryDark)
    }

    private val paintText = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        isAntiAlias = true
        color = buttonTextColor
        textSize = 34.0f
        textAlign = Paint.Align.CENTER
    }

    private var paintCircle = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        isAntiAlias = true
        color = context.getColor(R.color.colorAccent)
    }

    var value = 0.0f
    var width = 0.0f
    var sweepAngle = 0.0f

    var buttonState: ButtonState by Delegates.observable<ButtonState>(ButtonState.Completed) { property, old, new ->
        when (new) {
            ButtonState.Loading -> showButtonStateLoading()
            ButtonState.Completed -> showButtonStateCompleted()
            else -> {
                //no operation
            }
        }
    }

    init {
        isClickable = true
        initCustomAttributes(attrs, defStyleAttr)
    }

    private fun initCustomAttributes(attrs: AttributeSet?, defStyleAttr: Int) {
        context.theme.obtainStyledAttributes(attrs, R.styleable.LoadingButton, defStyleAttr, 0).apply {
            circleColor = getColor(R.styleable.LoadingButton_circleColor, 0)
            buttonTextColor = getColor(R.styleable.LoadingButton_buttonTextColor, 0)
            buttonBackgroundColor = getColor(R.styleable.LoadingButton_buttonBackgroundColor, 0)
        }
    }

    private fun showButtonStateLoading() {
        buttonText = resources.getString(R.string.button_loading)
        paintCircle.color = circleColor
        valueAnimator = ValueAnimator.ofFloat(0.0f, measuredWidth.toFloat())
            .setDuration(3000)
            .apply {
                addUpdateListener { valueAnimator ->
                    value = valueAnimator.animatedValue as Float
                    sweepAngle = value / 10
                    width = valueAnimator.animatedValue as Float
                    repeatMode = ValueAnimator.RESTART
                    repeatCount = ValueAnimator.INFINITE
                    invalidate()
                }
            }
        valueAnimator.start()
    }

    private fun showButtonStateCompleted() {
        buttonText = resources.getString(R.string.button_download)
        sweepAngle = 0f
        valueAnimator.cancel()
        width = 0f
        value = 0.0f
        invalidate()
    }


    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        paintText.color = buttonTextColor

        val textHeight: Float = paintText.descent() - paintText.ascent()
        val textOffset: Float = textHeight / 2 - paintText.descent()

        canvas?.drawRect(0f, 0f, width, heightSize.toFloat(), paintLoading)
        canvas?.drawRect(0.0f, 0.0f, widthSize.toFloat(), heightSize.toFloat(), paintButton)
        canvas?.drawText(buttonText, widthSize.toFloat() / 2.0f, heightSize.toFloat() / 2.0f + textOffset, paintText)

        canvas?.drawArc(
            widthSize - 150f,
            heightSize / 2 - 25f,
            widthSize - 80f,
            heightSize / 2 + 25f,
            0F,
            width,
            true,
            paintCircle
        )
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val minw: Int = paddingLeft + paddingRight + suggestedMinimumWidth
        val w: Int = resolveSizeAndState(minw, widthMeasureSpec, 1)
        val h: Int = resolveSizeAndState(
            MeasureSpec.getSize(w),
            heightMeasureSpec,
            0
        )
        widthSize = w
        heightSize = h
        setMeasuredDimension(w, h)
    }

    fun setState(state: ButtonState) {
        buttonState = state
    }
}
