package com.example.util

import android.content.Context
import android.content.Intent
import android.graphics.*
import android.os.Build
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream

enum class VerseCardTheme(val label: String, val bgColors: IntArray, val textColor: Int, val accentColor: Int) {
    ROYAL_GOLD(
        label = "Royal Gold",
        bgColors = intArrayOf(Color.parseColor("#10172A"), Color.parseColor("#1E293B"), Color.parseColor("#0F172A")),
        textColor = Color.parseColor("#F8FAFC"),
        accentColor = Color.parseColor("#D4AF37")
    ),
    SUNRISE_HOPE(
        label = "Sunrise Hope",
        bgColors = intArrayOf(Color.parseColor("#78350F"), Color.parseColor("#B45309"), Color.parseColor("#D97706")),
        textColor = Color.parseColor("#FFFBEB"),
        accentColor = Color.parseColor("#FDE68A")
    ),
    DEEP_SLATE(
        label = "Deep Slate",
        bgColors = intArrayOf(Color.parseColor("#020617"), Color.parseColor("#0F172A"), Color.parseColor("#1E293B")),
        textColor = Color.parseColor("#F1F5F9"),
        accentColor = Color.parseColor("#38BDF8")
    ),
    PLATINUM_CLEAN(
        label = "Platinum Light",
        bgColors = intArrayOf(Color.parseColor("#FFFFFF"), Color.parseColor("#F1F5F9"), Color.parseColor("#E2E8F0")),
        textColor = Color.parseColor("#0F172A"),
        accentColor = Color.parseColor("#1E3A8A")
    ),
    CRIMSON_FLAME(
        label = "Crimson Grace",
        bgColors = intArrayOf(Color.parseColor("#450A0A"), Color.parseColor("#7F1D1D"), Color.parseColor("#991B1B")),
        textColor = Color.parseColor("#FEF2F2"),
        accentColor = Color.parseColor("#FDE047")
    )
}

enum class VerseAspectRatio(val label: String, val width: Int, val height: Int) {
    SQUARE("Square 1:1", 1080, 1080),
    STORY("Story 9:16", 1080, 1920),
    LANDSCAPE("Banner 16:9", 1200, 675)
}

object VerseShareHelper {

