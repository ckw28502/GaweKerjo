package com.example.gawekerjo.repository

import android.util.Log
import com.example.gawekerjo.api.FollowApi
import com.example.gawekerjo.api.RetrofitClient
import com.example.gawekerjo.api.UserApi
import com.example.gawekerjo.database.AppDatabase
import com.example.gawekerjo.model.follow.Follow
import com.example.gawekerjo.model.follow.FollowItem
import com.example.gawekerjo.model.user.User
import com.example.gawekerjo.model.user.UserItem
import com.example.gawekerjo.view.AddFriendActivity
import com.example.gawekerjo.view.FriendListActivity
import com.example.gawekerjo.view.LoginActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit

class FollowRepository(var db : AppDatabase) {

    private val coroutine = CoroutineScope(Dispatchers.IO)
    var rc : Retrofit = RetrofitClient.getRetrofit()

    fun getFriends(mc: FriendListActivity, id: Int?, user_id:Int?, follow_id:Int?){
        var rc_follow : Call<Follow> = rc.create(FollowApi::class.java).searchfollows(id, user_id, follow_id)

        rc_follow.enqueue(object: Callback<Follow> {
            override fun onResponse(
                call: Call<Follow>,
                response: Response<Follow>
            ){
                    val responseBody = response.body()
                    var flw : FollowItem? = null

                    if(responseBody != null){
                        if(responseBody.status == 200 && responseBody.data.size > 0){
                            flw = responseBody.data[0]
                            Log.d("CCD",responseBody.data.size.toString())
                        }
                        mc.refresh(responseBody)
                    }
            }

            override fun onFailure(call: Call<Follow>, t: Throwable) {
                Log.d("CCD", "Error getting FOLLOW")
                Log.d("CCD", t.message.toString())
            }

        })
    }

    fun getUser(mc: FriendListActivity, id: Int?, email:String?, password:String?){
        var rc_user : Call<User> = rc.create(UserApi::class.java).getUser(id, email, password)

        rc_user.enqueue(object: Callback<User> {
            override fun onResponse(
                call: Call<User>,
                response: Response<User>
            ){
                val responseBody = response.body()
                var usr : UserItem? = null

                if(responseBody != null){
                    if(responseBody.status == 200 && responseBody.data.size > 0){
                        usr = responseBody.data[0]
                        Log.d("CCD",responseBody.data.size.toString())
                    }
                    mc.getAll(responseBody)
                }
            }

            override fun onFailure(call: Call<User>, t: Throwable) {
                Log.d("CCD", "Error getting FOLLOW")
                Log.d("CCD", t.message.toString())
            }

        })
    }

    fun getUser2(mc: AddFriendActivity, id: Int?, email:String?, password:String?){
        var rc_user : Call<User> = rc.create(UserApi::class.java).getUser(id, email, password)

        rc_user.enqueue(object: Callback<User> {
            override fun onResponse(
                call: Call<User>,
                response: Response<User>
            ){
                val responseBody = response.body()
                var usr : UserItem? = null

                if(responseBody != null){
                    if(responseBody.status == 200 && responseBody.data.size > 0){
                        usr = responseBody.data[0]
                        Log.d("CCD",responseBody.data.size.toString())
                    }
                    mc.getAll(responseBody)
                }
            }

            override fun onFailure(call: Call<User>, t: Throwable) {
                Log.d("CCD", "Error getting FOLLOW")
                Log.d("CCD", t.message.toString())
            }

        })
    }

    fun addFriends(mc: AddFriendActivity, user_id:Int?, follow_id:Int?){
        var rc_follow : Call<Follow> = rc.create(FollowApi::class.java).newFollow(user_id, follow_id)

        rc_follow.enqueue(object: Callback<Follow> {
            override fun onResponse(
                call: Call<Follow>,
                response: Response<Follow>
            ){
                val responseBody = response.body()
                var flw : FollowItem? = null

                if(responseBody != null){
                    if(responseBody.status == 200 && responseBody.data.size > 0){
                        flw = responseBody.data[0]
                        Log.d("CCD",responseBody.data.size.toString())
                    }
//                    mc.refresh(responseBody)
                }
            }

            override fun onFailure(call: Call<Follow>, t: Throwable) {
                Log.d("CCD", "Error getting FOLLOW")
                Log.d("CCD", t.message.toString())
            }

        })
    }

    fun removefollows(mc: AddFriendActivity,id:Int?){
        var rc_follow : Call<Follow> = rc.create(FollowApi::class.java).removefollows(id)

        rc_follow.enqueue(object: Callback<Follow> {
            override fun onResponse(
                call: Call<Follow>,
                response: Response<Follow>
            ){
                val responseBody = response.body()
                var flw : FollowItem? = null

                if(responseBody != null){
                    if(responseBody.status == 200 && responseBody.data.size > 0){
                        flw = responseBody.data[0]
                        Log.d("CCD",responseBody.data.size.toString())
                    }
//                    mc.refresh(responseBody)
                }
            }

            override fun onFailure(call: Call<Follow>, t: Throwable) {
                Log.d("CCD", "Error getting FOLLOW")
                Log.d("CCD", t.message.toString())
            }

        })
    }
}