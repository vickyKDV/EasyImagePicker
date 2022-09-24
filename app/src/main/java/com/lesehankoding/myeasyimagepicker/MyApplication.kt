package com.lesehankoding.myeasyimagepicker

import android.app.*
import android.content.*
import android.support.multidex.*

class MyApplication:Application() {
	override fun attachBaseContext(base: Context) {
		super.attachBaseContext(base)
		MultiDex.install(this)
	}
}