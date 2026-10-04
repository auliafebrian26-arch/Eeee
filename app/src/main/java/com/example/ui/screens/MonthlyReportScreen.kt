package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.filled.HealthAndSafety
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.TableChart
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.example.ui.PtmViewModel
import com.example.ui.theme.StatusBlue
import com.example.ui.theme.StatusGreen
import com.example.ui.theme.StatusOrange
import com.example.ui.theme.StatusRed
import com.example.ui.theme.StatusYellow
import com.example.util.ExportManager
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MonthlyReportScreen(
    viewModel: PtmViewModel,
    onRecordClick: (PtmRecord) -> Unit
) {
    val context = LocalContext.current
    val allRecords by viewModel.allRecords.collectAsState()
    val filteredRecords by viewModel.filteredRecords.collectAsState()
    val selectedMonth by viewModel.selectedMonth.collectAsState()
    val lastExportedFile by viewModel.lastExportedFile.collectAsState()
    val exportType by viewModel.exportType.collectAsState()

    var showExportDialog by remember { mutableStateOf(false) }
    var puskesmasName by remember { mutableStateOf("Puskesmas Pembina Kecamatan") }
    var petugasSigner by remember { mutableStateOf("Pengelola Program PTM Posyandu") }

    // Dapatkan daftar bulan yang tersedia dari data
    val availableMonths = remember(allRecords) {
        val months = allRecords.map {
            if (it.examinationDate.length >= 7) it.examinationDate.substring(0, 7) else "Lainnya"
        }.distinct().sortedDescending()
        listOf("Semua") + months
    }

    val currentMonthLabel = if (selectedMonth == "Semua") "Semua Periode" else {
        try {
            val parser = SimpleDateFormat("yyyy-MM", Locale.getDefault())
            val formatter = SimpleDateFormat("MMMM yyyy", Locale("id", "ID"))
            val parsedDate = parser.parse(selectedMonth)
            if (parsedDate != null) formatter.format(parsedDate) else selectedMonth
        } catch (e: Exception) {
            selectedMonth
        }
    }

    // Statistik Periode Terpilih
    val totalPeserta = filteredRecords.size
    val totalLaki = filteredRecords.count { it.gender.equals("Laki-laki", ignoreCase = true) }
    val totalPerempuan = filteredRecords.count { it.gender.equals("Perempuan", ignoreCase = true) }
    val totalHipertensi = filteredRecords.count { it.isHypertension }
    val totalDiabetes = filteredRecords.count { it.isDiabetes }
    val totalObesitas = filteredRecords.count { it.isCentralObesity || it.bmiCategory == "Obesitas" }
    val totalPerokok = filteredRecords.count { it.isSmoking }
    val totalKurangAktivitas = filteredRecords.count { !it.doesPhysicalActivity }
    val totalRujuk = filteredRecords.count { it.needsReferral }

    val persenHipertensi = if (totalPeserta > 0) String.format(Locale.US, "%.1f", (totalHipertensi.toDouble() / totalPeserta) * 100) else "0.0"
    val persenDiabetes = if (totalPeserta > 0) String.format(Locale.US, "%.1f", (totalDiabetes.toDouble() / totalPeserta) * 100) else "0.0"
    val persenObesitas = if (totalPeserta > 0) String.format(Locale.US, "%.1f", (totalObesitas.toDouble() / totalPeserta) * 100) else "0.0"
    val persenPerokok = if (totalPeserta > 0) String.format(Locale.US, "%.1f", (totalPerokok.toDouble() / totalPeserta) * 100) else "0.0"

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("monthly_report_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primary),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Assessment,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Laporan Bulanan PTM Puskesmas",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Text(
                                text = "Rekapitulasi Pelayanan PTM Usia Produktif",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                            )
                        }
                    }
                }
            }
        }

        // Filter Periode Bulan
        item {
            Column {
                Text(
                    text = "Pilih Periode Laporan:",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(8.dp))
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(availableMonths) { monthOption ->
                        val isSelected = (selectedMonth == monthOption)
                        val displayLabel = if (monthOption == "Semua") "Semua Periode" else {
                            try {
                                val parser = SimpleDateFormat("yyyy-MM", Locale.getDefault())
                                val formatter = SimpleDateFormat("MMMM yyyy", Locale("id", "ID"))
                                val date = parser.parse(monthOption)
                                if (date != null) formatter.format(date) else monthOption
                            } catch (e: Exception) {
                                monthOption
                            }
                        }

                        FilterChip(
                            selected = isSelected,
                            onClick = { viewModel.setSelectedMonth(monthOption) },
                            label = { Text(displayLabel, fontSize = 12.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primary,
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }
            }
        }

        // DUA TOMBOL UTAMA EKSPOR: EXCEL DAN PDF
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Ekspor Laporan Resmi Puskesmas",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Ekspor rekapitulasi data $currentMonthLabel ($totalPeserta Peserta)",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Tombol Ekspor Excel
                        Button(
                            onClick = {
                                val file = viewModel.exportExcel(context, currentMonthLabel)
                                if (file != null) {
                                    showExportDialog = true
                                    Toast.makeText(context, "Berkas Excel berhasil dibuat!", Toast.LENGTH_SHORT).show()
                                } else {
                                    Toast.makeText(context, "Gagal membuat berkas Excel", Toast.LENGTH_SHORT).show()
                                }
                            },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("btn_export_excel"),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF1E7145) // Excel Dark Green
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Default.TableChart, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Ekspor Excel", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }

                        // Tombol Ekspor PDF
                        Button(
                            onClick = {
                                val file = viewModel.exportPdf(
                                    context = context,
                                    periodLabel = currentMonthLabel,
                                    puskesmasName = puskesmasName,
                                    petugasSigner = petugasSigner
                                )
                                if (file != null) {
                                    showExportDialog = true
                                    Toast.makeText(context, "Laporan PDF berhasil dibuat!", Toast.LENGTH_SHORT).show()
                                } else {
                                    Toast.makeText(context, "Gagal membuat laporan PDF", Toast.LENGTH_SHORT).show()
                                }
                            },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("btn_export_pdf"),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFFC62828) // PDF Red
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Default.PictureAsPdf, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Ekspor PDF", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                    }
                }
            }
        }

        // Banner Status Berkas yang Baru Saja Diekspor (Jika Ada)
        if (lastExportedFile != null) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (exportType == "excel") Color(0xFFE8F5E9) else Color(0xFFFFEBEE)
                    )
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = if (exportType == "excel") StatusGreen else StatusRed
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Berkas Berhasil Dibuat!",
                                fontWeight = FontWeight.Bold,
                                color = if (exportType == "excel") StatusGreen else StatusRed,
                                fontSize = 14.sp
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = lastExportedFile?.name ?: "",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = {
                                    lastExportedFile?.let { file ->
                                        val mime = if (exportType == "excel") "text/csv" else "application/pdf"
                                        ExportManager.openFile(context, file, mime)
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (exportType == "excel") StatusGreen else StatusRed
                                ),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(Icons.Default.FolderOpen, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Buka Berkas", fontSize = 12.sp)
                            }

                            OutlinedButton(
                                onClick = {
                                    lastExportedFile?.let { file ->
                                        val mime = if (exportType == "excel") "text/csv" else "application/pdf"
                                        ExportManager.shareFile(
                                            context,
                                            file,
                                            mime,
                                            "Laporan PTM Puskesmas - $currentMonthLabel"
                                        )
                                    }
                                },
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Bagikan", fontSize = 12.sp)
                            }
                        }
                    }
                }
            }
        }

        // REKAPITULASI STATISTIK BULANAN
        item {
            Text(
                text = "Indikator PTM Bulan Ini:",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                ReportIndicatorCard(
                    title = "Hipertensi",
                    count = "$totalHipertensi Kasus",
                    percent = "$persenHipertensi% Peserta",
                    color = StatusRed,
                    modifier = Modifier.weight(1f)
                )
                ReportIndicatorCard(
                    title = "Diabetes Melitus",
                    count = "$totalDiabetes Kasus",
                    percent = "$persenDiabetes% Peserta",
                    color = StatusOrange,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                ReportIndicatorCard(
                    title = "Obesitas Sentral/IMT",
                    count = "$totalObesitas Kasus",
                    percent = "$persenObesitas% Peserta",
                    color = StatusYellow,
                    modifier = Modifier.weight(1f)
                )
                ReportIndicatorCard(
                    title = "Perokok Aktif",
                    count = "$totalPerokok Orang",
                    percent = "$persenPerokok% Peserta",
                    color = MaterialTheme.colorScheme.tertiary,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "Rincian Demografi & Rujukan Pelayanan",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.5.sp
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    DemographicRow(label = "Total Peserta Usia Produktif", value = "$totalPeserta Orang")
                    DemographicRow(label = "Jenis Kelamin (L / P)", value = "$totalLaki Laki-laki / $totalPerempuan Perempuan")
                    DemographicRow(label = "Kurang Aktivitas Fisik (<150 m/mgg)", value = "$totalKurangAktivitas Orang")
                    DemographicRow(label = "Perlu Rujukan ke Puskesmas", value = "$totalRujuk Orang", isHighlight = true)
                }
            }
        }

        // Daftar Peserta Periode Ini
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Daftar Peserta Periode Ini ($totalPeserta)",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        if (filteredRecords.isEmpty()) {
            item {
                Text(
                    text = "Tidak ada pemeriksaan pada periode ini.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            items(filteredRecords) { record ->
                RecentRecordCard(record = record, onClick = { onRecordClick(record) })
            }
        }
    }

    // Export Success Dialog
    if (showExportDialog && lastExportedFile != null) {
        val file = lastExportedFile!!
        val isExcel = (exportType == "excel")
        AlertDialog(
            onDismissRequest = { showExportDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = if (isExcel) Icons.Default.TableChart else Icons.Default.PictureAsPdf,
                        contentDescription = null,
                        tint = if (isExcel) Color(0xFF1E7145) else StatusRed
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(if (isExcel) "Ekspor Excel Selesai" else "Ekspor PDF Selesai")
                }
            },
            text = {
                Column {
                    Text(
                        text = "Laporan $currentMonthLabel telah berhasil diekspor ke format ${if (isExcel) "Excel (.CSV UTF-8)" else "PDF Resmi"}."
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Nama Berkas:\n${file.name}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val mime = if (isExcel) "text/csv" else "application/pdf"
                        ExportManager.openFile(context, file, mime)
                        showExportDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isExcel) Color(0xFF1E7145) else StatusRed
                    )
                ) {
                    Text("Buka Sekarang")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        val mime = if (isExcel) "text/csv" else "application/pdf"
                        ExportManager.shareFile(
                            context,
                            file,
                            mime,
                            "Laporan PTM Puskesmas - $currentMonthLabel"
                        )
                        showExportDialog = false
                    }
                ) {
                    Text("Bagikan")
                }
            }
        )
    }
}

@Composable
fun ReportIndicatorCard(
    title: String,
    count: String,
    percent: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = title,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = count,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = color
            )
            Text(
                text = percent,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                color = color.copy(alpha = 0.85f)
            )
        }
    }
}

@Composable
fun DemographicRow(label: String, value: String, isHighlight: Boolean = false) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            fontSize = 12.sp,
            fontWeight = if (isHighlight) FontWeight.Bold else FontWeight.SemiBold,
            color = if (isHighlight) StatusRed else MaterialTheme.colorScheme.onSurface
        )
    }
}
