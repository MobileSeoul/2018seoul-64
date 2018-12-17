package artground.otterbear.com.artground.main

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.LayerDrawable
import android.os.Bundle
import android.support.v4.app.ActivityOptionsCompat
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import artground.otterbear.com.artground.R
import artground.otterbear.com.artground.common.Values
import artground.otterbear.com.artground.db.model.SimpleArtItem
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade
import kotlinx.android.synthetic.main.fragment_dashboard_artitem.*
import java.text.SimpleDateFormat
import java.util.*

class DashboardArtItemFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_dashboard_artitem, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        activity?.let { activity ->
            val artItem = (arguments?.getSerializable(Values.EXTRA_ART_ITEM) as? SimpleArtItem)
            artItem?.let {
                var imageInfo = artItem.mainImg
                imageInfo?.let { info ->
                    if (info.contains(Values.API_ART_ITEM_IMAGE_URL_PREFIX)) {
                        imageInfo = StringBuilder(Values.API_ART_ITEM_IMAGE_URL_PREFIX.toLowerCase()).append(info.substring(Values.API_ART_ITEM_IMAGE_URL_PREFIX.length, info.length)).toString()
                    }
                }

                GlideApp.with(activity).load(imageInfo).placeholder(R.drawable.my_logo).transition(withCrossFade()).into(artItemImg)
                artItemTitle.text = artItem.title.replace("&#39;", "\'")

                val sdf = SimpleDateFormat("yyyy. MM. dd", Locale.KOREA)
                artItemDate.text = StringBuilder().apply {
                    append(sdf.format(artItem.startDate)).append("\t~\t").append(sdf.format(artItem.endDate))
                }

                artItemLocation.text = if (artItem.place == null || artItem.place == "()" || artItem.place.isBlank()) getString(R.string.no_info) else artItem.place
                officialDataBadge.visibility = if (artItem.cultCode != null) View.VISIBLE else View.GONE

                artItemCategory.apply {
                    val categoryLayer = (background as LayerDrawable).findDrawableByLayerId(R.id.categoryBackground)
                    (categoryLayer as GradientDrawable).setColor(Color.parseColor("#${artItem.categoryThemeColor}"))
                    text = artItem.categoryName
                }

                artItemFrame.setOnClickListener { v ->
                    val options = ActivityOptionsCompat.makeSceneTransitionAnimation(activity, artItemImg, "sharedTransition")
                    startActivity(Intent(v.context, DetailArtItemActivity::class.java).apply {
                        putExtra(Values.EXTRA_ART_ITEM, it)
                    }, options.toBundle())
                }
            }
        }
    }
}