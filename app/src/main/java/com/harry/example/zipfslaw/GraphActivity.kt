package com.harry.example.zipfslaw

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.anychart.AnyChart
import com.anychart.chart.common.dataentry.DataEntry
import com.anychart.chart.common.dataentry.ValueDataEntry
import com.anychart.charts.Cartesian
import com.anychart.enums.Anchor
import com.anychart.enums.MarkerType
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.activity_graph.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class GraphActivity : AppCompatActivity() {
    private val dataentries: ArrayList<DataEntry> = ArrayList()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_graph)
        //chart.setProgressBar(findViewById(R.id.graph_progress))
        chart.setOnRenderedListener {
            graph_progress.visibility=View.GONE
        }
        lifecycleScope.launch(Dispatchers.Default) {
            delay(2000)
            val linechart: Cartesian = AnyChart.line()
//            linechart.xScroller(true)
            linechart.xZoom(true)
            linechart.animation(true)
            linechart.title("Graph Result")
            linechart.xAxis(0).title("Words")
            linechart.yAxis(0).title("Frequency")
            val datas =
                Gson().fromJson<Map<String, Int>>(
                    intent?.getStringExtra(MainActivity.WORDS_FREQUENCIES),
                    object : TypeToken<Map<String, Int>>() {}.type
                )
            datas.onEach {
                val entry = ValueDataEntry(it.key, it.value)
                dataentries.add(entry)
            }
//            val set = Set.instantiate()
//            set.data(dataentries)
//            val mapping1 = set.mapAs("{ x: 'x', value: 'value' }")
            linechart.line(dataentries).color("#FF0000").name("Words Frequency")
            .hover().apply {
                enabled(true)
                markers().apply {
                    type(MarkerType.CIRCLE)
                    size(4)
                }
            }.tooltip().apply {
                anchor(Anchor.LEFT_CENTER)
                position("right")
                offsetX(5)
                offsetY(5)
            }
            linechart.legend().enabled(true)
            linechart.legend().fontSize(13.0)
//            //linechart.legend().padding(0.0, 0.0, 10.0, 0.0)
//            //linechart.data()
            withContext(Dispatchers.Main) {
                chart.setChart(linechart)
                chart.visibility = View.VISIBLE
                chart.setZoomEnabled(true)
            }
        }
    }
}