package com.project.findme.mainactivity.repository

import android.net.Uri
import android.util.Log
import androidx.core.net.toUri
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.project.findme.data.entity.*
import com.project.findme.utils.Constants
import com.project.findme.utils.Resource
import com.project.findme.utils.safeCall
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.lang.Exception
import java.util.*

class DefaultMainRepository() : MainRepository {

    val auth = FirebaseAuth.getInstance()
    private val storage = Firebase.storage
    val users = FirebaseFirestore.getInstance().collection("users")
    private val comments = FirebaseFirestore.getInstance().collection("comments")
    val posts = FirebaseFirestore.getInstance().collection("posts")
    private val draftPosts = FirebaseFirestore.getInstance().collection("drafts")

    override suspend fun searchUsers(query: String) = withContext(Dispatchers.IO) {
        safeCall {
            val userResult = users
                .orderBy("userName")
                .startAt(query)
                .endAt(query + "\uf8ff")
                .get().await().toObjects(User::class.java)
            val cur =
                users.document(auth.currentUser!!.uid).get().await().toObject(User::class.java)!!
            Resource.Success(userResult - cur)
        }
    }

    override suspend fun createPost(
        imageUri: Uri,
        title: String,
        description: String,
        postId: String,
        imageUrl: String
    ): Resource<Any> = withContext(Dispatchers.IO) {
        safeCall {
            val uid = auth.uid!!
            var newPostId = postId
            if (postId.trim() == "") {
                newPostId = UUID.randomUUID().toString()
            }

            var imgUrl = imageUrl
            if (imageUri != Uri.EMPTY) {
                val imgID = UUID.randomUUID().toString()
                val imageUploadResult = storage.getReference(imgID).putFile(imageUri).await()
                imgUrl =
                    imageUploadResult?.metadata?.reference?.downloadUrl?.await().toString()
            }
            val post = Post(
                id = newPostId,
                authorUid = uid,
                imageUrl = imgUrl,
                title = title,
                text = description,
                date = System.currentTimeMillis(),
            )
            posts.document(newPostId).set(post).await()
            if (postId.trim() != "") {
                draftPosts.document(postId).delete().await()
            }
            Resource.Success(Any())
        }
    }

    override suspend fun createDraftPost(
        imageUri: Uri,
        title: String,
        description: String,
    ): Resource<Any> = withContext(Dispatchers.IO) {
        safeCall {
            val uid = auth.uid!!
            val postId = UUID.randomUUID().toString()
            var imageUrl = ""
            if (imageUri != Uri.EMPTY) {
                val imageUploadResult = storage.getReference(postId).putFile(imageUri).await()
                imageUrl =
                    imageUploadResult?.metadata?.reference?.downloadUrl?.await().toString()
            }
            val post = Post(
                id = postId,
                authorUid = uid,
                imageUrl = imageUrl,
                title = title,
                text = description,
                date = System.currentTimeMillis(),
            )
            draftPosts.document(postId).set(post).await()
            Resource.Success(Any())
        }
    }

    override suspend fun updateDraftPost(
        imageUri: Uri,
        title: String,
        description: String,
        postId: String,
        imageUrl: String
    ): Resource<Any> = withContext(Dispatchers.IO) {
        safeCall {
            var imgUrl = imageUrl
            if (imageUri != Uri.EMPTY) {
                val imgID = UUID.randomUUID().toString()
                val imageUploadResult = storage.getReference(imgID).putFile(imageUri).await()
                imgUrl =
                    imageUploadResult?.metadata?.reference?.downloadUrl?.await().toString()
            }
            val map = mapOf(
                "imageUrl" to imgUrl,
                "title" to title,
                "text" to description,
                "date" to System.currentTimeMillis()
            )
            draftPosts.document(postId).update(map).await()
            Resource.Success(Any())
        }
    }

    override suspend fun updatePassword(
        oldPassword: String,
        newPassword: String
    ): Resource<Any> {
        return withContext(Dispatchers.IO) {
            safeCall {
                val user = Firebase.auth.currentUser

                val credential = EmailAuthProvider
                    .getCredential(user?.email!!, oldPassword)

                val result = user.reauthenticate(credential).addOnSuccessListener {
                    CoroutineScope(Dispatchers.IO).launch {
                        user.updatePassword(newPassword).await()
                    }
                }

                Resource.Success(result)
            }
        }
    }

