package com.allrecipes.util;

import android.support.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import io.reactivex.Maybe;
import io.reactivex.MaybeEmitter;
import io.reactivex.MaybeOnSubscribe;

public class RxFirebaseDatabase {
    public RxFirebaseDatabase() {
    }

    @NonNull
    public static Maybe<DataSnapshot> observeSingleValueEvent(@NonNull final Query query) {
        return Maybe.create(new MaybeOnSubscribe() {
            @Override
            public void subscribe(final @io.reactivex.annotations.NonNull MaybeEmitter e) throws Exception {
                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        e.onSuccess(dataSnapshot);
                        e.onComplete();
                    }

                    public void onCancelled(DatabaseError error) {
                        e.onError(new Exception());
                    }
                });
            }
        });
    }
}
