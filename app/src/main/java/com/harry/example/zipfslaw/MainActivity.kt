package com.harry.example.zipfslaw

import android.content.ClipboardManager
import android.content.Intent
import android.graphics.Typeface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.Html
import android.text.method.ScrollingMovementMethod
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import ir.androidexception.datatable.model.DataTableHeader
import ir.androidexception.datatable.model.DataTableRow
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.DisposableHandle
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.ArrayList

class MainActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var resultViewModel: ResultViewModel
    private lateinit var words: Set<String>

    companion object {
        const val WORDS_LIST = "list"
        const val WORDS_FREQUENCIES = "word_frequency"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initViewModel()
        observeWordFrequency()
        observeListWords()
        find_result.setOnClickListener(this)
        clear_text.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        v?.let {
            val buttonId = it.id
            when (buttonId) {
                R.id.find_result -> {
                    val content = textContent.text.toString().trim()
                    if (content.isNotEmpty() || content.isNotBlank()) {
                        disableOrEnableViews(false)
                        showOrHideProgress(View.VISIBLE)
                        findResult(content)
                    } else {
                        Snackbar.make(
                            findViewById(android.R.id.content),
                            "Cannot be empty or blank",
                            Snackbar.LENGTH_LONG
                        ).show()
                    }
                }
                else -> clearText()
            }
        }
    }

    private fun findResult(content: String) {
        resultViewModel.findResult(content)
    }

    private fun clearText() {
        textContent.text = Editable.Factory().newEditable("")
    }

    private fun initViewModel() {
        resultViewModel = ViewModelProvider(this).get(ResultViewModel::class.java)
    }

    private fun observeWordFrequency() {
        resultViewModel.wordFrequency.observe(this, Observer {
            lifecycleScope.launch(Dispatchers.Default) {
                val gson = Gson()
                val map_type = object : TypeToken<Map<String, Int>>() {}.type
                val set_type = object : TypeToken<Set<String>>() {}.type
                val intent = Intent(applicationContext, ResultActivity::class.java).apply {
                    putExtra(WORDS_FREQUENCIES, gson.toJson(it, map_type))
                    putExtra(WORDS_LIST, gson.toJson(words, set_type))
                }
                startActivity(intent)
                withContext(Dispatchers.Main) {
                    disableOrEnableViews(true)
                    showOrHideProgress(View.GONE)
                }
            }
        })
    }

    private fun observeListWords() {
        resultViewModel.setOfWords.observe(this, Observer {
            words = it
        })
    }

    private fun disableOrEnableViews(enabled: Boolean) {
        group.isEnabled = enabled
    }

    private fun showOrHideProgress(visibilty: Int) {
        resultProgress.visibility = visibilty
    }
}