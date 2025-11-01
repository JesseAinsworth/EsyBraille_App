package com.example.esybrailleapp.utils

import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.os.Environment
import android.text.StaticLayout
import android.text.TextPaint
import android.widget.Toast
import androidx.core.content.FileProvider
import com.example.esybrailleapp.R
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun getSavedPdfs(context: Context): List<File> {
    val directory = context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)
    return directory?.listFiles { file -> file.isFile && file.extension == "pdf" }
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
    val reversedBrailleText = brailleText.reversed()

    val pdfDocument = android.graphics.pdf.PdfDocument()
    val pageInfo = android.graphics.pdf.PdfDocument.PageInfo.Builder(595, 842, 1).create()
    val page = pdfDocument.startPage(pageInfo)
    val canvas = page.canvas

    val margin = 40f
    val pageWidth = pageInfo.pageWidth.toFloat()
    val pageHeight = pageInfo.pageHeight.toFloat()
    val contentWidth = pageWidth - (margin * 2)

    val borderPaint = Paint().apply {
        color = Color.BLUE
        strokeWidth = 3f
        style = Paint.Style.STROKE
    }
    val titlePaint = TextPaint().apply {
        color = Color.BLACK
        textSize = 18f
        isFakeBoldText = true
    }
    val sectionTitlePaint = TextPaint().apply {
        color = Color.BLACK
        textSize = 14f
        isFakeBoldText = true
    }
    val braillePaint = TextPaint().apply {
        color = Color.BLACK
        textSize = 20f
        typeface = Typeface.MONOSPACE
    }
    val spanishPaint = TextPaint().apply {
        color = Color.DKGRAY
        textSize = 14f
    }

    val logoSize = 60f



    canvas.drawText(
        "Formato de plantilla de traducción de braille  ",
        margin + logoSize + 15f,
        margin + (logoSize / 2) + 5f,
        titlePaint
    )


    val topBoxY = margin + logoSize + 30f
    val brailleBoxHeight = 420f
    val spanishBoxHeight = 180f
    val spacingBetweenBoxes = 30f

    val brailleBoxTop = topBoxY
    val brailleBoxBottom = brailleBoxTop + brailleBoxHeight
    val spanishBoxTop = brailleBoxBottom + spacingBetweenBoxes
    val spanishBoxBottom = spanishBoxTop + spanishBoxHeight


    canvas.drawRect(margin, brailleBoxTop, pageWidth - margin, brailleBoxBottom, borderPaint)


    try {
        val watermarkBitmap = BitmapFactory.decodeResource(context.resources, R.drawable.easybraillenegro)
        val watermarkScaled = android.graphics.Bitmap.createScaledBitmap(watermarkBitmap, 250, 250, false)

        val paintWatermark = Paint().apply {
            alpha = 60
        }

        val centerX = (pageWidth - watermarkScaled.width) / 2
        val centerY = brailleBoxTop + ((brailleBoxHeight - watermarkScaled.height) / 2)
        canvas.drawBitmap(watermarkScaled, centerX, centerY, paintWatermark)
    } catch (e: Exception) {
        e.printStackTrace()
    }


    canvas.drawText(
        "Texto en Braille, Perfora los puntos de este texto. Al terminar, gira la hoja para leer el relieve en Braille.:",
        margin + 10f,
        brailleBoxTop + 25f,
        sectionTitlePaint
    )


    val brailleLayout = StaticLayout.Builder.obtain(
        reversedBrailleText, 0, reversedBrailleText.length, braillePaint, (contentWidth - 20).toInt()
    ).build()
    canvas.save()
    canvas.translate(margin + 10f, brailleBoxTop + 50f)
    brailleLayout.draw(canvas)
    canvas.restore()


    canvas.drawRect(margin, spanishBoxTop, pageWidth - margin, spanishBoxBottom, borderPaint)
    canvas.drawText(
        "Texto normal en español:",
        margin + 10f,
        spanishBoxTop + 25f,
        sectionTitlePaint
    )


    val spanishLayout = StaticLayout.Builder.obtain(
        spanishText, 0, spanishText.length, spanishPaint, (contentWidth - 20).toInt()
    ).build()
    canvas.save()
    canvas.translate(margin + 10f, spanishBoxTop + 50f)
    spanishLayout.draw(canvas)
    canvas.restore()


    pdfDocument.finishPage(page)
    val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
    val fileName = "BrailleTemplate_${timeStamp}.pdf"
    val directory = context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)
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