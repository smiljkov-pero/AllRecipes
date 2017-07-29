package com.allrecipes.ui.views;

public interface AbstractPresenterView {

    void showToast(int messageResId);

    void showToast(String message);

    void showLoading();

    void hideLoading();

    boolean isFinishing();

    void showConnectivityError();

    void hideConnectivityError();
}