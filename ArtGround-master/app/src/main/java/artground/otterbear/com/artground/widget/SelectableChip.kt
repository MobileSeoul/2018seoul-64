package artground.otterbear.com.artground.widget

import android.content.Context
import android.util.AttributeSet
import android.widget.ImageView
import android.widget.TextView
import artground.otterbear.com.artground.R
import artground.otterbear.com.artground.common.AppLogger
import com.robertlevonyan.views.chip.Chip

class SelectableChip(context: Context, attrs: AttributeSet) : Chip(context, attrs) {
    private lateinit var chipTextView: TextView
    private lateinit var selectIcon: ImageView

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        for (i in 0 until childCount) {
            getChildAt(i).run {
                if (this is ImageView) selectIcon = this
                else if (this is TextView) chipTextView = this
            }
        }

        if (::chipTextView.isInitialized && ::selectIcon.isInitialized) {
            chipTextView.setTextAppearance(R.style.DashboardCategoryOptionTextStyle)
            setOnClickListener { selectIcon.performClick() }
        }
    }

    override fun setChipText(chipText: String?) {
        AppLogger.LOGE("t: $chipText")
        super.setChipText(chipText)
        if (::chipTextView.isInitialized) {
            AppLogger.stamp()
            chipTextView.text = chipText
        }
    }
}