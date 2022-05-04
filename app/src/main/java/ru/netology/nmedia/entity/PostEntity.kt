package ru.netology.nmedia.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.netology.nmedia.dto.Post

@Entity
data class PostEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val author: String,
    val authorAvatar: String,
    val content: String,
    val published: String,
    val likes: Int = 0,
    val shares: Int = 0,
    val views: Int = 0,
    val likedByMe: Boolean,
    val sharedByMe: Boolean,
    val video: String = "",
    val videoName: String = ""
) {
    fun toDto() =
        Post(id, author, authorAvatar, content, published, likes, shares, views, likedByMe, sharedByMe, video, videoName)

    companion object {
        fun fromDto(dto: Post) =
            PostEntity(
                dto.id,
                dto.author,
                dto.authorAvatar,
                dto.content,
                dto.published,
                dto.likes,
                dto.shares,
                dto.views,
                dto.likedByMe,
                dto.sharedByMe,
                dto.video,
                dto.videoName
            )
    }
}