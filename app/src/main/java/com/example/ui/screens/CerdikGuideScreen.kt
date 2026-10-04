package com.example.ui.screens

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
import androidx.compose.material.icons.filled.Bloodtype
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.HealthAndSafety
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Straighten
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.StatusBlue
import com.example.ui.theme.StatusGreen
import com.example.ui.theme.StatusOrange
import com.example.ui.theme.StatusRed
import com.example.ui.theme.StatusYellow

@Composable
fun CerdikGuideScreen() {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("cerdik_guide_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Hero Header
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f)
                )
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.MenuBook,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Pedoman Skrining PTM Kemenkes",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Text(
                            text = "Standar Nilai Rujukan & Perilaku CERDIK",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                        )
                    }
                }
            }
        }

        // Section 1: Standar Nilai Normal Tekanan Darah (Hipertensi)
        item {
            GuideSectionCard(
                title = "Standar Tekanan Darah (Kemenkes RI)",
                icon = Icons.Default.Favorite,
                iconColor = StatusRed
            ) {
                StandardRow("Normal", "< 120 mmHg", "< 80 mmHg", StatusGreen)
                StandardRow("Pre-Hipertensi", "120 - 139 mmHg", "80 - 89 mmHg", StatusYellow)
                StandardRow("Hipertensi Tk. 1", "140 - 159 mmHg", "90 - 99 mmHg", StatusOrange)
                StandardRow("Hipertensi Tk. 2", "≥ 160 mmHg", "≥ 100 mmHg", StatusRed)
            }
        }

        // Section 2: Standar Nilai Gula Darah (Diabetes Melitus)
        item {
            GuideSectionCard(
                title = "Standar Gula Darah (PERKENI / Kemenkes)",
                icon = Icons.Default.Bloodtype,
                iconColor = StatusOrange
            ) {
                Text(
                    text = "A. Gula Darah Sewaktu (GDS):",
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                StandardRow("Normal", "< 140 mg/dL", "Aman", StatusGreen)
                StandardRow("Pre-Diabetes (TGT)", "140 - 199 mg/dL", "Perlu Evaluasi", StatusYellow)
                StandardRow("Diabetes Melitus", "≥ 200 mg/dL", "Positif DM (Rujuk)", StatusRed)

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "B. Gula Darah Puasa (GDP) (Puasa 8-10 Jam):",
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                StandardRow("Normal", "< 100 mg/dL", "Aman", StatusGreen)
                StandardRow("Pre-Diabetes (IFG)", "100 - 125 mg/dL", "Perlu Evaluasi", StatusYellow)
                StandardRow("Diabetes Melitus", "≥ 126 mg/dL", "Positif DM (Rujuk)", StatusRed)
            }
        }

        // Section 3: Standar Antropometri & Obesitas
        item {
            GuideSectionCard(
                title = "Standar IMT & Lingkar Perut",
                icon = Icons.Default.Straighten,
                iconColor = MaterialTheme.colorScheme.primary
            ) {
                Text(
                    text = "A. Indeks Massa Tubuh (IMT Asia Pasifik):",
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                )
                StandardRow("Sangat Kurus", "< 17.0", "Kekurangan BB Berat", StatusOrange)
                StandardRow("Kurus", "17.0 - 18.4", "Kekurangan BB Ringan", StatusYellow)
                StandardRow("Normal", "18.5 - 25.0", "Ideal", StatusGreen)
                StandardRow("Gemuk (Overweight)", "25.1 - 27.0", "Kelebihan BB", StatusYellow)
                StandardRow("Obesitas", "> 27.0", "Beresiko PTM Tinggi", StatusRed)

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "B. Obesitas Sentral (Lingkar Perut):",
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                )
                StandardRow("Laki-laki Normal", "≤ 90 cm", "Batas Maksimal", StatusGreen)
                StandardRow("Laki-laki Beresiko", "> 90 cm", "Obesitas Sentral", StatusRed)
                StandardRow("Perempuan Normal", "≤ 80 cm", "Batas Maksimal", StatusGreen)
                StandardRow("Perempuan Beresiko", "> 80 cm", "Obesitas Sentral", StatusRed)
            }
        }

        // Section 4: Prinsip CERDIK Kemenkes RI
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.HealthAndSafety,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Prilaku Hidup Sehat 'CERDIK'",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    CerdikItem("C", "Cek Kesehatan Secara Berkala", "Tensi, gula darah, berat badan, dan lingkar perut rutin di Posyandu/Puskesmas minimal sebulan sekali bagi usia produktif beresiko.")
                    CerdikItem("E", "Enyahkan Asap Rokok", "Berhenti merokok dan hindari paparan asap rokok pasif di rumah maupun tempat kerja.")
                    CerdikItem("R", "Rajin Aktivitas Fisik", "Lakukan aktivitas fisik sedang minimal 30 menit per hari atau 150 menit per minggu.")
                    CerdikItem("D", "Diet Sehat dengan Kalori Seimbang", "G4 G1 L5 per orang per hari: Maks 4 sdm Gula (50g), 1 sdt Garam (5g), 5 sdm Lemak/Minyak (67g). Konsumsi 5 porsi sayur & buah.")
                    CerdikItem("I", "Istirahat Cukup", "Tidur malam teratur 7-8 jam per hari untuk menjaga metabolisme tubuh.")
                    CerdikItem("K", "Kelola Stres", "Jaga kesehatan mental, rekreasi bersama keluarga, dan relaksasi untuk menjaga tekanan darah tetap stabil.")
                }
            }
        }
    }
}

@Composable
fun GuideSectionCard(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconColor: Color,
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(iconColor.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(imageVector = icon, contentDescription = null, tint = iconColor, modifier = Modifier.size(18.dp))
                }
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            content()
        }
    }
}

@Composable
fun StandardRow(kategori: String, sistol: String, diastol: String, color: Color) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(color)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = kategori,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
        Text(
            text = "$sistol | $diastol",
            fontSize = 11.5.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun CerdikItem(letter: String, title: String, description: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .size(28.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = letter,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
        }
        Spacer(modifier = Modifier.width(10.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = description,
                fontSize = 11.5.sp,
                lineHeight = 16.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
