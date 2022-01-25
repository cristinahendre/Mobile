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
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.template.domain.Person
import com.example.template.model.Model
import com.example.template.viewmodel.PersonViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private lateinit var personViewModel: PersonViewModel
    private val model: Model by viewModels()
    private lateinit var adapter: ListAdapter
    private lateinit var view: View
    private lateinit var progress: ProgressDialog


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        progress = ProgressDialog(this)
        progress.setTitle("Loading")
        progress.setMessage("Please wait...")
        progress.setCancelable(false)
        view = View(this)
        personViewModel = ViewModelProviders.of(this).get(PersonViewModel::class.java)
        adapter = ListAdapter(this)
        personViewModel.deleteAll()
        fetchData()
        setupRecyclerView(findViewById(R.id.recyclerview))
        observeModel()

        val fab = findViewById<FloatingActionButton>(R.id.fab)
        fab.setOnClickListener {
            val intent = Intent(this@MainActivity, AddActivity::class.java)
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
        private var people = mutableListOf<Person>()

        inner class PeopleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val nameItemView: TextView = itemView.findViewById(R.id.name)
            val ageItemView: TextView = itemView.findViewById(R.id.age)
            val ivDelete: Button = itemView.findViewById(R.id.ivDelete)
            val ivUpdate: Button = itemView.findViewById(R.id.ivEdit)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PeopleViewHolder {
            val itemView = inflater.inflate(R.layout.recycler_item, parent, false)
            return PeopleViewHolder(itemView)
        }

        override fun onBindViewHolder(holder: PeopleViewHolder, position: Int) {
            val current = people[position]
            holder.nameItemView.text = current.name
            holder.ageItemView.text = current.age.toString()

            holder.ivDelete.setOnClickListener {
                logd("to delete $current")
                delete(current)


            }

            holder.ivUpdate.setOnClickListener {
                logd(" to update $current")
                update(current)
            }
        }


        internal fun setPeople(myGrades: List<Person>) {
            this.people.clear()
            this.people.addAll(myGrades)
            notifyDataSetChanged()
        }

        override fun getItemCount() = people.size

    }

    private fun delete(space: Person) {
        GlobalScope.launch(Dispatchers.Main) {
            progress.show()
            val result = model.delete(space.id)
            logd("server response = $result")
            if (result == "off") {
                //the server is off
                space.changed = 2
                personViewModel.update(space)
            }  else {
                personViewModel.delete(space.id)
            }
            progress.dismiss()
        }
    }

    private fun update(pers:Person){
        GlobalScope.launch(Dispatchers.Main) {
            progress.show()
            val intent = Intent(this@MainActivity, AddActivity::class.java)
            intent.putExtra("Name", pers.name)
            intent.putExtra("Id", pers.id)
            intent.putExtra("Age", pers.age)
            startActivity(intent)
            progress.dismiss()
        }
    }

    private fun setupRecyclerView(recyclerView: RecyclerView) {
        personViewModel.people
            ?.observe(this, { myGrades ->
                if (myGrades != null) {
                    adapter.setPeople(myGrades)
                }
            })
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)
    }

    private fun fetchData() {
        GlobalScope.launch(Dispatchers.Main) {

            progress.show()
            personViewModel.getPeopleChanged()
            personViewModel.peopleChanged?.observe { }
            val myGrades = model.getAll()
            if (myGrades == null) {
                //the server is off
                displayMessage("The server is down, using local data.")
            } else {
                areChangesToBeDone()
                personViewModel.insertAll(myGrades)
                logd("done inserting")
            }
            progress.dismiss()

        }
    }


    private fun displayData(gr: List<Person>) {
        adapter.setPeople(gr)
    }

    private fun observeModel() {
        personViewModel.getPeople()
        personViewModel.people?.observe { displayData(it ?: emptyList()) }
    }

    private fun <T> LiveData<T>.observe(observe: (T?) -> Unit) =
        observe(this@MainActivity, { observe(it) })

    private fun displayMessage(myMessage: String) {
        val parentLayout: View = findViewById(android.R.id.content)
        Snackbar.make(parentLayout, myMessage, Snackbar.LENGTH_LONG)
            .setAction("CLOSE") { }
            .setActionTextColor(resources.getColor(android.R.color.holo_red_light))
            .show()
    }

    private suspend fun areChangesToBeDone(): Boolean {
        val dbGrades = personViewModel.peopleChanged?.value
        progress.show()
        try {
            if (dbGrades != null) {
                if (dbGrades != null) {
                    for (gr: Person in dbGrades) {
                        logd(gr)
                        if (gr.changed == 1) {
                            //to add
                            gr.changed = 0
                            val res = model.add(gr)
                            if (res != "off") {
                                personViewModel.insert(gr)
                            } else gr.changed = 1
                        }
                        if (gr.changed == 2) {
                            //to delete
                            gr.changed = 0
                            val res = model.delete(gr.id)
                            if (res != "off") {
                                personViewModel.delete(gr.id)
                            } else gr.changed = 2
                        }
                        if (gr.changed == 3) {
                            //to update
                            gr.changed = 0
                            val res = model.update(gr)
                            if (res != "off") {
                                personViewModel.update(gr)
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