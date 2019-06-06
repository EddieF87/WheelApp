package xyz.sleekstats.wheelapp.ui.input

import xyz.sleekstats.wheelapp.model.WheelChoice

class InputPresenter(inputView: InputContract.View) :
    InputContract.Presenter {

    private val wheelChoiceDatabase = inputView.provideDatabase()
    private val wheelChoiceDAO = wheelChoiceDatabase.wheelChoiceDao()

    override fun insertWheelChoices(wheelChoices: List<WheelChoice>): Int {
        wheelChoiceDAO.insertAll(wheelChoices)
        return wheelChoices.filter { it.text.isNotEmpty() }.size
    }

    override suspend fun getWheelChoices(): List<WheelChoice> {
        return wheelChoiceDAO.getAllWheelChoices()
    }
}