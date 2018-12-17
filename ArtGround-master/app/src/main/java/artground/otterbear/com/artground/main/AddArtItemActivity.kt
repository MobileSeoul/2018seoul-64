package artground.otterbear.com.artground.main

import android.Manifest
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.view.MenuItem
import android.view.View
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import artground.otterbear.com.artground.R
import artground.otterbear.com.artground.common.AppLogger
import artground.otterbear.com.artground.common.Values
import artground.otterbear.com.artground.db.model.ArtItem
import artground.otterbear.com.artground.db.model.CategoryItem
import artground.otterbear.com.artground.db.model.SimpleArtItem
import artground.otterbear.com.artground.db.viewmodel.ArtItemViewModel
import artground.otterbear.com.artground.db.viewmodel.CategoryViewModel
import artground.otterbear.com.artground.dialog.ImagePickDialogFragment
import kotlinx.android.synthetic.main.activity_add_art_item.*
import kotlinx.android.synthetic.main.form_additional_info_section.*
import kotlinx.android.synthetic.main.form_image_section.*
import kotlinx.android.synthetic.main.form_main_info_section.*
import kotlinx.android.synthetic.main.form_main_info_section.view.*
import kotlinx.android.synthetic.main.form_time_place_info_section.*

class AddArtItemActivity : AppCompatActivity() {

