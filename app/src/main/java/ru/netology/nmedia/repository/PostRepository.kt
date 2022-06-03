package ru.netology.nmedia.repository

import ru.netology.nmedia.dto.Post
import androidx.lifecycle.LiveData

interface PostRepository {
    val data: LiveData<List<Post>>
    suspend fun load()
    suspend fun save(post: Post)
    suspend fun likeByID(id: Long)
    suspend fun disLikeByID(id: Long)
    suspend fun sharedByID(id: Long)
    suspend fun removeByID(id: Long)
}