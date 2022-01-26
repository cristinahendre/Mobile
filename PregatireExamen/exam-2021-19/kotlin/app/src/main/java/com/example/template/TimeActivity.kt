package com.example.template

import android.app.ProgressDialog
import android.content.Context
import android.os.Bundle
import android.view.*
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.template.domain.Rule
import com.example.template.model.Model
import com.example.template.viewmodel.MyViewModel
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class TimeActivity : AppCompatActivity() {
    private lateinit var personViewModel: MyViewModel
    private val model: Model by viewModels()
    private lateinit var from: EditText
    private lateinit var to: EditText
    private lateinit var button: Button
    private lateinit var adapter: ListAdapter
    private lateinit var view: View
    private lateinit var progress: ProgressDialog


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_time)
        progress = ProgressDialog(this)
        progress.setTitle("Loading")
        progress.setMessage("Please wait...")
        progress.setCancelable(false)
        view = View(this)
        personViewModel = ViewModelProviders.of(this).get(MyViewModel::class.java)
        adapter = ListAdapter(this)
        to = findViewById(R.id.to)
        from = findViewById(R.id.from)
        button = findViewById(R.id.button_save)
        setupRecyclerView(findViewById(R.id.recyclerview))
        button.setOnClickListener { view: View ->
            progress.show()
            fetchTimeData()
            progress.dismiss()
        }


    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.my_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.retry -> {
                logd("retry clicked")
                progress.show()
                fetchTimeData()
                progress.dismiss()
            }
        }
        return super.onOptionsItemSelected(item)
    }


    inner class ListAdapter internal constructor(
        context: Context
    ) : RecyclerView.Adapter<ListAdapter.PeopleViewHolder>() {

        private val inflater: LayoutInflater = LayoutInflater.from(context)
        private var data = mutableListOf<Rule>()

        inner class PeopleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val nameItemView: TextView = itemView.findViewById(R.id.name)
            val levelItemView: TextView = itemView.findViewById(R.id.level)
            val idItemView: TextView = itemView.findViewById(R.id.id)
            val toItemView: TextView = itemView.findViewById(R.id.to)
            val fromItemView: TextView = itemView.findViewById(R.id.from)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PeopleViewHolder {
            val itemView = inflater.inflate(R.layout.empty_item, parent, false)
            return PeopleViewHolder(itemView)
        }

        override fun onBindViewHolder(holder: PeopleViewHolder, position: Int) {
            val current = data[position]
            holder.nameItemView.text = current.name
            holder.levelItemView.text = current.level.toString()
            holder.idItemView.text = current.id.toString()
            holder.fromItemView.text = current.from.toString()
            holder.toItemView.text = current.to.toString()

        }


        internal fun setData(myGrades: List<Rule>) {
            this.data.clear()
            this.data.addAll(myGrades)
            notifyDataSetChanged()
        }

        override fun getItemCount() = data.size

    }


    private fun setupRecyclerView(recyclerView: RecyclerView) {
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)
    }

    private fun fetchTimeData() {

        GlobalScope.launch(Dispatchers.Main) {
            progress.show()

            try {
                val fromText = from.text.toString().toInt()
                val toText = to.text.toString().toInt()

                val myData = model.getAll()
                if (myData == null) {
                    displayMessage("The server is down.")
                } else {
                    val myList = myData.filter { it.from >= fromText && it.to <= toText }
                    displayData(myList)
                }
            } catch (e: NumberFormatException) {
                displayMessage("Choose valid from/to")
            }
            progress.dismiss()
        }

    }


    private fun displayData(gr: List<Rule>) {
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