package com.lesehankoding.easyimagepicker

import android.annotation.*
import android.app.*
import android.content.*
import android.os.*
import android.provider.*
import android.util.*
import androidx.activity.result.contract.*
import com.lesehankoding.easyimagepicker.EIPConstans.TAG
import com.lesehankoding.easyimagepicker.EIPConstans.fileName
import com.lesehankoding.easyimagepicker.Helper.*
import com.lesehankoding.easyimagepicker.databinding.*
import java.io.*


class EIPActivity: EIPBaseActivity() {

	private lateinit var binding: EasyimagepickerActivityXyzBinding




	@SuppressLint("QueryPermissionsNeeded")
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		if (intent == null) {
			showToast("Image picker is missing!")
			finish()
			return
		}
		binding = EasyimagepickerActivityXyzBinding.inflate(layoutInflater)
		setContentView(binding.root)
		clearCache(this)
		checkPersmission{
			if(it){
				setUI()
			}
		}
	}


	override fun onBackPressed() {
		setResult(Activity.RESULT_CANCELED)
		finish()
	}

	private fun setUI(){
		val isCamera = intent.getBooleanExtra("isCamera",true)
		val i : Intent
		if(isCamera) {
			if (! DeviceHelper.checkCameraAvailability(this)) {
				finish()
				return
			}
				fileName =
					System.currentTimeMillis().toString() + ".jpg"
				i = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
				i.putExtra(
						MediaStore.EXTRA_OUTPUT,
						getCacheImagePath(fileName !!)
				)
				if (i.resolveActivity(packageManager) == null) {
					showToast(getString(R.string.eip_denied_access_message))
					return
				}

		}else {
			i = Intent(
					Intent.ACTION_PICK,
					MediaStore.Images.Media.EXTERNAL_CONTENT_URI
			)

		}
		launchGalery(isCamera).launch(i)
	}


	private fun launchGalery(isCamera:Boolean) = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
		if (result.resultCode == Activity.RESULT_OK) {
			if(isCamera){
				try {
					Log.d(TAG, "launchGalery: $fileName")
					launchCropImage(getCacheImagePath(fileName!!))
				}catch (e:FileNotFoundException){
					Log.d(TAG, "launchGalery: ${e.printStackTrace()}")
					showToast(e.message)
				}
			}else {
				val imageUri = result.data!!.data
				launchCropImage(imageUri)
			}
		} else {
			finish()
		}
	}




}