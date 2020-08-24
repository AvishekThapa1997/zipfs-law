package com.harry.example.zipfslaw

import android.content.Intent
import android.graphics.Typeface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.lifecycle.lifecycleScope
import com.anychart.AnyChart
import com.anychart.chart.common.dataentry.DataEntry
import com.anychart.chart.common.dataentry.ValueDataEntry
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import ir.androidexception.datatable.model.DataTableHeader
import ir.androidexception.datatable.model.DataTableRow
import kotlinx.android.synthetic.main.activity_result.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class ResultActivity : AppCompatActivity() {
    private lateinit var wordFrequencies: Map<String, Int>
    private lateinit var words: Set<String>
    private val rows: ArrayList<DataTableRow> = ArrayList()
    private var data: Intent? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result)
        data = intent
        lifecycleScope.launch(Dispatchers.Default) {
            val gson = Gson()
            val map_type = object : TypeToken<Map<String, Int>>() {}.type
            val set_type = object : TypeToken<Set<String>>() {}.type
            wordFrequencies = gson.fromJson(
                data?.getStringExtra(MainActivity.WORDS_FREQUENCIES),
                map_type
            )
            words = gson.fromJson(
                data?.getStringExtra(MainActivity.WORDS_LIST),
                set_type
            )
            val header: DataTableHeader = DataTableHeader.Builder().apply {
                item("Word", Typeface.BOLD)
                item("Frequency", Typeface.BOLD)
            }.build()
            words.forEach {
                val currentWord = it
                val row = DataTableRow.Builder().apply {
                    value(currentWord)
                    value(wordFrequencies.get(currentWord).toString())
                }.build()
                rows.add(row)
            }
            withContext(Dispatchers.Main) {
                result_table.header = header
                result_table.rows = rows
                result_table.inflate(applicationContext)
                group.visibility = View.VISIBLE
            }
        }
        see_graph.setOnClickListener {
            val intent = Intent(applicationContext, GraphActivity::class.java).apply {
                putExtra(
                    MainActivity.WORDS_FREQUENCIES,
                    this@ResultActivity.data?.getStringExtra(MainActivity.WORDS_FREQUENCIES)
                )
            }
            startActivity(intent)
        }
    }
}