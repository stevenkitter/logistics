package com.julu666.logistics.http;

import com.julu666.logistics.controller.Const;
import com.julu666.logistics.json.CNApiJSON;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.QueryMap;

import java.util.Map;


public interface Service {
    @Headers({
            "Accept: application/json",
            "Cookie: " + Const.cookiess,
            "Content-type: application/x-www-form-urlencoded",
            "User-Agent: Mozilla/5.0 (Macintosh; Intel Mac OS X 10_14_3) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/74.0.3729.169 Safari/537.36"
    })
    @GET("/h5/mtop.cnwireless.cnlogisticdetailservice.wapquerylogisticpackagebymailno/1.0/")
    Call<CNApiJSON> getRepo(@QueryMap Map<String, String> options);
}
