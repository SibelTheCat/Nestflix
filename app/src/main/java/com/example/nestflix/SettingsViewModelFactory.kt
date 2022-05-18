package com.example.nestflix

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.nestflix.manager.SettingsDataStore
import com.example.nestflix.viewmodel.SettingsViewModel

class SettingsViewModelFactory(private val settingsDataStore: SettingsDataStore) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SettingsViewModel::class.java)){
            return SettingsViewModel(settingsDataStore) as T
        }
        throw IllegalStateException()
    }

}