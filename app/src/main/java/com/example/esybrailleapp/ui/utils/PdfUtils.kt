package com.example.esybrailleapp.utils

import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
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

// Obtiene la lista de PDFs guardados (Sin cambios)
fun getSavedPdfs(context: Context): List<File> {
    val directory = context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)
    return directory?.listFiles { file -> file.isFile && file.extension == "pdf" }
        ?.sortedByDescending { it.lastModified() } ?: emptyList()
}

// Abre un archivo PDF guardado (Sin cambios)
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

    val reversedBrailleText = brailleText
        .split('\n')
        .joinToString("\n") { line -> line.reversed()
        }

    val pdfDocument = android.graphics.pdf.PdfDocument()
    val pageInfo = android.graphics.pdf.PdfDocument.PageInfo.Builder(595, 842, 1).create() // A4
    val page = pdfDocument.startPage(pageInfo)
    val canvas = page.canvas

    // --- Configuración de Márgenes y Pinturas ---
    val margin = 40f
    val padding = 10f
    val pageWidth = pageInfo.pageWidth.toFloat()
    val pageHeight = pageInfo.pageHeight.toFloat()
    val contentWidth = (pageWidth - (margin * 2) - (padding * 2)).toInt()

    val borderPaint = Paint().apply { color = Color.BLUE; strokeWidth = 3f; style = Paint.Style.STROKE }
    val titlePaint = TextPaint().apply { color = Color.BLACK; textSize = 18f; isFakeBoldText = true }
    val sectionTitlePaint = TextPaint().apply { color = Color.BLACK; textSize = 14f; isFakeBoldText = true }
    val braillePaint = TextPaint().apply { color = Color.BLACK; textSize = 20f; typeface = Typeface.MONOSPACE }
    val spanishPaint = TextPaint().apply { color = Color.DKGRAY; textSize = 14f }

    val logoPaint = Paint().apply {
        isAntiAlias = true
        isFilterBitmap = true
    }

    var currentYPosition = margin

    // --- 1. Dibuja el Encabezado ---
    val logoSize = 60f
    try {
        val rightLogoBitmap = BitmapFactory.decodeResource(context.resources, R.drawable.easybraillenormal_negro)
        val rightLogoX = pageWidth - margin - logoSize
        val rightLogoRect = RectF(rightLogoX, margin, rightLogoX + logoSize, margin + logoSize)
        canvas.drawBitmap(rightLogoBitmap, null, rightLogoRect, logoPaint)
    } catch (e: Exception) { e.printStackTrace() }

    canvas.drawText("Tabla de Traduccion braille ", margin, currentYPosition + (logoSize / 2), titlePaint)
    currentYPosition += logoSize + 20f

    // --- 2. Define las Cajas (Braille más grande) ---
    val brailleSectionTop = currentYPosition
    val spanishSectionTop = pageHeight * 0.7f
    val brailleBoxBottom = spanishSectionTop - 20f
    val brailleBoxHeight = brailleBoxBottom - brailleSectionTop
    val spanishBoxBottom = pageHeight - margin

    // --- 3. Dibuja la Marca de Agua (en la caja de Braille) ---
    try {
        val watermarkBitmap = BitmapFactory.decodeResource(context.resources, R.drawable.easybraillenegro)
        val watermarkSize = 300f
        val watermarkPaint = Paint().apply {
            alpha = 50
            isAntiAlias = true
            isFilterBitmap = true
        }
        val centerX = (pageWidth - watermarkSize) / 2
        val centerY = brailleSectionTop + ((brailleBoxHeight - watermarkSize) / 2)
        val watermarkRect = RectF(centerX, centerY, centerX + watermarkSize, centerY + watermarkSize)
        canvas.drawBitmap(watermarkBitmap, null, watermarkRect, watermarkPaint)
    } catch (e: Exception) {
        e.printStackTrace()
    }

    // --- 4. Dibuja la Sección de Braille (Encima de la marca de agua) ---
    currentYPosition = brailleSectionTop + padding
    // 👇 Texto de las instrucciones actualizado
    val instructionText = "Texto en Braille: Perfora los puntos de este texto. Al terminar, gira la hoja para leer el relieve correctamente."
    val instructionLayout = StaticLayout.Builder.obtain(
        instructionText, 0, instructionText.length, sectionTitlePaint, contentWidth
    ).build()
    canvas.save()
    canvas.translate(margin + padding, currentYPosition)
    instructionLayout.draw(canvas)
    canvas.restore()
    currentYPosition += instructionLayout.height + padding

    val brailleLayout = StaticLayout.Builder.obtain(
        reversedBrailleText.ifEmpty { "(Vacío)" }, 0, reversedBrailleText.ifEmpty { "(Vacío)" }.length, braillePaint, contentWidth
    ).build()
    canvas.save()
    canvas.translate(margin + padding, currentYPosition)
    brailleLayout.draw(canvas)
    canvas.restore()

    // --- 5. Dibuja la Sección de Español ---
    currentYPosition = spanishSectionTop + padding
    val spanishTitleText = "Texto Normal en español:"
    val spanishTitleLayout = StaticLayout.Builder.obtain(
        spanishTitleText, 0, spanishTitleText.length, sectionTitlePaint, contentWidth
    ).build()
    canvas.save()
    canvas.translate(margin + padding, currentYPosition)
    spanishTitleLayout.draw(canvas)
    canvas.restore()
    currentYPosition += spanishTitleLayout.height + padding

    val spanishLayout = StaticLayout.Builder.obtain(
        spanishText.ifEmpty { "(Vacío)" }, 0, spanishText.ifEmpty { "(Vacío)" }.length, spanishPaint, contentWidth
    ).build()
    canvas.save()
    canvas.translate(margin + padding, currentYPosition)
    spanishLayout.draw(canvas)
    canvas.restore()

    // --- 6. Dibuja los Bordes Azules ---
    canvas.drawRect(margin, brailleSectionTop, pageWidth - margin, brailleBoxBottom, borderPaint)
    canvas.drawRect(margin, spanishSectionTop, pageWidth - margin, spanishBoxBottom, borderPaint)

    // --- Guardar (Sin cambios) ---
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