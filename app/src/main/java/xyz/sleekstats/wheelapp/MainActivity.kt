package xyz.sleekstats.wheelapp

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.graphics.*
import android.view.animation.Animation
import android.view.animation.RotateAnimation
import kotlinx.android.synthetic.main.activity_main.*
import kotlin.random.Random

/**This Activity displays a wheel of user-entered choices, along with a button that allows the user to spin the wheel.
 * The wheel will eventually stop on one of the possible choices based on the random amount of degrees it's spun.
**/
class MainActivity : AppCompatActivity() {

    private val dummyColors = intArrayOf(Color.YELLOW, Color.RED, Color.BLUE, Color.GREEN)
    private var wheelStartingDegrees = 0f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val wheelBitMap: Bitmap = drawWheel(dummyColors)
        wheel_view.setImageBitmap(wheelBitMap)

        spin_button.setOnClickListener {
            val random = Random
            spinWheel(random.nextInt(LARGEST_SPIN))
        }
    }

    //Draw wheel based on width of screen and colors given
    private fun drawWheel(colors: IntArray): Bitmap {
        val width = getWidthOfScreen()
        val bitmap = Bitmap.createBitmap(width, width, Bitmap.Config.ARGB_8888)

        val canvas = Canvas(bitmap)
        val rectF = RectF(0f, 0f, bitmap.width.toFloat(), bitmap.height.toFloat())

        val paint = Paint()
        paint.style = Paint.Style.FILL

        var startAngle = 0f
        val angleDegreesPerSection: Float = DEGREES_IN_CIRCLE / colors.size

        colors.forEach {
            paint.color = it
            canvas.drawArc(rectF, startAngle, angleDegreesPerSection, true, paint)
            startAngle += angleDegreesPerSection
        }
        return bitmap
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
    private fun createRotateAnimation(fromDegrees: Float, toDegrees: Float) : RotateAnimation {
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
