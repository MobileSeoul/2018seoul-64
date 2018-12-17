package artground.otterbear.com.artground.db.viewmodel

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import artground.otterbear.com.artground.db.model.*
import artground.otterbear.com.artground.db.repository.ArtItemRepository
import artground.otterbear.com.artground.db.repository.DashboardCategoryFilter
import java.util.*

class ArtItemViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: ArtItemRepository = ArtItemRepository(application)
    private lateinit var dashboardActiveArtItems: LiveData<MutableList<SimpleArtItem>>
    private lateinit var dashboardExpectArtItems: LiveData<MutableList<SimpleArtItem>>

    fun getAllArtItems() = repository.getAllArtItems()
    fun getAllUserArtItems() = repository.getAllUserArtItems()
    fun getArtItemById(id: Long, listener: ((SimpleArtItem) -> Unit)) {
        repository.getArtItemById(id, listener)
    }

    fun getDashboardActiveArtItems(filter: DashboardCategoryFilter): LiveData<MutableList<SimpleArtItem>> {
        dashboardActiveArtItems = repository.getDashboardActiveArtItems(filter)
        return dashboardActiveArtItems
    }

    fun getDashboardExpectArtItems(filter: DashboardCategoryFilter): LiveData<MutableList<SimpleArtItem>> {
        dashboardExpectArtItems = repository.getDashboardExpectArtItems(filter)
        return dashboardExpectArtItems
    }

    fun getDashboardReviewItems(filter: DashboardCategoryFilter): LiveData<MutableList<DashboardReviewItem>> {
        return repository.getDashboardReviewItems(filter)
    }

    fun getActiveArtItems(filter: DashboardCategoryFilter): LiveData<MutableList<SimpleArtItem>> {
        return repository.getActiveArtItems(filter)
    }

    fun getExpectArtItems(filter: DashboardCategoryFilter): LiveData<MutableList<SimpleArtItem>> {
        return repository.getExpectArtItems(filter)
    }

    fun getReviewItemsByArtItemId(aid: Long): LiveData<MutableList<SimpleReviewItem>> {
        return repository.getReviewItemsByArtItemId(aid)
    }

    fun findArtItem(cid: Long, start: Date, end: Date): LiveData<MutableList<SimpleArtItem>> {
        return repository.findArtItem(cid, start, end)
    }

    fun insertReviewItem(reviewItem: ReviewItem) {
        repository.insertReviewItem(reviewItem)
    }

    fun insertArtItem(artItem: ArtItem, listener: ((Long) -> Unit)? = null) {
        repository.insertArtItem(artItem, listener)
    }

    fun deleteArtItem(aid: Long) {
        repository.deleteArtItem(aid)
    }

    fun updateArtItem(artItem: ArtItem, listener: ((Int) -> Unit)? = null) {
        repository.updateArtItem(artItem, listener)
    }
}