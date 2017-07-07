package com.allrecipes.ui.home.activity

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.SystemClock
import android.support.v4.app.ActivityCompat
import android.support.v4.app.ActivityOptionsCompat
import android.support.v4.util.Pair
import android.support.v7.widget.LinearLayoutManager
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import com.allrecipes.R
import com.allrecipes.di.managers.FirebaseDatabaseManager
import com.allrecipes.model.Category
import com.allrecipes.presenters.HomeScreenPresenter
import com.allrecipes.ui.BaseActivity
import com.allrecipes.ui.home.adapters.ChannelsListDropdownAdapter
import com.allrecipes.ui.home.viewholders.HomeScreenItem
import com.allrecipes.ui.home.viewholders.HomeScreenItemFactory
import com.allrecipes.ui.home.viewholders.HomeScreenModelItemWrapper
import com.allrecipes.ui.home.viewholders.YoutubeItem
import com.allrecipes.ui.home.views.HomeScreenView
import com.allrecipes.ui.videodetails.activity.VideoActivity
import com.allrecipes.util.KeyboardUtils
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
    private var addressListCloseAnimator: ObjectAnimator? = null
    private var isAddressesDropDownVisible = false

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
                        presenter.fetchPlaylistsAndVideos(it.channelId)
                        TODO("save categories/ present them !")
                    }
                    initAddressListOverlayAdapter(categories, 0)
                },
                { error -> print("error $error") }
        )

        initSwipeRefresh()
        initRecyclerViewAdapter()
        var con: Context = this
        searchEditText.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(text: CharSequence?, start: Int, before: Int, count: Int) {
                //search_clear_button.visibility = if (text.isNotEmpty()) View.VISIBLE else View.GONE
                val prevSearchCriteria = if (searchCriteria == null) "" else searchCriteria
                searchCriteria = if (text!!.length < 3) "" else text.toString()
                if (!TextUtils.equals(searchCriteria, prevSearchCriteria)) {
                    presenter.fetchYoutubeChannelVideos(null, searchCriteria)
                }
            }
        })

        list.setOnTouchListener(View.OnTouchListener { v, event ->
            if (containerTopAddressList != null && containerTopAddressList.getVisibility() == View.VISIBLE) {
                closeAddressListOverlay()
            }
            if (searchEditText != null) {
                hideKeyboard(searchEditText)
            }

            false
        })

        title_text.setOnClickListener { v ->
            KeyboardUtils.hideKeyboard(this)
            if (isAddressListOverlayContainerVisible()) {
                closeAddressListOverlay()
            } else {
                showAddressListOverlay()
            }
        }

        initActionBar()
    }

    fun isAddressListOverlayContainerVisible(): Boolean {
        return containerTopAddressList.visibility == View.VISIBLE
    }

    override fun onBackPressed() {
        if (isAddressesDropDownVisible) {
            closeAddressListOverlay()
            return
        }

        super.onBackPressed()
    }

    override fun onStop() {
        addressListCloseAnimator?.removeAllListeners()
        super.onStop()
    }

    private fun hideKeyboard(view: View) {
        val inputMethodManger = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManger.hideSoftInputFromWindow(view.windowToken, 0)
    }

    fun initAddressListOverlayAdapter(addresses: List<Category>, selectedPosition: Int) {
        dropdown_addresses_listview.adapter = ChannelsListDropdownAdapter(applicationContext, addresses, selectedPosition)
        dropdown_addresses_listview.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
            presenter.onChannelListClick(addresses[position].channelId)
        }
        trans_overlay.setOnTouchListener(View.OnTouchListener { v, event ->
            closeAddressListOverlay()
            true
        })
        trans_overlay.isClickable = true
        trans_overlay.isFocusable = true
        trans_overlay.isFocusableInTouchMode = true
    }

    fun closeAddressListOverlay() {
        val duration = resources.getInteger(android.R.integer.config_mediumAnimTime)
        alphaAnimateHideAddressListTransitionOverlay(duration)
        animateTranslationYHideAddressList(duration)
    }

    fun showAddressListOverlay() {
        dropdown_addresses_listview.translationY = (-dropdown_addresses_listview.measuredHeight).toFloat()
        showAndAnimateAddressList()
    }

    fun changeActionBarIconToClear() {
        val actionBar = supportActionBar
        title_text.visibility = View.VISIBLE
        activity_toolbar.setNavigationOnClickListener(View.OnClickListener({
            closeAddressListOverlay()
        }))
    }

    private fun showAndAnimateAddressList() {
        isAddressesDropDownVisible = true
        containerTopAddressList.setVisibility(View.VISIBLE)
        app_bar.visibility = View.INVISIBLE
        val duration = resources.getInteger(android.R.integer.config_mediumAnimTime)
        changeActionBarIconToClear()

        alphaAnimateShowAddressListTransitionOverlay(duration)
        animateTranslationYShowAddressList(duration)

        if (title_text != null) {
            title_text.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_small_arrow_down_white, 0)
        }
    }

    private fun animateTranslationYShowAddressList(duration: Int) {
        dropdown_addresses_listview.setLayerType(View.LAYER_TYPE_HARDWARE, null)
        val height:Float = (-dropdown_addresses_listview.measuredHeight).toFloat()
        val addressListAnimator = ObjectAnimator
                .ofFloat(dropdown_addresses_listview, "translationY", height, 0f)
                .setDuration(duration.toLong())
        addressListAnimator.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                super.onAnimationEnd(animation)
                if (dropdown_addresses_listview != null) {
                    dropdown_addresses_listview.setLayerType(View.LAYER_TYPE_NONE, null)
                }
            }
        })
        addressListAnimator.start()
    }

    private fun animateTranslationYHideAddressList(duration: Int) {
        dropdown_addresses_listview.setLayerType(View.LAYER_TYPE_HARDWARE, null)
        val height:Float = (-dropdown_addresses_listview.measuredHeight).toFloat()
        addressListCloseAnimator = ObjectAnimator
                .ofFloat(dropdown_addresses_listview, "translationY", 0f, height)
        addressListCloseAnimator?.duration = duration.toLong()
        addressListCloseAnimator?.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                super.onAnimationEnd(animation)
                if (dropdown_addresses_listview != null) {
                    dropdown_addresses_listview.setLayerType(View.LAYER_TYPE_NONE, null)
                }
                isAddressesDropDownVisible = false
                changeActionBarDefaultDrawerIcon()
                if (containerTopAddressList != null) {
                    containerTopAddressList.setVisibility(View.INVISIBLE)
                }
                if (app_bar != null) {
                    app_bar.visibility = View.VISIBLE
                }
                if (title_text != null) {
                    title_text.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_small_arrow_down_white, 0)
                }
            }
        })
        addressListCloseAnimator?.start()
    }

    fun changeActionBarDefaultDrawerIcon() {
        val actionBar = supportActionBar
        actionBar!!.setHomeAsUpIndicator(null)
        activity_toolbar.setNavigationOnClickListener(null)
        title_text.visibility = View.VISIBLE
    }

    private fun alphaAnimateShowAddressListTransitionOverlay(duration: Int) {
        trans_overlay.setLayerType(View.LAYER_TYPE_HARDWARE, null)
        val addressListTransparentOverlayAnimator = ObjectAnimator
                .ofFloat(trans_overlay, "alpha", 0f, 1f)
                .setDuration(duration.toLong())
        addressListTransparentOverlayAnimator.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                super.onAnimationEnd(animation)
                if (trans_overlay != null) {
                    trans_overlay.setLayerType(View.LAYER_TYPE_NONE, null)
                }
            }
        })
        addressListTransparentOverlayAnimator.start()
    }

    private fun alphaAnimateHideAddressListTransitionOverlay(duration: Int) {
        trans_overlay.setLayerType(View.LAYER_TYPE_HARDWARE, null)
        val addressListTransparentOverlayAnimator = ObjectAnimator
                .ofFloat(trans_overlay, "alpha", 0f)
                .setDuration(duration.toLong())
        addressListTransparentOverlayAnimator.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                super.onAnimationEnd(animation)
                if (trans_overlay != null) {
                    trans_overlay.setLayerType(View.LAYER_TYPE_NONE, null)
                }
            }
        })
        addressListTransparentOverlayAnimator.start()
    }

    fun initActionBar() {
        setSupportActionBar(activity_toolbar)
        val actionBar = supportActionBar
        actionBar!!.setDisplayShowTitleEnabled(false)
        actionBar.setDisplayHomeAsUpEnabled(true)
        actionBar.setHomeButtonEnabled(true)
        //actionBar.setHomeAsUpIndicator(resources.getDrawable(R.drawable.ic_dehaze_white_24dp))
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

    override fun clearAdapterItems() {
        homeScreenItemAdapter.clearModel()
        homeScreenItemAdapter.notifyDataSetChanged()
        list.removeOnScrollListener(onVendorsScrollListener)
        createOnVendorsScrollListener()
        list.addOnScrollListener(onVendorsScrollListener)
    }

    private fun onItemRestaurantClick(v: View, item: YoutubeItem) {
        val intent = VideoActivity.newIntent(this, item.item)

        startRestaurantActivityWithTransition(v, intent)
        overridePendingTransition(0, 0)
    }

    private fun createOnVendorsScrollListener() {
        var con: Context  = this
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
}
