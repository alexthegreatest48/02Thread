package ru.netology.nmedia.repository

import ru.netology.nmedia.dto.Post

interface PostRepository {
    fun getAll(): List<Post>
    fun getOne(id: Long): Post
    fun likeById(post: Post)
    fun save(post: Post)
    fun removeById(id: Long)
    fun unlikeById(post: Post)
}

