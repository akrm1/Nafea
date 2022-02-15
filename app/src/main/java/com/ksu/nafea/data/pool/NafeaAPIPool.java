package com.ksu.nafea.data.pool;

import android.util.Log;

import com.ksu.nafea.api.NafeaApiRequest;
import com.ksu.nafea.data.request_result.ERequestResultType;
import com.ksu.nafea.data.request.QueryRequest;
import com.ksu.nafea.logic.Entity;

import java.util.List;
import java.util.Stack;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class NafeaAPIPool
{
    public final static String TAG = "NafeaAPIPool";
    private static Retrofit retrofit = null;
    private static final String BASE_URL = "https://nafea-database-backend.herokuapp.com";

    protected static final Stack<String> requestsStack = new Stack<String>();

    private static NafeaApiRequest getNafeaAPI()
    {
        if(retrofit == null)
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();


        NafeaApiRequest api = retrofit.create(NafeaApiRequest.class);

        return api;
    }



    private static <T, EntityType extends Entity<EntityType>, ReturnType>
    void sendRequest(Call<T> requestCall, final QueryRequest<EntityType, ReturnType> queryRequest, final boolean singleResponse)
    {
        requestsStack.push(queryRequest.toString() + "\n");

        requestCall.enqueue(new Callback<T>()
        {
            @Override
            public void onResponse(Call<T> call, Response<T> response)
            {
                if(response.isSuccessful())
                {
                    try
                    {
                        if(singleResponse)
                            queryRequest.sendSuccessSingleResponse(response.body(), ERequestResultType.Status);
                        else
                            queryRequest.sendSuccessListResponse(response.body(), ERequestResultType.Table);
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                        queryRequest.sendFailureResponse(TAG, e.getMessage());
                    }
                }
                else
                {
                    queryRequest.sendFailureResponse(TAG, response.message());
                }
            }

            @Override
            public void onFailure(Call<T> call, Throwable t)
            {
                queryRequest.sendFailureResponse(TAG, t.getMessage());
            }
        });
    }





    protected static <EntityType extends Entity<EntityType>, ReturnType>
    void executePostQuery(final QueryRequest<EntityType, ReturnType> queryRequest)
    {
        Call<Object> postCall = getNafeaAPI().executePostQuery(queryRequest.getMainQueries(), queryRequest.getAttachedQueries());
        sendRequest(postCall, queryRequest, true);
    }

    protected static <EntityType extends Entity<EntityType>, ReturnType>
    void executeGetQuery(final QueryRequest<EntityType, ReturnType> queryRequest)
    {
        Call<List<Object>> getCall = getNafeaAPI().executeGetQuery(queryRequest.getQueryRequestFormat());
        sendRequest(getCall, queryRequest, false);

    }

}