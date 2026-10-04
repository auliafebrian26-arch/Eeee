package com.example.data

import kotlinx.coroutines.flow.Flow

class PtmRepository(private val ptmDao: PtmDao) {

    val allRecords: Flow<List<PtmRecord>> = ptmDao.getAllRecords()

    val hypertensionRecords: Flow<List<PtmRecord>> = ptmDao.getHypertensionRecords()

    val diabetesRecords: Flow<List<PtmRecord>> = ptmDao.getDiabetesRecords()

    val obesityRecords: Flow<List<PtmRecord>> = ptmDao.getObesityRecords()

    val referralRecords: Flow<List<PtmRecord>> = ptmDao.getReferralRecords()

    fun getRecordById(id: Long): Flow<PtmRecord?> = ptmDao.getRecordById(id)

    fun search(query: String): Flow<List<PtmRecord>> = ptmDao.searchRecords(query)

    fun getRecordsByMonth(monthPrefix: String): Flow<List<PtmRecord>> = ptmDao.getRecordsByMonth(monthPrefix)

    suspend fun getRecordsListByMonthSync(monthPrefix: String): List<PtmRecord> =
        ptmDao.getRecordsListByMonthSync(monthPrefix)

    suspend fun getAllRecordsSync(): List<PtmRecord> = ptmDao.getAllRecordsSync()

    suspend fun insert(record: PtmRecord): Long = ptmDao.insertRecord(record)

    suspend fun update(record: PtmRecord) = ptmDao.updateRecord(record)

    suspend fun delete(record: PtmRecord) = ptmDao.deleteRecord(record)

    suspend fun deleteById(id: Long) = ptmDao.deleteRecordById(id)
}
