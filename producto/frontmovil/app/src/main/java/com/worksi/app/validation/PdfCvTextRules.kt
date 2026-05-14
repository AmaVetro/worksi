package com.worksi.app.validation

import com.tom_roush.pdfbox.pdmodel.PDDocument
import com.tom_roush.pdfbox.text.PDFTextStripper
import java.io.ByteArrayInputStream
import java.text.Normalizer

object PdfCvTextRules {
    private const val MIN_USEFUL_CHARS = 24

    fun normalizeExtracted(raw: String): String {
        var s = Normalizer.normalize(raw, Normalizer.Form.NFKC)
        s = s.replace('\r', '\n')
        s = s.replace('\u00a0', ' ')
        s = s.replace("[\\t\\f\\v]+".toRegex(), " ")
        s = s.replace(" +\n".toRegex(), "\n")
        s = s.replace("\n{3,}".toRegex(), "\n\n")
        s = s.replace(" {2,}".toRegex(), " ")
        s = s.replace("\\n ".toRegex(), "\n")
        return s.trim()
    }

    private fun countLettersAndDigits(s: String): Int {
        var n = 0
        for (c in s) {
            if (Character.isLetterOrDigit(c)) n++
        }
        return n
    }

    private fun stripTextFromPdf(bytes: ByteArray): String {
        ByteArrayInputStream(bytes).use { input ->
            PDDocument.load(input).use { doc ->
                if (doc.isEncrypted) {
                    try {
                        doc.setAllSecurityToBeRemoved(true)
                    } catch (_: Exception) {
                    }
                }
                val stripper = PDFTextStripper()
                return stripper.getText(doc)
            }
        }
    }

    fun validatePdfBytes(bytes: ByteArray): String? {
        val raw =
            try {
                stripTextFromPdf(bytes)
            } catch (_: Exception) {
                return "No se pudo leer el texto del PDF. Debe ser un PDF con texto seleccionable (no escaneo solo imagen; sin OCR en esta versión)."
            }
        val normalized = normalizeExtracted(raw)
        return if (countLettersAndDigits(normalized) < MIN_USEFUL_CHARS) {
            "Este PDF no tiene suficiente texto seleccionable. Sube un PDF con texto real (por ejemplo exportado desde Word), no un escaneo o fotocopia solo imagen."
        } else {
            null
        }
    }
}
