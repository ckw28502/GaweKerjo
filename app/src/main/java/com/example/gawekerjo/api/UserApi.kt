package com.example.gawekerjo.api

import com.example.gawekerjo.model.user.User
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface UserApi {
    @GET("users")
    fun getUser(
        @Query("id") id : Int?,
        @Query("email") email : String?,
        @Query("password") password : String?,
        ): Call<User>
    @GET("friend")
    fun getFriend(
        @Query("id") id : Int?
    ): Call<User>
    @GET("newfriend")
    fun getNewFriend(
        @Query("id") id : Int?
    ): Call<User>
    @GET("searchuser")
    fun searchuser(
        @Query("name") name: String?,
        @Query("email") email: String?
    ): Call<User>
    @POST("register")
    fun Register(
        @Query("type")type: Int,
        @Query("email")email: String,
        @Query("password")password:String,
        @Query("name")name:String,
        @Query("notelp")notelp:String
    ):Call<User>
    @POST("editprofile")
    fun editProfile(
        @Query("id") id: Int,
        @Query("name")name:String,
        @Query("description")description: String,
        @Query("notelp") notelp: String,
        @Query("gender")gender: String,
        @Query("tgllahir") tgllahir: String,
        @Query("negara")negara: String,
        @Query("founded")founded: String,
        @Query("industry")industry: String
    ):Call<User>

}