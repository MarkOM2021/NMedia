package ru.netology.nmedia.repository

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import ru.netology.nmedia.dto.Post

class PostRepositoryFileImpl(val context: Context) : PostRepository {


    private val gson = Gson()

    private val type = TypeToken.getParameterized(List::class.java, Post::class.java).type
    private val fileName = "posts.json"
    private var nextID = 1L
    private var posts = emptyList<Post>(
    )
    override val data = MutableLiveData(posts)

    init {
        val file = context.filesDir.resolve(fileName)
        if (file.exists()) {
            context.openFileInput(fileName).bufferedReader().use {
                posts = gson.fromJson(it, type)
                nextID = posts.maxOfOrNull { post -> post.id }?.inc() ?: 1L
                data.value = posts
            }
        } else {
            sync()
        }
    }

    override fun likedByID(id: Long) {
        posts = posts.map {
            if (it.id != id) {
                it
            } else {
                it.copy(
                    likes = if (!it.likedByMe) it.likes + 1 else it.likes - 1,
                    likedByMe = !it.likedByMe
                )
            }
        }
        data.value = posts
        sync()
    }

    override fun sharedByID(id: Long) {
        posts = posts.map {
            if (it.id == id) {
                it.copy(
                    shares = it.shares + 1
                )
            } else {
                it
            }
        }
        data.value = posts
        sync()
    }

    override fun removeByID(id: Long) {
        posts = posts.filter {
            it.id != id
        }
        data.value = posts
        sync()
    }

    override fun save(post: Post) {
        if (post.id == 0L) {
            val newId = posts.firstOrNull()?.id ?: post.id
            posts = listOf(
                post.copy(
                    id = nextID++,
                    author = "Me",
                    likedByMe = false,
                    published = "now"
                )
            ) + posts
            data.value = posts
            sync()
            return
        }

        posts = posts.map {
            if (it.id != post.id) it else it.copy(content = post.content)
        }
        data.value = posts
        sync()
    }

    private fun sync() {
        context.openFileOutput(fileName, Context.MODE_PRIVATE).bufferedWriter().use {
            it.write(gson.toJson(posts))
        }
    }
}