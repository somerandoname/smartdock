package cu.axel.smartdock.widgets

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.widget.LinearLayout

class HoverInterceptorLayout(context: Context, attrs: AttributeSet?) : LinearLayout(context, attrs) {

    // 0 lays children out normally. 90 or -90 gives them this layout's height as
    // their width (and vice versa) and turns them by that many degrees, so a
    // horizontal strip can fill a tall, narrow window.
    var contentRotation: Int = 0
        set(value) {
            if (field != value) {
                field = value
                requestLayout()
            }
        }

    override fun onInterceptHoverEvent(event: MotionEvent): Boolean {
        return true
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        if (contentRotation == 0) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec)
            return
        }
        val width = MeasureSpec.getSize(widthMeasureSpec)
        val height = MeasureSpec.getSize(heightMeasureSpec)
        setMeasuredDimension(width, height)
        val childWidthSpec = MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY)
        val childHeightSpec = MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY)
        for (i in 0 until childCount) {
            val child = getChildAt(i)
            if (child.visibility != View.GONE) child.measure(childWidthSpec, childHeightSpec)
        }
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        if (contentRotation == 0) {
            for (i in 0 until childCount) {
                val child = getChildAt(i)
                child.rotation = 0f
                child.translationX = 0f
                child.translationY = 0f
            }
            super.onLayout(changed, l, t, r, b)
            return
        }
        val width = r - l
        val height = b - t
        for (i in 0 until childCount) {
            val child = getChildAt(i)
            if (child.visibility == View.GONE) continue
            child.layout(0, 0, height, width)
            child.pivotX = 0f
            child.pivotY = 0f
            child.rotation = contentRotation.toFloat()
            // Rotating around the top-left corner swings the child out of bounds; shift it back in
            if (contentRotation > 0) {
                child.translationX = width.toFloat()
                child.translationY = 0f
            } else {
                child.translationX = 0f
                child.translationY = height.toFloat()
            }
        }
    }
}
