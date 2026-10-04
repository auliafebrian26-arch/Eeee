package com.example.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface PtmDao {

    @Query("SELECT * FROM ptm_records ORDER BY examinationDate DESC, id DESC")
    fun getAllRecords(): Flow<List<PtmRecord>>

    @Query("SELECT * FROM ptm_records WHERE id = :id LIMIT 1")
    fun getRecordById(id: Long): Flow<PtmRecord?>

    @Query("SELECT * FROM ptm_records WHERE fullName LIKE '%' || :query || '%' OR nik LIKE '%' || :query || '%' OR examinationDate LIKE '%' || :query || '%' ORDER BY examinationDate DESC, id DESC")
    fun searchRecords(query: String): Flow<List<PtmRecord>>

    @Query("SELECT * FROM ptm_records WHERE examinationDate LIKE :monthPrefix || '%' ORDER BY examinationDate DESC, id DESC")
    fun getRecordsByMonth(monthPrefix: String): Flow<List<PtmRecord>>

    @Query("SELECT * FROM ptm_records WHERE isHypertension = 1 ORDER BY examinationDate DESC")
    fun getHypertensionRecords(): Flow<List<PtmRecord>>

    @Query("SELECT * FROM ptm_records WHERE isDiabetes = 1 ORDER BY examinationDate DESC")
    fun getDiabetesRecords(): Flow<List<PtmRecord>>

    @Query("SELECT * FROM ptm_records WHERE isCentralObesity = 1 OR bmiCategory = 'Obesitas' ORDER BY examinationDate DESC")
    fun getObesityRecords(): Flow<List<PtmRecord>>

    @Query("SELECT * FROM ptm_records WHERE needsReferral = 1 ORDER BY examinationDate DESC")
    fun getReferralRecords(): Flow<List<PtmRecord>>

    @Query("SELECT * FROM ptm_records WHERE examinationDate LIKE :monthPrefix || '%' ORDER BY examinationDate ASC, id ASC")
    suspend fun getRecordsListByMonthSync(monthPrefix: String): List<PtmRecord>

    @Query("SELECT * FROM ptm_records ORDER BY examinationDate ASC, id ASC")
    suspend fun getAllRecordsSync(): List<PtmRecord>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecord(record: PtmRecord): Long

    @Update
    suspend fun updateRecord(record: PtmRecord)

    @Delete
    suspend fun deleteRecord(record: PtmRecord)

    @Query("DELETE FROM ptm_records WHERE id = :id")
    suspend fun deleteRecordById(id: Long)
}
