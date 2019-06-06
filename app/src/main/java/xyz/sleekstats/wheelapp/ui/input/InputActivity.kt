package xyz.sleekstats.wheelapp.ui.input

import android.content.Intent
import android.graphics.Color
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_input.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import xyz.sleekstats.wheelapp.R
import xyz.sleekstats.wheelapp.db.WheelChoiceDatabase
import xyz.sleekstats.wheelapp.model.WheelChoice
import xyz.sleekstats.wheelapp.ui.wheel.WheelActivity
import kotlin.random.Random

class InputActivity : AppCompatActivity(), InputContract.View {

    private lateinit var inputPresenter: InputPresenter

    private val dummyColors = listOf(
        Color.MAGENTA,
        Color.YELLOW,
        Color.GREEN,
        Color.RED,
        Color.BLUE,
        Color.GRAY,
        Color.CYAN
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_input)

        inputPresenter = InputPresenter(this)

        CoroutineScope(Dispatchers.Main).launch {
            val wheelChoices = withContext(Dispatchers.IO) { inputPresenter.getWheelChoices() }

            wheelChoices.forEach {
                when (it.id.toInt()) {
                    0 -> option1_edit_text.setText(it.text)
                    1 -> option2_edit_text.setText(it.text)
                    2 -> option3_edit_text.setText(it.text)
                    3 -> option4_edit_text.setText(it.text)
                    4 -> option5_edit_text.setText(it.text)
                }
            }

            save_choices_button.setOnClickListener {
                val choices: List<WheelChoice> = listOf(
                    createWheelChoice(0, option1_edit_text.text.toString()),
                    createWheelChoice(1, option2_edit_text.text.toString()),
                    createWheelChoice(2, option3_edit_text.text.toString()),
                    createWheelChoice(3, option4_edit_text.text.toString()),
                    createWheelChoice(4, option5_edit_text.text.toString())
                )
                insertAll(choices)
            }
        }
    }

    private fun createWheelChoice(id: Long, text: String) = WheelChoice(
        id = id, text = text, colorIndex = dummyColors[Random.nextInt(dummyColors.size)]
    )

    private fun insertAll(choices: List<WheelChoice>) =
        CoroutineScope(Dispatchers.Main).launch {
            var numberOfChoicesEntered = 0
            withContext(Dispatchers.IO) {
                numberOfChoicesEntered = inputPresenter.insertWheelChoices(choices)
            }
            if(numberOfChoicesEntered > 1) {
                goToWheelActivity()
            } else {
                Toast.makeText(this@InputActivity, "Please enter at least 2 wheel choices!", Toast.LENGTH_SHORT).show()
            }
        }

    override fun goToWheelActivity() {
        val newActivityIntent = Intent(this, WheelActivity::class.java)
        startActivity(newActivityIntent)
    }

    override fun provideDatabase(): WheelChoiceDatabase {
        return WheelChoiceDatabase.getDatabase(applicationContext)
    }
}

