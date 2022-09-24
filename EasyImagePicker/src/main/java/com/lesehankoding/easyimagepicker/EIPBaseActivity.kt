package com.lesehankoding.easyimagepicker

import android.*
import android.content.*
import android.graphics.*
import android.net.*
import android.provider.*
import android.util.*
import android.widget.*
import androidx.appcompat.app.*
import androidx.core.content.*
import com.lesehankoding.easyimagepicker.EIPConstans.DEFAULT_ASPECT_RATIO_X
import com.lesehankoding.easyimagepicker.EIPConstans.DEFAULT_ASPECT_RATIO_Y
import com.lesehankoding.easyimagepicker.EIPConstans.DEFAULT_COMPRESSION_QTY
import com.lesehankoding.easyimagepicker.EIPConstans.DEFAULT_MAX_HEIGHT
import com.lesehankoding.easyimagepicker.EIPConstans.DEFAULT_MAX_WIDTH
import com.lesehankoding.easyimagepicker.EIPConstans.INTENT_ASPECT_RATIO_X
import com.lesehankoding.easyimagepicker.EIPConstans.INTENT_ASPECT_RATIO_Y
import com.lesehankoding.easyimagepicker.EIPConstans.INTENT_BITMAP_MAX_HEIGHT
import com.lesehankoding.easyimagepicker.EIPConstans.INTENT_BITMAP_MAX_WIDTH
import com.lesehankoding.easyimagepicker.EIPConstans.INTENT_IMAGE_COMPRESSION_QUALITY
import com.lesehankoding.easyimagepicker.EIPConstans.INTENT_LOCK_ASPECT_RATIO
import com.nabinbhandari.android.permissions.*
import com.yalantis.ucrop.*
import id.zelory.compressor.*
import java.io.*

open class EIPBaseActivity:AppCompatActivity() {



	fun getCacheImagePath(fileName: String): Uri? {
		val path = File(externalCacheDir, "camera")
		if (! path.exists()) path.mkdirs()
		val image = File(path, fileName)
		return FileProvider.getUriForFile(
				this@EIPBaseActivity,
				"$packageName.provider",
				image
		)
	}

	fun checkPersmission(isGrant : (Boolean) -> Unit) {
		val permissions = arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA)
		Permissions.check(this, permissions, null, null, object : PermissionHandler() {
			override fun onGranted() {
				isGrant.invoke(true)
			}
			override fun onDenied(context: Context, deniedPermissions: ArrayList<String>) {
				showSettingsDialog()
			}
		})
	}

	@Deprecated("Deprecated in Java")
	override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
		super.onActivityResult(requestCode, resultCode, data)
		if (resultCode == RESULT_OK && requestCode == UCrop.REQUEST_CROP) {
			handleUCropResult(data)
		}
	}

	fun handleUCropResult(data: Intent?) {
		if (data == null) {
			resultCancel()
			return
		}
		val resultUri = UCrop.getOutput(data)
		resultOK(resultUri)
	}

	fun resultOK(imagePath: Uri?) {

		val maxWidth = intent.getIntExtra(
				INTENT_BITMAP_MAX_WIDTH,
				DEFAULT_MAX_WIDTH
		)
		val maxHeight = intent.getIntExtra(
				INTENT_BITMAP_MAX_HEIGHT,
				DEFAULT_MAX_HEIGHT
		)

		val compressQTY = intent.getIntExtra(
				INTENT_IMAGE_COMPRESSION_QUALITY,
				DEFAULT_COMPRESSION_QTY
		)

		val fileIMG = imagePath?.path?.let { File(it) }
		try {
			val newFileImageCompress = Compressor(this)
				.setMaxHeight(maxHeight)
				.setMaxWidth(maxWidth)
				.setQuality(compressQTY)
				.setCompressFormat(Bitmap.CompressFormat.PNG)
				.compressToFile(fileIMG)
			if (newFileImageCompress != null) {
				Log.d(EIPConstans.TAG, "onActivityResult: $newFileImageCompress")
				val uri = Uri.parse(newFileImageCompress.path)
				val intent = Intent()
				intent.putExtra("path", uri)
				setResult(RESULT_OK, intent)
				finish()
			}
		} catch (e: IOException) {
			e.printStackTrace()
			Log.d(EIPConstans.TAG, "setResultOk: " + e.message)
		}
	}

	fun launchCropImage(sourceUri: Uri?) {
		val ratioX = intent.getIntExtra(
				INTENT_ASPECT_RATIO_X,
				DEFAULT_ASPECT_RATIO_X
		)
		val ratioY = intent.getIntExtra(
				INTENT_ASPECT_RATIO_Y,
				DEFAULT_ASPECT_RATIO_Y
		)

		val compressQTY = intent.getIntExtra(
				INTENT_IMAGE_COMPRESSION_QUALITY,
				DEFAULT_COMPRESSION_QTY
		)

		val isCropAspectRatio = intent.getBooleanExtra(
				INTENT_LOCK_ASPECT_RATIO,
				true
		)

//		val isSetBitmapMaxWidthHeight = intent.getBooleanExtra(
//				isSetBitmapMaxWidthHeight,
//				true
//		)

		val destinationUri = Uri.fromFile(File(cacheDir, queryName(contentResolver, sourceUri)))
		val options = UCrop.Options()
		options.setCompressionQuality(compressQTY)

		// applying UI theme
		options.setToolbarColor(ContextCompat.getColor(this, R.color.colorPrimaryEIP))
		options.setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimaryEIP))
		options.setActiveControlsWidgetColor(ContextCompat.getColor(this, R.color.colorPrimaryEIP))

		//sizing
		options.setMaxBitmapSize(800)
		options.setCompressionQuality(80)
		if (isCropAspectRatio) options.withAspectRatio(
				ratioX.toFloat(),
				ratioY.toFloat()
		)
		if (true) options.withMaxResultSize(
				DEFAULT_MAX_WIDTH,
				DEFAULT_MAX_HEIGHT
		)
		UCrop.of(sourceUri !!, destinationUri)
			.withOptions(options)
			.start(this)
	}

	fun showSettingsDialog(){
		val builder = AlertDialog.Builder(this)
		builder.setTitle(R.string.eip_allow_access_title)
		builder.setMessage(R.string.eip_allow_access_description)
		builder.setPositiveButton(R.string.eip_goto_setting) { dialog, _ ->
			dialog.cancel()
			openSettings()
			finish()
		}
		builder.setNegativeButton(R.string.eip_cancel) { dialog, _ ->
			dialog.cancel()
			finish()
		}
		builder.show()
	}

	fun openSettings() {
		val intent = Intent(
				Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
				Uri.fromParts("package", packageName, null)
		)
		intent.addCategory(Intent.CATEGORY_DEFAULT)
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
		intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
		intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS)
		startActivity(intent)
	}

	fun showToast(msg:String?=""){
		Toast.makeText(applicationContext, msg, Toast.LENGTH_LONG).show()
	}

	fun resultCancel() {
		val intent = Intent()
		setResult(RESULT_CANCELED, intent)
		finish()
	}

	/**
	 * Clear Cache File Selected
	 **/
	fun clearCache(context: Context) {
		val path = File(context.externalCacheDir, "camera")
		if (path.exists() && path.isDirectory) {
			for (child in path.listFiles() !!) {
				child.delete()
			}
		}
	}

	fun queryName(resolver: ContentResolver, uri: Uri?): String {
		val returnCursor = resolver.query(uri !!, null, null, null, null) !!
		val nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
		returnCursor.moveToFirst()
		val name = returnCursor.getString(nameIndex)
		returnCursor.close()
		return name
	}

}