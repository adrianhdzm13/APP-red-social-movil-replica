package com.hdz.freegamer.providers;


import com.hdz.freegamer.models.FCMBody;
import com.hdz.freegamer.models.FCMResponse;
import com.hdz.freegamer.retrofit.IFCMApi;
import com.hdz.freegamer.retrofit.RetrofitClient;

import retrofit2.Call;

public class NotificationProvider {

    private String url = "https://fcm.googleapis.com";

    public NotificationProvider() {

    }

    public Call<FCMResponse> sendNotification(FCMBody body) {
        return RetrofitClient.getClient(url).create(IFCMApi.class).send(body);
    }

}

