package ru.netology.nmedia.repository

import androidx.lifecycle.LiveData
import ru.netology.nmedia.dto.Post

interface PostRepository {
    fun getAll(): LiveData<List<Post>>
    fun likedByID(id: Long)
    fun sharedByID(id: Long)
    fun removeByID(id: Long)
    fun save(post: Post)
}