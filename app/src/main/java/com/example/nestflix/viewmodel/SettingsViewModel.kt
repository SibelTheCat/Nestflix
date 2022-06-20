package com.example.nestflix.viewmodel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nestflix.manager.SettingsDataStore
import kotlinx.coroutines.launch

//https://www.youtube.com/watch?v=4D8dVPeWxIs
class SettingsViewModel(
    private val settingsDataStore: SettingsDataStore
)  : ViewModel(){

    private val _ipAddress = MutableLiveData("")
    val ipAddress: LiveData<String> = _ipAddress

    init{
        viewModelScope.launch{
            settingsDataStore.getIpAddress.collect{
                _ipAddress.value = it
            }
        }
    }
    suspend fun saveIpAddress(ipAddress: String){
        settingsDataStore.setIpAddress(ipAddress)
    }

}