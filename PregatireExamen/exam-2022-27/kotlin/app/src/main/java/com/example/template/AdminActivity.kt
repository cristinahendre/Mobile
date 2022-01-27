package com.example.template

import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Context
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
import com.example.template.domain.Dosar
import com.example.template.model.Model
import com.example.template.viewmodel.MyViewModel
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class AdminActivity : AppCompatActivity() {
    private lateinit var myViewModel: MyViewModel
    private val model: Model by viewModels()
    private lateinit var adapter: ListAdapter
    private lateinit var view: View
    private lateinit var progress: ProgressDialog


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin)
        progress = ProgressDialog(this)
        progress.setTitle("Loading")
        progress.setMessage("Please wait...")
        progress.setCancelable(false)
        view = View(this)
        myViewModel = ViewModelProviders.of(this).get(MyViewModel::class.java)
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
        private var data = mutableListOf<Dosar>()

        inner class PeopleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val idItemView: TextView = itemView.findViewById(R.id.id)
            val medieItemView: TextView = itemView.findViewById(R.id.medie)
            val ivValidate: Button = itemView.findViewById(R.id.ivValidate)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PeopleViewHolder {
            val itemView = inflater.inflate(R.layout.recycler_item, parent, false)
            return PeopleViewHolder(itemView)
        }

        override fun onBindViewHolder(holder: PeopleViewHolder, position: Int) {
            val current = data[position]
            holder.idItemView.text = current.id.toString()
            holder.medieItemView.text = current.medie.toString()

            holder.ivValidate.setOnClickListener {
                logd("to validate $current")
                validate(current)
            }
        }

        internal fun setData(myData: List<Dosar>) {
            this.data.clear()
            this.data.addAll(myData)
            notifyDataSetChanged()
        }

        override fun getItemCount() = data.size

    }

    private fun validate(space: Dosar) {
        GlobalScope.launch(Dispatchers.Main) {
            progress.show()
            val result = model.validate(space)
            logd("server response = $result")
            if (result == "off") {
                //the server is off
                displayMessage("The server is off.")
            } else {
                space.status = true
                myViewModel.update(space)
            }
            progress.dismiss()
        }
    }


    private fun setupRecyclerView(recyclerView: RecyclerView) {
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)
    }

    private fun fetchData() {
        GlobalScope.launch(Dispatchers.Main) {

            progress.show()
            val myGrades = model.getAll()
            if (myGrades == null) {
                //the server is off
                displayMessage("The server is down.")
            } else {
                areChangesToBeDone()
                val updatedData = myGrades.filter { !it.status }
                val valoare1 = 0.75
                val valoare2 = 0.25
                for (data in updatedData) {
                    data.medie = (data.medie1 * valoare1 + data.medie2 * valoare2).toInt()
                }
                myViewModel.getNevalidate()
                myViewModel.data?.observe { displayData(it ?: emptyList()) }
                displayData(updatedData)
            }
            progress.dismiss()

        }
    }


    private fun displayData(gr: List<Dosar>) {
        adapter.setData(gr)
    }

    private fun observeModel() {
        myViewModel.getAllChanged()
        myViewModel.changedData?.observe { }

    }

    private fun <T> LiveData<T>.observe(observe: (T?) -> Unit) =
        observe(this@AdminActivity, { observe(it) })

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
                for (gr: Dosar in dbGrades) {
                    logd(gr)
                    if (gr.changed == 1) {
                        //to add
                        gr.changed = 0
                        val res = model.add(gr)
                        if (res != "off") {
                            val myObj = deserialize(res)
                            if (myObj != null) {
                                displayFinalMessage(myObj.id)
                                myViewModel.delete(gr.id)
                                gr.id = myObj.id
                                myViewModel.insert(gr)
                            } else {
                                displayMessage("Error when saving.")
                            }
                        } else gr.changed = 1
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

    private fun displayFinalMessage(id: Int) {
        Toast.makeText(this, id.toString(), Toast.LENGTH_SHORT).show()
    }

    private fun deserialize(myString: String): Dosar? {

        try {
            var indexStart = myString.indexOf("id") + 3
            var indexStop = myString.indexOf("nume") - 3
            val id = getStringFromArea(indexStart, indexStop, myString).toInt()
            if (id == 0) {
                return null
            }
            return Dosar(id, "", 0, 0, 0, false, 0)
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