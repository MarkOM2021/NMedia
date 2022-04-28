package ru.netology.nmedia.repository

import ru.netology.nmedia.dto.Post

interface PostRepository {
    fun getAllAsync(callBack: PostCallBack<List<Post>>)
    fun saveAsync(post: Post, callBack: PostCallBack<Post>)
    fun likeByIDAsync(id: Long, callBack: PostCallBack<Post>)
    fun disLikeByIDAsync(id: Long, callBack: PostCallBack<Post>)
    fun sharedByIDAsync(id: Long, callBack: PostCallBack<Post>)
    fun removeByIDAsync(id: Long, callBack: PostCallBack<Post>)

    interface PostCallBack<T> {
        fun onSuccess (value: T)
        fun onError (e: Exception)
    }
}