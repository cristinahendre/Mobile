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


class DetailsActivity : AppCompatActivity() {

    private val model: Model by viewModels()
    private lateinit var vehicleViewModel: VehicleViewModel
    private lateinit var adapter: VehicleAdapter
    private lateinit var progress: ProgressDialog
    private lateinit var vehicle: Vehicle
    private lateinit var license: String
    private lateinit var status: String
    private lateinit var driver: String
    private lateinit var color: String
    private var seats: Int = 0
    private var id: Int = 0
    private var cargo: Int = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        progress = ProgressDialog(this)
        progress.setTitle("Loading")
        progress.setMessage("Please wait...")
        progress.setCancelable(false)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_details)
        val extras = intent.extras
        if (extras != null) {
            license = extras.getString("License")!!
            status = extras.getString("Status")!!
            seats = extras.getInt("Seats")
            color = extras.getString("Color")!!
            driver = extras.getString("Driver")!!
            cargo = extras.getInt("Cargo")
            id = extras.getInt("Id")
            vehicle = Vehicle(id, license, status, seats, driver, color, cargo, 0)
            logd("received vehicle is $vehicle")
        }

        vehicleViewModel = ViewModelProviders.of(this).get(VehicleViewModel::class.java)
        adapter = VehicleAdapter(this)
        fetchData()
        setupRecyclerView(findViewById(R.id.recyclerview))

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
            val statusItemView: TextView = itemView.findViewById(R.id.status)
            val seatsItemView: TextView = itemView.findViewById(R.id.seats)
            val driverItemView: TextView = itemView.findViewById(R.id.driver)
            val colorItemView: TextView = itemView.findViewById(R.id.color)
            val cargoItemView: TextView = itemView.findViewById(R.id.cargo)
        }


        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val itemView = inflater.inflate(R.layout.recycler_item, parent, false)
            return ViewHolder(itemView)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val current = vehicles[position]
            GlobalScope.launch(Dispatchers.Main) {
                holder.licenseItemView.text = current.license
                holder.statusItemView.text = current.status
                holder.seatsItemView.text = current.seats.toString()
                holder.driverItemView.text = current.driver
                holder.statusItemView.text = current.status
                holder.colorItemView.text = current.color
                holder.cargoItemView.text = current.cargo.toString()

            }

        }

        internal fun setVehicles(all: List<Vehicle>) {
            this.vehicles = all
            notifyDataSetChanged()
        }

        override fun getItemCount() = vehicles.size

    }


    private fun fetchData() {
        GlobalScope.launch(Dispatchers.Main) {
            progress.show()
            displayVehicles(listOf(vehicle))
            progress.dismiss()

        }
    }

}