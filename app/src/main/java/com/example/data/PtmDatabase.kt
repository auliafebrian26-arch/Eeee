package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(entities = [PtmRecord::class], version = 1, exportSchema = false)
abstract class PtmDatabase : RoomDatabase() {

    abstract fun ptmDao(): PtmDao

    companion object {
        @Volatile
        private var INSTANCE: PtmDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope = CoroutineScope(Dispatchers.IO)): PtmDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    PtmDatabase::class.java,
                    "si_ptm_posyandu.db"
                )
                    .addCallback(DatabaseCallback(scope))
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback(
            private val scope: CoroutineScope
        ) : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    scope.launch {
                        populateInitialSampleData(database.ptmDao())
                    }
                }
            }
        }

        private suspend fun populateInitialSampleData(dao: PtmDao) {
            val sampleRecords = listOf(
                PtmRecord(
                    petugasName = "Bdn. Siti Rahmawati, S.Tr.Keb",
                    examinationDate = "2026-10-02",
                    posyanduName = "Posyandu Melati 03",
                    nik = "3201124508820003",
                    fullName = "Bambang Supriyadi",
                    gender = "Laki-laki",
                    age = 44,
                    maritalStatus = "Menikah",
                    occupation = "Karyawan Swasta",
                    phoneNumber = "081234567891",
                    address = "RT 02 / RW 05 Ds. Sukamaju",
                    isSmoking = true,
                    cigarettesPerDay = 12,
                    doesPhysicalActivity = false,
                    consumesFruitVeggie = false,
                    familyHistoryPtm = true,
                    personalHistoryPtm = true,
                    weightKg = 78.5,
                    heightCm = 168.0,
                    bmi = 27.8,
                    bmiCategory = "Obesitas",
                    waistCircumferenceCm = 94.0,
                    isCentralObesity = true,
                    systolicBp = 155,
                    diastolicBp = 95,
                    bloodPressureCategory = "Hipertensi Derajat 1",
                    isHypertension = true,
                    bloodGlucoseType = "GDS",
                    bloodGlucoseValue = 185,
                    bloodGlucoseCategory = "Pre-Diabetes (TGT)",
                    isDiabetes = false,
                    cholesterolValue = 220,
                    uricAcidValue = 7.4,
                    ptmRiskLevel = "Tinggi",
                    needsReferral = true,
                    clinicalDiagnosisSummary = "Hipertensi Derajat 1, Pre-Diabetes, Obesitas Sentral, Perokok Aktif",
                    recommendation = "• SEGERA RUJUK KE PUSKESMAS untuk penegakan diagnosis & tata laksana medis lebih lanjut.\n• Batasi konsumsi garam maksimal 1 sdt/hari.\n• Ikuti konseling Berhenti Merokok di Puskesmas.\n• Terapkan pola CERDIK.",
                    notes = "Pasien mengeluh tengkuk terasa berat sejak 3 hari."
                ),
                PtmRecord(
                    petugasName = "Bdn. Siti Rahmawati, S.Tr.Keb",
                    examinationDate = "2026-10-02",
                    posyanduName = "Posyandu Melati 03",
                    nik = "3201126105880004",
                    fullName = "Sri Wahyuningsih",
                    gender = "Perempuan",
                    age = 38,
                    maritalStatus = "Menikah",
                    occupation = "Ibu Rumah Tangga",
                    phoneNumber = "081398765432",
                    address = "RT 01 / RW 05 Ds. Sukamaju",
                    isSmoking = false,
                    cigarettesPerDay = 0,
                    doesPhysicalActivity = true,
                    consumesFruitVeggie = true,
                    familyHistoryPtm = false,
                    personalHistoryPtm = false,
                    weightKg = 54.0,
                    heightCm = 158.0,
                    bmi = 21.6,
                    bmiCategory = "Normal",
                    waistCircumferenceCm = 72.0,
                    isCentralObesity = false,
                    systolicBp = 115,
                    diastolicBp = 75,
                    bloodPressureCategory = "Normal",
                    isHypertension = false,
                    bloodGlucoseType = "GDP",
                    bloodGlucoseValue = 88,
                    bloodGlucoseCategory = "Normal",
                    isDiabetes = false,
                    cholesterolValue = 175,
                    uricAcidValue = 4.2,
                    ptmRiskLevel = "Rendah",
                    needsReferral = false,
                    clinicalDiagnosisSummary = "Semua parameter dalam batas normal",
                    recommendation = "• Pertahankan pola hidup sehat CERDIK.\n• Cek kesehatan berkala setiap 6 bulan di Posyandu/Puskesmas.",
                    notes = "Kondisi sehat prima."
                ),
                PtmRecord(
                    petugasName = "Ahmad Zulkarnain, S.Kep",
                    examinationDate = "2026-10-03",
                    posyanduName = "Posyandu Mawar 01",
                    nik = "3201121402750001",
                    fullName = "H. Hendra Gunawan",
                    gender = "Laki-laki",
                    age = 51,
                    maritalStatus = "Menikah",
                    occupation = "Wiraswasta",
                    phoneNumber = "085211223344",
                    address = "RT 04 / RW 02 Ds. Sukamaju",
                    isSmoking = false,
                    cigarettesPerDay = 0,
                    doesPhysicalActivity = false,
                    consumesFruitVeggie = false,
                    familyHistoryPtm = true,
                    personalHistoryPtm = false,
                    weightKg = 82.0,
                    heightCm = 165.0,
                    bmi = 30.1,
                    bmiCategory = "Obesitas",
                    waistCircumferenceCm = 98.0,
                    isCentralObesity = true,
                    systolicBp = 165,
                    diastolicBp = 105,
                    bloodPressureCategory = "Hipertensi Derajat 2",
                    isHypertension = true,
                    bloodGlucoseType = "GDP",
                    bloodGlucoseValue = 210,
                    bloodGlucoseCategory = "Diabetes Melitus (DM)",
                    isDiabetes = true,
                    cholesterolValue = 245,
                    uricAcidValue = 8.1,
                    ptmRiskLevel = "Tinggi",
                    needsReferral = true,
                    clinicalDiagnosisSummary = "Hipertensi Derajat 2, Diabetes Melitus, Obesitas Sentral, Dislipidemia",
                    recommendation = "• SEGERA RUJUK KE PUSKESMAS untuk penanganan darurat Hipertensi & Diabetes.\n• Diet rendah gula dan rendah garam ketat.\n• Aktivitas fisik ringan bertahap.",
                    notes = "Diberikan surat pengantar rujukan PTM ke Puskesmas Induk."
                ),
                PtmRecord(
                    petugasName = "Ahmad Zulkarnain, S.Kep",
                    examinationDate = "2026-10-03",
                    posyanduName = "Posyandu Mawar 01",
                    nik = "3201125203990002",
                    fullName = "Dina Maharani",
                    gender = "Perempuan",
                    age = 27,
                    maritalStatus = "Belum Menikah",
                    occupation = "Guru Honorer",
                    phoneNumber = "087811992288",
                    address = "RT 03 / RW 01 Ds. Sukamaju",
                    isSmoking = false,
                    cigarettesPerDay = 0,
                    doesPhysicalActivity = true,
                    consumesFruitVeggie = true,
                    familyHistoryPtm = false,
                    personalHistoryPtm = false,
                    weightKg = 62.0,
                    heightCm = 155.0,
                    bmi = 25.8,
                    bmiCategory = "Gemuk (Overweight)",
                    waistCircumferenceCm = 83.0,
                    isCentralObesity = true,
                    systolicBp = 125,
                    diastolicBp = 82,
                    bloodPressureCategory = "Pre-Hipertensi",
                    isHypertension = false,
                    bloodGlucoseType = "GDS",
                    bloodGlucoseValue = 110,
                    bloodGlucoseCategory = "Normal",
                    isDiabetes = false,
                    cholesterolValue = 188,
                    uricAcidValue = 4.8,
                    ptmRiskLevel = "Sedang",
                    needsReferral = false,
                    clinicalDiagnosisSummary = "Pre-Hipertensi, Overweight, Obesitas Sentral",
                    recommendation = "• Edukasi modifikasi gaya hidup sehat (CERDIK).\n• Kontrol berat badan dan kurangi makanan asin/berminyak.\n• Cek ulang 1 bulan lagi.",
                    notes = "Edukasi gizi seimbang telah diberikan."
                )
            )

            for (record in sampleRecords) {
                dao.insertRecord(record)
            }
        }
    }
}
