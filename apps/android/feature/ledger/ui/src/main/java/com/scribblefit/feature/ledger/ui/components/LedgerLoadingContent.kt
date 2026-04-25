package com.scribblefit.feature.ledger.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.scribblefit.core.designsystem.ScribbleFitTheme

@Composable
internal fun LedgerLoadingContent(
    modifier: Modifier = Modifier,
    skeletonItemsCount: Int = 5,
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(
            horizontal = ScribbleFitTheme.spacing.large,
            vertical = ScribbleFitTheme.spacing.small
        ),
        verticalArrangement = Arrangement.spacedBy(ScribbleFitTheme.spacing.large)
    ) {
        items(skeletonItemsCount) {
            LedgerSkeletonItem()
        }
    }
}