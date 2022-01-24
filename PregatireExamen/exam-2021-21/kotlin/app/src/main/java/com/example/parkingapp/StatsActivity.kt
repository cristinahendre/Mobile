package com.example.parkingapp

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.Button
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.parkingapp.domain.Space
import com.example.parkingapp.viewmodel.NetworkModel
import com.example.parkingapp.viewmodel.SpaceViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class StatsActivity : AppCompatActivity() {

    private val model: NetworkModel by viewModels()
    private lateinit var spaceViewModel: SpaceViewModel
    private lateinit var adapter: MyAdapter
    private lateinit var progress: ProgressDialog


    override fun onCreate(savedInstanceState: Bundle?) {
        progress = ProgressDialog(this)
        progress.setTitle("Loading")
        progress.setMessage("Please wait...")
        progress.setCancelable(false)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_stats)

        spaceViewModel = ViewModelProviders.of(this).get(SpaceViewModel::class.java)
        adapter = MyAdapter(this)
        fetchData()
        setupRecyclerView(findViewById(R.id.recyclerview))

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


    private fun displayAll(gr: List<Space>) {
        adapter.setSpaces(gr)
    }

    private fun setupRecyclerView(recyclerView: RecyclerView) {

        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)
    }

    inner class MyAdapter internal constructor(
        context: Context
    ) : RecyclerView.Adapter<MyAdapter.ViewHolder>() {

        private val inflater: LayoutInflater = LayoutInflater.from(context)
        private var spaces = emptyList<Space>()

        inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val countItemView: TextView = itemView.findViewById(R.id.count)
            val numberItemView: TextView = itemView.findViewById(R.id.number)
            val statusItemView: TextView = itemView.findViewById(R.id.status)
            val addressItemView: TextView = itemView.findViewById(R.id.address)

        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val itemView = inflater.inflate(R.layout.normal_item, parent, false)
            return ViewHolder(itemView)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val current = spaces[position]
            GlobalScope.launch(Dispatchers.Main) {

                holder.countItemView.text = current.count.toString()
                holder.statusItemView.text = current.status
                holder.numberItemView.text = current.number
                holder.addressItemView.text = current.address
            }
        }

        internal fun setSpaces(all: List<Space>) {
            this.spaces = all
            notifyDataSetChanged()
        }

        override fun getItemCount() = spaces.size

    }


    private fun observeModel() {


    }

    private fun fetchData() {
        GlobalScope.launch(Dispatchers.Main) {
            progress.show()

            spaceViewModel.getAllChanged()
            spaceViewModel.allSpacesChanged?.observe { }
            var mySpaces = model.getAll()
            logd("my spaces in activity $mySpaces")
            if (mySpaces == null) {
                //the server is off
                displayMessage("The server is down. Please retry.")
            } else {
                val mySorted = mySpaces.sortedByDescending { it.count }
                mySpaces = mutableListOf()
                var i = 0
                for (space in mySorted) {
                    if (i == 15) break
                    mySpaces.add(space)
                    i += 1
                }
                logd("sorted 15 = $mySpaces")
                displayAll(mySpaces)
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
        observe(this@StatsActivity, { observe(it) })
}