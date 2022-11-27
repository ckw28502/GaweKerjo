package com.example.gawekerjo.database

import androidx.room.*
import com.example.gawekerjo.model.UserItem

@Dao
interface UserDao {
    @Insert
    suspend fun insertUser(user:UserItem)
    @Delete
    suspend fun deleteUser(user:UserItem)
    @Update
    suspend fun updateUser(user:UserItem)
    @Query("SELECT * FROM users")
    suspend fun getAllUser():List<UserItem>
    @Query("SELECT * FROM users WHERE email=:email")
    suspend fun getUserByEmail(email:String):UserItem
    @Query("SELECT * FROM users WHERE email=:email AND password=:password")
    suspend fun getUserByEmailPassword(email:String,password:String):UserItem
}