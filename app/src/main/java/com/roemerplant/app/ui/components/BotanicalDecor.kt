package com.roemerplant.app.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.min

private fun DrawScope.leafPath(length: Float, width: Float): Path = Path().apply {
    moveTo(0f, -length)
    quadraticBezierTo(width, -length * 0.3f, 0f, length)
    quadraticBezierTo(-width, -length * 0.3f, 0f, -length)
    close()
}

private fun DrawScope.drawLeaf(center: Offset, length: Float, angle: Float, color: Color) {
    translate(center.x, center.y) {
        rotate(angle) {
            drawPath(leafPath(length, length * 0.45f), color)
        }
    }
}

@Composable
fun LeafPattern(modifier: Modifier = Modifier) {
    val color = androidx.compose.material3.MaterialTheme.colorScheme.primary.copy(alpha = 0.07f)
    Canvas(modifier = modifier) {
        val leaves = listOf(
            Triple(0.08f, 0.12f, 35f),
            Triple(0.92f, 0.08f, -20f),
            Triple(0.5f, 0.5f, 70f),
            Triple(0.15f, 0.72f, -45f),
            Triple(0.85f, 0.8f, 15f),
            Triple(0.35f, 0.3f, 100f),
            Triple(0.7f, 0.6f, -80f)
        )
        val leafLength = min(size.width, size.height) * 0.07f
        leaves.forEach { (fx, fy, angle) ->
            drawLeaf(
                center = Offset(size.width * fx, size.height * fy),
                length = leafLength,
                angle = angle,
                color = color
            )
        }
    }
}

@Composable
fun GrowingPlantLoader(
    modifier: Modifier = Modifier,
    size: Dp = 28.dp,
    stemColor: Color = Color.Unspecified,
    leafColor: Color = Color.Unspecified,
    potColor: Color = Color.Unspecified
) {
    val defaultStem = if (stemColor == Color.Unspecified) {
        androidx.compose.material3.MaterialTheme.colorScheme.primary
    } else stemColor
    val defaultLeaf = if (leafColor == Color.Unspecified) {
        androidx.compose.material3.MaterialTheme.colorScheme.primary
    } else leafColor
    val defaultPot = if (potColor == Color.Unspecified) {
        androidx.compose.material3.MaterialTheme.colorScheme.tertiary
    } else potColor

    val transition = rememberInfiniteTransition(label = "plant")
    val growth by transition.animateFloat(
        initialValue = 0.15f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "growth"
    )

    Canvas(modifier = modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        val potWidth = w * 0.5f
        val potHeight = h * 0.22f
        val potLeft = (w - potWidth) / 2f
        val potTop = h - potHeight

        drawRoundRect(
            color = defaultPot,
            topLeft = Offset(potLeft, potTop),
            size = Size(potWidth, potHeight),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(w * 0.06f)
        )

        val stemBase = Offset(w / 2f, potTop)
        val stemLength = (h - potHeight) * 0.75f * growth
        drawLine(
            color = defaultStem,
            start = stemBase,
            end = Offset(w / 2f, potTop - stemLength),
            strokeWidth = w * 0.06f
        )

        val leafSize = w * 0.22f * growth
        val leafY1 = potTop - stemLength * 0.35f
        val leafY2 = potTop - stemLength * 0.7f
        drawLeaf(Offset(w / 2f, leafY1), leafSize, -50f, defaultLeaf)
        drawLeaf(Offset(w / 2f, leafY2), leafSize, 50f, defaultLeaf)

        drawCircle(
            color = defaultLeaf,
            radius = leafSize * 0.35f,
            center = Offset(w / 2f, potTop - stemLength - leafSize * 0.3f)
        )
    }
}

@Composable
fun WashiTape(modifier: Modifier = Modifier) {
    androidx.compose.material3.Surface(
        color = androidx.compose.material3.MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f),
        shape = androidx.compose.foundation.shape.RoundedCornerShape(bottomStart = 4.dp, bottomEnd = 4.dp),
        border = androidx.compose.foundation.BorderStroke(
            width = 0.5.dp,
            color = androidx.compose.material3.MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
        ),
        modifier = modifier
    ) {}
}

@Composable
fun RefStamp(ref: String, modifier: Modifier = Modifier) {
    androidx.compose.material3.Surface(
        color = androidx.compose.material3.MaterialTheme.colorScheme.surface.copy(alpha = 0.75f),
        shape = androidx.compose.foundation.shape.RoundedCornerShape(4.dp),
        border = androidx.compose.foundation.BorderStroke(
            width = 1.dp,
            color = androidx.compose.material3.MaterialTheme.colorScheme.outline.copy(alpha = 0.8f)
        ),
        modifier = modifier
    ) {
        androidx.compose.material3.Text(
            text = ref,
            style = androidx.compose.material3.MaterialTheme.typography.labelMedium,
            color = androidx.compose.material3.MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
        )
    }
}
