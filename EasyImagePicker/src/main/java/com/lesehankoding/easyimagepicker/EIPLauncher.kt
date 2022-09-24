package com.lesehankoding.easyimagepicker

import android.content.*
import android.net.*
import android.view.*
import android.widget.*
import androidx.activity.*
import androidx.activity.result.*
import androidx.activity.result.contract.*
import androidx.fragment.app.Fragment
import com.google.android.material.bottomsheet.*


typealias EIPCallback = (Uri) -> Unit

class EIPLauncher(
	private val context: () -> Context,
	private val resultLauncher: ActivityResultLauncher<Intent>
) {
	fun launchDialog(config: EIPConfig = EIPConfig()) {
		showImagePickerOptions(context,config, resultLauncher)
	}

	fun launchGalery(config: EIPConfig = EIPConfig()) {
		val intent = createImagePickerIntent(context(),false,config)
		resultLauncher.launch(intent)
	}

	fun launchCamera(config: EIPConfig = EIPConfig()) {
		val intent = createImagePickerIntent(context(),true, config )
		resultLauncher.launch(intent)
	}

	companion object {
		fun createIntent(
			context: Context,
		): Intent {
			return createImagePickerIntent(context)
		}
	}
}

fun showImagePickerOptions(
	context: () -> Context,
	config: EIPConfig,
	resultLauncher: ActivityResultLauncher<Intent>
) {
	val dialog = BottomSheetDialog(context(), R.style.BottomSheetDialogTheme)
	val view = LayoutInflater.from(context()).inflate(
			R.layout.bottomdlg_easyimagepicker, dialog.findViewById<View>(
			R.id.lnroot
	) as LinearLayout?
	)
	dialog.setContentView(view)
	view.findViewById<TextView>(R.id.txtClose).setOnClickListener {
		dialog.dismiss()
	}
	val cameraSelected = view.findViewById<ImageButton>(R.id.btnCamera)
	val galerySelected = view.findViewById<ImageButton>(R.id.btnImage)
	cameraSelected.setOnClickListener {
		val intent = createImagePickerIntent(context(),true,config)
		resultLauncher.launch(intent)
		dialog.dismiss()
	}
	galerySelected.setOnClickListener {
		val intent = createImagePickerIntent(context(),false,config)
		resultLauncher.launch(intent)
		dialog.dismiss()
	}
	dialog.show()
}
private fun createImagePickerIntent(ctx: Context,isCamera:Boolean = true,config:EIPConfig= EIPConfig()): Intent {
	val intent = Intent(ctx, EIPActivity::class.java)
	intent.putExtra("isCamera", isCamera)
	intent.putExtra(EIPConstans.INTENT_LOCK_ASPECT_RATIO, config.isCropAspectRatio)
	if (config.cropRatio == RATIO.WIDE){
		intent.putExtra(EIPConstans.INTENT_ASPECT_RATIO_X, 16) // 16x9, 1x1, 3:4, 3:2
		intent.putExtra(EIPConstans.INTENT_ASPECT_RATIO_Y, 9)
	}else{
		intent.putExtra(EIPConstans.INTENT_ASPECT_RATIO_X, 1) // 16x9, 1x1, 3:4, 3:2
		intent.putExtra(EIPConstans.INTENT_ASPECT_RATIO_Y, 1)
	}
	intent.putExtra(EIPConstans.INTENT_IMAGE_COMPRESSION_QUALITY, config.imageCompression)

	// setting maximum bitmap width and height
//	intent.putExtra(EIPConstans.INTENT_SET_BITMAP_MAX_WIDTH_HEIGHT, true)
	intent.putExtra(EIPConstans.INTENT_BITMAP_MAX_WIDTH, config.compressMaxWeight)
	intent.putExtra(EIPConstans.INTENT_BITMAP_MAX_HEIGHT, config.compressMaxHeight)
	return intent
}


fun ComponentActivity.registerEIP(
	context: () -> Context = { this },
	callback: EIPCallback
): EIPLauncher {
	return EIPLauncher(context, createLauncher(callback))
}

fun Fragment.registerEIP(
	context: () -> Context = { requireContext() },
	callback: EIPCallback
): EIPLauncher {
	return EIPLauncher(context, createLauncher(callback))
}

private fun ComponentActivity.createLauncher(cb: EIPCallback): ActivityResultLauncher<Intent> {
	return registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
		val uri = it.data?.getParcelableExtra<Uri>("path")
		uri?.let { it1 -> cb(it1) }
	}
}

private fun Fragment.createLauncher(cb: EIPCallback): ActivityResultLauncher<Intent> {
	return registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
		val uri = it.data?.getParcelableExtra<Uri>("path")
		uri?.let { it1 -> cb(it1) }

	}
}