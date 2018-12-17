package artground.otterbear.com.artground.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.support.v4.content.ContextCompat
import android.support.v7.widget.AppCompatImageView
import android.util.AttributeSet
import artground.otterbear.com.artground.R

class RibbonImageView(context: Context, attrs: AttributeSet) : AppCompatImageView(context, attrs) {
    private val paint = Paint().apply {
        isAntiAlias = true
        style = Paint.Style.FILL
        color = ContextCompat.getColor(context, R.color.colorPrimary)
    }

    private val path = Path()

    init {
        setWillNotDraw(false)
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        invalidate()
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        path.moveTo(0f, 100f)
        path.lineTo(100f, 0f)
        path.lineTo(150f, 0f)
        path.lineTo(0f, 150f)
        path.lineTo(0f, 100f)
        canvas?.let {
            it.drawPath(path, paint)
        }
    }
}