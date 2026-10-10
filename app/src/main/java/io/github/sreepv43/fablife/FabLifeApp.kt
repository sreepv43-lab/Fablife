package io.github.sreepv43.fablife

import android.app.Application
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import io.github.sreepv43.fablife.data.ContentRepository
import io.github.sreepv43.fablife.data.FabDatabase
import io.github.sreepv43.fablife.data.FabRepository
import io.github.sreepv43.fablife.data.PrefsRepository
import io.github.sreepv43.fablife.notifications.ReminderScheduler

class AppContainer(context: Context) {
    val database = FabDatabase.create(context)
    val prefs = PrefsRepository(context)
    val content = ContentRepository(context)
    val reminders = ReminderScheduler(context)
    val repository = FabRepository(database, prefs, content) { rescheduleReminders() }

    suspend fun rescheduleReminders() {
        val enabled = prefs.current().onboardingCompleted
        reminders.scheduleAll(if (enabled) database.ritualSettings().all() else emptyList())
    }
}

class FabLifeApp : Application() {
    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)
        container.reminders.createChannel()
    }
}

val Context.fabApp: FabLifeApp get() = applicationContext as FabLifeApp

/** Factory for ViewModels that need the [AppContainer]. */
fun <VM : ViewModel> viewModelFactory(create: (AppContainer) -> VM) = object : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
        val app = extras[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as FabLifeApp
        return create(app.container) as T
    }
}
