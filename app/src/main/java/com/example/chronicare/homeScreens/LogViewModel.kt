package com.example.chronicare.homeScreens

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

// Same data class as in your screen, can be moved to a separate file if needed
//data class LogEntry(val date: String, val description: String, val status: String)
data class LogEntry(
    val date: String,
    val steps: Long = 0,
    val sleepHours: Float = 0f,
    val waterMl: Float = 0f
)


class LogViewModel : ViewModel() {

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

                /** ---------------------------
                 * 1️⃣ FETCH STEPS (Firestore)
                 * --------------------------- */
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

                /** ---------------------------
                 * 2️⃣ FETCH SLEEP + WATER (Realtime DB)
                 * --------------------------- */
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

                    val sleepLong = dateSnap.child("sleepHours").getValue(Long::class.java)
                    val waterLong = dateSnap.child("waterIntakeML").getValue(Long::class.java)

                    sleepMap[date] = sleepLong?.toFloat() ?: 0f
                    waterMap[date] = waterLong?.toFloat() ?: 0f

                }


                /** ---------------------------
                 * 3️⃣ MERGE BY DATE
                 * --------------------------- */
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

            } catch (e: Exception) {
                Log.e("LogViewModel", "Error fetching logs", e)
            } finally {
                isLoading.value = false
            }
        }
    }
}

