package xyz.sleekstats.wheelapp.db

import androidx.room.*
import xyz.sleekstats.wheelapp.model.WheelChoice

@Dao
interface WheelChoiceDAO {

    @Query("SELECT * from wheel_choice_table")
    fun getAllWheelChoices(): List<WheelChoice>

    @Query("SELECT * from wheel_choice_table WHERE id = :wheelChoiceID")
    fun getWheelChoice(wheelChoiceID: Int): WheelChoice

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(wheelChoice: WheelChoice)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(wheelChoices: List<WheelChoice>)

    @Update
    fun update(wheelChoice: WheelChoice)
}