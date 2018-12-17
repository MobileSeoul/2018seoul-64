package artground.otterbear.com.artground.main

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import artground.otterbear.com.artground.R
import artground.otterbear.com.artground.common.Values
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import kotlinx.android.synthetic.main.activity_preperation.*
import kotlinx.android.synthetic.main.fragment_walkthrough.*

class PreperationActivity : AppCompatActivity() {

    private val walkthroughBackgroundRes = arrayOf(R.drawable.category_bg_busking, R.drawable.category_bg_musical_opera, R.drawable.category_bg_festival)
    private val autoPagingHandler = Handler()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_preperation)
        loadWalkthroughBg(0)

        viewPager.apply {
            viewPager.adapter = WalkthroughPagerAdapter(supportFragmentManager)
            addOnPageChangeListener(pageChangeListener)
        }

        startBtn.setOnClickListener {
            startActivity(Intent(it.context, FavoriteCategory::class.java).apply {
                putExtra(Values.EXTRA_FAVORITE_CATEGORY_OP_MODE, Mode.NEW)
            })
            finish()
        }
        autoPagingHandler.postDelayed(autoPagingRunnable, AUTO_PAGING_DELAY)
    }

    override fun onDestroy() {
        viewPager.removeOnPageChangeListener(pageChangeListener)
        autoPagingHandler.removeCallbacks(autoPagingRunnable)
        super.onDestroy()
    }

    private val autoPagingRunnable = Runnable {
        var nextPosition = viewPager.currentItem + 1
        if (nextPosition >= 3) nextPosition = 0
        viewPager.setCurrentItem(nextPosition, true)
    }

    private fun loadWalkthroughBg(position: Int) {
        GlideApp.with(this).load(walkthroughBackgroundRes[position]).transition(DrawableTransitionOptions.withCrossFade()).into(walkthroughBg)
    }

    private val pageChangeListener = object : ViewPager.SimpleOnPageChangeListener() {
        override fun onPageSelected(position: Int) {
            loadWalkthroughBg(position)
            autoPagingHandler.removeCallbacks(autoPagingRunnable)
            autoPagingHandler.postDelayed(autoPagingRunnable, AUTO_PAGING_DELAY)
        }
    }

    companion object {
        private const val AUTO_PAGING_DELAY: Long = 3500

        class WalkthroughPagerAdapter(fm: FragmentManager) : FragmentStatePagerAdapter(fm) {
            override fun getCount(): Int = 3
            override fun getItem(position: Int): Fragment {
                return WalkthroughFragment().apply {
                    arguments = Bundle().apply { putInt(Values.WALKTHROUGH_IDX, position) }
                }
            }
        }

        class WalkthroughFragment : Fragment() {
            override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
                return inflater.inflate(R.layout.fragment_walkthrough, container, false)
            }

            override fun onActivityCreated(savedInstanceState: Bundle?) {
                super.onActivityCreated(savedInstanceState)
                activity?.let { activity ->
                    val idx = arguments!!.getInt(Values.WALKTHROUGH_IDX)
                    title.text = activity.resources.getStringArray(R.array.walkthrough_titles)[idx]
                    subTitle.text = activity.resources.getStringArray(R.array.walkthrough_descs)[idx]
                }
            }
        }
    }
}