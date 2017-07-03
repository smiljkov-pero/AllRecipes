package com.allrecipes.ui.home.activity

import android.content.Intent
import android.os.Bundle
import android.os.SystemClock
import android.support.v4.app.ActivityCompat
import android.support.v4.app.ActivityOptionsCompat
import android.support.v4.util.Pair
import android.support.v7.widget.LinearLayoutManager
import android.text.TextUtils
import android.view.View
import butterknife.OnTextChanged
import com.allrecipes.R
import com.allrecipes.di.managers.FirebaseDatabaseManager
import com.allrecipes.presenters.HomeScreenPresenter
import com.allrecipes.ui.BaseActivity
import com.allrecipes.ui.home.viewholders.HomeScreenItem
import com.allrecipes.ui.home.viewholders.HomeScreenItemFactory
import com.allrecipes.ui.home.viewholders.HomeScreenModelItemWrapper
import com.allrecipes.ui.home.viewholders.YoutubeItem
import com.allrecipes.ui.home.views.HomeScreenView
import com.allrecipes.ui.videodetails.activity.VideoActivity
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
    private val REQ_CODE_RESTAURANT = 998

    lateinit var fastAdapter: FastAdapter<HomeScreenItem>
    lateinit var footerAdapter: FooterAdapter<ProgressItem>
    lateinit var homeScreenItemAdapter: GenericItemAdapter<HomeScreenModelItemWrapper, HomeScreenItem>
    lateinit var homeScreenItemFactory: HomeScreenItemFactory
    lateinit var layoutManager: LinearLayoutManager
    lateinit var onVendorsScrollListener: EndlessRecyclerOnScrollListener
    var searchCriteria = ""

    @Inject
    lateinit var presenter: HomeScreenPresenter

    @Inject
    lateinit var firebaseDatabaseManager: FirebaseDatabaseManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        getApp().createHomeScreenComponent(this).inject(this)

        presenter.fetchYoutubeChannelVideos(null, "")

        firebaseDatabaseManager.getCategories().subscribe(
                { categories ->
                    categories.forEach {
                        println(it.name)
                        presenter.fetchPlaylists(it.channelId)
                    }
                    TODO("save categories/ present them !")
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
        val intent = VideoActivity.newIntent(this, item.item)

        startRestaurantActivityWithTransition(v, intent)
        overridePendingTransition(0, 0)
    }

    private fun createOnVendorsScrollListener() {
        onVendorsScrollListener = object : EndlessRecyclerOnScrollListener(footerAdapter) {
            override fun onLoadMore(currentPage: Int) {
                removeBottomListProgress()
                if (existRestaurantItemsInAdapter()) {
                    addFooterLoadingView()
                    presenter.onLoadMore(searchCriteria)
                }
            }
        }
    }

    override fun removeBottomListProgress() {
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
        swipeContainer.setOnRefreshListener({
            presenter.fetchYoutubeChannelVideos(null, "")
        })
    }

    override fun addYoutubeItemToAdapter(item: com.allrecipes.model.YoutubeItem) {
        homeScreenItemAdapter.addModel(HomeScreenModelItemWrapper(item, R.id.home_screen_video_item))
    }


    override fun showLoading() {
        super.showLoading()
        swipeContainer.isRefreshing = false
    }

    internal fun startRestaurantActivityWithTransition(v: View, intent: Intent) {
        val options = ActivityOptionsCompat.makeSceneTransitionAnimation(
            this,
            Pair<View, String>(v.findViewById(R.id.videoThumbnail), "VideoImageTransition")
        )
        ActivityCompat.startActivityForResult(this, intent, REQ_CODE_RESTAURANT, options.toBundle())
    }

    @OnTextChanged(R.id.search_edit_text)
    internal fun onSearchTextChanged(text: CharSequence) {
        //search_clear_button.visibility = if (text.isNotEmpty()) View.VISIBLE else View.GONE
        val prevSearchCriteria = if (searchCriteria == null) "" else searchCriteria
        searchCriteria = if (text.length < 3) "" else text.toString()
        if (!TextUtils.equals(searchCriteria, prevSearchCriteria)) {
            presenter.fetchYoutubeChannelVideos(null, searchCriteria)
        }
    }
}
