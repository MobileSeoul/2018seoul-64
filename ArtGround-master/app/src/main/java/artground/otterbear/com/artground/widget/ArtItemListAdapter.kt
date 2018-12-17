package artground.otterbear.com.artground.widget

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.LayerDrawable
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import artground.otterbear.com.artground.R
import artground.otterbear.com.artground.common.AppLogger
import artground.otterbear.com.artground.common.Values
import artground.otterbear.com.artground.db.model.SimpleArtItem
import artground.otterbear.com.artground.main.GlideApp
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import kotlinx.android.synthetic.main.art_item_row.view.*
import java.text.SimpleDateFormat
import java.util.*

class ArtItemListAdapter(private val dataSet: MutableList<SimpleArtItem>,
                         private val isEditMode: Boolean = false) : RecyclerView.Adapter<ArtItemListAdapter.ItemViewHolder>() {
    private val dateFormat = SimpleDateFormat("yyyy. MM. dd", Locale.KOREA)
    private var itemClickCallback: ((Int, ItemViewHolder) -> Unit)? = null
    private var itemOptionClickCallback: ((ArtItemOptions, Int, ItemViewHolder) -> Unit)? = null

    inner class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    fun setOnItemClickCallback(callback: (Int, ItemViewHolder) -> Unit) {
        itemClickCallback = callback
    }

    fun setOnItemOptionClickCallback(callback: (ArtItemOptions, Int, ItemViewHolder) -> Unit) {
        itemOptionClickCallback = callback
    }

    override fun getItemCount() = dataSet.size
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        return ItemViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.art_item_row, parent, false))
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.itemView.apply {
            var imageInfo = dataSet[position].mainImg
            imageInfo?.let { info ->
                if (info.contains(Values.API_ART_ITEM_IMAGE_URL_PREFIX)) {
                    imageInfo = StringBuilder(Values.API_ART_ITEM_IMAGE_URL_PREFIX.toLowerCase()).append(info.substring(Values.API_ART_ITEM_IMAGE_URL_PREFIX.length, info.length)).toString()
                }
            }

            GlideApp.with(context).load(imageInfo).placeholder(R.drawable.my_logo).transition(DrawableTransitionOptions.withCrossFade()).into(artImg)
            artItemName.text = dataSet[position].title.replace("&#39;", "\'")

            artItemDate.text = StringBuilder().apply {
                append(dateFormat.format(dataSet[position].startDate)).append("\t~\t").append(dateFormat.format(dataSet[position].endDate))
            }

            val place = dataSet[position].place

            artItemLocation.text = if (place == null || place == "()" || place.isBlank()) context.getString(R.string.no_info) else place.trim()
            officialDataBadge.visibility = if (dataSet[position].cultCode != null) View.VISIBLE else View.GONE
            AppLogger.LOGE("p: ${artItemName.text} l: ${artItemName.length()}")

            artItemCategory.apply {
                val categoryLayer = (background as LayerDrawable).findDrawableByLayerId(R.id.categoryBackground)
                (categoryLayer as GradientDrawable).setColor(Color.parseColor("#${dataSet[position].categoryThemeColor}"))
                text = dataSet[position].categoryName
            }

            rowParent.setOnClickListener { itemClickCallback?.invoke(position, holder) }

            if (isEditMode) {
                editBtn.visibility = View.VISIBLE
                deleteBtn.visibility = View.VISIBLE
            } else {
                editBtn.visibility = View.GONE
                deleteBtn.visibility = View.GONE
            }

            editBtn.setOnClickListener { itemOptionClickCallback?.invoke(ArtItemOptions.EDIT, position, holder) }
            deleteBtn.setOnClickListener { itemOptionClickCallback?.invoke(ArtItemOptions.DELETE, position, holder) }
        }
    }
}

enum class ArtItemOptions {
    EDIT, DELETE
}