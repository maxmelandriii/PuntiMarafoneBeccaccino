package com.maxmelandriii.contapuntimarafone.data.local

import androidx.room.Dao
import androidx.room.Database
import androidx.room.Delete
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Update
import androidx.room.RoomDatabase
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "partite_cronologia")
data class PartitaEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val nomeNoi: String,
    val nomeVoi: String,
    val puntiNoi: Int,
    val puntiVoi: Int,
    val vincitore: String,
    val dataPartita: String,
    val inCorso: Boolean,
    val sogliaVittoria: Int = 41
)

@Dao
interface PartitaDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPartita(partita: PartitaEntity): Long

    @Update
    suspend fun updatePartita(partita: PartitaEntity)

    @Delete
    suspend fun deletePartita(partita: PartitaEntity)

    @Query("SELECT * FROM partite_cronologia WHERE id = :id LIMIT 1")
    suspend fun getPartitaById(id: Int): PartitaEntity?

    @Query("SELECT * FROM partite_cronologia WHERE inCorso = 1 ORDER BY id DESC LIMIT 1")
    suspend fun getPartitaInCorso(): PartitaEntity?

    @Query("SELECT * FROM partite_cronologia ORDER BY id DESC")
    fun getAllPartite(): Flow<List<PartitaEntity>>

    @Query("DELETE FROM partite_cronologia")
    suspend fun svuotaCronologia()
}

@Database(entities = [PartitaEntity::class], version = 3, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun partitaDao(): PartitaDao
}
