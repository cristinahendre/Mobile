package com.example.parkingapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.parkingapp.database.SpaceDatabase
import com.example.parkingapp.domain.Space
import com.example.parkingapp.logd
import com.example.parkingapp.repository.SpaceRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SpaceViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: SpaceRepository
    var allSpaces: LiveData<List<Space>>? = null
    var freeSpaces: LiveData<List<Space>>? = null
    var allSpacesChanged: LiveData<List<Space>>? = null

    init {
        val vehicaleDao = SpaceDatabase.getDatabase(application, viewModelScope).spaceDao()
        repository = SpaceRepository(vehicaleDao)
    }

    fun insertAll(gr: List<Space>) = viewModelScope.launch(Dispatchers.IO) {
        repository.deleteAll()
        repository.insertAll(gr)
        logd("inserted all")
    }


    fun insert(gr: Space) = viewModelScope.launch(Dispatchers.IO) {
        repository.insert(gr)
        logd("insert in view model: $gr")
    }

    fun update(gr: Space) = viewModelScope.launch(Dispatchers.IO) {

        repository.update(gr)
        logd("update in view model :$gr")
    }

    fun delete(id: Int) = viewModelScope.launch(Dispatchers.IO) {
        logd("delete in view model")
        repository.delete(id)
    }

    fun deleteAll() = viewModelScope.launch(Dispatchers.IO) {
        logd("delete all in view model")
        repository.deleteAll()
    }

    fun getAll() {
        allSpaces = repository.allSpaces
    }

    fun getAllChanged() {
        allSpacesChanged = repository.getAllChanged()
        logd("[Space changed] ${allSpacesChanged!!.value}")
    }


    fun getFreeSpaces() {
        freeSpaces = repository.getFreeSpaces()
    }

}