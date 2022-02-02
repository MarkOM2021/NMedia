package ru.netology.nmedia

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import ru.netology.nmedia.databinding.ActivityMainBinding
import ru.netology.nmedia.viewmodel.PostViewModel
import java.text.DecimalFormat

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val viewModel: PostViewModel by viewModels()
        with(binding) {
            viewModel.data.observe(this@MainActivity) { post ->
                author.text = post.author
                published.text = post.published
                content.text = post.content
                if (post.likedByMe) {
                    like?.setImageResource(R.drawable.ic_liked_24)
                } else {
                    like?.setImageResource(R.drawable.ic_like_24)
                }

                likeCount.text = eventNumberFormatter(post.likes)
                shareCount.text = eventNumberFormatter(post.shares)
            }

            binding.like.setOnClickListener {
                viewModel.like()
            }

            binding.share.setOnClickListener {
                viewModel.share()
            }
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

