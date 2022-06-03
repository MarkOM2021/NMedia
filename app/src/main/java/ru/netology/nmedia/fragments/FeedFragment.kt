package ru.netology.nmedia.fragments

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import ru.netology.nmedia.R
import ru.netology.nmedia.fragments.NewPostFragment.Companion.textArg
import ru.netology.nmedia.fragments.PreviewPostFragment.Companion.postID
import ru.netology.nmedia.adapter.ActionListener
import ru.netology.nmedia.adapter.PostsAdapter
import ru.netology.nmedia.databinding.FragmentFeedBinding
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.model.FeedModelState
import ru.netology.nmedia.viewModel.PostViewModel
import java.text.DecimalFormat

class FeedFragment : Fragment() {

    private val viewModel: PostViewModel by viewModels(ownerProducer = ::requireParentFragment)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentFeedBinding.inflate(inflater, container, false)

        val adapter = PostsAdapter(object : ActionListener {
                override fun onLike(post: Post) {
                    if (post.likedByMe) viewModel.disLikedByID(post.id)
                    else viewModel.likedByID(post.id)
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

                override fun onPreview(post: Post) {
                    findNavController().navigate(
                        R.id.action_mainActivity_to_post_preview,
                        Bundle().apply {
                            postID = post.id
                        })
                }
            }
        )

        binding.list.adapter = adapter
        viewModel.data.observe(viewLifecycleOwner) { data ->
            adapter.submitList(data.posts)
            binding.emptyText.isVisible = data.empty
        }

        viewModel.dataState.observe(viewLifecycleOwner) { state ->
            binding.progress.isVisible = state.loading
            binding.errorGroup.isVisible = state.error
            binding.serverErrorGroup.isVisible = state.serverNoResponse
            if(state.error) {
                Snackbar.make(binding.root, R.string.error_loading, Snackbar.LENGTH_LONG)
                    .setAction(R.string.retry_loading) {viewModel.loadPosts()}
                    .show()
            }
            binding.swiperefresh.isRefreshing = state.refreshing
        }

        viewModel.edited.observe(viewLifecycleOwner) { post ->
            post ?: return@observe
            if (post.id == 0L) {
                return@observe
            }
            findNavController().navigate(R.id.action_mainActivity_to_newPostFragment,
                Bundle().apply { textArg = post.content })
        }

        binding.addNewPost.setOnClickListener {
            findNavController().navigate(R.id.action_mainActivity_to_newPostFragment)
        }

        binding.swiperefresh.setOnRefreshListener {
            FeedModelState(refreshing = true)
            viewModel.refresh()
        }

        binding.retryButton.setOnClickListener {
            viewModel.loadPosts()
        }

        binding.retryAgainButton.setOnClickListener {
            viewModel.retry()
        }

        return binding.root
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

