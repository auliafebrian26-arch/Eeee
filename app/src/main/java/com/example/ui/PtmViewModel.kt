package com.example.ui

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.PtmClinicalEvaluator
import com.example.data.PtmDatabase
import com.example.data.PtmRecord
import com.example.data.PtmRepository
import com.example.util.ExportManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class PtmViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: PtmRepository
    val allRecords: StateFlow<List<PtmRecord>>

    // Filter & Pencarian
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedFilter = MutableStateFlow("Semua") // "Semua", "Hipertensi", "Diabetes", "Obesitas", "Rujuk"
    val selectedFilter: StateFlow<String> = _selectedFilter.asStateFlow()

    private val _selectedMonth = MutableStateFlow("Semua") // "Semua" atau "YYYY-MM"
    val selectedMonth: StateFlow<String> = _selectedMonth.asStateFlow()

    // Status Export Terakhir
    private val _lastExportedFile = MutableStateFlow<File?>(null)
    val lastExportedFile: StateFlow<File?> = _lastExportedFile.asStateFlow()

    private val _exportType = MutableStateFlow<String?>(null) // "excel" atau "pdf"
    val exportType: StateFlow<String?> = _exportType.asStateFlow()

    // State Form Skrining Baru
    var editingRecordId: Long? = null
    val formPetugasName = MutableStateFlow("Bdn. Siti Rahmawati, S.Tr.Keb")
    val formExaminationDate = MutableStateFlow(SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date()))
    val formPosyanduName = MutableStateFlow("Posyandu Melati")
    val formNik = MutableStateFlow("")
    val formFullName = MutableStateFlow("")
    val formGender = MutableStateFlow("Perempuan") // "Laki-laki" atau "Perempuan"
    val formAge = MutableStateFlow("35")
    val formMaritalStatus = MutableStateFlow("Menikah") // "Belum Menikah", "Menikah", "Janda/Duda"
    val formOccupation = MutableStateFlow("Ibu Rumah Tangga")
    val formPhoneNumber = MutableStateFlow("")
    val formAddress = MutableStateFlow("")

    val formIsSmoking = MutableStateFlow(false)
    val formCigarettesPerDay = MutableStateFlow("0")
    val formDoesPhysicalActivity = MutableStateFlow(true)
    val formConsumesFruitVeggie = MutableStateFlow(true)
    val formFamilyHistoryPtm = MutableStateFlow(false)
    val formPersonalHistoryPtm = MutableStateFlow(false)

    val formWeightKg = MutableStateFlow("60.0")
    val formHeightCm = MutableStateFlow("160.0")
    val formWaistCircumferenceCm = MutableStateFlow("78.0")

    val formSystolicBp = MutableStateFlow("120")
    val formDiastolicBp = MutableStateFlow("80")

    val formBloodGlucoseType = MutableStateFlow("GDS") // "GDS" atau "GDP"
    val formBloodGlucoseValue = MutableStateFlow("115")

    val formCholesterolValue = MutableStateFlow("")
    val formUricAcidValue = MutableStateFlow("")
    val formNotes = MutableStateFlow("")

    val formErrorMessage = MutableStateFlow<String?>(null)

    init {
        val database = PtmDatabase.getDatabase(application, viewModelScope)
        repository = PtmRepository(database.ptmDao())

        allRecords = repository.allRecords.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
    }

    // List terfilter yang di-observe UI
    val filteredRecords: StateFlow<List<PtmRecord>> = combine(
        allRecords,
        _searchQuery,
        _selectedFilter,
        _selectedMonth
    ) { records, query, filter, month ->
        records.filter { record ->
            val matchesQuery = query.isBlank() ||
                    record.fullName.contains(query, ignoreCase = true) ||
                    record.nik.contains(query, ignoreCase = true) ||
                    record.examinationDate.contains(query, ignoreCase = true) ||
                    record.petugasName.contains(query, ignoreCase = true)

            val matchesFilter = when (filter) {
                "Hipertensi" -> record.isHypertension
                "Diabetes" -> record.isDiabetes
                "Obesitas" -> record.isCentralObesity || record.bmiCategory == "Obesitas"
                "Rujuk" -> record.needsReferral
                else -> true
            }

            val matchesMonth = if (month == "Semua" || month.isBlank()) {
                true
            } else {
                record.examinationDate.startsWith(month)
            }

            matchesQuery && matchesFilter && matchesMonth
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setSelectedFilter(filter: String) {
        _selectedFilter.value = filter
    }

    fun setSelectedMonth(month: String) {
        _selectedMonth.value = month
    }

    fun clearLastExport() {
        _lastExportedFile.value = null
        _exportType.value = null
    }

    // Perhitungan kalkulasi klinis form secara real-time
    fun getCalculatedBmi(): Double {
        val weight = formWeightKg.value.toDoubleOrNull() ?: 0.0
        val height = formHeightCm.value.toDoubleOrNull() ?: 0.0
        return PtmClinicalEvaluator.calculateBmi(weight, height)
    }

    fun getCalculatedBmiCategory(): String {
        return PtmClinicalEvaluator.classifyBmi(getCalculatedBmi())
    }

    fun getCalculatedCentralObesity(): Boolean {
        val waist = formWaistCircumferenceCm.value.toDoubleOrNull() ?: 0.0
        return PtmClinicalEvaluator.isCentralObesity(waist, formGender.value)
    }

    fun getCalculatedBloodPressure(): Pair<String, Boolean> {
        val sys = formSystolicBp.value.toIntOrNull() ?: 0
        val dia = formDiastolicBp.value.toIntOrNull() ?: 0
        return PtmClinicalEvaluator.classifyBloodPressure(sys, dia)
    }

    fun getCalculatedBloodGlucose(): Pair<String, Boolean> {
        val value = formBloodGlucoseValue.value.toIntOrNull() ?: 0
        return PtmClinicalEvaluator.classifyBloodGlucose(formBloodGlucoseType.value, value)
    }

    fun getCalculatedRiskAndSummary(): Triple<String, Boolean, String> {
        val bp = getCalculatedBloodPressure()
        val bg = getCalculatedBloodGlucose()
        val bmiCat = getCalculatedBmiCategory()
        val waistObs = getCalculatedCentralObesity()

        return PtmClinicalEvaluator.evaluatePtmRisk(
            isHypertension = bp.second,
            bpCategory = bp.first,
            isDiabetes = bg.second,
            glucoseCategory = bg.first,
            isCentralObesity = waistObs,
            bmiCategory = bmiCat,
            isSmoking = formIsSmoking.value,
            doesPhysicalActivity = formDoesPhysicalActivity.value,
            familyHistory = formFamilyHistoryPtm.value
        )
    }

    fun getCalculatedRecommendations(): String {
        val risk = getCalculatedRiskAndSummary()
        val bp = getCalculatedBloodPressure()
        val bg = getCalculatedBloodGlucose()

        return PtmClinicalEvaluator.generateRecommendation(
            riskLevel = risk.first,
            needsReferral = risk.second,
            isHypertension = bp.second,
            isDiabetes = bg.second,
            isSmoking = formIsSmoking.value,
            isCentralObesity = getCalculatedCentralObesity(),
            doesPhysicalActivity = formDoesPhysicalActivity.value
        )
    }

    // Simpan Form Skrining Baru atau Edit
    fun saveFormRecord(onSuccess: (Long) -> Unit): Boolean {
        // Validasi
        if (formNik.value.trim().isEmpty()) {
            formErrorMessage.value = "NIK wajib diisi!"
            return false
        }
        if (formNik.value.trim().length != 16) {
            formErrorMessage.value = "NIK harus berjumlah 16 digit angka!"
            return false
        }
        if (formFullName.value.trim().isEmpty()) {
            formErrorMessage.value = "Nama Lengkap wajib diisi!"
            return false
        }
        val ageInt = formAge.value.toIntOrNull()
        if (ageInt == null || ageInt < 18 || ageInt > 59) {
            formErrorMessage.value = "Sasaran PTM adalah Usia Produktif (18 - 59 tahun)!"
            return false
        }
        val weight = formWeightKg.value.toDoubleOrNull()
        val height = formHeightCm.value.toDoubleOrNull()
        if (weight == null || weight <= 20.0 || height == null || height <= 80.0) {
            formErrorMessage.value = "Mohon isi Berat Badan dan Tinggi Badan dengan valid!"
            return false
        }
        val waist = formWaistCircumferenceCm.value.toDoubleOrNull()
        if (waist == null || waist <= 30.0) {
            formErrorMessage.value = "Mohon periksa input Lingkar Perut (cm)!"
            return false
        }
        val sys = formSystolicBp.value.toIntOrNull()
        val dia = formDiastolicBp.value.toIntOrNull()
        if (sys == null || sys <= 50 || dia == null || dia <= 30) {
            formErrorMessage.value = "Mohon periksa nilai Tekanan Darah (Sistolik / Diastolik)!"
            return false
        }
        val glucoseVal = formBloodGlucoseValue.value.toIntOrNull()
        if (glucoseVal == null || glucoseVal <= 20) {
            formErrorMessage.value = "Mohon isi nilai Gula Darah dengan benar!"
            return false
        }

        formErrorMessage.value = null

        val bmi = getCalculatedBmi()
        val bmiCategory = getCalculatedBmiCategory()
        val isCentralObesity = getCalculatedCentralObesity()
        val bp = getCalculatedBloodPressure()
        val bg = getCalculatedBloodGlucose()
        val riskSummary = getCalculatedRiskAndSummary()
        val recommendations = getCalculatedRecommendations()

        val record = PtmRecord(
            id = editingRecordId ?: 0,
            petugasName = formPetugasName.value.trim().ifEmpty { "Petugas PTM" },
            examinationDate = formExaminationDate.value.trim().ifEmpty {
                SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
            },
            posyanduName = formPosyanduName.value.trim().ifEmpty { "Posyandu Usia Produktif" },
            nik = formNik.value.trim(),
            fullName = formFullName.value.trim(),
            gender = formGender.value,
            age = ageInt,
            maritalStatus = formMaritalStatus.value,
            occupation = formOccupation.value.trim().ifEmpty { "Lainnya" },
            phoneNumber = formPhoneNumber.value.trim(),
            address = formAddress.value.trim(),
            isSmoking = formIsSmoking.value,
            cigarettesPerDay = formCigarettesPerDay.value.toIntOrNull() ?: 0,
            doesPhysicalActivity = formDoesPhysicalActivity.value,
            consumesFruitVeggie = formConsumesFruitVeggie.value,
            familyHistoryPtm = formFamilyHistoryPtm.value,
            personalHistoryPtm = formPersonalHistoryPtm.value,
            weightKg = weight,
            heightCm = height,
            bmi = bmi,
            bmiCategory = bmiCategory,
            waistCircumferenceCm = waist,
            isCentralObesity = isCentralObesity,
            systolicBp = sys,
            diastolicBp = dia,
            bloodPressureCategory = bp.first,
            isHypertension = bp.second,
            bloodGlucoseType = formBloodGlucoseType.value,
            bloodGlucoseValue = glucoseVal,
            bloodGlucoseCategory = bg.first,
            isDiabetes = bg.second,
            cholesterolValue = formCholesterolValue.value.toIntOrNull(),
            uricAcidValue = formUricAcidValue.value.toDoubleOrNull(),
            ptmRiskLevel = riskSummary.first,
            needsReferral = riskSummary.second,
            clinicalDiagnosisSummary = riskSummary.third,
            recommendation = recommendations,
            notes = formNotes.value.trim()
        )

        viewModelScope.launch {
            if (editingRecordId != null && editingRecordId != 0L) {
                repository.update(record)
                onSuccess(record.id)
            } else {
                val newId = repository.insert(record)
                onSuccess(newId)
            }
            resetForm()
        }
        return true
    }

    fun resetForm() {
        editingRecordId = null
        formNik.value = ""
        formFullName.value = ""
        formGender.value = "Perempuan"
        formAge.value = "35"
        formMaritalStatus.value = "Menikah"
        formOccupation.value = "Ibu Rumah Tangga"
        formPhoneNumber.value = ""
        formAddress.value = ""
        formIsSmoking.value = false
        formCigarettesPerDay.value = "0"
        formDoesPhysicalActivity.value = true
        formConsumesFruitVeggie.value = true
        formFamilyHistoryPtm.value = false
        formPersonalHistoryPtm.value = false
        formWeightKg.value = "60.0"
        formHeightCm.value = "160.0"
        formWaistCircumferenceCm.value = "78.0"
        formSystolicBp.value = "120"
        formDiastolicBp.value = "80"
        formBloodGlucoseType.value = "GDS"
        formBloodGlucoseValue.value = "115"
        formCholesterolValue.value = ""
        formUricAcidValue.value = ""
        formNotes.value = ""
        formErrorMessage.value = null
    }

    fun populateFormForEdit(record: PtmRecord) {
        editingRecordId = record.id
        formPetugasName.value = record.petugasName
        formExaminationDate.value = record.examinationDate
        formPosyanduName.value = record.posyanduName
        formNik.value = record.nik
        formFullName.value = record.fullName
        formGender.value = record.gender
        formAge.value = record.age.toString()
        formMaritalStatus.value = record.maritalStatus
        formOccupation.value = record.occupation
        formPhoneNumber.value = record.phoneNumber
        formAddress.value = record.address
        formIsSmoking.value = record.isSmoking
        formCigarettesPerDay.value = record.cigarettesPerDay.toString()
        formDoesPhysicalActivity.value = record.doesPhysicalActivity
        formConsumesFruitVeggie.value = record.consumesFruitVeggie
        formFamilyHistoryPtm.value = record.familyHistoryPtm
        formPersonalHistoryPtm.value = record.personalHistoryPtm
        formWeightKg.value = record.weightKg.toString()
        formHeightCm.value = record.heightCm.toString()
        formWaistCircumferenceCm.value = record.waistCircumferenceCm.toString()
        formSystolicBp.value = record.systolicBp.toString()
        formDiastolicBp.value = record.diastolicBp.toString()
        formBloodGlucoseType.value = record.bloodGlucoseType
        formBloodGlucoseValue.value = record.bloodGlucoseValue.toString()
        formCholesterolValue.value = record.cholesterolValue?.toString() ?: ""
        formUricAcidValue.value = record.uricAcidValue?.toString() ?: ""
        formNotes.value = record.notes
        formErrorMessage.value = null
    }

    fun deleteRecord(record: PtmRecord) {
        viewModelScope.launch {
            repository.delete(record)
        }
    }

    // Ekspor Excel (CSV dengan UTF-8 BOM untuk Microsoft Excel)
    fun exportExcel(context: Context, periodLabel: String): File? {
        val recordsToExport = filteredRecords.value
        val file = ExportManager.exportToExcel(context, recordsToExport, periodLabel)
        if (file != null) {
            _lastExportedFile.value = file
            _exportType.value = "excel"
        }
        return file
    }

    // Ekspor Dokumen PDF Resmi Laporan Bulanan
    fun exportPdf(
        context: Context,
        periodLabel: String,
        puskesmasName: String = "Puskesmas Pembina Kecamatan",
        petugasSigner: String = "Pengelola Program PTM"
    ): File? {
        val recordsToExport = filteredRecords.value
        val file = ExportManager.exportToPdf(
            context = context,
            records = recordsToExport,
            reportPeriod = periodLabel,
            puskesmasName = puskesmasName,
            petugasSigner = petugasSigner
        )
        if (file != null) {
            _lastExportedFile.value = file
            _exportType.value = "pdf"
        }
        return file
    }
}
