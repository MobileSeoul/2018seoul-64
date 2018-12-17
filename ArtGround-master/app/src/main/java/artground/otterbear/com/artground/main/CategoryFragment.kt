package artground.otterbear.com.artground.main

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import artground.otterbear.com.artground.R
import artground.otterbear.com.artground.db.model.StatCategoryItem
import artground.otterbear.com.artground.db.viewmodel.CategoryViewModel
import kotlinx.android.synthetic.main.category_list_row.view.*
import kotlinx.android.synthetic.main.fragment_category.*


class CategoryFragment : Fragment() {

    private val categoryViewModel by lazy { ViewModelProviders.of(this@CategoryFragment).get(CategoryViewModel::class.java) }
    private val categoryDataSet = mutableListOf<StatCategoryItem>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_category, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        categoryList.apply {
            layoutManager = LinearLayoutManager(context.applicationContext)
            setHasFixedSize(true)
            adapter = CategoryListAdapter(categoryDataSet)
        }

        categoryViewModel.getAllCategories().observe(this, Observer { r ->
            r?.let {
                categoryDataSet.apply {
                    if (size > 0) clear()
                    addAll(it)
                }
                categoryList.adapter.notifyDataSetChanged()
            }
        })
    }

    companion object {
        class CategoryListAdapter(private val dataSet: MutableList<StatCategoryItem>) : RecyclerView.Adapter<CategoryListAdapter.ItemViewHolder>() {
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
                return ItemViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.category_list_row, parent, false))
            }

            override fun getItemCount() = dataSet.size

            override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
                holder.itemView.apply {
                    val data = dataSet[position]
                    categoryInfo.text = context.getString(R.string.category_with_count, data.name, data.itemCount)
                    favoriteInfo.visibility = if (data.favorite) View.VISIBLE else View.GONE

                    val categoryBgRes = context.resources.getIdentifier(data.imgResName, "drawable", context.packageName)
                    GlideApp.with(context).load(categoryBgRes).into(categoryImg)
                }
            }

            inner class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
        }
    }
}