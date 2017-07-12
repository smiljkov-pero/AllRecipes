package com.allrecipes.ui.home.listeners;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import java.lang.ref.WeakReference;

import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.MainThreadDisposable;
import io.reactivex.annotations.NonNull;

public class TextChangeOnSubscribe implements ObservableOnSubscribe<String> {

    WeakReference<EditText> editText;

    public TextChangeOnSubscribe(EditText editText) {
        this.editText = new WeakReference<>(editText);
    }

    @Override
    public void subscribe(@NonNull final ObservableEmitter<String> e) throws Exception {
        final TextWatcher watcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!e.isDisposed()) {
                    e.onNext(s.toString());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        };
        e.setDisposable(new MainThreadDisposable() {
            @Override
            protected void onDispose() {
                editText.get().removeTextChangedListener(watcher);
            }
        });
        editText.get().addTextChangedListener(watcher);
        e.onNext("");
    }
}
