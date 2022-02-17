package ru.netology.nmedia.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import ru.netology.nmedia.R
import ru.netology.nmedia.adapter.ActionListener
import ru.netology.nmedia.adapter.PostsAdapter
import ru.netology.nmedia.databinding.ActivityMainBinding
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.viewModel.PostViewModel
import java.text.DecimalFormat

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val viewModel: PostViewModel by viewModels()

        val newPostContract = registerForActivityResult(NewPostActivity.Contract()) { result ->
            result?.let {
                viewModel.changeContent(it)
                viewModel.save()
            }
        }

        val adapter = PostsAdapter(
            object : ActionListener {
                override fun onLike(post: Post) {
                    viewModel.likedByID(post.id)
                }

                override fun onShare(post: Post) {
                    val intent = Intent().apply {
                        action = Intent.ACTION_SEND
                        type = "text/plain"
                        putExtra(Intent.EXTRA_TEXT, post.content)
                    }
                    val shareIntent =
                        Intent.createChooser(intent, getString(R.string.chooser_share_post))
                    startActivity(shareIntent)
                }

                override fun onRemove(post: Post) {
                    viewModel.removeByID(post.id)
                }

                override fun onEdit(post: Post) {
                    viewModel.edit(post)
                }

                override fun onPlay(post: Post) {
                    val intent = Intent(Intent.ACTION_VIEW).apply {
                        data = Uri.parse(post.video)
                    }
                    val playIntent =
                        Intent.createChooser(intent, getString(R.string.chooser_play_video))
                    startActivity(playIntent)
                }
            }
        )

        binding.list.adapter = adapter
        viewModel.data.observe(this) { posts ->
            adapter.submitList(posts)
        }

        viewModel.edited.observe(this) { post ->
            if (post.id == 0L) {
                return@observe
            }
            newPostContract.launch(post.content)
        }

        binding.addNewPost.setOnClickListener {
            newPostContract.launch("")
        }
    }
}

fun eventNumberFormatter(num: Int): String {
    val numDouble = num.toDouble()
    val df = DecimalFormat("#.#")
    when (num) {
        in 0..1_099 -> {
            return "$num"
        }
        in 1_100..10_000 -> {
            val rounded = df.format(numDouble / 1_000)
            return rounded + "K"
        }
        in 10001..999999 -> {
            val roundedK = num / 1_000
            return "$roundedK" + "K"
        }
        else -> {
            val roundedM = df.format(numDouble / 1_000_000)
            return roundedM + "M"
        }
    }
}

