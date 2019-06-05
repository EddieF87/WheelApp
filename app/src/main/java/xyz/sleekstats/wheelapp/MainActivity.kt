package xyz.sleekstats.wheelapp

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.graphics.*
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    private val dummyColors = intArrayOf(Color.YELLOW, Color.RED, Color.BLUE, Color.GREEN)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val wheelBitMap : Bitmap = drawWheel(dummyColors)
        wheel_view.setImageBitmap(wheelBitMap)
    }

    private fun drawWheel(colors: IntArray) : Bitmap {
        val width = getWidthOfScreen()
        val bitmap = Bitmap.createBitmap(width, width, Bitmap.Config.ARGB_8888)

        val canvas = Canvas(bitmap)
        val rectF = RectF(0f, 0f, bitmap.width.toFloat(), bitmap.height.toFloat())

        val paint = Paint()
        paint.style = Paint.Style.FILL

        var start = 0f
        val amountOfColors = colors.size

        colors.forEach {
            paint.color = it
            val angle: Float = DEGREES_IN_CIRCLE / amountOfColors
            canvas.drawArc(rectF, start, angle, true, paint)
            start += angle
        }
        return bitmap
    }

    private fun getWidthOfScreen() : Int {
        val display = windowManager.defaultDisplay
        val size = Point()
        display.getSize(size)
        return size.x
    }

    companion object {
        const val DEGREES_IN_CIRCLE = 360.0f
    }
}
