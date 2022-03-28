package com.project.findme.mainactivity.repository

import android.net.Uri
import com.project.findme.data.entity.Comment
import com.project.findme.data.entity.Post
import com.project.findme.data.entity.UpdateUser
import com.project.findme.data.entity.User
import com.project.findme.utils.Resource

interface MainRepository {

    suspend fun searchUsers(query: String): Resource<List<User>>

    suspend fun createPost(imageUri: Uri, title: String, description: String): Resource<Any>

    suspend fun updatePassword(oldPassword: String, newPassword: String): Resource<Any>

    suspend fun updateProfile(user: UpdateUser): Resource<Any>

    suspend fun updateProfileUI(uid: String): Resource<User>

    suspend fun getPostForProfile(uid: String): Resource<List<Post>>

    suspend fun getUser(uid: String): Resource<User>

    suspend fun followUser(uid: String): Resource<User>

    suspend fun getUsers(uid: String, type: String): Resource<List<User>>

    suspend fun createComment(commentText: String, postId:String): Resource<Comment>

    suspend fun getCommentFromPost(postId: String): Resource<List<Comment>>

}