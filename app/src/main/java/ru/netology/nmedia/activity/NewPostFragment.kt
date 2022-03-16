package ru.netology.nmedia.activity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import ru.netology.nmedia.databinding.FragmentNewPostBinding
import ru.netology.nmedia.util.StringArg
import ru.netology.nmedia.viewModel.PostViewModel
import androidx.navigation.fragment.findNavController

class NewPostFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentNewPostBinding.inflate(layoutInflater)

        arguments?.textArg?.let {
            binding.edit.setText(it)
        }

        val viewModel: PostViewModel by viewModels(::requireParentFragment)

        binding.edit.requestFocus()
        binding.add.setOnClickListener {
            if (binding.edit.text.toString().isNotBlank()) {
                val content = binding.edit.text.toString()
                viewModel.changeContent(content)
                viewModel.save()
            }
            findNavController().navigateUp()
        }

        binding.cancelEdition.setOnClickListener {
            findNavController().navigateUp()
        }
        return binding.root
    }

    companion object {
        var Bundle.textArg: String? by StringArg
    }
}