    fun generateVerseBitmap(
        context: Context,
        verseText: String,
        reference: String,
        version: String,
        theme: VerseCardTheme = VerseCardTheme.ROYAL_GOLD,
        aspectRatio: VerseAspectRatio = VerseAspectRatio.SQUARE
    ): Bitmap {
        val width = aspectRatio.width
        val height = aspectRatio.height
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        // 1. Background Gradient
        val bgPaint = Paint().apply {
            isAntiAlias = true
            shader = LinearGradient(
                0f, 0f, width.toFloat(), height.toFloat(),
                theme.bgColors, null, Shader.TileMode.CLAMP
            )
        }
        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), bgPaint)

        // 2. Decorative Golden Frame
        val frameMargin = width * 0.06f
        val framePaint = Paint().apply {
            isAntiAlias = true
            color = theme.accentColor
            style = Paint.Style.STROKE
            strokeWidth = width * 0.005f
            alpha = 180
        }
        val frameRect = RectF(
            frameMargin,
            frameMargin,
            width - frameMargin,
            height - frameMargin
        )
        canvas.drawRoundRect(frameRect, frameMargin * 0.3f, frameMargin * 0.3f, framePaint)

        // Inner subtle corner accents
        val innerMargin = frameMargin * 1.25f
        val innerPaint = Paint().apply {
            isAntiAlias = true
            color = theme.accentColor
            style = Paint.Style.STROKE
            strokeWidth = width * 0.0025f
            alpha = 120
        }
        val innerRect = RectF(
            innerMargin,
            innerMargin,
            width - innerMargin,
            height - innerMargin
        )
        canvas.drawRoundRect(innerRect, innerMargin * 0.2f, innerMargin * 0.2f, innerPaint)

        // 3. Content layout margins
        val contentWidth = (width - frameMargin * 3).toInt()
        val contentLeft = frameMargin * 1.5f

        // 4. Quotation Mark Symbol
        val quotePaint = TextPaint().apply {
            isAntiAlias = true
            color = theme.accentColor
            textSize = width * 0.14f
            typeface = Typeface.create(Typeface.SERIF, Typeface.BOLD)
            alpha = 160
        }
        canvas.drawText("“", contentLeft, height * 0.18f, quotePaint)

        // 5. Verse Text Layout
        val verseTextSize = when (aspectRatio) {
            VerseAspectRatio.SQUARE -> if (verseText.length > 150) width * 0.042f else width * 0.052f
            VerseAspectRatio.STORY -> if (verseText.length > 150) width * 0.046f else width * 0.056f
            VerseAspectRatio.LANDSCAPE -> if (verseText.length > 150) width * 0.034f else width * 0.042f
        }

        val textPaint = TextPaint().apply {
            isAntiAlias = true
            color = theme.textColor
            textSize = verseTextSize
            typeface = Typeface.create(Typeface.SERIF, Typeface.ITALIC)
        }

        val staticLayout = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            StaticLayout.Builder.obtain(verseText, 0, verseText.length, textPaint, contentWidth)
                .setAlignment(Layout.Alignment.ALIGN_NORMAL)
                .setLineSpacing(0f, 1.3f)
                .setIncludePad(true)
                .build()
        } else {
            @Suppress("DEPRECATION")
            StaticLayout(
                verseText, textPaint, contentWidth,
                Layout.Alignment.ALIGN_NORMAL, 1.3f, 0f, true
            )
        }

        val verseStartY = when (aspectRatio) {
            VerseAspectRatio.SQUARE -> (height - staticLayout.height) * 0.42f
            VerseAspectRatio.STORY -> (height - staticLayout.height) * 0.40f
            VerseAspectRatio.LANDSCAPE -> (height - staticLayout.height) * 0.38f
        }

        canvas.save()
        canvas.translate(contentLeft, verseStartY)
        staticLayout.draw(canvas)
        canvas.restore()

        // 6. Scripture Reference Text (e.g. PHILIPPIANS 4:13 (NKJV))
        val refStartY = verseStartY + staticLayout.height + (height * 0.04f)
        val refPaint = TextPaint().apply {
            isAntiAlias = true
            color = theme.accentColor
            textSize = width * 0.038f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        }
        val fullRefText = "— $reference ($version)".uppercase()
        canvas.drawText(fullRefText, contentLeft, refStartY, refPaint)

        // 7. Golden Divider Line
        val dividerY = refStartY + (height * 0.025f)
        val dividerPaint = Paint().apply {
            isAntiAlias = true
            color = theme.accentColor
            strokeWidth = width * 0.003f
            alpha = 150
        }
        canvas.drawLine(contentLeft, dividerY, contentLeft + (width * 0.25f), dividerY, dividerPaint)

        // 8. Footer Watermark Branding
        val footerPaint = TextPaint().apply {
            isAntiAlias = true
            color = theme.textColor
            textSize = width * 0.028f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
            alpha = 200
        }
        val footerY = height - (frameMargin * 1.3f)
        canvas.drawText("CHRIST ENVOY MINISTRY • CEMA DISCIPLESHIP", contentLeft, footerY, footerPaint)

        return bitmap
    }

    fun shareVerseImage(
        context: Context,
        bitmap: Bitmap,
        verseText: String,
        reference: String,
        version: String
    ) {
        try {
            val imagesDir = File(context.cacheDir, "images")
            if (!imagesDir.exists()) imagesDir.mkdirs()

            val imageFile = File(imagesDir, "verse_${System.currentTimeMillis()}.png")
            val outputStream = FileOutputStream(imageFile)
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
            outputStream.flush()
            outputStream.close()

            val contentUri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                imageFile
            )

            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "image/png"
                putExtra(Intent.EXTRA_STREAM, contentUri)
                putExtra(
                    Intent.EXTRA_TEXT,
                    "“$verseText”\n— $reference ($version)\n\nShared via Christ Envoy Ministry App (CEMA)"
                )
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }

            context.startActivity(Intent.createChooser(shareIntent, "Share Verse Graphic"))
        } catch (e: Exception) {
            e.printStackTrace()
            // Fallback to text sharing if image saving fails
            shareVerseText(context, verseText, reference, version)
        }
    }

    fun shareVerseText(
        context: Context,
        verseText: String,
        reference: String,
        version: String
    ) {
        val formattedText = "“$verseText”\n\n— $reference ($version)\n\nShared via CEMA Discipleship App"
        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, formattedText)
            type = "text/plain"
        }
        context.startActivity(Intent.createChooser(sendIntent, "Share Verse Snippet"))
    }
}
