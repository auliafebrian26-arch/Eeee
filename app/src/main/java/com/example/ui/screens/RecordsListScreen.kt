package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.HealthAndSafety
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
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

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun RecordsListScreen(
    viewModel: PtmViewModel,
    onNavigateToDetail: (PtmRecord) -> Unit,
    onNavigateToForm: () -> Unit,
    onNavigateToEdit: (PtmRecord) -> Unit
) {
    val records by viewModel.filteredRecords.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val selectedFilter by viewModel.selectedFilter.collectAsState()

    var recordToDelete by remember { mutableStateOf<PtmRecord?>(null) }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    viewModel.resetForm()
                    onNavigateToForm()
                },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.testTag("fab_add_screening")
            ) {
                Icon(Icons.Default.Add, contentDescription = "Tambah Skrining Baru")
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .testTag("records_list_screen")
        ) {
            // Search Bar & Filter Header
            Surface(
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 2.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { viewModel.setSearchQuery(it) },
                        placeholder = { Text("Cari NIK, Nama Warga, atau Tanggal...") },
                        leadingIcon = {
                            Icon(Icons.Default.Search, contentDescription = "Cari", tint = MaterialTheme.colorScheme.primary)
                        },
                        trailingIcon = {
                            if (searchQuery.isNotEmpty()) {
                                IconButton(onClick = { viewModel.setSearchQuery("") }) {
                                    Icon(Icons.Default.Clear, contentDescription = "Hapus")
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("search_field"),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
                        )
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Filter Chips (Semua, Hipertensi, Diabetes, Obesitas, Rujuk)
                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        val filterOptions = listOf(
                            Pair("Semua", "Semua (${records.size})"),
                            Pair("Hipertensi", "Hipertensi"),
                            Pair("Diabetes", "Diabetes Melitus"),
                            Pair("Obesitas", "Obesitas"),
                            Pair("Rujuk", "Rujuk Pusk.")
                        )

                        filterOptions.forEach { (key, label) ->
                            val isSelected = (selectedFilter == key)
                            FilterChip(
                                selected = isSelected,
                                onClick = { viewModel.setSelectedFilter(key) },
                                label = {
                                    Text(
                                        text = label,
                                        fontSize = 11.5.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                    )
                                },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                    selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            )
                        }
                    }
                }
            }

            // List of Records
            if (records.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(72.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.surfaceVariant),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Description,
                                contentDescription = null,
                                modifier = Modifier.size(36.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = if (searchQuery.isNotEmpty()) "Data Tidak Ditemukan" else "Belum Ada Catatan Skrining",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = if (searchQuery.isNotEmpty())
                                "Tidak ada data yang sesuai dengan kata kunci pencarian."
                            else
                                "Mulai skrining peserta usia produktif untuk melihat riwayat.",
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(records, key = { it.id }) { record ->
                        RecordCard(
                            record = record,
                            onClick = { onNavigateToDetail(record) },
                            onEdit = {
                                viewModel.populateFormForEdit(record)
                                onNavigateToEdit(record)
                            },
                            onDelete = { recordToDelete = record }
                        )
                    }
                }
            }
        }
    }

    // Delete Confirmation Dialog
    if (recordToDelete != null) {
        AlertDialog(
            onDismissRequest = { recordToDelete = null },
            title = { Text("Konfirmasi Hapus Data") },
            text = {
                Text("Apakah Anda yakin ingin menghapus data skrining PTM peserta ${recordToDelete?.fullName} (NIK: ${recordToDelete?.nik})? Data yang dihapus tidak dapat dipulihkan.")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        recordToDelete?.let { viewModel.deleteRecord(it) }
                        recordToDelete = null
                    }
                ) {
                    Text("Hapus", color = StatusRed, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { recordToDelete = null }) {
                    Text("Batal")
                }
            }
        )
    }
}

@Composable
fun RecordCard(
    record: PtmRecord,
    onClick: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag("record_card_${record.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.5.dp)
    ) {
        Column(
            modifier = Modifier.padding(14.dp)
        ) {
            // Header Row: Nama & Tanggal
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(
                                if (record.needsReferral) StatusRed.copy(alpha = 0.12f)
                                else if (record.isHypertension || record.isDiabetes) StatusOrange.copy(alpha = 0.12f)
                                else StatusGreen.copy(alpha = 0.12f)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (record.needsReferral) Icons.Default.Warning else Icons.Default.Favorite,
                            contentDescription = null,
                            tint = if (record.needsReferral) StatusRed
                            else if (record.isHypertension || record.isDiabetes) StatusOrange
                            else StatusGreen,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = record.fullName,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "NIK: ${record.nik} • ${record.age} Th • ${record.gender}",
                            fontSize = 11.5.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onEdit, modifier = Modifier.size(32.dp)) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    IconButton(onClick = onDelete, modifier = Modifier.size(32.dp)) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Hapus",
                            tint = MaterialTheme.colorScheme.outline,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Clinical Badges Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                // Tensi
                ClinicalBadge(
                    label = "TD ${record.systolicBp}/${record.diastolicBp}",
                    status = record.bloodPressureCategory,
                    isAlert = record.isHypertension,
                    alertColor = StatusRed
                )

                // Gula Darah
                ClinicalBadge(
                    label = "${record.bloodGlucoseType}: ${record.bloodGlucoseValue}",
                    status = record.bloodGlucoseCategory,
                    isAlert = record.isDiabetes,
                    alertColor = StatusOrange
                )

                // IMT
                ClinicalBadge(
                    label = "IMT ${record.bmi}",
                    status = record.bmiCategory,
                    isAlert = record.bmiCategory == "Obesitas",
                    alertColor = StatusYellow
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Summary Temuan
            Text(
                text = "Temuan: ${record.clinicalDiagnosisSummary}",
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = if (record.needsReferral) StatusRed else MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Footer: Posyandu & Referral Status
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${record.posyanduName} • ${record.examinationDate}",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.outline
                )

                if (record.needsReferral) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = StatusRed.copy(alpha = 0.12f)
                    ) {
                        Text(
                            text = "RUJUK KE PUSKESMAS",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = StatusRed,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                } else {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = StatusGreen.copy(alpha = 0.12f)
                    ) {
                        Text(
                            text = "Resiko ${record.ptmRiskLevel}",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Medium,
                            color = StatusGreen,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ClinicalBadge(
    label: String,
    status: String,
    isAlert: Boolean,
    alertColor: Color
) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = if (isAlert) alertColor.copy(alpha = 0.12f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = label,
                fontSize = 11.5.sp,
                fontWeight = FontWeight.Bold,
                color = if (isAlert) alertColor else MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = if (status.length > 10) status.take(9) + ".." else status,
                fontSize = 9.5.sp,
                color = if (isAlert) alertColor else MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
