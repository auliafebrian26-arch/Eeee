package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.PtmClinicalEvaluator
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

    @Test
    fun `read string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("SI-PTM Posyandu", appName)
    }

    @Test
    fun `test bmi and central obesity calculation`() {
        val bmi = PtmClinicalEvaluator.calculateBmi(70.0, 170.0)
        assertEquals(24.2, bmi, 0.1)
        assertEquals("Normal", PtmClinicalEvaluator.classifyBmi(bmi))

        assertTrue(PtmClinicalEvaluator.isCentralObesity(92.0, "Laki-laki"))
        assertFalse(PtmClinicalEvaluator.isCentralObesity(88.0, "Laki-laki"))
        assertTrue(PtmClinicalEvaluator.isCentralObesity(82.0, "Perempuan"))
        assertFalse(PtmClinicalEvaluator.isCentralObesity(78.0, "Perempuan"))
    }

    @Test
    fun `test blood pressure and glucose classification`() {
        val bpNormal = PtmClinicalEvaluator.classifyBloodPressure(118, 76)
        assertEquals("Normal", bpNormal.first)
        assertFalse(bpNormal.second)

        val bpHypertension = PtmClinicalEvaluator.classifyBloodPressure(145, 95)
        assertEquals("Hipertensi Derajat 1", bpHypertension.first)
        assertTrue(bpHypertension.second)

        val bgGds = PtmClinicalEvaluator.classifyBloodGlucose("GDS", 210)
        assertEquals("Diabetes Melitus (DM)", bgGds.first)
        assertTrue(bgGds.second)

        val bgGdp = PtmClinicalEvaluator.classifyBloodGlucose("GDP", 95)
        assertEquals("Normal", bgGdp.first)
        assertFalse(bgGdp.second)
    }
}
