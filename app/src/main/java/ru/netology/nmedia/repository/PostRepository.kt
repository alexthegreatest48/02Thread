package ru.netology.nmedia.repository

import ru.netology.nmedia.dto.Post
import java.lang.Exception

interface PostRepository {
    fun getAll(callback: PostCallback<List<Post>>)
    fun likeById(post: Post,callback: PostCallback<Post>)
    fun save(post: Post)
    fun removeById(id: Long)
    fun unlikeById(post: Post, callback: PostCallback<Post>)

    interface PostCallback<T>{
        fun onSuccess(data: T)
        fun onError(e: Exception)
    }
}

