package xyz.sleekstats.wheelapp.db

import android.content.Context
import android.graphics.Color
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import xyz.sleekstats.wheelapp.model.WheelChoice

@Database(entities = [WheelChoice::class], version = 1)
abstract class WheelChoiceDatabase : RoomDatabase() {

    abstract fun wheelChoiceDao(): WheelChoiceDAO

    companion object {
        private val PREPOPULATE_DATA = listOf(
            WheelChoice(text = "CHOICE GREEN", colorIndex = Color.GREEN),
            WheelChoice(text = "CHOICE RED", colorIndex = Color.RED),
            WheelChoice(text = "CHOICE BLUE", colorIndex = Color.BLUE),
            WheelChoice(text = "CHOICE YELLOW", colorIndex = Color.YELLOW),
            WheelChoice(text = "CHOICE DEFAULT")
        )

        private var INSTANCE: WheelChoiceDatabase? = null

        fun getDatabase(context: Context): WheelChoiceDatabase {
            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }
            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    WheelChoiceDatabase::class.java,
                    "wheel_choice_database"
                ).addCallback(object : Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        // moving to a new thread
                        GlobalScope.launch {
                            INSTANCE?.wheelChoiceDao()?.insertAll(PREPOPULATE_DATA)
                        }
                    }
                }).build()
                INSTANCE = instance
                return instance
            }
        }
    }
}
