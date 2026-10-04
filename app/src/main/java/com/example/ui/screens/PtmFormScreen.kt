package com.example.ui.screens

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AssignmentInd
import androidx.compose.material.icons.filled.Bloodtype
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.HealthAndSafety
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Straighten
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.PtmViewModel
import com.example.ui.theme.StatusBlue
import com.example.ui.theme.StatusGreen
import com.example.ui.theme.StatusOrange
import com.example.ui.theme.StatusRed
import com.example.ui.theme.StatusYellow

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun PtmFormScreen(
    viewModel: PtmViewModel,
    onNavigateBack: () -> Unit,
    onSavedSuccess: (Long) -> Unit
) {
    val context = LocalContext.current

    BackHandler {
        onNavigateBack()
    }

    val petugasName by viewModel.formPetugasName.collectAsState()
    val examinationDate by viewModel.formExaminationDate.collectAsState()
    val posyanduName by viewModel.formPosyanduName.collectAsState()
    val nik by viewModel.formNik.collectAsState()
    val fullName by viewModel.formFullName.collectAsState()
    val gender by viewModel.formGender.collectAsState()
    val age by viewModel.formAge.collectAsState()
    val maritalStatus by viewModel.formMaritalStatus.collectAsState()
    val occupation by viewModel.formOccupation.collectAsState()
    val phoneNumber by viewModel.formPhoneNumber.collectAsState()
    val address by viewModel.formAddress.collectAsState()

    val isSmoking by viewModel.formIsSmoking.collectAsState()
    val cigarettesPerDay by viewModel.formCigarettesPerDay.collectAsState()
    val doesPhysicalActivity by viewModel.formDoesPhysicalActivity.collectAsState()
    val consumesFruitVeggie by viewModel.formConsumesFruitVeggie.collectAsState()
    val familyHistoryPtm by viewModel.formFamilyHistoryPtm.collectAsState()

    val weightKg by viewModel.formWeightKg.collectAsState()
    val heightCm by viewModel.formHeightCm.collectAsState()
    val waistCm by viewModel.formWaistCircumferenceCm.collectAsState()

    val systolicBp by viewModel.formSystolicBp.collectAsState()
    val diastolicBp by viewModel.formDiastolicBp.collectAsState()

    val bloodGlucoseType by viewModel.formBloodGlucoseType.collectAsState()
    val bloodGlucoseValue by viewModel.formBloodGlucoseValue.collectAsState()

    val cholesterolValue by viewModel.formCholesterolValue.collectAsState()
    val uricAcidValue by viewModel.formUricAcidValue.collectAsState()
    val notes by viewModel.formNotes.collectAsState()
    val errorMessage by viewModel.formErrorMessage.collectAsState()

    // Realtime Computations
    val computedBmi = viewModel.getCalculatedBmi()
    val computedBmiCategory = viewModel.getCalculatedBmiCategory()
    val computedCentralObesity = viewModel.getCalculatedCentralObesity()
    val computedBp = viewModel.getCalculatedBloodPressure()
    val computedGlucose = viewModel.getCalculatedBloodGlucose()
    val computedRisk = viewModel.getCalculatedRiskAndSummary()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = if (viewModel.editingRecordId != null) "Edit Skrining PTM" else "Form Skrining PTM",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Sasaran Usia Produktif (18 - 59 Tahun)",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f)
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Kembali",
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        bottomBar = {
            Surface(
                tonalElevation = 6.dp,
                shadowElevation = 8.dp,
                color = MaterialTheme.colorScheme.surface
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = onNavigateBack,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Batal")
                    }

                    Button(
                        onClick = {
                            val success = viewModel.saveFormRecord { newId ->
                                Toast.makeText(context, "Data Skrining PTM Berhasil Disimpan!", Toast.LENGTH_SHORT).show()
                                onSavedSuccess(newId)
                            }
                            if (!success && errorMessage != null) {
                                Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
                            }
                        },
                        modifier = Modifier
                            .weight(1.5f)
                            .testTag("save_screening_button"),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) {
                        Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Simpan Skrining", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .testTag("ptm_form_scrollable"),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Error banner if any
            if (errorMessage != null) {
                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = StatusRed.copy(alpha = 0.1f)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Warning, contentDescription = null, tint = StatusRed)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = errorMessage ?: "",
                                color = StatusRed,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }

            // BAGIAN 1: DATA PETUGAS & PEMERIKSAAN
            item {
                FormSectionCard(
                    title = "1. Petugas & Waktu Pelayanan",
                    icon = Icons.Default.AssignmentInd,
                    color = MaterialTheme.colorScheme.primary
                ) {
                    OutlinedTextField(
                        value = petugasName,
                        onValueChange = { viewModel.formPetugasName.value = it },
                        label = { Text("Nama Petugas / Kader Pemeriksa *") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_petugas_name"),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedTextField(
                            value = examinationDate,
                            onValueChange = { viewModel.formExaminationDate.value = it },
                            label = { Text("Tgl Periksa (YYYY-MM-DD) *") },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("input_examination_date"),
                            singleLine = true,
                            leadingIcon = { Icon(Icons.Default.DateRange, contentDescription = null) }
                        )

                        OutlinedTextField(
                            value = posyanduName,
                            onValueChange = { viewModel.formPosyanduName.value = it },
                            label = { Text("Posyandu / Faskes") },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("input_posyandu_name"),
                            singleLine = true
                        )
                    }
                }
            }

            // BAGIAN 2: IDENTITAS PESERTA (18 - 59 TAHUN)
            item {
                FormSectionCard(
                    title = "2. Identitas Peserta Usia Produktif",
                    icon = Icons.Default.Person,
                    color = MaterialTheme.colorScheme.secondary
                ) {
                    // NIK
                    OutlinedTextField(
                        value = nik,
                        onValueChange = { if (it.length <= 16 && it.all { char -> char.isDigit() }) viewModel.formNik.value = it },
                        label = { Text("Nomor Induk Kependudukan (NIK) *") },
                        supportingText = { Text("${nik.length}/16 Digit KTP") },
                        isError = nik.isNotEmpty() && nik.length != 16,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_nik"),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Nama Lengkap
                    OutlinedTextField(
                        value = fullName,
                        onValueChange = { viewModel.formFullName.value = it },
                        label = { Text("Nama Lengkap Sesuai KTP *") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_full_name"),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Jenis Kelamin
                    Text(
                        text = "Jenis Kelamin *",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.SemiBold
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf("Laki-laki", "Perempuan").forEach { itemGender ->
                            FilterChip(
                                selected = (gender == itemGender),
                                onClick = { viewModel.formGender.value = itemGender },
                                label = { Text(itemGender) },
                                leadingIcon = if (gender == itemGender) {
                                    { Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp)) }
                                } else null,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Usia (18-59)
                    OutlinedTextField(
                        value = age,
                        onValueChange = { if (it.length <= 2 && it.all { c -> c.isDigit() }) viewModel.formAge.value = it },
                        label = { Text("Usia (Tahun) * Sasaran: 18 - 59 Th") },
                        supportingText = {
                            val a = age.toIntOrNull() ?: 0
                            if (a in 18..59) {
                                Text("✓ Memenuhi Kriteria Usia Produktif", color = StatusGreen)
                            } else {
                                Text("⚠ Sasaran PTM Puskesmas adalah 18-59 Tahun", color = StatusOrange)
                            }
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_age"),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Status Pernikahan
                    Text(
                        text = "Status Pernikahan *",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.SemiBold
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf("Belum Menikah", "Menikah", "Janda/Duda").forEach { status ->
                            FilterChip(
                                selected = (maritalStatus == status),
                                onClick = { viewModel.formMaritalStatus.value = status },
                                label = { Text(status, fontSize = 11.sp) },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Pekerjaan
                    OutlinedTextField(
                        value = occupation,
                        onValueChange = { viewModel.formOccupation.value = it },
                        label = { Text("Pekerjaan *") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedTextField(
                            value = phoneNumber,
                            onValueChange = { viewModel.formPhoneNumber.value = it },
                            label = { Text("No. HP / WhatsApp") },
                            modifier = Modifier.weight(1f),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                            singleLine = true
                        )

                        OutlinedTextField(
                            value = address,
                            onValueChange = { viewModel.formAddress.value = it },
                            label = { Text("Alamat / RT RW") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                    }
                }
            }

            // BAGIAN 3: FAKTOR RESIKO PTM
            item {
                FormSectionCard(
                    title = "3. Faktor Resiko PTM",
                    icon = Icons.Default.HealthAndSafety,
                    color = MaterialTheme.colorScheme.tertiary
                ) {
                    // Merokok
                    RiskSwitchRow(
                        title = "Merokok (Aktif)",
                        subtitle = "Apakah merokok dalam kurun waktu 1 bulan terakhir?",
                        checked = isSmoking,
                        onCheckedChange = { viewModel.formIsSmoking.value = it }
                    )

                    if (isSmoking) {
                        OutlinedTextField(
                            value = cigarettesPerDay,
                            onValueChange = { if (it.all { c -> c.isDigit() }) viewModel.formCigarettesPerDay.value = it },
                            label = { Text("Jumlah Konsumsi Rokok (Batang/Hari)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            singleLine = true
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Aktivitas Fisik
                    RiskSwitchRow(
                        title = "Melakukan Aktivitas Fisik",
                        subtitle = "Minimal 30 menit sehari atau 150 menit per minggu",
                        checked = doesPhysicalActivity,
                        onCheckedChange = { viewModel.formDoesPhysicalActivity.value = it }
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Konsumsi Sayur dan Buah
                    RiskSwitchRow(
                        title = "Cukup Sayur & Buah",
                        subtitle = "Makan sayur dan buah minimal 5 porsi/hari",
                        checked = consumesFruitVeggie,
                        onCheckedChange = { viewModel.formConsumesFruitVeggie.value = it }
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Riwayat PTM Keluarga
                    RiskSwitchRow(
                        title = "Riwayat PTM Keluarga",
                        subtitle = "Orang tua / saudara kandung ada riwayat Hipertensi / Diabetes",
                        checked = familyHistoryPtm,
                        onCheckedChange = { viewModel.formFamilyHistoryPtm.value = it }
                    )
                }
            }

            // BAGIAN 4: PEMERIKSAAN ANTROPOMETRI (BB, TB, LINGKAR PERUT)
            item {
                FormSectionCard(
                    title = "4. Antropometri (BB / TB & Lingkar Perut)",
                    icon = Icons.Default.Straighten,
                    color = Color(0xFF00796B)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedTextField(
                            value = weightKg,
                            onValueChange = { viewModel.formWeightKg.value = it },
                            label = { Text("Berat Badan (kg) *") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("input_weight"),
                            singleLine = true
                        )

                        OutlinedTextField(
                            value = heightCm,
                            onValueChange = { viewModel.formHeightCm.value = it },
                            label = { Text("Tinggi Badan (cm) *") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("input_height"),
                            singleLine = true
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = waistCm,
                        onValueChange = { viewModel.formWaistCircumferenceCm.value = it },
                        label = { Text("Lingkar Perut (cm) *") },
                        supportingText = {
                            Text(
                                text = "Batas Normal: Pria ≤ 90 cm, Wanita ≤ 80 cm",
                                fontSize = 11.sp
                            )
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_waist"),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Real-Time Result Box for Antropometri
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = if (computedCentralObesity || computedBmiCategory == "Obesitas") {
                                StatusRed.copy(alpha = 0.1f)
                            } else if (computedBmiCategory.startsWith("Gemuk")) {
                                StatusYellow.copy(alpha = 0.15f)
                            } else {
                                StatusGreen.copy(alpha = 0.1f)
                            }
                        ),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Hasil Hitung IMT: $computedBmi kg/m²",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                                Text(
                                    text = computedBmiCategory,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = if (computedBmiCategory == "Obesitas") StatusRed else if (computedBmiCategory == "Normal") StatusGreen else StatusOrange
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = if (computedCentralObesity) {
                                    "⚠ Obesitas Sentral Terdeteksi (Lingkar perut melebihi batas rujukan)"
                                } else {
                                    "✓ Lingkar Perut dalam batas normal"
                                },
                                fontSize = 12.sp,
                                color = if (computedCentralObesity) StatusRed else StatusGreen,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }

            // BAGIAN 5: PEMERIKSAAN TEKANAN DARAH (HIPERTENSI)
            item {
                FormSectionCard(
                    title = "5. Tekanan Darah (Hipertensi)",
                    icon = Icons.Default.Favorite,
                    color = StatusRed
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedTextField(
                            value = systolicBp,
                            onValueChange = { if (it.all { c -> c.isDigit() }) viewModel.formSystolicBp.value = it },
                            label = { Text("Sistolik (mmHg) *") },
                            placeholder = { Text("120") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("input_systolic"),
                            singleLine = true
                        )

                        OutlinedTextField(
                            value = diastolicBp,
                            onValueChange = { if (it.all { c -> c.isDigit() }) viewModel.formDiastolicBp.value = it },
                            label = { Text("Diastolik (mmHg) *") },
                            placeholder = { Text("80") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("input_diastolic"),
                            singleLine = true
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Real-Time Result Box for Blood Pressure
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = if (computedBp.second) StatusRed.copy(alpha = 0.12f)
                            else if (computedBp.first == "Pre-Hipertensi") StatusYellow.copy(alpha = 0.18f)
                            else StatusGreen.copy(alpha = 0.1f)
                        ),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "Klasifikasi Tekanan Darah:",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = computedBp.first,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp,
                                    color = if (computedBp.second) StatusRed else if (computedBp.first == "Pre-Hipertensi") StatusOrange else StatusGreen
                                )
                            }
                            if (computedBp.second) {
                                Text(
                                    text = "HIPERTENSI (+)",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = StatusRed,
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(StatusRed.copy(alpha = 0.2f))
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }
                }
            }

            // BAGIAN 6: PEMERIKSAAN GULA DARAH (DIABETES MELITUS)
            item {
                FormSectionCard(
                    title = "6. Gula Darah (Diabetes Melitus)",
                    icon = Icons.Default.Bloodtype,
                    color = StatusOrange
                ) {
                    Text(
                        text = "Jenis Pemeriksaan Gula Darah *",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.SemiBold
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf(
                            Pair("GDS", "Gula Darah Sewaktu (GDS)"),
                            Pair("GDP", "Gula Darah Puasa (GDP)")
                        ).forEach { (code, title) ->
                            FilterChip(
                                selected = (bloodGlucoseType == code),
                                onClick = { viewModel.formBloodGlucoseType.value = code },
                                label = { Text(title, fontSize = 12.sp) },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = bloodGlucoseValue,
                        onValueChange = { if (it.all { c -> c.isDigit() }) viewModel.formBloodGlucoseValue.value = it },
                        label = { Text("Kadar Gula Darah (mg/dL) *") },
                        placeholder = { Text("110") },
                        supportingText = {
                            val normalThreshold = if (bloodGlucoseType == "GDP") "< 100 mg/dL" else "< 140 mg/dL"
                            Text("Rujukan Normal $bloodGlucoseType: $normalThreshold")
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_glucose"),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Real-Time Result Box for Blood Glucose
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = if (computedGlucose.second) StatusRed.copy(alpha = 0.12f)
                            else if (computedGlucose.first.startsWith("Pre")) StatusYellow.copy(alpha = 0.18f)
                            else StatusGreen.copy(alpha = 0.1f)
                        ),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "Klasifikasi Diabetes Melitus:",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = computedGlucose.first,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp,
                                    color = if (computedGlucose.second) StatusRed else if (computedGlucose.first.startsWith("Pre")) StatusOrange else StatusGreen
                                )
                            }
                            if (computedGlucose.second) {
                                Text(
                                    text = "DIABETES (+)",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = StatusRed,
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(StatusRed.copy(alpha = 0.2f))
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }
                }
            }

            // BAGIAN 7: PEMERIKSAAN TAMBAHAN (OPSIONAL)
            item {
                FormSectionCard(
                    title = "7. Pemeriksaan Tambahan (Opsional)",
                    icon = Icons.Default.FitnessCenter,
                    color = MaterialTheme.colorScheme.outline
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedTextField(
                            value = cholesterolValue,
                            onValueChange = { if (it.all { c -> c.isDigit() }) viewModel.formCholesterolValue.value = it },
                            label = { Text("Kolesterol (mg/dL)") },
                            placeholder = { Text("< 200") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )

                        OutlinedTextField(
                            value = uricAcidValue,
                            onValueChange = { viewModel.formUricAcidValue.value = it },
                            label = { Text("Asam Urat (mg/dL)") },
                            placeholder = { Text("6.0") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = notes,
                        onValueChange = { viewModel.formNotes.value = it },
                        label = { Text("Catatan Tambahan Petugas / Keluhan") },
                        placeholder = { Text("Misal: Tengkuk terasa kaku, sering haus...") },
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 3
                    )
                }
            }

            // BAGIAN 8: KESIMPULAN KLINIS & TINDAK LANJUT PUSKESMAS OTOMATIS
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (computedRisk.second) StatusRed.copy(alpha = 0.08f) else Color(0xFFE8F5E9)
                    ),
                    border = CardDefaults.outlinedCardBorder().copy(
                        brush = androidx.compose.ui.graphics.SolidColor(
                            if (computedRisk.second) StatusRed else StatusGreen
                        )
                    )
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = if (computedRisk.second) Icons.Default.Warning else Icons.Default.Check,
                                contentDescription = null,
                                tint = if (computedRisk.second) StatusRed else StatusGreen
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Evaluasi Klinis & Rekomendasi Terpadu",
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = if (computedRisk.second) StatusRed else StatusGreen
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Tingkat Resiko PTM:", fontSize = 13.sp)
                            Text(
                                text = "Resiko ${computedRisk.first}",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = if (computedRisk.first == "Tinggi") StatusRed else if (computedRisk.first == "Sedang") StatusOrange else StatusGreen
                            )
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Rekomendasi Rujukan:", fontSize = 13.sp)
                            Text(
                                text = if (computedRisk.second) "RUJUK KE PUSKESMAS" else "Edukasi di Posyandu",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = if (computedRisk.second) StatusRed else StatusGreen
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "Temuan Skrining: ${computedRisk.third}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }

            // Bottom space for scrolling above buttons
            item {
                Spacer(modifier = Modifier.height(40.dp))
            }
        }
    }
}

@Composable
fun FormSectionCard(
    title: String,
    icon: ImageVector,
    color: Color,
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(color.copy(alpha = 0.14f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(imageVector = icon, contentDescription = null, tint = color, modifier = Modifier.size(18.dp))
                }
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            Spacer(modifier = Modifier.height(14.dp))
            content()
        }
    }
}

@Composable
fun RiskSwitchRow(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .clickable { onCheckedChange(!checked) }
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontSize = 13.5.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = subtitle,
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = MaterialTheme.colorScheme.primary,
                checkedTrackColor = MaterialTheme.colorScheme.primaryContainer
            )
        )
    }
}
