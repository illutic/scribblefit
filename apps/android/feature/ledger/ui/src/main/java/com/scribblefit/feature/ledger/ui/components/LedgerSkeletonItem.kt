package com.scribblefit.feature.ledger.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.unit.dp
import com.scribblefit.core.designsystem.ScribbleFitTheme
import com.scribblefit.core.designsystem.scribbleGlass

@Composable
internal fun LedgerSkeletonItem(modifier: Modifier = Modifier) {
    val infiniteTransition = rememberInfiniteTransition(label = "skeleton_transition")
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.2f,
        targetValue = 0.6f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "skeleton_alpha"
    )

    Surface(
        shape = RoundedCornerShape(ScribbleFitTheme.spacing.medium),
        modifier = modifier
            .fillMaxWidth()
            .scribbleGlass(cornerRadius = ScribbleFitTheme.spacing.medium)
            .alpha(alpha),
        color = ScribbleFitTheme.colors.surfaceContainerLow.copy(alpha = 0.5f)
    ) {
        Column(
            modifier = Modifier.padding(ScribbleFitTheme.spacing.medium)
        ) {
            Box(
                modifier = Modifier
                    .width(140.dp)
                    .height(20.dp)
                    .background(
                        color = ScribbleFitTheme.colors.midGray.copy(alpha = 0.5f),
                        shape = RoundedCornerShape(4.dp)
                    )
            )

            Spacer(modifier = Modifier.padding(vertical = ScribbleFitTheme.spacing.smallLarger))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(ScribbleFitTheme.spacing.medium)
                    .background(
                        color = ScribbleFitTheme.colors.midGray.copy(alpha = 0.3f),
                        shape = RoundedCornerShape(4.dp)
                    )
            )

            Spacer(modifier = Modifier.padding(vertical = 6.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth(0.7f)
                    .height(ScribbleFitTheme.spacing.medium)
                    .background(
                        color = ScribbleFitTheme.colors.midGray.copy(alpha = 0.3f),
                        shape = RoundedCornerShape(4.dp)
                    )
            )
        }
    }
}
