package com.sunnyxibei.hencoderpractice

import android.content.Context
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.util.AttributeSet
import android.view.View
import androidx.annotation.Keep

/**
 * 整个动画拆分成了三部分
 *
 * Created by jiayuanbin on 2017-9-23.
 */
class MapView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    //Y轴方向旋转角度
    @set:Keep
    var degreeY: Float = 0f
        set(value) {
            field = value
            invalidate()
        }

    //不变的那一半，Y轴方向旋转角度
    @set:Keep
    var fixDegreeY: Float = 0f
        set(value) {
            field = value
            invalidate()
        }

    //Z轴方向（平面内）旋转的角度
    @set:Keep
    var degreeZ: Float = 0f
        set(value) {
            field = value
            invalidate()
        }

    private val paint: Paint
    private var bitmap: Bitmap
    private val camera: Camera

    init {
        val a = context.obtainStyledAttributes(attrs, R.styleable.MapView)
        val drawable = a.getDrawable(R.styleable.MapView_mv_background) as? BitmapDrawable
        a.recycle()

        bitmap = drawable?.bitmap ?: BitmapFactory.decodeResource(resources, R.drawable.flip_board)
        paint = Paint(Paint.ANTI_ALIAS_FLAG)
        camera = Camera()

        val displayMetrics = resources.displayMetrics
        val newZ = -displayMetrics.density * 6
        camera.setLocation(0f, 0f, newZ) // Use float values for camera
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val bitmapWidth = bitmap.width
        val bitmapHeight = bitmap.height
        val centerX = width / 2f // Use float for center calculations
        val centerY = height / 2f // Use float for center calculations
        val x = centerX - bitmapWidth / 2f
        val y = centerY - bitmapHeight / 2f

        //画变换的一半
        //先旋转，再裁切，再使用camera执行3D动效,**然后保存camera效果**,最后再旋转回来
        canvas.save()
        camera.save()
        canvas.translate(centerX, centerY)
        canvas.rotate(-degreeZ)
        camera.rotateY(degreeY)
        camera.applyToCanvas(canvas)
        //计算裁切参数时清注意，此时的canvas的坐标系已经移动
        canvas.clipRect(0f, -centerY, centerX, centerY) // Use float values
        canvas.rotate(degreeZ)
        canvas.translate(-centerX, -centerY)
        camera.restore()
        canvas.drawBitmap(bitmap, x, y, paint)
        canvas.restore()

        //画不变换的另一半
        canvas.save()
        camera.save()
        canvas.translate(centerX, centerY)
        canvas.rotate(-degreeZ)
        //计算裁切参数时清注意，此时的canvas的坐标系已经移动
        canvas.clipRect(-centerX, -centerY, 0f, centerY) // Use float values
        //此时的canvas的坐标系已经旋转，所以这里是rotateY
        camera.rotateY(fixDegreeY)
        camera.applyToCanvas(canvas)
        canvas.rotate(degreeZ)
        canvas.translate(-centerX, -centerY)
        camera.restore()
        canvas.drawBitmap(bitmap, x, y, paint)
        canvas.restore()
    }

    /**
     * 启动动画之前调用，把参数reset到初始状态
     */
    fun reset() {
        degreeY = 0f
        fixDegreeY = 0f
        degreeZ = 0f
        invalidate() // Invalidate after resetting properties
    }

    fun setBitmap(bitmap: Bitmap) {
        this.bitmap = bitmap
        invalidate()
    }
}
