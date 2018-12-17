package artground.otterbear.com.artground.main

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.LayerDrawable
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import artground.otterbear.com.artground.R
import artground.otterbear.com.artground.common.AppLogger
import artground.otterbear.com.artground.common.Values
import artground.otterbear.com.artground.db.model.ReviewItem
import artground.otterbear.com.artground.db.model.SimpleArtItem
import artground.otterbear.com.artground.db.model.SimpleReviewItem
import artground.otterbear.com.artground.db.viewmodel.ArtItemViewModel
import kotlinx.android.synthetic.main.activity_detail_art_item.*
import kotlinx.android.synthetic.main.art_item_review_list_row.view.*
import java.text.SimpleDateFormat
import java.util.*

class DetailArtItemActivity : AppCompatActivity() {

    private val artItemViewModel by lazy { ViewModelProviders.of(this).get(ArtItemViewModel::class.java) }
    private val reviewDataSet = mutableListOf<SimpleReviewItem>()
    private lateinit var artItem: SimpleArtItem
    private var isReviewFirstLoad = true
    private val imm by lazy { getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_art_item)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        artItem = intent.extras.getSerializable(Values.EXTRA_ART_ITEM) as SimpleArtItem
        artItem.let {
            var imageInfo = it.mainImg
            imageInfo?.let { info ->
                if (info.contains(Values.API_ART_ITEM_IMAGE_URL_PREFIX)) {
                    imageInfo = StringBuilder(Values.API_ART_ITEM_IMAGE_URL_PREFIX.toLowerCase()).append(info.substring(Values.API_ART_ITEM_IMAGE_URL_PREFIX.length, info.length)).toString()
                }
            }
            GlideApp.with(this@DetailArtItemActivity).load(imageInfo).placeholder(R.drawable.my_logo).into(artItemImg)

            artItemCategory.apply {
                val categoryLayer = (background as LayerDrawable).findDrawableByLayerId(R.id.categoryBackground)
                (categoryLayer as GradientDrawable).setColor(Color.parseColor("#${it.categoryThemeColor}"))
                text = it.categoryName
            }

            val dateFormat = SimpleDateFormat("yyyy. MM. dd", Locale.KOREA)
            artItemDate.text = StringBuilder().apply {
                append(dateFormat.format(it.startDate)).append("\t~\t").append(dateFormat.format(it.endDate))
            }

            artItemName.text = it.title.replace("&#39;", "\'")

            val time = it.time
            AppLogger.LOGE("time: $time")
            artItemTime.text = if (time == null || time.isBlank()) getString(R.string.no_info) else time

            val place = it.place
            artItemLocation.text = if (place == null || place == "()" || place.isBlank()) getString(R.string.no_info) else place

            val useFee = it.useFee
            artItemFee.text = if (useFee == null || useFee.isBlank()) getString(R.string.no_info) else useFee

            val webPage = it.orgLink
            artItemWebPage.text = if (webPage == null || webPage.isBlank()) getString(R.string.no_info) else webPage

            val inquiry = it.inquiry
            artItemContact.text = if (inquiry == null || inquiry.isBlank()) getString(R.string.no_info) else inquiry

            val desc = it.etcDesc
            descGroup.visibility = if (desc == null || desc.isBlank()) View.GONE else View.VISIBLE
            desc?.let { d -> if (!d.isBlank()) artItemDesc.text = d }

            reviewList.apply {
                setHasFixedSize(false)
                layoutManager = LinearLayoutManager(context.applicationContext)
                adapter = ArtItemReviewListAdapter(reviewDataSet)
            }

            artItemViewModel.getReviewItemsByArtItemId(it._id).observe(this, Observer { r ->
                r?.let { items ->
                    reviewDataSet.apply {
                        if (isReviewFirstLoad) {
                            addAll(items)

                            val dummyReviewItemCount = Random().nextInt(5) + 1
                            for (i in 0 until dummyReviewItemCount) {
                                reviewDataSet.add(makeDummySimpleReviewItem())
                            }

                            Collections.sort(this, Comparator<SimpleReviewItem> { o1, o2 -> return@Comparator if (o1.date.time > o2.date.time) -1 else if (o1.date.time == o2.date.time) 0 else 1 })
                            isReviewFirstLoad = false
                        } else if (!items.isEmpty()) {
                            reviewDataSet.add(0, items[0])
                        }
                        reviewList.adapter?.notifyDataSetChanged()
                    }
                }
            })
        }

        window.apply {
            enterTransition.duration = 200
        }

        writeBtn.setOnClickListener {
            inputReview.apply {
                if (!text.isBlank()) {
                    publishReview()
                }
            }
        }
    }

    private fun publishReview() {
        //insert review
        artItemViewModel.insertReviewItem(
                ReviewItem(
                        aid = artItem._id,
                        desc = inputReview.text.toString(),
                        date = Date()
                )
        )
        inputReview.setText("")
        imm.hideSoftInputFromWindow(inputReview.windowToken, 0)
    }

    private fun makeDummySimpleReviewItem(): SimpleReviewItem {
        val random = Random()
        val desc = resources.getStringArray(R.array.dummy_review).run { get(random.nextInt(size)) }
        val date = generateRandomReviewDate(artItem!!.startDate.time)
        return SimpleReviewItem(
                _id = Values.DUMMY_REVIEW_ITEM_ID,
                desc = desc,
                date = date)
    }

    private fun generateRandomReviewDate(from: Long): Date {
        val to = System.currentTimeMillis()
        val degree = to - from
        return Date().apply { time = (from + (Random().nextDouble() * degree)).toLong() }
    }

    companion object {
        class ArtItemReviewListAdapter(private val reviewDataSet: MutableList<SimpleReviewItem>) : RecyclerView.Adapter<ArtItemReviewListAdapter.ItemViewHolder>() {
            private val dateFormat = SimpleDateFormat("yyyy. MM. dd hh:mm", Locale.KOREA)

            inner class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

            override fun getItemCount(): Int = reviewDataSet.size
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
                return ItemViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.art_item_review_list_row, parent, false))
            }

            override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
                holder.itemView.apply {
                    reviewDesc.text = reviewDataSet[position].desc
                    pubDate.text = dateFormat.format(reviewDataSet[position].date)
                }
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item?.itemId == android.R.id.home) {
            supportFinishAfterTransition()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}
