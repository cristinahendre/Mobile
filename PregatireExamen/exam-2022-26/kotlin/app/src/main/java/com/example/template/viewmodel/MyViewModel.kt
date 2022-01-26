package com.example.template.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.template.database.TheDatabase
import com.example.template.domain.Rezervare
import com.example.template.logd
import com.example.template.repository.DbRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MyViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: DbRepository

    var data: LiveData<List<Rezervare>>? = null
    var changedData: LiveData<List<Rezervare>>? = null

    init {
        val gradeDao = TheDatabase.getDatabase(application, viewModelScope).myDao()
        repository = DbRepository(gradeDao)
    }

    fun insertAll(gr: List<Rezervare>) = viewModelScope.launch(Dispatchers.IO) {
        logd("insert all in view model")
        repository.deleteAll()
        repository.insertAll(gr)
    }


    fun insert(gr: Rezervare) = viewModelScope.launch(Dispatchers.IO) {
        logd("insert in  view model: $gr")
        repository.insert(gr)
    }

    fun update(gr: Rezervare) = viewModelScope.launch(Dispatchers.IO) {
        logd("update in view model: $gr")
        repository.update(gr)
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
        logd("get all in view model")
        data = repository.allData
    }

    fun getAllChanged() {
        logd("get all  changed in view model")
        changedData = repository.getAllChanged()

    }


}