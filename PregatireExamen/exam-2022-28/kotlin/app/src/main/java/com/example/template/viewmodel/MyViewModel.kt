package com.example.template.viewmodel

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.template.WebSocketStuff
import com.example.template.database.TheDatabase
import com.example.template.domain.Dosar
import com.example.template.logd
import com.example.template.repository.DbRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MyViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: DbRepository

    var data: LiveData<List<Dosar>>? = null
    var changedData: LiveData<List<Dosar>>? = null

    init {
        val gradeDao = TheDatabase.getDatabase(application, viewModelScope).myDao()
        repository = DbRepository(gradeDao)

        viewModelScope.launch(Dispatchers.IO) {
            try{
                WebSocketStuff.start() {
                    viewModelScope.launch {
                        logd("aici $it")
                        var text =""
                        if(it.orientare == true) {

                            text = "Dosarul studentului ${it.nume} cu media ${it.medie}" +
                                    " s-a adaugat. Studentul sta la etajul ${it.etaj}," +
                                    " camera ${it.camera} in orientare nord"
                        }
                        else{
                            text = "Dosarul studentului ${it.nume} cu media ${it.medie}" +
                                    " s-a adaugat. Studentul sta la etajul ${it.etaj}," +
                                    " camera ${it.camera} in orientare sud"
                        }
                        Toast.makeText(
                            application.baseContext.applicationContext,
                            text,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

            }catch (e: Exception){

            }
        }
    }

    fun insertAll(gr: List<Dosar>) = viewModelScope.launch(Dispatchers.IO) {
        logd("insert all in view model")
        repository.deleteAll()
        repository.insertAll(gr)
    }


    fun insert(gr: Dosar) = viewModelScope.launch(Dispatchers.IO) {
        logd("insert in  view model: $gr")
        repository.insert(gr)
    }

    fun update(gr: Dosar) = viewModelScope.launch(Dispatchers.IO) {
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

    fun getNeconfirmate() {
        logd("get neconfirmate in view model")
        data = repository.getNeconfirmate()
    }

    fun getAllChanged() {
        logd("get all  changed in view model")
        changedData = repository.getAllChanged()

    }


}