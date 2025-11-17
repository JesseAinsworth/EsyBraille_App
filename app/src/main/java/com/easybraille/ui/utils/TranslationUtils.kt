package com.example.esybrailleapp.utils

import java.util.Locale
import kotlin.text.iterator


private val brailleMap: Map<Char, String> = mapOf(
    'a' to "⠁", 'b' to "⠃", 'c' to "⠉", 'd' to "⠙", 'e' to "⠑", 'f' to "⠋", 'g' to "⠛", 'h' to "⠓",
    'i' to "⠊", 'j' to "⠚", 'k' to "⠅", 'l' to "⠇", 'm' to "⠍", 'n' to "⠝", 'o' to "⠕", 'p' to "⠏",
    'q' to "⠟", 'r' to "⠗", 's' to "⠎", 't' to "⠞", 'u' to "⠥", 'v' to "⠧", 'w' to "⠺", 'x' to "⠭",
    'y' to "⠽", 'z' to "⠵", 'ñ' to "⠟",'á' to "⠷", 'é' to "⠮", 'í' to "⠌", 'ó' to "⠬", 'ú' to "⠾",
    'ü' to "⠳",


    '1' to "⠼⠁", '2' to "⠼⠃", '3' to "⠼⠉", '4' to "⠼⠙", '5' to "⠼⠑", '6' to "⠼⠋",
    '7' to "⠼⠛", '8' to "⠼⠓", '9' to "⠼⠊", '0' to "⠼⠚",

    '.' to "⠲", ',' to "⠂", ';' to "⠆", ':' to "⠒", '!' to "⠖", '(' to "⠶", ')' to "⠶", ' ' to " ",

    '+' to "⠖",
    '-' to "⠤",
    '*' to "⠦",
    '/' to "⠌"
)


fun translateToBraille(text: String): String {
    val lowercasedText = text.lowercase(Locale.ROOT)
    val stringBuilder = StringBuilder()
    for (char in lowercasedText) {
        stringBuilder.append(brailleMap[char] ?: char)
    }
    return stringBuilder.toString()
}