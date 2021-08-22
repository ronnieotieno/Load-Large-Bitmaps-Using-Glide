package dev.ronnie.github.bitmaps

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import dev.ronnie.github.imagepicker.ImagePicker

class MainActivity : AppCompatActivity() {
    private lateinit var openGalleryBtn: Button
    private lateinit var openCameraBtn: Button
    private lateinit var imageView: ImageView
    private val maxHeightWidth = 1080
    private var bitmap: Bitmap? = null
    private lateinit var imagePicker: ImagePicker
    private val _bitmapLivedata = MutableLiveData<Bitmap>()
    private val bitmapLiveData:LiveData<Bitmap> get() = _bitmapLivedata

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        imageView = findViewById(R.id.imageView)
        openCameraBtn = findViewById(R.id.openCamera)
        openGalleryBtn = findViewById(R.id.openStorage)
        imagePicker = ImagePicker(this)

        bitmapLiveData.observe(this, { bitmap ->
            this.bitmap = bitmap
            imageView.setImageBitmap(this.bitmap)
        })

        openCameraBtn.setOnClickListener { takeFromCamera() }
        openGalleryBtn.setOnClickListener { pickFromStorage() }
    }

    private fun takeFromCamera() {
        imagePicker.takeFromCamera { isSuccessful, uri, errorString ->
            if(isSuccessful){
                getLargeBitmap(uri!!)
            }else{
                Toast.makeText(this@MainActivity, errorString, Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun pickFromStorage() {
        imagePicker.pickFromStorage { isSuccessful, uri, errorString ->
            if(isSuccessful){
                getLargeBitmap(uri!!)
            }else{
                Toast.makeText(this@MainActivity, errorString, Toast.LENGTH_LONG).show()
            }
        }
    }

    /**
     * The function takes [Uri] which is passed to [Glide] with the max height, width needed, in my case its
     * [maxHeightWidth] but feel free to use any that you like.
     * Works well with phones with large images that notoriously crash the app.
     */
    //Can be put in somewhere other than activity or fragment.
    private fun getLargeBitmap(uri: Uri) {
        Glide.with(this)
            .asBitmap()
            .override(maxHeightWidth, maxHeightWidth)
            .load(uri)
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .into(object : CustomTarget<Bitmap>() {
                override fun onResourceReady(
                    resource: Bitmap,
                    transition: Transition<in Bitmap?>?
                ) {
                    _bitmapLivedata.postValue(resource)
                }

                override fun onLoadCleared(placeholder: Drawable?) {}
            })
    }
}