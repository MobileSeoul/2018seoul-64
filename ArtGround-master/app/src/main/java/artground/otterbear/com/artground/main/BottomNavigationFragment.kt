package artground.otterbear.com.artground.main

import android.animation.ArgbEvaluator
import android.animation.ObjectAnimator
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import artground.otterbear.com.artground.R
import kotlinx.android.synthetic.main.fragment_bottom_navigation.*


class BottomNavigationFragment : Fragment(), View.OnClickListener {
    private val pastAnimator by lazy {
        ObjectAnimator().apply {
            propertyName = "colorFilter"
            duration = ANIMATION_DURATION
            setObjectValues(selectIconColor, unSelectIconColor)
            setEvaluator(ArgbEvaluator())
        }
    }

    private val currentAnimator by lazy {
        ObjectAnimator().apply {
            propertyName = "colorFilter"
            duration = ANIMATION_DURATION
            setObjectValues(unSelectIconColor, selectIconColor)
            setEvaluator(ArgbEvaluator())
        }
    }

    private var selectIconColor: Int = 0
    private var unSelectIconColor: Int = 0
    private var currentItemPosition = 0

    companion object {
        const val ANIMATION_DURATION: Long = 300
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_bottom_navigation, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        view?.let {
            selectIconColor = ContextCompat.getColor(it.context, R.color.seoul_main_color1)
            unSelectIconColor = ContextCompat.getColor(it.context, R.color.seoul_main_color2)
            it.post {
                dot.apply {
                    (dashboardBtn.getChildAt(0) as ImageView).setColorFilter(selectIconColor)
                    x = ((dashboardBtn.width / 2) - (dot.width / 2)).toFloat()
                    y = (dashboardBtn.getChildAt(0).top - resources.getDimension(R.dimen.bottom_navigation_dot_bottom_margin) - dot.height)
                    visibility = View.VISIBLE
                }

                dashboardBtn.setOnClickListener(this)
                searchBtn.setOnClickListener(this)
                userArtItemBtn.setOnClickListener(this)
            }
        }
    }

    override fun onClick(v: View?) {
        v?.let {
            val itemTag = (it.tag as String).toInt()
            if (currentItemPosition == itemTag || isAnimating()) return

            pastAnimator.apply {
                target = getIconByPosition(currentItemPosition)
            }

            currentAnimator.apply {
                target = getIconByPosition(itemTag)
            }

            dot.animate().translationX(it.left + ((it.width / 2) - (dot.width / 2)).toFloat()).withLayer().duration = ANIMATION_DURATION
            pastAnimator.start()
            currentAnimator.start()

            val oldPosition = currentItemPosition
            currentItemPosition = itemTag

            (it.context as? OnNavigationItemClickListener)?.onItemClick(oldPosition, currentItemPosition)
        }
    }

    private fun getIconByPosition(position: Int): ImageView {
        val v = when (position) {
            0 -> dashboardBtn.getChildAt(0)
            1 -> searchBtn.getChildAt(0)
            else -> userArtItemBtn.getChildAt(0)
        }
        return v as ImageView
    }

    private fun isAnimating() = pastAnimator.isRunning || currentAnimator.isRunning
}