    override suspend fun updateProfile(user: UpdateUser) = withContext(Dispatchers.IO) {
        safeCall {

            val curUser = auth.currentUser
            val profileUpdate =
                UserProfileChangeRequest.Builder().setDisplayName(user.userName).build()

            curUser!!.updateProfile(profileUpdate).await()

            val map = mutableMapOf(
                "userName" to user.userName,
                "description" to user.description,
                "credential.profession" to user.updateCredential.profession,
                "credential.interest" to user.updateCredential.interest
            )

            if (user.profilePicture != Constants.DEFAULT_PROFILE_PICTURE_URL.toUri()) {
                val imgID = UUID.randomUUID().toString()
                val imageUploadResult =
                    storage.getReference(imgID).putFile(user.profilePicture!!).await()
                val imageUrl =
                    imageUploadResult?.metadata?.reference?.downloadUrl?.await().toString()

                map["profilePicture"] = imageUrl
            }

            users.document(user.uidToUpdate).update(map.toMap()).await()
            Resource.Success(Any())
        }
    }

    override suspend fun removeProfilePicture(): Resource<Any> = withContext(Dispatchers.IO) {
        safeCall {
            users.document(auth.currentUser!!.uid).update(
                "profilePicture",
                Constants.DEFAULT_PROFILE_PICTURE_URL
            ).await()
            Resource.Success(Any())
        }
    }

    override suspend fun updateProfileUI(uid: String): Resource<User> =
        withContext(Dispatchers.IO) {
            safeCall {
                val user = users.document(uid).get().await().toObject(User::class.java)!!
                Resource.Success(user)
            }
        }

    override suspend fun getPostForProfile(uid: String): Resource<List<Post>> =
        withContext(Dispatchers.IO) {
            safeCall {

                val profilePosts = getPostForUser(uid).data!!

                val followings =
                    users.document(uid).get().await().toObject(User::class.java)!!.followings

                var followingsPost: List<Post> = listOf()
                try {
                    followingsPost = posts.whereIn("authorUid", followings)
                        .orderBy("date", Query.Direction.DESCENDING)
                        .get()
                        .await()
                        .toObjects(Post::class.java)
                        .onEach { post ->
                            val user = getUser(post.authorUid).data!!
                            post.authorProfilePictureUrl = user.profilePicture
                            post.authorUsername = user.userName
                            post.isLiked = uid in post.likedBy
                        }
                } catch (e: Exception) {

                }

                var p = profilePosts + followingsPost
                p = p.sortedByDescending {
                    it.date
                }

                Resource.Success(p)
            }
        }

    override suspend fun getDraftPosts(uid: String): Resource<List<Post>> =
        withContext(Dispatchers.IO) {
            safeCall {
                val draftPosts = draftPosts.whereEqualTo("authorUid", uid)
                    .orderBy("date", Query.Direction.DESCENDING).get().await()
                    .toObjects(Post::class.java)
                    .onEach { post ->
                        val user = getUser(post.authorUid).data!!
                        post.authorProfilePictureUrl = user.profilePicture
                        post.authorUsername = user.userName
                        post.isLiked = uid in post.likedBy
                    }
                Resource.Success(draftPosts)
            }
        }

    override suspend fun getPostForUser(uid: String): Resource<List<Post>> =
        withContext(Dispatchers.IO) {
            safeCall {
                val profilePosts = posts.whereEqualTo("authorUid", uid)
                    .orderBy("date", Query.Direction.DESCENDING)
                    .get()
                    .await()
                    .toObjects(Post::class.java)
                    .onEach { post ->
                        val user = getUser(post.authorUid).data!!
                        post.authorProfilePictureUrl = user.profilePicture
                        post.authorUsername = user.userName
                        post.isLiked = uid in post.likedBy
                    }
                Resource.Success(profilePosts)
            }
        }

