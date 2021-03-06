package com.project.findme.mainactivity.mainfragments.ui.createTextPost

import android.content.Context
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Paint
import android.net.Uri
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.findme.mainactivity.repository.MainRepository
import com.project.findme.utils.Constants.COLORS
import com.project.findme.utils.Constants.PEN_COLORS
import com.project.findme.utils.Events
import com.project.findme.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class CreateTextPostViewModel @Inject constructor(
    private val repository: MainRepository,
    private val applicationContext: Context
) : ViewModel() {

    private val _createImageStatus = MutableLiveData<Events<Resource<Bitmap>>>()
    val createImageStatus: LiveData<Events<Resource<Bitmap>>> = _createImageStatus

    private val _createPostStatus = MutableLiveData<Events<Resource<Any>>>()
    val createPostStatus: LiveData<Events<Resource<Any>>> = _createPostStatus

    fun addTextToImage(text: String, color: Int, penColor: Int) {

        viewModelScope.launch(Dispatchers.IO) {
            _createImageStatus.postValue(Events(Resource.Loading()))
            val resources: Resources = applicationContext.resources
            val scale: Float = resources.displayMetrics.density
            val bmp = BitmapFactory.decodeResource(resources, COLORS[color]!!)
            var bitmap = Bitmap.createBitmap(bmp, 0, 0, 1000, 1000)

            var bitmapConfig = bitmap.config
            if (bitmapConfig == null) {
                bitmapConfig = Bitmap.Config.ARGB_8888
            }
            bitmap = bitmap.copy(bitmapConfig, true)

            val canvas = Canvas(bitmap)
            val paint = TextPaint(Paint.ANTI_ALIAS_FLAG)
            paint.color = PEN_COLORS[penColor]!!
            paint.textSize = (40 * scale)
            paint.setShadowLayer(1f, 0f, 1f, PEN_COLORS[penColor]!!)
            val textWidth: Int = canvas.width - (26 * scale).toInt()
            val sb = StaticLayout.Builder.obtain(text, 0, text.length, paint, textWidth)
                .setAlignment(Layout.Alignment.ALIGN_CENTER)
                .setLineSpacing(0.0f, 1.0f)
                .setIncludePad(false)
            val textLayout = sb.build()
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


    fun createPost(imageUri: Uri) {
        _createPostStatus.postValue(Events(Resource.Loading()))
        viewModelScope.launch(Dispatchers.IO) {
            val result =
                repository.createPost(imageUri, "", "", "", "")
            _createPostStatus.postValue(Events(result))
        }
    }
}