package ru.dmitry.callblocker.core.formatters.mask

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import kotlin.math.absoluteValue

class TextInputMask(
    private val mask: String,
    private val prefilterCallBack: (String) -> Boolean = { true }
) : VisualTransformation, Prefilter {

    private val specialSymbolsIndices = mask.indices.filter { mask[it] != REPLACED_CHAR }

    override fun isAcceptable(text: String): Boolean {
        return prefilterCallBack.invoke(text)
    }

    override fun prefilter(value: TextFieldValue): TextFieldValue = value

    override fun filter(text: AnnotatedString): TransformedText {
        var out = ""
        var maskIndex = 0
        text.forEach { char ->
            while (specialSymbolsIndices.contains(maskIndex)) {
                out += mask[maskIndex]
                maskIndex++
            }
            out += char
            maskIndex++
        }
        return TransformedText(AnnotatedString(out), offsetTranslator())
    }

    private fun offsetTranslator() = object : OffsetMapping {
        override fun originalToTransformed(offset: Int): Int {
            val offsetValue = offset.absoluteValue
            if (offsetValue == 0) return 0
            var numberOfHashtags = 0
            val masked = mask.takeWhile {
                if (it == REPLACED_CHAR) numberOfHashtags++
                numberOfHashtags < offsetValue
            }
            return masked.length + 1
        }

        override fun transformedToOriginal(offset: Int): Int {
            return mask.take(offset.absoluteValue).count { it == REPLACED_CHAR }
        }
    }

    companion object {
        const val REPLACED_CHAR = '#'
    }
}