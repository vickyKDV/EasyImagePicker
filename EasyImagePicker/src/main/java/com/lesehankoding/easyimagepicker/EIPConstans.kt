package com.lesehankoding.easyimagepicker

import android.content.*
import android.net.*
import android.provider.*

object EIPConstans {
	val TAG = "EIP-LOG"
	const val INTENT_IMAGE_PICKER_OPTION = "image_picker_option"
	const val INTENT_ASPECT_RATIO_X = "aspect_ratio_x"
	const val INTENT_ASPECT_RATIO_Y = "aspect_ratio_Y"
	const val INTENT_LOCK_ASPECT_RATIO = "lock_aspect_ratio"
	const val INTENT_IMAGE_COMPRESSION_QUALITY = "compression_quality"
	const val INTENT_SET_BITMAP_MAX_WIDTH_HEIGHT = "set_bitmap_max_width_height"
	const val INTENT_BITMAP_MAX_WIDTH = "max_width"
	const val INTENT_BITMAP_MAX_HEIGHT = "max_height"
	const val REQUEST_IMAGE_CAPTURE = 0
	const val REQUEST_GALLERY_IMAGE = 1
	var fileName : String? = null
	const val DEFAULT_MAX_WIDTH = 480
	const val DEFAULT_MAX_HEIGHT = 480
	const val DEFAULT_COMPRESSION_QTY = 480

//	var islockAspectRatio : Boolean = true
	const val isSetBitmapMaxWidthHeight = true
//	var lockAspectRatio : Boolean = true
	const val DEFAULT_ASPECT_RATIO_X  = 16
	const val DEFAULT_ASPECT_RATIO_Y  = 9
//	var bitmapMaxWidth : Int = 680
//	var bitmapMaxHeight : Int = 680
//	var IMAGE_COMPRESSION : Int = 80
}