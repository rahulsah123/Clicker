package com.example.retrofit

import com.example.retrofit.utils.Constant
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*

public interface RestInterface {


    @FormUrlEncoded
    @POST(Constant.UPDATESCORE)
    abstract fun updateNameScore(@Field(Constant.NAME) name: String,
                                 @Field(Constant.SCORE) score: String,
                                 @Field(Constant.SCORE_TIME) score_time: String

    ): Call<ResponseBody>
    @FormUrlEncoded
    @POST(Constant.H_SCORE)
    abstract fun getHscoreName(
            @Field(Constant.NAME) name: String
    ):Call<ResponseBody>
}