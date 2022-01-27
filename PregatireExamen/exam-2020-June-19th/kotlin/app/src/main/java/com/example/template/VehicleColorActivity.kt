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
import com.example.template.domain.Vehicle
import com.example.template.model.Model
import com.example.template.viewmodel.MyViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class VehicleColorActivity : AppCompatActivity() {
    private lateinit var myViewModel: MyViewModel
    private val model: Model by viewModels()
    private lateinit var adapter: ListAdapter
    private lateinit var view: View
    private lateinit var progress: ProgressDialog
    private lateinit var color: String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_vehiclecolor)
        progress = ProgressDialog(this)
        progress.setTitle("Loading")
        progress.setMessage("Please wait...")
        progress.setCancelable(false)
        view = View(this)
        myViewModel = ViewModelProviders.of(this).get(MyViewModel::class.java)
        adapter = ListAdapter(this)
        val bundle: Bundle? = intent.extras
        if (bundle != null) {
            color = bundle.getString("Color").toString()
        }
        fetchData()
        observeModel()
        setupRecyclerView(findViewById(R.id.recyclerview))
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
                    fetchData()
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
        private var data = mutableListOf<Vehicle>()

        inner class PeopleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val nameItemView: TextView = itemView.findViewById(R.id.name)
            val statusItemView: TextView = itemView.findViewById(R.id.status)
            val passengersItemView: TextView = itemView.findViewById(R.id.passengers)
            val paintItemView: TextView = itemView.findViewById(R.id.paint)
            val driverItemView: TextView = itemView.findViewById(R.id.driver)
            val capacityItemView: TextView = itemView.findViewById(R.id.capacity)
            val ivDelete: Button = itemView.findViewById(R.id.ivDelete)

        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PeopleViewHolder {
            val itemView = inflater.inflate(R.layout.delete_item, parent, false)
            return PeopleViewHolder(itemView)
        }

        override fun onBindViewHolder(holder: PeopleViewHolder, position: Int) {
            val current = data[position]
            holder.nameItemView.text = current.name
            holder.statusItemView.text = current.status
            holder.passengersItemView.text = current.passengers.toString()
            holder.driverItemView.text = current.driver
            holder.paintItemView.text = current.paint
            holder.capacityItemView.text = current.capacity.toString()
            holder.ivDelete.setOnClickListener {
                logd("to delete $current")
                delete(current)
            }
        }


        internal fun setData(myData: List<Vehicle>) {
            this.data.clear()
            this.data.addAll(myData)
            notifyDataSetChanged()
        }

        override fun getItemCount() = data.size

    }


    private fun delete(v: Vehicle) {
        GlobalScope.launch(Dispatchers.Main) {
            progress.show()
            val resp = model.delete(v.id)
            if (resp == "off") {
                displayMessage("The server is down. Please retry.")
            } else {
                val myObj = deserialize(resp)
                if (myObj == null) {
                    displayMessage("Invalid vehicle id")
                } else {
                    myViewModel.delete(v.id)
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

        GlobalScope.launch(Dispatchers.Main) {
            progress.show()

            val myData = model.getVehiclesOfColor(color)
            if (myData == null) {
                //the server is off
                displayData(emptyList())
                displayMessage("The server is down, there is no local data.")
            } else {
                myViewModel.getVehiclesOfColor(color)
                myViewModel.coloredData?.observe { displayData(it ?: emptyList()) }
                displayData(myData)
            }
            progress.dismiss()
        }
    }

    private fun observeModel() {
        logd("colored vehicles ${myViewModel.coloredData?.value}")
    }

    private fun <T> LiveData<T>.observe(observe: (T?) -> Unit) =
        observe(this@VehicleColorActivity, { observe(it) })

    private fun displayData(gr: List<Vehicle>) {
        adapter.setData(gr)
    }


    private fun displayMessage(myMessage: String) {
        val parentLayout: View = findViewById(android.R.id.content)
        Snackbar.make(parentLayout, myMessage, Snackbar.LENGTH_LONG)
            .setAction("CLOSE") { }
            .setActionTextColor(resources.getColor(android.R.color.holo_red_light))
            .show()
    }

    private fun deserialize(myString: String): Vehicle? {

        try {
            var indexStart = myString.indexOf("id") + 3
            var indexStop = myString.indexOf("name") - 3
            val id = getStringFromArea(indexStart, indexStop, myString).toInt()
            if (id == 0) {
                return null
            }
            indexStart = myString.indexOf("status") + 7
            indexStop = myString.indexOf("passengers") - 3
            val status = getStringFromArea(indexStart, indexStop, myString)
            return Vehicle(id, "", status, 0, "", "", 0, 0)
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