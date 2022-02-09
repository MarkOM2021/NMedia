package ru.netology.nmedia.activity

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import ru.netology.nmedia.adapter.ActionListener
import ru.netology.nmedia.adapter.PostsAdapter
import ru.netology.nmedia.databinding.ActivityMainBinding
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.util.AndroidUtils
import ru.netology.nmedia.viewModel.PostViewModel
import java.text.DecimalFormat

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val viewModel: PostViewModel by viewModels()

        val adapter = PostsAdapter(
            object : ActionListener {
                override fun onLike(post: Post) {
                    viewModel.likedByID(post.id)
                }

                override fun onShare(post: Post) {
                    viewModel.sharedByID(post.id)
                }

                override fun onRemove(post: Post) {
                    viewModel.removeByID(post.id)
                }

                override fun onEdit(post: Post) {
                    viewModel.edit(post)
                }
            }
        )

        binding.list.adapter = adapter
        viewModel.data.observe(this, adapter::submitList)

        viewModel.edited.observe(this) {
            if (it.id == 0L) {
                return@observe
            }

            with(binding.editingContent) {
                text = it.content
            }
            with(binding.edition) {
                requestFocus()
                setText(it.content)
                binding.editionField.visibility = View.VISIBLE
            }
        }


        binding.closeEditMode.setOnClickListener {
            binding.editionField.visibility = View.GONE
            binding.edition.clearFocus()
            binding.edition.setText("")

        }

        binding.save.setOnClickListener {
            val text = binding.edition.text.toString()
            if (binding.edition.text.isNullOrBlank()) {
                Toast.makeText(this, "Content empty", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            viewModel.changeContent(text)
            viewModel.save()

            binding.editionField.visibility = View.GONE
            binding.edition.setText("")
            binding.edition.clearFocus()

            AndroidUtils.hideKeyboard(binding.edition)
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

