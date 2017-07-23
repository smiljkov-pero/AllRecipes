package com.allrecipes.ui.filters.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.allrecipes.R;
import com.allrecipes.model.RecipeFilterOption;

import java.util.List;

public class FilterListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final LayoutInflater inflater;
    private final List<RecipeFilterOption> filters;

    public FilterListAdapter(Context context, List<RecipeFilterOption> filters) {
        inflater = LayoutInflater.from(context);
        this.filters = filters;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.filter_item_view, parent, false);
        return new FilterItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        FilterItemViewHolder filterItemViewHolder = (FilterItemViewHolder)holder;
        filterItemViewHolder.onBindViewHolder(filters.get(position));
    }

    @Override
    public int getItemCount() {
        return filters.size();
    }
}
