package ru.netology.nmedia.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.activity.result.launch
import androidx.activity.viewModels
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.netology.nmedia.R
import ru.netology.nmedia.activity.MainActivity
import ru.netology.nmedia.activity.NewPostActivity
import ru.netology.nmedia.activity.eventNumberFormatter
import ru.netology.nmedia.databinding.CardPostBinding
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.viewModel.PostViewModel

interface ActionListener {
    fun onLike(post: Post) {}
    fun onShare(post: Post) {}
    fun onRemove(post: Post) {}
    fun onEdit(post: Post) {}
    fun onPlay(post: Post) {}
}

class PostsAdapter(
    private val actionListener: ActionListener
) : ListAdapter<Post, PostViewHolder>(PostDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val binding = CardPostBinding.inflate(LayoutInflater.from(parent.context),
            parent, false)
        return PostViewHolder(binding, actionListener)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val post = getItem(position)
        holder.bind(post)
    }
}

class PostViewHolder(
    private val binding: CardPostBinding,
    private val actionListener: ActionListener
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(post: Post) {
        binding.apply {
            author.text = post.author
            published.text = post.published
            content.text = post.content
            like.isChecked = post.likedByMe
            like.text = eventNumberFormatter(post.likes)
            share.text = eventNumberFormatter(post.shares)
            videoName.text = post.videoName
            videoGroup.visibility = if (post.video.isBlank()) View.GONE else View.VISIBLE
            like.setOnClickListener {
                actionListener.onLike(post)
            }
            share.setOnClickListener {
                actionListener.onShare(post)
            }
            onPlayButton.setOnClickListener {
                actionListener.onPlay(post)
            }
            screenShot.setOnClickListener {
                actionListener.onPlay(post)
            }
            menu.setOnClickListener {
                PopupMenu(binding.root.context, binding.menu).apply {
                    inflate(R.menu.post_menu)
                    setOnMenuItemClickListener {
                        when (it.itemId) {
                            R.id.remove -> {
                                actionListener.onRemove(post)
                                true
                            }
                            R.id.edit -> {
                                actionListener.onEdit(post)
                                true
                            }
                            else -> false
                        }
                    }
                }
                    .show()
            }
        }
    }
}

class PostDiffCallback : DiffUtil.ItemCallback<Post>() {
    override fun areItemsTheSame(oldItem: Post, newItem: Post): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Post, newItem: Post): Boolean {
        return oldItem == newItem
    }
}