package artground.otterbear.com.artground.main


import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import artground.otterbear.com.artground.R
import artground.otterbear.com.artground.common.AppLogger
import artground.otterbear.com.artground.common.Values
import artground.otterbear.com.artground.db.model.StatCategoryItem
import artground.otterbear.com.artground.db.viewmodel.CategoryViewModel
import kotlinx.android.synthetic.main.activity_favorite_category.*
import kotlinx.android.synthetic.main.favorite_category_item.view.*


class FavoriteCategory : AppCompatActivity() {

    private val categoryViewModel by lazy { ViewModelProviders.of(this).get(CategoryViewModel::class.java) }
    private val categoryDataSet = mutableListOf<StatCategoryItem>()
    private var isFirstLoaded = true
    private lateinit var mode: Mode

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_favorite_category)

        mode = intent.extras!!.getSerializable(Values.EXTRA_FAVORITE_CATEGORY_OP_MODE) as Mode

        categoryViewModel.getAllCategories().observe(this, Observer { r ->
            r?.let {
                if (isFirstLoaded) {
                    categoryDataSet.addAll(it)
                    val favoriteItemSize = categoryDataSet.filter { fit -> fit.favorite }.size
                    fc_count.text = getString(R.string.favorite_category_count_format, MAX_FAVORITE_CATEGORY_COUNT, favoriteItemSize)
                    loadCategoryList()

                    isFirstLoaded = false
                }
//                categoryDataSet.apply {
//                    if (size > 0) clear()
//                    addAll(it)
//                }
//
//                favorite_category.adapter.notifyDataSetChanged()
            }
        })

        fc_btn.setOnClickListener {
            if ((favorite_category.adapter as CategoryListAdapter).getSelectedItemCount() == MAX_FAVORITE_CATEGORY_COUNT) {
                postUpdateCategories()
            } else {
                Toast.makeText(this, R.string.noti_favorite_category_select, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun loadCategoryList() {
        favorite_category.apply {
            layoutManager = GridLayoutManager(context.applicationContext, 3)
            setHasFixedSize(true)
            adapter = CategoryListAdapter(categoryDataSet).apply {
                setItemSelectedListener { count ->
                    for (c in categoryDataSet) {
                        AppLogger.LOGE("n: ${c.name} f: ${c.favorite}")
                    }
                    fc_count.text = getString(R.string.favorite_category_count_format, MAX_FAVORITE_CATEGORY_COUNT, count)
                }
            }
        }
    }

    private fun postUpdateCategories() {
        categoryViewModel.updateCategories(categoryDataSet) {
            if (mode == Mode.NEW) {
                AGPreferences.setPreperationComplete(this)
                startActivity(Intent(this, MainActivity::class.java))
            }
            finish()
        }
    }

//    private fun saveData() {
//        val prefs = AGPreferences(applicationContext)
//        prefs.text = "USED"
//    }

    companion object {
        private const val MAX_FAVORITE_CATEGORY_COUNT = 3

        class CategoryListAdapter(private val dataSet: MutableList<StatCategoryItem>) : RecyclerView.Adapter<CategoryListAdapter.FCItemViewHolder>() {
            private var selectedItemCount: Int = dataSet.filter { it.favorite }.size
            private var itemSelectedListener: ((Int) -> Unit)? = null

            fun setItemSelectedListener(listener: ((Int) -> Unit)?) {
                itemSelectedListener = listener
            }

            fun getSelectedItemCount() = selectedItemCount

            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FCItemViewHolder {
                return FCItemViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.favorite_category_item, parent, false))
            }

            override fun getItemCount() = dataSet.size

            override fun onBindViewHolder(holder: FCItemViewHolder, position: Int) {
                holder.itemView.apply {
                    val data = dataSet[position]
                    fc_item_title.text = context.getString(R.string.category_with_count, data.name, data.itemCount)
                    selectedMask.visibility = if (data.favorite) View.VISIBLE else View.GONE

                    val categoryBgRes = context.resources.getIdentifier(data.imgResName, "drawable", context.packageName)
                    GlideApp.with(context).load(categoryBgRes).into(fc_item_img)

                    setOnClickListener { v ->
                        if (dataSet[position].favorite) selectedItemCount-- else selectedItemCount++

                        if (selectedItemCount <= MAX_FAVORITE_CATEGORY_COUNT) {
                            dataSet[position].favorite = !dataSet[position].favorite
                            notifyItemChanged(position)

                            itemSelectedListener?.invoke(selectedItemCount)
                        } else {
                            selectedItemCount = MAX_FAVORITE_CATEGORY_COUNT
                        }
                    }
                }

//                holder.itemView.setOnClickListener {
//                    dataSet[position].favorite = !dataSet[position].favorite
//                    holder.itemView.apply {
//                        fc_item_unchecked.visibility = if (dataSet[position].favorite) View.GONE else View.VISIBLE
//                        fc_item_checked.visibility = if (dataSet[position].favorite) View.VISIBLE else View.GONE
//                    }
//                }
            }

            inner class FCItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
        }
    }
}

enum class Mode {
    NEW, MODIFY
}