package com.sildian.apps.togetrail.common.files

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import com.sildian.apps.togetrail.R
import java.io.File

class TogeTrailFileProvider : FileProvider(R.xml.files) {

    fun createUriForCacheImageFile(context: Context): Uri {
        val path = File(context.cacheDir, FILES_DIRECTORY_IMAGES).apply {
            if (exists().not()) mkdirs()
        }
        val file = File(path, "$FILES_PREFIX${System.currentTimeMillis()}$FILES_SUFFIX_JPG")
        return getUriForFile(context, AUTHORITY, file)
    }

    companion object {
        private const val AUTHORITY = "com.sildian.apps.togetrail.fileprovider"
        private const val FILES_DIRECTORY_IMAGES = "images/togetrail"
        private const val FILES_PREFIX = "togetrail_"
        private const val FILES_SUFFIX_JPG = ".jpg"
    }
}
