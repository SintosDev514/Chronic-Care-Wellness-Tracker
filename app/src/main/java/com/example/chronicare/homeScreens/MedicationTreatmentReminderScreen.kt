package com.example.chronicare.homeScreens

import androidx.compose.material.icons.filled.Add
import android.Manifest
import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.DatePickerDialog
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.example.chronicare.screens.SharedData
import java.text.SimpleDateFormat
import java.util.*

// --- Medication data class ---
data class MedicationReminder(
    val id: String = "",
    val medicationName: String = "",
    val dosage: String = "",
    val dateTime: String = "",
    var status: String = "Due" // "Due", "Taken", "Missed"
)

// --- Notification Receiver ---
class ReminderReceiver : BroadcastReceiver() {

    @SuppressLint("MissingPermission")
    override fun onReceive(context: Context, intent: Intent) {
        val notificationIdString = intent.getStringExtra("notificationId") ?: UUID.randomUUID().toString()
        val notificationId = notificationIdString.hashCode() // Use the same unique hash code as the PendingIntent
        val medicationName = intent.getStringExtra("medicationName") ?: "Medication"
        val dosage = intent.getStringExtra("dosage") ?: ""

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "reminder_channel",
                "Medication Reminders",
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager(context).createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(context, "reminder_channel")
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("Time to take your medication!")
            .setContentText("It's time to take $medicationName ($dosage).")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        // Only post notification if permission is granted (for Android 13+)
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED
            || Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU
        ) {
            notificationManager(context).notify(notificationId, notification)
        }
    }
}
private fun notificationManager(context: Context) =
    context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

// --- Schedule reminder ---
fun scheduleReminder(context: Context, reminder: MedicationReminder) {
    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    val sdf = SimpleDateFormat("MMM dd, yyyy, hh:mm a", Locale.getDefault())
    val calendar = Calendar.getInstance()

    try {
        calendar.time = sdf.parse(reminder.dateTime) ?: return
    } catch (e: Exception) {
        e.printStackTrace()
        return // Don't schedule if date is invalid
    }


    val intent = Intent(context, ReminderReceiver::class.java).apply {
        putExtra("notificationId", reminder.id) // <-- ADD THIS LINE
        putExtra("medicationName", reminder.medicationName)
        putExtra("dosage", reminder.dosage)
    }


    val pendingIntent = PendingIntent.getBroadcast(
        context,
        reminder.id.hashCode(), // unique requestCode per reminder
        intent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    // Ensure the alarm can wake up the device (best effort)
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !alarmManager.canScheduleExactAlarms()) {
        Toast.makeText(context, "Cannot schedule exact alarms. Please enable permission in settings.", Toast.LENGTH_LONG).show()
        return
    }

    alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)
}

// --- Main Screen Composable ---
val accentColorReminder = Color(0xFF007F7A)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MedicationTreatmentReminderScreen(sharedData: SharedData) {
    val context = LocalContext.current
    var showAddDialog by remember { mutableStateOf(false) }

    // Notification Permission Handling (Android 13+)
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (!isGranted) {
            Toast.makeText(context, "Notification permission is required for reminders.", Toast.LENGTH_LONG).show()
        }
    }

    // Request permission when the screen is first shown (if necessary)
    LaunchedEffect(key1 = true) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            launcher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = accentColorReminder
            ) {
                Icon(Icons.Filled.Add, contentDescription = "Add Reminder", tint = Color.White)
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            Text(
                text = "Medication Reminders",
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier.padding(bottom = 16.dp),
                color = accentColorReminder
            )

            // Use LazyColumn and get medications from SharedData
            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(sharedData.medicationReminders, key = { it.id }) { medication ->
                    MedicationReminderCard(
                        medication = medication,
                        onStatusChange = { newStatus ->
                            sharedData.updateMedicationStatus(medication.id, newStatus)
                        }
                    )
                }
            }
        }
    }

    if (showAddDialog) {
        AddReminderDialog(
            onDismiss = { showAddDialog = false },
            onAdd = { name, dosage, dateTime ->
                val newId = UUID.randomUUID().toString()
                val newReminder = MedicationReminder(
                    id = newId,
                    medicationName = name,
                    dosage = dosage,
                    dateTime = dateTime,
                    status = "Due"
                )
                sharedData.addMedicationReminder(newReminder)
                scheduleReminder(context, newReminder)
                showAddDialog = false
            }
        )
    }
}

