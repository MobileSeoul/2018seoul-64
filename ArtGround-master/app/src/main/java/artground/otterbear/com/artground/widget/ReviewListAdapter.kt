package artground.otterbear.com.artground.widget

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.LayerDrawable
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import artground.otterbear.com.artground.R
import artground.otterbear.com.artground.common.Values
import artground.otterbear.com.artground.db.model.DashboardReviewItem
import artground.otterbear.com.artground.main.GlideApp
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import kotlinx.android.synthetic.main.review_list_row.view.*
import java.text.SimpleDateFormat
import java.util.*

class ReviewListAdapter(private val reviewDataSet: MutableList<DashboardReviewItem>) : RecyclerView.Adapter<ReviewListAdapter.ItemHolder>() {
    private val dateFormat = SimpleDateFormat("yyyy. MM. dd hh:mm", Locale.KOREA)
    private var itemClickListener: ((Int, ReviewListAdapter.ItemHolder) -> Unit)? = null

    inner class ItemHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    fun setOnItemClickListener(listener: ((Int, ReviewListAdapter.ItemHolder) -> Unit)) {
        itemClickListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReviewListAdapter.ItemHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.review_list_row, parent, false)
        return ItemHolder(itemView)
    }

    override fun getItemCount() = reviewDataSet.size

    override fun onBindViewHolder(holder: ReviewListAdapter.ItemHolder, position: Int) {
        holder.itemView.apply {
            var imageInfo = reviewDataSet[position].mainImg
            imageInfo?.let { info ->
                if (info.contains(Values.API_ART_ITEM_IMAGE_URL_PREFIX)) {
                    imageInfo = StringBuilder(Values.API_ART_ITEM_IMAGE_URL_PREFIX.toLowerCase()).append(info.substring(Values.API_ART_ITEM_IMAGE_URL_PREFIX.length, info.length)).toString()
                }
            }

            GlideApp.with(context).load(imageInfo).placeholder(R.drawable.my_logo).transition(DrawableTransitionOptions.withCrossFade()).into(reviewImg)
            artItemTitle.text = reviewDataSet[position].title
            artItemCategory.apply {
                val d = (background as LayerDrawable).findDrawableByLayerId(R.id.categoryBackground)
                (d as GradientDrawable).setColor(Color.parseColor("#${reviewDataSet[position].categoryThemeColor}"))
                text = reviewDataSet[position].categoryName
            }

            reviewDesc.text = reviewDataSet[position].desc
            pubDate.text = dateFormat.format(reviewDataSet[position].date)

            setOnClickListener {
                itemClickListener?.invoke(position, holder)
            }
        }
    }
}