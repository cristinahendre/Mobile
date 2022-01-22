package com.example.template.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.template.database.PersonDatabase
import com.example.template.domain.Person
import com.example.template.logd
import com.example.template.repository.PersonRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PersonViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: PersonRepository

    var people: LiveData<List<Person>>? = null
    var peopleChanged: LiveData<List<Person>>? = null

    init {
        val gradeDao = PersonDatabase.getDatabase(application, viewModelScope).personDao()
        repository = PersonRepository(gradeDao)
    }

    fun insertAll(gr: List<Person>) = viewModelScope.launch(Dispatchers.IO) {
        logd("insert all in view model")
        repository.deleteAll()
        repository.insertAll(gr)
    }


    fun insert(gr: Person) = viewModelScope.launch(Dispatchers.IO) {
        logd("insert in  view model: $gr")
        repository.insert(gr)
    }

    fun update(gr: Person) = viewModelScope.launch(Dispatchers.IO) {
        logd("update in view model")
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


    fun getPeople() {
        logd("get all in view model")
        people = repository.allPeople
    }

    fun getPeopleChanged() {
        logd("get all  changed in view model")
        peopleChanged = repository.getPeopleChanged()

    }


}