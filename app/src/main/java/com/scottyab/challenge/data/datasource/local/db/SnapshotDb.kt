package com.scottyab.challenge.data.datasource.local.db

import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.Insert
import androidx.room.OnConflictStrategy.Companion.ABORT
import androidx.room.PrimaryKey
import androidx.room.Query
import com.scottyab.challenge.data.extensions.nowUTC
import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime

@Entity(
    tableName = "Snapshot",
    indices = [Index("created_at"), Index("activity_id")],
    foreignKeys = [
        ForeignKey(
            entity = ActivityDb::class,
            parentColumns = ["id"],
            childColumns = ["activity_id"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class SnapshotDb(
    @PrimaryKey
    val id: String,
    @ColumnInfo(name = "activity_id")
    val activityId: String,
    val photoId: String,
    @ColumnInfo(name = "image_url")
    val imageUrl: String,
    val title: String,
    @ColumnInfo(name = "created_at")
    val createdAt: LocalDateTime = nowUTC(),
    val latitude: String,
    val longitude: String
)

@Dao
interface SnapshotDao {

    @Query("select * from Snapshot WHERE activity_id=:activityId ORDER BY datetime(Snapshot.created_at) DESC")
    fun observeSnapshots(activityId: String): Flow<List<SnapshotDb>>

    @Query("select * from Snapshot WHERE activity_id=:activityId ORDER BY photoId")
    fun getSnapshots(activityId: String): List<SnapshotDb>

    @Insert(onConflict = ABORT)
    suspend fun insert(snapshot: SnapshotDb)

    @Query("DELETE FROM Snapshot WHERE activity_id=:activityId")
    suspend fun deleteAllRows(activityId: String)
}
