package com.example.template

import android.app.AlertDialog
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.*
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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private lateinit var personViewModel: PersonViewModel
    private val model: Model by viewModels()
    private lateinit var adapter: ListAdapter
    private lateinit var view: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        view = View(this)
        val extras = intent.extras
        personViewModel = ViewModelProviders.of(this).get(PersonViewModel::class.java)
        adapter = ListAdapter(this)
        fetchData()
        setupRecyclerView(findViewById(R.id.recyclerview))
        observeModel()

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.my_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {


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
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PeopleViewHolder {
            val itemView = inflater.inflate(R.layout.recycler_item, parent, false)
            return PeopleViewHolder(itemView)
        }

        override fun onBindViewHolder(holder: PeopleViewHolder, position: Int) {
            val current = people[position]
            holder.nameItemView.text = current.name
            holder.ageItemView.text = current.age.toString()
        }


        internal fun setPeople(myGrades: List<Person>) {
            this.people.clear()
            this.people.addAll(myGrades)
            notifyDataSetChanged()
        }

        override fun getItemCount() = people.size

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
        Toast.makeText(
            applicationContext,
            myMessage,
            Toast.LENGTH_SHORT
        ).show()
    }

    suspend fun areChangesToBeDone(): Boolean {
        val dbGrades = personViewModel.peopleChanged?.value
        try {
            if (dbGrades != null) {
                if (dbGrades != null) {
                    for (gr: Person in dbGrades) {
                        logd(gr)
                        if (gr.changed == 1) {
                            //to add
                            gr.changed = 0
                            val res = model.add(gr)
                            if (res != -1) {
                                personViewModel.insert(gr)
                            } else gr.changed = 1
                        }
                        if (gr.changed == 2) {
                            //to delete
                            gr.changed = 0
                            val res = model.delete(gr.id)
                            if (res != -1) {
                                personViewModel.delete(gr.id)
                            } else gr.changed = 2
                        }
                        if (gr.changed == 3) {
                            //to update
                            gr.changed = 0
                            val res = model.update(gr)
                            if (res != -1) {
                                personViewModel.update(gr)
                            } else gr.changed = 3
                        }
                    }
                }
            }
        } catch (e: Exception) {
            return false
        }
        return true
    }

}