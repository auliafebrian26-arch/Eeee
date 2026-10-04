package com.example.util

import android.content.Context
import android.content.Intent
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.widget.Toast
import androidx.core.content.FileProvider
import com.example.data.PtmRecord
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStreamWriter
import java.nio.charset.StandardCharsets
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object ExportManager {

    /**
     * Ekspor Data Skrining PTM ke File Excel (Format CSV dengan UTF-8 BOM untuk Microsoft Excel)
     */
    fun exportToExcel(
        context: Context,
        records: List<PtmRecord>,
        reportPeriod: String = "Semua Periode"
    ): File? {
        try {
            val reportsDir = File(context.cacheDir, "reports")
            if (!reportsDir.exists()) reportsDir.mkdirs()

            val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
            val cleanPeriod = reportPeriod.replace(" ", "_").replace("/", "-")
            val fileName = "Laporan_PTM_${cleanPeriod}_$timeStamp.csv"
            val file = File(reportsDir, fileName)

            FileOutputStream(file).use { fos ->
                // Tulis UTF-8 BOM agar Excel di Windows & Android membaca format teks dan aksen dengan sempurna
                fos.write(byteArrayOf(0xEF.toByte(), 0xBB.toByte(), 0xBF.toByte()))
                OutputStreamWriter(fos, StandardCharsets.UTF_8).use { writer ->
                    // Header Dokumen
                    writer.append("\"LAPORAN BULANAN PELAYANAN SKRINING PENYAKIT TIDAK MENULAR (PTM)\"\n")
                    writer.append("\"KEMENTERIAN KESEHATAN RI - PUSKESMAS & POSYANDU USIA PRODUKTIF (18-59 TAHUN)\"\n")
                    writer.append("\"Periode: $reportPeriod\"\n")
                    writer.append("\"Waktu Unduh: ${SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.getDefault()).format(Date())}\"\n")
                    writer.append("\"Total Peserta: ${records.size} Orang\"\n\n")

                    // Ringkasan Statistik
                    val totalLaki = records.count { it.gender.equals("Laki-laki", ignoreCase = true) }
                    val totalPerempuan = records.count { it.gender.equals("Perempuan", ignoreCase = true) }
                    val totalHipertensi = records.count { it.isHypertension }
                    val totalDm = records.count { it.isDiabetes }
                    val totalObesitas = records.count { it.isCentralObesity || it.bmiCategory == "Obesitas" }
                    val totalPerokok = records.count { it.isSmoking }
                    val totalRujuk = records.count { it.needsReferral }

                    writer.append("\"RINGKASAN STATISTIK PUSKESMAS:\"\n")
                    writer.append("\"Laki-laki: $totalLaki Orang\";\"Perempuan: $totalPerempuan Orang\";\"Hipertensi: $totalHipertensi Orang\";\"Diabetes Melitus: $totalDm Orang\";\"Obesitas: $totalObesitas Orang\";\"Perokok: $totalPerokok Orang\";\"Rujuk Puskesmas: $totalRujuk Orang\"\n\n")

                    // Header Kolom Tabel
                    val headers = listOf(
                        "No",
                        "Tanggal Periksa",
                        "Nama Petugas",
                        "Posyandu / Unit",
                        "NIK",
                        "Nama Lengkap",
                        "Usia (Th)",
                        "Jenis Kelamin",
                        "Status Nikah",
                        "Pekerjaan",
                        "No. HP",
                        "Alamat",
                        "Merokok",
                        "Jml Rokok/Hari",
                        "Aktivitas Fisik",
                        "Konsumsi Sayur/Buah",
                        "Riwayat Keluarga",
                        "BB (kg)",
                        "TB (cm)",
                        "IMT",
                        "Kategori IMT",
                        "Lingkar Perut (cm)",
                        "Obesitas Sentral",
                        "TD Sistol (mmHg)",
                        "TD Diastol (mmHg)",
                        "Kategori TD",
                        "Status Hipertensi",
                        "Jenis Cek Gula",
                        "Gula Darah (mg/dL)",
                        "Kategori Gula Darah",
                        "Status Diabetes",
                        "Kolesterol (mg/dL)",
                        "Asam Urat (mg/dL)",
                        "Tingkat Resiko PTM",
                        "Rujuk Puskesmas",
                        "Ringkasan Temuan",
                        "Rekomendasi Petugas",
                        "Catatan"
                    )

                    writer.append(headers.joinToString(";") { "\"$it\"" })
                    writer.append("\n")

                    // Baris Data
                    records.forEachIndexed { index, r ->
                        val row = listOf(
                            (index + 1).toString(),
                            r.examinationDate,
                            r.petugasName,
                            r.posyanduName,
                            "'" + r.nik, // Tanda kutip tunggal agar NIK tidak diubah format oleh Excel jadi angka eksponensial
                            r.fullName,
                            r.age.toString(),
                            r.gender,
                            r.maritalStatus,
                            r.occupation,
                            r.phoneNumber,
                            r.address,
                            if (r.isSmoking) "Iya" else "Tidak",
                            r.cigarettesPerDay.toString(),
                            if (r.doesPhysicalActivity) "Iya (Cukup)" else "Tidak (<150 mnt/mgg)",
                            if (r.consumesFruitVeggie) "Cukup" else "Kurang",
                            if (r.familyHistoryPtm) "Ada" else "Tidak Ada",
                            r.weightKg.toString(),
                            r.heightCm.toString(),
                            r.bmi.toString(),
                            r.bmiCategory,
                            r.waistCircumferenceCm.toString(),
                            if (r.isCentralObesity) "Ya (Beresiko)" else "Normal",
                            r.systolicBp.toString(),
                            r.diastolicBp.toString(),
                            r.bloodPressureCategory,
                            if (r.isHypertension) "POSITIF HIPERTENSI" else "Normal/Pre",
                            r.bloodGlucoseType,
                            r.bloodGlucoseValue.toString(),
                            r.bloodGlucoseCategory,
                            if (r.isDiabetes) "POSITIF DIABETES (DM)" else "Normal/Pre",
                            r.cholesterolValue?.toString() ?: "-",
                            r.uricAcidValue?.toString() ?: "-",
                            r.ptmRiskLevel,
                            if (r.needsReferral) "YA (RUJUK KE PUSKESMAS)" else "Tidak Perlu",
                            r.clinicalDiagnosisSummary.replace(";", ","),
                            r.recommendation.replace("\n", " ").replace(";", ","),
                            r.notes.replace("\n", " ").replace(";", ",")
                        )
                        writer.append(row.joinToString(";") { "\"${it.replace("\"", "\"\"")}\"" })
                        writer.append("\n")
                    }
                    writer.flush()
                }
            }
            return file
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    /**
     * Ekspor Laporan Bulanan Puskesmas ke Dokumen PDF Resmi
     */
    fun exportToPdf(
        context: Context,
        records: List<PtmRecord>,
        reportPeriod: String = "Semua Periode",
        puskesmasName: String = "Puskesmas Pembina Kecamatan",
        petugasSigner: String = "Pengelola Program PTM"
    ): File? {
        val pdfDocument = PdfDocument()

        try {
            // A4 Landscape Dimensions in points (72 points/inch)
            val pageWidth = 842
            val pageHeight = 595
            val margin = 36f

            val paint = Paint().apply { isAntiAlias = true }
            val boldPaint = Paint().apply {
                isAntiAlias = true
                isFakeBoldText = true
            }

            val recordsPerPage = 12
            val totalPages = if (records.isEmpty()) 1 else ((records.size - 1) / recordsPerPage) + 1

            for (pageIndex in 0 until totalPages) {
                val pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageIndex + 1).create()
                val page = pdfDocument.startPage(pageInfo)
                val canvas = page.canvas

                // Latar belakang putih
                canvas.drawColor(Color.WHITE)

                var y = margin + 14f

                // KOP SURAT RESMI PUSKESMAS (Halaman Pertama)
                if (pageIndex == 0) {
                    // Header Bar dekoratif
                    paint.color = Color.rgb(0, 109, 91) // Teal Primary
                    canvas.drawRect(margin, margin - 10f, pageWidth - margin, margin - 4f, paint)

                    boldPaint.color = Color.rgb(20, 30, 25)
                    boldPaint.textSize = 14f
                    boldPaint.textAlign = Paint.Align.CENTER
                    canvas.drawText("PEMERINTAH KABUPATEN / KOTA - DINAS KESEHATAN", pageWidth / 2f, y, boldPaint)
                    y += 16f

                    boldPaint.textSize = 15f
                    boldPaint.color = Color.rgb(0, 109, 91)
                    canvas.drawText(puskesmasName.uppercase(Locale.getDefault()), pageWidth / 2f, y, boldPaint)
                    y += 14f

                    paint.textSize = 10f
                    paint.color = Color.rgb(80, 80, 80)
                    paint.textAlign = Paint.Align.CENTER
                    canvas.drawText("Pelayanan Terpadu Penyakit Tidak Menular (PTM) Usia Produktif (18 - 59 Tahun)", pageWidth / 2f, y, paint)
                    y += 12f

                    boldPaint.textSize = 11f
                    boldPaint.color = Color.rgb(40, 40, 40)
                    canvas.drawText("LAPORAN BULANAN SKRINING PTM - PERIODE: ${reportPeriod.uppercase(Locale.getDefault())}", pageWidth / 2f, y, boldPaint)
                    y += 10f

                    // Garis Pemisah Kop Surat
                    paint.color = Color.rgb(0, 109, 91)
                    paint.strokeWidth = 2f
                    canvas.drawLine(margin, y, pageWidth - margin, y, paint)
                    y += 16f

                    // REKAPITULASI KOTAK STATISTIK
                    val totalLaki = records.count { it.gender.equals("Laki-laki", ignoreCase = true) }
                    val totalPerempuan = records.count { it.gender.equals("Perempuan", ignoreCase = true) }
                    val totalHipertensi = records.count { it.isHypertension }
                    val totalDm = records.count { it.isDiabetes }
                    val totalObesitas = records.count { it.isCentralObesity || it.bmiCategory == "Obesitas" }
                    val totalRujuk = records.count { it.needsReferral }

                    val boxWidth = (pageWidth - (margin * 2) - 30f) / 6f
                    val boxHeight = 40f

                    val stats = listOf(
                        Pair("Total Peserta", "${records.size} Org"),
                        Pair("L / P", "$totalLaki / $totalPerempuan"),
                        Pair("Hipertensi", "$totalHipertensi Kasus"),
                        Pair("Diabetes (DM)", "$totalDm Kasus"),
                        Pair("Obesitas", "$totalObesitas Kasus"),
                        Pair("Rujuk Puskesmas", "$totalRujuk Org")
                    )

                    stats.forEachIndexed { i, stat ->
                        val boxX = margin + (i * (boxWidth + 6f))
                        paint.style = Paint.Style.FILL
                        paint.color = if (i == 2 || i == 3 || i == 5) Color.rgb(255, 235, 238) else Color.rgb(240, 248, 246)
                        canvas.drawRoundRect(boxX, y, boxX + boxWidth, y + boxHeight, 4f, 4f, paint)

                        paint.style = Paint.Style.STROKE
                        paint.strokeWidth = 1f
                        paint.color = if (i == 2 || i == 3 || i == 5) Color.rgb(239, 154, 154) else Color.rgb(178, 223, 219)
                        canvas.drawRoundRect(boxX, y, boxX + boxWidth, y + boxHeight, 4f, 4f, paint)

                        paint.style = Paint.Style.FILL
                        paint.color = Color.rgb(100, 100, 100)
                        paint.textSize = 8.5f
                        paint.textAlign = Paint.Align.CENTER
                        canvas.drawText(stat.first, boxX + boxWidth / 2f, y + 15f, paint)

                        boldPaint.color = if (i == 2 || i == 3 || i == 5) Color.rgb(198, 40, 40) else Color.rgb(0, 109, 91)
                        boldPaint.textSize = 11f
                        boldPaint.textAlign = Paint.Align.CENTER
                        canvas.drawText(stat.second, boxX + boxWidth / 2f, y + 31f, boldPaint)
                    }
                    y += boxHeight + 14f
                } else {
                    // Header ringkas pada halaman lanjutan
                    boldPaint.textSize = 10f
                    boldPaint.color = Color.rgb(0, 109, 91)
                    boldPaint.textAlign = Paint.Align.LEFT
                    canvas.drawText("Lanjutan Laporan Skrining PTM - Periode: $reportPeriod (Hal ${pageIndex + 1}/$totalPages)", margin, y, boldPaint)
                    y += 12f
                    paint.color = Color.rgb(180, 180, 180)
                    paint.strokeWidth = 1f
                    canvas.drawLine(margin, y, pageWidth - margin, y, paint)
                    y += 10f
                }

                // TABEL DATA SKRINING PTM
                // Kolom:
                // No (25), Tgl (50), NIK (75), Nama Pasien (95), Usia/JK (45), Merokok (40), IMT & LP (70), Tensi (75), Gula Darah (70), Temuan / Resiko (95), Tindak Lanjut (60)
                val colWidths = floatArrayOf(24f, 48f, 74f, 96f, 48f, 42f, 78f, 78f, 74f, 96f, 62f)
                val colHeaders = arrayOf(
                    "No", "Tgl", "NIK", "Nama Lengkap", "Usia/JK", "Rokok", "Antropometri", "Tekanan Darah", "Gula Darah", "Diagnosa Skrining", "Status/Rujuk"
                )

                // Header Tabel
                val tableHeaderHeight = 20f
                paint.style = Paint.Style.FILL
                paint.color = Color.rgb(0, 109, 91)
                canvas.drawRect(margin, y, pageWidth - margin, y + tableHeaderHeight, paint)

                boldPaint.textSize = 8.5f
                boldPaint.color = Color.WHITE
                boldPaint.textAlign = Paint.Align.CENTER

                var curX = margin
                colHeaders.forEachIndexed { i, title ->
                    val w = colWidths[i]
                    canvas.drawText(title, curX + (w / 2f), y + 13f, boldPaint)
                    curX += w
                }
                y += tableHeaderHeight

                // Baris Data
                val startIdx = pageIndex * recordsPerPage
                val endIdx = minOf(startIdx + recordsPerPage, records.size)
                val rowHeight = 22f

                for (idx in startIdx until endIdx) {
                    val r = records[idx]
                    val isEven = (idx % 2 == 0)

                    paint.style = Paint.Style.FILL
                    paint.color = if (r.needsReferral) Color.rgb(255, 243, 240) else if (isEven) Color.rgb(249, 252, 251) else Color.WHITE
                    canvas.drawRect(margin, y, pageWidth - margin, y + rowHeight, paint)

                    // Border bawah baris
                    paint.color = Color.rgb(220, 230, 228)
                    paint.strokeWidth = 0.5f
                    canvas.drawLine(margin, y + rowHeight, pageWidth - margin, y + rowHeight, paint)

                    paint.style = Paint.Style.FILL
                    paint.textSize = 7.5f
                    paint.color = Color.rgb(40, 40, 40)
                    paint.textAlign = Paint.Align.CENTER

                    var cellX = margin

                    // 0: No
                    canvas.drawText((idx + 1).toString(), cellX + colWidths[0] / 2f, y + 14f, paint)
                    cellX += colWidths[0]

                    // 1: Tanggal
                    canvas.drawText(r.examinationDate.takeLast(5), cellX + colWidths[1] / 2f, y + 14f, paint)
                    cellX += colWidths[1]

                    // 2: NIK
                    val shortNik = if (r.nik.length >= 16) "${r.nik.take(6)}...${r.nik.takeLast(4)}" else r.nik
                    canvas.drawText(shortNik, cellX + colWidths[2] / 2f, y + 14f, paint)
                    cellX += colWidths[2]

                    // 3: Nama Lengkap
                    paint.textAlign = Paint.Align.LEFT
                    val displayName = if (r.fullName.length > 17) r.fullName.take(15) + ".." else r.fullName
                    canvas.drawText(displayName, cellX + 4f, y + 14f, paint)
                    cellX += colWidths[3]

                    // 4: Usia/JK
                    paint.textAlign = Paint.Align.CENTER
                    val jkShort = if (r.gender.startsWith("L", ignoreCase = true)) "L" else "P"
                    canvas.drawText("${r.age} th ($jkShort)", cellX + colWidths[4] / 2f, y + 14f, paint)
                    cellX += colWidths[4]

                    // 5: Rokok
                    canvas.drawText(if (r.isSmoking) "Ya (${r.cigarettesPerDay})" else "Tidak", cellX + colWidths[5] / 2f, y + 14f, paint)
                    cellX += colWidths[5]

                    // 6: Antropometri
                    val antroText = "IMT:${r.bmi} LP:${r.waistCircumferenceCm.toInt()}cm"
                    canvas.drawText(antroText, cellX + colWidths[6] / 2f, y + 14f, paint)
                    cellX += colWidths[6]

                    // 7: Tekanan Darah
                    val bpText = "${r.systolicBp}/${r.diastolicBp}"
                    if (r.isHypertension) {
                        boldPaint.color = Color.rgb(198, 40, 40)
                        boldPaint.textSize = 7.5f
                        boldPaint.textAlign = Paint.Align.CENTER
                        canvas.drawText("$bpText (HT)", cellX + colWidths[7] / 2f, y + 14f, boldPaint)
                    } else {
                        canvas.drawText(bpText, cellX + colWidths[7] / 2f, y + 14f, paint)
                    }
                    cellX += colWidths[7]

                    // 8: Gula Darah
                    val gdText = "${r.bloodGlucoseType}: ${r.bloodGlucoseValue}"
                    if (r.isDiabetes) {
                        boldPaint.color = Color.rgb(198, 40, 40)
                        boldPaint.textSize = 7.5f
                        boldPaint.textAlign = Paint.Align.CENTER
                        canvas.drawText("$gdText (DM)", cellX + colWidths[8] / 2f, y + 14f, boldPaint)
                    } else {
                        canvas.drawText(gdText, cellX + colWidths[8] / 2f, y + 14f, paint)
                    }
                    cellX += colWidths[8]

                    // 9: Ringkasan Diagnosa Skrining
                    paint.textAlign = Paint.Align.LEFT
                    val diagText = if (r.clinicalDiagnosisSummary.length > 21) {
                        r.clinicalDiagnosisSummary.take(19) + ".."
                    } else {
                        r.clinicalDiagnosisSummary
                    }
                    canvas.drawText(diagText, cellX + 3f, y + 14f, paint)
                    cellX += colWidths[9]

                    // 10: Status/Rujuk
                    paint.textAlign = Paint.Align.CENTER
                    if (r.needsReferral) {
                        boldPaint.color = Color.rgb(198, 40, 40)
                        boldPaint.textSize = 7.5f
                        boldPaint.textAlign = Paint.Align.CENTER
                        canvas.drawText("RUJUK PUSK", cellX + colWidths[10] / 2f, y + 14f, boldPaint)
                    } else {
                        canvas.drawText("Edukasi/Mgg", cellX + colWidths[10] / 2f, y + 14f, paint)
                    }

                    y += rowHeight
                }

                // Tanda Tangan Resmi (Pada Halaman Terakhir)
                if (pageIndex == totalPages - 1) {
                    y = pageHeight - 88f
                    val signDate = SimpleDateFormat("dd MMMM yyyy", Locale("id", "ID")).format(Date())

                    paint.textSize = 8.5f
                    paint.color = Color.rgb(40, 40, 40)
                    paint.textAlign = Paint.Align.CENTER

                    // Kolom Kiri: Mengetahui Kepala Puskesmas
                    val leftCenter = margin + 110f
                    canvas.drawText("Mengetahui,", leftCenter, y, paint)
                    canvas.drawText("Kepala Puskesmas", leftCenter, y + 12f, paint)
                    boldPaint.textSize = 9f
                    boldPaint.color = Color.rgb(20, 20, 20)
                    boldPaint.textAlign = Paint.Align.CENTER
                    canvas.drawText("( dr. H. Rahmat Hidayat, M.Kes )", leftCenter, y + 54f, boldPaint)
                    paint.textSize = 7.5f
                    canvas.drawText("NIP. 19780512 200501 1 004", leftCenter, y + 64f, paint)

                    // Kolom Kanan: Petugas Pelaksana / Pengelola Program PTM
                    val rightCenter = pageWidth - margin - 120f
                    paint.textSize = 8.5f
                    canvas.drawText("Dibuat di Puskesmas, $signDate", rightCenter, y, paint)
                    canvas.drawText(petugasSigner, rightCenter, y + 12f, paint)
                    val samplePetugasName = records.firstOrNull()?.petugasName ?: "Petugas Pelaksana PTM"
                    canvas.drawText("( $samplePetugasName )", rightCenter, y + 54f, boldPaint)
                    canvas.drawText("Pengelola Program PTM Posyandu", rightCenter, y + 64f, paint)
                }

                // Footer Halaman
                paint.textSize = 7.5f
                paint.color = Color.rgb(120, 120, 120)
                paint.textAlign = Paint.Align.LEFT
                canvas.drawText("SI-PTM Posyandu - Sistem Informasi Skrining PTM Kemenkes RI", margin, pageHeight - 12f, paint)
                paint.textAlign = Paint.Align.RIGHT
                canvas.drawText("Halaman ${pageIndex + 1} dari $totalPages", pageWidth - margin, pageHeight - 12f, paint)

                pdfDocument.finishPage(page)
            }

            val reportsDir = File(context.cacheDir, "reports")
            if (!reportsDir.exists()) reportsDir.mkdirs()

            val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
            val cleanPeriod = reportPeriod.replace(" ", "_").replace("/", "-")
            val pdfFile = File(reportsDir, "Laporan_Bulanan_PTM_${cleanPeriod}_$timeStamp.pdf")

            FileOutputStream(pdfFile).use { fos ->
                pdfDocument.writeTo(fos)
            }

            return pdfFile
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        } finally {
            pdfDocument.close()
        }
    }

    /**
     * Membuka File (PDF / CSV / Excel) melalui Intent Android
     */
    fun openFile(context: Context, file: File, mimeType: String) {
        try {
            val uri: Uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file
            )
            val intent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(uri, mimeType)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(Intent.createChooser(intent, "Buka Berkas Laporan"))
        } catch (e: Exception) {
            Toast.makeText(context, "Gagal membuka berkas: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    /**
     * Bagikan File Laporan (WhatsApp, Email, Drive, Print)
     */
    fun shareFile(context: Context, file: File, mimeType: String, subjectTitle: String) {
        try {
            val uri: Uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file
            )
            val intent = Intent(Intent.ACTION_SEND).apply {
                type = mimeType
                putExtra(Intent.EXTRA_STREAM, uri)
                putExtra(Intent.EXTRA_SUBJECT, subjectTitle)
                putExtra(Intent.EXTRA_TEXT, "Berikut terlampir $subjectTitle dari aplikasi SI-PTM Posyandu Puskesmas.")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            context.startActivity(Intent.createChooser(intent, "Bagikan Laporan PTM"))
        } catch (e: Exception) {
            Toast.makeText(context, "Gagal membagikan berkas: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }
}
