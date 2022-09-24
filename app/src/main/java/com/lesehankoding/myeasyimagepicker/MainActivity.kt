package com.lesehankoding.myeasyimagepicker

import android.net.*
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.*
import android.widget.*
import com.lesehankoding.easyimagepicker.*
import java.io.*

class MainActivity : AppCompatActivity() {
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_main)
		findViewById<Button>(R.id.button).setOnClickListener {
			val config = EIPConfig(
					isCropAspectRatio = true,
					cropRatio = RATIO.DEFAULT
			)
			launcherEIP.launchDialog(config)
		}
	}



	val launcherEIP = registerEIP {

//		get file path , Simple use with 1 lines code
		val file = File(it.path!!)

//		and load to image or send file to server
		val img = findViewById<ImageView>(R.id.imageView)
		img.setImageURI(Uri.parse(file.absolutePath))
	}
}