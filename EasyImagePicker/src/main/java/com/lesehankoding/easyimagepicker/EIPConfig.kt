package com.lesehankoding.easyimagepicker


class EIPConfig (
	var isCropAspectRatio : Boolean = true,
	var cropRatio: RATIO = RATIO.DEFAULT,
	var compressMaxWeight : Int = 680,
	var compressMaxHeight : Int = 680,
	var imageCompression : Int = 80,
)
sealed class RATIO {
	object DEFAULT : RATIO()
	object WIDE : RATIO()
}