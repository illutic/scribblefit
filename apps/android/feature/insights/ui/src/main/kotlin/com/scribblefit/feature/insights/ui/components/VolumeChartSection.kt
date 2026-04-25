package com.scribblefit.feature.insights.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.scribblefit.feature.insights.ui.InsightsState
import com.scribblefit.feature.insights.ui.getVolumeChartTitle

@Composable
internal fun VolumeChartSection(state: InsightsState) {
    SectionContainer(title = state.getVolumeChartTitle()) {
        VolumeChart(
            points = state.volumePoints,
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp),
        )
    }
}
