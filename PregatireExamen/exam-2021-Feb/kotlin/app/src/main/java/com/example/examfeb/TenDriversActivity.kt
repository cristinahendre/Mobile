package com.example.examfeb

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Context
import android.os.Bundle
import android.view.*
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.examfeb.domain.Vehicle
import com.example.examfeb.models.Model
import com.example.examfeb.viewmodel.VehicleViewModel
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class TenDriversActivity : AppCompatActivity() {

    private val model: Model by viewModels()
    private lateinit var vehicleViewModel: VehicleViewModel
    private lateinit var adapter: VehicleAdapter
    private lateinit var progress: ProgressDialog


    override fun onCreate(savedInstanceState: Bundle?) {
        progress = ProgressDialog(this)
        progress.setTitle("Loading")
        progress.setMessage("Please wait...")
        progress.setCancelable(false)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_10drivers)

        vehicleViewModel = ViewModelProviders.of(this).get(VehicleViewModel::class.java)
        adapter = VehicleAdapter(this)
        fetchData()
        setupRecyclerView(findViewById(R.id.recyclerview))
        observeModel()


    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_retry, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            R.id.retry -> {
                logd("retry clicked")
                GlobalScope.launch(Dispatchers.Main) {
                    fetchData()
                    observeModel()
                }

            }
        }
        return super.onOptionsItemSelected(item)
    }


    private fun displayVehicles(gr: Map<String,Int>) {
        adapter.setVehicles(gr)
    }

    private fun setupRecyclerView(recyclerView: RecyclerView) {

        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)
    }

    inner class VehicleAdapter internal constructor(
        context: Context
    ) : RecyclerView.Adapter<VehicleAdapter.ViewHolder>() {

        private val inflater: LayoutInflater = LayoutInflater.from(context)
        private var vehicles = emptyMap<String,Int>()

        inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val nameItemView: TextView = itemView.findViewById(R.id.driver)
            val numberItemView: TextView = itemView.findViewById(R.id.number)

        }


        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val itemView = inflater.inflate(R.layout.drivers_10, parent, false)
            return ViewHolder(itemView)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val current = vehicles.toList()[position]
            GlobalScope.launch(Dispatchers.Main) {

                //holder.numberItemView.text = current.number.toString()
                holder.nameItemView.text = current.first
                holder.numberItemView.text = current.second.toString()
            }

        }

        internal fun setVehicles(all: Map<String,Int>) {
            this.vehicles = all
            notifyDataSetChanged()
        }

        override fun getItemCount() = vehicles.size

    }

    private fun observeModel() {
//        vehicleViewModel.getVehiclesOrdered()
//        vehicleViewModel.allVehicles?.observe { displayVehicles(it ?: emptyList()) }

    }

    private fun fetchData() {
        GlobalScope.launch(Dispatchers.Main) {
            progress.show()

            vehicleViewModel.getVehiclesChanged()
            vehicleViewModel.vehiclesChanged?.observe { }
            var myGrades = model.getTenVehicles()
            if (myGrades == null) {
                //the server is off
                displayMessage("The server is down. Please retry.")
            } else {
                var map:MutableMap<String, Int> = emptyMap<String, Int>().toMutableMap()
                var finalMap:MutableMap<String, Int> = emptyMap<String, Int>().toMutableMap()
                for(vehicle:Vehicle in myGrades){

                    if(map.containsKey(vehicle.driver)){
                        var value =1+ map[vehicle.driver]!!
                        map[vehicle.driver] =value
                    }
                    else{
                        map[vehicle.driver] = 1
                    }
                }
                val myList =map.toList().sortedByDescending { (_,value)->value }
                map =myList.toMap().toMutableMap()

                logd("my list $myList and map = $map")
                var i=0
                if(map.size>10){

                    for(key in map.keys){
                        if(i == 10) break
                        map[key]?.let { finalMap.put(key, it) }
                    }

                    displayVehicles(finalMap)

                }
                else{
                    displayVehicles(map)
                }
            }
            progress.dismiss()


        }
    }

    @SuppressLint("ShowToast")
    private fun displayMessage(myMessage: String) {

        val parentLayout: View = findViewById(android.R.id.content)
        Snackbar.make(parentLayout, myMessage, Snackbar.LENGTH_LONG)
            .setAction("CLOSE") { }
            .setActionTextColor(resources.getColor(android.R.color.holo_red_light))
            .show()
    }


    private fun <T> LiveData<T>.observe(observe: (T?) -> Unit) =
        observe(this@TenDriversActivity, { observe(it) })

}