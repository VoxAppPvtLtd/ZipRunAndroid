package com.ziprun.consumer.utils;

import android.widget.EditText;

import rx.Observable;
import rx.android.widget.OnTextChangeEvent;
import rx.android.widget.WidgetObservable;
import rx.functions.Func1;

//Taken from https://github.com/mcharmas/Android-ReactiveLocation/blob/master/sample%2Fsrc%2Fmain%2Fjava%2Fpl%2Fcharmas%2Fandroid%2Freactivelocation%2Fsample%2Futils%2FTextObservable.java
public final class TextObservable {
    private TextObservable() {
    }

    public static Observable<String> create(EditText editText) {
        return WidgetObservable.text(editText, true).map(new Func1<OnTextChangeEvent, String>() {
            @Override
            public String call(OnTextChangeEvent onTextChangeEvent) {
                return onTextChangeEvent.text().toString();
            }
        });
    }
}