package ru.netology.nmedia.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ru.netology.nmedia.dto.Post

class PostRepositoryInMemoryImpl : PostRepository {

    private val post = Post(
        id = 1,
        author = "Нетология. Университет интернет-профессий будущего",
        content = "Привет, это новая Нетология! Когда-то Нетология начиналась с интенсивов по онлайн-маркетингу. Затем появились курсы по дизайну, разработке, аналитике и управлению. Мы растём сами и помогаем расти студентам: от новичков до уверенных профессионалов. Но самое важное остаётся с нами: мы верим, что в каждом уже есть сила, которая заставляет хотеть больше, целиться выше, бежать быстрее. Наша миссия — помочь встать на путь роста и начать цепочку перемен → http://netolo.gy/fyb",
        published = "21 мая в 18:36",
        likes = 1099,
        shares = 999_998,
        likedByMe = false
    )
    private val _data = MutableLiveData(post)

    override val data: LiveData<Post>
        get() = _data

    override fun like() {
        val post = _data.value ?: return
        _data.value = _data.value?.copy(
            likes = if (!post.likedByMe) post.likes + 1 else post.likes - 1,
            likedByMe = !post.likedByMe
        )
    }

    override fun share() {
        val post = _data.value ?: return
        _data.value = _data.value?.copy(
            shares = post.shares + 1
        )
    }
}