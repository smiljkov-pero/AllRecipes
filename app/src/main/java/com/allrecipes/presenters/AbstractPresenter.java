package com.allrecipes.presenters;

import com.allrecipes.ui.views.AbstractPresenterView;

import java.lang.ref.WeakReference;

public abstract class AbstractPresenter<T extends AbstractPresenterView> {

    protected WeakReference<T> view;

    public AbstractPresenter(WeakReference<T> view) {
        this.view = view;
    }

    protected T getView() {
        return view.get();
    }

    protected boolean isViewAvailable() {
        return getView() != null;
    }
}
