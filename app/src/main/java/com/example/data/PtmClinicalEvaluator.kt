package com.example.data

import java.util.Locale

object PtmClinicalEvaluator {

    /**
     * Hitung Indeks Massa Tubuh (IMT / BMI)
     * Rumus: BB (kg) / (TB (m) * TB (m))
     */
    fun calculateBmi(weightKg: Double, heightCm: Double): Double {
        if (heightCm <= 0.0 || weightKg <= 0.0) return 0.0
        val heightM = heightCm / 100.0
        val bmi = weightKg / (heightM * heightM)
        return String.format(Locale.US, "%.1f", bmi).toDoubleOrNull() ?: 0.0
    }

    /**
     * Klasifikasi IMT Standar Kemenkes RI (Asia Pasifik):
     * < 17.0 : Sangat Kurus (Kekurangan BB Tingkat Berat)
     * 17.0 - 18.4 : Kurus (Kekurangan BB Tingkat Ringan)
     * 18.5 - 25.0 : Normal
     * 25.1 - 27.0 : Gemuk (Overweight)
     * > 27.0 : Obesitas
     */
    fun classifyBmi(bmi: Double): String {
        return when {
            bmi <= 0.0 -> "-"
            bmi < 17.0 -> "Sangat Kurus"
            bmi <= 18.4 -> "Kurus"
            bmi <= 25.0 -> "Normal"
            bmi <= 27.0 -> "Gemuk (Overweight)"
            else -> "Obesitas"
        }
    }

    /**
     * Klasifikasi Obesitas Sentral Kemenkes RI:
     * Laki-laki: Lingkar Perut > 90 cm
     * Perempuan: Lingkar Perut > 80 cm
     */
    fun isCentralObesity(waistCm: Double, gender: String): Boolean {
        if (waistCm <= 0.0) return false
        return if (gender.equals("Laki-laki", ignoreCase = true)) {
            waistCm > 90.0
        } else {
            waistCm > 80.0
        }
    }

    /**
     * Klasifikasi Tekanan Darah (JNC 7 / Kemenkes RI):
     * Normal: Sistolik < 120 dan Diastolik < 80
     * Pre-Hipertensi: Sistolik 120-139 atau Diastolik 80-89
     * Hipertensi Derajat 1: Sistolik 140-159 atau Diastolik 90-99
     * Hipertensi Derajat 2: Sistolik >= 160 atau Diastolik >= 100
     * Krisis Hipertensi: Sistolik >= 180 atau Diastolik >= 120
     */
    fun classifyBloodPressure(systolic: Int, diastolic: Int): Pair<String, Boolean> {
        if (systolic <= 0 || diastolic <= 0) return Pair("-", false)
        return when {
            systolic >= 180 || diastolic >= 120 -> Pair("Krisis Hipertensi", true)
            systolic >= 160 || diastolic >= 100 -> Pair("Hipertensi Derajat 2", true)
            systolic >= 140 || diastolic >= 90 -> Pair("Hipertensi Derajat 1", true)
            systolic >= 120 || diastolic >= 80 -> Pair("Pre-Hipertensi", false)
            else -> Pair("Normal", false)
        }
    }

    /**
     * Klasifikasi Gula Darah Kemenkes / PERKENI:
     * Jika Gula Darah Sewaktu (GDS):
     * < 140 mg/dL : Normal
     * 140 - 199 mg/dL : Pre-Diabetes (Toleransi Glukosa Terganggu)
     * >= 200 mg/dL : Diabetes Melitus (DM)
     *
     * Jika Gula Darah Puasa (GDP):
     * < 100 mg/dL : Normal
     * 100 - 125 mg/dL : Pre-Diabetes (Gula Darah Puasa Terganggu)
     * >= 126 mg/dL : Diabetes Melitus (DM)
     */
    fun classifyBloodGlucose(type: String, value: Int): Pair<String, Boolean> {
        if (value <= 0) return Pair("-", false)
        return if (type.equals("GDP", ignoreCase = true)) {
            when {
                value < 100 -> Pair("Normal", false)
                value <= 125 -> Pair("Pre-Diabetes (GDP Terganggu)", false)
                else -> Pair("Diabetes Melitus (DM)", true)
            }
        } else {
            when {
                value < 140 -> Pair("Normal", false)
                value <= 199 -> Pair("Pre-Diabetes (TGT)", false)
                else -> Pair("Diabetes Melitus (DM)", true)
            }
        }
    }

