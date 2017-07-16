package com.allrecipes.presenters;

import com.allrecipes.ui.views.AbstractPresenterView;

import java.lang.ref.WeakReference;

import io.reactivex.disposables.Disposable;
import rx.Subscription;

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

    protected void unsubscribe(Subscription subscription) {
        if (subscription != null && !subscription.isUnsubscribed()) {
            subscription.unsubscribe();
        }
    }

    private boolean isDisposed(Disposable disposable) {
        return disposable != null && !disposable.isDisposed();
    }

    public boolean isDisposedAndViewAvailable(Disposable disposable) {
        return isDisposed(disposable) && isViewAvailable();
    }

    protected boolean isSubscribed(Subscription subscription) {
        return subscription != null && !subscription.isUnsubscribed();
    }

    protected boolean isSubscribedAndViewAvailable(Subscription subscription) {
        return isSubscribed(subscription) && isViewAvailable();
    }
}
