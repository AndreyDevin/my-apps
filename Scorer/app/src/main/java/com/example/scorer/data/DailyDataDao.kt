package com.example.scorer.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface DailyDataDao {
    @Insert(
        entity = Day::class,
        onConflict = OnConflictStrategy.REPLACE
    )
    suspend fun insert(day: Day)

    @Query("SELECT * FROM dailyDB")
    fun getAll(): Flow<List<Day>>

    @Delete
    suspend fun delete(day: Day)

    @Query("UPDATE dailyDB SET durations = durations + :addedTime WHERE dayKey =:day")
    suspend fun update(day: Long, addedTime: Long)

    @Query("DELETE FROM dailyDB")
    suspend fun deleteAll()

    @Query("DELETE FROM dailyDB WHERE dayKey = :dayKey")
    suspend fun deleteDay(dayKey: Long)
}