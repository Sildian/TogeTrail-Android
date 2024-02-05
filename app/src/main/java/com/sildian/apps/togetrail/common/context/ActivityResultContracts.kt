package com.sildian.apps.togetrail.common.context

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import androidx.activity.result.contract.ActivityResultContract
import com.sildian.apps.togetrail.common.files.TogeTrailFileProvider

class GetPictureContract : ActivityResultContract<Unit, Uri?>() {

    override fun createIntent(context: Context, input: Unit): Intent =
        Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)

    override fun parseResult(resultCode: Int, intent: Intent?): Uri? =
        intent.takeIf { resultCode == Activity.RESULT_OK }?.data
}

class TakePictureContract : ActivityResultContract<Unit, Uri?>() {

    private var uri: Uri? = null

    override fun createIntent(context: Context, input: Unit): Intent {
        uri = TogeTrailFileProvider().createUriForCacheImageFile(context = context)
        return Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            .putExtra(MediaStore.EXTRA_OUTPUT, uri)
    }

    override fun parseResult(resultCode: Int, intent: Intent?): Uri? =
        intent.takeIf { resultCode == Activity.RESULT_OK }
            ?.let { uri }
            ?.also { uri = null }
}