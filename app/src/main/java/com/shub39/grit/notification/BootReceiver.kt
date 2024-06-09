package com.shub39.grit.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.shub39.grit.viewModel.HabitViewModel

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent != null && context != null) {
            if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
                HabitViewModel(context).scheduleAll()
            }
        }
    }

}