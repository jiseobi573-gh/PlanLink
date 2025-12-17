package kau.tutorials.planlink

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable


@Composable
fun AppNavigation(navController: NavHostController) {

    val schedules = listOf(
        Schedule("user1","회의", "2025-12-15", 10*60, 12*60),
        Schedule("user2","병원 예약", "2025-12-15", 11*60 + 30, 13*60),
        Schedule("user3","운동", "2025-12-15", 18*60 + 24, 19 * 60),
        Schedule("user4","1", "2025-12-19", 14*60 + 25, 22*60+57),
        Schedule("user5","2", "2025-12-19", 11*60, 12*60 + 24),
        Schedule("user6","3", "2025-12-19", 5*60 + 30, 19*60),
        Schedule("user7","4", "2025-12-21", 1*60 + 24, 23 * 60),
        Schedule("user8","5", "2025-12-21", 3*60 + 33, 21*60+57),

        Schedule("other1", "[상대방] 점심 약속", "2025-12-15", 12*60, 13*60),
        Schedule("other2", "[상대방] 수업", "2025-12-15", 9*60 + 30, 11*60),
        Schedule("other3", "[상대방] 미팅", "2025-12-19", 18*60, 20*60),

        Schedule("other4", "[상대방] 카페", "2025-12-20", 19*60, 20*60 + 30),
        Schedule("other5", "[상대방] 스터디", "2025-12-21", 14*60, 16*60),
        Schedule("other6", "생일", "2025-12-29", 5*60+4, 7*60+30)

    )

    NavHost(
        navController = navController,
        startDestination = "calendar"
    ) {
        composable("calendar") {
            CalendarScreen(
                navController = navController,
                schedules = schedules
            )
        }

        composable("analysis/{date}") { backStackEntry ->
            val date = backStackEntry.arguments?.getString("date")!!
            val daySchedules = schedules.filter { it.date == date }

            AnalysisScreen(
                navController = navController,
                date = date,
                schedules = daySchedules
            )
        }
    }
}
