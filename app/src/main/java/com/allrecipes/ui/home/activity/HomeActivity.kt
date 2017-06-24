package com.allrecipes.ui.home.activity

import android.os.Bundle
import android.os.SystemClock
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import com.allrecipes.R
import com.allrecipes.di.managers.FirebaseDatabaseManager
import com.allrecipes.presenters.HomeScreenPresenter
import com.allrecipes.ui.BaseActivity
import com.allrecipes.ui.home.viewholders.HomeScreenItem
import com.allrecipes.ui.home.viewholders.HomeScreenItemFactory
import com.allrecipes.ui.home.viewholders.HomeScreenModelItemWrapper
import com.allrecipes.model.YoutubeItem
import com.allrecipes.ui.home.views.HomeScreenView
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.adapters.FooterAdapter
import com.mikepenz.fastadapter.adapters.GenericItemAdapter
import com.mikepenz.fastadapter_extensions.items.ProgressItem
import com.mikepenz.fastadapter_extensions.scroll.EndlessRecyclerOnScrollListener
import kotlinx.android.synthetic.main.activity_home.*
import javax.inject.Inject

class HomeActivity : BaseActivity(), HomeScreenView {

    private val DOUBLE_CLICK_TIMEOUT = 1000
    private var lastClickTime: Long = 0

    lateinit var fastAdapter: FastAdapter<HomeScreenItem>
    lateinit var footerAdapter: FooterAdapter<ProgressItem>
    lateinit var homeScreenItemAdapter: GenericItemAdapter<HomeScreenModelItemWrapper, HomeScreenItem>
    lateinit var homeScreenItemFactory: HomeScreenItemFactory
    lateinit var layoutManager: LinearLayoutManager
    lateinit var onVendorsScrollListener: EndlessRecyclerOnScrollListener

    @Inject
    lateinit var presenter: HomeScreenPresenter

    @Inject
    lateinit var firebaseDatabaseManager: FirebaseDatabaseManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        getApp().createHomeScreenComponent(this).inject(this)

        presenter.fetchYoutubeChannelVideos()

        firebaseDatabaseManager.getCategories().subscribe(
                { categories ->
                    categories.forEach { println(it.name) }
                    /*val root = LinearLayout(this)
                    root.orientation = LinearLayout.VERTICAL
                    categories.forEach {
                        val textView: TextView = TextView(this)
                        textView.text = it.name
                        root.addView(textView)

                        setContentView(root)*/
                },
                { error -> print("error $error") }
        )

        initSwipeRefresh()
        initRecyclerViewAdapter()
    }

    private fun initRecyclerViewAdapter() {
        list.setHasFixedSize(true)
        list.isClickable = true
        layoutManager = LinearLayoutManager(this)
        list.layoutManager = layoutManager

        fastAdapter = FastAdapter()
        homeScreenItemFactory = HomeScreenItemFactory(
            resources.getDimensionPixelSize(R.dimen.toolbar_layout_image_height)
        )
        homeScreenItemAdapter = GenericItemAdapter<HomeScreenModelItemWrapper, HomeScreenItem>(homeScreenItemFactory)
        footerAdapter = FooterAdapter<ProgressItem>()
        list.adapter = footerAdapter.wrap(homeScreenItemAdapter.wrap(fastAdapter))
        createOnVendorsScrollListener()
        list.addOnScrollListener(onVendorsScrollListener)
        fastAdapter.withOnClickListener(onOrdersListClickListener)
    }

    private val onOrdersListClickListener = FastAdapter.OnClickListener<HomeScreenItem> { v, adapter, item, position ->
        if (SystemClock.elapsedRealtime() > lastClickTime + DOUBLE_CLICK_TIMEOUT) {
            lastClickTime = SystemClock.elapsedRealtime()
            if (item.type == R.id.home_screen_video_item) {
                onItemRestaurantClick(v, item as YoutubeItem)
            }
        }

        true
    }

    private fun onItemRestaurantClick(v: View, item: YoutubeItem) {

    }

    private fun createOnVendorsScrollListener() {
        onVendorsScrollListener = object : EndlessRecyclerOnScrollListener(footerAdapter) {
            override fun onLoadMore(currentPage: Int) {
                removeBottomListProgress()
                if (existRestaurantItemsInAdapter()) {
                    addFooterLoadingView()
                    //presenter.onLoadMore(currentPage, area, currentFilterSettings, searchCriteria)
                }
            }
        }
    }

    fun removeBottomListProgress() {
        footerAdapter.clear()
    }

    fun existRestaurantItemsInAdapter(): Boolean {
        val itemExist = homeScreenItemAdapter.adapterItems.any {
            it.type == R.id.home_screen_video_item
        }

        return itemExist
    }

    fun addFooterLoadingView() {
        if (footerAdapter.adapterItems.isEmpty()) {
            footerAdapter.add(ProgressItem().withEnabled(false))
        }
    }

    private fun initSwipeRefresh() {
        restaurants_swipe_container.setOnRefreshListener({
            presenter.fetchYoutubeChannelVideos()
        })
    }

    override fun addYoutubeItemToAdapter(item: YoutubeItem) {
        homeScreenItemAdapter
                .addModel(HomeScreenModelItemWrapper(item, R.id.home_screen_video_item))
    }
}
