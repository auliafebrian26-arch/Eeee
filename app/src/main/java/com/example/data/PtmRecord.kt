package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "ptm_records")
data class PtmRecord(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    
    // Petugas & Pemeriksaan
    val petugasName: String,
    val examinationDate: String, // Format: YYYY-MM-DD
    val posyanduName: String = "Posyandu Usia Produktif",
    
    // Identitas Warga (Usia Produktif 18-59 Tahun)
    val nik: String,
    val fullName: String,
    val gender: String, // "Laki-laki" atau "Perempuan"
    val age: Int, // 18 - 59 tahun
    val maritalStatus: String, // "Belum Menikah", "Menikah", "Janda/Duda"
    val occupation: String,
    val phoneNumber: String = "",
    val address: String = "",
    
    // Faktor Resiko PTM
    val isSmoking: Boolean,
    val cigarettesPerDay: Int = 0,
    val doesPhysicalActivity: Boolean, // Aktivitas fisik cukup (>= 150 mnt/mgg atau >= 30 mnt/hari)
    val consumesFruitVeggie: Boolean = true, // Cukup konsumsi sayur/buah
    val familyHistoryPtm: Boolean = false, // Riwayat PTM keluarga (Hipertensi/DM/Jantung/Stroke)
    val personalHistoryPtm: Boolean = false, // Riwayat PTM sebelumnya
    
    // Antropometri
    val weightKg: Double,
    val heightCm: Double,
    val bmi: Double,
    val bmiCategory: String, // "Sangat Kurus", "Kurus", "Normal", "Gemuk", "Obesitas"
    val waistCircumferenceCm: Double,
    val isCentralObesity: Boolean, // Pria > 90cm, Wanita > 80cm
    
    // Tekanan Darah (Pemeriksaan Hipertensi)
    val systolicBp: Int,
    val diastolicBp: Int,
    val bloodPressureCategory: String, // "Normal", "Pre-Hipertensi", "Hipertensi Derajat 1", "Hipertensi Derajat 2"
    val isHypertension: Boolean,
    
    // Gula Darah (Pemeriksaan Diabetes Melitus)
    val bloodGlucoseType: String, // "GDP" (Gula Darah Puasa) atau "GDS" (Gula Darah Sewaktu)
    val bloodGlucoseValue: Int, // mg/dL
    val bloodGlucoseCategory: String, // "Normal", "Pre-Diabetes", "Diabetes Melitus"
    val isDiabetes: Boolean,
    
    // Nilai Laboratorium Tambahan (Opsional)
    val cholesterolValue: Int? = null, // mg/dL (Normal < 200)
    val uricAcidValue: Double? = null, // mg/dL (Pria: 3.4-7.0, Wanita: 2.4-5.7)
    
    // Evaluasi Klinis & Tindak Lanjut Puskesmas
    val ptmRiskLevel: String, // "Rendah", "Sedang", "Tinggi"
    val needsReferral: Boolean, // Rujuk ke Puskesmas
    val clinicalDiagnosisSummary: String,
    val recommendation: String,
    val notes: String = "",
    
    val createdAt: Long = System.currentTimeMillis()
)
