package com.example.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

@Composable
fun CemaLogoGraphic(
    modifier: Modifier = Modifier,
    size: Dp = 220.dp
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = modifier
    ) {
        // Metallic Emblem Badge
        Box(
            modifier = Modifier
                .size(size)
                .shadow(16.dp, CircleShape)
                .clip(CircleShape)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color(0xFFE2E8F0),
                            Color(0xFFCBD5E1),
                            Color(0xFF94A3B8),
                            Color(0xFF64748B),
                            Color(0xFF334155)
                        )
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp)
            ) {
                val width = this.size.width
                val height = this.size.height
                val center = Offset(width / 2f, height / 2f)
                val radius = width / 2f

                // Metallic Bezel Outer Rim
                drawCircle(
                    brush = Brush.sweepGradient(
                        listOf(
                            Color(0xFFF8FAFC),
                            Color(0xFF94A3B8),
                            Color(0xFFE2E8F0),
                            Color(0xFF475569),
                            Color(0xFFF8FAFC)
                        )
                    ),
                    radius = radius - 4f,
                    style = Stroke(width = 12f)
                )

                drawCircle(
                    color = Color(0xFF1E293B).copy(alpha = 0.2f),
                    radius = radius - 12f,
                    style = Stroke(width = 4f)
                )

                // 1. OPEN BIBLE AT THE BASE
                val biblePath = Path().apply {
                    moveTo(width * 0.15f, height * 0.72f)
                    cubicTo(width * 0.35f, height * 0.62f, width * 0.45f, height * 0.78f, center.x, height * 0.70f)
                    cubicTo(width * 0.55f, height * 0.78f, width * 0.65f, height * 0.62f, width * 0.85f, height * 0.72f)
                    lineTo(width * 0.82f, height * 0.82f)
                    cubicTo(width * 0.65f, height * 0.72f, width * 0.55f, height * 0.88f, center.x, height * 0.82f)
                    cubicTo(width * 0.45f, height * 0.88f, width * 0.35f, height * 0.72f, width * 0.18f, height * 0.82f)
                    close()
                }
                drawPath(
                    path = biblePath,
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF1E40AF),
                            Color(0xFF3B82F6),
                            Color(0xFF60A5FA),
                            Color(0xFF1E3A8A)
                        )
                    )
                )
                drawPath(
                    path = biblePath,
                    color = Color(0xFF93C5FD),
                    style = Stroke(width = 3f)
                )

                // Bible Pages Lines
                val pagesPath = Path().apply {
                    moveTo(width * 0.22f, height * 0.73f)
                    quadraticTo(width * 0.35f, height * 0.67f, center.x - 4f, height * 0.72f)
                    moveTo(center.x + 4f, height * 0.72f)
                    quadraticTo(width * 0.65f, height * 0.67f, width * 0.78f, height * 0.73f)
                }
                drawPath(
                    path = pagesPath,
                    color = Color.White.copy(alpha = 0.8f),
                    style = Stroke(width = 2.5f)
                )

                // 2. THE METALLIC BLUE CROSS
                val crossPath = Path().apply {
                    // Vertical Beam
                    moveTo(center.x - width * 0.08f, height * 0.18f)
                    lineTo(center.x + width * 0.08f, height * 0.18f)
                    lineTo(center.x + width * 0.07f, height * 0.75f)
                    lineTo(center.x - width * 0.07f, height * 0.75f)
                    close()
                }
                val crossArmPath = Path().apply {
                    // Horizontal Beam
                    moveTo(width * 0.22f, height * 0.32f)
                    lineTo(width * 0.68f, height * 0.32f)
                    lineTo(width * 0.66f, height * 0.42f)
                    lineTo(width * 0.24f, height * 0.42f)
                    close()
                }

                val crossBrush = Brush.horizontalGradient(
                    colors = listOf(
                        Color(0xFF1D4ED8),
                        Color(0xFF60A5FA),
                        Color(0xFFEFF6FF),
                        Color(0xFF2563EB),
                        Color(0xFF1E3A8A)
                    )
                )
                drawPath(crossPath, crossBrush)
                drawPath(crossArmPath, crossBrush)

                // Cross 3D bevel stroke
                drawPath(crossPath, Color(0xFF93C5FD), style = Stroke(width = 3f))
                drawPath(crossArmPath, Color(0xFF93C5FD), style = Stroke(width = 3f))

                // 3. RED HEART ON THE LEFT
                val heartPath = Path().apply {
                    val hx = width * 0.33f
                    val hy = height * 0.54f
                    moveTo(hx, hy)
                    cubicTo(hx - width * 0.12f, hy - height * 0.12f, hx - width * 0.20f, hy + height * 0.02f, hx, hy + height * 0.14f)
                    cubicTo(hx + width * 0.20f, hy + height * 0.02f, hx + width * 0.12f, hy - height * 0.12f, hx, hy)
                    close()
                }
                drawPath(
                    path = heartPath,
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color(0xFFEF4444),
                            Color(0xFFDC2626),
                            Color(0xFF991B1B)
                        ),
                        center = Offset(width * 0.31f, height * 0.52f)
                    )
                )
                drawPath(
                    path = heartPath,
                    color = Color(0xFFFECACA),
                    style = Stroke(width = 2.5f)
                )

                // 4. FIRE FLAMES ON THE RIGHT
                val flamePath1 = Path().apply {
                    moveTo(width * 0.52f, height * 0.65f)
                    cubicTo(width * 0.65f, height * 0.52f, width * 0.62f, height * 0.38f, width * 0.56f, height * 0.26f)
                    cubicTo(width * 0.68f, height * 0.36f, width * 0.75f, height * 0.48f, width * 0.68f, height * 0.65f)
                    close()
                }
                val flamePath2 = Path().apply {
                    moveTo(width * 0.55f, height * 0.58f)
                    cubicTo(width * 0.62f, height * 0.45f, width * 0.60f, height * 0.35f, width * 0.58f, height * 0.29f)
                    cubicTo(width * 0.66f, height * 0.38f, width * 0.70f, height * 0.48f, width * 0.62f, height * 0.58f)
                    close()
                }

                drawPath(
                    path = flamePath1,
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFFFEF08A),
                            Color(0xFFF59E0B),
                            Color(0xFFEF4444)
                        )
                    )
                )
                drawPath(
                    path = flamePath2,
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFFFFFFFF),
                            Color(0xFFFDE047),
                            Color(0xFFEA580C)
                        )
                    )
                )

                // Metallic Shine Reflection Across Shield
                val shinePath = Path().apply {
                    moveTo(0f, 0f)
                    lineTo(width * 0.4f, 0f)
                    lineTo(width * 0.1f, height)
                    lineTo(0f, height)
                    close()
                }
                drawPath(
                    path = shinePath,
                    color = Color.White.copy(alpha = 0.12f)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // BOLD METALLIC 3D "CEMA" TEXT AT THE BOTTOM
        Box(contentAlignment = Alignment.Center) {
            // Shadow layer for 3D depth
            Text(
                text = "CEMA",
                style = MaterialTheme.typography.displayMedium.copy(
                    fontWeight = FontWeight.Black,
                    fontFamily = FontFamily.Serif,
                    letterSpacing = 6.sp,
                    fontSize = (size.value * 0.22f).sp
                ),
                color = Color(0xFF0F172A).copy(alpha = 0.6f),
                modifier = Modifier.offset(x = 2.dp, y = 3.dp)
            )

            // Metallic gradient text
            Text(
                text = "CEMA",
                style = MaterialTheme.typography.displayMedium.copy(
                    fontWeight = FontWeight.Black,
                    fontFamily = FontFamily.Serif,
                    letterSpacing = 6.sp,
                    fontSize = (size.value * 0.22f).sp,
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFFFFFFFF),
                            Color(0xFFE2E8F0),
                            Color(0xFF94A3B8),
                            Color(0xFF475569)
                        )
                    )
                )
            )
        }

        Text(
            text = "CHRIST ENVOY MINISTRY",
            style = MaterialTheme.typography.labelMedium.copy(
                fontWeight = FontWeight.Bold,
                letterSpacing = 2.sp
            ),
            color = MaterialTheme.colorScheme.primary
        )
    }
}

@Composable
fun SplashScreen(
    onSplashFinished: () -> Unit
) {
    var startAnimation by remember { mutableStateOf(false) }

    val scaleAnimate = animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0.7f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "SplashScale"
    )

    val alphaAnimate = animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0f,
        animationSpec = tween(durationMillis = 800),
        label = "SplashAlpha"
    )

    LaunchedEffect(Unit) {
        startAnimation = true
        delay(2200)
        onSplashFinished()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF0F172A),
                        Color(0xFF1E293B),
                        Color(0xFF0F172A)
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .scale(scaleAnimate.value)
                .alpha(alphaAnimate.value)
        ) {
            CemaLogoGraphic(size = 240.dp)

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "Official Discipleship & Study Platform",
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFF94A3B8)
            )
        }
    }
}
