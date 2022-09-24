package com.lesehankoding.easyimagepicker

import android.content.*
import android.content.pm.*
import android.graphics.*
import android.media.*
import android.os.*
import com.yalantis.ucrop.model.*
import kotlinx.android.parcel.*
import kotlinx.coroutines.*
import java.util.ArrayList

class EIPConfig (
//	var isCameraOnly: Boolean = false,
//	var isGaleryOnly: Boolean = false,
//	var cropAspectRatio: Boolean = true,
//	var isSetBitmapMaxWidthHeight : Boolean = true,
	var isCropAspectRatio : Boolean = true,
//	var cropAspectRatioX : Int = 1,
//	var cropAspectRatioY : Int = 1,
	var cropRatio: RATIO = RATIO.DEFAULT,
	var compressMaxWeight : Int = 680,
	var compressMaxHeight : Int = 680,
	var imageCompression : Int = 80,
)
sealed class RATIO {
	object DEFAULT : RATIO()
	object WIDE : RATIO()
}