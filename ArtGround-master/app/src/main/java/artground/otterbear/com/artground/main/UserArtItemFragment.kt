package artground.otterbear.com.artground.main

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.ActivityOptionsCompat
import android.support.v4.app.Fragment
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import artground.otterbear.com.artground.R
import artground.otterbear.com.artground.common.AppLogger
import artground.otterbear.com.artground.common.Values
import artground.otterbear.com.artground.db.model.SimpleArtItem
import artground.otterbear.com.artground.db.viewmodel.ArtItemViewModel
import artground.otterbear.com.artground.widget.ArtItemListAdapter
import artground.otterbear.com.artground.widget.ArtItemOptions
import kotlinx.android.synthetic.main.art_item_row.view.*
import kotlinx.android.synthetic.main.fragment_user_artitem.*

class UserArtItemFragment : Fragment() {

    private val artItemViewModel by lazy { ViewModelProviders.of(this).get(ArtItemViewModel::class.java) }
    private val artItemDataSet = mutableListOf<SimpleArtItem>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_user_artitem, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        artItemViewModel.getAllUserArtItems().observe(this, Observer { r ->
            r?.let {
                artItemDataSet.apply {
                    if (!isEmpty()) clear()
                    addAll(it)
                }

                if (artItemDataSet.isEmpty()) {
                    emptySectionGroup.visibility = View.VISIBLE
                    artItemWriteBtn.visibility = View.GONE
                    userArtItemList.visibility = View.GONE
                } else {
                    emptySectionGroup.visibility = View.GONE
                    artItemWriteBtn.visibility = View.VISIBLE
                    userArtItemList.visibility = View.VISIBLE
                }
                userArtItemList.adapter?.notifyDataSetChanged()
            }
        })

        userArtItemList.apply {
            setHasFixedSize(false)
            layoutManager = LinearLayoutManager(context.applicationContext)
            adapter = ArtItemListAdapter(artItemDataSet, true).apply {
                setOnItemClickCallback(artItemClickCallback)
                setOnItemOptionClickCallback(artItemOptionClickCallback)
            }
        }

        emptyArtItemWriteBtn.setOnClickListener {
            startActivity(Intent(it.context, AddArtItemActivity::class.java))
        }

        artItemWriteBtn.setOnClickListener {
            startActivity(Intent(it.context, AddArtItemActivity::class.java))
        }
    }

    private fun showItemDeleteDialog(itemPosition: Int) {
        context?.let {
            AlertDialog.Builder(it)
                    .setTitle(android.R.string.dialog_alert_title)
                    .setMessage(R.string.art_item_delete_msg)
                    .setPositiveButton(android.R.string.ok) { dialog, which ->
                        val item = artItemDataSet[itemPosition]
                        artItemViewModel.deleteArtItem(item._id)
                    }
                    .setNegativeButton(android.R.string.cancel, null)
                    .show()
        }
    }

    private val artItemOptionClickCallback: (ArtItemOptions, Int, ArtItemListAdapter.ItemViewHolder) -> Unit = { option, position, holder ->
        AppLogger.LOGE("o: $option p: $position")
        when (option) {
            ArtItemOptions.EDIT -> {
                context?.let {
                    startActivity(Intent(it, AddArtItemActivity::class.java).apply {
                        putExtra(Values.EXTRA_ART_ITEM, artItemDataSet[position])
                    })
                }
            }
            ArtItemOptions.DELETE -> showItemDeleteDialog(position)
        }
    }

    private val artItemClickCallback: (Int, ArtItemListAdapter.ItemViewHolder) -> Unit = { position, holder ->
        activity?.let {
            val options = ActivityOptionsCompat.makeSceneTransitionAnimation(it, holder.itemView.artImg, "sharedTransition")
            startActivity(Intent(it, DetailArtItemActivity::class.java).apply {
                putExtra(Values.EXTRA_ART_ITEM, artItemDataSet[position])
            }, options.toBundle())
        }
    }
}