package com.hdz.freegamer.retrofit;

import com.hdz.freegamer.models.FCMBody;
import com.hdz.freegamer.models.FCMResponse;


import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface IFCMApi {

    @Headers({
            "Content-Type:application/json",
            "Authorization:key=AAAArJ8gv3c:APA91bGJBxVfKYNtcIDW647souOGEzcIZIFd4jdKyVM0jbEqq8KEGRbl5BRtvMB-D9nkHKDh62Q3krLl8-5hhNqIJYUxGcLaAqAmsvoKbkDaO98uAf7xdKzrLXMG7yriw80FYhBb7UvN"
    })
    @POST("fcm/send")
    Call<FCMResponse> send(@Body FCMBody body);
}
