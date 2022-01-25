package com.example.template

import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.*
import android.widget.Button
import android.widget.TextView
import androidx.activity.viewModels
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.template.domain.Item
import com.example.template.model.Model
import com.example.template.viewmodel.ItemViewModel
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class ShoppingActivity : AppCompatActivity() {
    private lateinit var itemViewModel: ItemViewModel
    private val model: Model by viewModels()
    private lateinit var adapter: ListAdapter
    private lateinit var view: View
    private lateinit var progress: ProgressDialog


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shopping)
        progress = ProgressDialog(this)
        progress.setTitle("Loading")
        progress.setMessage("Please wait...")
        progress.setCancelable(false)
        view = View(this)
        itemViewModel = ViewModelProviders.of(this).get(ItemViewModel::class.java)
        adapter = ListAdapter(this)
        itemViewModel.deleteAll()
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
                    fetchData()
                    observeModel()
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
        private var people = mutableListOf<Item>()

        inner class PeopleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val nameItemView: TextView = itemView.findViewById(R.id.name)
            val idItemView: TextView = itemView.findViewById(R.id.id)
            val statusItemView: TextView = itemView.findViewById(R.id.status)
            val quantityItemView: TextView = itemView.findViewById(R.id.quantity)
            val ivBuy: Button = itemView.findViewById(R.id.ivBuy)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PeopleViewHolder {
            val itemView = inflater.inflate(R.layout.buy_item, parent, false)
            return PeopleViewHolder(itemView)
        }

        override fun onBindViewHolder(holder: PeopleViewHolder, position: Int) {
            val current = people[position]
            holder.nameItemView.text = current.name
            holder.idItemView.text = current.id.toString()
            holder.quantityItemView.text = current.quantity.toString()
            holder.statusItemView.text = current.status

            holder.ivBuy.setOnClickListener {
                logd("to buy $current")
                buy(current)

            }
        }

        internal fun setItems(myGrades: List<Item>) {
            this.people.clear()
            this.people.addAll(myGrades)
            notifyDataSetChanged()
        }

        override fun getItemCount() = people.size

    }

    private fun buy(space: Item) {
        GlobalScope.launch(Dispatchers.Main) {
            progress.show()
            val result = model.update(space)
            logd("server response = $result")
            if (result == "off") {
                //the server is off
                displayMessage("The server is down. You cannot buy offline.")
            }  else {
                val obj= deserialize(result)
                logd("deserializat $obj")
                if(obj!=null)  {
                    space.status = obj.status
                    itemViewModel.update(space)
                }
                else{
                    displayMessage("An error occured.")
                }
            }
            progress.dismiss()
        }
    }


    private fun deserialize(myString: String): Item? {
        //Item(id=0, name=a, quantity=2, status=ned, price=82, changed=0)
        try {
            var indexStart = myString.indexOf("id") + 3
            var indexStop = myString.indexOf("name") - 3
            val id = getStringFromArea(indexStart, indexStop, myString).toInt()
            if (id == 0) {
                return null
            }
            indexStart = myString.indexOf("status") + 7
            indexStop = myString.indexOf("price") - 3
            val status = getStringFromArea(indexStart, indexStop, myString)

            indexStart = myString.indexOf("price") + 6
            indexStop = myString.indexOf("changed") - 3
            val price = getStringFromArea(indexStart, indexStop, myString).toInt()
            if (price == 0) {
                return null
            }
            return Item(id, "", 0, status, price, 0)
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

    private fun setupRecyclerView(recyclerView: RecyclerView) {
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)
    }

    private fun fetchData() {
        GlobalScope.launch(Dispatchers.Main) {

            progress.show()
            itemViewModel.getItemsChanged()
            itemViewModel.itemsChanged?.observe { }
            val myGrades = model.getAll()
            if (myGrades == null) {
                //the server is off
                displayMessage("The server is down, retry.")
            } else {
                itemViewModel.insertAll(myGrades)
                logd("done inserting")
            }
            progress.dismiss()

        }
    }


    private fun displayData(gr: List<Item>) {
        adapter.setItems(gr)
    }

    private fun observeModel() {
        itemViewModel.getItemsAvailable()
        itemViewModel.itemsAvailable?.observe { displayData(it ?: emptyList()) }
    }

    private fun <T> LiveData<T>.observe(observe: (T?) -> Unit) =
        observe(this@ShoppingActivity, { observe(it) })

    private fun displayMessage(myMessage: String) {
        val parentLayout: View = findViewById(android.R.id.content)
        Snackbar.make(parentLayout, myMessage, Snackbar.LENGTH_LONG)
            .setAction("CLOSE") { }
            .setActionTextColor(resources.getColor(android.R.color.holo_red_light))
            .show()
    }

}