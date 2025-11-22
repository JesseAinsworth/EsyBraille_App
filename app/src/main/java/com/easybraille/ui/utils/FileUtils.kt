package com.easybraille.ui.utils

import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.os.Environment
import android.text.StaticLayout
import android.text.TextPaint
import android.text.TextUtils
import android.widget.Toast
import androidx.core.content.FileProvider
import com.easybraille.R
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.max

fun getSavedPdfs(context: Context): List<File> {
    val userId = AuthManager.getUserId(context)
    if (userId.isEmpty()) return emptyList()

    val directory = File(
        context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS),
        userId
    )

    return directory.listFiles { file -> file.isFile && file.extension == "pdf" }
        ?.sortedByDescending { it.lastModified() } ?: emptyList()
}

fun openPdf(context: Context, file: File) {
    val uri = FileProvider.getUriForFile(context, "${context.packageName}.provider", file)
    val intent = Intent(Intent.ACTION_VIEW).apply {
        setDataAndType(uri, "application/pdf")
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }
    try {
        context.startActivity(intent)
    } catch (e: Exception) {
        Toast.makeText(context, "No se encontró una aplicación para abrir PDFs", Toast.LENGTH_SHORT).show()
    }
}

fun saveBraillePdf(
    context: Context,
    spanishText: String,
    brailleText: String
): File? {

    val cleanSpanishText = spanishText
        .replace('\n', ' ')
        .replace("\\s+".toRegex(), " ")
        .trim()
        .ifEmpty { "(Vacío)" }

    val cleanBrailleText = brailleText
        .replace('\n', ' ')
        .replace("\\s+".toRegex(), " ")
        .trim()

    val reversedBrailleText = cleanBrailleText.reversed().ifEmpty { "(Vacío)" }

    val pdfDocument = PdfDocument()
    val pageWidth = 595
    val pageHeight = 842
    val margin = 40f
    val padding = 10f
    val contentWidth = (pageWidth - (margin * 2) - (padding * 2)).toInt()


    val borderPaint = Paint().apply { color = Color.BLUE; strokeWidth = 3f; style = Paint.Style.STROKE }
    val titlePaint = TextPaint().apply { color = Color.BLACK; textSize = 18f; isFakeBoldText = true }
    val sectionTitlePaint = TextPaint().apply { color = Color.BLACK; textSize = 14f; isFakeBoldText = true }
    val braillePaint = TextPaint().apply {
        color = Color.BLACK
        textSize = 20f
        typeface = Typeface.MONOSPACE
    }
    val spanishPaint = TextPaint().apply { color = Color.DKGRAY; textSize = 12f }
    val logoPaint = Paint().apply { isAntiAlias = true; isFilterBitmap = true }
    val watermarkPaint = Paint().apply { alpha = 50; isAntiAlias = true; isFilterBitmap = true }


    var currentBrailleIndex = 0
    var currentSpanishIndex = 0
    var pageNumber = 1

    while (currentBrailleIndex < reversedBrailleText.length || currentSpanishIndex < cleanSpanishText.length) {

        val pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNumber).create()
        val page = pdfDocument.startPage(pageInfo)
        val canvas = page.canvas
        var currentYPosition = margin



        // 1. Logo Superior
        val logoSize = 60f
        try {
            val rightLogoBitmap = BitmapFactory.decodeResource(context.resources, R.drawable.easybraillenormal_negro)
            val rightLogoX = pageWidth - margin - logoSize
            val rightLogoRect = RectF(rightLogoX, margin, rightLogoX + logoSize, margin + logoSize)
            canvas.drawBitmap(rightLogoBitmap, null, rightLogoRect, logoPaint)
        } catch (e: Exception) { e.printStackTrace() }


        canvas.drawText("Tabla de Traducción en Braille", margin, currentYPosition + (logoSize / 2), titlePaint)
        currentYPosition += logoSize + 20f


        val brailleSectionTop = currentYPosition
        val spanishSectionTop = (pageHeight * 0.7f)
        val brailleBoxBottom = spanishSectionTop - 20f
        val brailleBoxHeight = brailleBoxBottom - brailleSectionTop
        val spanishBoxBottom = pageHeight - margin

        try {
            val watermarkBitmap = BitmapFactory.decodeResource(context.resources, R.drawable.easybraillenegro)
            val watermarkSize = 300f
            val centerX = (pageWidth - watermarkSize) / 2f
            val centerY = brailleSectionTop + ((brailleBoxHeight - watermarkSize) / 2)
            val watermarkRect = RectF(centerX, centerY, centerX + watermarkSize, centerY + watermarkSize)
            canvas.drawBitmap(watermarkBitmap, null, watermarkRect, watermarkPaint)
        } catch (e: Exception) { e.printStackTrace() }

        currentYPosition = brailleSectionTop + padding
        val instructionText = "Instrucción: Perfora los puntos. Al terminar, gira la hoja para leer en Braille."
        val instructionLayout = StaticLayout.Builder.obtain(instructionText, 0, instructionText.length, sectionTitlePaint, contentWidth).build()
        canvas.save()
        canvas.translate(margin + padding, currentYPosition)
        instructionLayout.draw(canvas)
        canvas.restore()
        currentYPosition += instructionLayout.height + padding

        val availableBrailleHeight = (brailleBoxBottom - currentYPosition - padding).coerceAtLeast(0f)

        val remainingBraille = if (currentBrailleIndex < reversedBrailleText.length) {
            reversedBrailleText.substring(currentBrailleIndex)
        } else ""

        if (remainingBraille.isNotEmpty()) {
            val brailleLayoutMeasure = StaticLayout.Builder.obtain(remainingBraille, 0, remainingBraille.length, braillePaint, contentWidth).build()

            val fm = braillePaint.fontMetrics
            val lineHeight = fm.bottom - fm.top
            val linesPerPage = (availableBrailleHeight / lineHeight).toInt().coerceAtLeast(1)

            var charsToDraw = remainingBraille.length
            if (brailleLayoutMeasure.lineCount > linesPerPage) {
                charsToDraw = brailleLayoutMeasure.getLineEnd(linesPerPage - 1)
            }

            val chunkToDraw = remainingBraille.substring(0, charsToDraw)
            val finalLayout = StaticLayout.Builder.obtain(chunkToDraw, 0, chunkToDraw.length, braillePaint, contentWidth).build()

            canvas.save()
            canvas.translate(margin + padding, currentYPosition)
            finalLayout.draw(canvas)
            canvas.restore()

            currentBrailleIndex += charsToDraw
        }

        currentYPosition = spanishSectionTop + padding

        val titleText = if (pageNumber == 1) "Texto original:" else "Texto original (Continuación...)"
        val titleLayout = StaticLayout.Builder.obtain(titleText, 0, titleText.length, sectionTitlePaint, contentWidth).build()
        canvas.save()
        canvas.translate(margin + padding, currentYPosition)
        titleLayout.draw(canvas)
        canvas.restore()
        currentYPosition += titleLayout.height + padding

        val availableSpanishHeight = (spanishBoxBottom - currentYPosition - padding).coerceAtLeast(0f)

        // Tomamos lo que resta del texto Español
        val remainingSpanish = if (currentSpanishIndex < cleanSpanishText.length) {
            cleanSpanishText.substring(currentSpanishIndex)
        } else ""

        if (remainingSpanish.isNotEmpty()) {
            val spanishLayoutMeasure = StaticLayout.Builder.obtain(remainingSpanish, 0, remainingSpanish.length, spanishPaint, contentWidth).build()

            val fm = spanishPaint.fontMetrics
            val lineHeight = fm.bottom - fm.top
            val linesPerPage = (availableSpanishHeight / lineHeight).toInt().coerceAtLeast(1)

            var charsToDraw = remainingSpanish.length
            if (spanishLayoutMeasure.lineCount > linesPerPage) {
                charsToDraw = spanishLayoutMeasure.getLineEnd(linesPerPage - 1)
            }

            val chunkToDraw = remainingSpanish.substring(0, charsToDraw)
            val finalLayout = StaticLayout.Builder.obtain(chunkToDraw, 0, chunkToDraw.length, spanishPaint, contentWidth).build()

            canvas.save()
            canvas.translate(margin + padding, currentYPosition)
            finalLayout.draw(canvas)
            canvas.restore()

            currentSpanishIndex += charsToDraw
        }

        canvas.drawRect(margin, brailleSectionTop, pageWidth - margin, brailleBoxBottom, borderPaint)
        canvas.drawRect(margin, spanishSectionTop, pageWidth - margin, spanishBoxBottom, borderPaint)

        // Número de página
        val pageNumText = "Pág. $pageNumber"
        canvas.drawText(pageNumText, pageWidth - margin - 50, pageHeight - margin + 10, spanishPaint)

        pdfDocument.finishPage(page)
        pageNumber++
    }

    // --- GUARDAR ARCHIVO ---
    val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
    val fileName = "BrailleTemplate_${timeStamp}.pdf"
    val userId = AuthManager.getUserId(context)
    val directory = File(context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), userId)
    if (!directory.exists()) directory.mkdirs()
    val file = File(directory, fileName)

    return try {
        pdfDocument.writeTo(FileOutputStream(file))
        pdfDocument.close()
        file
    } catch (e: IOException) {
        e.printStackTrace()
        pdfDocument.close()
        null
    }
}

fun deletePdf(file: File): Boolean {
    return try {
        file.delete()
    } catch (e: Exception) {
        e.printStackTrace()
        false
    }
}