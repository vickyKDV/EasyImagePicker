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
	
	
How To Use
    
    public class MainActivity extends AppCompatActivity {
    
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);
    
    
    
            findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ImagePickerActivity.showImagePickerOptions(MainActivity.this,2000,800,800);
                }
            });
        }
    
        @Override
        protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
            super.onActivityResult(requestCode, resultCode, data);
            if(resultCode == RESULT_OK){
                if(requestCode == 2000){
                    ImageView imageView = findViewById(R.id.imageView);
                    Uri uri = data.getParcelableExtra("path");
                    Log.d("RESULT", "onActivityResult: "+uri);
                                    File fileImgProduk = new File(uri.getPath());
                    Bitmap bitmap = BitmapFactory.decodeFile(fileImgProduk.getAbsolutePath());
    
                    imageView.setImageBitmap(bitmap);
                }
            }
        }
    }	
