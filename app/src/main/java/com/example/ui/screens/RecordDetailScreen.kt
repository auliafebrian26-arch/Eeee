package com.example.ui.screens

import android.content.Intent
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AssignmentInd
import androidx.compose.material.icons.filled.Bloodtype
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.HealthAndSafety
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Straighten
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.PtmRecord
import com.example.ui.theme.StatusGreen
import com.example.ui.theme.StatusOrange
import com.example.ui.theme.StatusRed
import com.example.ui.theme.StatusYellow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecordDetailScreen(
    record: PtmRecord,
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current

    BackHandler {
        onNavigateBack()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Kartu Hasil Skrining PTM",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
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
                actions = {
                    IconButton(
                        onClick = {
                            val shareText = buildString {
                                appendLine("📋 KARTU HASIL SKRINING PTM POSYANDU")
                                appendLine("Puskesmas Pelayanan Usia Produktif")
                                appendLine("----------------------------------")
                                appendLine("Nama: ${record.fullName}")
                                appendLine("NIK: ${record.nik}")
                                appendLine("Usia: ${record.age} Tahun (${record.gender})")
                                appendLine("Tanggal Periksa: ${record.examinationDate}")
                                appendLine("Petugas: ${record.petugasName}")
                                appendLine("----------------------------------")
                                appendLine("1. TEKANAN DARAH: ${record.systolicBp}/${record.diastolicBp} mmHg (${record.bloodPressureCategory})")
                                appendLine("2. GULA DARAH (${record.bloodGlucoseType}): ${record.bloodGlucoseValue} mg/dL (${record.bloodGlucoseCategory})")
                                appendLine("3. ANTROPOMETRI:")
                                appendLine("   - BB/TB: ${record.weightKg} kg / ${record.heightCm} cm")
                                appendLine("   - IMT: ${record.bmi} (${record.bmiCategory})")
                                appendLine("   - Lingkar Perut: ${record.waistCircumferenceCm} cm (${if (record.isCentralObesity) "Obesitas Sentral" else "Normal"})")
                                appendLine("4. FAKTOR RESIKO:")
                                appendLine("   - Merokok: ${if (record.isSmoking) "Ya (${record.cigarettesPerDay} btg/hari)" else "Tidak"}")
                                appendLine("   - Aktivitas Fisik: ${if (record.doesPhysicalActivity) "Cukup (≥150 mnt/mgg)" else "Kurang"}")
                                appendLine("----------------------------------")
                                appendLine("KESIMPULAN: Resiko ${record.ptmRiskLevel}")
                                appendLine("Temuan: ${record.clinicalDiagnosisSummary}")
                                if (record.needsReferral) {
                                    appendLine("⚠ SEGERA RUJUK KE PUSKESMAS UNTUK PEMERIKSAAN MEDIS LANJUTAN!")
                                }
                                appendLine("\nAnjuran Pola Hidup Sehat (CERDIK):")
                                appendLine(record.recommendation)
                            }

                            val intent = Intent(Intent.ACTION_SEND).apply {
                                type = "text/plain"
                                putExtra(Intent.EXTRA_SUBJECT, "Hasil Skrining PTM - ${record.fullName}")
                                putExtra(Intent.EXTRA_TEXT, shareText)
                            }
                            context.startActivity(Intent.createChooser(intent, "Bagikan Hasil Skrining"))
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "Bagikan",
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .testTag("record_detail_screen"),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Patient Header Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (record.needsReferral) StatusRed.copy(alpha = 0.08f)
                        else MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)
                    )
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(CircleShape)
                                    .background(
                                        if (record.needsReferral) StatusRed.copy(alpha = 0.15f)
                                        else MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = if (record.needsReferral) Icons.Default.Warning else Icons.Default.Person,
                                    contentDescription = null,
                                    tint = if (record.needsReferral) StatusRed else MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(26.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = record.fullName,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "NIK: ${record.nik}",
                                    fontSize = 13.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            DetailItem(title = "Usia & Gender", value = "${record.age} Th • ${record.gender}")
                            DetailItem(title = "Pernikahan", value = record.maritalStatus)
                            DetailItem(title = "Pekerjaan", value = record.occupation)
                        }

                        if (record.phoneNumber.isNotEmpty() || record.address.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(8.dp))
                            if (record.phoneNumber.isNotEmpty()) {
                                Text(
                                    text = "No. HP: ${record.phoneNumber}",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            if (record.address.isNotEmpty()) {
                                Text(
                                    text = "Alamat: ${record.address}",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }

            // Status Rujukan & Resiko
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (record.needsReferral) StatusRed.copy(alpha = 0.12f) else StatusGreen.copy(alpha = 0.1f)
                    )
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Kesimpulan Skrining PTM:",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = if (record.needsReferral) StatusRed else StatusGreen
                            )
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = if (record.needsReferral) StatusRed else StatusGreen
                            ) {
                                Text(
                                    text = if (record.needsReferral) "PERLU RUJUKAN PUSKESMAS" else "RESIKO ${record.ptmRiskLevel.uppercase()}",
                                    color = Color.White,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Temuan Klinis: ${record.clinicalDiagnosisSummary}",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }

            // HASIL 1: TEKANAN DARAH (HIPERTENSI)
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Favorite, contentDescription = null, tint = StatusRed, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("1. Pemeriksaan Tekanan Darah", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            DetailItem(title = "Nilai Tensi", value = "${record.systolicBp} / ${record.diastolicBp} mmHg")
                            DetailItem(title = "Kategori", value = record.bloodPressureCategory)
                            DetailItem(
                                title = "Status",
                                value = if (record.isHypertension) "HIPERTENSI (+)" else "Normal/Pre"
                            )
                        }
                    }
                }
            }

            // HASIL 2: GULA DARAH (DIABETES MELITUS)
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Bloodtype, contentDescription = null, tint = StatusOrange, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("2. Pemeriksaan Gula Darah (${record.bloodGlucoseType})", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            DetailItem(title = "Jenis Cek", value = record.bloodGlucoseType)
                            DetailItem(title = "Kadar Glukosa", value = "${record.bloodGlucoseValue} mg/dL")
                            DetailItem(
                                title = "Status",
                                value = if (record.isDiabetes) "DIABETES (+)" else record.bloodGlucoseCategory
                            )
                        }
                    }
                }
            }

            // HASIL 3: ANTROPOMETRI (BB, TB, IMT, LINGKAR PERUT)
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Straighten, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("3. Antropometri & Obesitas", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            DetailItem(title = "BB / TB", value = "${record.weightKg} kg / ${record.heightCm} cm")
                            DetailItem(title = "IMT", value = "${record.bmi} (${record.bmiCategory})")
                            DetailItem(
                                title = "Lingkar Perut",
                                value = "${record.waistCircumferenceCm} cm (${if (record.isCentralObesity) "Obesitas Sentral" else "Normal"})"
                            )
                        }
                    }
                }
            }

            // HASIL 4: FAKTOR RESIKO & GAYA HIDUP
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.HealthAndSafety, contentDescription = null, tint = MaterialTheme.colorScheme.secondary, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("4. Faktor Resiko Perilaku", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            DetailItem(
                                title = "Merokok",
                                value = if (record.isSmoking) "Ya (${record.cigarettesPerDay} btg/hari)" else "Tidak"
                            )
                            DetailItem(
                                title = "Aktivitas Fisik",
                                value = if (record.doesPhysicalActivity) "Cukup (≥150 mnt/mgg)" else "Kurang"
                            )
                            DetailItem(
                                title = "Sayur & Buah",
                                value = if (record.consumesFruitVeggie) "Cukup" else "Kurang"
                            )
                        }
                    }
                }
            }

            // HASIL 5: REKOMENDASI & TINDAK LANJUT
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text("5. Rekomendasi Petugas & Edukasi CERDIK", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = record.recommendation,
                            fontSize = 12.5.sp,
                            lineHeight = 18.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        if (record.notes.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = "Catatan Khusus: ${record.notes}",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "Pemeriksa: ${record.petugasName} (${record.posyanduName})",
                            fontSize = 11.5.sp,
                            color = MaterialTheme.colorScheme.outline
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun DetailItem(title: String, value: String) {
    Column {
        Text(
            text = title,
            fontSize = 10.5.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = value,
            fontSize = 12.5.sp,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}
