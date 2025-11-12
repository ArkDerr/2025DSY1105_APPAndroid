package cl.daeriquelme.appduoc_profe.data.media

import android.content.Context
import android.net.Uri

fun readBytes(context: Context, uri: Uri): ByteArray? =
    context.contentResolver.openInputStream(uri)?.use { it.readBytes() }
