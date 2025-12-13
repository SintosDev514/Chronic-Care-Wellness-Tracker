package com.example.chronicare.homeScreens

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

// Same data class as in your screen, can be moved to a separate file if needed
data class LogEntry(val date: String, val description: String, val status: String)

class LogViewModel : ViewModel() {
    // A private mutable state for the list of logs
    private val _dailyLogs = mutableStateOf<List<LogEntry>>(emptyList())
    // An immutable public state that the UI will observe
    val dailyLogs = _dailyLogs

    // A state to track whether data is currently being loaded
    val isLoading = mutableStateOf(true)

    init {
        // Automatically fetch the logs when the ViewModel is created
        fetchDailyLogs()
    }

    private fun fetchDailyLogs() {
        // Use viewModelScope to launch a coroutine that is automatically cancelled
        // when the ViewModel is cleared.
        viewModelScope.launch {
            try {
                // Get the current user's ID. If not logged in, we can't fetch data.
                val userId = FirebaseAuth.getInstance().currentUser?.uid
                if (userId == null) {
                    Log.e("LogViewModel", "User is not logged in. Cannot fetch logs.")
                    isLoading.value = false // Stop loading
                    return@launch
                }

                val db = FirebaseFirestore.getInstance()

                // Firestore query: Go to the user's document, then to the 'dailySteps' collection,
                // and order the results by the document ID (which is the date string) in descending order.
                val documents = db.collection("users")
                    .document(userId)
                    .collection("dailySteps")
                    .orderBy(com.google.firebase.firestore.FieldPath.documentId(), Query.Direction.DESCENDING)
                    .get()
                    .await() // .await() is a helper from the coroutines library to wait for the task to complete

                // Map the Firestore documents to our LogEntry data class
                val logs = documents.map { doc ->
                    val steps = doc.getLong("steps") ?: 0
                    LogEntry(
                        date = doc.id, // The document ID is our date "yyyy-MM-dd"
                        description = "You walked $steps steps.",
                        status = if (steps > 5000) "Completed" else "Pending" // Example status logic
                    )
                }

                // Update the state with the new list of logs
                _dailyLogs.value = logs
                Log.d("LogViewModel", "Successfully fetched ${logs.size} daily logs.")

            } catch (e: Exception) {
                // Handle any errors during the fetch, e.g., network issues
                Log.e("LogViewModel", "Error fetching daily logs", e)
            } finally {
                // Ensure the loading indicator is turned off, even if there was an error
                isLoading.value = false
            }
        }
    }
}