    private val categoryViewModel by lazy { ViewModelProviders.of(this).get(CategoryViewModel::class.java) }
    private val artItemViewModel by lazy { ViewModelProviders.of(this).get(ArtItemViewModel::class.java) }
    private val categoryItems = mutableListOf<CategoryItem>()
    private var itemImagePath: String? = null
    private var preArtItem: SimpleArtItem? = null
    private var mode: Modes = Modes.ADD

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_art_item)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        preArtItem = intent.extras?.getSerializable(Values.EXTRA_ART_ITEM) as? SimpleArtItem
        preArtItem?.let {
            loadPreArtItem(it)
            mode = Modes.UPDATE
        }

        setTitle(if (mode == Modes.ADD) R.string.registration_artitem_title else R.string.modify_artitem_title)
        submitBtnText.setText(if (mode == Modes.ADD) R.string.registration else R.string.edit)

        categoryViewModel.getRawAllCategories().observe(this, Observer { r ->
            r?.let {
                categoryItems.addAll(it)
                categoryItems.add(createHintCategoryItem())

                val categoryNames = mutableListOf<String>()
                var categorySelection = -1
                for (i in 0 until categoryItems.size) {
                    preArtItem?.let { pre ->
                        if (pre.categoryName == categoryItems[i].name) {
                            categorySelection = i
                        }
                    }
                    categoryNames.add(categoryItems[i].name)
                }

                val adapter = CategorySpinnerAdapter(this, R.layout.category_spinner_item, categoryNames)
                adapter.setDropDownViewResource(R.layout.category_spinner_dropdown_item)
                categorySpinner.apply {
                    if (categorySelection == -1) categorySelection = adapter.count
                    this.adapter = adapter
                    setSelection(categorySelection)
                    isClickable = true
                }
                spinnerArrowIcon.visibility = View.VISIBLE
            }
        })

        setSectionTitleSpan(formMainInfoSection.sectionTitle, true)
        setSectionTitleSpan(formTimePlaceInfoSection.sectionTitle, true)
        setSectionTitleSpan(formAdditionalInfoSection.sectionTitle, false)
        setSectionTitleSpan(formImageSection.sectionTitle, false)

        formStartDatePicker.setDateInfoListener {
            AppLogger.LOGE("startDate: $it")
            formEndDatePicker.getDateInfo()?.run {
                if (it.time > this.time) {
                    Toast.makeText(formStartDatePicker.context, R.string.start_date_error_msg_1, Toast.LENGTH_SHORT).show()
                    return@setDateInfoListener false
                }
            }
            return@setDateInfoListener true
        }

        formEndDatePicker.setDateInfoListener {
            AppLogger.LOGE("endDate: $it")
            formStartDatePicker.getDateInfo()?.run {
                if (it.time < this.time) {
                    Toast.makeText(formStartDatePicker.context, R.string.end_date_error_msg_1, Toast.LENGTH_SHORT).show()
                    return@setDateInfoListener false
                }
            }
            return@setDateInfoListener true
        }

        imagePickFrame.setOnClickListener { it ->
            checkPermissionForImagePick()
        }

        submitBtn.setOnClickListener {
            val result = submitArtItemForm()
            AppLogger.LOGE("result: $result")
            result.failedReason?.let { reason ->
                Toast.makeText(this, reason.errorMsgResId, Toast.LENGTH_SHORT).show()
            }
            result.artItem?.let { newItem ->
                when (mode) {
                    Modes.ADD -> postInsertArtItem(newItem)
                    Modes.UPDATE -> postUpdateArtItem(newItem)
                }
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item?.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_PICK) {
            data?.let { intent ->
                selectedImage.visibility = View.VISIBLE
                emptySectionGroup.visibility = View.GONE

                val cursor = contentResolver.query(intent.data, null, null, null, null)
                cursor.moveToNext()
                val path = cursor.getString(cursor.getColumnIndex("_data"))
                cursor.close()
                AppLogger.LOGE("path: $path")
                GlideApp.with(this).load(path).into(selectedImage)
                itemImagePath = path
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            clickImageFrame()
        } else {
            Toast.makeText(this, R.string.error_permission_denied_for_image_pick, Toast.LENGTH_SHORT).show()
        }
    }

    private fun postUpdateArtItem(updateItem: ArtItem) {
        artItemViewModel.updateArtItem(updateItem) { id ->
            AppLogger.LOGE("id: $id")
            if (id > 0) {
                Toast.makeText(this, R.string.complete_modify_art_item, Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    private fun postInsertArtItem(newItem: ArtItem) {
        artItemViewModel.insertArtItem(newItem) { id ->
            AppLogger.LOGE("id: $id")
            if (id > -1) {
                Toast.makeText(this, R.string.complete_add_art_item, Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    private fun checkPermissionForImagePick() {
        ContextCompat.checkSelfPermission(applicationContext, Manifest.permission.READ_EXTERNAL_STORAGE).let { result ->
            if (result == PackageManager.PERMISSION_GRANTED) {
                clickImageFrame()
            } else {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 100)
            }
        }
    }

    private fun loadPreArtItem(item: SimpleArtItem) {
        inputArtItemName.setText(item.title)
        formStartDatePicker.setDateInfo(item.startDate)
        formEndDatePicker.setDateInfo(item.endDate)
        inputTime.setText(item.time)
        inputLocation.setText(item.place)
        inputDesc.setText(item.etcDesc)
        inputUseFee.setText(item.useFee)
        inputWebPage.setText(item.orgLink)
        inputContact.setText(item.inquiry)

        item.mainImg?.apply {
            if (!isBlank()) {
                GlideApp.with(this@AddArtItemActivity).load(this).into(selectedImage)
                selectedImage.visibility = View.VISIBLE
                emptySectionGroup.visibility = View.GONE
            }
        }
    }

    private fun clickImageFrame() {
        if (selectedImage.visibility == View.VISIBLE) {
            ImagePickDialogFragment().apply {
                setOnItemClickListener { pos ->
                    if (pos == 0) {
                        doActionImagePick()
                    } else {
                        this@AddArtItemActivity.selectedImage.setImageDrawable(null)
                        this@AddArtItemActivity.selectedImage.visibility = View.GONE
                        this@AddArtItemActivity.emptySectionGroup.visibility = View.VISIBLE
                        itemImagePath = null
                    }
                }
                show(supportFragmentManager, "image_pick")
            }

        } else {
            doActionImagePick()
        }
    }

    private fun createHintCategoryItem(): CategoryItem {
        return CategoryItem(name = getString(R.string.error_no_select_category), imgResName = "", favorite = false, themeColor = "")
    }

    private fun submitArtItemForm(): SubmitResult {
        val selectedCategoryPosition = categorySpinner.selectedItemPosition

        val id = preArtItem?._id

        val cid = if (selectedCategoryPosition == categorySpinner.adapter.count) {
            return SubmitResult(failedReason = FailedReason.FAILED_NO_SELECT_CATEGORY)
        } else {
            categoryItems[selectedCategoryPosition]._id
        }

        val name = inputArtItemName.text.toString().apply {
            if (isBlank()) return SubmitResult(failedReason = FailedReason.FAILED_EMPTY_NAME)
        }

        val startDate = formStartDatePicker.getDateInfo()?.apply {} ?: run {
            return SubmitResult(failedReason = FailedReason.FAILED_EMPTY_START_DATE)
        }

        val endDate = formEndDatePicker.getDateInfo()?.apply {} ?: run {
            return SubmitResult(failedReason = FailedReason.FAILED_EMPTY_END_DATE)
        }

        val time = inputTime.text.toString().apply {
            if (isBlank()) return SubmitResult(failedReason = FailedReason.FAILED_EMPTY_TIME)
        }

        AppLogger.LOGE("time: $time")

        val location = inputLocation.text.toString().apply {
            if (isBlank()) return SubmitResult(failedReason = FailedReason.FAILED_EMPTY_PLACE)
        }
        val desc = inputDesc.text.toString().run { if (isBlank()) null else this }
        val useFee = inputUseFee.text.toString().run { if (isBlank()) null else this }
        val webPage = inputWebPage.text.toString().run { if (isBlank()) null else this }
        val contact = inputContact.text.toString().run { if (isBlank()) null else this }

        return SubmitResult(artItem = ArtItem(
                _id = id,
                title = name,
                startDate = startDate,
                endDate = endDate,
                time = time,
                place = location,
                etcDesc = desc,
                useFee = useFee,
                orgLink = webPage,
                inquiry = contact,
                mainImg = itemImagePath,
                cid = cid?.toInt()
        ))
    }

    private fun doActionImagePick() {
        Intent(Intent.ACTION_PICK).apply {
            type = MediaStore.Images.Media.CONTENT_TYPE
            data = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            startActivityForResult(this, REQUEST_IMAGE_PICK)
        }
    }

    private fun setSectionTitleSpan(titleView: TextView, isEssential: Boolean) {
        val spanColor = if (isEssential) ContextCompat.getColor(this, R.color.colorPrimary) else Color.DKGRAY
        val sb = SpannableStringBuilder(titleView.text).apply {
            setSpan(ForegroundColorSpan(spanColor), lastIndex - 4, length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        }
        titleView.text = sb
    }

    companion object {
        const val REQUEST_IMAGE_PICK = 100

        class CategorySpinnerAdapter(context: Context, spinnerResId: Int, dataSet: MutableList<String>) : ArrayAdapter<String>(context, spinnerResId, 0, dataSet) {
            override fun getCount(): Int {
                val count = super.getCount()
                return if (count > 0) count - 1 else count
            }
        }
    }
}

data class SubmitResult(val artItem: ArtItem? = null, val failedReason: FailedReason? = null)

enum class FailedReason(val errorMsgResId: Int) {
    FAILED_EMPTY_NAME(R.string.error_empty_art_item_name),
    FAILED_NO_SELECT_CATEGORY(R.string.error_no_select_category),
    FAILED_EMPTY_TIME(R.string.error_empty_art_item_time),
    FAILED_EMPTY_PLACE(R.string.error_empty_art_item_place),
    FAILED_EMPTY_START_DATE(R.string.error_empty_start_date),
    FAILED_EMPTY_END_DATE(R.string.error_empty_end_date)
}

enum class Modes {
    ADD, UPDATE
}