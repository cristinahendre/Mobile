package com.example.template

import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.*
import android.widget.Button
import android.widget.TextView
import androidx.activity.viewModels
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.template.domain.Item
import com.example.template.model.Model
import com.example.template.viewmodel.ItemViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class ItemsActivity : AppCompatActivity() {
    private lateinit var itemViewModel: ItemViewModel
    private val model: Model by viewModels()
    private lateinit var adapter: ListAdapter
    private lateinit var view: View
    private lateinit var progress: ProgressDialog


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_items)
        progress = ProgressDialog(this)
        progress.setTitle("Loading")
        progress.setMessage("Please wait...")
        progress.setCancelable(false)
        view = View(this)
        itemViewModel = ViewModelProviders.of(this).get(ItemViewModel::class.java)
        adapter = ListAdapter(this)
        itemViewModel.deleteAll()
        fetchData()
        setupRecyclerView(findViewById(R.id.recyclerview))
        observeModel()

        val fab = findViewById<FloatingActionButton>(R.id.fab)
        fab.setOnClickListener {
            val intent = Intent(this@ItemsActivity, AddActivity::class.java)
            startActivity(intent)
        }

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.my_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.retry -> {
                logd("retry clicked")
                GlobalScope.launch(Dispatchers.Main) {
                    areChangesToBeDone()
                    fetchData()
                    observeModel()
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }


    inner class ListAdapter internal constructor(
        context: Context
    ) : RecyclerView.Adapter<ListAdapter.PeopleViewHolder>() {

        private val inflater: LayoutInflater = LayoutInflater.from(context)
        private val builder = AlertDialog.Builder(context)
        private var people = mutableListOf<Item>()

        inner class PeopleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val nameItemView: TextView = itemView.findViewById(R.id.name)
            val idItemView: TextView = itemView.findViewById(R.id.id)
            val statusItemView: TextView = itemView.findViewById(R.id.status)
            val quantityItemView: TextView = itemView.findViewById(R.id.quantity)
            val ivDelete: Button = itemView.findViewById(R.id.ivDelete)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PeopleViewHolder {
            val itemView = inflater.inflate(R.layout.recycler_item, parent, false)
            return PeopleViewHolder(itemView)
        }

        override fun onBindViewHolder(holder: PeopleViewHolder, position: Int) {
            val current = people[position]
            holder.nameItemView.text = current.name
            holder.idItemView.text = current.id.toString()
            holder.quantityItemView.text = current.quantity.toString()
            holder.statusItemView.text = current.status

            holder.ivDelete.setOnClickListener {
                logd("to delete $current")
                delete(current)

            }
        }

        internal fun setItems(myGrades: List<Item>) {
            this.people.clear()
            this.people.addAll(myGrades)
            notifyDataSetChanged()
        }

        override fun getItemCount() = people.size

    }

    private fun delete(space: Item) {
        GlobalScope.launch(Dispatchers.Main) {
            progress.show()
            val result = model.delete(space.id)
            logd("server response = $result")
            if (result == "off") {
                //the server is off
                space.changed = 2
                itemViewModel.update(space)
            }  else {
                itemViewModel.delete(space.id)
            }
            progress.dismiss()
        }
    }


    private fun setupRecyclerView(recyclerView: RecyclerView) {
        itemViewModel.items
            ?.observe(this, { myGrades ->
                if (myGrades != null) {
                    adapter.setItems(myGrades)
                }
            })
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)
    }

    private fun fetchData() {
        GlobalScope.launch(Dispatchers.Main) {

            progress.show()
            itemViewModel.getItemsChanged()
            itemViewModel.itemsChanged?.observe { }
            val myGrades = model.getAll()
            if (myGrades == null) {
                //the server is off
                displayMessage("The server is down, using local data.")
            } else {
                areChangesToBeDone()
                itemViewModel.insertAll(myGrades)
                logd("done inserting")
            }
            progress.dismiss()

        }
    }


    private fun displayData(gr: List<Item>) {
        adapter.setItems(gr)
    }

    private fun observeModel() {
        itemViewModel.getAll()
        itemViewModel.items?.observe { displayData(it ?: emptyList()) }
    }

    private fun <T> LiveData<T>.observe(observe: (T?) -> Unit) =
        observe(this@ItemsActivity, { observe(it) })

    private fun displayMessage(myMessage: String) {
        val parentLayout: View = findViewById(android.R.id.content)
        Snackbar.make(parentLayout, myMessage, Snackbar.LENGTH_LONG)
            .setAction("CLOSE") { }
            .setActionTextColor(resources.getColor(android.R.color.holo_red_light))
            .show()
    }

    private suspend fun areChangesToBeDone(): Boolean {
        val dbGrades = itemViewModel.itemsChanged?.value
        progress.show()
        try {
            if (dbGrades != null) {
                if (dbGrades != null) {
                    for (gr: Item in dbGrades) {
                        logd(gr)
                        if (gr.changed == 1) {
                            //to add
                            gr.changed = 0
                            val res = model.add(gr)
                            if (res != "off") {
                                itemViewModel.insert(gr)
                            } else gr.changed = 1
                        }
                        if (gr.changed == 2) {
                            //to delete
                            gr.changed = 0
                            val res = model.delete(gr.id)
                            if (res != "off") {
                                itemViewModel.delete(gr.id)
                            } else gr.changed = 2
                        }
                        if (gr.changed == 3) {
                            //to update
                            gr.changed = 0
                            val res = model.update(gr)
                            if (res != "off") {
                                itemViewModel.update(gr)
                            } else gr.changed = 3
                        }
                    }
                }
            }
        } catch (e: Exception) {
            progress.dismiss()
            return false
        }
        progress.dismiss()
        return true
    }

}