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
import com.example.template.domain.Rezervare
import com.example.template.model.Model
import com.example.template.viewmodel.MyViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class SecretareActivity : AppCompatActivity() {
    private lateinit var myViewModel: MyViewModel
    private val model: Model by viewModels()
    private lateinit var adapter: ListAdapter
    private lateinit var view: View
    private lateinit var progress: ProgressDialog


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_secretare)
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
                    progress.show()
                    areChangesToBeDone()
                    fetchData()
                    observeModel()
                    progress.dismiss()
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
        private var data = mutableListOf<Rezervare>()

        inner class PeopleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val numeItemView: TextView = itemView.findViewById(R.id.nume)
            val idItemView: TextView = itemView.findViewById(R.id.id)
            val doctorItemView: TextView = itemView.findViewById(R.id.doctor)
            val dataItemView: TextView = itemView.findViewById(R.id.data)
            val oraItemView: TextView = itemView.findViewById(R.id.ora)
            val ivSelect: Button = itemView.findViewById(R.id.ivSelect)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PeopleViewHolder {
            val itemView = inflater.inflate(R.layout.recycler_item, parent, false)
            return PeopleViewHolder(itemView)
        }

        override fun onBindViewHolder(holder: PeopleViewHolder, position: Int) {
            val current = data[position]
            holder.numeItemView.text = current.nume
            holder.doctorItemView.text = current.doctor
            holder.idItemView.text = current.id.toString()
            holder.dataItemView.text = current.data.toString()
            holder.oraItemView.text = current.ora.toString()


            holder.ivSelect.setOnClickListener {
                logd(" to select $current")
                confirma(current)
            }
        }


        internal fun setData(myData: List<Rezervare>) {
            this.data.clear()
            this.data.addAll(myData)
            notifyDataSetChanged()
        }

        override fun getItemCount() = data.size

    }

    private fun confirma(r: Rezervare) {
        GlobalScope.launch(Dispatchers.Main) {
            progress.show()

            val resp = model.confirm(r.id)
            if (resp == "off") {
                displayMessage("The server is down.")
                r.status = true
                r.changed = 3
                myViewModel.update(r)
            } else {
                val myObj = deserialize(resp)
                if (myObj == null) {
                    displayMessage("No reservation with the specified id! id: ${r.id}")
                } else {

                    r.status = myObj.status
                    myViewModel.update(r)
                    displayMessage("Succes.")

                }
            }
            progress.dismiss()
        }
    }

    private fun setupRecyclerView(recyclerView: RecyclerView) {

        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)
    }

    private fun fetchData() {
        progress.show()
        myViewModel.getAll()
        myViewModel.data?.observe(this,
            { sections ->
                progress.show()

                if (sections == null || sections.isEmpty()) {
                    GlobalScope.launch(Dispatchers.Main) {
                        progress.show()
                        val myData = model.getAll()
                        if (myData == null) {
                            //the server is off
                            displayData(emptyList())
                            displayMessage("The server is down, there is no local data.")
                        } else {
                            areChangesToBeDone()
                            myViewModel.insertAll(myData)
                            logd("done inserting")
                        }
                        progress.dismiss()
                    }

                } else {
                    progress.show()
                    myViewModel.data!!.value?.let { displayData(it) }
                    progress.dismiss()
                }
                progress.dismiss()
            })

        progress.dismiss()
    }


    private fun displayData(gr: List<Rezervare>) {
        adapter.setData(gr)
    }

    private fun observeModel() {
        myViewModel.getAllChanged()
        myViewModel.changedData?.observe { }
    }

    private fun <T> LiveData<T>.observe(observe: (T?) -> Unit) =
        observe(this@SecretareActivity, { observe(it) })

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
                for (gr: Rezervare in dbGrades) {
                    logd(gr)
                    if (gr.changed == 1) {
                        //to add
                        gr.changed = 0
                        val res = model.add(gr)
                        if (res != "off") {
                            val myObj = deserialize(res)
                            if (myObj != null) {
                                displayFinalMessage(myObj)
                                myViewModel.delete(gr.id)
                                gr.id = myObj.id
                                myViewModel.insert(gr)
                            } else {
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
                        val res = model.confirm(gr.id)
                        if (res != "off") {
                            val obj = deserialize(res)
                            if (obj == null) {
                                displayMessage("No reservation with the specified id! id: ${gr.id}")
                            } else {
                                displayMessage("Succes.")
                                myViewModel.update(gr)
                            }
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

    private fun displayFinalMessage(rezervare: Rezervare) {
        val text = "Rezervarea pacientului ${rezervare.nume} cu doctorul ${rezervare.doctor} " +
                " se va desfasura in ziua ${rezervare.data} , la ora ${rezervare.ora} si are" +
                " detaliile ${rezervare.detalii}"
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
    }

    private fun deserialize(myString: String): Rezervare? {

        //Rezervare(id=10, nume=A9, doctor=D1, data=7, ora=16, detalii=test 33, status=true, changed=0)
        try {
            var indexStart = myString.indexOf("id") + 3
            var indexStop = myString.indexOf("nume") - 3
            val id = getStringFromArea(indexStart, indexStop, myString).toInt()
            if (id == 0) {
                return null
            }
            indexStart = myString.indexOf("status") + 7
            indexStop = myString.indexOf("changed") - 3
            val status = getStringFromArea(indexStart, indexStop, myString).toBoolean()
            return Rezervare(id, "", "", 0, 0, "", status, 0)
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