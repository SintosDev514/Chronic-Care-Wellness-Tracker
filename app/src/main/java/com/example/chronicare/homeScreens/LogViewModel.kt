package com.example.chronicare.homeScreens

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chronicare.model.HealthInsight
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

data class LogEntry(
    val date: String,
    val steps: Long = 0,
    val sleepHours: Float = 0f,
    val waterMl: Float = 0f
)

class LogViewModel : ViewModel() {

    val healthInsights = mutableStateOf<List<HealthInsight>>(emptyList())

    private val _dailyLogs = mutableStateOf<List<LogEntry>>(emptyList())
    val dailyLogs = _dailyLogs

    val isLoading = mutableStateOf(true)

    init {
        fetchDailyLogs()
    }

    private fun fetchDailyLogs() {
        viewModelScope.launch {
            try {
                val userId = FirebaseAuth.getInstance().currentUser?.uid
                if (userId == null) {
                    isLoading.value = false
                    return@launch
                }

                val firestore = FirebaseFirestore.getInstance()
                val realtimeDb = FirebaseDatabase.getInstance().reference

                // 1️⃣ Fetch steps (Firestore)
                val stepDocs = firestore.collection("users")
                    .document(userId)
                    .collection("dailySteps")
                    .orderBy(
                        com.google.firebase.firestore.FieldPath.documentId(),
                        Query.Direction.DESCENDING
                    )
                    .get()
                    .await()

                val stepMap = mutableMapOf<String, Long>()
                for (doc in stepDocs) {
                    stepMap[doc.id] = doc.getLong("steps") ?: 0L
                }

                // 2️⃣ Fetch sleep & water (Realtime DB)
                val healthSnapshot = realtimeDb
                    .child("users")
                    .child(userId)
                    .child("health_logs")
                    .get()
                    .await()

                val sleepMap = mutableMapOf<String, Float>()
                val waterMap = mutableMapOf<String, Float>()

                for (dateSnap in healthSnapshot.children) {
                    val date = dateSnap.key ?: continue
                    sleepMap[date] =
                        dateSnap.child("sleepHours").getValue(Long::class.java)?.toFloat() ?: 0f
                    waterMap[date] =
                        dateSnap.child("waterIntakeML").getValue(Long::class.java)?.toFloat() ?: 0f
                }

                // 3️⃣ Merge by date
                val allDates = (stepMap.keys + sleepMap.keys + waterMap.keys)
                    .distinct()
                    .sortedDescending()

                val logs = allDates.map { date ->
                    LogEntry(
                        date = date,
                        steps = stepMap[date] ?: 0,
                        sleepHours = sleepMap[date] ?: 0f,
                        waterMl = waterMap[date] ?: 0f
                    )
                }

                _dailyLogs.value = logs
                buildHealthInsights(logs)

            } catch (e: Exception) {
                Log.e("LogViewModel", "Error fetching logs", e)
            } finally {
                isLoading.value = false
            }
        }
    }

    private fun buildHealthInsights(logs: List<LogEntry>) {
        if (logs.isEmpty()) return

        val latest = logs.first()

        healthInsights.value = listOf(
            HealthInsight(
                title = "Steps Walked",
                value = latest.steps.toString(),
                description = "Steps recorded for the latest day"
            ),
            HealthInsight(
                title = "Sleep Duration",
                value = "${latest.sleepHours} hrs",
                description = "Sleep duration from last night"
            ),
            HealthInsight(
                title = "Water Intake",
                value = "${latest.waterMl / 1000} L",
                description = "Water consumed "
            )
        )
    }
}
