package com.example.examfeb
import android.content.Context
import android.os.Bundle
import android.view.*
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.examfeb.domain.Vehicle
import com.example.examfeb.models.Model
import com.example.examfeb.viewmodel.VehicleViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class RegistrationActivity : AppCompatActivity() {

    private val model: Model by viewModels()
    private lateinit var vehicleViewModel: VehicleViewModel

    private lateinit var adapter: VehicleAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)

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
                    val myStudentsGrades = model.vehicles
                    if (myStudentsGrades == null) {
                        //use db data
                        Toast.makeText(
                            applicationContext,
                            "The server is down",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
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

        vehicleViewModel.allVehicles
            ?.observe(this, { all ->
                if (all != null) {
                    adapter.setVehicles(all)
                }
            })
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

                holder.licenseItemView.text = current.License
                holder.statusItemView.text = current.Status
                holder.seatsItemView.text = current.Seats.toString()
                holder.driverItemView.text = current.Driver
                holder.statusItemView.text = current.Status
                holder.colorItemView.text = current.Color
                holder.cargoItemView.text = current.Cargo.toString()
            }
        }

        internal fun setVehicles(all: List<Vehicle>) {
            this.vehicles = all
            notifyDataSetChanged()
        }

        override fun getItemCount() = vehicles.size

    }

    override fun onDestroy() {
        super.onDestroy()
    }

    private fun observeModel() {
        vehicleViewModel.getVehicles()
        vehicleViewModel.allVehicles?.observe { displayVehicles(it ?: emptyList()) }
    }

    private fun fetchData() {
        GlobalScope.launch(Dispatchers.Main) {

            vehicleViewModel.getVehiclesChanged()
            vehicleViewModel.vehiclesChanged?.observe {  }
            val myGrades = model.getVehicles()
            logd("My grades $myGrades")
            if (myGrades == null) {
                //the server is off
                displayMessage("The server is down. Please retry.")
            } else {
                areChangesToBeDone()
                vehicleViewModel.insertAll(myGrades)
                logd("done inserting")
            }

        }
    }
    private fun displayMessage(myMessage: String) {
        Toast.makeText(
            applicationContext,
            myMessage,
            Toast.LENGTH_SHORT
        ).show()
    }

    private suspend fun areChangesToBeDone(): Boolean{
        val dbChanges = vehicleViewModel.vehiclesChanged?.value
        try {
            if (dbChanges != null) {
                logd("are changes to be done, with grades $dbChanges")
                if (dbChanges != null) {
                    for (gr: Vehicle in dbChanges) {
                        logd(gr)
                        if (gr.Changed == 1) {
                            //to add
                            gr.Changed = 0
                            val res =model.add(gr)
                            if(res!=-1) {
                                gr.Changed = 0
                                vehicleViewModel.update(gr)
                            }
                            else gr.Changed =1
                        }
                        if(gr.Changed ==2){
                            //to delete
                            gr.Changed = 0
                            val res = model.delete(gr.Id)
                            if(res != -1) {
                                vehicleViewModel.delete(gr.Id)
                            }
                            else gr.Changed =2
                        }
                        if(gr.Changed ==3){
                            //to update
                            gr.Changed = 0
                            val res =model.update(gr)
                            if(res != -1) {
                                vehicleViewModel.update(gr)
                            }
                            else gr.Changed = 3
                        }
                    }
                }
            }
        }
        catch (e: Exception){
            return false
        }
        return true
    }


    private fun <T> LiveData<T>.observe(observe: (T?) -> Unit) =
        observe(this@RegistrationActivity, { observe(it) })

}