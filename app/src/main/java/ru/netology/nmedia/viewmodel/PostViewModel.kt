package ru.netology.nmedia.viewmodel

import android.app.Application
import androidx.lifecycle.*
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.model.FeedModel
import ru.netology.nmedia.repository.*
import ru.netology.nmedia.util.SingleLiveEvent
import java.io.IOException
import java.lang.Exception
import kotlin.concurrent.thread

private val empty = Post(
    id = 0,
    content = "",
    author = "",
    likedByMe = false,
    likes = 0,
    published = "",
    authorAvatar = ""
)

class PostViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: PostRepository = PostRepositoryImpl()
    private val _data = MutableLiveData(FeedModel())
    val data: LiveData<FeedModel>
        get() = _data
    val edited = MutableLiveData(empty)
    private val _postCreated = SingleLiveEvent<Unit>()
    val postCreated: LiveData<Unit>
        get() = _postCreated

    init {
        loadPosts()
    }

    fun loadPosts() {
            _data.value = FeedModel(loading = true)
        repository.getAll(object: PostRepository.PostCallback<List<Post>>{
            override fun onSuccess(data: List<Post>) {
                _data.postValue(FeedModel(posts = data, empty = data.isEmpty()))
            }

            override fun onError(e: Exception) {
                _data.postValue(FeedModel(error = true))
            }
        })
    }

    fun save() {
        edited.value?.let {
            thread {
                repository.save(it)
                _postCreated.postValue(Unit)
            }
        }
        edited.value = empty
    }

    fun edit(post: Post) {
        edited.value = post
    }

    fun changeContent(content: String) {
        val text = content.trim()
        if (edited.value?.content == text) {
            return
        }
        edited.value = edited.value?.copy(content = text)
    }

    fun likeById(post: Post) {
           if (post.likedByMe){
               repository.unlikeById(post, object: PostRepository.PostCallback<Post>{
                   override fun onSuccess(data: Post) {
                       _data.postValue(
                           _data.value?.copy(posts = _data.value?.posts.orEmpty()
                               .map {
                                   if (it.id == post.id) {
                                       data
                                   } else {
                                       it
                                   }
                               }
                           )
                       )
                   }
                   override fun onError(e: Exception) {
                       _data.postValue(FeedModel(error = true))
                   }

               })
           }else{
               repository.likeById(post, object: PostRepository.PostCallback<Post>{
                   override fun onSuccess(data: Post) {
                       _data.postValue(
                           _data.value?.copy(posts = _data.value?.posts.orEmpty()
                               .map {
                                   if (it.id == post.id) {
                                       data
                                   } else {
                                       it
                                   }
                               }
                           )
                       )
                   }

                   override fun onError(e: Exception) {
                       _data.postValue(FeedModel(error = true))
                   }
               })
       }
    }

    fun removeById(id: Long) {
        thread {
            val old = _data.value?.posts.orEmpty()
            _data.postValue(
                _data.value?.copy(posts = _data.value?.posts.orEmpty()
                    .filter { it.id != id }
                )
            )
            try {
                repository.removeById(id)
            } catch (e: IOException) {
                _data.postValue(_data.value?.copy(posts = old))
            }
        }
    }
}