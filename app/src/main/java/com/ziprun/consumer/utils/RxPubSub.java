package com.ziprun.consumer.utils;

import rx.Subscription;
import rx.functions.Action1;
import rx.subjects.PublishSubject;
import rx.subjects.SerializedSubject;
import rx.subjects.Subject;

//Adapted From http://nerds.weddingpartyapp
// .com/tech/2014/12/24/implementing-an-event-bus-with-rxjava-rxbus/

public class RxPubSub<T> {
    private final Subject<T, T> bus;
    public RxPubSub(){
        bus = new SerializedSubject<T, T>(PublishSubject.<T>create());
    }

    public void publish(T item){
        bus.onNext(item);
    }


    public Subscription subscribe(Action1<? super T> action1){
        return bus.asObservable().subscribe(action1);
    }

}