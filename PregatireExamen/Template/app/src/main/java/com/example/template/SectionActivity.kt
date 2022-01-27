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
import com.example.template.viewmodel.MyViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class SectionActivity : AppCompatActivity() {
    private lateinit var myViewModel: MyViewModel
    private val model: Model by viewModels()
    private lateinit var adapter: ListAdapter
    private lateinit var view: View
    private lateinit var progress: ProgressDialog


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_section)
        progress = ProgressDialog(this)
        progress.setTitle("Loading")
        progress.setMessage("Please wait...")
        progress.setCancelable(false)
        view = View(this)
        myViewModel = ViewModelProviders.of(this).get(MyViewModel::class.java)
        adapter = ListAdapter(this)
        myViewModel.deleteAll()
        fetchData()
        setupRecyclerView(findViewById(R.id.recyclerview))
        observeModel()

        val fab = findViewById<FloatingActionButton>(R.id.fab)
        fab.setOnClickListener {
            val intent = Intent(this@SectionActivity, AddActivity::class.java)
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
        private var data = mutableListOf<Person>()

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
            val current = data[position]
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


        internal fun setData(myData: List<Person>) {
            this.data.clear()
            this.data.addAll(myData)
            notifyDataSetChanged()
        }

        override fun getItemCount() = data.size

    }

    private fun delete(space: Person) {
        GlobalScope.launch(Dispatchers.Main) {
            progress.show()
            val result = model.delete(space.id)
            logd("server response = $result")
            if (result == "off") {
                //the server is off
                space.changed = 2
                myViewModel.update(space)
            }  else {
                myViewModel.delete(space.id)
            }
            progress.dismiss()
        }
    }

    private fun update(pers:Person){
        GlobalScope.launch(Dispatchers.Main) {
            progress.show()
            val intent = Intent(this@SectionActivity, AddActivity::class.java)
            intent.putExtra("Name", pers.name)
            intent.putExtra("Id", pers.id)
            intent.putExtra("Age", pers.age)
            startActivity(intent)
            progress.dismiss()
        }
    }

    private fun setupRecyclerView(recyclerView: RecyclerView) {
        myViewModel.data
            ?.observe(this, { myGrades ->
                if (myGrades != null) {
                    adapter.setData(myGrades)
                }
            })
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)
    }

    private fun fetchData() {
        GlobalScope.launch(Dispatchers.Main) {

            progress.show()
            myViewModel.getAllChanged()
            myViewModel.changedData?.observe { }
            val myGrades = model.getAll()
            if (myGrades == null) {
                //the server is off
                displayMessage("The server is down, using local data.")
            } else {
                areChangesToBeDone()
                myViewModel.insertAll(myGrades)
                logd("done inserting")
            }
            progress.dismiss()

        }
    }


    private fun displayData(gr: List<Person>) {
        adapter.setData(gr)
    }

    private fun observeModel() {
        myViewModel.getAll()
        myViewModel.data?.observe { displayData(it ?: emptyList()) }
    }

    private fun <T> LiveData<T>.observe(observe: (T?) -> Unit) =
        observe(this@SectionActivity, { observe(it) })

    private fun displayMessage(myMessage: String) {
        val parentLayout: View = findViewById(android.R.id.content)
        Snackbar.make(parentLayout, myMessage, Snackbar.LENGTH_LONG)
            .setAction("CLOSE") { }
            .setActionTextColor(resources.getColor(android.R.color.holo_red_light))
            .show()
    }

    private suspend fun areChangesToBeDone(): Boolean {
        val dbGrades = myViewModel.changedData?.value
        progress.show()
        try {
            if (dbGrades != null) {
                for (gr: Person in dbGrades) {
                    logd(gr)
                    if (gr.changed == 1) {
                        //to add
                        gr.changed = 0
                        val res = model.add(gr)
                        if (res != "off") {
                            val myObj =deserialize(res)
                            if(myObj!=null) {
                                displayFinalMessage(myObj)
                                myViewModel.delete(gr.id)
                                gr.id = myObj.id
                                myViewModel.insert(gr)
                            }
                            else{
                                displayMessage("Error when saving.")
                            }
                        } else gr.changed = 1
                    }
                    if (gr.changed == 2) {
                        //to delete
                        gr.changed = 0
                        val res = model.delete(gr.id)
                        if (res != "off") {
                            myViewModel.delete(gr.id)
                        } else gr.changed = 2
                    }
                    if (gr.changed == 3) {
                        //to update
                        gr.changed = 0
                        val res = model.update(gr)
                        if (res != "off") {
                            myViewModel.update(gr)
                        } else gr.changed = 3
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

    private fun displayFinalMessage(person: Person) {
        val text = "The person with the name ${person.name} has ${person.age} years."
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
    }

    private fun deserialize(myString: String): Person? {

        try {
            var indexStart = myString.indexOf("id") + 3
            var indexStop = myString.indexOf("name") - 3
            val id = getStringFromArea(indexStart, indexStop, myString).toInt()
            if (id == 0) {
                return null
            }
            return Person(id,"",0,0)
        } catch (e: NumberFormatException) {
            logd("exceptie la deserializare $e")
            return null
        }

    }

    private fun getStringFromArea(start: Int, end: Int, myMessage: String): String {
        var result = ""
        for (i in myMessage.indices) {
            if (i in start..end) result += myMessage[i]
        }
        return result
    }

}