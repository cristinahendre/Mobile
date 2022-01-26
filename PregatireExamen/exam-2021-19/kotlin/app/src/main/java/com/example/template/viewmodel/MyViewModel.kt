package com.example.template.viewmodel

import android.app.Application
import androidx.annotation.Nullable
import androidx.lifecycle.*
import com.example.template.database.TheDatabase
import com.example.template.domain.Rule
import com.example.template.logd
import com.example.template.repository.DbRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MyViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: DbRepository

    var data: LiveData<List<Rule>>? = null
    var mSectionLive: MediatorLiveData<List<Rule>>? = MediatorLiveData()
    var changedData: LiveData<List<Rule>>? = null

    init {
        val gradeDao = TheDatabase.getDatabase(application, viewModelScope).personDao()
        repository = DbRepository(gradeDao)
    }

    fun insertAll(gr: List<Rule>) = viewModelScope.launch(Dispatchers.IO) {
        logd("insert all in view model")
        repository.deleteAll()
        repository.insertAll(gr)
    }


    fun insert(gr: Rule) = viewModelScope.launch(Dispatchers.IO) {
        logd("insert in  view model: $gr")
        repository.insert(gr)
    }

    fun update(gr: Rule) = viewModelScope.launch(Dispatchers.IO) {
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
        data = repository.getAll()

    }

    fun getAllChanged() {
        logd("get all  changed in view model")
        changedData = repository.getAllChanged()

    }


}