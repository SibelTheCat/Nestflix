package com.example.nestflix.manager

import android.content.Context
import android.util.Log
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.preferencesKey
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton
import androidx.datastore.preferences.createDataStore
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

private const val TAG = "SettingsManager"

enum class SortOrder{BY_NAME, BY_DATE}

data class FilterSettings(val sortOrder: SortOrder, val hideCompleted: Boolean)

//https://www.youtube.com/watch?v=Q5OKmS0unAI
//abstraction layer class, so the view model can stay clean
@Singleton
class SettingsManager @Inject constructor(@ApplicationContext context: Context){
    //name of DataStore = usr_settings
    private val dataSore = context.createDataStore("user_settings")

    val settingsFlow = dataSore.data
        .catch { exception ->
            if (exception is IOException) {
                Log.e(TAG, "Error reading preferences", exception)
                emit(emptyPreferences())
            }
            else {
                throw exception
            }

        }
        .map{
            settings ->
            val sortOrder = SortOrder.valueOf(
                settings[SettingsKeys.SORT_ORDER] ?: SortOrder.BY_DATE.name
            )
            val hideCompleted = settings[SettingsKeys.HIDE_COMPLETED] ?: false
            FilterSettings(sortOrder, hideCompleted)
        }

    suspend fun updateSortOrder(sortOrder: SortOrder){
        dataSore.edit { settings->
            settings[SettingsKeys.SORT_ORDER] = sortOrder.name
        }
    }

    suspend fun updateHideComplete(hideCompleted: Boolean){
        dataSore.edit { settings->
            settings[SettingsKeys.HIDE_COMPLETED] = hideCompleted
        }
    }

    private object SettingsKeys {
        val SORT_ORDER = preferencesKey<String>("sort_order")
        val HIDE_COMPLETED = preferencesKey<Boolean>("hide_completed")
    }
}