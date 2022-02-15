package com.ksu.nafea.api;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Url;

public interface NafeaApiRequest
{
    @GET
    Call<List<Object>> executeGetQuery(@Url String requestPath);

    @FormUrlEncoded
    @POST("/post/execute")
    Call<Object> executePostQuery(@Field("command") String command, @Field("attach") String attach);


}
