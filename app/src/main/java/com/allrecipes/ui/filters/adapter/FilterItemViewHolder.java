package com.allrecipes.ui.filters.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.allrecipes.R;
import com.allrecipes.custom.views.AllRecipesCheckbox;
import com.allrecipes.model.RecipeFilterOption;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;

public class FilterItemViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.filter_item_checkbox) AllRecipesCheckbox filterItemCheckbox;

    private RecipeFilterOption restaurantFilterOption;

    @OnCheckedChanged(R.id.filter_item_checkbox)
    public void onFilterItemCheckedChanged(boolean isChecked) {
        restaurantFilterOption.setIsChecked(isChecked);
    }

    public FilterItemViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    public void onBindViewHolder(RecipeFilterOption restaurantFilterOption) {
        this.restaurantFilterOption = restaurantFilterOption;
        filterItemCheckbox.setText(restaurantFilterOption.getRecipeFilter());
        filterItemCheckbox.setChecked(restaurantFilterOption.isChecked());
    }
}
