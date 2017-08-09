package com.allrecipes.presenters;

import com.allrecipes.ui.views.AbstractPresenterView;

import java.lang.ref.WeakReference;

import io.reactivex.disposables.Disposable;

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

    public abstract void unbindAll();

    protected void dispose(Disposable disposable) {
        if (disposable != null) {
            disposable.dispose();
        }
    }

    private boolean isDisposed(Disposable disposable) {
        return disposable != null && !disposable.isDisposed();
    }

    public boolean isDisposedAndViewAvailable(Disposable disposable) {
        return isDisposed(disposable) && isViewAvailable();
    }
}
