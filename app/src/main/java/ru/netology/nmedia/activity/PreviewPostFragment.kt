package ru.netology.nmedia.activity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import ru.netology.nmedia.R
import ru.netology.nmedia.activity.NewPostFragment.Companion.textArg
import ru.netology.nmedia.databinding.FragmentPostBinding
import ru.netology.nmedia.util.PostFinder
import ru.netology.nmedia.viewModel.PostViewModel

class PreviewPostFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentPostBinding.inflate(layoutInflater)

        val viewModel: PostViewModel by viewModels(::requireParentFragment)

        viewModel.data.observe(viewLifecycleOwner) { posts ->
            val id = arguments?.postID
            val findPost = posts.find { id == it.id }
            if (findPost != null) {
                binding.postPreview.content.text = findPost.content
                binding.postPreview.published.text = findPost.published
                binding.postPreview.author.text = findPost.author
                binding.postPreview.like.text = findPost.likes.toString()
                binding.postPreview.share.text = findPost.shares.toString()
                binding.postPreview.videoGroup.visibility =
                    if (findPost.video.isBlank()) View.GONE else View.VISIBLE
                binding.postPreview.videoName.text = findPost.videoName
                binding.postPreview.menu.setOnClickListener {
                    PopupMenu(binding.root.context, binding.postPreview.menu).apply {
                        inflate(R.menu.post_menu)
                        setOnMenuItemClickListener {
                            when (it.itemId) {
                                R.id.remove -> {
                                    findNavController().navigateUp()
                                    true
                                }
                                R.id.edit -> {
                                    findNavController().navigate(R.id.action_post_preview_to_newPostFragment,
                                        Bundle().apply {
                                            textArg = findPost.content
                                        })
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

        return binding.root
    }

    companion object {
        var Bundle.postID: Long by PostFinder
    }
}