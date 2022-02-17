package ru.netology.nmedia.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContract
import ru.netology.nmedia.databinding.ActivityNewPostBinding

class NewPostActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = ActivityNewPostBinding.inflate(layoutInflater)
        setContentView(binding.root)

        intent?.getStringExtra(Intent.EXTRA_TEXT)?.let {
                binding.edit.setText(it)
        }

        binding.edit.requestFocus()
        binding.add.setOnClickListener {
            val text = binding.edit.text.toString()
            if (text.isBlank()) {
                setResult(Activity.RESULT_CANCELED)
            } else {
                setResult(Activity.RESULT_OK, Intent().apply {putExtra(Intent.EXTRA_TEXT, text)})
            }
            finish()
        }

        binding.cancelEdition.setOnClickListener {
            setResult(Activity.RESULT_CANCELED)
            finish()
        }
    }

    class Contract : ActivityResultContract<String, String?>() {

        override fun createIntent(context: Context, input: String): Intent {
            return Intent(context, NewPostActivity::class.java).apply { putExtra(Intent.EXTRA_TEXT, input)}
        }

        override fun parseResult(resultCode: Int, intent: Intent?): String? {
            return if (resultCode == RESULT_OK) {
                intent?.getStringExtra(Intent.EXTRA_TEXT)
            } else {
                null
            }
        }
    }
}