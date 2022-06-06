package ru.netology.nmedia.viewModel

import android.app.Application
import androidx.lifecycle.*
import kotlinx.coroutines.launch
import ru.netology.nmedia.db.AppDb
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.model.FeedModel
import ru.netology.nmedia.model.FeedModelState
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
    private val repository: PostRepository =
        PostRepositoryImpl(AppDb.getInstance(application).postDao())

    private val _data = MutableLiveData<FeedModel>()
    val data: LiveData<FeedModel>
        get() = repository.data.map(::FeedModel)


    private val _dataState = MutableLiveData<FeedModelState>()
    val dataState: LiveData<FeedModelState>
        get() = _dataState

    val edited = MutableLiveData(empty)
    private val _postCreated = SingleLiveEvent<Unit>()
    val postCreated: LiveData<Unit>
        get() = _postCreated

    init {
        loadPosts()
    }

    fun loadPosts() = viewModelScope.launch {
        lastAction = null
        try {
            _dataState.value = FeedModelState(loading = true)
            repository.load()
            _dataState.value = FeedModelState()
        } catch (e: Exception) {
            _dataState.value = FeedModelState(error = true)
        }
    }

    fun refresh() = viewModelScope.launch {
        lastAction = null
        try {
            _dataState.value = FeedModelState(loading = true)
            repository.load()
            _dataState.value = FeedModelState()
        } catch (e: Exception) {
            _dataState.value = FeedModelState(error = true)
        }
    }

    fun save() {
        edited.value?.let {
            lastAction = null
            _postCreated.value = Unit
            viewModelScope.launch {
                try {
                    repository.save(it)
                    _dataState.value = FeedModelState()
                } catch (e: Exception) {
                    lastAction = ActionType.SAVE
                    _dataState.value = FeedModelState(error = true)
                }
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

    fun likedByID(id: Long) {
        lastAction = null
        lastLikedID = null
        viewModelScope.launch {
            try {
                repository.likeByID(id)
                _data.postValue(
                    _data.value?.copy(
                        posts = data.value?.posts.orEmpty()
                            .map {
                                if (it.id == id) it.copy(likedByMe = true, likes = it.likes + 1)
                                else it
                            })
                )
                _dataState.value = FeedModelState()
            } catch (e: Exception) {
                lastAction = ActionType.LIKE
                lastLikedID = id
                _dataState.value = FeedModelState(error = true)
            }
        }
    }

    fun disLikedByID(id: Long) {
        lastAction = null
        lastDisLikedID = null
        viewModelScope.launch {
            try {
                repository.disLikeByID(id)
                _data.postValue(
                    _data.value?.copy(
                        posts = data.value?.posts.orEmpty()
                            .map {
                                if (it.id == id) it.copy(likedByMe = false, likes = it.likes - 1)
                                else it
                            })
                )
                _dataState.value = FeedModelState()
            } catch (e: Exception) {
                lastAction = ActionType.DISLIKE
                lastLikedID = id
                _dataState.value = FeedModelState(error = true)
            }
        }
    }

    fun removeByID(id: Long) {
        lastAction = null
        lastRemovedID = null

        viewModelScope.launch {
            try {
                repository.removeByID(id)
                val updated = _data.value?.posts.orEmpty().filter {it.id != id}
                _data.postValue(
                    _data.value?.copy(
                        posts = updated
                    )
                )
            } catch (e: Exception) {
                lastAction = ActionType.REMOVE
                lastRemovedID = id
                val old = data.value?.posts.orEmpty()
                _dataState.value = FeedModelState(error = true)
                _data.postValue(_data.value?.copy(posts = old))
            }
        }
    }


    private var lastAction: ActionType? = null
    private var lastLikedID: Long? = null
    private var lastDisLikedID: Long? = null
    private var lastRemovedID: Long? = null
    private var lastSaved: Long? = null

    fun retry() {
        when (lastAction) {
            ActionType.SAVE -> retrySave()
            ActionType.LIKE -> retryLike()
            ActionType.DISLIKE -> retryDisLike()
            ActionType.REMOVE -> retryRemove()
            else -> loadPosts()
        }
    }

    private fun retryDisLike() {
        lastDisLikedID?.let {
            disLikedByID(it)
        }
    }

    private fun retrySave() {
        lastSaved?.let {
            save()
        }
    }

    private fun retryRemove() {
        lastRemovedID?.let {
            removeByID(it)
        }
    }

    private fun retryLike() {
        lastLikedID?.let {
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