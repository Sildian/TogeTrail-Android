package com.sildian.apps.togetrail.common.ui.imagePicker

import android.content.ActivityNotFoundException
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.sildian.apps.togetrail.R
import com.sildian.apps.togetrail.common.context.GetPictureContract
import com.sildian.apps.togetrail.common.context.Permission
import com.sildian.apps.togetrail.common.context.PermissionRequestLauncher
import com.sildian.apps.togetrail.common.context.TakePictureContract
import com.sildian.apps.togetrail.common.context.registerForMultiplePermissionRequest
import com.sildian.apps.togetrail.common.context.registerForSinglePermissionRequest
import com.sildian.apps.togetrail.common.context.showInfoDialog
import com.sildian.apps.togetrail.common.context.showSnackbar
import com.sildian.apps.togetrail.databinding.DialogFragmentImagePickerBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ImagePickerBottomSheetDialogFragment : BottomSheetDialogFragment() {

    private val imagePickerViewModel: ImagePickerViewModel by viewModels()

    private var binding: DialogFragmentImagePickerBinding? = null

    private lateinit var getPicturePermissionRequestLauncher: PermissionRequestLauncher
    private lateinit var takePicturePermissionRequestLauncher: PermissionRequestLauncher
    
    private lateinit var getPictureActivityResultLauncher: ActivityResultLauncher<Unit>
    private lateinit var takePictureActivityResultLauncher: ActivityResultLauncher<Unit>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        registerGetPicturePermissionRequestLauncher()
        registerTakePicturePermissionRequestLauncher()
        registerGetPictureActivityResultLauncher()
        registerTakePictureActivityResultLauncher()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View =
        DataBindingUtil.inflate<DialogFragmentImagePickerBinding>(
            inflater,
            R.layout.dialog_fragment_image_picker,
            container,
            false,
        ).apply {
            lifecycleOwner = this@ImagePickerBottomSheetDialogFragment
            imagePickerViewModel = this@ImagePickerBottomSheetDialogFragment.imagePickerViewModel
            binding = this
        }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        collectEvents()
    }

    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }

    private fun registerGetPicturePermissionRequestLauncher() {
        getPicturePermissionRequestLauncher = registerForSinglePermissionRequest(
            permission = Permission.WriteExternalStorage,
            callback = object: PermissionRequestLauncher.Callback {
                override fun onGranted() {
                    try {
                        getPictureActivityResultLauncher.launch(Unit)
                    } catch (e: ActivityNotFoundException) {
                        requireContext().showSnackbar(
                            view = requireView(),
                            message = getString(R.string.message_device_gallery_unavailable)
                        )
                    }
                }
                override fun onDenied() {
                    requireContext().showInfoDialog(
                        title = getString(R.string.message_permission_denied_title),
                        message = getString(R.string.message_permission_denied_message_write),
                        onNeutralButtonClick = { },
                    )
                }
                override fun onShowRationale() {
                    requireContext().showInfoDialog(
                        title = getString(R.string.message_permission_requested_title),
                        message = getString(R.string.message_permission_requested_message_write),
                        onNeutralButtonClick = { },
                    )
                }
            }
        )
    }

    private fun registerTakePicturePermissionRequestLauncher() {
        takePicturePermissionRequestLauncher = registerForMultiplePermissionRequest(
            permissions = arrayOf(Permission.WriteExternalStorage, Permission.Camera),
            callback = object: PermissionRequestLauncher.Callback {
                override fun onGranted() {
                    try {
                        takePictureActivityResultLauncher.launch(Unit)
                    } catch (e: ActivityNotFoundException) {
                        requireContext().showSnackbar(
                            view = requireView(),
                            message = getString(R.string.message_device_camera_unavailable)
                        )
                    }
                }
                override fun onDenied() {
                    requireContext().showInfoDialog(
                        title = getString(R.string.message_permission_denied_title),
                        message = getString(R.string.message_permission_denied_message_write_and_camera),
                        onNeutralButtonClick = { },
                    )
                }
                override fun onShowRationale() {
                    requireContext().showInfoDialog(
                        title = getString(R.string.message_permission_requested_title),
                        message = getString(R.string.message_permission_requested_message_write_and_camera),
                        onNeutralButtonClick = { },
                    )
                }
            }
        )
    }

    private fun registerGetPictureActivityResultLauncher() {
        getPictureActivityResultLauncher = registerForActivityResult(GetPictureContract()) { uri ->
            if (uri != null) {
                onPictureSelected(uri = uri)
            }
        }
    }

    private fun registerTakePictureActivityResultLauncher() {
        takePictureActivityResultLauncher = registerForActivityResult(TakePictureContract()) { uri ->
            if (uri != null) {
                onPictureSelected(uri = uri)
            }
        }
    }

    private fun collectEvents() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(state = Lifecycle.State.STARTED) {
                imagePickerViewModel.event.collect(::onEvent)
            }
        }
    }
    
    private fun onEvent(event: ImagePickerEvent) {
        when (event) {
            is ImagePickerEvent.GetPicture ->
                getPicturePermissionRequestLauncher.launch()
            is ImagePickerEvent.TakePicture ->
                takePicturePermissionRequestLauncher.launch()
        }
    }

    private fun onPictureSelected(uri: Uri) {
        setFragmentResult(
            requestKey = KEY_REQUEST_PICTURE,
            result = bundleOf(KEY_BUNDLE_PICTURE_URI to uri.path)
        )
        dismiss()
    }

    companion object {

        private const val TAG = "ImagePickerBottomSheetDialogFragment"
        private const val KEY_REQUEST_PICTURE = "KEY_REQUEST_PICTURE"
        private const val KEY_BUNDLE_PICTURE_URI = "KEY_BUNDLE_PICTURE_URI"

        fun show(fragmentManager: FragmentManager, onPictureSelected: (uri: String) -> Unit) {
            if (fragmentManager.findFragmentByTag(TAG) == null) {
                ImagePickerBottomSheetDialogFragment().apply {
                    setFragmentResultListener(requestKey = KEY_REQUEST_PICTURE) { _, bundle ->
                        onPictureSelected(requireNotNull(bundle.getString(KEY_BUNDLE_PICTURE_URI)))
                    }
                }.show(fragmentManager, TAG)
            }
        }
    }
}