@Composable
fun AddReminderDialog(onDismiss: () -> Unit, onAdd: (String, String, String) -> Unit) {
    val context = LocalContext.current
    var medicationName by remember { mutableStateOf("") }
    var dosage by remember { mutableStateOf("") }
    var dateTimeDisplay by remember { mutableStateOf("") }
    val calendar = remember { Calendar.getInstance() }

    val sdf = SimpleDateFormat("MMM dd, yyyy, hh:mm a", Locale.getDefault())

    // Time picker
    val timePickerDialog = TimePickerDialog(
        context,
        { _, hourOfDay, minute ->
            calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
            calendar.set(Calendar.MINUTE, minute)
            dateTimeDisplay = sdf.format(calendar.time)
        },
        calendar.get(Calendar.HOUR_OF_DAY),
        calendar.get(Calendar.MINUTE),
        false // 12-hour format
    )

    // Date picker shows time picker after date selected
    val datePickerDialog = DatePickerDialog(
        context,
        { _, year, month, dayOfMonth ->
            calendar.set(Calendar.YEAR, year)
            calendar.set(Calendar.MONTH, month)
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            timePickerDialog.show()
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text("Add New Reminder", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                OutlinedTextField(
                    value = medicationName,
                    onValueChange = { medicationName = it },
                    label = { Text("Medication Name") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = dosage,
                    onValueChange = { dosage = it },
                    label = { Text("Dosage") },
                    modifier = Modifier.fillMaxWidth()
                )

                // Clickable field to show the date picker
                Box(modifier = Modifier
                    .fillMaxWidth()
                    .clickable { datePickerDialog.show() }
                ) {
                    OutlinedTextField(
                        value = dateTimeDisplay,
                        onValueChange = {},
                        label = { Text("Date & Time") },
                        readOnly = true, // keep it read-only; user taps to open pickers
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    TextButton(onClick = onDismiss) { Text("Cancel") }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(onClick = {
                        if (medicationName.isNotBlank() && dosage.isNotBlank() && dateTimeDisplay.isNotBlank()) {
                            onAdd(medicationName, dosage, dateTimeDisplay)
                        } else {
                            Toast.makeText(context, "Please fill all fields and select a date/time", Toast.LENGTH_SHORT).show()
                        }
                    }) {
                        Text("Add")
                    }
                }
            }
        }
    }
}

// --- Reminder Card UI ---
@Composable
fun MedicationReminderCard(medication: MedicationReminder, onStatusChange: (String) -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(medication.medicationName, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold)
                Text(medication.dateTime, style = MaterialTheme.typography.bodyMedium, color = accentColorReminder)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text("Dosage: ${medication.dosage}", style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                "Status: ${medication.status}",
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                color = when (medication.status) {
                    "Due" -> Color(0xFFFFA500)
                    "Taken" -> Color(0xFF10B981)
                    "Missed" -> Color(0xFFF44336)
                    else -> Color.Gray
                }
            )

            if (medication.status == "Due") {
                Spacer(modifier = Modifier.height(16.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick = { onStatusChange("Taken") },
                        colors = ButtonDefaults.buttonColors(containerColor = accentColorReminder),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Mark as Taken", color = Color.White)
                    }
                    Button(
                        onClick = { onStatusChange("Missed") },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F)),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Mark as Missed", color = Color.White)
                    }
                }
            }
        }
    }
}
