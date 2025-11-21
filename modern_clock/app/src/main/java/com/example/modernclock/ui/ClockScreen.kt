package com.example.modernclock.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun ClockScreen() {
    var selectedTab by remember { mutableIntStateOf(0) }

    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = Color.Black,
                contentColor = Color.White
            ) {
                NavigationBarItem(
                    icon = { Icon(Icons.Filled.AccessTime, contentDescription = "Clock") },
                    label = { Text("Clock") },
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.primary,
                        selectedTextColor = MaterialTheme.colorScheme.primary,
                        indicatorColor = Color.Transparent,
                        unselectedIconColor = Color.Gray,
                        unselectedTextColor = Color.Gray
                    )
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Filled.Timer, contentDescription = "Stopwatch") },
                    label = { Text("Stopwatch") },
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.primary,
                        selectedTextColor = MaterialTheme.colorScheme.primary,
                        indicatorColor = Color.Transparent,
                        unselectedIconColor = Color.Gray,
                        unselectedTextColor = Color.Gray
                    )
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
                .padding(innerPadding)
        ) {
            if (selectedTab == 0) {
                WorldClockContent()
            } else {
                StopwatchContent()
            }
        }
    }
}

@Composable
fun WorldClockContent() {
    var currentTime by remember { mutableStateOf(Calendar.getInstance().time) }

    LaunchedEffect(Unit) {
        while (true) {
            currentTime = Calendar.getInstance().time
            delay(1000)
        }
    }

    val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
    val dateFormat = SimpleDateFormat("EEE, d MMM", Locale.getDefault())

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Local Time",
            color = Color.Gray,
            fontSize = 16.sp
        )
        Text(
            text = timeFormat.format(currentTime),
            style = MaterialTheme.typography.displayLarge,
            color = Color.White
        )
        Text(
            text = dateFormat.format(currentTime),
            color = Color.Gray,
            fontSize = 20.sp
        )
    }
}

@Composable
fun StopwatchContent() {
    var isRunning by remember { mutableStateOf(false) }
    var timeMillis by remember { mutableLongStateOf(0L) }
    var lastTimestamp by remember { mutableLongStateOf(0L) }
    var laps = remember { mutableStateListOf<Long>() }

    LaunchedEffect(isRunning) {
        if (isRunning) {
            lastTimestamp = System.currentTimeMillis()
            while (isRunning) {
                delay(10)
                val now = System.currentTimeMillis()
                timeMillis += now - lastTimestamp
                lastTimestamp = now
            }
        }
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(100.dp))
        
        // Timer Display
        Text(
            text = formatTime(timeMillis),
            style = MaterialTheme.typography.displayLarge,
            color = Color.White,
            fontSize = 70.sp
        )

        Spacer(modifier = Modifier.height(80.dp))

        // Controls
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            // Lap/Reset Button
            Button(
                onClick = {
                    if (isRunning) {
                        laps.add(0, timeMillis)
                    } else {
                        timeMillis = 0L
                        laps.clear()
                    }
                },
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF333333))
            ) {
                Text(text = if (isRunning) "Lap" else "Reset", color = Color.White)
            }

            // Start/Stop Button
            Button(
                onClick = { isRunning = !isRunning },
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isRunning) Color(0xFF320E0B) else Color(0xFF082A11)
                )
            ) {
                Text(
                    text = if (isRunning) "Stop" else "Start",
                    color = if (isRunning) Color(0xFFFF453A) else Color(0xFF30D158)
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Laps List
        LazyColumn(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp)
        ) {
            items(laps) { lapTime ->
                Column {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "Lap ${laps.size - laps.indexOf(lapTime)}", color = Color.White)
                        Text(text = formatTime(lapTime), color = Color.White)
                    }
                    Divider(color = Color.DarkGray)
                }
            }
        }
    }
}

fun formatTime(millis: Long): String {
    val minutes = (millis / 60000) % 60
    val seconds = (millis / 1000) % 60
    val hundredths = (millis / 10) % 100
    return String.format("%02d:%02d.%02d", minutes, seconds, hundredths)
}
