package com.allrecipes.ui.home.viewholders.items

import android.content.Context
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.TextView

import com.allrecipes.R
import com.allrecipes.model.playlist.YoutubePlaylistWithVideos
import com.allrecipes.model.video.YoutubeVideoResponse
import com.allrecipes.ui.home.adapters.SwipeLaneChannelAdapter
import com.allrecipes.ui.home.viewholders.BaseHomeScreenItem
import com.allrecipes.ui.home.viewholders.HomeScreenModelItemWrapper
import com.allrecipes.ui.home.viewholders.listeners.SwipeLaneListener
import com.mikepenz.fastadapter_extensions.scroll.EndlessRecyclerOnScrollListener

import butterknife.BindView
import butterknife.ButterKnife
import com.allrecipes.model.SearchChannelVideosResponse
import com.allrecipes.ui.home.viewholders.HomeScreenItemFactory
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.adapters.GenericItemAdapter

class SwipeLaneChannelItem(wrapper: HomeScreenModelItemWrapper, private val listener: SwipeLaneListener?)
    : BaseHomeScreenItem(wrapper) {
    val item: YoutubePlaylistWithVideos
    private var adapter: SwipeLaneChannelAdapter? = null

    lateinit var fastAdapter: FastAdapter<BaseHomeScreenItem>
    lateinit var swipeLineItemAdapter: GenericItemAdapter<HomeScreenModelItemWrapper, BaseHomeScreenItem>
    lateinit var swipeLaneItemFactory: HomeScreenItemFactory
    lateinit var layoutManager: LinearLayoutManager
    lateinit var onItemsScrollListener: EndlessRecyclerOnScrollListener

    init {
        this.item = wrapper.t as YoutubePlaylistWithVideos
    }

    override fun getType(): Int {
        return R.id.home_swimlane_channel_item
    }

    override fun getLayoutRes(): Int {
        return R.layout.item_swipelane_channel
    }

    override fun getViewHolder(v: View): SwipeLineChannelViewHolder {
        return SwipeLineChannelViewHolder(v)
    }

    override fun bindView(holder: BaseHomeScreenItem.BaseViewHolder, payloads: List<*>?) {
        super.bindView(holder, payloads)
        val viewHolder = holder as SwipeLineChannelViewHolder
        if (item.channel != null) {
            viewHolder.name.text = item.channel.snippet!!.title
        }
        initRecyclerViewAdapter(viewHolder.videosRecyclerView, viewHolder.context)

        item.videosResponse.items.forEachIndexed { index, videoItem ->
            swipeLineItemAdapter.addModel(HomeScreenModelItemWrapper(videoItem, R.id.swipelane_video_item))
        }

    }

    fun loadMore(youtubeVideoResponse: SearchChannelVideosResponse) {
        youtubeVideoResponse.items.forEachIndexed { index, videoItem ->
            swipeLineItemAdapter.addModel(HomeScreenModelItemWrapper(videoItem, R.id.swipelane_video_item))
        }
        item.videosResponse.nextPageToken = youtubeVideoResponse.nextPageToken
    }

    private fun initRecyclerViewAdapter(videosRecyclerView: RecyclerView, context: Context) {
        val layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        videosRecyclerView.layoutManager = layoutManager
        videosRecyclerView.setHasFixedSize(true)
        videosRecyclerView.isClickable = true
        videosRecyclerView.layoutManager = layoutManager

        fastAdapter = FastAdapter()
        swipeLaneItemFactory = HomeScreenItemFactory()

        swipeLineItemAdapter = GenericItemAdapter<HomeScreenModelItemWrapper, BaseHomeScreenItem>(swipeLaneItemFactory)
        videosRecyclerView.adapter = swipeLineItemAdapter.wrap(fastAdapter)
        createOnVendorsScrollListener()
        videosRecyclerView.addOnScrollListener(onItemsScrollListener)
        fastAdapter.withOnClickListener(onOrdersListClickListener)
    }

    private fun createOnVendorsScrollListener() {
        onItemsScrollListener = object : EndlessRecyclerOnScrollListener() {
            override fun onLoadMore(currentPage: Int) {
                listener?.loadMoreOnSwipe(0, this@SwipeLaneChannelItem)
            }
        }
    }

    private val onOrdersListClickListener = FastAdapter.OnClickListener<BaseHomeScreenItem> { v, adapter, item, position ->
        if (item.type == R.id.swipelane_video_item) {
            val casted: SwipeLaneVideoItem = item as SwipeLaneVideoItem
            listener?.onSwapLaneItemClicked(v, casted.item)
        }
        true
    }

    class SwipeLineChannelViewHolder(view: View) : BaseHomeScreenItem.BaseViewHolder(view) {

        @BindView(R.id.videosRecyclerView)
        lateinit var videosRecyclerView: RecyclerView

        @BindView(R.id.name)
        lateinit var name: TextView

        var context: Context

        init {
            ButterKnife.bind(this, view)
            context = view.context
        }
    }
}
