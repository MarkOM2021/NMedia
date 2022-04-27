package ru.netology.nmedia.repository

import androidx.lifecycle.LiveData
import ru.netology.nmedia.dto.Post

interface PostRepository {
    fun getAll(): List<Post>
    fun save(post: Post)
    fun likedByID(id: Long)
    fun disLikeByID(id: Long)
    fun sharedByID(id: Long)
    fun removeByID(id: Long)
}