    override suspend fun getUser(uid: String): Resource<User> = withContext(Dispatchers.IO) {
        safeCall {
            val user = users.document(uid).get().await().toObject(User::class.java)
                ?: throw IllegalStateException()
            val currentUid = FirebaseAuth.getInstance().uid!!
            val currentUser =
                users.document(currentUid).get().await().toObject(User::class.java)
                    ?: throw IllegalStateException()
            user.isFollowing = uid in currentUser.follows
            Resource.Success(user)
        }
    }

    override suspend fun followUser(uid: String): Resource<User> = withContext(Dispatchers.IO) {
        safeCall {
            val currentUser = auth.currentUser?.uid!!
            users.document(currentUser).update("followings", FieldValue.arrayUnion(uid)).await()
            users.document(uid).update("follows", FieldValue.arrayUnion(currentUser)).await()

            val user = users.document(uid).get().await().toObject(User::class.java)!!
            Resource.Success(user)
        }
    }

    override suspend fun unFollowUser(uid: String): Resource<User> =
        withContext(Dispatchers.IO) {
            safeCall {
                val currentUser = auth.currentUser?.uid!!
                users.document(currentUser).update("followings", FieldValue.arrayRemove(uid))
                    .await()
                users.document(uid).update("follows", FieldValue.arrayRemove(currentUser))
                    .await()

                val user = users.document(uid).get().await().toObject(User::class.java)!!
                Resource.Success(user)
            }
        }

    override suspend fun getUsersLiked(uid: String): Resource<List<User>> =
        withContext(Dispatchers.IO) {
            safeCall {
                val userList = mutableListOf<User>()

                val post = posts.document(uid).get().await().toObject(Post::class.java)!!

                post.likedBy.forEach {
                    val user = users.document(it).get().await().toObject(User::class.java)!!
                    userList.add(user)
                }

                return@safeCall Resource.Success(userList)
            }
        }

    override suspend fun getFollowersList(uid: String): Resource<List<User>> =
        withContext(Dispatchers.IO) {
            safeCall {
                val user = users.document(uid).get().await().toObject(User::class.java)!!
                val userList = mutableListOf<User>()

                for (u in user.follows) {
                    val cur = users.document(u).get().await().toObject(User::class.java)!!
                    userList.add(cur)
                }

                return@safeCall Resource.Success(userList)
            }
        }

    override suspend fun getFollowingList(uid: String): Resource<List<User>> =
        withContext(Dispatchers.IO) {
            safeCall {
                val user = users.document(uid).get().await().toObject(User::class.java)!!
                val userList = mutableListOf<User>()

                for (u in user.followings) {
                    val cur =
                        users.document(u).get().await().toObject(User::class.java)!!
                    userList.add(cur)
                }
                return@safeCall Resource.Success(userList)
            }
        }

    override suspend fun getMutualList(uid: String): Resource<List<User>> =
        withContext(Dispatchers.IO) {
            safeCall {
                val user = users.document(uid).get().await().toObject(User::class.java)!!
                val userList = mutableListOf<User>()

                for (u in user.followings) {
                    val cur =
                        users.document(u).get().await().toObject(User::class.java)!!
                    userList.add(cur)
                }
                val curUser = users.document(auth.currentUser!!.uid).get().await()
                    .toObject(User::class.java)!!
                val userList1 = mutableListOf<User>()
                for (u in curUser.followings) {
                    val cur =
                        users.document(u).get().await().toObject(User::class.java)!!
                    userList1.add(cur)
                }
                return@safeCall Resource.Success((userList intersect userList1).toList())
            }
        }

