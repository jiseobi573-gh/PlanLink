package kau.tutorials.planlink

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

   //분 → 시:분 포맷
fun formatMinute(minute: Int): String {
    val hour = minute / 60
    val min = minute % 60
    return String.format("%02d:%02d", hour, min)
}

//중복 설명용 데이터

data class OverlapExplanation(
    val first: Schedule,
    val second: Schedule,
    val overlapStart: Int,
    val overlapEnd: Int
)

fun explainOverlaps(schedules: List<Schedule>): List<OverlapExplanation> {
    val results = mutableListOf<OverlapExplanation>()

    for (i in schedules.indices) {
        for (j in i + 1 until schedules.size) {
            val a = schedules[i]
            val b = schedules[j]

            val start = maxOf(a.startMinute, b.startMinute)
            val end = minOf(a.endMinute, b.endMinute)

            if (start < end) {
                results.add(
                    OverlapExplanation(
                        first = a,
                        second = b,
                        overlapStart = start,
                        overlapEnd = end
                    )
                )
            }
        }
    }
    return results
}

//타임라인 UI
@Composable
fun TimelineSection(
    schedules: List<Schedule>,
    problemSchedules: Set<Schedule>
) {
    if (schedules.isEmpty()) return

    val minMinute = schedules.minOf { it.startMinute }
    val maxMinute = schedules.maxOf { it.endMinute }
    val totalMinutes = maxMinute - minMinute

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp)
    ) {
        Text(
            text = "시간대 분석",
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(modifier = Modifier.height(8.dp))

        schedules.forEach { schedule ->
            TimelineRow(
                schedule = schedule,
                minMinute = minMinute,
                totalMinutes = totalMinutes,
                isProblem = problemSchedules.contains(schedule)
            )
            Spacer(modifier = Modifier.height(6.dp))
        }
    }
}

@Composable
fun TimelineRow(
    schedule: Schedule,
    minMinute: Int,
    totalMinutes: Int,
    isProblem: Boolean
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = schedule.title,
            style = MaterialTheme.typography.bodySmall
        )

        Spacer(modifier = Modifier.height(6.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(28.dp)
                .background(Color.LightGray.copy(alpha = 0.3f))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(
                        (schedule.endMinute - schedule.startMinute).toFloat() / totalMinutes)
                    .offset(
                        x = ((schedule.startMinute - minMinute).toFloat() / totalMinutes * 300).dp)
                    .background(
                        if (isProblem) MaterialTheme.colorScheme.error
                        else MaterialTheme.colorScheme.primary)
            )
        }
    }
}


 //분석화면
@Composable
fun AnalysisScreen(
    navController: NavController,
    date: String,
    schedules: List<Schedule>
) {
    val overlapExplanations = explainOverlaps(schedules)

    val problemSchedules = overlapExplanations
        .flatMap { listOf(it.first, it.second) }
        .toSet()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .verticalScroll(rememberScrollState())
    ) {

        //제목
        Text(
            text = "일정 분석",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = date,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(16.dp))

        // 요약
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Column(modifier = Modifier.padding(12.dp)) {

                Text("요약", style = MaterialTheme.typography.titleSmall)

                Spacer(modifier = Modifier.height(4.dp))

                Text("총 일정 수: ${schedules.size}")

                Text(
                    text =
                        if (overlapExplanations.isNotEmpty())
                            "⚠ 중복 일정이 있습니다"
                        else
                            "중복 일정 없음",
                    color =
                        if (overlapExplanations.isNotEmpty())
                            MaterialTheme.colorScheme.error
                        else
                            MaterialTheme.colorScheme.primary
                )
            }
        }

        // 타임라인
        TimelineSection(
            schedules = schedules,
            problemSchedules = problemSchedules
        )

        //겹침 설명
        if (overlapExplanations.isNotEmpty()) {

            Text(
                text = "겹치는 시간대",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.error
            )

            Spacer(modifier = Modifier.height(8.dp))

            overlapExplanations.forEach { overlap ->
                val minutes =
                    overlap.overlapEnd - overlap.overlapStart

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {

                        Text(
                            text = "문제 일정",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onErrorContainer
                        )

                        Text(
                            text = "${overlap.first.title} " +
                                    "(${formatMinute(overlap.first.startMinute)} ~ ${formatMinute(overlap.first.endMinute)})"
                        )

                        Text(
                            text = "${overlap.second.title} " +
                                    "(${formatMinute(overlap.second.startMinute)} ~ ${formatMinute(overlap.second.endMinute)})"
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = "→ ${formatMinute(overlap.overlapStart)} ~ " +
                                    "${formatMinute(overlap.overlapEnd)} (${minutes}분 겹침)",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onErrorContainer
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = { navController.popBackStack() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("캘린더로 돌아가기")
        }

        Button(
            onClick = {},
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("홈으로")
        }
    }
}
