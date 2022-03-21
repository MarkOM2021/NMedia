package ru.netology.nmedia.viewModel

import android.app.Application
import androidx.lifecycle.*
import ru.netology.nmedia.db.AppDb
import ru.netology.nmedia.repository.PostRepository
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.repository.*

val empty = Post(
    id = 0L,
    author = "",
    content = "",
    published = "",
    video = ""
)

class PostViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: PostRepository =
        //PostRepositoryFileImpl (application)
        //PostRepositorySQLiteImpl(AppDb.getInstance(application).postDao)
        PostRepositoryImpl(
            AppDb.getInstance(context = application).postDao()
        )
    val data = repository.getAll()
    val edited = MutableLiveData(empty)

    fun likedByID(id: Long) = repository.likedByID(id)
    fun removeByID(id: Long) = repository.removeByID(id)

    fun save() {
        edited.value?.let {
            repository.save(it)
            edited.value = empty
        }
    }

    fun edit(post: Post) {
        edited.value = post
    }

    fun changeContent(content: String) {
        edited.value?.let {
            if (it.content == content) {
                return
            }
            edited.value = it.copy(content = content)
        }
    }

    fun cancel() {
        TODO()
    }
}