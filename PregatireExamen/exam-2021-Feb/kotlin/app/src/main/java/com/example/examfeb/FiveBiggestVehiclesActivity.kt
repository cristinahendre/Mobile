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


class FiveBiggestVehiclesActivity : AppCompatActivity() {

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
        setContentView(R.layout.activity_5biggest)

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


    private fun displayVehicles(gr: List<Vehicle>) {
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
        private var vehicles = emptyList<Vehicle>()

        inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val licenseItemView: TextView = itemView.findViewById(R.id.license)
            val seatsItemView: TextView = itemView.findViewById(R.id.seats)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val itemView = inflater.inflate(R.layout.item_biggest5, parent, false)
            return ViewHolder(itemView)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val current = vehicles[position]
            GlobalScope.launch(Dispatchers.Main) {

                holder.licenseItemView.text = current.license
                holder.seatsItemView.text = current.seats.toString()

            }

        }

        internal fun setVehicles(all: List<Vehicle>) {
            this.vehicles = all
            notifyDataSetChanged()
        }

        override fun getItemCount() = vehicles.size

    }

    private fun observeModel() {

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
                val mySorted: List<Vehicle> = myGrades.sortedByDescending { it.cargo }
                logd("list sorted $mySorted")
                myGrades = mutableListOf()
                var i = 0
                for (value in mySorted) {
                    if (i == 5) break
                    myGrades.add(value)
                    i += 1
                }
                myGrades = myGrades.sortedByDescending { it.cargo }
                logd("display $myGrades")
                displayVehicles(myGrades)
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
        observe(this@FiveBiggestVehiclesActivity, { observe(it) })

}