package com.example.esybrailleapp.utils

import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
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

    val leftMargin = 40f
    var currentYPosition = 40f
    val contentWidth = pageInfo.pageWidth - (leftMargin * 2).toInt()

    // Dibuja el Logo como Marca de Agua
    try {
        val logoBitmap = BitmapFactory.decodeResource(context.resources, R.drawable.easybraillenegro)
        val watermarkWidth = pageInfo.pageWidth * 0.7f
        val watermarkHeight = (watermarkWidth / logoBitmap.width) * logoBitmap.height
        val watermarkX = (pageInfo.pageWidth - watermarkWidth) / 2f
        val watermarkY = (pageInfo.pageHeight - watermarkHeight) / 2f
        val watermarkPaint = Paint().apply { alpha = 50 }
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

    // Dibuja la sección de Braille
    val titlePaint = TextPaint().apply {
        color = Color.BLACK
        textSize = 24f
        typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
    }
    canvas.drawText("Plantilla de Traducción Braille", leftMargin, currentYPosition, titlePaint)
    currentYPosition += 60f

    val braillePaint = TextPaint().apply {
        color = Color.BLACK
        textSize = 20f
        typeface = Typeface.create(Typeface.MONOSPACE, Typeface.NORMAL)
    }
    canvas.drawText("Texto en Braille (Modo Espejo):", leftMargin, currentYPosition, braillePaint)
    currentYPosition += 30f

    val brailleLayout = StaticLayout.Builder.obtain(reversedBrailleText, 0, reversedBrailleText.length, braillePaint, contentWidth).build()
    canvas.save()
    canvas.translate(leftMargin, currentYPosition)
    brailleLayout.draw(canvas)
    canvas.restore()
    currentYPosition += brailleLayout.height + 100f

    // Dibuja la sección de Español
    val spanishPaint = TextPaint().apply {
        color = Color.DKGRAY
        textSize = 20f
        typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
    }
    canvas.drawText("Texto Original en Español:", leftMargin, currentYPosition, spanishPaint)
    currentYPosition += 30f

    val spanishLayout = StaticLayout.Builder.obtain(spanishText, 0, spanishText.length, spanishPaint, contentWidth).build()
    canvas.save()
    canvas.translate(leftMargin, currentYPosition)
    spanishLayout.draw(canvas)
    canvas.restore()

    // Guardar el archivo
    pdfDocument.finishPage(page)
    val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
    val fileName = "BrailleTranslation_${timeStamp}.pdf"
    val directory = context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)
    val file = File(directory, fileName)

    try {
        pdfDocument.writeTo(FileOutputStream(file))
        pdfDocument.close()
        return file
    } catch (e: IOException) {
        e.printStackTrace()
        pdfDocument.close()
        return null
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