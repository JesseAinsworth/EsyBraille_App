package com.example.esybrailleapp.utils

import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.Typeface
import android.text.StaticLayout
import android.text.TextPaint
import androidx.core.content.FileProvider
import com.example.esybrailleapp.R
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

fun shareBrailleAsPdf(
    context: Context,
    spanishText: String,
    brailleText: String
) {
    val reversedBrailleText = brailleText.reversed()

    val pdfDocument = android.graphics.pdf.PdfDocument()
    val pageInfo = android.graphics.pdf.PdfDocument.PageInfo.Builder(595, 842, 1).create()
    val page = pdfDocument.startPage(pageInfo)
    val canvas = page.canvas

    val leftMargin = 40f
    val topMargin = 40f
    var currentYPosition = topMargin
    val contentWidth = pageInfo.pageWidth - (leftMargin * 2).toInt()


    try {
        val logoBitmap = BitmapFactory.decodeResource(context.resources, R.drawable.easybraillenegro)
        val watermarkWidth = pageInfo.pageWidth * 0.7f
        val watermarkHeight = (watermarkWidth / logoBitmap.width) * logoBitmap.height
        val watermarkX = (pageInfo.pageWidth - watermarkWidth) / 2f
        val watermarkY = (pageInfo.pageHeight - watermarkHeight) / 2f


        val watermarkPaint = Paint().apply {
            alpha = 50
            isAntiAlias = true
            isFilterBitmap = true
        }

        canvas.drawBitmap(
            logoBitmap, null,
            Rect(
                watermarkX.toInt(), watermarkY.toInt(),
                (watermarkX + watermarkWidth).toInt(), (watermarkY + watermarkHeight).toInt()
            ),
            watermarkPaint
        )
    } catch (e: Exception) {
        e.printStackTrace()
    }


    val braillePaint = TextPaint().apply {
        color = Color.BLACK
        textSize = 24f
        typeface = Typeface.create(Typeface.MONOSPACE, Typeface.NORMAL)
    }
    canvas.drawText("Plantilla de Traduccion de braille", leftMargin, currentYPosition, braillePaint)
    currentYPosition += 40f

    val braillemencion = TextPaint().apply {
        color = Color.BLACK
        textSize = 20f
        typeface = Typeface.create(Typeface.MONOSPACE, Typeface.NORMAL)
    }
    canvas.drawText("Texto en Braille:", leftMargin, currentYPosition, braillemencion)
    currentYPosition += 20f

    val brailleLayout = StaticLayout.Builder.obtain(
        reversedBrailleText, 0, reversedBrailleText.length, braillePaint, contentWidth
    ).build()


    canvas.save()
    canvas.translate(leftMargin, currentYPosition)
    brailleLayout.draw(canvas)
    canvas.restore()
    currentYPosition += brailleLayout.height + 200f


    val spanishPaint = TextPaint().apply {
        color = Color.DKGRAY
        textSize = 20f
        typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
    }
    canvas.drawText("Texto Original en Español:", leftMargin, currentYPosition, spanishPaint)
    currentYPosition += 20f


    val spanishLayout = StaticLayout.Builder.obtain(
        spanishText, 0, spanishText.length, spanishPaint, contentWidth
    ).build()

    canvas.save()
    canvas.translate(leftMargin, currentYPosition)
    spanishLayout.draw(canvas)
    canvas.restore()

    pdfDocument.finishPage(page)
    val file = File(context.cacheDir, "EasyBraille_Template.pdf")
    try {
        pdfDocument.writeTo(FileOutputStream(file))
    } catch (e: IOException) {
        e.printStackTrace()
        return
    }
    pdfDocument.close()

    val fileUri = FileProvider.getUriForFile(context, "${context.packageName}.provider", file)
    val shareIntent = Intent(Intent.ACTION_SEND).apply {
        type = "application/pdf"
        putExtra(Intent.EXTRA_STREAM, fileUri)
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        putExtra(Intent.EXTRA_SUBJECT, "Plantilla Braille")
    }
    context.startActivity(Intent.createChooser(shareIntent, "Compartir Plantilla Braille"))
}