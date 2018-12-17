package artground.otterbear.com.artground.main

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.ActivityOptionsCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.MenuItem
import android.view.View
import artground.otterbear.com.artground.R
import artground.otterbear.com.artground.common.AppLogger
import artground.otterbear.com.artground.common.Values
import artground.otterbear.com.artground.db.model.SimpleArtItem
import artground.otterbear.com.artground.db.repository.DashboardArtItemType
import artground.otterbear.com.artground.db.repository.DashboardCategoryFilter
import artground.otterbear.com.artground.db.viewmodel.ArtItemViewModel
import artground.otterbear.com.artground.widget.ArtItemListAdapter
import kotlinx.android.synthetic.main.activity_art_item_list.*
import kotlinx.android.synthetic.main.art_item_row.view.*
import java.io.Serializable

//TODO: ArtItem 새로운 걸로 바꿔서 올릴지 검토

class ArtItemListActivity : AppCompatActivity() {

    private val artItemViewModel by lazy { ViewModelProviders.of(this).get(ArtItemViewModel::class.java) }
    private val artItemDataSet = mutableListOf<SimpleArtItem>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_art_item_list)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        artItemList.apply {
            setHasFixedSize(false)
            layoutManager = LinearLayoutManager(context.applicationContext)
            adapter = ArtItemListAdapter(artItemDataSet).apply { setOnItemClickCallback(artItemClickCallback) }
        }

        val params = intent.extras?.getSerializable(Values.EXTRA_SEARCH_PARAMS) as? SearchParams
        params?.let { p ->
            artItemViewModel.findArtItem(p.cid, p.start, p.end).observe(this, observer)
            supportActionBar?.setTitle(R.string.art_item_search_result_title)
        }

        val opType = intent.extras?.getSerializable(Values.EXTRA_ART_ITEM_LIST_OP_TYPE) as? OpType
        AppLogger.LOGE("$opType")
        opType?.let {
            when (it.itemType) {
                DashboardArtItemType.ACTIVE -> {
                    supportActionBar?.setTitle(R.string.dashboard_playing_artitem_title)
                    artItemViewModel.getActiveArtItems(it.filter).observe(this, observer)
                }
                DashboardArtItemType.EXPECTED -> {
                    supportActionBar?.setTitle(R.string.dashboard_expect_artitem_title)
                    artItemViewModel.getExpectArtItems(it.filter).observe(this, observer)
                }
            }
            supportActionBar?.setSubtitle(if (it.filter == DashboardCategoryFilter.ALL) R.string.filter_all_category else R.string.filter_favorite_category)
        }
    }

    private val observer = Observer<MutableList<SimpleArtItem>> { r ->
        r?.let {
            AppLogger.LOGE("size: ${it.size}\n$it")
            artItemDataSet.apply {
                if (isEmpty()) clear()
                addAll(it)

                if (isEmpty()) {
                    emptySectionGroup.visibility = View.VISIBLE
                    artItemList.visibility = View.GONE
                } else {
                    emptySectionGroup.visibility = View.GONE
                    artItemList.visibility = View.VISIBLE
                }
            }
            artItemList.adapter?.notifyDataSetChanged()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item?.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private val artItemClickCallback: (Int, ArtItemListAdapter.ItemViewHolder) -> Unit = { position, holder ->
        val options = ActivityOptionsCompat.makeSceneTransitionAnimation(this, holder.itemView.artImg, "sharedTransition")
        startActivity(Intent(this@ArtItemListActivity, DetailArtItemActivity::class.java).apply {
            putExtra(Values.EXTRA_ART_ITEM, artItemDataSet[position])
        }, options.toBundle())
    }
}

data class OpType(val itemType: DashboardArtItemType, val filter: DashboardCategoryFilter) : Serializable