    /**
     * Evaluasi Tingkat Resiko PTM, Ringkasan Diagnosis, dan Tindak Lanjut Puskesmas
     */
    fun evaluatePtmRisk(
        isHypertension: Boolean,
        bpCategory: String,
        isDiabetes: Boolean,
        glucoseCategory: String,
        isCentralObesity: Boolean,
        bmiCategory: String,
        isSmoking: Boolean,
        doesPhysicalActivity: Boolean,
        familyHistory: Boolean
    ): Triple<String, Boolean, String> {
        val findings = mutableListOf<String>()

        if (isHypertension) {
            findings.add(bpCategory)
        } else if (bpCategory == "Pre-Hipertensi") {
            findings.add("Pre-Hipertensi")
        }

        if (isDiabetes) {
            findings.add("Suspek DM")
        } else if (glucoseCategory.startsWith("Pre-Diabetes")) {
            findings.add("Pre-Diabetes")
        }

        if (isCentralObesity) {
            findings.add("Obesitas Sentral")
        }

        if (bmiCategory == "Obesitas") {
            findings.add("IMT Obesitas")
        } else if (bmiCategory.startsWith("Gemuk")) {
            findings.add("Overweight")
        }

        if (isSmoking) {
            findings.add("Perokok Aktif")
        }

        if (!doesPhysicalActivity) {
            findings.add("Kurang Aktivitas Fisik")
        }

        val riskLevel: String
        val needsReferral: Boolean

        if (isHypertension || isDiabetes || bpCategory == "Krisis Hipertensi") {
            riskLevel = "Tinggi"
            needsReferral = true
        } else if (findings.size >= 2 || bpCategory == "Pre-Hipertensi" || glucoseCategory.startsWith("Pre-Diabetes") || isCentralObesity) {
            riskLevel = "Sedang"
            needsReferral = false
        } else {
            riskLevel = "Rendah"
            needsReferral = false
        }

        val summary = if (findings.isEmpty()) {
            "Semua parameter dalam batas normal"
        } else {
            findings.joinToString(", ")
        }

        return Triple(riskLevel, needsReferral, summary)
    }

    /**
     * Saran & Rekomendasi Terpadu Berdasarkan Hasil Skrining
     */
    fun generateRecommendation(
        riskLevel: String,
        needsReferral: Boolean,
        isHypertension: Boolean,
        isDiabetes: Boolean,
        isSmoking: Boolean,
        isCentralObesity: Boolean,
        doesPhysicalActivity: Boolean
    ): String {
        val recs = mutableListOf<String>()

        if (needsReferral) {
            recs.add("SEGERA RUJUK KE PUSKESMAS untuk penegakan diagnosis & tata laksana medis lebih lanjut.")
        }

        if (isHypertension) {
            recs.add("Batasi konsumsi garam dapur maksimal 1 sendok teh (5 gram/hari), pantau tensi rutin tiap 2 minggu.")
        }

        if (isDiabetes) {
            recs.add("Batasi konsumsi gula maksimal 4 sendok makan/hari, hindari minuman manis berenergi/kemasan.")
        }

        if (isCentralObesity) {
            recs.add("Terapkan diet seimbang isi piringku (1/2 piring sayur & buah, 1/4 karbohidrat, 1/4 lauk pauk).")
        }

        if (isSmoking) {
            recs.add("Ikuti konseling Berhenti Merokok (KBM) di Puskesmas, kurangi rokok bertahap hingga berhenti total.")
        }

        if (!doesPhysicalActivity) {
            recs.add("Tingkatkan aktivitas fisik minimal 30 menit setiap hari atau 150 menit per minggu (jalan cepat, senam, bersepeda).")
        }

        recs.add("Terapkan perilaku CERDIK: Cek kesehatan berkala, Enyahkan asap rokok, Rajin aktivitas fisik, Diet seimbang, Istirahat cukup (7-8 jam), Kelola stres.")

        return recs.joinToString("\n• ", prefix = "• ")
    }
}
