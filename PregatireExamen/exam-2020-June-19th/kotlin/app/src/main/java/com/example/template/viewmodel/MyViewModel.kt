package com.example.template.viewmodel

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.template.WebSocketStuff
import com.example.template.database.TheDatabase
import com.example.template.domain.Vehicle
import com.example.template.logd
import com.example.template.repository.DbRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MyViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: DbRepository

    var data: LiveData<List<Vehicle>>? = null
    var changedData: LiveData<List<Vehicle>>? = null
    var coloredData: LiveData<List<Vehicle>>? = null

    init {
        val gradeDao = TheDatabase.getDatabase(application, viewModelScope).myDao()
        repository = DbRepository(gradeDao)

        viewModelScope.launch(Dispatchers.IO) {
            try {
                WebSocketStuff.start() {
                    viewModelScope.launch {
                        logd("aici $it")
                        val vehicle = it
                        val text =
                            "The vehicle with the name ${vehicle.name} has the driver ${vehicle.driver} and " +
                                    " it can fit ${vehicle.passengers} people, with a capacity of ${vehicle.capacity}"
                        Toast.makeText(
                            application.baseContext.applicationContext,
                            text,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

            } catch (e: Exception) {

            }
        }
    }

    fun insertAll(gr: List<Vehicle>) = viewModelScope.launch(Dispatchers.IO) {
        logd("insert all in view model")
        repository.deleteAll()
        repository.insertAll(gr)
    }


    fun insert(gr: Vehicle) = viewModelScope.launch(Dispatchers.IO) {
        logd("insert in  view model: $gr")
        repository.insert(gr)
    }

    fun update(gr: Vehicle) = viewModelScope.launch(Dispatchers.IO) {
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

    fun getVehiclesOfColor(c:String) {
        logd("get vehicles of color $c in view model")
        coloredData = repository.getVehiclesOfColor(c)
    }



    fun getAllChanged() {
        logd("get all  changed in view model")
        changedData = repository.getAllChanged()

    }


}