package com.allrecipes.ui.filters

import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem

import com.allrecipes.R
import com.jakewharton.rxbinding2.widget.RxRadioGroup
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.activity_filters.*

class FiltersActivity : AppCompatActivity() {
    companion object {
        val KEY_SORT_BY_SETTINGS = "KEY_SORT_BY_SETTINGS"

        fun newIntent(context: Context, filterSortBy: String): Intent {
            val intent = Intent(context, FiltersActivity::class.java)
            intent.putExtra(KEY_SORT_BY_SETTINGS, filterSortBy)

            return intent
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_filters)

        RxRadioGroup.checkedChanges(sortByGroup)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                when (it) {
                    R.id.radio_date -> finishActivityWithResult("date")
                    R.id.radio_rating -> finishActivityWithResult("rating")
                    R.id.radio_relevance -> finishActivityWithResult("relevance")
                    R.id.radio_title -> finishActivityWithResult("title")
                    R.id.radio_view_count -> finishActivityWithResult("viewCount")
                }
            })

        initActionBar()
    }

    fun finishActivityWithResult(sortBy: String) {
        val intent = Intent()
        intent.putExtra(KEY_SORT_BY_SETTINGS, sortBy)
        setResult(RESULT_OK, intent)
        finish()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> finishActivityWithResult("rating")
        }

        return super.onOptionsItemSelected(item)
    }

    private fun initActionBar() {
        setSupportActionBar(activity_toolbar)
        if (supportActionBar != null) {
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            supportActionBar?.title = ""
        }
    }
}
