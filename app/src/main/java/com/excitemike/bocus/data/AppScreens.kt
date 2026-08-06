package com.excitemike.bocus.data

import com.excitemike.bocus.R

enum class AppScreens(val labelId: Int) {
    ALARMS(R.string.alarm_tab_label),
    ABOUT(R.string.about_tab_label),
}

val INITIAL_APP_SCREEN = AppScreens.ALARMS