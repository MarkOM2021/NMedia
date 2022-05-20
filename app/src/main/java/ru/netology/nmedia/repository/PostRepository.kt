package ru.netology.nmedia.repository

import ru.netology.nmedia.dto.Post

interface PostRepository {
    fun getAllAsync(callback: PostCallBack<List<Post>>)
    fun saveAsync(post: Post, callback: PostCallBack<Post>)
    fun likeByIDAsync(id: Long, callback: PostCallBack<Post>)
    fun disLikeByIDAsync(id: Long, callback: PostCallBack<Post>)
    fun sharedByIDAsync(id: Long, callback: PostCallBack<Post>)
    fun removeByIDAsync(id: Long, callback: PostCallBack<Unit>)

    interface PostCallBack<T> {
        fun onSuccess (value: T)
        fun onError (e: Exception)
    }
}