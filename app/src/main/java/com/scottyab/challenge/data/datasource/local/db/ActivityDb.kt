package com.scottyab.challenge.data.datasource.local.db

import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.Insert
import androidx.room.OnConflictStrategy.Companion.ABORT
import androidx.room.OnConflictStrategy.Companion.REPLACE
import androidx.room.PrimaryKey
import androidx.room.Query
import com.scottyab.challenge.data.extensions.nowUTC
import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime

@Entity(
    tableName = "Activity",
    indices = [Index(value = ["created_at", "id"])]
)
data class ActivityDb(
    @PrimaryKey
    val id: String,
    val title: String,
    @ColumnInfo(name = "created_at")
    val createdAt: LocalDateTime = nowUTC(),
    @ColumnInfo(name = "finished_at")
    val finishedAt: LocalDateTime?
)

/**
 * This note is for reviewers typically I would of discussed this with team prior to implementation
 *
 * The goal here is to store which activity is actively recording (one and only one)
 * I was torn between having an extra `activity.is_active` column and seperate table.
 * Either way we'd need an transaction to clear active flag and then set (to ensure only one)
 * With a extra column as the number of activities grew so to would the processing needed.
 * Where as for a seperate table is would only ever be a single row and delete and inset SQL is
 * simple.
 *
 * It might be premature optimisation, but I felt it didn't add extra complexity given this extra
 * is handled by the ActivityDao.
 *
 * Having the ForeignKey relationship ensures we have a valid activity_id and that it's deleted
 * if the activity is deleted while in active state.
 *
 */
@Entity(
    tableName = "CurrentActivity",
    indices = [Index("activity_id")],
    foreignKeys = [
        ForeignKey(
            entity = ActivityDb::class,
            parentColumns = ["id"],
            childColumns = ["activity_id"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class CurrentActivityDb(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    @ColumnInfo(name = "activity_id")
    val activityId: String
)

@Dao
interface ActivityDao {

    @Query("select * from Activity ORDER BY datetime(Activity.created_at) DESC")
    fun observeActivities(): Flow<List<ActivityDb>>

    @Insert(onConflict = ABORT)
    suspend fun insert(activityDb: ActivityDb)

    @Query("UPDATE Activity SET finished_at = :finishedAt WHERE id = :activityId  ")
    suspend fun finish(activityId: String, finishedAt: LocalDateTime)

    @Query("DELETE FROM Activity WHERE id = :activityId")
    suspend fun delete(activityId: String)

    @Query("DELETE FROM Activity")
    suspend fun deleteAllRows()

    @Query("select * from CurrentActivity")
    fun getCurrentActivity(): List<CurrentActivityDb>

    @Query("DELETE from CurrentActivity")
    fun clearCurrentActivity()

    @Insert(onConflict = REPLACE)
    fun startActivity(currentActivityDb: CurrentActivityDb)
}
