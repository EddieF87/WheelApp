package xyz.sleekstats.wheelapp.ui.wheel

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.widget.ImageView
import xyz.sleekstats.wheelapp.model.WheelChoice

class WheelView(context: Context, attributeSet: AttributeSet?) : ImageView(context, attributeSet) {

    //Draw wheel based on width of screen and colors given
    fun drawWheel(choices: List<WheelChoice>, screenWidth: Int): Bitmap {
        val bitmap = Bitmap.createBitmap(screenWidth, screenWidth, Bitmap.Config.ARGB_8888)

        val canvas = Canvas(bitmap)
        val rectF = RectF(0f, 0f, bitmap.width.toFloat(), bitmap.height.toFloat())

        val paint = Paint()
        paint.style = Paint.Style.FILL

        var startAngle = 0f
        val angleDegreesPerSection: Float = WheelActivity.DEGREES_IN_CIRCLE / choices.size

        choices.forEach {
            paint.color = it.colorIndex
            canvas.drawArc(rectF, startAngle, angleDegreesPerSection, true, paint)

            val endAngle = startAngle + angleDegreesPerSection
            val medianAngle = (startAngle + endAngle) / 2

            drawChoiceText(it.text, canvas, paint, medianAngle)

            startAngle = endAngle
        }
        setImageBitmap(bitmap)
        return bitmap
    }

    //Create text for choice around the center and along the angle of its section of the wheel
    private fun drawChoiceText(choiceText: String, canvas: Canvas, paint: Paint, medianAngle: Float) {
        paint.color = Color.WHITE
        paint.textSize = 48f
        val rect = Rect()
        paint.getTextBounds(choiceText, 0, choiceText.length, rect)

        canvas.save()
        canvas.rotate(
            medianAngle,
            canvas.width / 2f,
            canvas.height / 2f
        )
        canvas.drawText(
            choiceText,
            canvas.width * .75f,
            canvas.height / 2f, paint
        )
        canvas.restore()
    }
}