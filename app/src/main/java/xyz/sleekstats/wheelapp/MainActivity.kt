package xyz.sleekstats.wheelapp

import android.os.Bundle
import android.graphics.*
import android.view.animation.Animation
import android.view.animation.RotateAnimation
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import xyz.sleekstats.wheelapp.db.WheelChoiceDAO
import xyz.sleekstats.wheelapp.db.WheelChoiceDatabase
import xyz.sleekstats.wheelapp.model.WheelChoice
import kotlin.random.Random

/**This Activity displays a wheel of user-entered choices, along with a button that allows the user to spin the wheel.
 * The wheel will eventually stop on one of the possible choices based on the random amount of degrees it's spun.
 **/
class MainActivity : AppCompatActivity() {

    private lateinit var wheelChoices: Array<WheelChoice>
    private var wheelStartingDegrees = 0f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val dao = initiateDBAndDao()
        getChoices(dao)
    }

    //initiate database and get DAO for wheel choices, to be moved out of activity soon
    private fun initiateDBAndDao() : WheelChoiceDAO {
        val db = WheelChoiceDatabase.getDatabase(applicationContext)
        return db.wheelChoiceDao()
    }

    //retrieve choices from Room database and then build a wheel if two or more choices are retrieved
    private fun getChoices(dao: WheelChoiceDAO) = GlobalScope.launch {
        wheelChoices = dao.getAllWheelChoices().toTypedArray()
        if (wheelChoices.size > 1) {
            buildWheel()
        }
    }


    private fun buildWheel() {
        val wheelBitMap: Bitmap = drawWheel(wheelChoices)
        wheel_view.setImageBitmap(wheelBitMap)

        spin_button.setOnClickListener {
            val random = Random
            spinWheel(random.nextInt(LARGEST_SPIN))
        }
    }

    //Draw wheel based on width of screen and colors given
    private fun drawWheel(choices: Array<WheelChoice>): Bitmap {
        val width = getWidthOfScreen()
        val bitmap = Bitmap.createBitmap(width, width, Bitmap.Config.ARGB_8888)

        val canvas = Canvas(bitmap)
        val rectF = RectF(0f, 0f, bitmap.width.toFloat(), bitmap.height.toFloat())

        val paint = Paint()
        paint.style = Paint.Style.FILL

        var startAngle = 0f
        val angleDegreesPerSection: Float = DEGREES_IN_CIRCLE / choices.size

        choices.forEach {
            paint.color = it.colorIndex
            canvas.drawArc(rectF, startAngle, angleDegreesPerSection, true, paint)

            val endAngle = startAngle + angleDegreesPerSection
            val medianAngle = (startAngle + endAngle) / 2

            drawChoiceText(it.text, canvas, paint, medianAngle)

            startAngle = endAngle
        }
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

    private fun getWidthOfScreen(): Int {
        val display = windowManager.defaultDisplay
        val size = Point()
        display.getSize(size)
        return size.x
    }

    //Rotate wheel the amount of degrees entered and update starting point
    private fun spinWheel(degreesToSpinWheel: Int) {
        val wheelEndingDegrees = wheelStartingDegrees + degreesToSpinWheel

        val rotateAnimation = createRotateAnimation(wheelStartingDegrees, wheelEndingDegrees)
        wheel_view.startAnimation(rotateAnimation)

        wheelStartingDegrees = wheelEndingDegrees
    }

    //Create animation that rotates object around its center a certain amount of degrees and keeps it there
    private fun createRotateAnimation(fromDegrees: Float, toDegrees: Float): RotateAnimation {
        val rotateAnimation = RotateAnimation(
            fromDegrees, toDegrees,
            Animation.RELATIVE_TO_SELF, .5f,
            Animation.RELATIVE_TO_SELF, .5f
        )
        rotateAnimation.duration = SPIN_DURATION
        rotateAnimation.fillAfter = true
        rotateAnimation.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(p0: Animation?) {
                spin_button.isClickable = false
            }

            override fun onAnimationEnd(p0: Animation?) {
                spin_button.isClickable = true
            }

            override fun onAnimationRepeat(p0: Animation?) {}
        })
        return rotateAnimation
    }

    companion object {
        const val DEGREES_IN_CIRCLE: Float = 360.0f
        const val SPIN_DURATION: Long = 3000
        const val LARGEST_SPIN = 1000
    }
}
