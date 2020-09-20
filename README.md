# EasyImagePicker
[![](https://jitpack.io/v/vickyKDV/EasyImagePicker.svg)](https://jitpack.io/#vickyKDV/EasyImagePicker)


![alt text](https://raw.githubusercontent.com/vickyKDV/EasyImagePicker/master/dialogpng.png)
![alt text](https://raw.githubusercontent.com/vickyKDV/EasyImagePicker/master/croppng.png)
![alt text](https://raw.githubusercontent.com/vickyKDV/EasyImagePicker/master/resultpng.png)

   Cara implementasi
   
   
   Set pada build.gradle application
   
     allprojects {
          repositories {
             ...
             ...
             maven { url "https://jitpack.io" }

          }
      }
    
   Set pada build.gradle module
    
    dependencies {
        ...
        ...
	     implementation 'com.github.vickyKDV:EasyImagePicker:0.1'
	}
