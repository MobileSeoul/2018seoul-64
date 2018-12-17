package artground.otterbear.com.artground.main

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import artground.otterbear.com.artground.R
import artground.otterbear.com.artground.common.AppLogger
import artground.otterbear.com.artground.common.Values
import artground.otterbear.com.artground.db.model.CategoryItem
import artground.otterbear.com.artground.db.viewmodel.CategoryViewModel
import kotlinx.android.synthetic.main.fragment_search.*
import kotlinx.android.synthetic.main.simple_category_list_item.view.*
import java.io.Serializable
import java.util.*

class SearchFragment : Fragment() {

    private val categoryViewModel by lazy { ViewModelProviders.of(this).get(CategoryViewModel::class.java) }
    private val categoryItems = mutableListOf<CategoryItem>()
    private val favoriteCategoryItems = mutableListOf<CategoryItem>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_search, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        categoryViewModel.getRawAllCategories().observe(this, Observer { r ->
            r?.let {
                categoryItems.addAll(it)
                categoryItems.add(0, createAllCategoryItem())

                val categoryNames = mutableListOf<String>()
                for (c in categoryItems) {
                    categoryNames.add(c.name)
                }

                val adapter = ArrayAdapter<String>(context!!, R.layout.category_spinner_item, 0, categoryNames)
                adapter.setDropDownViewResource(R.layout.category_spinner_dropdown_item)
                categorySpinner.adapter = adapter
            }
        })

        favoriteCategoryList.apply {
            setHasFixedSize(true)
            layoutManager = GridLayoutManager(context.applicationContext, 3)
            this.adapter = SimpleCategoryListAdapter(favoriteCategoryItems)
        }

        categoryViewModel.getFavoriteCategories().observe(this, Observer { r ->
            r?.let {
                favoriteCategoryItems.apply {
                    if (size > 0) clear()
                    addAll(it)
                }
                favoriteCategoryList.adapter.notifyDataSetChanged()
            }
        })

        startDateBtn.setDateInfoListener {
            AppLogger.LOGE("startDate: $it")
            endDateBtn.getDateInfo()?.run {
                if (it.time > this.time) {
                    Toast.makeText(startDateBtn.context, R.string.start_date_error_msg_1, Toast.LENGTH_SHORT).show()
                    return@setDateInfoListener false
                }
                searchBtn.isEnabled = true
            }
            return@setDateInfoListener true
        }

        endDateBtn.setDateInfoListener {
            AppLogger.LOGE("endDate: $it")
            startDateBtn.getDateInfo()?.run {
                if (it.time < this.time) {
                    Toast.makeText(startDateBtn.context, R.string.end_date_error_msg_1, Toast.LENGTH_SHORT).show()
                    return@setDateInfoListener false
                }
                searchBtn.isEnabled = true
            }

            return@setDateInfoListener true
        }

        searchBtn.setOnClickListener {
            postSearch()
        }

        favoriteCategorySettingBtn.setOnClickListener {
            startActivity(Intent(it.context, FavoriteCategory::class.java).apply {
                putExtra(Values.EXTRA_FAVORITE_CATEGORY_OP_MODE, Mode.MODIFY)
            })
        }
    }

    private fun postSearch() {
        context?.let {
            val params = SearchParams(categoryItems[categorySpinner.selectedItemPosition]._id!!,
                    startDateBtn.getDateInfo()!!, endDateBtn.getDateInfo()!!)

            startActivity(Intent(it, ArtItemListActivity::class.java).apply {
                putExtra(Values.EXTRA_SEARCH_PARAMS, params)
            })
        }
    }

    private fun createAllCategoryItem(): CategoryItem {
        return CategoryItem(_id = 0, name = getString(R.string.all_categories), imgResName = "", favorite = false, themeColor = "")
    }

    companion object {
        class SimpleCategoryListAdapter(private val dataSet: MutableList<CategoryItem>) : RecyclerView.Adapter<SimpleCategoryListAdapter.ItemViewHolder>() {
            inner class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

            override fun getItemCount() = dataSet.size
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
                return ItemViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.simple_category_list_item, parent, false))
            }

            override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
                holder.itemView.apply {
                    val resId = context.resources.getIdentifier(dataSet[position].imgResName, "drawable", context.packageName)
                    GlideApp.with(context).load(resId).into(categoryImg)
                    categoryName.text = dataSet[position].name
                }
            }
        }
    }
}

data class SearchParams(val cid: Long, val start: Date, val end: Date) : Serializable