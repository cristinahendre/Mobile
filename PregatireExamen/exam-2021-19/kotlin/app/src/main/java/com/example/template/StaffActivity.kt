package com.example.template

import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.template.domain.Rule
import com.example.template.model.Model
import com.example.template.viewmodel.MyViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class StaffActivity : AppCompatActivity() {
    private lateinit var personViewModel: MyViewModel
    private val model: Model by viewModels()
    private lateinit var adapter: ListAdapter
    private lateinit var view: View
    private lateinit var progress: ProgressDialog


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_staff)
        progress = ProgressDialog(this)
        progress.setTitle("Loading")
        progress.setMessage("Please wait...")
        progress.setCancelable(false)
        view = View(this)
        personViewModel = ViewModelProviders.of(this).get(MyViewModel::class.java)
        adapter = ListAdapter(this)
        fetchData()
        setupRecyclerView(findViewById(R.id.recyclerview))

        val fab = findViewById<FloatingActionButton>(R.id.fab)
        fab.setOnClickListener {
            val intent = Intent(this@StaffActivity, AddActivity::class.java)
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
                progress.show()

                GlobalScope.launch(Dispatchers.Main) {

                    areChangesToBeDone()
                    if (personViewModel.data?.value?.size == 0) {
                        logd("db size is  0")
                        val myGrades = model.getAll()
                        if (myGrades == null) {
                            //the server is off
                            displayMessage("The server is down, using local data.")
                        } else {
                            personViewModel.insertAll(myGrades)
                            logd("done inserting")
                        }
                    }


                }
                progress.dismiss()
            }
        }
        return super.onOptionsItemSelected(item)
    }


    inner class ListAdapter internal constructor(
        context: Context
    ) : RecyclerView.Adapter<ListAdapter.PeopleViewHolder>() {

        private val inflater: LayoutInflater = LayoutInflater.from(context)
        private var data = mutableListOf<Rule>()

        inner class PeopleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val nameItemView: TextView = itemView.findViewById(R.id.name)
            val levelItemView: TextView = itemView.findViewById(R.id.level)
            val idItemView: TextView = itemView.findViewById(R.id.id)
            val ivSelect: Button = itemView.findViewById(R.id.ivSelect)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PeopleViewHolder {
            val itemView = inflater.inflate(R.layout.recycler_item, parent, false)
            return PeopleViewHolder(itemView)
        }

        override fun onBindViewHolder(holder: PeopleViewHolder, position: Int) {
            val current = data[position]
            holder.nameItemView.text = current.name
            holder.levelItemView.text = current.level.toString()
            holder.idItemView.text = current.id.toString()

            holder.ivSelect.setOnClickListener {
                logd("to see details of $current")
                getDetails(current)

            }
        }


        internal fun setData(myGrades: List<Rule>) {
            this.data.clear()
            this.data.addAll(myGrades)
            notifyDataSetChanged()
        }

        override fun getItemCount() = data.size

    }


    private fun getDetails(pers: Rule) {
        GlobalScope.launch(Dispatchers.Main) {
            progress.show()
            val intent = Intent(this@StaffActivity, AddActivity::class.java)
            intent.putExtra("Id", pers.id)
            startActivity(intent)
            progress.dismiss()
        }
    }

    private fun setupRecyclerView(recyclerView: RecyclerView) {
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)
    }

    private fun fetchData() {
        personViewModel.getAll()

        personViewModel.getAllChanged()
        personViewModel.changedData?.observe { }

//            if (personViewModel.data!!.value?.size == 0 || personViewModel.data!!.value == null) {
//                logd("db size is  0")
//                val myGrades = model.getAll()
//                if (myGrades == null) {
//                    //the server is off
//                    displayMessage("The server is down, using local data.")
//                } else {
//                    areChangesToBeDone()
//                    personViewModel.insertAll(myGrades)
//                    logd("done inserting")
//                }
//            }
        progress.show()

        personViewModel.data?.observe(this,
            { sections ->

                progress.show()

                if (sections == null || sections.isEmpty()) {
                    GlobalScope.launch(Dispatchers.Main) {
                        val myGrades = model.getAll()
                        if (myGrades == null) {
                            //the server is off
                            displayData(emptyList())
                            displayMessage("The server is down, using local data.")
                        } else {
                            areChangesToBeDone()
                            personViewModel.insertAll(myGrades)
                            logd("done inserting")
                        }
                    }

                } else {
                    // One or more items retrieved, no need to call your api for data.
                    personViewModel.data!!.value?.let { displayData(it ?: emptyList()) }
                }
                progress.dismiss()
            })
        progress.dismiss()
    }


    private fun displayData(gr: List<Rule>) {
        adapter.setData(gr)
    }

    private fun observeModel() {
        personViewModel.getAll()
        logd(" my data is ${personViewModel.data?.value}")

        //  personViewModel.data?.observe { displayData(it ?: emptyList()) }
    }

    private fun <T> LiveData<T>.observe(observe: (T?) -> Unit) =
        observe(this@StaffActivity, { observe(it) })

    private fun displayMessage(myMessage: String) {
        val parentLayout: View = findViewById(android.R.id.content)
        Snackbar.make(parentLayout, myMessage, Snackbar.LENGTH_LONG)
            .setAction("CLOSE") { }
            .setActionTextColor(resources.getColor(android.R.color.holo_red_light))
            .show()
    }

    private suspend fun areChangesToBeDone(): Boolean {
        val dbGrades = personViewModel.changedData?.value
        progress.show()
        try {
            if (dbGrades != null) {
                for (gr: Rule in dbGrades) {
                    logd(gr)
                    if (gr.changed == 1) {
                        //to add
                        gr.changed = 0
                        val res = model.add(gr)
                        if (res != "off") {
                            val myObj = deserialize(res)
                            if (myObj != null) {
                                personViewModel.delete(gr.id)
                                gr.id = myObj.id
                                gr.status = myObj.status
                                displayFinalMessage(gr)
                                personViewModel.insert(gr)
                            } else {
                                displayMessage("Error when saving.")
                            }
                        } else {
                            displayMessage("The server if off.")
                        }
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
        } catch (e: Exception) {
            progress.dismiss()
            return false
        }
        progress.dismiss()
        return true
    }


    private fun deserialize(myString: String): Rule? {

        try {
            var indexStart = myString.indexOf("id") + 3
            var indexStop = myString.indexOf("name") - 3
            val id = getStringFromArea(indexStart, indexStop, myString).toInt()
            if (id == 0) {
                return null
            }
            indexStart = myString.indexOf("status") + 7
            indexStop = myString.indexOf("from") - 3
            val status = getStringFromArea(indexStart, indexStop, myString)
            return Rule(id, "", 0, status, 0, 0, 0)
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

    private fun displayFinalMessage(rule: Rule) {
        val text = "The rule with the name ${rule.name} and level ${rule.level} has the" +
                " status ${rule.status} and from ${rule.from} to ${rule.to} "
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
    }

}