package tech.kjo.kjo_mind_care.ui.main.mood

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.unit.dp
import com.patrykandpatrick.vico.compose.cartesian.*
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLineCartesianLayer
import com.patrykandpatrick.vico.core.cartesian.Zoom
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.cartesian.data.lineSeries

import com.patrykandpatrick.vico.compose.cartesian.axis.rememberStart
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberBottom
import com.patrykandpatrick.vico.core.cartesian.CartesianChart
import com.patrykandpatrick.vico.core.common.component.LineComponent
import com.patrykandpatrick.vico.compose.common.component.rememberLineComponent

@Composable
fun MoodChart(
    modifier: Modifier = Modifier,
    moodValues: List<Float> = listOf(0f, 3f, 4f, 1f, 1f, 4f, 0f)
) {
    val producer = remember { CartesianChartModelProducer() }
    LaunchedEffect(moodValues) {
        producer.runTransaction {
            lineSeries {
                series(y = moodValues.map { it as Number })
            }
        }
    }


    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(220.dp),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(
            Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surfaceVariant),
            contentAlignment = Alignment.Center
        ) {
            CartesianChartHost(
                chart = CartesianChart(
                    rememberLineCartesianLayer(pointSpacing = 0.dp),
                    startAxis = null,
                    bottomAxis = null
                ),
                modelProducer = producer,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                zoomState = rememberVicoZoomState(
                    zoomEnabled = false,
                    initialZoom = Zoom { _, _, _ -> 1f },

                    )
//                    rememberBottom(
//                    label = listOf("Sat", "Sun", "Mon", "Tue", "Wed", "Thu", "Fri"),
//                    guideline = GuidelineStyle(
//                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),
//                        thickness = 1.dp,
//                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
//                    )
            )
//                scrollState = rememberChartScrollSpec(isScrollEnabled = false),

//                style = ChartStyle.fromColors(
//                    axisLabelColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
//                    axisLineColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
//                    lineColor = MaterialTheme.colorScheme.primary,
//                    lineThickness = 2.dp
//                )
//            )
        }
    }
}

