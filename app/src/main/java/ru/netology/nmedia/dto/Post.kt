package ru.netology.nmedia.dto

import ru.netology.nmedia.enumeration.AttachmentType

data class Post(
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
    val attachment: Attachment? = null,
    //val video: String =" video ",
    //val videoName: String = " name"
)

data class Attachment(
    val url: String,
    val type: AttachmentType,
)