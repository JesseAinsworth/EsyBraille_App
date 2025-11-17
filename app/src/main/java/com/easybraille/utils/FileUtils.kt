package com.easybraille.utils

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
import android.widget.Toast
import androidx.core.content.FileProvider
import com.easybraille.R
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// --- Funciones de Utilidad para Archivos y PDF ---

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
    val reversedBrailleText = brailleText
        .split('\n')
        .joinToString("\n") { line -> line.reversed() }

    val pdfDocument = PdfDocument()
    val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create() // A4
    val page = pdfDocument.startPage(pageInfo)
    val canvas = page.canvas

    // --- Configuración ---
    val margin = 40f
    val padding = 10f
    val pageWidth = pageInfo.pageWidth.toFloat()
    val pageHeight = pageInfo.pageHeight.toFloat()
    val contentWidth = (pageWidth - (margin * 2) - (padding * 2)).toInt()

    val borderPaint = Paint().apply { setColor(Color.BLUE); strokeWidth = 3f; style = Paint.Style.STROKE }
    val titlePaint = TextPaint().apply { color = Color.BLACK; textSize = 18f; isFakeBoldText = true }
    val sectionTitlePaint = TextPaint().apply { color = Color.BLACK; textSize = 14f; isFakeBoldText = true }
    val braillePaint = TextPaint().apply { color = Color.BLACK; textSize = 20f; typeface = Typeface.MONOSPACE }
    val spanishPaint = TextPaint().apply { color = Color.DKGRAY; textSize = 14f }
    val logoPaint = Paint().apply { isAntiAlias = true; isFilterBitmap = true }

    var currentYPosition = margin

    // --- Encabezado ---
    val logoSize = 60f
    try {
        val rightLogoBitmap = BitmapFactory.decodeResource(context.resources, R.drawable.easybraillenormal_negro)
        val rightLogoX = pageWidth - margin - logoSize
        val rightLogoRect = RectF(rightLogoX, margin, rightLogoX + logoSize, margin + logoSize)
        canvas.drawBitmap(rightLogoBitmap, null, rightLogoRect, logoPaint)
    } catch (e: Exception) { e.printStackTrace() }

    canvas.drawText("Tabla de Traducción Braille", margin, currentYPosition + (logoSize / 2), titlePaint)
    currentYPosition += logoSize + 20f

    // --- Cajas ---
    val brailleSectionTop = currentYPosition
    val spanishSectionTop = pageHeight * 0.7f
    val brailleBoxBottom = spanishSectionTop - 20f
    val spanishBoxBottom = pageHeight - margin

    // --- Sección Braille ---
    currentYPosition = brailleSectionTop + padding
    val instructionText = "Texto en Braille: Perfora los puntos de este texto. Al terminar, gira la hoja para leer el relieve correctamente."
    val instructionLayout = StaticLayout.Builder.obtain(instructionText, 0, instructionText.length, sectionTitlePaint, contentWidth).build()
    canvas.save()
    canvas.translate(margin + padding, currentYPosition)
    instructionLayout.draw(canvas)
    canvas.restore()
    currentYPosition += instructionLayout.height + padding

    val brailleLayout = StaticLayout.Builder.obtain(reversedBrailleText.ifEmpty { "(Vacío)" }, 0, reversedBrailleText.ifEmpty { "(Vacío)" }.length, braillePaint, contentWidth).build()
    canvas.save()
    canvas.translate(margin + padding, currentYPosition)
    brailleLayout.draw(canvas)
    canvas.restore()

    // --- Sección Español ---
    currentYPosition = spanishSectionTop + padding
    val spanishTitleText = "Texto Normal en español:"
    val spanishTitleLayout = StaticLayout.Builder.obtain(spanishTitleText, 0, spanishTitleText.length, sectionTitlePaint, contentWidth).build()
    canvas.save()
    canvas.translate(margin + padding, currentYPosition)
    spanishTitleLayout.draw(canvas)
    canvas.restore()
    currentYPosition += spanishTitleLayout.height + padding

    val spanishLayout = StaticLayout.Builder.obtain(spanishText.ifEmpty { "(Vacío)" }, 0, spanishText.ifEmpty { "(Vacío)" }.length, spanishPaint, contentWidth).build()
    canvas.save()
    canvas.translate(margin + padding, currentYPosition)
    spanishLayout.draw(canvas)
    canvas.restore()

    // --- Bordes ---
    canvas.drawRect(margin, brailleSectionTop, pageWidth - margin, brailleBoxBottom, borderPaint)
    canvas.drawRect(margin, spanishSectionTop, pageWidth - margin, spanishBoxBottom, borderPaint)

    // --- Guardar ---
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

// --- Función de traducción a Braille ---
fun translateToBraille(text: String): String {
    val brailleMap = mapOf(
        'a' to "⠁", 'b' to "⠃", 'c' to "⠉", 'd' to "⠙", 'e' to "⠑", 'f' to "⠋", 'g' to "⠛", 'h' to "⠓", 'i' to "⠊", 'j' to "⠚",
        'k' to "⠅", 'l' to "⠇", 'm' to "⠍", 'n' to "⠝", 'o' to "⠕", 'p' to "⠏", 'q' to "⠟", 'r' to "⠗", 's' to "⠎", 't' to "⠞",
        'u' to "⠥", 'v' to "⠧", 'w' to "⠺", 'x' to "⠭", 'y' to "⠽", 'z' to "⠵",
        ' ' to " ",
        '1' to "⠼⠁", '2' to "⠼⠃", '3' to "⠼⠉", '4' to "⠼⠙", '5' to "⠼⠑", '6' to "⠼⠋", '7' to "⠼⠛", '8' to "⠼⠓", '9' to "⠼⠊", '0' to "⠼⠚",
        '.' to "⠲", ',' to "⠂", ';' to "⠆", ':' to "⠒", '?' to "⠦", '!' to "⠖"
    )
    return text.lowercase().map { brailleMap[it] ?: "" }.joinToString("")
}
