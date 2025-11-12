package com.example.chronicare.homeScreens

import android.Manifest
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
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import java.text.SimpleDateFormat
import java.util.*

// --- Medication data class ---
data class MedicationReminder(
    val id: Int,
    val medicationName: String,
    val dosage: String,
    val dateTime: String,
    var status: String
)

private val sampleMedications = listOf(
    MedicationReminder(
        id = 1,
        medicationName = "Aspirin",
        dosage = "100mg",
        dateTime = "Oct 26, 2025 08:00 AM",
        status = "Due"
    )
)

// --- Notification Receiver ---
class ReminderReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val medicationName = intent.getStringExtra("medicationName") ?: "Medication"
        val dosage = intent.getStringExtra("dosage") ?: ""

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "reminder_channel",
                "Medication Reminders",
                NotificationManager.IMPORTANCE_HIGH
            ).apply { description = "Channel for medication reminders" }
            val notificationManager = context.getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(context, "reminder_channel")
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("Medication Reminder")
            .setContentText("Time to take $medicationName ($dosage)")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            NotificationManagerCompat.from(context).notify(Random().nextInt(), notification)
        }
    }
}

// --- Schedule reminder ---
fun scheduleReminder(context: Context, reminder: MedicationReminder) {
    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    val sdf = SimpleDateFormat("MMM dd, yyyy hh:mm a", Locale.getDefault())
    val calendar = Calendar.getInstance()
    try {
        val time = sdf.parse(reminder.dateTime)
        calendar.time = time ?: Date()
        val now = Calendar.getInstance()
        if (calendar.before(now)) calendar.add(Calendar.DAY_OF_MONTH, 1)
    } catch (e: Exception) {
        calendar.add(Calendar.MINUTE, 1)
    }

    val intent = Intent(context, ReminderReceiver::class.java).apply {
        putExtra("medicationName", reminder.medicationName)
        putExtra("dosage", reminder.dosage)
    }

    val pendingIntent = PendingIntent.getBroadcast(
        context,
        reminder.id,
        intent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)
}

// --- UI Colors ---
val accentColorReminder = Color(0xFF007F7A)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MedicationTreatmentReminderScreen() {
    val context = LocalContext.current
    var medications by remember { mutableStateOf(sampleMedications.toMutableList()) }
    var showAddDialog by remember { mutableStateOf(false) }
    var nextId by remember { mutableStateOf(2) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Medication Treatment Reminder",
            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
            modifier = Modifier.padding(bottom = 16.dp),
            color = accentColorReminder
        )

        medications.forEach { medication ->
            MedicationReminderCard(
                medication = medication,
                onStatusChange = { newStatus ->
                    medications = medications.map {
                        if (it.id == medication.id) it.copy(status = newStatus) else it
                    }.toMutableList()
                }
            )
            Spacer(modifier = Modifier.height(12.dp))
        }

        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.BottomEnd
        ) {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(16.dp)
            ) {
                Text("+", style = MaterialTheme.typography.headlineMedium, color = Color.White)
            }
        }
    }

    if (showAddDialog) {
        AddReminderDialog(
            onDismiss = { showAddDialog = false },
            onAdd = { name, dosage, dateTime ->
                val newReminder = MedicationReminder(
                    id = nextId++,
                    medicationName = name,
                    dosage = dosage,
                    dateTime = dateTime,
                    status = "Due"
                )
                medications = (medications + newReminder).toMutableList()
                scheduleReminder(context, newReminder)
                showAddDialog = false
            }
        )
    }
}

// --- Add Reminder Dialog with Date & Time Picker ---

@Composable
fun AddReminderDialog(
    onDismiss: () -> Unit,
    onAdd: (String, String, String) -> Unit
) {
    val context = LocalContext.current
    var medicationName by remember { mutableStateOf("") }
    var dosage by remember { mutableStateOf("") }
    var dateTimeDisplay by remember { mutableStateOf("") }

    val sdf = SimpleDateFormat("MMM dd, yyyy hh:mm a", Locale.getDefault())

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Add New Reminder",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

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

                // Date & Time Picker

                OutlinedTextField(
                      value = dateTimeDisplay,
                     onValueChange = { dateTimeDisplay = it},
                      label = { Text("Date & Time") },
                     modifier = Modifier.fillMaxWidth()

                )

             /* OutlinedTextField(
                    value = dateTimeDisplay,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Date & Time") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {

                            val datePicker = DatePickerDialog(
                                context,
                                { _, year, month, dayOfMonth ->
                                    selectedCalendar.value.set(year, month, dayOfMonth)
                                    // Show Time Picker after date
                                    val timePicker = TimePickerDialog(
                                        context,
                                        { _, hourOfDay, minute ->
                                            selectedCalendar.value.set(Calendar.HOUR_OF_DAY, hourOfDay)
                                            selectedCalendar.value.set(Calendar.MINUTE, minute)
                                            dateTimeDisplay = sdf.format(selectedCalendar.value.time)
                                        },
                                        selectedCalendar.value.get(Calendar.HOUR_OF_DAY),
                                        selectedCalendar.value.get(Calendar.MINUTE),
                                        false
                                    )
                                    timePicker.show()
                                },
                                selectedCalendar.value.get(Calendar.YEAR),
                                selectedCalendar.value.get(Calendar.MONTH),
                                selectedCalendar.value.get(Calendar.DAY_OF_MONTH)
                            )
                            datePicker.show()
                        }
                )
                */
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) { Text("Cancel") }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            if (medicationName.isNotBlank() && dosage.isNotBlank() && dateTimeDisplay.isNotBlank()) {
                                onAdd(medicationName, dosage, dateTimeDisplay)
                            } else {
                                Toast.makeText(context, "Please fill all fields and select a date & time", Toast.LENGTH_SHORT).show()
                            }
                        }
                    ) {
                        Text("Add")
                    }
                }
            }
        }
    }
}

// --- Reminder Card ---
@Composable
fun MedicationReminderCard(
    medication: MedicationReminder,
    onStatusChange: (String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = medication.medicationName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = medication.dateTime,
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color(0xFF00796B)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Dosage: ${medication.dosage}",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "Status: ${medication.status}",
                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                color = when (medication.status) {
                    "Due" -> Color(0xFFFFA500)
                    "Taken" -> Color(0xFF10B981)
                    "Missed" -> Color(0xFFF44336)
                    else -> Color.Gray
                }
            )
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(
                    onClick = { onStatusChange("Taken") },
                    colors = ButtonDefaults.buttonColors(containerColor = accentColorReminder),
                    modifier = Modifier.weight(1f)
                ) { Text("Mark as Taken") }
                Spacer(modifier = Modifier.width(8.dp))
                Button(
                    onClick = { onStatusChange("Missed") },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF44336)),
                    modifier = Modifier.weight(1f)
                ) { Text("Mark as Missed") }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MedicationTreatmentReminderScreenPreview() {
    MedicationTreatmentReminderScreen()
}
