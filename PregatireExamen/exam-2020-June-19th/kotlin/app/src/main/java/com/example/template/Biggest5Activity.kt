package com.example.template

import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.*
import android.widget.TextView
import androidx.activity.viewModels
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.template.domain.Vehicle
import com.example.template.model.Model
import com.example.template.viewmodel.MyViewModel
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class Biggest5Activity : AppCompatActivity() {
    private lateinit var myViewModel: MyViewModel
    private val model: Model by viewModels()
    private lateinit var adapter: ListAdapter
    private lateinit var view: View
    private lateinit var progress: ProgressDialog


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_biggest5)
        progress = ProgressDialog(this)
        progress.setTitle("Loading")
        progress.setMessage("Please wait...")
        progress.setCancelable(false)
        view = View(this)
        myViewModel = ViewModelProviders.of(this).get(MyViewModel::class.java)
        adapter = ListAdapter(this)
        fetchData()
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
            val capacityItemView: TextView = itemView.findViewById(R.id.capacity)

        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PeopleViewHolder {
            val itemView = inflater.inflate(R.layout.item5, parent, false)
            return PeopleViewHolder(itemView)
        }

        override fun onBindViewHolder(holder: PeopleViewHolder, position: Int) {
            val current = data[position]
            holder.nameItemView.text = current.name
            holder.capacityItemView.text = current.capacity.toString()
        }


        internal fun setData(myData: List<Vehicle>) {
            this.data.clear()
            this.data.addAll(myData)
            notifyDataSetChanged()
        }

        override fun getItemCount() = data.size

    }


    private fun setupRecyclerView(recyclerView: RecyclerView) {
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)
    }

    private fun fetchData() {

        GlobalScope.launch(Dispatchers.Main) {
            progress.show()
            var myData = model.getAll()
            if (myData == null) {
                //the server is off
                displayData(emptyList())
                displayMessage("The server is down, there is no local data.")
            } else {
                val mySorted =myData.sortedByDescending { it.capacity }
                myData =mySorted.subList(0,5)
                displayData(myData)
            }
            progress.dismiss()
        }
    }

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

}