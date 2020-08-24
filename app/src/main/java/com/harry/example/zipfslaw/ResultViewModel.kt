package com.harry.example.zipfslaw


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Locale
import kotlin.collections.HashMap

class ResultViewModel : ViewModel() {
    val wordFrequency: LiveData<Map<String, Int>> = MutableLiveData()
    val setOfWords: LiveData<Set<String>> = MutableLiveData()
    fun findResult(content: String) {
        viewModelScope.launch(Dispatchers.Default) {
            val pattern = Regex("[^a-z0-9 ]")
            var newContent = content.replace("\n", " ").toLowerCase(Locale.ROOT)
            newContent = pattern.replace(newContent, " ")
            val words: List<String> = newContent.split(" ").filter {
                it.isNotEmpty() && it.isNotBlank()
            }
            var frequency: MutableMap<String, Int> = HashMap()
            words.forEach {
                val currentWord = it
                if (!frequency.containsKey(currentWord) && !currentWord.equals(" ")) {
                    frequency.put(currentWord, 1)
                } else {
                    val currentFrequency = frequency.get(currentWord)
                    val newFrequency = currentFrequency?.inc()
                    newFrequency?.let {
                        frequency.put(currentWord, newFrequency)
                    }
                }
            }
            frequency = frequency.toList().sortedByDescending {
                it.second
            }.toMap() as MutableMap<String, Int>
            (setOfWords as MutableLiveData<Set<String>>).postValue(frequency.keys)
            (wordFrequency as MutableLiveData<Map<String, Int>>).postValue(frequency)
        }
    }
}