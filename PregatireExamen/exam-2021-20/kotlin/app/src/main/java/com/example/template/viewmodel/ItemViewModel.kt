package com.example.template.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.template.database.ItemDatabase
import com.example.template.domain.Item
import com.example.template.logd
import com.example.template.repository.ItemRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ItemViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: ItemRepository

    var items: LiveData<List<Item>>? = null
    var itemsAvailable: LiveData<List<Item>>? = null
    var itemsChanged: LiveData<List<Item>>? = null

    init {
        val dao = ItemDatabase.getDatabase(application, viewModelScope).itemDao()
        repository = ItemRepository(dao)
    }

    fun insertAll(gr: List<Item>) = viewModelScope.launch(Dispatchers.IO) {
        logd("insert all in view model")
        repository.deleteAll()
        repository.insertAll(gr)
    }


    fun insert(gr: Item) = viewModelScope.launch(Dispatchers.IO) {
        logd("insert in  view model: $gr")
        repository.insert(gr)
    }

    fun update(gr: Item) = viewModelScope.launch(Dispatchers.IO) {
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


    fun getAll() {
        logd("get all in view model")
        items = repository.allItems
    }

    fun getItemsChanged() {
        logd("get all  changed in view model")
        itemsChanged = repository.getItemsChanged()

    }

    fun getItemsAvailable() {
        logd("get all  available in view model")
        itemsAvailable = repository.getAllAvailable()

    }


}