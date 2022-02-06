package ru.netology.nmedia.activity

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import ru.netology.nmedia.activity.adapter.PostsAdapter
import ru.netology.nmedia.databinding.ActivityMainBinding
import ru.netology.nmedia.viewModel.PostViewModel
import java.text.DecimalFormat

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val viewModel: PostViewModel by viewModels()
        val adapter = PostsAdapter {
            viewModel.likedByID(it.id)
            viewModel.sharedByID(it.id)
        }
        binding.root.adapter = adapter
        viewModel.data.observe(this, adapter::submitList)
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

