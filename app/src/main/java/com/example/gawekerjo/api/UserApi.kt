package com.example.gawekerjo.api

import com.example.gawekerjo.model.UserItem
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface UserApi {

    @GET("users")
    fun getUser(
        @Query("id") id : Int?,
        @Query("username") username : String?,
        ): Call<List<UserItem>>
}