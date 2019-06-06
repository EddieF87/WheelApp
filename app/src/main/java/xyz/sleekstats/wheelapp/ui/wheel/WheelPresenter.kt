package xyz.sleekstats.wheelapp.ui.wheel

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import xyz.sleekstats.wheelapp.model.WheelChoice
import kotlin.random.Random

class WheelPresenter(private val wheelView: WheelContract.View) :
    WheelContract.Presenter {

    var wheelStartingDegrees = 0f
    var wheelEndingDegrees = 0f
    var wheelChoices: List<WheelChoice> = arrayListOf()

    private val wheelChoiceDatabase = wheelView.provideDatabase()
    private val wheelChoiceDAO = wheelChoiceDatabase.wheelChoiceDao()

    override fun getWheelChoices() {
        CoroutineScope(Dispatchers.Main).launch {
            wheelChoices =
                withContext(Dispatchers.IO) { wheelChoiceDAO.getAllWheelChoices() }
            if (wheelChoices.size > 1) {
                wheelView.buildWheel(wheelChoices)
            } else {
                wheelView.onNotEnoughWheelChoices()
            }
        }
    }

    override fun determineSpin() {
        val degreesToSpin = SMALLEST_SPIN + Random.nextInt(SPIN_VARIANCE)
        wheelEndingDegrees = wheelStartingDegrees + degreesToSpin

        wheelView.spinWheel(wheelStartingDegrees, wheelEndingDegrees)

        wheelStartingDegrees = wheelEndingDegrees

        determineChoiceWheelLandsOn(wheelStartingDegrees.toInt())
    }

    private fun determineChoiceWheelLandsOn(wheelPosition: Int) {

        val finalAngle: Int = getFinalAngle(wheelPosition)
        val sections = wheelChoices.size
        val sectionLength = getSectionLength(sections)
        val wheelSectionIndex = getWheelSectionIndex(finalAngle, sectionLength)
        val choiceLandedOn = wheelChoices[wheelSectionIndex]

        wheelView.displayWinner(choiceLandedOn.text, choiceLandedOn.colorIndex)
    }

    private fun getFinalAngle(landingSpotInDegrees: Int): Int =
        landingSpotInDegrees % WheelActivity.DEGREES_IN_CIRCLE.toInt()

    private fun getSectionLength(sections: Int): Int = WheelActivity.DEGREES_IN_CIRCLE.toInt() / sections

    private fun getWheelSectionIndex(finalAngle: Int, sectionLength: Int): Int = finalAngle / sectionLength

    companion object {
        const val SMALLEST_SPIN = 500
        const val LARGEST_SPIN = 1500
        const val SPIN_VARIANCE = LARGEST_SPIN - SMALLEST_SPIN
    }
}