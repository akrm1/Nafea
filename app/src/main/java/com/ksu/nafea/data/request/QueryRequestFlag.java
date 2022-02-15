package com.ksu.nafea.data.request;

import com.ksu.nafea.data.request.FailureResponse;

public interface QueryRequestFlag<T>
{
    public void onQuerySuccess(T resultObject);
    public void onQueryFailure(FailureResponse failure);
}
