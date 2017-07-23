package com.allrecipes.ui.filters

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import android.view.View
import android.widget.CheckBox
import com.allrecipes.R
import com.allrecipes.model.FiltersAndSortSettings
import com.jakewharton.rxbinding2.view.RxView
import com.jakewharton.rxbinding2.widget.RxRadioGroup
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.activity_filters.*
import java.util.concurrent.TimeUnit

class FiltersActivity : AppCompatActivity() {

    companion object {
        val KEY_SORT_BY_SETTINGS = "KEY_SORT_BY_SETTINGS"

        fun newIntent(context: Context, filterSortSettings: FiltersAndSortSettings): Intent {
            val intent = Intent(context, FiltersActivity::class.java)
            intent.putExtra(KEY_SORT_BY_SETTINGS, filterSortSettings)

            return intent
        }
    }

    lateinit var currentFilterSettings : FiltersAndSortSettings

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_filters)

        if (savedInstanceState != null) {
            currentFilterSettings = savedInstanceState.getParcelable<FiltersAndSortSettings>(KEY_SORT_BY_SETTINGS)
        } else {
            val extras = intent.extras
            currentFilterSettings = extras.getParcelable<FiltersAndSortSettings>(KEY_SORT_BY_SETTINGS)
        }

        RxRadioGroup.checkedChanges(sortByGroup)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                when (it) {
                    R.id.radio_date -> currentFilterSettings.sort = "date"
                    R.id.radio_rating -> currentFilterSettings.sort = "rating"
                    R.id.radio_relevance -> currentFilterSettings.sort = "relevance"
                    R.id.radio_title -> currentFilterSettings.sort = "title"
                    R.id.radio_view_count -> currentFilterSettings.sort = "viewCount"
                }
            })

        initActionBar()

        RxView.clicks(buttonApplyFilters)
            .debounce(700, TimeUnit.MILLISECONDS)
            .subscribe({
                finishActivityWithResult()
            })
    }

    fun onFilterCheckboxClicked(view : View) : Unit {
        val checked = (view as CheckBox).isChecked

        when (view.id) {
            R.id.filter_breakfast -> currentFilterSettings.filters.find {
                it.recipeFilter == "Breakfast"
            }?.setIsChecked(checked)
            R.id.filter_healthy -> currentFilterSettings.filters.find {
                it.recipeFilter == "Healthy"
            }?.setIsChecked(checked)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putParcelable(KEY_SORT_BY_SETTINGS, currentFilterSettings)
        super.onSaveInstanceState(outState)
    }

    fun finishActivityWithResult() {
        val intent = Intent()
        intent.putExtra(KEY_SORT_BY_SETTINGS, currentFilterSettings)
        setResult(RESULT_OK, intent)
        finish()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> finishActivityWithResult()
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
