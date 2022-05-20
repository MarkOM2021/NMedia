package ru.netology.nmedia.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.model.FeedModel
import ru.netology.nmedia.repository.PostRepository
import ru.netology.nmedia.repository.PostRepositoryImpl
import ru.netology.nmedia.util.SingleLiveEvent

val empty = Post(
    id = 0L,
    author = "",
    authorAvatar = "",
    content = "",
    published = "",
    likedByMe = false,
    sharedByMe = false,
    video = ""
)

class PostViewModel(application: Application) : AndroidViewModel(application) {
    // упрощённый вариант
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
        lastAction = null
        // Начинаем загрузку
        _data.postValue(FeedModel(loading = true))
        // Данные успешно получены
        repository.getAllAsync(object : PostRepository.PostCallBack<List<Post>> {
            override fun onSuccess(value: List<Post>) {
                _data.postValue(FeedModel(posts = value, empty = value.isEmpty()))
            }

            override fun onError(e: Exception) {
                _data.postValue(FeedModel(error = true))
            }
        })
    }

    fun save() {
        edited.value?.let {
            lastAction = null

            repository.saveAsync(it, object : PostRepository.PostCallBack<Post> {
                override fun onSuccess(value: Post) {
                    _postCreated.postValue(Unit)
                    edited.value = empty
                }

                override fun onError(e: Exception) {
                    lastAction = ActionType.SAVE
                    _data.postValue(FeedModel(serverNoResponse = true))
                }
            })
        }
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

    fun likedByID(id: Long) {
        lastAction = null
        lastLikedID = null

        repository.likeByIDAsync(id, object : PostRepository.PostCallBack<Post> {
            override fun onSuccess(value: Post) {
                _data.postValue(
                    _data.value?.copy(
                        posts = _data.value?.posts.orEmpty()
                            .map {
                                if (it.id == id) it.copy(likedByMe = true, likes = it.likes + 1)
                                else it
                            })
                )
            }

            override fun onError(e: Exception) {
                lastAction = ActionType.LIKE
                lastLikedID = id
                _data.postValue(FeedModel(serverNoResponse = true))
            }
        })
    }

    fun disLikedByID(id: Long) {
        lastAction = null
        lastDisLikedID = null

        repository.disLikeByIDAsync(id, object : PostRepository.PostCallBack<Post> {
            override fun onSuccess(value: Post) {
                _data.postValue(
                    _data.value?.copy(
                        posts = _data.value?.posts.orEmpty()
                            .map {
                                if (it.id == id) it.copy(likedByMe = false, likes = it.likes - 1)
                                else it
                            })
                )
            }

            override fun onError(e: Exception) {
                lastDisLikedID = id
                lastAction = ActionType.DISLIKE
                _data.postValue(FeedModel(serverNoResponse = true))
            }
        })
    }

    fun removeByID(id: Long) {
        lastAction = null
        lastRemovedID = null

        repository.removeByIDAsync(id, object : PostRepository.PostCallBack<Unit> {
            override fun onSuccess(value: Unit) {
                val updated = _data.value?.posts.orEmpty().filter {it.id != id}
                _data.postValue(
                    _data.value?.copy(
                        posts = updated
                    )
                )
            }

            override fun onError(e: Exception) {
                lastAction = ActionType.REMOVE
                lastRemovedID = id

                val old = _data.value?.posts.orEmpty()
                _data.postValue(FeedModel(serverNoResponse = true))
                _data.postValue(_data.value?.copy(posts = old))
            }

        })
    }


    private var lastAction: ActionType? = null
    private var lastLikedID: Long? = null
    private var lastDisLikedID: Long? = null
    private var lastRemovedID: Long? = null

    fun retry() {
        when(lastAction) {
            ActionType.SAVE -> retrySave()
            ActionType.LIKE -> retryLike()
            ActionType.DISLIKE -> retryDisLike()
            ActionType.REMOVE -> retryRemove()
            else -> loadPosts()
        }
    }

    private fun retryDisLike() {
        lastRemovedID?.let {
            disLikedByID(it)
        }
    }

    private fun retrySave() {
        lastRemovedID?.let {
            save()
        }
    }

    private fun retryRemove() {
        lastRemovedID?.let {
            removeByID(it)
        }
    }

    private fun retryLike() {
        lastRemovedID?.let {
            likedByID(it)
        }
    }

    enum class ActionType {
        SAVE,
        LIKE,
        REMOVE,
        DISLIKE
    }
}