package com.allrecipes.ui

import android.os.Bundle
import com.allrecipes.R
import com.allrecipes.di.managers.FirebaseDatabaseManager
import com.allrecipes.presenters.HomeScreenPresenter
import com.allrecipes.ui.views.HomeScreenView
import kotlinx.android.synthetic.main.activity_home.*
import javax.inject.Inject

class HomeActivity : BaseActivity(), HomeScreenView {

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

        test.setOnClickListener {
            presenter.fetchYoutubeChannelVideos()
        }
    }
}
