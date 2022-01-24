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


class ManagerActivity : AppCompatActivity() {

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
        setContentView(R.layout.activity_manager)

        spaceViewModel = ViewModelProviders.of(this).get(SpaceViewModel::class.java)
        adapter = MyAdapter(this)
        spaceViewModel.deleteAll()
        fetchData()
        setupRecyclerView(findViewById(R.id.recyclerview))
        observeModel()
        val addButton = findViewById<FloatingActionButton>(R.id.add)
        addButton.setOnClickListener {
            val intent = Intent(this@ManagerActivity, AddSpaceActivity::class.java)
            startActivity(intent)
        }
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
                    areChangesToBeDone()
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

        spaceViewModel.allSpaces
            ?.observe(this, { all ->
                if (all != null) {
                    adapter.setSpaces(all)
                }
            })
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)
    }

    inner class MyAdapter internal constructor(
        context: Context
    ) : RecyclerView.Adapter<MyAdapter.ViewHolder>() {

        private val inflater: LayoutInflater = LayoutInflater.from(context)
        private var spaces = emptyList<Space>()

        inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val idItemView: TextView = itemView.findViewById(R.id.id)
            val numberItemView: TextView = itemView.findViewById(R.id.number)
            val statusItemView: TextView = itemView.findViewById(R.id.status)
            val addressItemView: TextView = itemView.findViewById(R.id.address)
            val btnDelete: Button = itemView.findViewById(R.id.ivDelete)

        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val itemView = inflater.inflate(R.layout.delete_item, parent, false)
            return ViewHolder(itemView)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val current = spaces[position]
            GlobalScope.launch(Dispatchers.Main) {

                holder.idItemView.text = current.id.toString()
                holder.statusItemView.text = current.status
                holder.numberItemView.text = current.number
                holder.addressItemView.text = current.address

                holder.btnDelete.setOnClickListener {
                    logd("to delete $current")
                    delete(current)


                }
            }
        }

        internal fun setSpaces(all: List<Space>) {
            this.spaces = all
            notifyDataSetChanged()
        }

        override fun getItemCount() = spaces.size

    }

    private fun delete(space: Space) {
        GlobalScope.launch(Dispatchers.Main) {
            progress.show()
            val result = model.delete(space.id)
            if (result == "off") {
                //the server is off
                space.changed = 2
                spaceViewModel.update(space)
            }  else {
                spaceViewModel.delete(space.id)
            }
            progress.dismiss()
        }
    }

    private fun observeModel() {
        spaceViewModel.getAll()
        spaceViewModel.allSpaces?.observe { displayAll(it ?: emptyList()) }

    }

    private fun fetchData() {
        GlobalScope.launch(Dispatchers.Main) {
            progress.show()

            spaceViewModel.getAllChanged()
            spaceViewModel.allSpacesChanged?.observe { }
            val mySpaces = model.getAll()
            logd("my spaces in activity $mySpaces")
            if (mySpaces == null) {
                //the server is off
                displayMessage("The server is down. Please retry.")
            } else {
                spaceViewModel.insertAll(mySpaces)
                logd("done inserting")
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

    private suspend fun areChangesToBeDone(): Boolean {
        val dbChanges = spaceViewModel.allSpacesChanged?.value
        progress.show()
        try {
            if (dbChanges != null) {
                logd("are changes to be done, with data $dbChanges")
                if (dbChanges != null) {
                    for (gr: Space in dbChanges) {
                        logd(gr)
                        if (gr.changed == 1) {
                            //to add
                            gr.changed = 0
                            val resp = model.add(gr)
                            if (resp != null) {
                                if (resp == "off") {
                                    //the server is down
                                    displayMessage("The server is down.")

                                } else {
                                    val id = getId(resp)
                                    logd("id computed $id")
                                    if (id == -1) {
                                        displayMessage(resp)
                                    } else {
                                        gr.changed = 0
                                        gr.id = id
                                        spaceViewModel.update(gr)
                                    }
                                }
                            } else {
                                displayMessage("There is some trouble.")
                                progress.dismiss()
                            }
                        }
                        if (gr.changed == 2) {
                            //to delete
                            gr.changed = 0
                            val res = model.delete(gr.id)

                            if (res == "off") {
                                gr.changed = 2
                                displayMessage("The server is down")
                            } else {
                                spaceViewModel.delete(gr.id)
                            }

                        }
                        if (gr.changed == 3) {
                            //to update
                            gr.changed = 0
                            val res = model.update(gr)
                            if (res != "off") {
                                spaceViewModel.update(gr)
                            } else gr.changed = 3
                        }
                    }
                }
            }
        } catch (e: Exception) {
            return false
        }
        progress.dismiss()
        return true
    }


    private fun getId(message: String): Int {

        val placeDouaPuncte = message.indexOf("=")
        val placeVirgula = message.indexOf(",")
        if (placeDouaPuncte == -1 || placeVirgula == -1) {
            return -1
        }
        var myValue = ""
        for (i in message.indices) {
            if (i in (placeDouaPuncte + 1) until placeVirgula) {
                myValue += message[i]
            }
        }
        return myValue.toInt()

    }

    private fun <T> LiveData<T>.observe(observe: (T?) -> Unit) =
        observe(this@ManagerActivity, { observe(it) })
}