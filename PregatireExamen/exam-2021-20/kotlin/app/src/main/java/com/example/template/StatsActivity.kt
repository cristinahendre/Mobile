package com.example.template
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.*
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

class StatsActivity : AppCompatActivity() {

    private lateinit var itemViewModel: ItemViewModel
    private val model: Model by viewModels()
    private lateinit var adapter: ListAdapter
    private lateinit var view: View
    private lateinit var progress: ProgressDialog


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bought)
        progress = ProgressDialog(this)
        progress.setTitle("Loading")
        progress.setMessage("Please wait...")
        progress.setCancelable(false)
        view = View(this)
        itemViewModel = ViewModelProviders.of(this).get(ItemViewModel::class.java)
        adapter = ListAdapter(this)
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
        private var people = mutableListOf<Item>()

        inner class PeopleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val nameItemView: TextView = itemView.findViewById(R.id.name)
            val idItemView: TextView = itemView.findViewById(R.id.id)
            val statusItemView: TextView = itemView.findViewById(R.id.status)
            val quantityItemView: TextView = itemView.findViewById(R.id.quantity)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PeopleViewHolder {
            val itemView = inflater.inflate(R.layout.normal_item, parent, false)
            return PeopleViewHolder(itemView)
        }

        override fun onBindViewHolder(holder: PeopleViewHolder, position: Int) {
            val current = people[position]
            holder.nameItemView.text = current.name
            holder.idItemView.text = current.id.toString()
            holder.quantityItemView.text = current.quantity.toString()
            holder.statusItemView.text = current.status
        }

        internal fun setItems(myGrades: List<Item>) {
            this.people.clear()
            this.people.addAll(myGrades)
            notifyDataSetChanged()
        }

        override fun getItemCount() = people.size

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
            var myBoughtItems = model.getBoughtItems()
            if (myBoughtItems == null) {
                //the server is off
                displayMessage("The server is down, retry.")
            } else {

                val orderedItems =myBoughtItems.sortedByDescending { it.quantity }
                myBoughtItems = mutableListOf()
                var i =0
                for(element in orderedItems){
                    if(i == 10) break
                    myBoughtItems.add(element)
                    i++
                }
                displayData(myBoughtItems)
            }
            progress.dismiss()

        }
    }


    private fun displayData(gr: List<Item>) {
        adapter.setItems(gr)
    }

    private fun observeModel() {
        itemViewModel.getAll()
        itemViewModel.items?.observe {  }
    }

    private fun <T> LiveData<T>.observe(observe: (T?) -> Unit) =
        observe(this@StatsActivity, { observe(it) })

    private fun displayMessage(myMessage: String) {
        val parentLayout: View = findViewById(android.R.id.content)
        Snackbar.make(parentLayout, myMessage, Snackbar.LENGTH_LONG)
            .setAction("CLOSE") { }
            .setActionTextColor(resources.getColor(android.R.color.holo_red_light))
            .show()
    }
}