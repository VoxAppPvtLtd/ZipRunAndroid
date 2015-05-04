package com.ziprun.consumer.managers;

import com.ziprun.consumer.data.model.ZipConsumer;

import javax.inject.Inject;

import retrofit.RequestInterceptor;


public class TokenRequestInterceptor implements RequestInterceptor {
    private static final String TAG = TokenRequestInterceptor.class.getCanonicalName();

    @Inject
    ZipConsumer zipConsumer;

    @Inject
    public TokenRequestInterceptor(){
    }

    @Override
    public void intercept(RequestFacade request) {
        if(zipConsumer == null){
            return;
        }

        String authToken = zipConsumer.getToken();
        if(authToken != null) {
            request.addHeader("WWW-Authenticate", "Token");
            request.addHeader("Authorization", "Token " + authToken);
        }
    }
}
