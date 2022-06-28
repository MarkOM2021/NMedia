package ru.netology.nmedia.repository

import ru.netology.nmedia.dto.Post
import kotlinx.coroutines.flow.Flow
import ru.netology.nmedia.dto.Media
import ru.netology.nmedia.dto.MediaUpload

interface PostRepository {
    val data: Flow<List<Post>>
    suspend fun load()
    fun getNewerCount(id: Long): Flow<Int>
    suspend fun save(post: Post)
    suspend fun saveWithAttachment(post: Post, upLoad: MediaUpload)
    suspend fun likeByID(id: Long)
    suspend fun disLikeByID(id: Long)
    suspend fun sharedByID(id: Long)
    suspend fun removeByID(id: Long)
    suspend fun upload(upload: MediaUpload): Media
}