    override suspend fun getSuggestionList(uid: String): Resource<List<User>> =
        withContext(Dispatchers.IO) {
            safeCall {
                val c = FirebaseAuth.getInstance().currentUser!!
                val curUser =
                    users.document(c.uid).get().await().toObject(User::class.java)!!
                val interests = curUser.credential.interest
                val userList = mutableSetOf<User>()

                val user = users.document(uid).get().await().toObject(User::class.java)!!
                val followers = user.follows
                val followings = user.followings

                for (i in followers) {
                    val cur = users.document(i).get().await().toObject(User::class.java)!!
                    if ((interests.intersect(cur.credential.interest)).isNotEmpty() or
                        (user.credential.interest.intersect(cur.credential.interest).isNotEmpty())
                    ) {
                        if (cur != curUser) {
                            userList.add(cur)
                        }
                    }
                }

                for (i in followings) {
                    val cur = users.document(i).get().await().toObject(User::class.java)!!
                    if ((interests.intersect(cur.credential.interest)).isNotEmpty() or
                        (user.credential.interest.intersect(cur.credential.interest).isNotEmpty())
                    ) {
                        if (cur != curUser) {
                            userList.add(cur)
                        }
                    }
                }

                return@safeCall Resource.Success(userList.toList())
            }
        }


    override suspend fun createComment(commentText: String, postId: String, parentId: String?) =
        withContext(Dispatchers.IO) {
            safeCall {
                val uid = auth.uid!!
                val commentId = UUID.randomUUID().toString()
                val user = getUser(uid).data!!
                val comment = Comment(
                    commentId = commentId,
                    postId = postId,
                    uid = uid,
                    username = user.userName,
                    profilePicture = user.profilePicture,
                    comment = commentText,
                    parentId = parentId,
                )
                comments.document(commentId).set(comment).await()
                if (parentId != null) {
                    comments.document(parentId)
                        .update("repliedComments", FieldValue.arrayUnion(comment.commentId))
                }
                Resource.Success(comment)
            }
        }

    override suspend fun getCommentFromPost(postId: String) = withContext(Dispatchers.IO) {
        safeCall {

            val newList = mutableListOf<Comment>()

            comments
                .whereEqualTo("postId", postId)
                .orderBy("date", Query.Direction.DESCENDING)
                .get()
                .await()
                .toObjects(Comment::class.java)
                .onEach { comment ->
                    val user = getUser(comment.uid).data!!
                    comment.username = user.userName
                    comment.profilePicture = user.profilePicture
                }.forEach {
                    if (it.parentId == null) {
                        newList.add(it)
                    }
                }

            Resource.Success(newList.toList())
        }
    }

    override suspend fun deletePost(post: Post) = withContext(Dispatchers.IO) {
        safeCall {
            comments.whereEqualTo("postId", post.id).get().await()
                .toObjects(Comment::class.java).forEach {
                    comments.document(it.commentId).delete().await()
                }
            posts.document(post.id).delete().await()
            storage.getReferenceFromUrl(post.imageUrl).delete().await()
            Resource.Success(post)
        }
    }

    override suspend fun deleteDraftPost(post: Post): Resource<Post> =
        withContext(Dispatchers.IO) {
            safeCall {
                draftPosts.document(post.id).delete().await()
                if (post.imageUrl.isNotEmpty())
                    storage.getReferenceFromUrl(post.imageUrl).delete().await()
                Resource.Success(post)
            }
        }


    override suspend fun deleteComment(comment: Comment) = withContext(Dispatchers.IO) {
        safeCall {
            val open = mutableListOf<String>()
            val close = mutableListOf<String>()

            open.add(comment.commentId)

            while (open.isNotEmpty()) {
                val cur = open.removeAt(0)
                val curComment =
                    comments.document(cur).get().await().toObject(Comment::class.java)!!
                open.addAll(curComment.repliedComments)
                close.add(cur)
            }

            for (c in close) {
                val curComment =
                    comments.document(c).get().await().toObject(Comment::class.java)!!
                if (curComment.parentId != null) {
                    comments.document(curComment.parentId)
                        .update("repliedComments", FieldValue.arrayRemove(c))
                }
                comments.document(c).delete().await()
            }

            Resource.Success(comment)
        }
    }

    override suspend fun likePost(post: Post): Resource<Any> = withContext(Dispatchers.IO) {
        safeCall {
            val uid = auth.uid
            val result = if (uid in post.likedBy) posts.document(post.id).update(
                "likedBy",
                FieldValue.arrayRemove(uid)
            ) else posts.document(post.id).update(
                "likedBy", FieldValue.arrayUnion(uid)
            )
            post.isLiking = false
            Resource.Success(result)
        }
    }
}