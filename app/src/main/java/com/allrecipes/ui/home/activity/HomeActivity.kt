package com.allrecipes.ui.home.activity

import android.accounts.Account
import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Typeface
import android.os.Build
import android.os.Bundle
import android.os.SystemClock
import android.support.annotation.RequiresApi
import android.support.v4.app.ActivityCompat
import android.support.v4.app.ActivityOptionsCompat
import android.support.v4.util.Pair
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.TextUtils
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.Window
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import com.allrecipes.R
import com.allrecipes.custom.RoundedBackgroundSpan
import com.allrecipes.model.Channel
import com.allrecipes.model.FiltersAndSortSettings
import com.allrecipes.model.YoutubeItem
import com.allrecipes.model.playlist.YoutubePlaylistWithVideos
import com.allrecipes.presenters.HomeScreenPresenter
import com.allrecipes.ui.BaseActivity
import com.allrecipes.ui.filters.FiltersActivity
import com.allrecipes.ui.home.adapters.ChannelsListDropdownAdapter
import com.allrecipes.ui.home.listener.ScrollStateListener
import com.allrecipes.ui.home.viewholders.BaseHomeScreenItem
import com.allrecipes.ui.home.viewholders.HomeScreenItemFactory
import com.allrecipes.ui.home.viewholders.HomeScreenModelItemWrapper
import com.allrecipes.ui.home.viewholders.items.SwipeLaneChannelItem
import com.allrecipes.ui.home.viewholders.items.YoutubeVideoItem
import com.allrecipes.ui.home.viewholders.listeners.SwipeLaneListener
import com.allrecipes.ui.home.views.HomeScreenView
import com.allrecipes.ui.videodetails.activity.VideoActivity
import com.allrecipes.util.Constants
import com.allrecipes.util.KeyboardUtils
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.firebase.crash.FirebaseCrash
import com.jakewharton.rxbinding2.view.RxView
import com.jakewharton.rxbinding2.widget.RxTextView
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.adapters.FooterAdapter
import com.mikepenz.fastadapter.adapters.GenericItemAdapter
import com.mikepenz.fastadapter_extensions.items.ProgressItem
import com.mikepenz.fastadapter_extensions.scroll.EndlessRecyclerOnScrollListener
import de.foodora.android.networkutils.NetworkQuality
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import it.sephiroth.android.library.tooltip.Tooltip
import kotlinx.android.synthetic.main.activity_home.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class HomeActivity : BaseActivity(), HomeScreenView, SwipeLaneListener, ScrollStateListener {

    private val DOUBLE_CLICK_TIMEOUT = 1000
    private var lastClickTime: Long = 0
    private val REQ_CODE_RECIPE_VIDEO = 998
    private val REQ_CODE_CHANGE_FILTER = 996
    private val ADDRESS_TOOLTIP_ID = 101

    lateinit var fastAdapter: FastAdapter<BaseHomeScreenItem>
    lateinit var footerAdapter: FooterAdapter<ProgressItem>
    lateinit var homeScreenItemAdapter: GenericItemAdapter<HomeScreenModelItemWrapper, BaseHomeScreenItem>
    lateinit var homeScreenItemFactory: HomeScreenItemFactory
    lateinit var layoutManager: LinearLayoutManager
    lateinit var onVendorsScrollListener: EndlessRecyclerOnScrollListener
    var searchCriteria: String? = ""
    private var addressListCloseAnimator: ObjectAnimator? = null
    private var isAddressesDropDownVisible = false
    private lateinit var searchTextSubscription: Disposable
    private var currentFilterSettings: FiltersAndSortSettings = FiltersAndSortSettings()
    private var loggedInAccount: Account? = null
    private var listScrollState: Int = 0

    @Inject
    lateinit var presenter: HomeScreenPresenter

    @Inject
    lateinit var networkQuality: NetworkQuality

    companion object {
        val KEY_GOOGLE_ACCOUNT_USER = "KEY_GOOGLE_ACCOUNT_USER"

        fun newIntent(context: Context, loggedInAccount: Account): Intent {
            val intent = Intent(context, HomeActivity::class.java)
            intent.putExtra(KEY_GOOGLE_ACCOUNT_USER, loggedInAccount)

            return intent
        }

        fun newIntent(context: Context): Intent {
            val intent = Intent(context, HomeActivity::class.java)

            return intent
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        getApp().createHomeScreenComponent(this).inject(this)
        logCustomError("App started")
        if (savedInstanceState == null) {
            if (intent.hasExtra(KEY_GOOGLE_ACCOUNT_USER)) {
                loggedInAccount = intent.getParcelableExtra(KEY_GOOGLE_ACCOUNT_USER)
            }
        } else {
            if (savedInstanceState.containsKey(KEY_GOOGLE_ACCOUNT_USER)) {
                loggedInAccount = savedInstanceState.getParcelable(KEY_GOOGLE_ACCOUNT_USER)
            }
        }
        getUserYoutubeToken()

        initSwipeRefresh()
        initRecyclerViewAdapter()

        list.setOnTouchListener({ view: View, motionEvent: MotionEvent ->
            if (containerTopAddressList != null && containerTopAddressList.visibility == View.VISIBLE) {
                closeAddressListOverlay()
            }
            if (searchEditText != null) {
                hideKeyboard(searchEditText)
            }

            false
        })

        title_text.setOnClickListener {
            onClickToolbarText()
        }

        search_clear_button.setOnClickListener {
            searchEditText.setText("")
        }

        RxView.clicks(sortFilter)
            .debounce(700, TimeUnit.MILLISECONDS, AndroidSchedulers.mainThread())
            .subscribe({
                val filterIntent = FiltersActivity.newIntent(this@HomeActivity, currentFilterSettings)
                startActivityForResult(filterIntent, REQ_CODE_CHANGE_FILTER)
            })

        initActionBar()
        setDefaultAddressListDropDownIcons()
    }

    override fun getScreenNameForTracking(): String {
        return "HomeScreen"
    }

    fun logCustomError(message: String) {
        try {
            FirebaseCrash.log(message)
        } catch (e: Exception) {
        }

    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putParcelable(KEY_GOOGLE_ACCOUNT_USER, loggedInAccount)
        super.onSaveInstanceState(outState)
    }

    override fun onPause() {
        super.onPause()
        if (isFinishing) {
            presenter.unbindAll()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            REQ_CODE_CHANGE_FILTER -> if (resultCode == Activity.RESULT_OK) {
                currentFilterSettings = data!!.getParcelableExtra(FiltersActivity.KEY_SORT_BY_SETTINGS)
                presenter.fetchYoutubeChannelVideos(null, searchCriteria, currentFilterSettings)
            }
        }
    }

    private fun getUserYoutubeToken() {
        if (loggedInAccount != null) {
            showLoading()
            Observable.fromCallable {
                val credential = GoogleAccountCredential.usingOAuth2(
                    this@HomeActivity,
                    setOf(Constants.YOUTUBE_SCOPE)
                )
                credential.selectedAccount = loggedInAccount
                val token = credential.token

                token
            }.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ token ->
                    hideLoading()
                    if (token != null) {
                        presenter.onCreate(token)
                    } else {
                        presenter.onCreate(null)
                    }
                }, {
                    hideLoading()
                    presenter.onCreate(null)
                })
        } else {
            presenter.onCreate(null)
        }
    }

    private fun onClickToolbarText() {
        KeyboardUtils.hideKeyboard(this)
        if (isAddressListOverlayContainerVisible()) {
            closeAddressListOverlay()
        } else {
            showAddressListOverlay()
        }
    }

    override fun setCurrentFilterSettings(filtersAndSortSettings: FiltersAndSortSettings) {
        currentFilterSettings = filtersAndSortSettings;
    }

    private fun initSearchTextOnTextChangeEvents() {
        searchTextSubscription = RxTextView.textChangeEvents(searchEditText)
            .debounce(400, TimeUnit.MILLISECONDS)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { text ->
                search_clear_button.visibility = if (text.text().isNotEmpty()) View.VISIBLE else View.GONE
                val prevSearchCriteria = if (searchCriteria == null) "" else searchCriteria
                searchCriteria = if (text.text().length < 3) "" else text.text().toString()
                if (!TextUtils.equals(searchCriteria, prevSearchCriteria)) {
                    presenter.fetchYoutubeChannelVideos(null, searchCriteria, currentFilterSettings)
                }
            }
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
        if (searchTextSubscription != null && !searchTextSubscription.isDisposed && isFinishing) {
            searchTextSubscription.dispose()
        }
        super.onStop()
    }

    override fun onResume() {
        super.onResume()
        initSearchTextOnTextChangeEvents()
        initAppliedFiltersText()
        presenter.onResume(currentFilterSettings)
    }

    fun initAppliedFiltersText() {
        if (currentFilterSettings.filters.size > 0 && currentFilterSettings.isAtLeastOneFilterSet) {
            val stringBuilder = SpannableStringBuilder()

            var between = "  "
            for (tag in currentFilterSettings.filters) {
                if (tag.isChecked) {
                    stringBuilder.append(between)
                    if (between.isEmpty()) between = "  "
                    val thisTag = "  ${tag.recipeFilter}  "
                    stringBuilder.append(thisTag)
                    stringBuilder.setSpan(
                        RoundedBackgroundSpan(this),
                        stringBuilder.length - thisTag.length,
                        stringBuilder.length - thisTag.length + thisTag.length,
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                }
            }
            filter_search_divider.visibility = View.VISIBLE
            applied_filters.text = stringBuilder
            applied_filters.visibility = View.VISIBLE
        } else {
            filter_search_divider.visibility = View.GONE
            applied_filters.visibility = View.GONE
        }
    }

    private fun hideKeyboard(view: View) {
        val inputMethodManger = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManger.hideSoftInputFromWindow(view.windowToken, 0)
    }

    override fun initChannelsListOverlayAdapter(channels: List<Channel>, currentChannelId: String) {
        dropdown_addresses_listview.adapter = ChannelsListDropdownAdapter(applicationContext, channels, currentChannelId)
        dropdown_addresses_listview.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
            val channel: Channel = channels[position]
            presenter.onChannelListClick(channel, currentFilterSettings)
            search_clear_button.visibility = View.GONE
            searchEditText.setText("")
            title_text.text = channel.name
            closeAddressListOverlay()
            val adapter = (parent.adapter as ChannelsListDropdownAdapter)
            adapter.setCurrentChannelId(channel.channelId)
            adapter.notifyDataSetChanged()
            initAppliedFiltersText()
        }
        trans_overlay.setOnTouchListener({ view: View, motionEvent: MotionEvent ->
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
        title_text.visibility = View.VISIBLE
        activity_toolbar_home.setNavigationOnClickListener({
            closeAddressListOverlay()
        })
    }

    fun setDefaultAddressListDropDownIcons() {
        isAddressesDropDownVisible = false
        changeActionBarDefaultDrawerIcon()
        setToolbarTextArrow(R.drawable.ic_arrow_drop_down_white_24dp)
    }

    private fun showAndAnimateAddressList() {
        isAddressesDropDownVisible = true
        containerTopAddressList.visibility = View.VISIBLE
        app_bar.visibility = View.INVISIBLE
        val duration = resources.getInteger(android.R.integer.config_mediumAnimTime)
        changeActionBarIconToClear()

        alphaAnimateShowAddressListTransitionOverlay(duration)
        animateTranslationYShowAddressList(duration)

        setToolbarTextArrow(R.drawable.ic_arrow_drop_up_white_24dp)
    }

    private fun setToolbarTextArrow(imageResource: Int) {
        if (title_text != null) {
            title_text.setCompoundDrawablesWithIntrinsicBounds(0, 0, imageResource, 0)
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
                    containerTopAddressList.visibility = View.INVISIBLE
                }
                if (app_bar != null) {
                    app_bar.visibility = View.VISIBLE
                }
                setToolbarTextArrow(R.drawable.ic_arrow_drop_down_white_24dp)
            }
        })
        addressListCloseAnimator?.start()
    }

    fun changeActionBarDefaultDrawerIcon() {
        val actionBar = supportActionBar
        actionBar!!.setHomeAsUpIndicator(null)
        activity_toolbar_home.setNavigationOnClickListener(null)
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
        setSupportActionBar(activity_toolbar_home)
        val actionBar = supportActionBar
        actionBar!!.setDisplayShowTitleEnabled(false)
        actionBar.setDisplayHomeAsUpEnabled(false)
        actionBar.setHomeButtonEnabled(false)
        //actionBar.setHomeAsUpIndicator(resources.getDrawable(R.drawable.ic_dehaze_white_24dp))
    }

    private fun initRecyclerViewAdapter() {
        list.setHasFixedSize(true)
        list.isClickable = true
        layoutManager = LinearLayoutManager(this)
        list.layoutManager = layoutManager

        fastAdapter = FastAdapter()
        homeScreenItemFactory = HomeScreenItemFactory(networkQuality)

        homeScreenItemAdapter = GenericItemAdapter<HomeScreenModelItemWrapper, BaseHomeScreenItem>(homeScreenItemFactory)
        footerAdapter = FooterAdapter<ProgressItem>()
        list.adapter = footerAdapter.wrap(homeScreenItemAdapter.wrap(fastAdapter))
        createOnVendorsScrollListener()
        list.addOnScrollListener(onVendorsScrollListener)
        fastAdapter.withOnClickListener(onOrdersListClickListener)
    }

    private val onOrdersListClickListener
        = FastAdapter.OnClickListener<BaseHomeScreenItem> { v, adapter, item, position ->
        if (SystemClock.elapsedRealtime() > lastClickTime + DOUBLE_CLICK_TIMEOUT) {
            lastClickTime = SystemClock.elapsedRealtime()
            if (item.type == R.id.home_screen_video_item) {
                val youtubeVideoItem = item as YoutubeVideoItem
                onItemYoutubeVideoClick(v, youtubeVideoItem.item)
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

    private fun onItemYoutubeVideoClick(v: View, item: YoutubeItem) {
        val intent = VideoActivity.newIntent(this, item)

        startVideoActivityWithTransition(v, intent)
        overridePendingTransition(0, 0)
    }

    private fun createOnVendorsScrollListener() {
        onVendorsScrollListener = object : EndlessRecyclerOnScrollListener(footerAdapter) {
            override fun onLoadMore(currentPage: Int) {
                removeBottomListProgress()
                if (existVideoItemsInAdapter()) {
                    addFooterLoadingView()
                    presenter.onLoadMore(searchCriteria, currentFilterSettings)
                }
            }

            override fun onScrollStateChanged(recyclerView: RecyclerView?, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                listScrollState = newState
            }
        }
    }

    override fun removeBottomListProgress() {
        footerAdapter.clear()
    }

    fun existVideoItemsInAdapter(): Boolean {
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
            presenter.fetchYoutubeChannelVideos(null, searchCriteria, currentFilterSettings)
        })
    }

    override fun addYoutubeItemToAdapter(item: com.allrecipes.model.YoutubeItem, position: Int) {
        if (TextUtils.equals(item?.id?.kind, "youtube#video")) {
            homeScreenItemAdapter.addModel(HomeScreenModelItemWrapper(item, R.id.home_screen_video_item))
        }

        if (position % 3 == 0) {
            homeScreenItemAdapter.addModel(HomeScreenModelItemWrapper(null, R.id.home_ad_item, this))
        }
    }

    override fun showLoading() {
        super.showLoading()
        swipeContainer.isRefreshing = false
    }

    override fun setToolbarTitleText(value: String) {
        title_text.text = value
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    internal fun startVideoActivityWithTransition(v: View, intent: Intent) {
        val statusBar = findViewById(android.R.id.statusBarBackground)
        val navigationBar = findViewById(android.R.id.navigationBarBackground)

        val pairStatusBar = if (statusBar != null)
            Pair.create(statusBar, Window.STATUS_BAR_BACKGROUND_TRANSITION_NAME) else null
        val pairNavigationBar = if (navigationBar != null)
            Pair.create(navigationBar, Window.NAVIGATION_BAR_BACKGROUND_TRANSITION_NAME) else null
        val pairVideoImage = Pair.create(v.findViewById(R.id.videoThumbnail), "VideoImageTransition")

        val options = ActivityOptionsCompat.makeSceneTransitionAnimation(
            this,
            *toArray(mutableListOf(pairStatusBar, pairNavigationBar, pairVideoImage).filterNotNull())
        )
        ActivityCompat.startActivityForResult(this, intent, REQ_CODE_RECIPE_VIDEO, options.toBundle())
    }

    inline fun <reified T> toArray(list: List<*>): Array<T> {
        return (list as List<T>).toTypedArray()
    }

    override fun addSwapLaneChannelItemToAdapter(youtubePlaylistWithVideos: YoutubePlaylistWithVideos, position: Int) {
        val currentPosition = layoutManager.findFirstVisibleItemPosition()
        homeScreenItemAdapter.addModel(
            position,
            HomeScreenModelItemWrapper(youtubePlaylistWithVideos, R.id.home_swimlane_channel_item, this)
        )

        Log.d("HomeActivity", "added swap lane at position = " +position)
        if (currentPosition == 0 && position == 0) {
            layoutManager.scrollToPosition(0)
        }
    }

    override fun onSwapLaneItemClicked(view: View, item: YoutubeItem) {
        onItemYoutubeVideoClick(view, item)
    }

    override fun loadMoreOnSwipe(position: Int, item: SwipeLaneChannelItem) {
        presenter.fetchMoreVideosFromPlaylist(item)
    }

    override fun showFiltersTooltip() {
        val build = Tooltip.Builder(ADDRESS_TOOLTIP_ID)
            .anchor(sortFilter, Tooltip.Gravity.BOTTOM)
            .closePolicy(Tooltip.ClosePolicy()
                .insidePolicy(true, false)
                .outsidePolicy(true, false), 4500)
            .showDelay(800)
            .withStyleId(R.style.ToolTipLayoutRecipesStyle)
            .text(resources.getText(R.string.APP_FILTER_TOOLTIP_MSG))
            .withArrow(true)
            .withOverlay(false)
            .typeface(Typeface.createFromAsset(assets, Constants.DEFAULT_FONT))
            .fadeDuration(600)
            .build()
        Tooltip.make(this, build)
            .show()
    }

    override fun getScrollState(): Int {
        return listScrollState
    }
}
