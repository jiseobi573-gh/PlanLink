package kau.tutorials.planlink

import android.R.attr.text
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController


// 중복 로직
fun hasOverlap(schedules: List<Schedule>): Boolean {
    for (i in schedules.indices) {
        for (j in i + 1 until schedules.size) {
            val a = schedules[i]
            val b = schedules[j]
            if (a.startMinute < b.endMinute && a.endMinute > b.startMinute) return true
        }
    }
    return false
}

fun getOverlapDates(schedules: List<Schedule>): Set<String> {
    val result = mutableSetOf<String>()
    val grouped = schedules.groupBy { it.date }
    for ((date, daySchedules) in grouped) {
        if (hasOverlap(daySchedules)) result.add(date)
    }
    return result
}

// 화면
@Composable
fun CalendarScreen(
    navController: NavController,
    schedules: List<Schedule>
) {
    var selectedDate by remember { mutableStateOf<String?>(null) }
    val overlapDates = getOverlapDates(schedules)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = {}) {          //홈으로 navigate
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "홈으로"
                )
            }
            Text(
                text = "캘린더",
                style = MaterialTheme.typography.headlineMedium
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // 캘린더 (CalendarWithDots 사용)
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(4.dp)
        ) {
            CalendarWithDots(
                overlapDates = overlapDates,
                selectedDate = selectedDate,
                onDateClick = { clicked ->
                    selectedDate = clicked
                }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (selectedDate == null) {
            Text(
                text = "날짜를 선택하세요",
                modifier = Modifier.padding(16.dp)
            )
            return@Column
        }

        val daySchedules = schedules.filter { it.date == selectedDate }

        if (hasOverlap(daySchedules)) {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                Text(
                    text = "⚠ 중복 일정이 있습니다",
                    color = MaterialTheme.colorScheme.onErrorContainer,
                    modifier = Modifier.padding(12.dp)
                )
            }

            Text(
                text = "분석 보기 →",
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .padding(start = 16.dp, bottom = 8.dp)
                    .clickable {
                        navController.navigate("analysis/$selectedDate")
                    }
            )
        }

        Spacer(modifier = Modifier.height(12.dp))
        HorizontalDivider(Modifier, DividerDefaults.Thickness, DividerDefaults.color)
        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "선택한 날짜",
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.primary
        )

        Text(
            text = selectedDate
                ?: "",
            style = MaterialTheme.typography.titleMedium
        )


        Spacer(modifier = Modifier.height(8.dp))

        if (daySchedules.isEmpty()) {
            Text(
                text = "이 날짜에는 일정이 없습니다",
                modifier = Modifier.padding(16.dp)
            )
        } else {
            Spacer(modifier = Modifier.height(8.dp))

            daySchedules.forEach { schedule ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = schedule.title,
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Text(
                            text ="${formatMinute(schedule.startMinute)} ~ ${formatMinute(schedule.endMinute)}",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
        }
    }
}
