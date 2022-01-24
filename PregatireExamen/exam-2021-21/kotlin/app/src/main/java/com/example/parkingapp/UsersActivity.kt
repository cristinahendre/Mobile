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


class UsersActivity : AppCompatActivity() {

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
        setContentView(R.layout.activity_free)

        spaceViewModel = ViewModelProviders.of(this).get(SpaceViewModel::class.java)
        adapter = MyAdapter(this)
        spaceViewModel.getAll()
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
            val btnUpdate: Button = itemView.findViewById(R.id.ivUpdate)

        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val itemView = inflater.inflate(R.layout.update_item, parent, false)
            return ViewHolder(itemView)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val current = spaces[position]
            GlobalScope.launch(Dispatchers.Main) {

                holder.countItemView.text = current.count.toString()
                holder.statusItemView.text = current.status
                holder.numberItemView.text = current.number
                holder.addressItemView.text = current.address

                holder.btnUpdate.setOnClickListener {
                    logd("to update $current")
                    update(current)
                }
            }
        }

        internal fun setSpaces(all: List<Space>) {
            this.spaces = all
            notifyDataSetChanged()
        }

        override fun getItemCount() = spaces.size

    }

    private fun update(space: Space) {
        GlobalScope.launch(Dispatchers.Main) {
            progress.show()
            space.status = "taken"
            val result = model.update(space)
            if (result == "off") {
                //the server is off
                displayMessage("The server is down. Please retry.")
            } else {
                val mySpace = deserializeSpace(result)
                logd("deserializare pe result: $mySpace")
                if (mySpace != null) {
                    space.status = mySpace.status
                    space.count = mySpace.count
                    spaceViewModel.update(space)
                }
            }
            progress.dismiss()
        }
    }

    private fun observeModel() {
        logd("observe with spaces ${model.freeSpaces}")
        spaceViewModel.allSpaces?.observe { model.freeSpaces.value?.let { it1 -> displayAll(it1) } }
    }

    private fun fetchData() {
        GlobalScope.launch(Dispatchers.Main) {
            progress.show()
            val mySpaces = model.getFreeSpaces()
            logd("my spaces in activity $mySpaces")
            if (mySpaces == null) {
                //the server is off
                displayMessage("The server is down. Please retry.")
            } else {
                val mySortedSpaces = mySpaces.sortedBy { it.number }
                displayAll(mySortedSpaces)
            }
            progress.dismiss()
        }
    }

    private fun deserializeSpace(myString: String): Space? {

        try {
            var indexStart = myString.indexOf("id") + 3
            var indexStop = myString.indexOf("number") - 3
            val id = getStringFromArea(indexStart, indexStop, myString).toInt()
            if (id == 0) {
                return null
            }
            indexStart = myString.indexOf("status") + 7
            indexStop = myString.indexOf("count") - 3
            val status = getStringFromArea(indexStart, indexStop, myString)

            indexStart = myString.indexOf("count") + 6
            indexStop = myString.indexOf("changed") - 3
            val count = getStringFromArea(indexStart, indexStop, myString).toInt()

            return Space(id, "", "", status, count, 0)
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

    @SuppressLint("ShowToast")
    private fun displayMessage(myMessage: String) {

        val parentLayout: View = findViewById(android.R.id.content)
        Snackbar.make(parentLayout, myMessage, Snackbar.LENGTH_LONG)
            .setAction("CLOSE") { }
            .setActionTextColor(resources.getColor(android.R.color.holo_red_light))
            .show()
    }

    private fun <T> LiveData<T>.observe(observe: (T?) -> Unit) =
        observe(this@UsersActivity, { observe(it) })
}