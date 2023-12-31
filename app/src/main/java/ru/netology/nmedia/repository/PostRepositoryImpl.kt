package ru.netology.nmedia.repository

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import ru.netology.nmedia.dto.Post
import java.io.IOException
import java.util.concurrent.TimeUnit


class PostRepositoryImpl: PostRepository {
    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .build()
    private val gson = Gson()
    private val typeToken = object : TypeToken<List<Post>>() {}

    companion object {
        private const val BASE_URL = "http://10.0.2.2:9999"
        private val jsonType = "application/json".toMediaType()
    }

    override fun getAll(callback: PostRepository.PostCallback<List<Post>>) {
        val request: Request = Request.Builder()
            .url("${BASE_URL}/api/slow/posts")
            .build()

        return client.newCall(request)
            .enqueue(object: Callback {
                override fun onFailure(call: Call, e: IOException) {
                    callback.onError(e)
                }

                override fun onResponse(call: Call, response: Response) {
                    if(!response.isSuccessful) {
                        callback.onError(Exception(response.message))
                    }else{
                        callback.onSuccess(
                            gson.fromJson(
                                requireNotNull(response.body?.string()),
                                typeToken.type
                            )
                        )
                    }
                }
        })
    }



    override fun likeById(post: Post, callback: PostRepository.PostCallback<Post>) {
        val id = post.id
        val request: Request = Request.Builder()
            .post(gson.toJson(post).toRequestBody(jsonType))
            .url("${BASE_URL}/api/slow/posts/$id/likes")
            .build()

        return client.newCall(request)
            .enqueue(object: Callback {
                override fun onFailure(call: Call, e: IOException) {
                    callback.onError(e)
                }

                override fun onResponse(call: Call, response: Response) {
                    if(!response.isSuccessful) {
                        callback.onError(Exception(response.message))
                    }else{
                        callback.onSuccess(
                            gson.fromJson(
                                requireNotNull(response.body?.string()),
                                Post::class.java
                            )
                        )
                    }
                }
            })
    }

    override fun unlikeById(post: Post, callback: PostRepository.PostCallback<Post>){
        val id = post.id
        val request: Request = Request.Builder()
            .delete(gson.toJson(post).toRequestBody(jsonType))
            .url("${BASE_URL}/api/slow/posts/$id/likes")
            .build()

        return client.newCall(request)
            .enqueue(object: Callback {
                override fun onFailure(call: Call, e: IOException) {
                    callback.onError(e)
                }

                override fun onResponse(call: Call, response: Response) {
                    if(!response.isSuccessful) {
                        callback.onError(Exception(response.message))
                    }else{
                        callback.onSuccess(
                            gson.fromJson(
                                requireNotNull(response.body?.string()),
                                Post::class.java
                            )
                        )
                    }
                }
            })
    }

    override fun save(post: Post) {
        val request: Request = Request.Builder()
            .post(gson.toJson(post).toRequestBody(jsonType))
            .url("${BASE_URL}/api/slow/posts")
            .build()

        client.newCall(request)
            .execute()
            .close()
    }

    override fun removeById(id: Long) {
        val request: Request = Request.Builder()
            .delete()
            .url("${BASE_URL}/api/slow/posts/$id")
            .build()

        client.newCall(request)
            .execute()
            .close()
    }
}