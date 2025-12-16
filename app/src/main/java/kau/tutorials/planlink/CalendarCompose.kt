package kau.tutorials.planlink

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.kizitonwose.calendar.compose.HorizontalCalendar
import com.kizitonwose.calendar.compose.rememberCalendarState
import com.kizitonwose.calendar.core.DayPosition
import com.kizitonwose.calendar.core.daysOfWeek
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.Locale
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch


@Composable
fun CalendarWithDots(
    overlapDates: Set<String>,
    selectedDate: String?,
    onDateClick: (String) -> Unit
) {
    val currentMonth = remember { YearMonth.now() }
    val state = rememberCalendarState(
        startMonth = currentMonth.minusMonths(12),
        endMonth = currentMonth.plusMonths(12),
        firstVisibleMonth = currentMonth
    )
    val scope = rememberCoroutineScope()


    val daysOfWeek =  remember { daysOfWeek() }

    Column(modifier = Modifier.fillMaxWidth()) {

        // 월 헤더
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // ◀ 이전 달
            Text(
                text = "<",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.clickable {
                    scope.launch {
                        state.animateScrollToMonth(
                            state.firstVisibleMonth.yearMonth.minusMonths(1))
                    }
                }
            )

            // 월 표시
            Text(
                text = state.firstVisibleMonth.yearMonth.month
                    .getDisplayName(TextStyle.FULL, Locale.KOREA) +
                        "  " + state.firstVisibleMonth.yearMonth.year,
                style = MaterialTheme.typography.titleLarge
            )

            // ▶ 다음 달
            Text(
                text = ">",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.clickable {
                    scope.launch {
                        state.animateScrollToMonth(
                            state.firstVisibleMonth.yearMonth.plusMonths(1))
                    }
                }
            )
        }


        //요일 헤더
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
        ) {
            daysOfWeek.forEach { dayOfWeek ->
                Text(
                    text = dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.KOREA),
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.labelMedium
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        //캘린더 (높이 명시)
        HorizontalCalendar(
            modifier = Modifier
                .fillMaxWidth()
                .height(320.dp)
                .padding(horizontal = 8.dp),
            state = state,
            dayContent = { day ->
                val dateStr = "%04d-%02d-%02d".format(
                    day.date.year,
                    day.date.monthValue,
                    day.date.dayOfMonth
                )

                val isInMonth = day.position == DayPosition.MonthDate
                val isSelected = (dateStr == selectedDate)
                val hasDot = overlapDates.contains(dateStr)

                Box(
                    modifier = Modifier
                        .aspectRatio(1f) // 정사각 셀
                        .clickable(enabled = isInMonth) {
                            onDateClick(dateStr)
                        },
                    contentAlignment = Alignment.Center
                ) {
                    // 선택된 날짜 배경(동그라미)
                    if (isSelected) {
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .background(
                                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                                    shape = CircleShape
                                )
                        )
                    }

                    //날짜 숫자
                    Text(
                        text = day.date.dayOfMonth.toString(),
                        color = Color.Black
                    )

                    // 빨간 점
                    if (hasDot && isInMonth) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .align(Alignment.BottomCenter)
                                .offset(y = (-6).dp)
                                .background(Color.Red, CircleShape)
                        )
                    }
                }
            }
        )
    }
}
