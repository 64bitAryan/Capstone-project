package com.project.findme.mainactivity.mainfragments.ui.createTextPost

import android.content.Context
import android.content.res.Resources
import android.graphics.*
import android.net.Uri
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.findme.mainactivity.repository.MainRepository
import com.project.findme.utils.Events
import com.project.findme.utils.Resource
import com.ryan.findme.R
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class CreateTextPostViewModel @Inject constructor(
    private val repository: MainRepository,
    private val applicationContext: Context,
    private val dispatcher: CoroutineDispatcher = Dispatchers.Main
) : ViewModel() {

    private val _createImageStatus = MutableLiveData<Events<Resource<Bitmap>>>()
    val createImageStatus: LiveData<Events<Resource<Bitmap>>> = _createImageStatus

    private val _createPostStatus = MutableLiveData<Events<Resource<Any>>>()
    val createPostStatus: LiveData<Events<Resource<Any>>> = _createPostStatus

    fun createImage(text: String) {
        if (text.isEmpty()) {
            val error = applicationContext.getString(R.string.error_input_empty)
            _createImageStatus.postValue(Events(Resource.Error(error)))
        } else {
            viewModelScope.launch(Dispatchers.IO) {
                _createImageStatus.postValue(Events(Resource.Loading()))
                val resources: Resources = applicationContext.resources
                val scale: Float = resources.displayMetrics.density
                val bmp = BitmapFactory.decodeResource(resources, R.drawable.red_background)
                var bitmap = Bitmap.createBitmap(bmp, 0, 0, 1000, 1000)

                var bitmapConfig = bitmap.config
                if (bitmapConfig == null) {
                    bitmapConfig = Bitmap.Config.ARGB_8888
                }
                bitmap = bitmap.copy(bitmapConfig, true)

                val canvas = Canvas(bitmap)
                val paint = TextPaint(Paint.ANTI_ALIAS_FLAG)
                paint.color = Color.rgb(255, 255, 255)
                paint.textSize = (40 * scale)
                paint.setShadowLayer(1f, 0f, 1f, Color.WHITE)
                val textWidth: Int = canvas.width - (32 * scale).toInt()
                val textLayout = StaticLayout(
                    text, paint, textWidth, Layout.Alignment.ALIGN_CENTER, 1.0f, 0.0f, false
                )
                val textHeight = textLayout.height
                val x = ((bitmap.width - textWidth) / 2).toFloat()
                val y = ((bitmap.height - textHeight) / 2).toFloat()
                canvas.save()
                canvas.translate(x, y)
                textLayout.draw(canvas)
                canvas.restore()
                _createImageStatus.postValue(Events(Resource.Success(bitmap)))
            }
        }
    }

    fun createPost(imageUri: Uri) {
        viewModelScope.launch(Dispatchers.IO) {
            val result =
                repository.createPost(imageUri, "", "", "", "")
            _createPostStatus.postValue(Events(result))
        }
    }
}